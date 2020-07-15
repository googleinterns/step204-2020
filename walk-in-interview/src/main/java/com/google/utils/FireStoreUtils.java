package com.google.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.configuration.ConfigurationFactory;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;
import com.google.job.data.JobsDatabase;

import javax.annotation.Nullable;
import javax.servlet.ServletException;
import java.io.IOException;
import java.util.Optional;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

/** Util methods related to Cloud Firestore database. */
public final class FireStoreUtils {
    private static final long TIMEOUT = 5;
    @Nullable
    private static Firestore firestore;

    private FireStoreUtils() {}

    // Use the application default credentials
    private static void init() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(ConfigurationFactory.getFireStoreConfiguration().getProjectId())
                .build();

        if(FirebaseApp.getApps().isEmpty()) {
            FirebaseApp.initializeApp(options);
        }

        firestore = FirestoreClient.getFirestore();
    }

    /**
     * Gets the only cloud firestore database.
     *
     * @return The only cloud firestore database.
     * @throws IOException If error occurs when creating database.
     */
    public static Firestore getFireStore() {
        if (firestore == null) {
            try {
                init();
            } catch (IOException e) {
                // TODO(issue/10.1): error handling
                e.printStackTrace();
            }
        }

        return firestore;
    }

    /**
     * Converts the DocumentSnapshot object to the target raw object.
     *
     * @param documentSnapshot Target document snapshot to be converted.
     * @param classType Type of the target raw object.
     * @param <T> Generic class type.
     * @return Converted target raw object.
     */
    public static <T> Optional<T> convertDocumentSnapshotToPOJO(DocumentSnapshot documentSnapshot, Class<T> classType) {
        T item = null;

        if (documentSnapshot.exists()) {
            // Converts document to POJO
            item = documentSnapshot.toObject(classType);
        } else {
            // TODO(issue/10.2): error handling
        }

        return Optional.ofNullable(item);
    }

    /**
     * Verifies if it is a valid job id that this user can update.
     *
     * @throws IllegalArgumentException If given id is empty.
     */
    // TODO(issue/25): incorporate the account stuff into job post.
    public static void verifyUserCanUpdateJob(String jobId) throws
            IllegalArgumentException ,ServletException, ExecutionException, TimeoutException {
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
