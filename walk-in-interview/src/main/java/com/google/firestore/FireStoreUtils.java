package com.google.firestore;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.WriteResult;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Util methods related to Cloud Firestore database. */
public final class FireStoreUtils {

    private static final String PROJECT_ID = "google.com:walk-in-interview";
    private static Firestore db;

    private FireStoreUtils() {}

    // Use the application default credentials
    private static void init() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(PROJECT_ID)
                .build();
        FirebaseApp.initializeApp(options);

        db = FirestoreClient.getFirestore();
    }

    public static Firestore getFireStore() throws IOException {
        if (db == null) {
            init();
        }

        return db;
    }

    public static ApiFuture<DocumentReference> store(String collection, Object item)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = db.collection(collection).add(item);

        // Blocks operation
        addedDocRef.get();

        return addedDocRef;
        // id for the document: addedDocRef.getId()
    }

    public static void update(DocumentReference docRef, String field, Object value)
            throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = docRef.update("capital", true);

        // Blocks operation
        future.get();
    }

    public static DocumentSnapshot load(DocumentReference docRef)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = future.get();

        return document;
    }
}
