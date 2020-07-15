package com.google.job.servlets;

import com.google.job.data.JobStatus;
import com.google.job.data.JobsDatabase;
import com.google.utils.FireStoreUtils;
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
        // Explicitly routes PATCH requests to a doPatch method since by default HttpServlet doesn't do it for us
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

            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            FireStoreUtils.verifyUserCanUpdateJob(jobId);

            // Gets the target status
            JobStatus jobStatus = getTargetStatus(request);

            // Change the status
            switch (jobStatus) {
                case ACTIVE:
                case EXPIRED:
                    throw new UnsupportedOperationException("Not implemented");
                case DELETED:
                    this.jobsDatabase.markJobPostAsDeleted(jobId);
            }
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException | ServletException | ExecutionException | TimeoutException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

//    /** Verifies if it is a valid job id that this user can update. */
//    // TODO(issue/25): incorporate the account stuff into job post.
//    private void verifyUserCanUpdateJob(String jobId) throws ServletException, ExecutionException, TimeoutException {
//        if (jobId.isEmpty()) {
//            throw new IllegalArgumentException("Job Id should be an non-empty string");
//        }
//
//        try {
//            // Use timeout in case it blocks forever.
//            boolean hasJob = JobsDatabase.hasJob(jobId).get(TIMEOUT, TimeUnit.SECONDS);
//            if (!hasJob) {
//                throw new IllegalArgumentException("Invalid Job Id");
//            }
//        } catch (InterruptedException e) {
//            throw new ServletException(e);
//        }
//    }

    private JobStatus getTargetStatus(HttpServletRequest request) throws IllegalArgumentException {
        JobStatus targetStatus = ServletUtils.getJobStatusParameter(request, JOB_STATUS_FIELD, /** defaultValue= */ null);

        if (targetStatus == null) {
            throw new IllegalArgumentException("Invalid Status Requested");
        }

        return targetStatus;
    }
}
