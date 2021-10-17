package csumb.edu.project2.firebase;

import com.google.api.core.ApiFuture;
import com.google.cloud.firestore.*;
import com.google.firebase.cloud.FirestoreClient;
import csumb.edu.project2.objects.CookieNames;
import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

/**
 * This class has all the helper methods that allow us to put/update/remove objects in the database.
 */
@Service
public class FirebaseService {

    /**
     * The key for each User is just the username
     * @param user
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void saveUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).set(user);
    }

    /**
     * The key for each WishList is wishlist username + wishlist name
     * @param wishList wishlist data model object
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public void saveWishListDetails(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(createWishlistKey(wishList.getUsername(), wishList.getListName())).set(wishList);
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

    public WishList getWishListDetails(String username, String wishlistName) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        DocumentReference documentReference = dbFirestore.collection("wishList").document(createWishlistKey(username, wishlistName));
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

    public void updateUserDetails(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).update("password", user.getPassword());
        ApiFuture<WriteResult> collectionsApiFuture2 = dbFirestore.collection("users").document(user.getUsername()).update("username", user.getUsername());
    }

    public void updateWishListDetails(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(createWishlistKey(wishList.getUsername(), wishList.getListName())).update("username",  wishList.getUsername());
        ApiFuture<WriteResult> collectionsApiFuture2 = dbFirestore.collection("wishList").document(createWishlistKey(wishList.getUsername(), wishList.getListName())).update("listName",  wishList.getListName());
        ApiFuture<WriteResult> collectionsApiFuture3 = dbFirestore.collection("wishList").document(createWishlistKey(wishList.getUsername(), wishList.getListName())).update("items",  wishList.getItems());
    }

    public void deleteUser(User user) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("users").document(user.getUsername()).delete();
    }

    public void deleteWishList(WishList wishList) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        ApiFuture<WriteResult> collectionsApiFuture = dbFirestore.collection("wishList").document(createWishlistKey(wishList.getUsername(), wishList.getListName())).delete();
    }


    public List<WishList> getAllWishLists() throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection("wishList");
        ApiFuture<QuerySnapshot> future = collectionReference.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<WishList> out = new ArrayList<WishList>();
        for (DocumentSnapshot document : documents) {
            out.add(document.toObject(WishList.class));
        }
        return out;
    }

    /**
     * Get all wishlists that belong to a certain username;
     * @param username
     * @return
     * @throws ExecutionException
     * @throws InterruptedException
     */
    public List<WishList> getAllWishLists(String username) throws ExecutionException, InterruptedException {
        Firestore dbFirestore = FirestoreClient.getFirestore();
        CollectionReference collectionReference = dbFirestore.collection("wishList");

        Query query = collectionReference.whereEqualTo("username", username);
        ApiFuture<QuerySnapshot> future = query.get();

        List<QueryDocumentSnapshot> documents = future.get().getDocuments();

        List<WishList> out = new ArrayList<WishList>();
        for (DocumentSnapshot document : documents) {
            out.add(document.toObject(WishList.class));
        }
        return out;
    }

    /**
     * Generate a key for a specific wishlist. The key can be used for both inserting, updating and importantly retrieving wishlists.
     * @param username
     * @param wishListName
     * @return
     */
    public String createWishlistKey(String username, String wishListName) {
        return username + "_u_" + wishListName + "_w";
    }

    public boolean verifyUser(String username, String password) {
        try {
            List<User> users = this.getAllUsers();
            for (User user: users){
                if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create("/"));
                    //set their cookies when the user calls the api/makes post request to this endpoint
                    return true;
                }
            }
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }

        return false;
    }
}
