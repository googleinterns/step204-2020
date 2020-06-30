package com.google.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.firestore.FireStoreUtils;

import java.util.concurrent.ExecutionException;

public class JobsDatabase {
    // TODO
    private static final String COLLECTION = "Jobs";
    private final Firestore firestore;

    public JobsDatabase(Firestore firestore) {
        this.firestore = firestore;
    }

    // TODO: change the return type to "string" to return jobId
    public String addJob(Job newJob) throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = FireStoreUtils.store(COLLECTION, newJob);

        String jobId = addedDocRef.get().getId();
        return jobId;
    }

    public void editJob(String jobId, Job job) {
        // Overwrites the whole document
        firestore.collection(COLLECTION).document(jobId).set(job);
    }

    public Job fetchJob(String jobId) throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(COLLECTION).document(jobId);
        DocumentSnapshot document = FireStoreUtils.load(docRef);

        Job job = null;
        if (document.exists()) {
            // convert document to POJO
            job = document.toObject(Job.class);
        } else {
            // TODO
        }

        return job;
    }
}
