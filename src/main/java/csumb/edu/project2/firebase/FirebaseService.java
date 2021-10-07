package csumb.edu.project2.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
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

    public void saveWishListDetails(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(wishList.getListName()).set(wishList);
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

    ////
    public WishList getWishListDetails(String username, List<ArrayList> items) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("wishList").document(username);
        ApiFuture<DocumentSnapshot> future = documentReference.get();

        DocumentSnapshot document = future.get();

        WishList wishList = null;

        if(document.exists()) {
            wishList = document.toObject(WishList.class);
            if (wishList.getItems() == null || wishList.getUsername() == null) {
                //If object doens't exist firebase still gives us an Object with all the fields null
                //This if statement forces it to return a null object if the object doesn't exist
                return null;
            }
            return wishList;
        } else {
            return null;
        }
    }

    public List<User> getAllUsers() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection("users");
        ApiFuture<QuerySnapshot> future = collectionReference.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<User> out = new ArrayList<User>();
        for (DocumentSnapshot document : documents) {
            out.add(document.toObject(User.class));
        }

        return out;
    }

    public List<Item> getAllItems() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection("items");
        ApiFuture<QuerySnapshot> future = collectionReference.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<Item> out = new ArrayList<Item>();
        for (DocumentSnapshot document : documents) {
            out.add(document.toObject(Item.class));
        }
        return out;
    }

    //for firebase save and update are the same thing
    public void updateUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).update("password", user.getPassword());
        ApiFuture<WriteResult> collectionsApiFuture2 = dbFirestore.collection("users").document(user.getUsername()).update("username", user.getUsername());
    }

    /////
    //for firebase save and update are the same thing
    public void updateWishListDetails(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(wishList.getListName()).update("wishList", wishList.getListName());
    }

    public void deleteUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).delete();
    }

    ////
    public void deleteWishList(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(wishList.getListName()).delete();
    }

}
