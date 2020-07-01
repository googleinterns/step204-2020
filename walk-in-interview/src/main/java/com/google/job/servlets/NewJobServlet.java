package com.google.job.servlets;

import com.google.common.collect.ImmutableList;
import com.google.job.data.*;
import com.google.utils.FireStoreUtils;
import com.google.utils.ServletUtils;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.time.LocalDate;
import java.util.Arrays;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

@WebServlet("/jobs")
public final class NewJobServlet extends HttpServlet {
    private static final String PARAM_NAME_TITLE = "job-title";
    private static final String PARAM_NAME_DESCRIPTION = "job-description";
    private static final String PARAM_NAME_ADDRESS = "job-address";
    private static final String PARAM_NAME_FREQUENCY = "job-pay-frequency";
    private static final String PARAM_NAME_MIN = "job-pay-min";
    private static final String PARAM_NAME_MAX = "job-pay-max";
    private static final String PARAM_NAME_REQUIREMENTS = "requirement-list";
    private static final String PARAM_NAME_POST_EXPIRY = "post-expiry";
    private static final String PARAM_NAME_JOB_DURATION = "job-duration";

    private static final String DEFAULT_STRING_VALUE = "";
    private static final float DEFAULT_FLOAT_VALUE = 0;
    private static final LocalDate DEFAULT_DATE_VALUE = null;
    private static final String DEFAULT_FREQUENCY = "HOURLY";
    private static final String DEFAULT_DURATION = "SIX_MONTHS";

    private static final String REDIRECT_LINK = "/new-job.html";

    private JobsDatabase jobsDatabase;

    @Override
    public void init() {
        try {
            this.jobsDatabase = new JobsDatabase(FireStoreUtils.getFireStore());
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: error handling
        }
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        // Gets job post from the form
        Job job = parseJobPost(request);

        // Stores job post into the database
        storeJobPost(job);

        // Redirects back to the HTML page.
        response.sendRedirect(REDIRECT_LINK);
    }

    private Job parseJobPost(HttpServletRequest request) {
        // Gets jobs from the form
        // TODO: validate the parameter
        JobStatus jobStatus = JobStatus.ACTIVE;

        String jobName = ServletUtils.getStringParameter(request, PARAM_NAME_TITLE, DEFAULT_STRING_VALUE);

        String jobAddress = ServletUtils.getStringParameter(request, PARAM_NAME_ADDRESS, DEFAULT_STRING_VALUE);
        // TODO: hardcoded first, later need Places Library to locate the lat and lon
        double lat = 0;
        double lon = 0;
        JobLocation jobLocation = new JobLocation(jobAddress, lat, lon);

        String jobDescription = ServletUtils.getStringParameter(request, PARAM_NAME_DESCRIPTION, DEFAULT_STRING_VALUE);

        Frequency frequency = Frequency.valueOf(
                ServletUtils.getStringParameter(request,PARAM_NAME_FREQUENCY, DEFAULT_FREQUENCY));
        float min = ServletUtils.getFloatParameter(request, PARAM_NAME_MIN, DEFAULT_FLOAT_VALUE);
        float max = ServletUtils.getFloatParameter(request, PARAM_NAME_MAX, DEFAULT_FLOAT_VALUE);
        JobPayment jobPayment = new JobPayment(min, max, frequency);

        ImmutableList<String> requirements = ImmutableList.copyOf(
                Arrays.asList(request.getParameterValues(PARAM_NAME_REQUIREMENTS)));

        LocalDate postExpiry = ServletUtils.getLocalDateParameter(request, PARAM_NAME_POST_EXPIRY, DEFAULT_DATE_VALUE);

        String jobDurationString = ServletUtils.getStringParameter(
                request, PARAM_NAME_JOB_DURATION, DEFAULT_STRING_VALUE);
        Duration duration = jobDurationString.isEmpty() ? null : Duration.valueOf(jobDurationString);
        Optional<Duration> jobDuration = Optional.ofNullable(duration);

        Job job = new Job(jobStatus, jobName, jobLocation, jobDescription,
                jobPayment, requirements, postExpiry, jobDuration);

        return job;
    }

    private void storeJobPost(Job job) {
        try {
            this.jobsDatabase.addJob(job);
        } catch (ExecutionException e) {
            e.printStackTrace();
            // TODO: error handling
        } catch (InterruptedException e) {
            e.printStackTrace();
            // TODO: error handling
        }
    }
}