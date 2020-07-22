package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.utils.FireStoreUtils;
import org.junit.*;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import org.apache.commons.lang3.Range;

import static com.google.job.data.Requirement.*;
import static org.junit.Assert.*;

/** Tests for {@link JobsDatabase} class. */
public final class JobsDatabaseTest {
    // TODO(issue/15): Add failure test case

    private static final String TEST_JOB_COLLECTION = "Jobs";
    private static final int BATCH_SIZE = 10;

    static JobsDatabase jobsDatabase;
    static Firestore firestore;

    @BeforeClass
    public static void setUp() throws IOException {
        jobsDatabase = new JobsDatabase();
        firestore = FireStoreUtils.getFireStore();
    }

    @Before
    public void clearCollection() {
        try {
            deleteCollection(firestore.collection(TEST_JOB_COLLECTION), BATCH_SIZE);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownCollection() {
        try {
            deleteCollection(firestore.collection(TEST_JOB_COLLECTION), BATCH_SIZE);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error tearing down collection : " + e.getMessage());
        }
    }

    @Test
    public void addJob_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange.
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Software Engineer";
        Location expectedLocation =  new Location("Google", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "Programming using java";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY, 260000);
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
        Future<WriteResult> future = jobsDatabase.addJob(job);

        // Assert.
        // future.get() blocks on response.
        future.get();

        //asynchronously retrieve all documents
        ApiFuture<QuerySnapshot> futures = firestore.collection(TEST_JOB_COLLECTION).get();
        // future.get() blocks on response
        List<QueryDocumentSnapshot> documents = futures.get().getDocuments();

        // Since clears the collection before each test, it is the only document in the collection
        QueryDocumentSnapshot document = documents.get(0);
        String expectedJobId = document.getId();

        Job actualJob = document.toObject(Job.class);
        Job expectedJob = Job.newBuilder()
                .setJobId(expectedJobId)
                .setJobStatus(expectedJobStatus)
                .setJobTitle(expectedJobName)
                .setLocation(expectedLocation)
                .setJobDescription(expectedJobDescription)
                .setJobPay(expectedJobPayment)
                .setRequirements(expectedRequirements)
                .setPostExpiry(expectedPostExpiry)
                .setJobDuration(expectedJobDuration)
                .build();

        assertEquals(expectedJob, actualJob);
    }

    @Test
    public void setJob_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Noogler";
        Location expectedLocation =  new Location("Google", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY, 260000);
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
                .setJobId(jobId)
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
        Future<DocumentReference> editedDocRefFuture = jobsDatabase.setJob(jobId, updatedJob);

        // Assert.
        // future.get() blocks on response.
        DocumentReference editedDocRef = editedDocRefFuture.get();

        // Asynchronously retrieve the document.
        Future<DocumentSnapshot> documentSnapshoFuture = editedDocRef.get();

        // future.get() blocks on response.
        DocumentSnapshot documentSnapshot = documentSnapshoFuture.get();

        Job actualJob = documentSnapshot.toObject(Job.class);

        assertEquals(updatedJob, actualJob);
    }

    @Test
    public void markJobPostAsDeleted_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange.
        Job job = new Job();
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        // Gets job id.
        String jobId = document.getId();

        // Act.
        Future<DocumentReference> resultFuture = this.jobsDatabase.markJobPostAsDeleted(jobId);

        // Assert.
        // future.get() blocks on response.
        documentReference = resultFuture.get();

        // Asynchronously retrieve the document.
        future = documentReference.get();

        // future.get() blocks on response.
        document = future.get();

        Job actualJob = document.toObject(Job.class);
        JobStatus actualJobStatus = actualJob.getJobStatus();

        assertEquals(JobStatus.DELETED, actualJobStatus);
    }

    @Test
    public void fetchJob_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange.
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Programmer";
        Location expectedLocation =  new Location("Maple Tree", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "Fighting to defeat hair line recede";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY, 260000);
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

    @Test
    public void fetchJobPage_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        /* fields that don't affect the job page details */
        JobStatus jobStatus = JobStatus.ACTIVE;
        String jobName = "Programmer";
        String jobDescription = "Fighting to defeat hair line recede";
        List<String> requirements = Requirement.getLocalizedNames(Arrays.asList(O_LEVEL), "en");
        long postExpiry = System.currentTimeMillis();
        JobDuration jobDuration = JobDuration.ONE_MONTH;

        /* fields that affect the sorting/filtering of the job page details */
        // this should be returned first (order will be descending by salary, and region will be central)
        Location location1 =  new Location("Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment1 = new JobPayment(0, 5000, PaymentFrequency.WEEKLY, 260000);

        // this should be returned second
        Location location2 =  new Location("Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment2 = new JobPayment(0, 4000, PaymentFrequency.WEEKLY, 208000);

        // this should be returned third
        Location location3 =  new Location("Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment3 = new JobPayment(0, 3000, PaymentFrequency.WEEKLY, 156000);

        // this should not be returned (minLimit will be set to 104001)
        Location location4 =  new Location("Maple Tree", "123456", SingaporeRegion.NORTH, 0, 0);
        JobPayment jobPayment4 = new JobPayment(0, 2000, PaymentFrequency.WEEKLY, 104000);

        Job job1 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location1)
                .setJobPay(jobPayment1)
                .build();

        Job job2 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location2)
                .setJobPay(jobPayment2)
                .build();

        Job job3 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location3)
                .setJobPay(jobPayment3)
                .build();

        Job job4 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location4)
                .setJobPay(jobPayment4)
                .build();
    
        // the jobs will be added in a random order
        firestore.collection(TEST_JOB_COLLECTION).add(job1).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job3).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job4).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job2).get();

        JobPage expectedJobPage = new JobPage(/* jobList= */ Arrays.asList(job1, job2, job3),
            /* totalCount= */ 3, Range.between(1, 3));

        // sorting is already defaulted to SALARY and ordering is defaulted to DESCENDING
        // maxLimit is defaulted to Integer.MAX_VALUE
        JobQuery jobQuery = new JobQuery().setMinLimit(104001).setRegion(SingaporeRegion.CENTRAL);
        
        JobPage actualJobPage = jobsDatabase.fetchJobPage(jobQuery).get();

        assertEquals(expectedJobPage, actualJobPage);
    }

     @Test
    public void fetchJobPage_noJobsFitFilters_success() throws ExecutionException, InterruptedException, IOException {
        /* fields that don't affect the job page details */
        JobStatus jobStatus = JobStatus.ACTIVE;
        String jobName = "Programmer";
        String jobDescription = "Fighting to defeat hair line recede";
        List<String> requirements = Requirement.getLocalizedNames(Arrays.asList(O_LEVEL), "en");
        long postExpiry = System.currentTimeMillis();
        JobDuration jobDuration = JobDuration.ONE_MONTH;

        /* fields that affect the sorting/filtering of the job page details */
        Location location1 =  new Location("Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment1 = new JobPayment(0, 5000, PaymentFrequency.WEEKLY, 260000);

        Location location2 =  new Location("Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment2 = new JobPayment(0, 4000, PaymentFrequency.WEEKLY, 208000);

        Job job1 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location1)
                .setJobPay(jobPayment1)
                .build();

        Job job2 = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setJobDescription(jobDescription)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .setLocation(location2)
                .setJobPay(jobPayment2)
                .build();
    
        firestore.collection(TEST_JOB_COLLECTION).add(job1).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job2).get();

        JobPage expectedJobPage = new JobPage(/* jobList= */ Arrays.asList(),
            /* totalCount= */ 0, Range.between(0, 0));

        // sorting is already defaulted to SALARY and ordering is defaulted to DESCENDING
        // minLimit is defaulted to 0 and maxLimit is defaulted to Integer.MAX_VALUE
        JobQuery jobQuery = new JobQuery().setRegion(SingaporeRegion.NORTH);
        
        JobPage actualJobPage = jobsDatabase.fetchJobPage(jobQuery).get();

        assertEquals(expectedJobPage, actualJobPage);
    }

    /**
     * Delete a collection in batches to avoid out-of-memory errors.
     *
     * Batch size may be tuned based on document size (atmost 1MB) and application requirements.
     */
    private static void deleteCollection(CollectionReference collection, int batchSize)
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
}
