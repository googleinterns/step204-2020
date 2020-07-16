package com.google.job.servlets;

import com.google.job.data.*;
import com.google.utils.ServletUtils;
import com.google.gson.Gson;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Range;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Time;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.stream.Collectors;

import java.util.*;
import static com.google.job.data.Requirement.*;

/**
 * Servlet that handles posting new job posts, updating existing job posts,
 * and getting the job listings.
 */
@WebServlet("/jobs")
public final class JobServlet extends HttpServlet {
    private static final String PATCH_METHOD_TYPE = "PATCH";
    private static final String JOB_ID_FIELD = "jobId";
    private static final long TIMEOUT = 5;

    public static final String SORT_BY_PARAM = "sortBy";
    public static final String ORDER_PARAM = "order";
    public static final String PAGE_SIZE_PARAM = "pageSize";
    public static final String PAGE_INDEX_PARAM = "pageIndex";

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

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            Filter sortBy = parseSortBy(request);
            Order order = parseOrder(request);
            int pageSize = parsePageSize(request);
            int pageIndex = parsePageIndex(request);

            JobPage jobPage = fetchJobPageDetails(sortBy, order, pageSize, pageIndex);

            String json = new Gson().toJson(jobPage);
            response.setContentType("application/json;");
            response.getWriter().println(json);
        } catch(IllegalArgumentException | ServletException | ExecutionException | TimeoutException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
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

    /**
     * Returns the JobPage object.
     *
     * @param sortBy The sorting of the list of jobs.
     * @param order The ordering of the sorting.
     * @param pageSize The number of job listings to be returned.
     * @param pageIndex The page which we are on (pagination).
     * @return JobPage object with all the details for the GET response.
     */
    private JobPage fetchJobPageDetails(Filter sortBy, Order order, int pageSize, int pageIndex)
            throws ServletException, ExecutionException, TimeoutException {
        try {
            return this.jobsDatabase.fetchJobPage(sortBy, order, pageSize, pageIndex)
                    .get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Returns the sorting as a Filter enum.
     *
     * @param request From the GET request.
     * @return Filter enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    private Filter parseSortBy(HttpServletRequest request) throws IllegalArgumentException {
        String sortById = (String) request.getParameter(SORT_BY_PARAM);

        if (sortById == null || sortById.isEmpty()) {
            throw new IllegalArgumentException("sort by param should not be null or empty");
        }

        return Filter.getFromId(sortById); // IAE may be thrown
    }

    /**
     * Returns the ordering as an Order enum.
     *
     * @param request From the GET request.
     * @return Order enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    private Order parseOrder(HttpServletRequest request) throws IllegalArgumentException {
        String orderId = (String) request.getParameter(ORDER_PARAM);

        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("order param should not be null or empty");
        }

        return Order.getFromId(orderId); // IAE may be thrown
    }

    /**
     * Returns the page size as an int.
     *
     * @param request From the GET request.
     * @return the page size.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    private int parsePageSize(HttpServletRequest request) throws IllegalArgumentException {
        String pageSizeStr = (String) request.getParameter(PAGE_SIZE_PARAM);

        if (pageSizeStr == null || pageSizeStr.isEmpty()) {
            throw new IllegalArgumentException("page size param should not be null or empty");
        }

        try {
            int pageSize =  Integer.parseInt(pageSizeStr);
            return pageSize;
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
    private int parsePageIndex(HttpServletRequest request) throws IllegalArgumentException {
        String pageIndexStr = (String) request.getParameter(PAGE_INDEX_PARAM);

        if (pageIndexStr == null || pageIndexStr.isEmpty()) {
            throw new IllegalArgumentException("page index param should not be null or empty");
        }

        try {
            int pageIndex =  Integer.parseInt(pageIndexStr);
            return pageIndex;
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("page index param should be an int");
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
            this.jobsDatabase.addJob(job).get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }

    /** Updates the target job post in the database. */
    private void updateJobPost(String jobId, Job job) throws ServletException, ExecutionException, TimeoutException {
        try {
            // Verifies if the current user can update the job post with this job id.
            // TODO(issue/25): incorporate the account stuff into job post.
            verifyUserCanUpdateJob(jobId);

            // Blocks the operation.
            // Use timeout in case it blocks forever.
            this.jobsDatabase.setJob(jobId, job).get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException e) {
            throw new ServletException(e);
        }
    }

    /** Verifies if it is a valid job id that this user can update. */
    // TODO(issue/25): incorporate the account stuff into job post.
    private void verifyUserCanUpdateJob(String jobId) throws ServletException, ExecutionException, TimeoutException {
        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Job Id should be an non-empty string");
        }

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
