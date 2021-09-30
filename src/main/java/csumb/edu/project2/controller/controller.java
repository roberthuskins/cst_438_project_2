package csumb.edu.project2.controller;

import csumb.edu.project2.firebase.FirebaseService;
import csumb.edu.project2.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ExecutionException;

@Controller
public class controller {

    @RequestMapping("/")
    public String index() {
        return "index";
    }

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @RequestMapping("/signin")
    public String login(){
        return "login";
    }

    @PostMapping("/createUser")
    public String postExample(@RequestBody User user) throws InterruptedException, ExecutionException {
        return "Created User " + user.getUsername();
    }

    @PutMapping("/updateUser")
    public String putExample(@RequestBody User user) throws InterruptedException, ExecutionException {
        return "Updated User" + user.getUsername();
    }

    @DeleteMapping("/deleteUser")
    public String deleteExample(@RequestHeader String name) {
        return "Deleted User " + name;
    }

}