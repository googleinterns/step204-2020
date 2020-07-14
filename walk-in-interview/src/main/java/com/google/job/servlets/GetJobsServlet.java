package com.google.job.servlets;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import com.google.job.data.*;

/** Servlet that gets job listings. */
@WebServlet("/jobs")
public final class GetJobsServlet extends HttpServlet {

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
            Filter sortBy = parseSortBy(request);
            Order order = parseOrder(request);
            int pageSize = parsePageSize(request);
            int pageIndex = parsePageIndex(request);
            // TODO(issue/44): get jobs from the database
        } catch(IllegalArgumentException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
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
            throw new IllegalArgumentException("page size param should be an int")
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
            throw new IllegalArgumentException("page index param should be an int")
        }
    }
}
