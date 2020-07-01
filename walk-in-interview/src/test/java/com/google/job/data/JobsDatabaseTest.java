package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.CollectionReference;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.cloud.firestore.QuerySnapshot;
import com.google.firestore.FireStoreUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class JobsDatabaseTest {

    private static final String TEST_JOB_COLLECTION = "Jobs_Test";
    private static final int BATCH_SIZE = 10;

    Firestore firestore;
    JobsDatabase jobsDatabase;

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

    @Before
    public void setUp() throws IOException {
        firestore = FireStoreUtils.getFireStore();
        jobsDatabase = new JobsDatabase(firestore);
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        deleteCollection(firestore.collection(JOB_COLLECTION), BATCH_SIZE);

        String expectedJobName = "Noogler";
        Job job = new Job(expectedJobName);
        jobsDatabase.addJob(job);

        // Asynchronously retrieves all documents.
        ApiFuture<QuerySnapshot> future = firestore.collection(JOB_COLLECTION).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documentSnapshots = future.get().getDocuments();
        Job actualJob = documentSnapshots.get(0).toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void editJob_NormalInput_success() throws ExecutionException, InterruptedException {
        deleteCollection(firestore.collection(JOB_COLLECTION), BATCH_SIZE);

        firestore.collection(JOB_COLLECTION).add(new Job("Noogler"));

        String expectedJobName = "Googler";
        Job job = new Job(expectedJobName);

        // Asynchronously retrieves all documents.
        ApiFuture<QuerySnapshot> future = firestore.collection(JOB_COLLECTION).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documentSnapshots = future.get().getDocuments();
        String jobId = documentSnapshots.get(0).getId();

        jobsDatabase.editJob(jobId, job);

        documentSnapshots = firestore.collection(JOB_COLLECTION).get().get().getDocuments();
        Job actualJob = documentSnapshots.get(0).toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        deleteCollection(firestore.collection(JOB_COLLECTION), BATCH_SIZE);

        firestore.collection(JOB_COLLECTION).add(new Job("Noogler"));

        // Asynchronously retrieves all documents.
        ApiFuture<QuerySnapshot> future = firestore.collection(JOB_COLLECTION).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documentSnapshots = future.get().getDocuments();
        String jobId = documentSnapshots.get(0).getId();

        Job job = jobsDatabase.fetchJob(jobId).get();

        String actualJobName = job.getJobName();
        String expectedJobName = "Noogler";

        Assert.assertEquals(expectedJobName, actualJobName);
    }
}