package com.google.job.servlets;

import com.google.cloud.firestore.DocumentReference;
import com.google.job.data.*;
import com.google.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.stream.Collectors;

/** Servlet that handles posting new job posts. */
@WebServlet("/jobs")
public final class NewJobServlet extends HttpServlet {

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
        } catch (IOException | IllegalArgumentException | ServletException e) {
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    private Job parseJobPost(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job object from the POST request
        BufferedReader bufferedReader = request.getReader();
        String jobPostJsonStr = bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

        if (StringUtils.isBlank(jobPostJsonStr)) {
            throw new IllegalArgumentException("Json for Job object is Empty");
        }

        Job rawJob = ServletUtils.parseFromJsonUsingGson(jobPostJsonStr, Job.class);

        Job job = Job.newBuilder()
                .setJobStatus(JobStatus.ACTIVE)
                .setJobTitle(rawJob.getJobTitle())
                .setLocation(rawJob.getJobLocation())
                .setJobDescription(rawJob.getJobDescription())
                .setJobPay(rawJob.getJobPay())
                .setRequirements(rawJob.getRequirements())
                .setPostExpiry(rawJob.getPostExpiryTimestamp())
                .setJobDuration(rawJob.getJobDuration())
                .build();


        return job;
    }

    private void storeJobPost(Job job) throws ServletException {
        Future<DocumentReference> future = this.jobsDatabase.addJob(job);

        try {
            // Synchronizes and blocks the operation.
            String jobId = future.get().getId();
            // Updates the jobId field of the job post with the auto-generated cloud firestore id.
            this.jobsDatabase.updateJobId(jobId).get();
        } catch (InterruptedException e) {
            throw new ServletException(e);
        } catch (ExecutionException e) {
            System.err.println("Error occur: " + e.getCause());
        }
    }
}