package com.google.job.data;

import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import java.util.concurrent.Future;

/** Helps persist and retrieve Future of job posts. */
public final class JobsDatabase {
    private static final String JOB_COLLECTION = "Jobs";
    private final Firestore firestore;

    public JobsDatabase(Firestore firestore) {
        this.firestore = firestore;
    }

    /**
     * Adds a newly created job post.
     *
     * @param newJob Newly created job post. Assumes that it is non-nullable.
     * @return Future for the added job post.
     */
    public Future<DocumentReference> addJob(Job newJob) {
        Future<DocumentReference> addedDocRef = firestore.collection(JOB_COLLECTION).add(newJob);
        return addedDocRef;
    }

    /**
     * Edits the job post.
     *
     * @param jobId Id for the target job post in the database.
     * @param job Updated job post.
     */
    public void editJob(String jobId, Job job) {
        // Overwrites the whole job post
        firestore.collection(JOB_COLLECTION).document(jobId).set(job);
    }

    /**
     * Fetches the snapshot future of a specific job post.
     *
     * @param jobId Id for the job post in the database.
     * @return Target job post.
     */
    public Future<DocumentSnapshot> fetchJob(String jobId) {
        DocumentReference docRef = firestore.collection(JOB_COLLECTION).document(jobId);
        // Asynchronously retrieves the document
        Future<DocumentSnapshot> future = docRef.get();
        return future;
    }
}
