package csumb.edu.project2.controller;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;

@Controller
public class WebController {
    @RequestMapping("/")
    public String index(Model model) throws IOException {
        String apiURL = "http://localhost:8080/wishlists";

        URL url = new URL(apiURL);
        URLConnection request = url.openConnection();
        request.connect();

        // Convert to a JSON object to print data
        JsonParser jp = new JsonParser(); //from gson
        JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
        JsonArray rootobj = root.getAsJsonArray();
        System.out.println(rootobj);
        ArrayList<JsonElement> listNames = new ArrayList<>();
        for(JsonElement obj: rootobj){
            listNames.add(obj.getAsJsonObject().get("listName"));
        }
        model.addAttribute("listNames", listNames);

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

}