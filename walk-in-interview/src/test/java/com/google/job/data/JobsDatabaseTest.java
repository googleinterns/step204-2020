package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.utils.FireStoreUtils;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Tests for {@link JobsDatabase} class. */
public final class JobsDatabaseTest {
    // TODO(issue/15): Add failure test case

    private static final String TEST_JOB_COLLECTION = "Jobs";
    private static final int BATCH_SIZE = 10;
    private static final String DUMMY = "dummy";

    JobsDatabase jobsDatabase;
    Firestore firestore;

    /**
     * Delete a collection in batches to avoid out-of-memory errors.
     *
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    private void deleteCollection(CollectionReference collection, int batchSize)
            throws ExecutionException, InterruptedException {
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
    }

    @Before
    public void setUp() {
        jobsDatabase = new JobsDatabase();
        firestore = FireStoreUtils.getFireStore();
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Software Engineer";
        JobLocation expectedJobLocation =  new JobLocation("Google", 0, 0);
        String expectedJobDescription = "Programming using java";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("O Level", "Driving License");
        long expectedPostExpiry = System.currentTimeMillis();

        Optional<Duration> expectedJobDuration = Optional.of(Duration.SIX_MONTHS);

        Job job = new Job(DUMMY, expectedJobName, expectedJobStatus, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

        // Act.
        Future<DocumentReference> addedJobFuture = jobsDatabase.addJob(job);

        // Assert.
        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();

        Job actualJob = document.toObject(Job.class);

        JobStatus actualJobStatus = actualJob.getJobStatus();
        assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobTitle();
        assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPay();
        assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        assertEquals(expectedRequirements, actualRequirements);

        long actualPostExpiry = actualJob.getPostExpiry();
        assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        assertEquals(expectedJobDuration, actualJobDuration);
    }

    @Test
    public void setJob_NormalInput_success() throws ExecutionException, InterruptedException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Noogler";
        JobLocation expectedJobLocation =  new JobLocation("Google", 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("Bachelor Degree");
        long expectedPostExpiry = System.currentTimeMillis();
        Optional<Duration> expectedJobDuration = Optional.of(Duration.ONE_MONTH);

        Job oldJob = new Job(DUMMY, expectedJobName, expectedJobStatus, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(oldJob);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        expectedJobName = "Googler";
        Job updatedJob = new Job(expectedJobName, expectedJobStatus, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

        // Act.
        Future<WriteResult> editedDocRef = jobsDatabase.setJob(jobId, updatedJob);

        // Assert.
        // future.get() blocks on response.
        editedDocRef.get();

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).
                document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

        Job actualJob = documentSnapshot.toObject(Job.class);

        JobStatus actualJobStatus = actualJob.getJobStatus();
        assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobTitle();
        assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPay();
        assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        assertEquals(expectedRequirements, actualRequirements);

        long actualPostExpiry = actualJob.getPostExpiry();
        assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        assertEquals(expectedJobDuration, actualJobDuration);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Programmer";
        JobLocation expectedJobLocation =  new JobLocation("Maple Tree", 0, 0);
        String expectedJobDescription = "Fighting to defeat hair line recede";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("Bachelor Degree");
        long expectedPostExpiry = System.currentTimeMillis();;
        Optional<Duration> expectedJobDuration = Optional.of(Duration.ONE_MONTH);

        Job job = new Job(DUMMY, expectedJobName, expectedJobStatus, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

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

        JobStatus actualJobStatus = actualJob.getJobStatus();
        assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobTitle();
        assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPay();
        assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        assertEquals(expectedRequirements, actualRequirements);

        long actualPostExpiry = actualJob.getPostExpiry();
        assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        assertEquals(expectedJobDuration, actualJobDuration);
    }

    @After
    public void clearCollection() {
        try {
            deleteCollection(firestore.collection(TEST_JOB_COLLECTION), BATCH_SIZE);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }
}
