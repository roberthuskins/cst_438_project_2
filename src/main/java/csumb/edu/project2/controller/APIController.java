package csumb.edu.project2.controller;

import csumb.edu.project2.Heroku.URLFetcher;
import csumb.edu.project2.firebase.FirebaseService;
import csumb.edu.project2.objects.CookieNames;
import csumb.edu.project2.objects.Item;
import csumb.edu.project2.objects.User;
import csumb.edu.project2.objects.WishList;
import org.apache.coyote.Response;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
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
    public ResponseEntity<Object> createNewUser(@RequestParam String username, @RequestParam String password, HttpServletResponse response) throws IOException {
        //first we are going to check if the username exists in the DB, if so reroute them to the login page
        try {
            if (userExists(username)) {
                HttpHeaders userExistsReroute = new HttpHeaders();
                userExistsReroute.setLocation(URI.create("/signin"));
                return new ResponseEntity<>(userExistsReroute, HttpStatus.MOVED_PERMANENTLY);
            } else ;
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return null;
        } catch (InterruptedException e) {
            return null;
        }
        //register worked correctly and user is now signed in
        // Redirect code credit: https://stackoverflow.com/a/47411493
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        response.addCookie(new Cookie(CookieNames.USERNAME, username));
        response.addCookie(new Cookie(CookieNames.PASSWORD, password));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    private boolean userExists(String username) {
        try {
            List<User> users = firebaseService.getAllUsers();
            for (User user : users) {
                if (user.getUsername().equals(username)) {
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
            System.out.println("ERIK ADMIN: " + username + " " + password);
            if(firebaseService.verifyAdmin(username, password)) {
                HttpHeaders headers = new HttpHeaders();
                headers.setLocation(URI.create("/administrator"));
                //set their cookies when the user calls the api/makes post request to this endpoint
                response.addCookie(new Cookie(CookieNames.USERNAME, username));
                response.addCookie(new Cookie(CookieNames.PASSWORD, password));
                return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
            }
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
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(HttpServletRequest req, HttpServletResponse response) {
        //https://stackoverflow.com/questions/890935/how-do-you-remove-a-cookie-in-a-java-servlet
        Cookie[] cookies = req.getCookies();
        if (cookies != null)
            for (Cookie cookie : cookies) {
                cookie.setValue("");
                cookie.setPath("/");
                cookie.setMaxAge(0);
                response.addCookie(cookie);
            }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/signin"));
        return new ResponseEntity<>(headers, HttpStatus.MOVED_PERMANENTLY);
    }

    //deletes the user
    @DeleteMapping("/logout")
    public ResponseEntity<?> deleteUser(@RequestParam String username, @RequestParam String password) {
        try {
            firebaseService.deleteUser(new User(username, password));
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Creates a wishlist in our database with a blank item list to be filled by additems
     *
     * @param listName
     * @param login_username
     * @param login_password
     * @return
     */
    @PostMapping("/initWishlist")
    public ResponseEntity<?> initWishlist(@RequestParam String listName, @CookieValue(value = CookieNames.USERNAME) String login_username, boolean isPublic, @CookieValue(value = CookieNames.PASSWORD) String login_password) {
        if (!firebaseService.verifyUser(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        try {
            List<WishList> myWishlists = firebaseService.getAllWishLists(login_username);
            for (WishList wishList : myWishlists) {
                if (wishList.getListName().equals(listName)) {
                    return new ResponseEntity<>(HttpStatus.CONFLICT);
                }
            }
            firebaseService.saveWishListDetails(new WishList(login_username, listName, new ArrayList<>(), isPublic));
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    //If no params, then they should show all items for a specific user that is logged in. If search it should search the db. if list it will return all the items in said wishlist.
    @GetMapping("/items")
    public ResponseEntity<?> items(@RequestParam Optional<String> search, @RequestParam Optional<String> list, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        List<Item> myItems = new ArrayList<>();

        try {

            //check login
            if (!firebaseService.verifyUser(login_username, login_password)) {
                return new ResponseEntity<>(HttpStatus.FORBIDDEN);
            }

            //if search and list not specified, return all items from all the user's wishlists
            if (search.isEmpty() && list.isEmpty()) {
                List<WishList> myWishlists = firebaseService.getAllWishLists(login_username);

                for (WishList x : myWishlists) {
                    myItems.addAll(x.getItems());
                }

                return new ResponseEntity<>(myItems, HttpStatus.OK);

            }
            //if search parameter is specified, return all items that either belong to the user/are public that match the search term
            else if (!search.isEmpty()) {
                List<WishList> myWishlists = firebaseService.getAllWishLists();

                for (WishList x : myWishlists) {
                    if (x.getIsPublic() || x.getUsername().equals(login_username)) {
                        for (Item y : x.getItems()) {
                            if (y.getName().toLowerCase().contains(search.get().toLowerCase())) {
                                myItems.add(y);
                            }
                        }
                    }
                }

                return new ResponseEntity<>(myItems, HttpStatus.OK);

            }
            //if list parameter is specified, return all the items from a wishlist that the user owns/is public that matches the list name
            else if (!list.isEmpty()) {

                List<WishList> myWishlists = firebaseService.getAllWishlistsWithListName(list.get());

                for (WishList x : myWishlists) {
                    if (x.getIsPublic() || x.getUsername().equals(login_username)) {
                        myItems.addAll(x.getItems());
                    }
                }
                return new ResponseEntity<>(myItems, HttpStatus.OK);
            }

        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //add item
    @PostMapping("/items")
    public ResponseEntity<?> addItem(@RequestParam String item_name, @RequestParam String list, @RequestParam Optional<Double> price, @RequestParam Optional<String> url, @RequestParam Optional<String> imageurl, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        if(!firebaseService.verifyUser(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<WishList> myWishlists;
        try {
            myWishlists = firebaseService.getAllWishLists(login_username);

            for(WishList x : myWishlists) {
                if(x.getListName().equals(list)) {
                    if(imageurl.get().equals("")){
                        imageurl = Optional.of("https://calgarylegacy.ca/wp-content/uploads/2020/02/480px-No_image_available.svg_.png");
                    }
                    Item myItem = new Item(price.get(),item_name,url.get(), imageurl.get());
                    //this is pass by reference so should work
                    x.getItems().add(myItem);
                    firebaseService.updateWishListDetails(x);

                    HttpHeaders headers = new HttpHeaders();
                    if(list.contains(" ")){
                        list = list.replaceAll(" ", "%20");
                    }
                    headers.setLocation(URI.create("/wishlist?list="+list));
                    return new ResponseEntity<>(headers, HttpStatus.FOUND);
                }
            }

        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @PatchMapping("/items")
    public ResponseEntity<?> updateItem(@RequestParam String item_name, @RequestParam String list_name, @RequestParam Optional<Double> price, @RequestParam Optional<String> shopURL, @RequestParam Optional<String> imageURL, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "" ) String login_password ){
        if(!firebaseService.verifyUser(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<WishList> myWishlists;
        try {
            myWishlists = firebaseService.getAllWishLists(login_username);
            for (WishList x : myWishlists) {
                if (x.getListName().equals(list_name)) {
                    //not sure if this is a thing with 'Optional' but a comma is being inserted into our things, so to actually test for equality we need to remove it, and to properly update
                    item_name = item_name.replace(",","");
                    String tempShopURL = shopURL.get().replace(",","");
                    String tempImageURL = imageURL.get().replace(",","");
                    for (Item y : x.getItems()) {
                        if ((y.getName()).equalsIgnoreCase(item_name)) {
                            if (price.isPresent()) {
                                y.setPrice(price.get());
                            }

                            if (shopURL.isPresent()) {
                                y.setShopURL(tempShopURL);
                            }

                            if (imageURL.isPresent()) {
                                if(tempImageURL.isEmpty()){
                                    String placeholderImg = "https://calgarylegacy.ca/wp-content/uploads/2020/02/480px-No_image_available.svg_.png";
                                    y.setImageURL(placeholderImg);
                                } else {
                                    y.setImageURL(tempImageURL);
                                }
                            }

                            firebaseService.updateWishListDetails(x);
                            HttpHeaders headers = new HttpHeaders();
                            if(list_name.contains(" ")){
                                list_name = list_name.replaceAll(" ", "%20");
                            }
                            headers.setLocation(URI.create("/wishlist?list="+list_name));
                            return new ResponseEntity<>(headers, HttpStatus.FOUND);
                        }
                    }
                }
            }
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    @DeleteMapping("/items")
    public ResponseEntity<?> deleteItem(@RequestParam String item_name, @RequestParam String list_name, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "" ) String login_password ){
        if(!firebaseService.verifyUser(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }
        List<WishList> myWishlists;
        try {
            myWishlists = firebaseService.getAllWishLists(login_username);

            for (WishList x : myWishlists) {
                if(x.getListName().equalsIgnoreCase(list_name)) {
                    for(int i = 0; i < x.getItems().size(); i++) {
                        if (x.getItems().get(i).getName().equalsIgnoreCase(item_name)) {
                            x.getItems().remove(i);
                            firebaseService.updateWishListDetails(x);
                            firebaseService.updateWishListDetails(x);
                            HttpHeaders headers = new HttpHeaders();
                            if(list_name.contains(" ")){
                                list_name = list_name.replaceAll(" ", "%20");
                            }
                            headers.setLocation(URI.create("/wishlist?list="+list_name));
                            return new ResponseEntity<>(headers, HttpStatus.FOUND);

                        }
                    }
                }
            }

        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
    }

    //If no params, then they should show all wish lists for a specific user that is logged in. If search it should search the db. if list it will return all the items in said wishlist.
    @GetMapping("/wishlists")
    public ResponseEntity<?> wishlists(@RequestParam Optional<String> search, @RequestParam Optional<String> list, @RequestParam Optional<String> key, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        //we read the cookies through the @CookieValues. Note that these cookies were not passed in directly through the browser. It was the web controller that transferred the cookies through the browser to the APIController.
        if (!firebaseService.verifyUser(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            //if no parameters, return all wishlists for a specific user.
            if (search.isEmpty() && list.isEmpty()) {
                return new ResponseEntity<>(firebaseService.getAllWishLists(login_username), HttpStatus.OK);
            }

            //if search parameter is specified, return wishlists that are public or belong to the user that matches.
            else if (!search.isEmpty()) {
                List<WishList> myList = firebaseService.getAllWishLists();

                List<WishList> out = new ArrayList<>();
                for (WishList x : myList) {
                    if (x.getListName().toLowerCase().contains(search.get().toLowerCase())) {
                        if (x.getIsPublic() || x.getUsername().equals(login_username)) {
                            out.add(x);
                        }

                    }
                }

                return new ResponseEntity<>(out, HttpStatus.OK);

                //if list parameter is specified, then return all lists that match the name and are either public or belong to the user
            } else if (!list.isEmpty()) {
                List<WishList> myList = firebaseService.getAllWishlistsWithListName(list.get());

                List<WishList> out = new ArrayList<>();

                for (WishList x : myList) {
                    if (x.getIsPublic() || x.getUsername().equals(login_username)) {
                        out.add(x);
                    }
                }
                return new ResponseEntity<>(out, HttpStatus.OK);
            }
        } catch (ExecutionException e) {

        } catch (InterruptedException e) {

        }

        return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
    }


    /* Admin endpoints go below here */

    @GetMapping("/users")
    public ResponseEntity<?> users(@CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        if(!firebaseService.verifyAdmin(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        List<User> users;
        try {
            users = firebaseService.getAllUsers();
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        return new ResponseEntity<>(users, HttpStatus.OK);
    }

    @PutMapping("/users")
    public ResponseEntity<?> createUser(@RequestParam String username, @RequestParam String password, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        if(!firebaseService.verifyAdmin(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            firebaseService.saveUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/administrator"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);

    }

    @PatchMapping("/users")
    public ResponseEntity<?> updateUser(@RequestParam String username, @RequestParam String password, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        if(!firebaseService.verifyAdmin(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            firebaseService.updateUserDetails(new User(username, password));
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/administrator"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

    @DeleteMapping("/users")
    public ResponseEntity<?> deleteUser(@RequestParam String username, @CookieValue(value = CookieNames.USERNAME, defaultValue = "") String login_username, @CookieValue(value = CookieNames.PASSWORD, defaultValue = "") String login_password) {
        if(!firebaseService.verifyAdmin(login_username, login_password)) {
            return new ResponseEntity<>(HttpStatus.FORBIDDEN);
        }

        try {
            //Deleting user: delete user and wishlist
            List <WishList> myWishlist = firebaseService.getAllWishLists(username);
            for (WishList x: myWishlist){
                firebaseService.deleteWishList(x);
            }
            User myUser = firebaseService.getUserDetails(username);
            firebaseService.deleteUser(myUser);
        } catch (ExecutionException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        } catch (InterruptedException e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setLocation(URI.create("/administrator"));
        return new ResponseEntity<>(headers, HttpStatus.FOUND);
    }

}