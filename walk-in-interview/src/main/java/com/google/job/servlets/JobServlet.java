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
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

/**
 * Servlet that handles posting new job posts, updating existing job posts,
 * and getting an individual job post.
 */
@WebServlet("/jobs")
public final class JobServlet extends HttpServlet {
    private static final String PATCH_METHOD_TYPE = "PATCH";
    private static final String JOB_ID_FIELD = "jobId";
    private static final long TIMEOUT_SECONDS = 5;

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Explicitly routes PATCH requests to a doPatch method since by default HttpServlet doesn't do it for us.
        if (request.getMethod().equalsIgnoreCase(PATCH_METHOD_TYPE)){
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // TODO(issue/60): get individual job post from jobId
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets job post from the form
            Job rawJob = parseRawJobPost(request);

            // New jobs always start in ACTIVE status.
            Job job = rawJob.toBuilder().setJobStatus(JobStatus.ACTIVE).build();

            // Stores job post into the database
            storeJobPost(job);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException | IllegalArgumentException | ServletException | IOException | TimeoutException e) {
            // TODO(issue/47): use custom exceptions
            System.err.println("Error occur: " + e.getCause());
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Handles the PATCH request from client. */
    public void doPatch(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets the target job post id from the form
            String jobId = ServletUtils.getStringParameter(request, JOB_ID_FIELD, /* defaultValue= */ "");

            // Gets job post from the form
            Job updatedJob = parseRawJobPost(request);

            // Stores job post into the database
            updateJobPost(jobId, updatedJob);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException | IllegalArgumentException | ServletException | IOException | TimeoutException e) {
            // TODO(issue/47): use custom exceptions
            System.err.println("Error occur: " + e.getCause());
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Parses into valid Job object from json received from client. */
    private Job parseRawJobPost(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job object from the POST request
        try (BufferedReader bufferedReader = request.getReader()) {
            String jobPostJsonStr = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

            if (StringUtils.isBlank(jobPostJsonStr)) {
                throw new IllegalArgumentException("Json for Job object is Empty");
            }

            Job rawJob = ServletUtils.parseFromJsonUsingGson(jobPostJsonStr, Job.class);

            // Validates the attributes via build()
            return rawJob.toBuilder().build();
        }
    }

    /** Stores the job post into the database. */
    private void storeJobPost(Job job) throws ServletException, ExecutionException, TimeoutException {
        try {
            // Blocks the operation.
            // Use timeout in case it blocks forever.
            this.jobsDatabase.addJob(job).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }

    /** Updates the target job post in the database. */
    private void updateJobPost(String jobId, Job job)
            throws IllegalArgumentException, ServletException, ExecutionException, TimeoutException {
        try {
            // Blocks the operation.
            // Use timeout in case it blocks forever.
            this.jobsDatabase.setJob(jobId, job).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }
}
