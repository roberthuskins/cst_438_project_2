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
import java.net.URI;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.Map;

@Controller
public class WebController {
    @Autowired URLFetcher urlFetcher;

    @RequestMapping("/")
    public String index(Model model, HttpServletRequest request) throws IOException {
        String apiURL = urlFetcher.getUrl() + "/wishlists";
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
        String apiURL = urlFetcher.getUrl() + "/items";
        URL url = new URL(apiURL);
        URLConnection req = url.openConnection();

        String apiurl_wishlist = urlFetcher.getUrl() + "/wishlists";
        URL url_wishlist = new URL(apiurl_wishlist);
        URLConnection req_wishlist = url_wishlist.openConnection();

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
            req_wishlist.setRequestProperty("Cookie", cookieValues);
        }
        else {
            //I would imagine you redirect to login page here, if we reach here it means that the user has no cookies;
            return "redirect:/signin";
        }

        req.connect();
        req_wishlist.connect();

        // Convert to a JSON object to print data
        JsonParser jp_wish = new JsonParser(); //from gson
        JsonElement root_wish = jp_wish.parse(new InputStreamReader((InputStream) req_wishlist.getContent())); //Convert the input stream to a json element
        JsonArray rootobj_wish = root_wish.getAsJsonArray();
        ArrayList<String> listNames = new ArrayList<>();
        for(JsonElement obj: rootobj_wish){
            //this removes double quotes from the string returned in the JSON response.
            listNames.add(obj.getAsJsonObject().get("listName").toString().replace("\"", ""));
        }
        System.out.println("ERIK LISTNAMES: " + listNames);
        model.addAttribute("listNames", listNames);

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        ArrayList<Map<String, String>> listItems = new ArrayList<>();
        for(JsonElement obj: rootobj){
            String n, p, s, i = "";
            n = obj.getAsJsonObject().get("name").toString().replace("\"", "");
            p = obj.getAsJsonObject().get("price").toString().replace("\"", "");
            s = obj.getAsJsonObject().get("shopURL").toString().replace("\"", "");
            i = obj.getAsJsonObject().get("imageURL").toString().replace("\"", "");
            Map<String, String> itemsInList = Map.of("name", n, "price", p, "shopURL",s, "imageURL", i);
            listItems.add(itemsInList);
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