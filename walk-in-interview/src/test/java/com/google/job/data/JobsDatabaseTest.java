package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firestore.FireStoreUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Tests for {@link JobsDatabase} class. */
public final class JobsDatabaseTest {
    // TODO(issue/15): Add failure test case
    // TODO(issue/16): Move the clear database method as @After

    private static final String TEST_JOB_COLLECTION = "Jobs";
    private static final int BATCH_SIZE = 10;

    JobsDatabase jobsDatabase;
    Firestore firestore;

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
    public void setUp() {
        jobsDatabase = new JobsDatabase();
        firestore = FireStoreUtils.getFireStore();
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        String expectedJobName = "Software Engineer";
        Job job = new Job(expectedJobName);

        // Act.
        Future<DocumentReference> addedJobFuture = jobsDatabase.addJob(job);

        // Assert.
        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();

        Job actualJob = document.toObject(Job.class);
        String actualJobName = actualJob.getJobTitle();

        assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void setJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        Job oldJob = new Job("Noogler");
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION)
                .add(oldJob);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        String expectedJobName = "Googler";
        Job job = new Job(expectedJobName);

        // Act.
        Future<WriteResult> editedDocRef = jobsDatabase.setJob(jobId, job);

        // Assert.
        // future.get() blocks on response.
        editedDocRef.get();

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).
                document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();
        Job actualJob = documentSnapshot.toObject(Job.class);

        String actualJobName = actualJob.getJobTitle();

        assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        Job job = new Job("Programmer");

        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        // Act.
        Optional<Job> jobOptional = jobsDatabase.fetchJob(jobId).get();

        // Assert.
        assertTrue(jobOptional.isPresent());

        Job actualJob = jobOptional.get();

        String actualJobName = actualJob.getJobTitle();
        String expectedJobName = "Programmer";

        assertEquals(expectedJobName, actualJobName);
    }

    @After
    public void clearCollection() {
        deleteCollection(firestore.collection(TEST_JOB_COLLECTION), BATCH_SIZE);
    }
}
