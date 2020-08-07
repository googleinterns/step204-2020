package com.google.account;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.utils.ServletUtils;

import javax.servlet.http.HttpServlet;
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

/** Servlet that handles creating and clearing firebase auth session cookie. */
public final class FirebaseAuthSessionCookieServlet extends HttpServlet {
    private static final int SESSION_COOKIE_DURATION_DAYS = 5;
    private static final String SESSION_COOKIE_NAME = "session";
    private static final String ID_TOKEN_PARAM = "idToken";
    private static final String LOG_IN_PAGE_PATH = "/log-in/index.html";

    private static final String DATABASE_URL = "https://com-walk-in-interview.firebaseio.com/";
    private static final String PROJECT_ID = "com-walk-in-interview";

    public FirebaseAuthSessionCookieServlet() throws IOException {
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

            // Sets session expiration
            long expiresIn = TimeUnit.DAYS.toMillis(/* duration= */SESSION_COOKIE_DURATION_DAYS);
            SessionCookieOptions options = SessionCookieOptions.builder().setExpiresIn(expiresIn).build();

            // Creates the session cookie. This will also verify the ID token in the process.
            // The session cookie will have the same claims as the ID token.
            String sessionCookie = FirebaseAuth.getInstance().createSessionCookieAsync(idToken, options).get();

            // Sets cookie policy parameters as required
            NewCookie cookie = new NewCookie(/* name= */ SESSION_COOKIE_NAME, sessionCookie);
            return Response.ok().cookie(cookie).build();
        } catch (Exception e) {
            String errorMessage = "Failed to create a session cookie";
            request.getServletContext().log(errorMessage);
            return Response.status(Response.Status.UNAUTHORIZED).entity(errorMessage).build();
        }
    }

    /**
     * Clears the session cookie at the server side when the user signs out.
     *
     * @param cookie Session cookie
     * @return Response to direct to log in page.
     */
    @POST
    @Path("/business-log-out")
    public Response clearSessionCookie(@CookieParam(SESSION_COOKIE_NAME) Cookie cookie) {
        String sessionCookie = cookie.getValue();

        try {
            // Clears session cookie at the server side
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie);
            FirebaseAuth.getInstance().revokeRefreshTokens(decodedToken.getUid());

            // Returns an expired cookie to informs the client that the session cookie expires
            // The maximum age of the cookie in seconds when the cookie will expire
            final int maxAge = 0;
            NewCookie newCookie = new NewCookie(cookie, /* comment= */ null, maxAge, /* secure= */ true);
            return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).cookie(newCookie).build();
        } catch (FirebaseAuthException e) {
            System.err.println("Fails to verify or revoke tokens");
            return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).build();
        }
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
}
