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

import static com.google.job.data.Requirement.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/** Tests for {@link JobsDatabase} class. */
public final class JobsDatabaseTest {
    // TODO(issue/15): Add failure test case

    private static final String TEST_JOB_COLLECTION = "Jobs";
    private static final int BATCH_SIZE = 10;

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
        Location expectedLocation =  new Location("Google", "123456", 0, 0);
        String expectedJobDescription = "Programming using java";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        List<String> expectedRequirements = Requirement.getLocalizedNames(
                Arrays.asList(DRIVING_LICENSE_C, O_LEVEL, ENGLISH), "en");
        long expectedPostExpiry = System.currentTimeMillis();
        JobDuration expectedJobDuration = JobDuration.SIX_MONTHS;

        Job job = Job.newBuilder()
                .setJobStatus(expectedJobStatus)
                .setJobTitle(expectedJobName)
                .setLocation(expectedLocation)
                .setJobDescription(expectedJobDescription)
                .setJobPay(expectedJobPayment)
                .setRequirements(expectedRequirements)
                .setPostExpiry(expectedPostExpiry)
                .setJobDuration(expectedJobDuration)
                .build();

        // Act.
        DocumentReference documentReference = jobsDatabase.addJob(job);

        // Assert.
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();

        Job actualJob = document.toObject(Job.class);

        assertEquals(job, actualJob);
    }

    @Test
    public void setJob_NormalInput_success() throws ExecutionException, InterruptedException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Noogler";
        Location expectedLocation =  new Location("Google", "123456", 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        List<String> expectedRequirements = Requirement.getLocalizedNames(
                Arrays.asList(O_LEVEL, ENGLISH), "en");
        long expectedPostExpiry = System.currentTimeMillis();
        JobDuration expectedJobDuration = JobDuration.ONE_MONTH;

        Job oldJob = Job.newBuilder()
                .setJobStatus(expectedJobStatus)
                .setJobTitle(expectedJobName)
                .setLocation(expectedLocation)
                .setJobDescription(expectedJobDescription)
                .setJobPay(expectedJobPayment)
                .setRequirements(expectedRequirements)
                .setPostExpiry(expectedPostExpiry)
                .setJobDuration(expectedJobDuration)
                .build();

        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(oldJob);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        expectedJobName = "Googler";
        Job updatedJob = Job.newBuilder()
                .setJobStatus(expectedJobStatus)
                .setJobTitle(expectedJobName)
                .setLocation(expectedLocation)
                .setJobDescription(expectedJobDescription)
                .setJobPay(expectedJobPayment)
                .setRequirements(expectedRequirements)
                .setPostExpiry(expectedPostExpiry)
                .setJobDuration(expectedJobDuration)
                .build();

        // Act.
        Future<WriteResult> editedDocRef = jobsDatabase.setJob(jobId, updatedJob);

        // Assert.
        // future.get() blocks on response.
        editedDocRef.get();

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).
                document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

        Job actualJob = documentSnapshot.toObject(Job.class);

        assertEquals(updatedJob, actualJob);
    }

    @Test
    public void updateJobId_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        Job job = new Job();
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String expectedJobId = document.getId();

        // Act.
        Future<WriteResult> resultFuture = this.jobsDatabase.updateJobId(expectedJobId);

        // Assert.
        // future.get() blocks on response.
        resultFuture.get();

        documentReference = firestore.collection(TEST_JOB_COLLECTION).document(expectedJobId);
        // Asynchronously retrieve the document.
        future = documentReference.get();

        // future.get() blocks on response.
        document = future.get();

        Job actualJob = document.toObject(Job.class);
        String actualJobId = actualJob.getJobId();

        assertEquals(expectedJobId, actualJobId);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        // Arrange.
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Programmer";
        Location expectedLocation =  new Location("Maple Tree", "123456", 0, 0);
        String expectedJobDescription = "Fighting to defeat hair line recede";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        List<String> expectedRequirements = Requirement.getLocalizedNames(Arrays.asList(O_LEVEL), "en");
        long expectedPostExpiry = System.currentTimeMillis();;
        JobDuration expectedJobDuration = JobDuration.ONE_MONTH;

        Job job = Job.newBuilder()
                .setJobStatus(expectedJobStatus)
                .setJobTitle(expectedJobName)
                .setLocation(expectedLocation)
                .setJobDescription(expectedJobDescription)
                .setJobPay(expectedJobPayment)
                .setRequirements(expectedRequirements)
                .setPostExpiry(expectedPostExpiry)
                .setJobDuration(expectedJobDuration)
                .build();

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

        assertEquals(job, actualJob);
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
