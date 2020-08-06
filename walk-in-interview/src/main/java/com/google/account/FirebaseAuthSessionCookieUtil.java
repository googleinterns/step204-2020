package com.google.account;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.CookieParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.core.Cookie;
import javax.ws.rs.core.NewCookie;
import javax.ws.rs.core.Response;
import java.io.IOException;
import java.net.URI;
import java.util.concurrent.TimeUnit;

/** Util methods related to firebase auth session cookies. */
public final class FirebaseAuthSessionCookieUtil {
    private static final String ID_TOKEN_PARAM = "idToken";
    private static final String LOG_IN_PAGE_PATH = "/log-in/index.html";

    public FirebaseAuthSessionCookieUtil() throws IOException {
        initAdminSDK();
    }

    /**
     * Creates the session cookies. It is evoked when the user signs in.
     *
     * @param request Log in request.
     * @return Response with session cookie.
     */
    @POST
    @Path("/business-log-in")
    @Consumes("application/json")
    public Response createSessionCookie(HttpServletRequest request) {
        try {
            // Gets the ID token sent by the client
            String idToken = getIDToken(request);

            // Sets session expiration to 5 days
            long expiresIn = TimeUnit.DAYS.toMillis(/* duration= */5);
            SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

            // Creates the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            String sessionCookie = FirebaseAuth.getInstance().createSessionCookieAsync(idToken, options).get();

            // Sets cookie policy parameters as required
            NewCookie cookie = new NewCookie(/* name= */ "session", sessionCookie);
            return Response.ok().cookie(cookie).build();
        } catch (Exception e) {
            return Response.status(Response.Status.UNAUTHORIZED).entity("Failed to create a session cookie").build();
        }
    }

    /**
     * Verifies session cookies.
     *
     * @param cookie Session cookie.
     * @return Response with uid of the current user account.
     */
    @POST
    @Path("/profile")
    public Response verifySessionCookie(@CookieParam("session") Cookie cookie) {
        String sessionCookie = cookie.getValue();
        try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            final boolean checkRevoked = true;
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie, checkRevoked);
            return getUid(decodedToken);
        } catch (FirebaseAuthException e) {
            // Session cookie is unavailable, invalid or revoked. Force user to login.
            return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).build();
        }
    }

    /**
     * Clears the session cookie when the user signs out at the client side.
     *
     * @param cookie Session cookie
     * @return Response to direct to log in page.
     */
    @POST
    @Path("/business-log-out")
    public static Response clearSessionCookie(@CookieParam("session") Cookie cookie) {
        final int maxAge = 0;
        NewCookie newCookie = new NewCookie(cookie, /* comment= */ null, maxAge, /* secure= */ true);
        return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).cookie(newCookie).build();
    }

    /** Initializes the admin SDK. */
    private void initAdminSDK() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl("https://com-walk-in-interview.firebaseio.com/")
                .setProjectId("com-walk-in-interview")
                .build();

        FirebaseApp.initializeApp(options);
    }

    /** Gets the id token from the log in request. */
    private String getIDToken(HttpServletRequest request) throws IllegalArgumentException {
        String idToken = request.getParameter(ID_TOKEN_PARAM).trim();

        if (idToken == null || idToken.isEmpty()) {
            throw new IllegalArgumentException("ID Token should be an non-empty string");
        }

        return idToken;
    }

    /** Gets the uid the of the current account. */
    private Response getUid(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();
        return Response.ok(uid).build();
    }
}
