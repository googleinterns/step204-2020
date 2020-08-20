package com.google.account.business.data;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.common.collect.ImmutableList;
import com.google.job.data.Job;
import com.google.utils.FireStoreUtils;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

import static org.junit.Assert.*;

/** Tests for {@link BusinessDatabase} class. */
public final class BusinessDatabaseTest {
    // TODO(issue/15): Add failure test case

    private static final String TEST_BUSINESS_COLLECTION = "BusinessAccounts";
    private static final int BATCH_SIZE = 10;

    static BusinessDatabase businessDatabase;
    static Firestore firestore;

    @BeforeClass
    public static void setUp() throws IOException {
        businessDatabase = new BusinessDatabase();
        firestore = FireStoreUtils.getFireStore();
    }

    @Before
    public void clearCollection() {
        try {
            deleteCollection(firestore.collection(TEST_BUSINESS_COLLECTION), BATCH_SIZE);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error deleting collection : " + e.getMessage());
        }
    }

    @AfterClass
    public static void tearDownCollection() {
        try {
            deleteCollection(firestore.collection(TEST_BUSINESS_COLLECTION), BATCH_SIZE);
        } catch (ExecutionException | InterruptedException e) {
            System.err.println("Error tearing down collection : " + e.getMessage());
        }
    }
    
    @Test
    public void getBusinessAccount_normalInput_success() throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        String uid = "dummyBusinessUid";
        Business expectedBusiness = testBusinessAccountCreation();

        Future<WriteResult> future = FireStoreUtils.getFireStore()
                .collection(TEST_BUSINESS_COLLECTION)
                .document(uid)
                .set(expectedBusiness);

        // future.get() blocks on response.
        future.get();

        // Act.
        Optional<Business> businessOptional = this.businessDatabase.getBusinessAccount(uid).get();

        // Assert.
        assertTrue(businessOptional.isPresent());

        Business actualBusiness = businessOptional.get();

        assertEquals(expectedBusiness, actualBusiness);
    }

    @Test
    public void updateBusinessAccount_normalInput_success()
            throws IOException, ExecutionException, InterruptedException {
        // Arrange.
        String uid = "dummyBusinessUid";
        Business expectedBusiness = testBusinessAccountCreation();

        // Act.
        Future<WriteResult> future = this.businessDatabase.updateBusinessAccount(uid, expectedBusiness);

        // Assert.

        // future.get() blocks on response.
        future.get();

        DocumentReference documentReference = FireStoreUtils.getFireStore()
                .collection(TEST_BUSINESS_COLLECTION).document(uid);
        DocumentSnapshot documentSnapshot = documentReference.get().get();

        Business actualBusiness = documentSnapshot.toObject(Business.class);

        assertEquals(expectedBusiness, actualBusiness);
    }

    @Test
    public void updateJobsMade_normalInput_success() throws ExecutionException, InterruptedException, IOException {
        // Arrange.
        String jobId = "testJobId";

        String uid = "dummyBusinessUid";
        Business expectedBusiness = testBusinessAccountCreation();

        Future<WriteResult> addedJobFuture = FireStoreUtils.getFireStore()
                .collection(TEST_BUSINESS_COLLECTION)
                .document(uid)
                .set(expectedBusiness);

        // future.get() blocks on response.
        addedJobFuture.get();

        // Act.
        Future<Void> future = this.businessDatabase.updateJobsMade(uid, jobId);

        // Assert.
        // future.get() blocks on response.
        future.get();

        DocumentReference documentReference = FireStoreUtils.getFireStore()
                .collection(TEST_BUSINESS_COLLECTION).document(uid);
        DocumentSnapshot documentSnapshot = documentReference.get().get();

        Business actualBusiness = documentSnapshot.toObject(Business.class);

        List<String> expectedJobs = Arrays.asList("jobId1", "jobId2", jobId);

        List<String> actualJobs = actualBusiness.getJobs();

        assertEquals(expectedJobs, actualJobs);
    }

    @Test
    public void updateJobsMade_invalidUid_fail() throws IOException, InterruptedException {
        // Arrange.
        String jobId = "testJobId";

        String uid = "dummyBusinessUid";
        Business expectedBusiness = testBusinessAccountCreation();

        try {
            // Act.
            Future<Void> future = this.businessDatabase.updateJobsMade(uid, jobId);
            // future.get() blocks on response.
            future.get();

            fail();
        } catch (ExecutionException e) {
            // Assert.
            String expectedErrorMessage = "java.lang.IllegalArgumentException: Invalid uid";
            assertEquals(expectedErrorMessage, e.getMessage());
        }
    }

    /** Creates Business object for tests. */
    private Business testBusinessAccountCreation() {
        String businessName = "testBusinessAccount";
        List<String> jobs = ImmutableList.of("jobId1", "jobId2");
        Business expectedBusiness = Business.newBuilder().setName(businessName).setJobs(jobs).build();

        return expectedBusiness;
    }

    /**
     * Delete a collection in batches to avoid out-of-memory errors.
     *
     * Batch size may be tuned based on document size (at most 1MB) and application requirements.
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
