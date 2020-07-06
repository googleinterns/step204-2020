package com.google.job.servlets;

import com.google.job.data.*;
import com.google.utils.FireStoreUtils;
import com.google.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.time.LocalDate;
import java.util.stream.Collectors;

@WebServlet("/jobs")
public final class NewJobServlet extends HttpServlet {
    private static final String REDIRECT_LINK = "/new-job.html";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        this.jobsDatabase = new JobsDatabase();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            // Gets job post from the form
            Job job = parseJobPost(request);

            // Stores job post into the database
            storeJobPost(job);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (IOException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        } finally {
            // Redirects back to the HTML page.
            response.sendRedirect(REDIRECT_LINK);
        }
    }

    private Job parseJobPost(HttpServletRequest request) throws IOException {
        // Parses job object from the POST request
        BufferedReader bufferedReader = request.getReader();
        String jobPostJsonStr = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator()));

        if (StringUtils.isBlank(jobPostJsonStr)) {
            throw new IOException();
        }

        Job job = ServletUtils.parseFromJsonUsingGson(jobPostJsonStr, Job.class);
        return job;
    }

    private void storeJobPost(Job job) {
        this.jobsDatabase.addJob(job);
    }
}