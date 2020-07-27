package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.common.collect.ImmutableMap;
import com.google.cloud.firestore.*;
import com.google.utils.FireStoreUtils;
import com.google.common.collect.ImmutableList; 
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
    // should delete all the documents that the tests create
    private static final int BATCH_SIZE = 20;

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
        Location expectedLocation =  new Location(
                "Google", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "Programming using java";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        Map<String, Boolean> expectedRequirements = ImmutableMap.of(
                                                        DRIVING_LICENSE_C.getRequirementId(), true,
                                                        O_LEVEL.getRequirementId(), true,
                                                        ENGLISH.getRequirementId(), true);
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
        Location expectedLocation =  new Location(
                "Google", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        Map<String, Boolean> expectedRequirements = ImmutableMap.of(
                                                        DRIVING_LICENSE_C.getRequirementId(), false,
                                                        O_LEVEL.getRequirementId(), true,
                                                        ENGLISH.getRequirementId(), true);
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
        Future<DocumentSnapshot> documentSnapshotFuture = editedDocRef.get();

        // future.get() blocks on response.
        DocumentSnapshot documentSnapshot = documentSnapshotFuture.get();

        Job actualJob = documentSnapshot.toObject(Job.class);

        assertEquals(updatedJob, actualJob);
    }

    @Test
    public void markJobPostAsDeleted_normalInput_success()
            throws ExecutionException, InterruptedException, IOException {
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
        Location expectedLocation =  new Location(
                "Maple Tree", "123456", SingaporeRegion.ENTIRE, 0, 0);
        String expectedJobDescription = "Fighting to defeat hair line recede";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        Map<String, Boolean> expectedRequirements = ImmutableMap.of(
                                                        DRIVING_LICENSE_C.getRequirementId(), false,
                                                        O_LEVEL.getRequirementId(), true,
                                                        ENGLISH.getRequirementId(), false);
        long expectedPostExpiry = System.currentTimeMillis();
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
    public void fetchAllEligibleJobs_normalInput_success()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        Map<String, Boolean> requirements = ImmutableMap.of(
                                                DRIVING_LICENSE_C.getRequirementId(), false,
                                                O_LEVEL.getRequirementId(), true,
                                                ENGLISH.getRequirementId(), false);
        Job job1 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job2 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job3 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job4 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job5 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        List<String> skills = Requirement.getRequirementIds(Arrays.asList(O_LEVEL, ENGLISH));

        // Act.
        Future<Collection<Job>> jobsFuture = jobsDatabase.fetchAllEligibleJobs(skills);

        // Assert.
        Collection<Job> expectedJobs = new HashSet<>(Arrays.asList(job1, job2, job5));
        Collection<Job> actualJobs = jobsFuture.get();
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void fetchAllEligibleJobs_noJobMatch_noJobSelected()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        Map<String, Boolean> requirements = ImmutableMap.of(
                                                DRIVING_LICENSE_C.getRequirementId(), false,
                                                O_LEVEL.getRequirementId(), true,
                                                ENGLISH.getRequirementId(), false);
        Job job1 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job2 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job3 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job4 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job5 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        List<String> skills = Requirement.getRequirementIds(Arrays.asList(DRIVING_LICENSE_C));

        // Act.
        Future<Collection<Job>> jobsFuture = jobsDatabase.fetchAllEligibleJobs(skills);

        // Assert.
        Collection<Job> expectedJobs = new HashSet<>();
        Collection<Job> actualJobs = jobsFuture.get();
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void fetchAllEligibleJobs_hasDeletedJob_deletedJobNotSelected()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        Map<String, Boolean> requirements = ImmutableMap.of(
                                                DRIVING_LICENSE_C.getRequirementId(), false,
                                                O_LEVEL.getRequirementId(), true,
                                                ENGLISH.getRequirementId(), false);
        Job job1 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job2 = requirementFileterTestJobDataCreation(JobStatus.DELETED, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job3 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job4 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job5 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        List<String> skills = Requirement.getRequirementIds(Arrays.asList(O_LEVEL, ENGLISH));

        // Act.
        Future<Collection<Job>> jobsFuture = jobsDatabase.fetchAllEligibleJobs(skills);

        // Assert.
        Collection<Job> expectedJobs = new HashSet<>(Arrays.asList(job1, job5));
        Collection<Job> actualJobs = jobsFuture.get();
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void fetchAllEligibleJobs_missingRequirement_success()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        // DRIVING_LICENSE_C is missing
        Map<String, Boolean> requirements = ImmutableMap.of(
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), false);
        Job job1 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job2 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job3 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job4 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job5 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        List<String> skills = Requirement.getRequirementIds(Arrays.asList(O_LEVEL, ENGLISH));

        // Act.
        Future<Collection<Job>> jobsFuture = jobsDatabase.fetchAllEligibleJobs(skills);

        // Assert.
        Collection<Job> expectedJobs = new HashSet<>(Arrays.asList(job1, job2, job5));
        Collection<Job> actualJobs = jobsFuture.get();
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void fetchAllEligibleJobs_redundantAndMissingRequirement_success()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        // DRIVING_LICENSE_C is missing
        // "HEIGHT" is redundant
        Map<String, Boolean> requirements = ImmutableMap.of(
                "HEIGHT", true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), false);
        Job job1 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job2 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job3 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), true,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), true);
        Job job4 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), false,
                ENGLISH.getRequirementId(), true);
        Job job5 = requirementFileterTestJobDataCreation(JobStatus.ACTIVE, requirements);

        List<String> skills = Requirement.getRequirementIds(Arrays.asList(O_LEVEL, ENGLISH));

        // Act.
        Future<Collection<Job>> jobsFuture = jobsDatabase.fetchAllEligibleJobs(skills);

        // Assert.
        Collection<Job> expectedJobs = new HashSet<>(Arrays.asList(job1, job2, job5));
        Collection<Job> actualJobs = jobsFuture.get();
        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void fetchJobPage_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange
        /* fields that affect the sorting/filtering of the job page details */
        // this should be returned first (order will be descending by salary, and region will be central)
        Location location1 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment1 = new JobPayment(0, 5000, PaymentFrequency.WEEKLY);

        // this should be returned second
        Location location2 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment2 = new JobPayment(0, 4000, PaymentFrequency.WEEKLY);

        // this should be returned third
        Location location3 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment3 = new JobPayment(0, 3000, PaymentFrequency.WEEKLY);

        // this should not be returned (minLimit will be set to 104001)
        Location location4 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment4 = new JobPayment(0, 2000, PaymentFrequency.WEEKLY);

        // this should not be returned (region will be set to CENTRAL)
        Location location5 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.NORTH, 0, 0);
        JobPayment jobPayment5 = new JobPayment(0, 2000, PaymentFrequency.WEEKLY);
        int annualMaxJob5 = (int) jobPayment5.getAnnualMax();

        // this should not be returned (only active jobs should be shown)
        JobStatus jobStatusExpired = JobStatus.EXPIRED;
        Location location6 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.NORTH, 0, 0);
        JobPayment jobPayment6 = new JobPayment(0, 3000, PaymentFrequency.WEEKLY);

        List<Job> jobs = createTestJobs(6);

        Job job1 = jobs.get(0).toBuilder()
                        .setLocation(location1)
                        .setJobPay(jobPayment1)
                        .build();

        Job job2 = jobs.get(1).toBuilder()
                        .setLocation(location2)
                        .setJobPay(jobPayment2)
                        .build();
        
        Job job3 = jobs.get(2).toBuilder()
                        .setLocation(location3)
                        .setJobPay(jobPayment3)
                        .build();
        
        Job job4 = jobs.get(3).toBuilder()
                        .setLocation(location4)
                        .setJobPay(jobPayment4)
                        .build();

        Job job5 = jobs.get(4).toBuilder()
                        .setLocation(location5)
                        .setJobPay(jobPayment5)
                        .build();

        Job job6 = jobs.get(5).toBuilder()
                        .setLocation(location6)
                        .setJobPay(jobPayment6)
                        .setJobStatus(jobStatusExpired)
                        .build();
    
        // the jobs will be added in a random order
        firestore.collection(TEST_JOB_COLLECTION).add(job5).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job1).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job3).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job4).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job2).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job6).get();

        JobPage expectedJobPage = new JobPage(/* jobList= */ Arrays.asList(job1, job2, job3),
            /* totalCount= */ 3, Range.between(1, 3));

        // Act
        // sorting is already defaulted to SALARY and ordering is defaulted to DESCENDING
        // maxLimit is defaulted to Integer.MAX_VALUE
        JobQuery jobQuery = new JobQuery().setMinLimit(annualMaxJob5 + 1).setRegion(SingaporeRegion.CENTRAL);
        
        JobPage actualJobPage = jobsDatabase.fetchJobPage(jobQuery).get();

        // Assert
        assertEquals(expectedJobPage, actualJobPage);
    }

     @Test
    public void fetchJobPage_noJobsFitFilters_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange
        /* fields that affect the sorting/filtering of the job page details */
        Location location1 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment1 = new JobPayment(0, 5000, PaymentFrequency.WEEKLY);

        Location location2 =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        JobPayment jobPayment2 = new JobPayment(0, 4000, PaymentFrequency.WEEKLY);

        List<Job> jobs = createTestJobs(6);

        Job job1 = jobs.get(0).toBuilder()
                        .setLocation(location1)
                        .setJobPay(jobPayment1)
                        .build();

        Job job2 = jobs.get(1).toBuilder()
                        .setLocation(location2)
                        .setJobPay(jobPayment2)
                        .build();
    
        firestore.collection(TEST_JOB_COLLECTION).add(job1).get();
        firestore.collection(TEST_JOB_COLLECTION).add(job2).get();

        JobPage expectedJobPage = new JobPage(/* jobList= */ Arrays.asList(),
            /* totalCount= */ 0, Range.between(0, 0));

        // Act
        // sorting is already defaulted to SALARY and ordering is defaulted to DESCENDING
        // minLimit is defaulted to 0 and maxLimit is defaulted to Integer.MAX_VALUE
        JobQuery jobQuery = new JobQuery().setRegion(SingaporeRegion.NORTH);
        
        JobPage actualJobPage = jobsDatabase.fetchJobPage(jobQuery).get();

        // Assert
        assertEquals(expectedJobPage, actualJobPage);
    }

    private Job requirementFileterTestJobDataCreation(JobStatus jobStatus, Map<String, Boolean> requirements)
            throws ExecutionException, InterruptedException {
        String jobName = "Programmer";
        Location location = new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL,0, 0);
        String jobDescription = "Fighting to defeat hair line recede";
        JobPayment jobPayment = new JobPayment(0, 5000, PaymentFrequency.MONTHLY);
        long postExpiry = System.currentTimeMillis();
        JobDuration jobDuration = JobDuration.ONE_MONTH;

        Job job = Job.newBuilder()
                .setJobStatus(jobStatus)
                .setJobTitle(jobName)
                .setLocation(location)
                .setJobDescription(jobDescription)
                .setJobPay(jobPayment)
                .setRequirements(requirements)
                .setPostExpiry(postExpiry)
                .setJobDuration(jobDuration)
                .build();

        // future.get() blocks on response.
        firestore.collection(TEST_JOB_COLLECTION).add(job).get();

        return job;
    }

    /**
     * This will add all the default properties to the job. For particular tests, if the properties of 
     * a job need to be changed, then we can just use the job.toBuilder() method and then reset the properties
     * that way.
     * @param count The number of jobs you want created.
     * 
     * @return The list of jobs created.
     */
    private static List<Job> createTestJobs(int count) {
        // Arrange
        /* fields that don't affect the job page details */
        JobStatus jobStatus = JobStatus.ACTIVE;
        String jobName = "Programmer";
        String jobDescription = "Fighting to defeat hair line recede";
        Location location =  new Location(
                "Maple Tree", "123456", SingaporeRegion.CENTRAL, 0, 0);
        Map<String, Boolean> requirements = ImmutableMap.of(
                DRIVING_LICENSE_C.getRequirementId(), false,
                O_LEVEL.getRequirementId(), true,
                ENGLISH.getRequirementId(), false);
        long postExpiry = System.currentTimeMillis();
        JobDuration jobDuration = JobDuration.ONE_MONTH;
        JobPayment jobPayment = new JobPayment(0, 5000, PaymentFrequency.WEEKLY);
        
        ImmutableList.Builder<Job> jobs = ImmutableList.builder();

        /* add the required number of jobs to the list */
        for (int i = 0; i < count; i++) {
            Job job = Job.newBuilder()
                    .setJobStatus(jobStatus)
                    .setJobTitle(jobName)
                    .setJobDescription(jobDescription)
                    .setLocation(location)
                    .setRequirements(requirements)
                    .setPostExpiry(postExpiry)
                    .setJobDuration(jobDuration)
                    .setJobPay(jobPayment)
                    .build();
            jobs.add(job);
        }

        return jobs.build();
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
