package com.google.job.servlets;

import com.google.job.data.*;
import com.google.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/** Servlet that handles posting new job posts and updating existing job posts. */
@WebServlet("/jobs")
public final class JobServlet extends HttpServlet {
    private static final String PATCH = "PATCH";
    private static final String JOB_ID_FIELD = "jobId";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase(PATCH)){
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets job post from the form
            Job rawJob = parseRawJobPost(request);

            // Sets the status to be ACTIVE and validates the attributes via build().
            Job job = rawJob.toBuilder().setJobStatus(JobStatus.ACTIVE).build();

            // Stores job post into the database
            storeJobPost(job);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException | IllegalArgumentException | ServletException | IOException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Handles the PATCH request from client. */
    public void doPatch(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets the target job post id from the form
            String jobId = ServletUtils.getStringParameter(request, JOB_ID_FIELD, /* defaultValue= */ "");

            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            verifyUserCanUpdateJob(jobId);

            // Gets raw job post from the form
            Job rawJob = parseRawJobPost(request);

            // Validates the attributes via build()
            Job updatedJob = rawJob.toBuilder().build();

            // Stores job post into the database
            updateJobPost(jobId, updatedJob);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException | IllegalArgumentException | ServletException | IOException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Parses into raw Job object from json received from client. */
    private Job parseRawJobPost(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job object from the POST request
        BufferedReader bufferedReader = request.getReader();
        String jobPostJsonStr = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

        if (StringUtils.isBlank(jobPostJsonStr)) {
            throw new IllegalArgumentException("Json for Job object is Empty");
        }

        Job rawJob = ServletUtils.parseFromJsonUsingGson(jobPostJsonStr, Job.class);

        return rawJob;
    }

    /** Stores the job post into the database. */
    private void storeJobPost(Job job) throws ServletException, ExecutionException {
        try {
            // Synchronizes and blocks the operation.
            this.jobsDatabase.addJob(job).get();
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
            throw e;
        }
    }

    /** Verifies if it is a valid job id that this user can update. */
    // TODO(issue/25): incorporate the account stuff into job post.
    private void verifyUserCanUpdateJob(String jobId) throws ServletException, ExecutionException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }

        try {
            if (!JobsDatabase.hasJobId(jobId)) {
                throw new IllegalArgumentException("Invalid Job Id");
            }
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
            throw e;
        }
    }

    /** Updates the target job post in the database. */
    private void updateJobPost(String jobId, Job job) throws ServletException, ExecutionException {
        try {
            // Synchronizes and blocks the operation.
            this.jobsDatabase.setJob(jobId, job).get();
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
            throw e;
        }
    }
}