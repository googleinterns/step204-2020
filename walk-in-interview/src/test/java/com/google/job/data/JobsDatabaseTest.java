package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.utils.FireStoreUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class JobsDatabaseTest {

    private static final String TEST_JOB_COLLECTION = "Jobs";

    Firestore firestore;
    JobsDatabase jobsDatabase;

    @Before
    public void setUp() throws IOException {
        firestore = FireStoreUtils.getFireStore();
        jobsDatabase = new JobsDatabase(firestore);
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Software Engineer";
        JobLocation expectedJobLocation =  new JobLocation("Google", 0, 0);
        String expectedJobDescription = "Programming using java";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("O Level", "Driving License");
        LocalDate expectedPostExpiry = LocalDate.of(2020, 7, 2);
        Optional<Duration> expectedJobDuration = Optional.of(Duration.SIX_MONTHS);

        Job job = new Job(expectedJobStatus, expectedJobName, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);
        Future<DocumentReference> addedJobFuture = jobsDatabase.addJob(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        Job actualJob = document.toObject(Job.class);

        JobStatus actualJobStatus = actualJob.getJobStatus();
        Assert.assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobName();
        Assert.assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        Assert.assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        Assert.assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPayment();
        Assert.assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        Assert.assertEquals(expectedRequirements, actualRequirements);

        LocalDate actualPostExpiry = actualJob.getJobExpiry();
        Assert.assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        Assert.assertEquals(expectedJobDuration, actualJobDuration);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }

    @Test
    public void editJob_NormalInput_success() throws ExecutionException, InterruptedException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Noogler";
        JobLocation expectedJobLocation =  new JobLocation("Google", 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("Bachelor Degree");
        LocalDate expectedPostExpiry = LocalDate.of(2020, 7, 3);
        Optional<Duration> expectedJobDuration = Optional.of(Duration.ONE_MONTH);

        Job job = new Job(expectedJobStatus, expectedJobName, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        expectedJobName = "Googler";
        Job updatedJob = new Job(expectedJobStatus, expectedJobName, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);
        Future<WriteResult> edittedDocRef = jobsDatabase.editJob(jobId, updatedJob);
        // future.get() blocks on response.
        edittedDocRef.get();

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();

        Job actualJob = documentSnapshot.toObject(Job.class);

        JobStatus actualJobStatus = actualJob.getJobStatus();
        Assert.assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobName();
        Assert.assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        Assert.assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        Assert.assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPayment();
        Assert.assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        Assert.assertEquals(expectedRequirements, actualRequirements);

        LocalDate actualPostExpiry = actualJob.getJobExpiry();
        Assert.assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        Assert.assertEquals(expectedJobDuration, actualJobDuration);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        JobStatus expectedJobStatus = JobStatus.ACTIVE;
        String expectedJobName = "Noogler";
        JobLocation expectedJobLocation =  new JobLocation("Programmer", 0, 0);
        String expectedJobDescription = "New employee";
        JobPayment expectedJobPayment = new JobPayment(0, 5000, Frequency.MONTHLY);
        List<String> expectedRequirements = Arrays.asList("Bachelor Degree");
        LocalDate expectedPostExpiry = LocalDate.of(2020, 7, 3);
        Optional<Duration> expectedJobDuration = Optional.of(Duration.ONE_MONTH);

        Job job = new Job(expectedJobStatus, expectedJobName, expectedJobLocation,
                expectedJobDescription, expectedJobPayment, expectedRequirements,
                expectedPostExpiry, expectedJobDuration);

        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION).add(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        Job actualJob = jobsDatabase.fetchJob(jobId).get();

        JobStatus actualJobStatus = actualJob.getJobStatus();
        Assert.assertEquals(expectedJobStatus, actualJobStatus);

        String actualJobName = actualJob.getJobName();
        Assert.assertEquals(expectedJobName, actualJobName);

        JobLocation actualJobLocation = actualJob.getJobLocation();
        Assert.assertEquals(expectedJobLocation, actualJobLocation);

        String actualJobDescription = actualJob.getJobDescription();
        Assert.assertEquals(expectedJobDescription, actualJobDescription);

        JobPayment actualJobPayment = actualJob.getJobPayment();
        Assert.assertEquals(expectedJobPayment, actualJobPayment);

        Collection<String> actualRequirements = actualJob.getRequirements();
        Assert.assertEquals(expectedRequirements, actualRequirements);

        LocalDate actualPostExpiry = actualJob.getJobExpiry();
        Assert.assertEquals(expectedPostExpiry, actualPostExpiry);

        Optional<Duration> actualJobDuration = actualJob.getJobDuration();
        Assert.assertEquals(expectedJobDuration, actualJobDuration);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }
}