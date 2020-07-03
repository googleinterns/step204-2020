package com.google.job.data;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.firestore.FireStoreUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import java.util.Optional;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicBoolean;

/** Helps persist and retrieve job posts. */
public final class JobsDatabase {
    private static final String JOB_COLLECTION = "Jobs";

    /**
     * Adds a newly created job post.
     *
     * @param newJob Newly created job post. Assumes that it is non-nullable.
     * @return Future for the added job post task, convenient for testing.
     */
    public Future<DocumentReference> addJob(Job newJob) {
        return FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION)
                .add(newJob);
    }

    /**
     * Edits the job post.
     *
     * @param jobId Id for the target job post in the database.
     * @param job Updated job post.
     * @return A future of the detailed information of the update.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<WriteResult> setJob(String jobId, Job job) throws IllegalArgumentException {
        // Overwrites the whole job post
        DocumentReference docRef = FireStoreUtils.getFireStore().collection(JOB_COLLECTION).document(jobId);
        if (!isDocumentExist(docRef)) {
            throw new IllegalArgumentException("Job Id does not exist");
        }
        return docRef.set(job);
    }

    /**
     * Fetches the snapshot future of a specific job post.
     *
     * @param jobId Id for the job post in the database.
     * @return Future of the target job post.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<Optional<Job>> fetchJob(String jobId) throws IllegalArgumentException {
        DocumentReference docRef = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId);

        if (!isDocumentExist(docRef)) {
            throw new IllegalArgumentException("Job Id does not exist");
        }

        // Asynchronously retrieves the document
        ApiFuture<DocumentSnapshot> snapshotFuture = docRef.get();

        ApiFunction<DocumentSnapshot, Optional<Job>> jobFunction = new ApiFunction<DocumentSnapshot, Optional<Job>>() {
            @NullableDecl
            public Optional<Job> apply(@NullableDecl DocumentSnapshot documentSnapshot) {
                return FireStoreUtils.convertDocumentSnapshotToPOJO(documentSnapshot, Job.class);
            }
        };

        return ApiFutures.transform(snapshotFuture, jobFunction, MoreExecutors.directExecutor());
    }

    private boolean isDocumentExist(DocumentReference docIdRef) {
        AtomicBoolean isExist = new AtomicBoolean(/* initialValue= */false);
        docIdRef.addSnapshotListener((documentSnapshot, e) -> isExist.set(documentSnapshot.exists()));

        return isExist.get();
    }
}
