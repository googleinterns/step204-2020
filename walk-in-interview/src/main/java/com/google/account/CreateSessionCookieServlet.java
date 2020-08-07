package com.google.account;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.utils.FirebaseAuthUtils;
import com.google.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Servlet that handles creating firebase auth session cookie. */
@WebServlet("/business-log-in")
public final class CreateSessionCookieServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CreateSessionCookieServlet.class.getName());

    private static final int SESSION_COOKIE_DURATION_DAYS = 5;
    private static final String SESSION_COOKIE_NAME = "session";
    private static final String ID_TOKEN_PARAM = "idToken";
    private static final String LOG_IN_PAGE_PATH = "/log-in/index.html";

    @Override
    public void init() {
        try {
            FirebaseAuthUtils.initAdminSDK();
        } catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Error occur when initializing the Admin SDK: ", e);
        }
    }

    /**
     * Creates the session cookies. It is evoked when the user signs in.
     */
    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
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
            Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionCookie);
            response.addCookie(cookie);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            String errorMessage = "Failed to create a session cookie";
            LOGGER.log(Level.SEVERE, errorMessage, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.sendRedirect(LOG_IN_PAGE_PATH);
        }
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
