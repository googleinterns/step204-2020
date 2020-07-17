package com.google.job.data;

import com.google.api.core.ApiFunction;
import com.google.api.core.ApiFuture;
import com.google.api.core.ApiFutures;
import com.google.cloud.firestore.*;
import com.google.cloud.firestore.Query.Direction;
import com.google.common.util.concurrent.MoreExecutors;
import com.google.utils.FireStoreUtils;
import org.checkerframework.checker.nullness.compatqual.NullableDecl;
import org.apache.commons.lang3.Range;

import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.List;
import java.util.LinkedList;
import java.io.IOException;

import java.util.logging.Logger;

/** Helps persist and retrieve job posts. */
public final class JobsDatabase {
    private static final String JOB_COLLECTION = "Jobs";
    private static final String SALARY_SORT = "jobPay.annualMax";
    private static final String REGION_FILTER = "jobLocation.region";

    /**
     * Adds a newly created job post.
     *
     * @param newJob Newly created job post. Assumes that it is non-nullable.
     * @return A future of the detailed information of the writing.
     */
    public Future<WriteResult> addJob(Job newJob) {
        // Add document data after generating an id.
        DocumentReference addedDocRef = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document();

        String jobId = addedDocRef.getId();

        // Updates the Job with cloud firestore id
        // Status is already set when parsing the job post
        Job job = newJob.toBuilder()
                .setJobId(jobId)
                .build();

        return addedDocRef.set(job);
    }

    /**
     * Edits the job post.
     *
     * @param jobId Id for the target job post in the database.
     * @param updatedJob Updated job post.
     * @return A future of the detailed information of the update.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<WriteResult> setJob(String jobId, Job updatedJob) throws IllegalArgumentException {
        // Sets the Job with cloud firestore id and ACTIVE status
        Job job = updatedJob.toBuilder()
                .setJobId(jobId)
                .build();

        // Overwrites the whole job post
        return FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId)
                .set(job);
    }

    /**
     * Fetches the snapshot future of a specific job post.
     *
     * @param jobId Id for the job post in the database.
     * @return Future of the target job post.
     * @throws IllegalArgumentException If the job id is invalid.
     */
    public Future<Optional<Job>> fetchJob(String jobId) throws IllegalArgumentException {
        DocumentReference docRef = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId);

        // Asynchronously retrieves the document
        ApiFuture<DocumentSnapshot> snapshotFuture = docRef.get();

        ApiFunction<DocumentSnapshot, Optional<Job>> jobFunction = new ApiFunction<DocumentSnapshot, Optional<Job>>() {
            @NullableDecl
            public Optional<Job> apply(@NullableDecl DocumentSnapshot documentSnapshot) {
                return FireStoreUtils.convertDocumentSnapshotToPOJO(documentSnapshot, Job.class);
            }
        };

        return ApiFutures.transform(snapshotFuture, jobFunction, MoreExecutors.directExecutor());
    }

    /**
     * Returns a future of boolean to check if the job matching the given id is valid.
     * @throws IllegalArgumentException If the input jobId is empty.
     */
    public static Future<Boolean> hasJob(String jobId) throws IllegalArgumentException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Empty Job Id");
        }

        ApiFuture<DocumentSnapshot> snapshotFuture = FireStoreUtils.getFireStore()
                .collection(JOB_COLLECTION).document(jobId).get();

        return ApiFutures.transform(snapshotFuture,
                documentSnapshot -> documentSnapshot.exists(),
                MoreExecutors.directExecutor());
    }

    private static final Logger log = Logger.getLogger(JobsDatabase.class.getName());

    /**
     * Gets all the jobs given the params from the database.
     * Currently, they can only be sorted by salary.
     *
     * @param region The region in Singapore.
     * @param sortBy The sorting of the list of jobs.
     * @param order The ordering of the sorting.
     * @param pageSize The number of job listings to be returned.
     * @param pageIndex The page which we are on (pagination).
     * @return Future of the JobPage object.
     */
    public static Future<JobPage> fetchJobPage(SingaporeRegion region, Filter sortBy, Order order,
        int pageSize, int pageIndex) throws IOException {
        CollectionReference jobsCollection = FireStoreUtils.getFireStore().collection(JOB_COLLECTION);

        Query query;

        if (region.equals(SingaporeRegion.ENTIRE)) {
            query = jobsCollection.orderBy(SALARY_SORT, Order.getQueryDirection(order));
        } else {
            query = jobsCollection.whereEqualTo(REGION_FILTER, region.name());
                // .orderBy(SALARY_SORT, Order.getQueryDirection(order));
        }

        // TODO(issue/xx): add the query to include pagination

        ApiFuture<QuerySnapshot> future = query.get();

        ApiFunction<QuerySnapshot, JobPage> jobFunction = new ApiFunction<QuerySnapshot, JobPage>() {
            @NullableDecl
            public JobPage apply(@NullableDecl QuerySnapshot future) {
                List<QueryDocumentSnapshot> documents = future.getDocuments();
                List<Job> jobList = new LinkedList<>();

                for (QueryDocumentSnapshot document : documents) {
                    Job job = document.toObject(Job.class);
                    jobList.add(job);
                }

                long totalCount = documents.size();
                // TODO(issue/xx): implement pagination
                Range<Integer> range = Range.between(Math.max(0, documents.size()), documents.size());

                JobPage jobPage = new JobPage(jobList, totalCount, range);
                log.info(jobPage.toString());

                return jobPage;
            }
        };

        return ApiFutures.transform(future, jobFunction, MoreExecutors.directExecutor());
    }
}
