package csumb.edu.project2.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import csumb.edu.project2.Heroku.URLFetcher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@Controller
public class WebController {
    @Autowired URLFetcher urlFetcher;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) throws IOException {
        String apiURL = urlFetcher.getUrl() + "/wishlists";
        System.out.println(apiURL);
        URL url = new URL(apiURL);
        URLConnection req = url.openConnection();

        /*
        First we get the cookies from the request. then we append them to a string in a certain format that
        URLConnection wants
         */
        String cookieValues = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i=0; i < cookies.length; i++) {
                cookieValues += cookies[i].getName() + "=" + cookies[i].getValue();

                if (i != cookies.length -1) {
                    cookieValues+=";";
                }
            }
            //this appends the cookies to the request that we are about to make with req.connect()
            req.setRequestProperty("Cookie", cookieValues);
        }
        else {
            //I would imagine you redirect to login page here, if we reach here it means that the user has no cookies;
            return "login";
        }


        req.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        System.out.println(rootobj);
        ArrayList<String> listNames = new ArrayList<>();
        for(JsonElement obj: rootobj){
            //this removes double quotes from the string returned in the JSON response.
            listNames.add(obj.getAsJsonObject().get("listName").toString().replace("\"", ""));
            System.out.println(obj.getAsJsonObject().get("listName").toString());
        }
        model.addAttribute("listNames", listNames);

        return "index";
    }

    @RequestMapping("/myitems")
    public String items(Model model, HttpServletRequest request) throws IOException {
        String apiURL = "https://radiant-cliffs-80770.herokuapp.com/items";

        URL url = new URL(apiURL);
        URLConnection req = url.openConnection();
        String cookieValues = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i=0; i < cookies.length; i++) {
                cookieValues += cookies[i].getName() + "=" + cookies[i].getValue();
                if (i != cookies.length -1) {
                    cookieValues+=";";
                }
            }
            //this appends the cookies to the request that we are about to make with req.connect()
            req.setRequestProperty("Cookie", cookieValues);
        }
        else {
            //I would imagine you redirect to login page here, if we reach here it means that the user has no cookies;
            return "items";
        }

        req.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        System.out.println("ERIKS:"+rootobj);
        ArrayList<JsonElement> listItems = new ArrayList<>();
        for(JsonElement obj: rootobj){
            listItems.add(obj.getAsJsonObject());
        }

        model.addAttribute("listItems", listItems);

        return "items";
    }

    @RequestMapping("/register")
    public String register(){
        return "register";
    }

    @RequestMapping("/signin")
    public String login(){
        return "login";
    }


}