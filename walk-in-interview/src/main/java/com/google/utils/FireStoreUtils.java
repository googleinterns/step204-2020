package com.google.utils;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.configuration.ConfigurationFactory;
import com.google.configuration.DevelopmentFireStoreConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;

/** Util methods related to Cloud Firestore database. */
public final class FireStoreUtils {
    private static Firestore firestore;

    private FireStoreUtils() {}

    // Use the application default credentials
    private static void init() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(ConfigurationFactory.getFireStoreConfiguration().getProjectId())
                .build();
        FirebaseApp.initializeApp(options);

        firestore = FirestoreClient.getFirestore();
    }

    /**
     * Gets the only cloud firestore database.
     *
     * @return The only cloud firestore database.
     * @throws IOException If error occurs when creating database.
     */
    public static Firestore getFireStore() throws IOException {
        if (firestore == null) {
            init();
        }

        return firestore;
    }

    public static <T> T convertDocumentSnapshotToPOJO(DocumentSnapshot documentSnapshot, Class<T> classType) {
        T item = null;

        if (documentSnapshot.exists()) {
            // Converts document to POJO
            item = documentSnapshot.toObject(classType);
        } else {
            // TODO(issue/10): error handling
        }

        return item;
    }
}
