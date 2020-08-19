package com.google.account.business.data;

import com.google.cloud.firestore.WriteResult;
import com.google.utils.FireStoreUtils;

import java.io.IOException;
import java.util.concurrent.Future;

/** Helps persist and retrieve business accounts in cloud firestore. */
public final class BusinessDatabase {
    private static final String BUSINESS_ACCOUNT_COLLECTION = "BusinessAccounts";

    /**
     * Replace with a new business object and stores into the cloud firestore.
     *
     * @param uid Uid of the current user account.
     * @param business Business object for the current account.
     * @return Future with writing details.
     */
    public Future<WriteResult> updateBusinessAccount(String uid, Business business) throws IOException {
        // Adds the business object into cloud firestore using uid as document id
        return FireStoreUtils.getFireStore()
                .collection(BUSINESS_ACCOUNT_COLLECTION)
                .document(uid)
                .set(business);
    }
}
