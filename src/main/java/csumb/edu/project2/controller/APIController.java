package csumb.edu.project2.controller;

import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Class that handles all the REST API routes
 */
@RestController
public class APIController {
    //https://github.com/rajendrach/restdemo/blob/master/src/main/java/com/example/demo/controller/RestDemoController.jav

    @PutMapping("/newUser")
    public void newUser(@RequestParam("username") String username, @RequestParam("password") String password) {

    }

    @PostMapping("/login")
    public void login(@RequestParam("username") String username, @RequestParam("password") String password) {

    }

    @PostMapping("/logout")
    public void logout(@RequestParam("username") String username) {

    }

    @DeleteMapping("/logout")
    public void deleteUser(@RequestParam("username") String username, @RequestParam("password") String password) {

    }

    //should return all items to a logged in user if they're logged in and don't specify a search
    @GetMapping("/items")
    public List<Item> items(@RequestParam("search") String search) {
        return Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods", "item1", "image1"));
    }

    //this endpoint may be combined with the one above it
    @GetMapping("/wishlist")
    public List<WishList> getWishlist(@RequestParam("list") String list) {
        return Arrays.asList(new WishList("guillermo@guillermo.com", new ArrayList<>()));
    }

    //add item
    @PostMapping("/items")
    public void addItem(@RequestParam("item") String list, @RequestParam("url") String url, @RequestParam("imageurl") String imageurl) {
    }

    @DeleteMapping("/items")
    public void deleteItem(@RequestParam("item") String item) {
    }

    @PatchMapping("/items")
    public void updateItems(@RequestParam("item") String item) {

    }

    /* Admin stuff goes below here */
    @GetMapping("/users")
    public List<User> users() {
        return Arrays.asList(new User("guillermo@guillermo.com", "long"), new User("rob@rob.com", "long"));
    }
}