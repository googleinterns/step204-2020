package com.google.job.servlets;

import com.google.job.data.JobStatus;
import com.google.job.data.JobsDatabase;
import com.google.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@WebServlet("/jobs/change-job-status")
public final class ChangeJobStatusServlet extends MyServlet {
    private static final String JOB_ID_FIELD = "jobId";
    private static final String JOB_STATUS_FIELD = "jobStatus";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doPatch(HttpServletRequest request, HttpServletResponse response) {
        try {
            // Gets the target job post id
            String jobId = getJobId(request, JOB_ID_FIELD, "");

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
        } catch (IOException | IllegalArgumentException | ServletException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private JobStatus getTargetStatus(HttpServletRequest request)
            throws IllegalArgumentException {
        JobStatus targetStatus = ServletUtils.getJobStatusParameter(request, JOB_STATUS_FIELD, /** defaultValue= */ null);

        if (targetStatus == null) {
            throw new IllegalArgumentException("Invalid Status Requested");
        }

        return targetStatus;
    }
}
