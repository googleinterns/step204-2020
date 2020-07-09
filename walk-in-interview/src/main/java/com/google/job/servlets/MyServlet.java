package com.google.job.servlets;

import com.google.job.data.JobsDatabase;
import com.google.utils.ServletUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Class to handle PATCH request related method. */
public abstract class MyServlet extends HttpServlet {
    @Override
    public void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (request.getMethod().equalsIgnoreCase("PATCH")){
            doPatch(request, response);
        } else {
            super.service(request, response);
        }
    }

    /** Gets the Job id from the request. */
    protected String getJobId(HttpServletRequest request, String paramName, String defaultValue)
            throws ServletException, IOException {
        String jobId = ServletUtils.getStringParameter(request, paramName, defaultValue);

        if (jobId.isEmpty()) {
            throw new IllegalArgumentException("Empty Job Id");
        }

        try {
            if (JobsDatabase.isJobIdExist(jobId)) {
                return jobId;
            }

            throw new IllegalArgumentException("Invalid Job Id");
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
            throw new IOException("Error occur");
        }
    }

    /** Handles the PATCH request from the client. */
    public abstract void doPatch(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException;
}
