package com.google.job.data;

import com.google.cloud.firestore.WriteResult;

import java.util.concurrent.Future;

/**
 * A Result Type class, specific for "addJob" function, that stores both job id and future.
 * Returns job id so that it can be tracked and returns future so that it can be synchronized.
 */
public class AddJobResult {
    private String jobId;
    private Future<WriteResult> future;

    public AddJobResult(String jobId, Future<WriteResult> future) {
        this.jobId = jobId;
        this.future = future;
    }

    /** Returns the auto generated cloud firestore job id so that this entity can be tracked */
    public String getJobId() {
        return jobId;
    }

    /** Returns future of update details so that it can be synchronized. */
    public Future<WriteResult> getFuture() {
        return future;
    }
}
