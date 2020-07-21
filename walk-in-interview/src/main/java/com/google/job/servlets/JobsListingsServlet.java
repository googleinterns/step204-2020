package com.google.job.servlets;

import com.google.job.data.*;
import com.google.gson.Gson;

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
    private static final long TIMEOUT = 5;

    public static final String MIN_LIMIT_PARAM = "minLimit";
    public static final String MAX_LIMIT_PARAM = "maxLimit";
    public static final String REGION_PARAM = "region";
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
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            
            JobQuery jobQuery = parseJobQuery(request);

            JobPage jobPage = fetchJobPageDetails(jobQuery);

            String json = new Gson().toJson(jobPage);
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
                    .get(TIMEOUT, TimeUnit.SECONDS);
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

        JobQuery jobQuery = new JobQuery().toBuilder().setMinLimit(minLimit).setMaxLimit(maxLimit).setRegion(region)
            .setSortBy(sortBy).setOrder(order).build();
        
        return jobQuery;
    }

    /**
     * Returns the lower limit as an int.
     *
     * @param request From the GET request.
     * @return the lower limit.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    public static int parseMinLimit(HttpServletRequest request) throws IllegalArgumentException {
        String minLimitStr = (String) request.getParameter(MIN_LIMIT_PARAM);

        if (minLimitStr == null || minLimitStr.isEmpty()) {
            throw new IllegalArgumentException("min limit param should not be null or empty");
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
        String maxLimitStr = (String) request.getParameter(MAX_LIMIT_PARAM);

        if (maxLimitStr == null || maxLimitStr.isEmpty()) {
            throw new IllegalArgumentException("max limit param should not be null or empty");
        }

        try {
            return Integer.parseInt(maxLimitStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("max limit param should be an int");
        }
    }

    /**
     * Returns the region as a SingaporeRegion enum.
     *
     * @param request From the GET request.
     * @return SingaporeRegion enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    private static SingaporeRegion parseRegion(HttpServletRequest request) throws IllegalArgumentException {
        String region = (String) request.getParameter(REGION_PARAM);

        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("region param should not be null or empty");
        }

        return SingaporeRegion.getFromId(region); // Illegal Argument Exception may be thrown
    }

    /**
     * Returns the sorting as a Filter enum.
     *
     * @param request From the GET request.
     * @return Filter enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    public static Filter parseSortBy(HttpServletRequest request) throws IllegalArgumentException {
        String sortById = (String) request.getParameter(SORT_BY_PARAM);

        if (sortById == null || sortById.isEmpty()) {
            throw new IllegalArgumentException("sort by param should not be null or empty");
        }

        return Filter.getFromId(sortById); // Illegal Argument Exception may be thrown
    }

    /**
     * Returns the ordering as an Order enum.
     *
     * @param request From the GET request.
     * @return Order enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    public static Order parseOrder(HttpServletRequest request) throws IllegalArgumentException {
        String orderId = (String) request.getParameter(ORDER_PARAM);

        if (orderId == null || orderId.isEmpty()) {
            throw new IllegalArgumentException("order param should not be null or empty");
        }

        return Order.getFromId(orderId); // Illegal Argument Exception may be thrown
    }

    /**
     * Returns the page size as an int.
     *
     * @param request From the GET request.
     * @return the page size.
     * @throws IllegalArgumentException if the page size is invalid.
     */
    public static int parsePageSize(HttpServletRequest request) throws IllegalArgumentException {
        String pageSizeStr = (String) request.getParameter(PAGE_SIZE_PARAM);

        if (pageSizeStr == null || pageSizeStr.isEmpty()) {
            throw new IllegalArgumentException("page size param should not be null or empty");
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
        String pageIndexStr = (String) request.getParameter(PAGE_INDEX_PARAM);

        if (pageIndexStr == null || pageIndexStr.isEmpty()) {
            throw new IllegalArgumentException("page index param should not be null or empty");
        }

        try {
            return Integer.parseInt(pageIndexStr);
        } catch(NumberFormatException e) {
            throw new IllegalArgumentException("page index param should be an int");
        }
    }
}
