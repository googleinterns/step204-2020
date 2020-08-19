package com.google.account.business.servlets;

import com.google.account.business.data.Business;
import com.google.account.business.data.BusinessDatabase;
import com.google.common.collect.ImmutableList;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.utils.FirebaseAuthUtils;
import com.google.utils.ServletUtils;
import org.apache.commons.lang3.StringUtils;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.BufferedReader;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;

/** Servlet that handles creating new business account. */
@WebServlet("/business-account")
public final class CreateBusinessServlet extends HttpServlet {
    private static final Logger LOGGER = Logger.getLogger(CreateBusinessServlet.class.getName());
    private static final long TIMEOUT_SECONDS = 5;

    private BusinessDatabase businessDatabase;

    @Override
    public void init() {
        this.businessDatabase = new BusinessDatabase();
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response) {
        try {
            Optional<String> optionalUid = FirebaseAuthUtils.getUid(request);

            if (!optionalUid.isPresent()) {
                LOGGER.log(Level.SEVERE, /* msg= */"Illegal uid");
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return;
            }

            String uid = optionalUid.get();

            // Gets business object from the client
            Business inputBusiness = parseBusinessAccount(request);

            // Fetches the existing business object from database
            Optional<Business> existingBusinessOptional = this.businessDatabase.getBusinessAccount(uid).get();

            Business business;

            // Updates only the updatable fields
            if (!existingBusinessOptional.isPresent()) {
                business = Business.newBuilder()
                        .setName(inputBusiness.getName())
                        .setJobs(ImmutableList.of()).build();
            } else {
                business = Business.newBuilder()
                        .setName(inputBusiness.getName())
                        .build();
            }

            // Stores the account into cloud firestore
            addBusinessAccount(uid, business);

            // Sends the success status code in the response
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (FirebaseAuthException | IOException | ServletException
                | ExecutionException | TimeoutException | InterruptedException e) {
            // TODO(issue/47): use custom exceptions
            LOGGER.log(Level.SEVERE, /* msg= */"Error occur: " + e.getCause(), e);
            // Sends the fail status code in the response
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
        }
    }

    /** Parses into valid Business object from json received from client request. */
    private Business parseBusinessAccount(HttpServletRequest request) throws IOException, IllegalArgumentException {
        // Parses job object from the POST request
        try (BufferedReader bufferedReader = request.getReader()) {
            String businessAccountJsonStr =
                    bufferedReader.lines().collect(Collectors.joining(System.lineSeparator())).trim();

            if (StringUtils.isBlank(businessAccountJsonStr)) {
                throw new IllegalArgumentException("Json for Business object is Empty");
            }

            Business rawBusiness = ServletUtils.parseFromJsonUsingGson(businessAccountJsonStr, Business.class);

            // Validates the attributes via build()
            return rawBusiness.toBuilder().build();
        }
    }

    /** Adds the business account in the database. */
    private void addBusinessAccount(String uid, Business business)
            throws IllegalArgumentException, ServletException, ExecutionException, TimeoutException {
        try {
            // Blocks the operation and waits for the future.
            // Use timeout in case it blocks forever.
            this.businessDatabase.updateBusinessAccount(uid, business).get(TIMEOUT_SECONDS, TimeUnit.SECONDS);
        } catch (InterruptedException | IOException e) {
            throw new ServletException(e);
        }
    }
}
