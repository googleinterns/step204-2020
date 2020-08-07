package com.google.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.Optional;

/** Util methods related to Firebase Auth.  */
public final class FirebaseAuthUtils {
    private static final String SESSION_COOKIE_NAME = "session";

    private FirebaseAuthUtils() {}

    /**
     * Initializes the admin SDK.
     *
     * @param databaseUrl Url to database.
     * @param projectId Project Id.
     * @param projectName Project Name.
     */
    public static void initAdminSDK(String databaseUrl, String projectId, String projectName) throws IOException {
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(GoogleCredentials.getApplicationDefault())
                .setDatabaseUrl(databaseUrl)
                .setProjectId(projectId)
                .build();

        FirebaseApp.initializeApp(options, /* name= */ projectName);
    }


    /**
     * Gets the uid from firebase auth session cookie.
     *
     * @param request Http Servlet Request.
     * @return Optional of uid of the current account.
     * @throws IllegalArgumentException If uid is null or empty.
     * @throws FirebaseAuthException If session cookie is unavailable, invalid or revoked.
     */
    public Optional<String> getUid(HttpServletRequest request)
            throws IllegalArgumentException, FirebaseAuthException {
        String sessionCookie = getCookie(request, SESSION_COOKIE_NAME).getValue();

        // Verifies the session cookie. In this case an additional check is added to detect
        // if the user's Firebase session was revoked, user deleted/disabled, etc.
        FirebaseToken decodedToken = FirebaseAuth.getInstance()
                .verifySessionCookie(sessionCookie, /* checkRevoked= */ true);

        String uid = decodedToken.getUid();

        if (uid == null || uid.isEmpty()) {
            throw new IllegalArgumentException("uid should be an non-empty string");
        }

        return Optional.of(uid);
    }

    /**
     * Gets the cookie given the cookie name.
     *
     * @param request Http Servlet Request.
     * @param name Cookie name.
     * @return The whole cookie.
     * @throws IllegalArgumentException If no cookie matched the given name.
     */
    public static Cookie getCookie(HttpServletRequest request, String name) throws IllegalArgumentException {
        Cookie[] cookies = request.getCookies();
        for (Cookie cookie: cookies) {
            if (cookie.getName().equals(name)) {
                return cookie;
            }
        }

        throw new IllegalArgumentException("No matched cookie");
    }
}
