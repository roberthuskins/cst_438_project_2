package csumb.edu.project2.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.DocumentReference;
import com.google.cloud.firestore.DocumentSnapshot;
import com.google.cloud.firestore.Firestore;
import com.google.cloud.firestore.WriteResult;
import com.google.firebase.cloud.FirestoreClient;
import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.stereotype.Service;

import java.util.concurrent.ExecutionException;

/**
 * This class has all the helper methods that allow us to put/update/remove objects in the database.
 */
@Service
public class FirebaseService {

    public void saveUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).set(user);
    }


    public User getUserDetails(String name) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("users").document(name);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future. get();

        User user = null;

        if(document.exists()) {
            user = document.toObject(User.class);
            if (user.getPassword() == null || user.getUsername() == null) {
                //If object doens't exist firebase still gives us an Object with all the fields null
                //This if statement forces it to return a null object if the object doesn't exist
                return null;
            }
            return user;
        } else {
            return null;
        }
    }

    //for firebase save and update are the same thing
    public void updateUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).update("password", user.getPassword());
        ApiFuture<WriteResult> collectionsApiFuture2 = dbFirestore.collection("users").document(user.getUsername()).update("username", user.getUsername());
    }

    public void deleteUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).delete();
    }
}
