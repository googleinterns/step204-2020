package com.google.job.servlets;

import com.google.job.data.JobsDatabase;

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

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {

            int pageSize = parsePageIndex(request);
            int pageIndex = parsePageIndex(request);
            
            JobPage jobPage = fetchJobPageDetails(pageSize, pageIndex);

            String json = ServletUtils.convertToJsonUsingGson(jobPage);
            response.setContentType("application/json;");
            response.getWriter().println(json);
        } catch(IllegalArgumentException | ServletException | ExecutionException | TimeoutException e) {
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
     * Returns the page size as an int.
     *
     * @param request From the GET request.
     * @return the page size.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    public static int parsePageSize(HttpServletRequest request) throws IllegalArgumentException {
        String pageSizeStr = ServletUtils.getStringParameter(request, PAGE_SIZE_PARAM, /* defaultValue= */ "");

        if (pageSizeStr.isEmpty()) {
            throw new IllegalArgumentException("page size param should not be empty");
        }

        try {
            return Integer.parseInt(pageSizeStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("page size param should be an int");
        }
    }

    /**
     * Returns the page index as an int.
     *
     * @param request From the GET request.
     * @return the page index.
     * @throws IllegalArgumentException if the page index is invalid.
     */
    public static int parsePageIndex(HttpServletRequest request) throws IllegalArgumentException {
        String pageIndexStr = ServletUtils.getStringParameter(request, PAGE_INDEX_PARAM, /* defaultValue= */ "");

        if (pageIndexStr.isEmpty()) {
            throw new IllegalArgumentException("page index param should not be empty");
        }

        try {
            return Integer.parseInt(pageIndexStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("page index param should be an int");
        }
    }
}
