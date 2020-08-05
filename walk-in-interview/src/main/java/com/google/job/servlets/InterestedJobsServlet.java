package com.google.job.servlets;

import com.google.job.data.*;
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

/** Servlet that handles adding/removing and getting an applicant's interested jobs. */
@WebServlet("/my-interested-list")
public final class InterestedJobsServlet extends HttpServlet {
    private static final long TIMEOUT_SECONDS = 5;

    private static final String PAGE_SIZE_PARAM = "pageSize";
    private static final String PAGE_INDEX_PARAM = "pageIndex";
    private static final String INTERESTED_PARAM = "interested";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            int pageSize = JobsListingsServlet.parsePageIndex(request);
            int pageIndex = JobsListingsServlet.parsePageIndex(request);
            
            JobPage jobPage = fetchJobPageDetails(pageSize, pageIndex);

            String json = ServletUtils.convertToJsonUsingGson(jobPage);
            response.setContentType("application/json;");
            response.getWriter().println(json);
        } catch(IllegalArgumentException | ServletException | ExecutionException | TimeoutException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {

            String jobId = JobServlet.parseJobId(request);
            // true if the applicant is already interested (they want to remove it now)
            boolean interested = parseInterested(request);

            updateInterestedList(jobId, interested);

            response.setStatus(HttpServletResponse.SC_OK);
        } catch (ExecutionException | IllegalArgumentException | ServletException | TimeoutException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /**
     * Returns the JobPage object.
     *
     * @param pageSize The the number of jobs to be shown on the page.
     * @param pageIndex The page number on which we are at.
     * @return JobPage object with all the details for the GET response.
     */
    private JobPage fetchJobPageDetails(int pageSize, int pageIndex) throws ServletException, ExecutionException, TimeoutException {
        try {
            return this.jobsDatabase.fetchInterestedJobPage(pageSize, pageIndex)
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Updates the applicant's interested list to add or remove the job.
     *
     * @param jobId The job id.
     * @param interested Whether the applicant is currently interested in it or not.
     */
    private void updateInterestedList(String jobId, boolean interested) throws ServletException, ExecutionException, TimeoutException {
        try {
            this.jobsDatabase.updateInterestedJobsList(jobId, interested)
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    /**
     * Returns whether the applicant was already interested in the job.
     *
     * @param request From the POST request.
     * @return interested.
     * @throws IllegalArgumentException if the interested param doesn't exist.
     */
    public static boolean parseInterested(HttpServletRequest request) throws IllegalArgumentException {
        String interestedStr = ServletUtils.getStringParameter(request, INTERESTED_PARAM, /* defaultValue= */ "");

        if (interestedStr.isEmpty()) {
            throw new IllegalArgumentException("interested param should not be empty");
        }

        if (!(interestedStr.equalsIgnoreCase("true") || interestedStr.equalsIgnoreCase("false"))) {
            throw new IllegalArgumentException("interested param should be either true or false");
        }

        return Boolean.parseBoolean(interestedStr);
    }
}
