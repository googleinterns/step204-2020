package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.utils.FireStoreUtils;

import java.util.concurrent.ExecutionException;

/** Interaction methods between job posts and database. */
public final class JobsDatabase {

    private static final String COLLECTION = "Jobs";
    private final Firestore firestore;

    public JobsDatabase(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Adds a newly created job post.
     *
     * @param newJob Newly created job post. Assumes that it is non-nullable.
     * @return Id for the job in the database.
     * @throws ExecutionException If error occurs when getting job id.
     * @throws InterruptedException If error occurs when getting job id.
     */
    public String addJob(Job newJob) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = FireStoreUtils.store(COLLECTION, newJob);

        String jobId = addedDocRef.get().getId();
        return jobId;
    }

    /**
     * Edits the job post.
     *
     * @param jobId Id for the target job post in the database.
     * @param job Updated job post.
     */
    public void editJob(String jobId, Job job) {
        // Overwrites the whole job post
        firestore.collection(COLLECTION).document(jobId).set(job);
    }

    /**
     * Fetches a specific job post.
     *
     * @param jobId Id for the job post in the database.
     * @return Target job post.
     * @throws ExecutionException If error occurs when getting job post.
     * @throws InterruptedException If error occurs when getting job post.
     */
    public Job fetchJob(String jobId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION).document(jobId);
        DocumentSnapshot document = FireStoreUtils.load(docRef);

        Job job = null;
        if (document.exists()) {
            // convert document to POJO
            job = document.toObject(Job.class);
        } else {
            // TODO: error handling
        }

        return job;
    }
}