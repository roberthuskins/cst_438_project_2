package csumb.edu.project2.firebase;

import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for our database. The timers are to prevent flakiness due to latency.
 */
@SpringBootTest
public class FirebaseServiceTest {
    @Autowired
    FirebaseService firebaseService;

    @Test
    public void testInsertShowUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test@test.com", "test");
        firebaseService.saveUserDetails(tempUser);
        TimeUnit.SECONDS.sleep(5);

        User newUser = firebaseService.getUserDetails("test@test.com");

        TimeUnit.SECONDS.sleep(5);

        assertEquals(newUser.getUsername(), tempUser.getUsername());
        assertEquals(newUser.getPassword(), tempUser.getPassword());

        firebaseService.deleteUser(newUser);
    }

    @Test
    public void testDeleteUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test1@test.com", "test1");
        firebaseService.saveUserDetails(tempUser);

        TimeUnit.SECONDS.sleep(5);

        firebaseService.deleteUser(tempUser);
        TimeUnit.SECONDS.sleep(5);

        assertEquals(null, firebaseService.getUserDetails("test1@test.com"));
    }

    @Test
    public void testUpdateUser() throws ExecutionException, InterruptedException {
        User tempUser = new User("test2@test.com", "wrong password");
        firebaseService.saveUserDetails(tempUser);
        TimeUnit.SECONDS.sleep(5);
        User newUser = new User("test2@test.com", "test3");
        firebaseService.updateUserDetails(newUser);
        TimeUnit.SECONDS.sleep(5);
        String mynewPassword = firebaseService.getUserDetails("test2@test.com").getPassword();
        assertEquals("test3", mynewPassword);

        firebaseService.deleteUser(newUser);
    }

    @Test
    public void testInsertWishList() throws ExecutionException, InterruptedException {
        WishList wishList = new WishList("test2@test.com", "wishList1", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);
        firebaseService.saveWishListDetails(wishList);
        TimeUnit.SECONDS.sleep(5);

        TimeUnit.SECONDS.sleep(5);

        WishList wishList2 = new WishList("test2@test.com", "wishList2", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")));
        firebaseService.saveWishListDetails(wishList2);
        TimeUnit.SECONDS.sleep(5);

        assertEquals("test2@test.com", wishList.getUsername());
        assertEquals("wishList1", wishList.getListName());


        assertEquals("test2@test.com", wishList2.getUsername());
        assertEquals("wishList2", wishList2.getListName());

        firebaseService.deleteWishList(wishList);
        firebaseService.deleteWishList(wishList2);
    }


    @Test
    public void testUpdateWishList() throws ExecutionException, InterruptedException {
        WishList wishList = new WishList("test2@test.com", "wishList1", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);
        firebaseService.saveWishListDetails(wishList);
        TimeUnit.SECONDS.sleep(5);

        WishList wishList2 = new WishList("test2@test.com", "wishList1", Arrays.asList(new Item(10.00, "cars", "item2", "image2"), new Item(10.00, "cars2", "item2", "image2")),true);

        firebaseService.updateWishListDetails(wishList2);
        TimeUnit.SECONDS.sleep(5);

        WishList wishList3 = new WishList("test2@test.com", "wishList1", Arrays.asList(new Item(10.50, "phone", "item2", "image2"), new Item(10.00, "cars2", "item2", "image2")));

        firebaseService.updateWishListDetails(wishList3);
        TimeUnit.SECONDS.sleep(5);

        WishList wishList4 = firebaseService.getWishListDetails(wishList3.getUsername(),wishList3.getListName());
        TimeUnit.SECONDS.sleep(5);

        assertEquals(wishList4.getItems().get(0).getName(), wishList3.getItems().get(0).getName());
        assertEquals(wishList4.getItems().get(0).getImageURL(), wishList3.getItems().get(0).getImageURL());
        assertEquals(wishList4.getItems().get(0).getPrice(), wishList3.getItems().get(0).getPrice());
        assertEquals(wishList4.getItems().get(0).getShopURL(), wishList3.getItems().get(0).getShopURL());
    }

    @Test
    public void testDeleteWishList() throws ExecutionException, InterruptedException {
        WishList wishList = new WishList("test2@test.com", "wishList1", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);
        firebaseService.saveWishListDetails(wishList);

        TimeUnit.SECONDS.sleep(5);

        firebaseService.deleteWishList(wishList);
        TimeUnit.SECONDS.sleep(5);

        assertEquals(null, firebaseService.getUserDetails("test2@test.com"));
    }

    @Test
    public void testGetAllWishLists() throws ExecutionException, InterruptedException {
        WishList wishList = new WishList("getAllWishlists@temp.com", "wishList1", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);
        firebaseService.saveWishListDetails(wishList);
        TimeUnit.SECONDS.sleep(5);

        WishList wishList2 = new WishList("getAllWishlists@temp.com", "wishList2", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);

        firebaseService.saveWishListDetails(wishList2);
        TimeUnit.SECONDS.sleep(5);

        WishList wishList3 = new WishList("getAllWishlists@temp.com", "wishList3", Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods2", "item4", "image4")),true);
        firebaseService.saveWishListDetails(wishList3);
        TimeUnit.SECONDS.sleep(5);

        List<WishList> allWishlists = firebaseService.getAllWishLists();
        TimeUnit.SECONDS.sleep(5);
        assertTrue(allWishlists.size() >= 3);

        allWishlists= firebaseService.getAllWishLists("getAllWishlists@temp.com");
        TimeUnit.SECONDS.sleep(5);

        assertTrue(allWishlists.size() == 3);

        firebaseService.deleteWishList(wishList);
        firebaseService.deleteWishList(wishList2);
        firebaseService.deleteWishList(wishList3);
    }
}
