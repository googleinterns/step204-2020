package com.google.job.data;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.utils.FireStoreUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;

import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Helps persist and retrieve job posts. */
public final class JobsDatabase {
    private static final String JOB_COLLECTION = "Jobs";
    private static final String JOB_STATUS_FIELD = "jobStatus";
    private static final long TIMEOUT = 5;

    /**
     * Adds a newly created job post.
     *
     * @param newJob Newly created job post. Assumes that it is non-nullable.
     * @return A future of the detailed information of the writing.
     */
    public Future<WriteResult> addJob(Job newJob) throws IOException {
        // Add document data after generating an id.
        DocumentReference addedDocRef = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document();

        String jobId = addedDocRef.getId();

        // Updates the Job with cloud firestore id
        // Status is already set when parsing the job post
        Job job = newJob.toBuilder()
                .setJobId(jobId)
                .build();

        return addedDocRef.set(job);
    }

    /**
     * Edits the job post.
     *
     * @param jobId Id for the target job post in the database.
     * @param updatedJob Updated job post.
     * @return A future of transaction.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<Void> setJob(String jobId, Job updatedJob) throws IllegalArgumentException, IOException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }
        
        // Sets the Job with cloud firestore id and ACTIVE status
        Job job = updatedJob.toBuilder()
                .setJobId(jobId)
                .build();

        // Runs an asynchronous transaction
        ApiFuture<Void> futureTransaction = FireStoreUtils.getFireStore().runTransaction(transaction -> {
            final DocumentReference documentReference = FireStoreUtils.getFireStore()
                    .collection(JOB_COLLECTION).document(jobId);

            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            verifyUserCanUpdateJob(jobId);

            // Overwrites the whole job post
            transaction.set(documentReference, job);
            
            return null;
        });
        
        return futureTransaction;
    }

    /**
     * Marks a job post as DELETED.
     *
     * @param jobId Cloud Firestore Id of the job post.
     * @return A future of transaction.
     */
    public Future<Void> markJobPostAsDeleted(String jobId) throws IOException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }
        
        // Runs an asynchronous transaction
        ApiFuture<Void> futureTransaction = FireStoreUtils.getFireStore().runTransaction(transaction -> {
            final DocumentReference documentReference = FireStoreUtils.getFireStore()
                    .collection(JOB_COLLECTION).document(jobId);

            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            verifyUserCanUpdateJob(jobId);

            // Updates the jobStatus field to DELETED.
            transaction.update(documentReference, JOB_STATUS_FIELD, JobStatus.DELETED);

            return null;
        });

        return futureTransaction;
    }

    /**
     * Fetches the snapshot future of a specific job post.
     *
     * @param jobId Id for the job post in the database.
     * @return Future of the target job post.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<Optional<Job>> fetchJob(String jobId) throws IllegalArgumentException, IOException {
        DocumentReference docRef = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId);

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

    /**
     * Returns a future of boolean to check if the job matching the given id is valid.
     *
     * @throws IllegalArgumentException If the input jobId is empty.
     */
    public static Future<Boolean> hasJob(String jobId) throws IllegalArgumentException, IOException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }

        ApiFuture<DocumentSnapshot> snapshotFuture = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId).get();

        return ApiFutures.transform(snapshotFuture,
                documentSnapshot -> documentSnapshot.exists(),
                MoreExecutors.directExecutor());
    }

    /**
     * Verifies if it is a valid job id that this user can update.
     *
     * @throws IllegalArgumentException If there is no such id in database.
     */
    // TODO(issue/25): incorporate the account stuff into job post.
    private void verifyUserCanUpdateJob(String jobId) throws
            IllegalArgumentException, ServletException, ExecutionException, TimeoutException {
        try {
            // Use timeout in case it blocks forever.
            boolean hasJob = JobsDatabase.hasJob(jobId).get(TIMEOUT, TimeUnit.SECONDS);
            if (!hasJob) {
                throw new IllegalArgumentException("Invalid Job Id");
            }
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }
}
