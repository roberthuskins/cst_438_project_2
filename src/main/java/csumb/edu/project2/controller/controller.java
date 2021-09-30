package csumb.edu.project2.controller;

import csumb.edu.project2.firebase.FirebaseService;
import csumb.edu.project2.objects.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

//@Controller
//public class controller {
//
//    @RequestMapping("/")
//    public String index() {
//        return "index";
//    }
//
//    @RequestMapping("/register")
//    public String register(){
//        return "register";
//    }
//
//    @RequestMapping("/signin")
//    public String login(){
//        return "login";
//    }
//
//
//
////    @PostMapping("/createUser")
////    public String postExample(@RequestBody User user) throws InterruptedException, ExecutionException {
////        return "Created User " + user.getUsername();
////    }
////
////    @PutMapping("/updateUser")
////    public String putExample(@RequestBody User user) throws InterruptedException, ExecutionException {
////        return "Updated User" + user.getUsername();
////    }
////
////    @DeleteMapping("/deleteUser")
////    public String deleteExample(@RequestHeader String name) {
////        return "Deleted User " + name;
//    }
//
//}

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

@Controller
public class controller{

    @GetMapping("/")
    public String process(Model model, HttpSession session) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) session.getAttribute("MY_SESSION_MESSAGES");

        if (messages == null) {
            messages = new ArrayList<>();
        }
        model.addAttribute("sessionMessages", messages);

        return "index";
    }

    @PostMapping("/persistMessage")
    public String persistMessage(@RequestParam("msg") String msg, HttpServletRequest request) {
        @SuppressWarnings("unchecked")
        List<String> messages = (List<String>) request.getSession().getAttribute("MY_SESSION_MESSAGES");
        if (messages == null) {
            messages = new ArrayList<>();
            request.getSession().setAttribute("MY_SESSION_MESSAGES", messages);
        }
        messages.add(msg);
        request.getSession().setAttribute("MY_SESSION_MESSAGES", messages);
        return "redirect:/";
    }

    @PostMapping("/destroy")
    public String destroySession(HttpServletRequest request) {
        request.getSession().invalidate();
        return "redirect:/";
    }
}