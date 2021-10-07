package csumb.edu.project2.controller;

import csumb.edu.project2.firebase.FirebaseService;
import csumb.edu.project2.objects.CookieNames;
import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.Cookie;

import java.io.IOException;
import java.net.URI;
import java.util.*;
import java.util.concurrent.ExecutionException;

/**
 * Class that handles all the REST API routes
 */
@RestController
public class APIController {
    @Autowired FirebaseService firebaseService;

    @PutMapping("/newUser")
    public String newUser(@RequestParam String username, @RequestParam String password) {
        try {
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return "Execution Exception";
        } catch (InterruptedException e) {
            return "Interrupted Exception";
        }

        return "User added successfully.";
    }

    //This is temporary as learn how this is supposed to be designed.
    @PostMapping("/createNewUser")
        public ResponseEntity<Object> createNewUser(@RequestParam String username, @RequestParam String password) throws IOException {
        try {
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
        // Redirect code credit: https://stackoverflow.com/a/47411493
        //TODO: Attach a cookie for persistence sake/sanity check
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException{
        try {
            List<User> users = firebaseService.getAllUsers();
            for (User user: users){
                if(user.getUsername().equals(username) && user.getPassword().equals(password)){
                    HttpHeaders headers = new HttpHeaders();
                    headers.setLocation(URI.create("/"));
                    //set their cookies when the user calls the api/makes post request to this endpoint
                    response.addCookie(new Cookie(CookieNames.USERNAME, username));
                    response.addCookie(new Cookie(CookieNames.PASSWORD, password));
                    return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/signin"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
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
    public List<WishList> wishlists(@RequestParam Optional<String> search, @RequestParam Optional<String> list, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        //we read the cookies through the @CookieValues. Note that these cookies were not passed in directly through the browser. It was the web controller that transferred the cookies through the browser to the APIController.
        System.out.println(login_username);
        System.out.println(login_password);

        //below this comment we will make the request to the database with the specific usernames and passwords.

        return Arrays.asList(new WishList("guillermo@gflores.dev", "list 1", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"),new Item(20.00, "galaxy buds 2", "item url","image") )), new WishList("guillermo@gflores.dev", "list 2", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"),new Item(20.00, "galaxy buds 2", "item url","image") )));
    }

    /* Admin endpoints go below here */

    @GetMapping("/users")
    public List<User> users() {
        try {
            List<User> users = firebaseService.getAllUsers();
            return users;
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return null;
    }

    @PutMapping("/users")
    public String createUser(@RequestParam String username, @RequestParam String password) {
        try {
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return "Execution Exception";
        } catch (InterruptedException e) {
            return "Interrupted Exception";
        }

        return "User added successfully.";
    }

    @PatchMapping("/users")
    public String updateUser(@RequestParam String username, @RequestParam String password) {
        try {
            firebaseService.updateUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return "Execution Exception";
        } catch (InterruptedException e) {
            return "Interrupted Exception";
        }

        return "User edited successfully.";
    }

    @DeleteMapping("/users")
    public String deleteUser(@RequestParam String username) {
        try {
            User myUser = firebaseService.getUserDetails(username);
            firebaseService.deleteUser(myUser);
            return myUser.getUsername() + " has been deleted successfully.";
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        return "User not deleted successfully.";
    }

}
