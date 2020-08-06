package com.google.account;

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
import java.net.URI;
import java.util.concurrent.TimeUnit;

public final class FirebaseAuthSessionCookieUtil {
    private static final String LOG_IN_PAGE_PATH = "/log-in/index.html";

    private FirebaseAuthSessionCookieUtil() {};

    @POST
    @Path("/business-log-in")
    @Consumes("application/json")
    public static Response createSessionCookie(HttpServletRequest request) {
        try {
            // Gets the ID token sent by the client
            String idToken = FirebaseAuthIDTokenUtil.getUID(request);

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

    @POST
    @Path("/profile")
    public static Response verifySessionCookie(@CookieParam("session") Cookie cookie) {
        String sessionCookie = cookie.getValue();
        try {
            // Verify the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            final boolean checkRevoked = true;
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookie, checkRevoked);
            return serveContentForUser(decodedToken);
        } catch (FirebaseAuthException e) {
            // Session cookie is unavailable, invalid or revoked. Force user to login.
            return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).build();
        }
    }

    @POST
    @Path("/business-log-out")
    public static Response clearSessionCookie(@CookieParam("session") Cookie cookie) {
        final int maxAge = 0;
        NewCookie newCookie = new NewCookie(cookie, /* comment= */ null, maxAge, /* secure= */ true);
        return Response.temporaryRedirect(URI.create(LOG_IN_PAGE_PATH)).cookie(newCookie).build();
    }

    private static Response serveContentForUser(FirebaseToken decodedToken) {
        return null;
    }
}
