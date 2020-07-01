package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firestore.FireStoreUtils;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class JobsDatabaseTest {

    private static final String TEST_JOB_COLLECTION = "Jobs_Test";
    private static final int BATCH_SIZE = 10;

    Firestore firestore;
    JobsDatabase jobsDatabase;

    @Before
    public void setUp() throws IOException {
        firestore = FireStoreUtils.getFireStore();
        jobsDatabase = new JobsDatabase(firestore);
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        String expectedJobName = "Noogler";
        Job job = new Job(expectedJobName);
        Future<DocumentReference> addedJobFuture = jobsDatabase.addJob(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();

        Job actualJob = document.toObject(Job.class);
        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void editJob_NormalInput_success() throws ExecutionException, InterruptedException {
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION)
                .add(new Job("Noogler"));

        String expectedJobName = "Googler";
        Job job = new Job(expectedJobName);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        jobsDatabase.editJob(jobId, job);

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();
        Job actualJob = documentSnapshot.toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION)
                .add(new Job("Noogler"));

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        Job job = jobsDatabase.fetchJob(jobId).get();

        String actualJobName = job.getJobName();
        String expectedJobName = "Noogler";

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @After
    public void clearCollection() {
        deleteCollection(firestore.collection(TEST_JOB_COLLECTION), BATCH_SIZE);
    }

    /** Delete a collection in batches to avoid out-of-memory errors.
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    private void deleteCollection(CollectionReference collection, int batchSize) {
        try {
            // retrieve a small batch of documents to avoid out-of-memory errors
            ApiFuture<QuerySnapshot> future = collection.limit(batchSize).get();
            int deleted = 0;
            // future.get() blocks on document retrieval
            List<QueryDocumentSnapshot> documents = future.get().getDocuments();
            for (QueryDocumentSnapshot document : documents) {
                document.getReference().delete();
                ++deleted;
            }
            if (deleted >= batchSize) {
                // retrieve and delete another batch
                deleteCollection(collection, batchSize);
            }
        } catch (Exception e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }
}