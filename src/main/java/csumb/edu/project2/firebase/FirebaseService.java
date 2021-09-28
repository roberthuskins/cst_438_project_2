package csumb.edu.project2.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import csumb.edu.project2.objects.User;

import java.util.concurrent.ExecutionException;

public class FirebaseService {
    //https://youtu.be/ScsID2yPB8k?t=494
    public String saveUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).set(user);
        return collectionsApiFuture.get().getUpdateTime().toString();

    }

    public User getUserDetails(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future. get();

        User user = null;

        if(document.exists()) {
            user = document.toObject(User.class);

            return user;
        } else {
            return null;
        }
    }
}
