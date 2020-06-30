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

    /**
     * Gets the only cloud firestore database.
     *
     * @return The only cloud firestore database.
     * @throws IOException If error occurs when creating database.
     */
    public static Firestore getFireStore() throws IOException {
        if (db == null) {
            init();
        }

        return db;
    }

    /**
     * Stores item into the database
     *
     * @param collection Name of the collection where the item will be stored.
     * @param item Target item to be stored.
     * @return A reference towards the item (in the form of "document").
     * @throws ExecutionException If error occurs when blocking the operation.
     * @throws InterruptedException If error occurs when blocking the operation.
     */
    public static ApiFuture<DocumentReference> store(String collection, Object item)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = db.collection(collection).add(item);

        // Blocks operation
        addedDocRef.get();

        return addedDocRef;
        // id for the document: addedDocRef.getId()
    }

    /**
     * Updates the field in a specific document.
     *
     * @param docRef A reference towards the target document.
     * @param field Field to be updated.
     * @param value Updated value.
     * @throws ExecutionException If error occurs when blocking the operation.
     * @throws InterruptedException If error occurs when blocking the operation.
     */
    public static void update(DocumentReference docRef, String field, Object value)
            throws ExecutionException, InterruptedException {
        ApiFuture<WriteResult> future = docRef.update(field, /* value= */true);

        // Blocks operation
        future.get();
    }

    /**
     * Loads the target document in database.
     *
     * @param docRef Document reference towards the target document.
     * @return Document snap shot.
     * @throws ExecutionException If error occurs when getting the document snap shot.
     * @throws InterruptedException If error occurs when getting the document snap shot.
     */
    public static DocumentSnapshot load(DocumentReference docRef)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentSnapshot> future = docRef.get();

        DocumentSnapshot document = future.get();

        return document;
    }
}
