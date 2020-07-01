package com.google.utils;

import com.google.api.core.ApiFuture;
import com.google.auth.oauth2.GoogleCredentials;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;

import com.google.cloud.firestore.WriteResult;
import com.google.configuration.TestFireStoreConfiguration;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.cloud.FirestoreClient;

import java.io.IOException;
import java.util.concurrent.ExecutionException;

/** Util methods related to Cloud Firestore database. */
public final class FireStoreUtils {
    private static Firestore firestore;

    private FireStoreUtils() {}

    // Use the application default credentials
    private static void init() throws IOException {
        GoogleCredentials credentials = GoogleCredentials.getApplicationDefault();
        FirebaseOptions options = new FirebaseOptions.Builder()
                .setCredentials(credentials)
                .setProjectId(TestFireStoreConfiguration.PROJECT_ID)
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

    /**
     * Stores item into the database in the form of documents.
     *
     * @param collection Name of the collection where the item will be stored.
     * @param item Target item to be stored.
     * @return Auto-generated id for the stored item.
     * @throws ExecutionException If error occurs when blocking the operation.
     * @throws InterruptedException If error occurs when blocking the operation.
     */
    public static String store(String collection, Object item)
            throws ExecutionException, InterruptedException {
        ApiFuture<DocumentReference> addedDocRef = firestore.collection(collection).add(item);

        // Blocks operation and gets the auto-generated id for the item.
        String documentId = addedDocRef.get().getId();

        return documentId;
    }

    /**
     * Updates the field in a specific item.
     *
     * @param collection Name of the collection where the target item is stored.
     * @param documentId Id of the target item.
     * @param field Field to be updated.
     * @param value Updated value.
     * @throws ExecutionException If error occurs when blocking the operation.
     * @throws InterruptedException If error occurs when blocking the operation.
     */
    public static void update(String collection, String documentId, String field, Object value)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collection).document(documentId);
        ApiFuture<WriteResult> future = docRef.update(field, value);

        // Blocks operation
        future.get();
    }

    /**
     * Loads the target item from the database.
     *
     * @param collection Name of the collection where the target item is stored.
     * @param documentId Id of the target item.
     * @param classType The class type of the target item.
     * @param <T> Generic type.
     * @return The target item in the form of original type.
     * @throws ExecutionException ExecutionException If error occurs when getting the document snap shot.
     * @throws InterruptedException If error occurs when getting the document snap shot.
     */
    public static <T> T load(String collection, String documentId, Class<T> classType)
            throws ExecutionException, InterruptedException {
        DocumentReference docRef = firestore.collection(collection).document(documentId);
        ApiFuture<DocumentSnapshot> future = docRef.get();
        DocumentSnapshot document = future.get();

        T item = null;

        if (document.exists()) {
            // Converts document to POJO
            item = document.toObject(classType);
        } else {
            // TODO(issue/10): error handling
        }

        return item;
    }
}
