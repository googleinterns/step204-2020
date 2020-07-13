package com.google.job.data;

import com.google.cloud.firestore.WriteResult;

import java.util.concurrent.Future;

public class AddJobResult {
    private String jobId;
    private Future<WriteResult> future;

    public AddJobResult(String jobId, Future<WriteResult> future) {
        this.jobId = jobId;
        this.future = future;
    }

    public String getJobId() {
        return jobId;
    }

    public Future<WriteResult> getFuture() {
        return future;
    }
}
