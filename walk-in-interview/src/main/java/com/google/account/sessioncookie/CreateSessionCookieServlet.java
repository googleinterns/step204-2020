package com.google.account.sessioncookie;

import com.google.account.UserType;
import com.google.account.business.data.Business;
import com.google.account.business.data.BusinessDatabase;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.common.collect.ImmutableList;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseToken;
import com.google.firebase.auth.SessionCookieOptions;
import com.google.firebase.auth.UserRecord;
import com.google.utils.FireStoreUtils;
import com.google.utils.FirebaseAuthUtils;
import com.google.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

/** Servlet that handles creating firebase auth session cookie. */
@WebServlet("/sign-in")
public final class CreateSessionCookieServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CreateSessionCookieServlet.class.getName());

    private static final String BUSINESS_ACCOUNT_COLLECTION = "BusinessAccounts";

    private static final int SESSION_COOKIE_DURATION_DAYS = 5;
    private static final String SESSION_COOKIE_NAME = "session";
    private static final String ID_TOKEN_PARAM = "idToken";

    // TODO(issue/87): move to config file
    private static final String DATABASE_URL = "https://com-walk-in-interview.firebaseio.com/";
    private static final String PROJECT_ID = "com-walk-in-interview";
    private static final String PROJECT_NAME = "Walk-In-Interview";

    private BusinessDatabase businessDatabase;


    @Override
    public void init() {
        this.businessDatabase = new BusinessDatabase();

        try {
            FirebaseAuthUtils.initAdminSDK(DATABASE_URL, PROJECT_ID, PROJECT_NAME);
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

            // Verifies the session cookie. In this case an additional check is added to detect
            // if the user's Firebase session was revoked, user deleted/disabled, etc.
            FirebaseToken decodedToken = FirebaseAuth.getInstance()
                    .verifySessionCookie(sessionCookie, /* checkRevoked= */ true);

            String uid = decodedToken.getUid();

            // Creates the preliminary Account object if the account does not exist
            String userType = FirebaseAuthUtils.getUserType(request);

            if (userType.equals(UserType.BUSINESS.getUserTypeId())) {
                if (!isBusinessAccountExist(uid)) {
                    UserRecord userRecord = FirebaseAuth.getInstance().getUser(uid);
                    String email = userRecord.getEmail();

                    Business preliminaryBusiness = Business.newBuilder()
                            .setName(email)
                            .setJobs(ImmutableList.of()).build();

                    this.businessDatabase.createBusinessAccount(uid, preliminaryBusiness);
                }
            }

            // Sets cookie policy parameters as required
            Cookie cookie = new Cookie(SESSION_COOKIE_NAME, sessionCookie);
            response.addCookie(cookie);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            String errorMessage = "Failed to create a session cookie";
            LOGGER.log(Level.SEVERE, errorMessage, e);
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
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

    private boolean isBusinessAccountExist(String uid) throws IOException, ExecutionException, InterruptedException {
        DocumentSnapshot account = FireStoreUtils.getFireStore()
                .collection(BUSINESS_ACCOUNT_COLLECTION).document(uid).get().get();

        return account.exists();
    }
}
