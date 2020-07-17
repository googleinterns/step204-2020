package com.google.job.servlets;

import com.google.job.data.*;
import com.google.job.servlets.JobServlet;
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
 * Servlet that handles getting the job listings by region.
 */
@WebServlet("/jobs/by-region")
public final class JobByRegionServlet extends HttpServlet {
    private static final long TIMEOUT = 5;

    public static final String REGION_PARAM = "region";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            SingaporeRegion region = parseRegion(request);
            Filter sortBy = JobServlet.parseSortBy(request);
            Order order = JobServlet.parseOrder(request);
            int pageSize = JobServlet.parsePageSize(request);
            int pageIndex = JobServlet.parsePageIndex(request);

            JobPage jobPage = fetchJobPageDetails(region, sortBy, order, pageSize, pageIndex);

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
     * @param region The region in Singapore.
     * @param sortBy The sorting of the list of jobs.
     * @param order The ordering of the sorting.
     * @param pageSize The number of job listings to be returned.
     * @param pageIndex The page which we are on (pagination).
     * @return JobPage object with all the details for the GET response.
     */
    private JobPage fetchJobPageDetails(SingaporeRegion region, Filter sortBy, Order order,
        int pageSize, int pageIndex) throws ServletException, ExecutionException, TimeoutException {
        try {
            return this.jobsDatabase.fetchJobPage(region, sortBy, order, pageSize, pageIndex)
                    .get(TIMEOUT, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }

    /**
     * Returns the region as a SingaporeRegion enum.
     *
     * @param request From the GET request.
     * @return SingaporeRegion enum given the id in the request params.
     * @throws IllegalArgumentException if the id is invalid.
     */
    private SingaporeRegion parseRegion(HttpServletRequest request) throws IllegalArgumentException {
        String region = (String) request.getParameter(REGION_PARAM);

        if (region == null || region.isEmpty()) {
            throw new IllegalArgumentException("region param should not be null or empty");
        }

        return SingaporeRegion.getFromId(region); // IAE may be thrown
    }
}
