package com.google.account;

import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseToken;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;

public final class FirebaseAuthIDTokenUtil {
    private static final String ID_TOKEN_PARAM = "idToken";

    private FirebaseAuthIDTokenUtil() {};

    public static String getUID(HttpServletRequest request) throws FirebaseAuthException, IllegalArgumentException {
        FirebaseApp.initializeApp();

        String idToken = getIDToken(request);

        FirebaseToken decodedToken = FirebaseAuth.getInstance().verifyIdToken(idToken);
        String uid = decodedToken.getUid();
        return uid;
    }

    private static String getIDToken(HttpServletRequest request) throws IllegalArgumentException {
        String idToken = request.getParameter(ID_TOKEN_PARAM).trim();

        if (idToken == null || idToken.isEmpty()) {
            throw new IllegalArgumentException("ID Token should be an non-empty string");
        }

        return idToken;
    }
}
