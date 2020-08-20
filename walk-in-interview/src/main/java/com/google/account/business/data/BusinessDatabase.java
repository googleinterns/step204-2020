package com.google.account.business.data;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.FieldValue;
import com.google.appengine.repackaged.com.google.common.util.concurrent.MoreExecutors;
import com.google.cloud.firestore.WriteResult;
import com.google.utils.FireStoreUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.Future;

/** Helps persist and retrieve business accounts in cloud firestore. */
public final class BusinessDatabase {
    private static final String BUSINESS_ACCOUNT_COLLECTION = "BusinessAccounts";
    private static final String JOBS_FIELD = "jobs";

    /**
     * Gets the snapshot future of a specific account.
     *
     * @param uid Uid of the account.
     * Returns uture of the target account.
     */
    public Future<Optional<Business>> getBusinessAccount(String uid) throws IOException {
        DocumentReference docRef = FireStoreUtils.getFireStore()
                .collection(BUSINESS_ACCOUNT_COLLECTION).document(uid);

        // Asynchronously retrieves the document
        ApiFuture<DocumentSnapshot> snapshotFuture = docRef.get();

        ApiFunction<DocumentSnapshot, Optional<Business>> businessFunction =
                new ApiFunction<DocumentSnapshot, Optional<Business>>() {
            @NullableDecl
            public Optional<Business> apply(@NullableDecl DocumentSnapshot documentSnapshot) {
                return FireStoreUtils.convertDocumentSnapshotToPOJO(documentSnapshot, Business.class);
            }
        };

        return ApiFutures.transform(snapshotFuture, businessFunction, MoreExecutors.directExecutor());
    }

    /**
     * Replace with a new business object and stores into the cloud firestore.
     *
     * @param uid Uid of the current user account.
     * @param business Business object for the current account.
     * Returns future with writing details.
     */
    public Future<WriteResult> updateBusinessAccount(String uid, Business business) throws IOException {
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
     * Returns a future.
     * @throws IllegalArgumentException If uid does not exist
     */
    public Future<Void> updateJobsMade(String uid, String jobId) throws IOException, IllegalArgumentException {
        // Runs an asynchronous transaction
        ApiFuture<Void> futureTransaction = FireStoreUtils.getFireStore().runTransaction(transaction -> {
            final DocumentReference documentReference = FireStoreUtils.getFireStore()
                    .collection(BUSINESS_ACCOUNT_COLLECTION).document(uid);

            // Verifies if the current user can update the job post with this job id
            DocumentSnapshot documentSnapshot = transaction.get(documentReference).get();

            // Account does not exist
            if (!documentSnapshot.exists()) {
                throw new IllegalArgumentException("Invalid uid");
            }

            // Adds the newly created job Id into the array.
            transaction.update(documentReference, JOBS_FIELD, FieldValue.arrayUnion(jobId));

            return null;
        });

        return futureTransaction;
    }
}
