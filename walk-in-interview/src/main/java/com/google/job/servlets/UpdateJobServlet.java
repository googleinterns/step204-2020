package com.google.job.servlets;

import com.google.cloud.firestore.DocumentReference;
import com.google.job.data.Job;
import com.google.job.data.JobsDatabase;
import com.google.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

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
            // Gets the target job post id
            String jobId = getJobId(request);

            // Gets job post from the form
            Job job = ServletUtils.parseJobPost(request);

            // Stores job post into the database
            storeJobPost(jobId, job);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException | IllegalArgumentException | ServletException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private String getJobId(HttpServletRequest request)
            throws ServletException, IOException {
        String jobId = ServletUtils.getStringParameter(request, JOB_ID_FIELD, /* defaultValue= */ "");

        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Empty Job Id");
        }

        try {
            if (JobsDatabase.isJobIdExist(jobId)) {
                return jobId;
            }

            throw new IllegalArgumentException("Invalid Job Id");
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
            throw new IOException("Error occur");
        }
    }

    private void storeJobPost(String jobId, Job job) throws ServletException {

        try {
            // Synchronizes and blocks the operation.
            this.jobsDatabase.setJob(jobId, job).get();
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
        }
    }
}
