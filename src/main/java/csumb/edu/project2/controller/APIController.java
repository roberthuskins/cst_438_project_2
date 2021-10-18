package csumb.edu.project2.controller;

import csumb.edu.project2.Heroku.URLFetcher;
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
    @Autowired
    FirebaseService firebaseService;
    @Autowired
    URLFetcher urlFetcher;

    @PostMapping("/createNewUser")
        public ResponseEntity<Object> createNewUser(@RequestParam String username, @RequestParam String password) throws IOException {
        //first we are going to check if the username exists in the DB, if so reroute them to the login page
        try {
            if(userExists(username)){
                HttpHeaders userExistsReroute = new HttpHeaders();
                userExistsReroute.setLocation(URI.create("/signin"));
                return new ResponseEntity<>(userExistsReroute, HttpStatus.MOVED_PERMANENTLY);
            }
            else;
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
        //register worked correctly and user is now signed in
        // Redirect code credit: https://stackoverflow.com/a/47411493
        //TODO: Attach a cookie for persistence sake/sanity check
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    private boolean userExists(String username) {
        try {
            List<User> users = firebaseService.getAllUsers();
            for (User user: users){
                if(user.getUsername().equals(username)){
                    return true;
                }
            }
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return false;
    }


    @PostMapping("/login")
    public ResponseEntity<Object> login(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
        try {
            List<User> users = firebaseService.getAllUsers();
            for (User user : users) {
                if (user.getUsername().equals(username) && user.getPassword().equals(password)) {
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

    //deletes the user
    @DeleteMapping("/logout")
    public Boolean deleteUser(@RequestParam String username, @RequestParam String password) {
        try {
            firebaseService.deleteUser(new User(username, password));
        } catch (ExecutionException e) {
            return false;
        } catch (InterruptedException e) {
            return false;
        }
        return true;
    }

    /**
     * Creates a wishlist in our database with a blank item list to be filled by additems
     * @param listName
     * @param login_username
     * @param login_password
     * @return
     */
    @PostMapping("/initWishlist")
    public String initWishlist(@RequestParam String listName, @CookieValue(value = CookieNames.USERNAME) String login_username, boolean isPublic, @CookieValue(value = CookieNames.PASSWORD) String login_password) {
        if (!firebaseService.verifyUser(login_username, login_password)) {
            return "Invalid Login";
        }
        try {
            List<WishList> myWishlists = firebaseService.getAllWishLists(login_username);
            for(WishList wishList : myWishlists){
                if (wishList.getListName().equals(listName)){
                    return "Cannot have two lists with the same name!";
                }
            }
            firebaseService.saveWishListDetails(new WishList(login_username, listName, new ArrayList<>(), isPublic));
        } catch (ExecutionException e) {
            return "Error";
        } catch (InterruptedException e) {
            return "Error";
        }
        return "Added successfully.";
    }

    //If no params, then they should show all items for a specific user that is logged in. If search it should search the db. if list it will return all the items in said wishlist.
    @GetMapping("/items")
    public List<Item> items(@RequestParam Optional<String> search, @RequestParam Optional<String> list, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        List<Item> myItems = new ArrayList<>();
        if (login_username != null && login_password != null && firebaseService.verifyUser(login_username, login_password)) {
            try {

                //get all the wishlists for the logged in user
                List<WishList> myWishlists = firebaseService.getAllWishLists(login_username);

                //if no params, add all items for all the user's wishlists
                if (search.isEmpty() && list.isEmpty()) {
                    for (WishList x : myWishlists) {
                        myItems.addAll(x.getItems());
                    }
                //if the list parameter is specified, return all the items for the user's list that was specified
                } else if (!list.isEmpty()) {
                    for (WishList x : myWishlists) {
                        if (x.getListName().equals(list.get())) {
                            myItems.addAll(x.getItems());
                        }
                    }

                //if search parameter is specified, look for all wishlists where the search parameter is inside the wishlist name
                } else if (!search.isEmpty()) {
                    for (WishList x : myWishlists) {
                        for (Item y : x.getItems()) {
                            if (y.getName().toLowerCase().contains(search.get().toLowerCase())) {
                                myItems.add(y);
                            }
                        }
                    }
                }
            } catch (ExecutionException e) {

            } catch (InterruptedException e) {

            }
        }

        return myItems;
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

        return Arrays.asList(new WishList("guillermo@gflores.dev", "list 1", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"), new Item(20.00, "galaxy buds 2", "item url", "image")), true), new WishList("guillermo@gflores.dev", "list 2", Arrays.asList(new Item(10.00, "galaxy buds", "itme url", "image"), new Item(20.00, "galaxy buds 2", "item url", "image")), true));
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
