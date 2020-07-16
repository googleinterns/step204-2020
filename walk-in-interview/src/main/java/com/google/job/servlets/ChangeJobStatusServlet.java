package com.google.job.servlets;

import com.google.job.data.JobStatus;
import com.google.job.data.JobsDatabase;
import com.google.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Servlet that handles changing status of the existing job posts. */
@WebServlet("/jobs/change-job-status")
public final class ChangeJobStatusServlet extends HttpServlet {
    private static final String PATCH_METHOD_TYPE = "PATCH";
    private static final String JOB_ID_FIELD = "jobId";
    private static final String JOB_STATUS_FIELD = "jobStatus";
    private static final long TIMEOUT = 5;

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

    /** Handles the PATCH request from client. */
    public void doPatch(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets the target job post id
            String jobId = ServletUtils.getStringParameter(request, JOB_ID_FIELD, /* defaultValue= */ "");

            // Gets the target status
            JobStatus jobStatus = getTargetStatus(request);

            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            verifyUserCanUpdateJob(jobId);

            // Change the status
            switch (jobStatus) {
                // TODO(issue/28): deal with expired job post/re-activate job post
                case ACTIVE:
                case EXPIRED:
                    throw new UnsupportedOperationException("Not implemented");
                case DELETED:
                    this.jobsDatabase.markJobPostAsDeleted(jobId);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException | ServletException | ExecutionException | TimeoutException e) {
            // TODO(issue/47): use custom exceptions
            System.err.println("Error occur: " + e.getCause());
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Gets the updated status from the client. */
    private JobStatus getTargetStatus(HttpServletRequest request) throws IllegalArgumentException {
        JobStatus targetStatus = ServletUtils.getJobStatusParameter(request, JOB_STATUS_FIELD, /** defaultValue= */ null);

        if (targetStatus == null) {
            throw new IllegalArgumentException("Invalid Status Requested");
        }

        return targetStatus;
    }

    /**
     * Verifies if it is a valid job id that this user can update.
     *
     * @throws IllegalArgumentException If there is no such id in database.
     */
    // TODO(issue/25): incorporate the account stuff into job post.
    private void verifyUserCanUpdateJob(String jobId) throws
            IllegalArgumentException ,ServletException, ExecutionException, TimeoutException {
        try {
            // Use timeout in case it blocks forever.
            boolean hasJob = JobsDatabase.hasJob(jobId).get(TIMEOUT, TimeUnit.SECONDS);
            if (!hasJob) {
                throw new IllegalArgumentException("Invalid Job Id");
            }
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }
}
