package com.google.account.sessioncookie;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;
import com.google.utils.FirebaseAuthUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Servlet that handles clearing firebase auth session cookie at server side. */
@WebServlet("/sign-out")
public final class ClearSessionCookieServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(ClearSessionCookieServlet.class.getName());

    private static final String SESSION_COOKIE_NAME = "session";

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Cookie sessionCookie = FirebaseAuthUtils.getCookie(request, SESSION_COOKIE_NAME);
        String sessionCookieValue = sessionCookie.getValue();

        try {
            // Clears session cookie at the server side
            FirebaseToken decodedToken = FirebaseAuth.getInstance().verifySessionCookie(sessionCookieValue);
            FirebaseAuth.getInstance().revokeRefreshTokens(decodedToken.getUid());

            // Returns an expired cookie to informs the client that the session cookie expires
            // The maximum age of the cookie in seconds when the cookie will expire
            final int maxAge = 0;
            sessionCookie.setMaxAge(maxAge);
            sessionCookie.setSecure(true);

            response.addCookie(sessionCookie);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (FirebaseAuthException e) {
            String errorMessage = "Fails to verify or revoke tokens";
            LOGGER.log(Level.SEVERE, errorMessage, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        }
    }
}
