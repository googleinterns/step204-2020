package com.google.job.data;

import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.QueryDocumentSnapshot;
import com.google.firestore.FireStoreUtils;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

public final class JobsDatabaseTest {

    private static final String JOB_COLLECTION = "Jobs";

    Firestore firestore;
    JobsDatabase jobsDatabase;

    public JobsDatabaseTest() throws IOException {
        firestore = FireStoreUtils.getFireStore();
        jobsDatabase = new JobsDatabase(firestore);
    }

    @Test
    public void addJob_NormalInput_success() throws ExecutionException, InterruptedException {
        String expectedJobName = "Noogler";
        Job job = new Job(expectedJobName);
        jobsDatabase.addJob(job);

        List<QueryDocumentSnapshot> documentSnapshots = firestore.collection(JOB_COLLECTION).get().get().getDocuments();
        Job actualJob = documentSnapshots.get(0).toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void editJob_NormalInput_success() throws ExecutionException, InterruptedException {
        String expectedJobName = "Googler";
        Job job = new Job(expectedJobName);

        List<QueryDocumentSnapshot> documentSnapshots = firestore.collection(JOB_COLLECTION).get().get().getDocuments();
        String jobId = documentSnapshots.get(0).getId();

        jobsDatabase.editJob(jobId, job);

        documentSnapshots = firestore.collection(JOB_COLLECTION).get().get().getDocuments();
        Job actualJob = documentSnapshots.get(0).toObject(Job.class);

        String actualJobName = actualJob.getJobName();

        Assert.assertEquals(expectedJobName, actualJobName);
    }

    @Test
    public void fetchJob_NormalInput_success() throws ExecutionException, InterruptedException {
        List<QueryDocumentSnapshot> documentSnapshots = firestore.collection(JOB_COLLECTION).get().get().getDocuments();
        String jobId = documentSnapshots.get(0).getId();

        Job job = documentSnapshots.get(0).toObject(Job.class);

        String actualJobName = job.getJobName();
        String expectedJobName = "Googler";

        Assert.assertEquals(expectedJobName, actualJobName);
    }
}