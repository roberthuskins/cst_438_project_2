package csumb.edu.project2.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import csumb.edu.project2.Heroku.URLFetcher;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.Cookie;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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

        if (urlFetcher.checkLoginCookies(cookies)) {
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
            return "redirect:/signin";
        }


        req.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        ArrayList<String> listNames = new ArrayList<>();
        for(JsonElement obj: rootobj){
            //this removes double quotes from the string returned in the JSON response.
            listNames.add(obj.getAsJsonObject().get("listName").toString().replace("\"", ""));
        }
        boolean isListEmpty = true;
        if (listNames.size()>0){
            isListEmpty = false;
        }
        model.addAttribute("isListEmpty", isListEmpty);
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

        if (urlFetcher.checkLoginCookies(cookies)) {
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

    @RequestMapping("/wishlist")
    public String wishlist(Model model, HttpServletRequest request, @RequestParam(value = "list") String listName) throws IOException {
        //Sanity check never hurts anyone
        if(listName == null){
            return "redirect:/";
        }
        //if the name contains a space (ex: "baby shower") we replace the space with the necessary %20 to complete the request
        if(listName.contains(" ")){
            listName = listName.replaceAll(" ", "%20");
        }
        String apiURL = urlFetcher.getUrl() + "/wishlists?list="+listName;
        URL url = new   URL(apiURL);
        URLConnection req = url.openConnection();

        /*
        First we get the cookies from the request. then we append them to a string in a certain format that
        URLConnection wants
         */
        String cookieValues = "";
        Cookie[] cookies = request.getCookies();

        if (urlFetcher.checkLoginCookies(cookies)) {
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
            return "redirect:/";
        }


        req.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        ArrayList<Map<String,String>> itemsInList = new ArrayList<>();
        JsonElement items = rootobj.getAsJsonArray().get(0).getAsJsonObject().get("items");
        //grabs the name of the list, so we can display it
        String currentListName = rootobj.getAsJsonArray().get(0).getAsJsonObject().get("listName").toString();
        //removes quotes
        currentListName = currentListName.replace("\"","");
        for(JsonElement item: items.getAsJsonArray()){
            //this removes double quotes from the string returned in the JSON response.
            String n, p, s, i, e = "";
            n = item.getAsJsonObject().get("name").toString().replace("\"", "");
            e = n.replaceAll(" ","-");
            p = item.getAsJsonObject().get("price").toString().replace("\"", "");
            s = item.getAsJsonObject().get("shopURL").toString().replace("\"", "");
            i = item.getAsJsonObject().get("imageURL").toString().replace("\"", "");
            //'e' adds a modal friendly name (for ID's sake)
            Map<String, String> myItem = Map.of("name", n, "price", p, "shopURL",s, "imageURL", i, "modal",e);
            itemsInList.add(myItem);
        }
        boolean isListEmpty = true;
        if (itemsInList.size() > 0){
            isListEmpty = false;
        }
        model.addAttribute("isListEmpty", isListEmpty);
        model.addAttribute("itemsInList", itemsInList);
        model.addAttribute("listName", currentListName);
        return "wishlist";
    }

    @RequestMapping("/searchResults")
        public String results(Model model, HttpServletRequest request, @RequestParam String search) throws IOException {
        String apiURL = urlFetcher.getUrl() + "/wishlists?search="+search;
        URL url = new URL(apiURL);
        URLConnection req = url.openConnection();
        String cookieValues = "";
        Cookie[] cookies = request.getCookies();

        if (urlFetcher.checkLoginCookies(cookies)) {
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
            return "redirect:/signin";
        }

        req.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        ArrayList<String> listNames = new ArrayList<>();
        for(JsonElement obj: rootobj){
            //this removes double quotes from the string returned in the JSON response.
            listNames.add(obj.getAsJsonObject().get("listName").toString().replace("\"", ""));
        }
        boolean isListEmpty = true;
        if (listNames.size()>0){
            isListEmpty = false;
        }
        model.addAttribute("isListEmpty", isListEmpty);
        model.addAttribute("listNames", listNames);

        return "results";
    }

    @RequestMapping("/administrator")
    public String admin(Model model, HttpServletRequest request) throws IOException {

        String apiURL = urlFetcher.getUrl() + "/users";
        URL url = new URL(apiURL);
        URLConnection req = url.openConnection();

        String cookieValues = "";
        Cookie[] cookies = request.getCookies();

        if (urlFetcher.checkLoginCookies(cookies)) {
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
            return "redirect:/signin";
        }

        req.connect();
        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) req.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        ArrayList<Map<String, String>> listUsers = new ArrayList<>();
        for(JsonElement obj: rootobj){
            String n, p = "";
            n = obj.getAsJsonObject().get("username").toString().replace("\"", "");
            p = obj.getAsJsonObject().get("password").toString().replace("\"", "");
            if(n.equals("ADMIN@ADMIN.COM") && p.equals("ADMIN")){
                continue;
            }
            Map<String, String> itemsInList = Map.of("username", n, "password", p);
            listUsers.add(itemsInList);
        }
        model.addAttribute("listUsers", listUsers);
        return "admin";
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