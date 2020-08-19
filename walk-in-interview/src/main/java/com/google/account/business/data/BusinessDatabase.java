package com.google.account.business.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.cloud.firestore.WriteResult;
import com.google.utils.FireStoreUtils;

import java.io.IOException;
import java.util.concurrent.Future;

/** Helps persist and retrieve business accounts in cloud firestore. */
public final class BusinessDatabase {
    private static final String BUSINESS_ACCOUNT_COLLECTION = "BusinessAccounts";
    private static final String JOBS_FIElD = "jobs";

    // Creates a new business object and stores into the cloud firestore.
    public Future<WriteResult> createBusinessAccount(String uid, Business business) throws IOException {
        // Adds the business object into cloud firestore using uid as document id
        return FireStoreUtils.getFireStore()
                .collection(BUSINESS_ACCOUNT_COLLECTION)
                .document(uid)
                .set(business);
    }

    /**
     * Updates the job posts made once a new job post is created.
     * If the jobId alr exist in the array, it will not be added.
     *
     * @param uid Uid of the current user
     * @param jobId Id of the newly created job post.
     * @return A future.
     */
    public Future<Void> updateJobsMade(String uid, String jobId) throws IOException {
        // Runs an asynchronous transaction
        ApiFuture<Void> futureTransaction = FireStoreUtils.getFireStore().runTransaction(transaction -> {
            final DocumentReference documentReference = FireStoreUtils.getFireStore()
                    .collection(BUSINESS_ACCOUNT_COLLECTION).document(uid);

            // Verifies if the current user can update the job post with this job id
            DocumentSnapshot documentSnapshot = transaction.get(documentReference).get();

            // Job does not exist
            if (!documentSnapshot.exists()) {
                throw new IllegalArgumentException("Invalid uid");
            }

            // Adds the newly created job Id into the array.
            transaction.update(documentReference, JOBS_FIElD, FieldValue.arrayUnion(jobId));

            return null;
        });

        return futureTransaction;
    }
}