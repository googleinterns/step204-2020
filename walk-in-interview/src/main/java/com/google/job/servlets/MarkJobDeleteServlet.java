package com.google.job.servlets;

import com.google.job.data.JobsDatabase;
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

/** Servlet that handles changing status of the existing job posts to DELETED. */
@WebServlet("/jobs/delete")
public final class MarkJobDeleteServlet extends HttpServlet {
    private static final String PATCH_METHOD_TYPE = "PATCH";
    private static final long TIMEOUT_SECONDS = 5;

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
            String jobId = getJobId(request);

            // Changes the status to DELETED
            this.jobsDatabase.markJobPostAsDeleted(jobId).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IllegalArgumentException | IOException
                | InterruptedException | ExecutionException | TimeoutException e) {
            // TODO(issue/47): use custom exceptions
            System.err.println("Error occur: " + e.getCause());
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Gets jobId received from client. */
    private String getJobId(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job Id from the PATCH request body
        try (BufferedReader bufferedReader = request.getReader()) {
            String jobId = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

            if (StringUtils.isBlank(jobId)) {
                throw new IllegalArgumentException("jobId received is Empty");
            }

            return jobId;
        }
    }
}
