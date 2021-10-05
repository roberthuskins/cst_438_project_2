package csumb.edu.project2.controller;

import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

/**
 * Class that handles all the REST API routes
 */
@RestController
public class APIController {

    @PutMapping("/newUser")
    public String newUser(@RequestParam String username, @RequestParam String password) {
        return "new user added";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, @RequestParam String password) {
        return "login successful";
    }

    @PostMapping("/logout")
    public String logout(@RequestParam String username) {
        return "logout with just username successful";
    }

    @DeleteMapping("/logout")
    public String deleteUser(@RequestParam String username, @RequestParam String password) {
        return "logout with username and password (delete user) successful";
    }

    //If no params, then they should show all items for a specific user that is logged in. If search it should search the db. if list it will return all the items in said wishlist.
    @GetMapping("/items")
    public List<Item> items(@RequestParam Optional<String> search, @RequestParam Optional<String> list) {
        return Arrays.asList(new Item(10.00, "airpods", "item1", "image1"), new Item(10.00, "airpods", "item1", "image1"));
    }

    //add item
    @PostMapping("/items")
    public void addItem(@RequestParam String item_name, @RequestParam Optional<String> list, @RequestParam Optional<String> url, @RequestParam Optional<String> imageurl) {

    }

    @DeleteMapping("/items")
    public String deleteItem(@RequestParam String item_name) {
        return "item deleted";
    }

    @PatchMapping("/items")
    public String updateItems(@RequestParam String item_name) {
        return "item added";
    }

    //If no params, then they should show all wish lists for a specific user that is logged in. If search it should search the db. if list it will return all the items in said wishlist.
    @GetMapping("/wishlists")
    public List<WishList> wishlists(@RequestParam Optional<String> search, @RequestParam Optional<String> list) {
        return Arrays.asList(new WishList("guillermo@gflores.dev", "list 1", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"),new Item(20.00, "galaxy buds 2", "item url","image") )), new WishList("guillermo@gflores.dev", "list 2", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"),new Item(20.00, "galaxy buds 2", "item url","image") )));
    }

    /* Admin endpoints go below here */

    @GetMapping("/users")
    public List<User> users() {
        return Arrays.asList(new User("guillermo@guillermo.com", "long"), new User("rob@rob.com", "long"));
    }

    @PutMapping("/users")
    public String createUser(@RequestParam String username, @RequestParam String password) {
        return "user created";
    }

    @PatchMapping
    public String updateUser(@RequestParam String username, @RequestParam String password) {
        return "user updated";
    }
}