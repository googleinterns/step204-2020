package com.google.utils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import javax.ws.rs.CookieParam;
import javax.ws.rs.core.Cookie;
import java.util.Optional;

/** Util methods related to Firebase Auth.  */
public final class FirebaseAuthUtils {
    private static final String SESSION_COOKIE_NAME = "session";

    private FirebaseAuthUtils() {}

    /**
     * Gets the uid from firebase auth session cookie.
     *
     * @param cookie Firebase Auth session cookie.
     * @return Optional of uid of the current account.
     * @throws IllegalArgumentException If uid is null or empty.
     * @throws FirebaseAuthException If session cookie is unavailable, invalid or revoked.
     */
    public Optional<String> getUid(@CookieParam(SESSION_COOKIE_NAME) Cookie cookie)
            throws IllegalArgumentException, FirebaseAuthException {
        String sessionCookie = cookie.getValue();

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
}
