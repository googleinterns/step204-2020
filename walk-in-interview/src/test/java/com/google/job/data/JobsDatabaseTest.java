package com.google.job.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firestore.FireStoreUtils;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public final class JobsDatabaseTest {

    private static final String TEST_JOB_COLLECTION = "Jobs";
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
        String expectedJobName = "Software Engineer";
        Job job = new Job(expectedJobName);
        Future<DocumentReference> addedJobFuture = jobsDatabase.addJob(job);

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        Job actualJob = document.toObject(Job.class);
        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }

    @Test
    public void editJob_NormalInput_success() throws ExecutionException, InterruptedException {
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION)
                .add(new Job("Noogler"));

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        String expectedJobName = "Googler";
        Job job = new Job(expectedJobName);
        Future<WriteResult> edittedDocRef = jobsDatabase.editJob(jobId, job);
        // future.get() blocks on response.
        edittedDocRef.get();

        ApiFuture<DocumentSnapshot> documentSnapshotApiFuture = firestore.collection(TEST_JOB_COLLECTION).document(jobId).get();
        DocumentSnapshot documentSnapshot = documentSnapshotApiFuture.get();
        Job actualJob = documentSnapshot.toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        Future<DocumentReference> addedJobFuture = firestore.collection(TEST_JOB_COLLECTION)
                .add(new Job("Programmer"));

        DocumentReference documentReference = addedJobFuture.get();
        // Asynchronously retrieve the document.
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        // future.get() blocks on response.
        DocumentSnapshot document = future.get();
        String jobId = document.getId();

        Job job = jobsDatabase.fetchJob(jobId).get();

        String actualJobName = job.getJobName();
        String expectedJobName = "Programmer";

        Assert.assertEquals(expectedJobName, actualJobName);

        firestore.collection(TEST_JOB_COLLECTION).document(jobId).delete();
    }
}