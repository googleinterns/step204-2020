package com.google.job.servlets;

import com.google.job.data.*;
import com.google.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import java.sql.Time;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.*;

/**
 * Servlet that handles getting the job listings given the filters.
 */
@WebServlet("/jobs/listings")
public final class JobsListingsServlet extends HttpServlet {
    private static final long TIMEOUT_SECONDS = 5;

    private static final String MIN_LIMIT_PARAM = "minLimit";
    private static final String MAX_LIMIT_PARAM = "maxLimit";
    private static final String REGION_PARAM = "region";
    private static final String SORT_BY_PARAM = "sortBy";
    private static final String ORDER_PARAM = "order";
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
            
            JobQuery jobQuery = parseJobQuery(request);

            JobPage jobPage = fetchJobPageDetails(jobQuery);

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
     * @param jobQuery The job query object with all the filtering/sorting params.
     * @return JobPage object with all the details for the GET response.
     */
    private JobPage fetchJobPageDetails(JobQuery jobQuery) throws ServletException, ExecutionException, TimeoutException {
        try {
            return this.jobsDatabase.fetchJobPage(jobQuery)
                    .get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Returns the job query object.
     *
     * @param request From the GET request.
     * @return the job query object.
     * @throws IllegalArgumentException if one of the params is invalid.
     */
    public static JobQuery parseJobQuery(HttpServletRequest request) throws IllegalArgumentException {
        int minLimit = parseMinLimit(request);
        int maxLimit = parseMaxLimit(request);
        SingaporeRegion region = parseRegion(request);
        Filter sortBy = parseSortBy(request);
        Order order = parseOrder(request);
        // TODO(issue/34): parse page size and index once pagination is implemented
        
        return new JobQuery()
                        .setMinLimit(minLimit)
                        .setMaxLimit(maxLimit)
                        .setRegion(region)
                        .setSortBy(sortBy)
                        .setOrder(order);
    }

    /**
     * Returns the lower limit as an int.
     *
     * @param request From the GET request.
     * @return the lower limit.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    public static int parseMinLimit(HttpServletRequest request) throws IllegalArgumentException {
        String minLimitStr = ServletUtils.getStringParameter(request, MIN_LIMIT_PARAM, /* defaultValue= */ "");

        if (minLimitStr.isEmpty()) {
            throw new IllegalArgumentException("min limit param should not be empty");
        }

        try {
            return Integer.parseInt(minLimitStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("min limit param should be an int");
        }
    }

    /**
     * Returns the upper limit as an int.
     *
     * @param request From the GET request.
     * @return the upper limit.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    public static int parseMaxLimit(HttpServletRequest request) throws IllegalArgumentException {
        String maxLimitStr = ServletUtils.getStringParameter(request, MAX_LIMIT_PARAM, /* defaultValue= */ "");

        if (maxLimitStr.isEmpty()) {
            throw new IllegalArgumentException("max limit param should not be empty");
        }

        try {
            return Integer.parseInt(maxLimitStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("max limit param should be an int");
        }
    }

    /**
     * Returns the region as a SingaporeRegion enum. We assume ENTIRE is region param is empty.
     *
     * @param request From the GET request.
     * @return SingaporeRegion enum given the id in the request params.
     */
    private static SingaporeRegion parseRegion(HttpServletRequest request) {
        String region = ServletUtils.getStringParameter(request, REGION_PARAM, /* defaultValue= */ "");

        if (region.isEmpty()) {
            return SingaporeRegion.ENTIRE;
        }

        return SingaporeRegion.getFromId(region); // IllegalArgumentException may be thrown
    }

    /**
     * Returns the sorting as a Filter enum.
     *
     * @param request From the GET request.
     * @return Filter enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    public static Filter parseSortBy(HttpServletRequest request) throws IllegalArgumentException {
        String sortById = ServletUtils.getStringParameter(request, SORT_BY_PARAM, /* defaultValue= */ "");

        if (sortById.isEmpty()) {
            throw new IllegalArgumentException("sort by param should not be empty");
        }

        return Filter.getFromId(sortById); // IllegalArgumentException may be thrown
    }

    /**
     * Returns the ordering as an Order enum.
     *
     * @param request From the GET request.
     * @return Order enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    public static Order parseOrder(HttpServletRequest request) throws IllegalArgumentException {
        String orderId = ServletUtils.getStringParameter(request, ORDER_PARAM, /* defaultValue= */ "");

        if (orderId.isEmpty()) {
            throw new IllegalArgumentException("order param should not be empty");
        }

        return Order.getFromId(orderId); // IllegalArgumentException may be thrown
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
