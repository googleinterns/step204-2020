package com.google.account;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.utils.ServletUtils;

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
    private static final int SESSION_COOKIE_DURATION_DAYS = 5;
    private static final String SESSION_COOKIE_NAME = "session";
    private static final String ID_TOKEN_PARAM = "idToken";
    private static final String LOG_IN_PAGE_PATH = "/log-in/index.html";

    private static final String DATABASE_URL = "https://com-walk-in-interview.firebaseio.com/";
    private static final String PROJECT_ID = "com-walk-in-interview";

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
            String idToken = parseIDToken(request);

            // Sets session expiration to 5 days
            long expiresIn = TimeUnit.DAYS.toMillis(/* duration= */SESSION_COOKIE_DURATION_DAYS);
            SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

            // Creates the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            String sessionCookie = FirebaseAuth.getInstance().createSessionCookieAsync(idToken, options).get();

            // Sets cookie policy parameters as required
            NewCookie cookie = new NewCookie(/* name= */ SESSION_COOKIE_NAME, sessionCookie);
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
    public Response verifySessionCookie(@CookieParam(SESSION_COOKIE_NAME) Cookie cookie) {
        String sessionCookie = cookie.getValue();
        try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            FirebaseToken decodedToken = FirebaseAuth.getInstance()
                    .verifySessionCookie(sessionCookie, /* checkRevoked= */ true);
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
    public Response clearSessionCookie(@CookieParam(SESSION_COOKIE_NAME) Cookie cookie) {
        // The maximum age of the cookie in seconds when the cookie will expire
        final int maxAge = 0;
        NewCookie newCookie = new NewCookie(cookie, /* comment= */ null, maxAge, /* secure= */ true);
        return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).cookie(newCookie).build();
    }

    /** Initializes the admin SDK. */
    private void initAdminSDK() throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl(DATABASE_URL)
                .setProjectId(PROJECT_ID)
                .build();

        FirebaseApp.initializeApp(options);
    }

    /** Gets the id token from the log in request. */
    private String parseIDToken(HttpServletRequest request) throws IllegalArgumentException {
        String idToken = ServletUtils.getStringParameter(request, ID_TOKEN_PARAM, /* defaultValue= */ "");

        if (idToken.isEmpty()) {
            throw new IllegalArgumentException("ID Token should be an non-empty string");
        }

        return idToken;
    }

    /** Gets the uid the of the current account. */
    private Response getUid(FirebaseToken decodedToken) {
        String uid = decodedToken.getUid();

        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("uid should be an non-empty string");
        }

        return Response.ok(uid).build();
    }
}
