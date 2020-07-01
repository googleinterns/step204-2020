package com.google.job.data;

import com.google.cloud.firestore.Firestore;
import com.google.utils.FireStoreUtils;

import java.util.concurrent.ExecutionException;

/** Helps persist and retrieve job posts. */
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
     * @return Id for the job in the database.
     * @throws ExecutionException If error occurs when getting job id.
     * @throws InterruptedException If error occurs when getting job id.
     */
    public String addJob(Job newJob) throws ExecutionException, InterruptedException {
        String jobId = FireStoreUtils.store(JOB_COLLECTION, newJob);
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
        firestore.collection(JOB_COLLECTION).document(jobId).set(job);
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
        Job job = FireStoreUtils.load(JOB_COLLECTION, jobId, Job.class);
        return job;
    }
}
