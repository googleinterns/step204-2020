package com.google.job.servlets;

import com.google.job.data.Job;
import com.google.job.data.JobStatus;
import com.google.job.data.JobsDatabase;
import com.google.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

/** Servlet that handles updating an existing job posts. */
@WebServlet("/jobs")
public final class UpdateJobServlet extends MyServlet {
    private static final String JOB_ID_FIELD = "jobId";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doPatch(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        try {
            // Gets the target job post id from the form
            String jobId = getJobId(request);

            // Gets job post from the form
            Job job = parseJobPost(request);

            // Stores job post into the database
            storeJobPost(jobId, job);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | IllegalArgumentException | ServletException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getJobId(HttpServletRequest request) throws ServletException, IOException {
        String jobId = ServletUtils.getStringParameter(request, JOB_ID_FIELD, /* defaultValue= */ "");

        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }

        try {
            if (!JobsDatabase.isJobIdExist(jobId)) {
                throw new IllegalArgumentException("Invalid Job Id");
            }

            return jobId;
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            String errorMessage = "Error occur: " + e.getCause();
            System.err.println(errorMessage);
            throw new IOException(errorMessage);
        }
    }

    /** Parses into Job object from json received from client. */
    private Job parseJobPost(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job object from the POST request
        BufferedReader bufferedReader = request.getReader();
        String jobPostJsonStr = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

        if (StringUtils.isBlank(jobPostJsonStr)) {
            throw new IllegalArgumentException("Json for Job object is Empty");
        }

        Job rawJob = ServletUtils.parseFromJsonUsingGson(jobPostJsonStr, Job.class);

        Job job = Job.newBuilder()
                .setJobStatus(rawJob.getJobStatus())
                .setJobTitle(rawJob.getJobTitle())
                .setLocation(rawJob.getJobLocation())
                .setJobDescription(rawJob.getJobDescription())
                .setJobPay(rawJob.getJobPay())
                .setRequirements(rawJob.getRequirements())
                .setPostExpiry(rawJob.getPostExpiryTimestamp())
                .setJobDuration(rawJob.getJobDuration())
                .build();

        return job;
    }

    private void storeJobPost(String jobId, Job job) throws ServletException, IOException {
        try {
            // Synchronizes and blocks the operation.
            this.jobsDatabase.setJob(jobId, job).get();
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            String errorMessage = "Error occur: " + e.getCause();
            System.err.println(errorMessage);
            throw new IOException(errorMessage);
        }
    }
}
