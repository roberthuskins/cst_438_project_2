package csumb.edu.project2.Heroku;

import csumb.edu.project2.objects.CookieNames;
import org.springframework.stereotype.Service;

import javax.servlet.http.Cookie;

@Service
public class URLFetcher {
    private String url;

    public URLFetcher() {
        if (System.getenv("ON_HEROKU")!= null) {
            this.url = "https://radiant-cliffs-80770.herokuapp.com";
        } else {
            this.url = "http://localhost:8080";
        }
    }

    public String getUrl() {
        return this.url;
    }

    public boolean checkLoginCookies(Cookie[] cookies){
        if (cookies == null){
            return false;
        }
        boolean hasUsername = false;
        boolean hasPassword = false;
        for (Cookie cookie : cookies){
            if (cookie.getName().equals(CookieNames.USERNAME)){
                hasUsername =  true;
            }
            if (cookie.getName().equals(CookieNames.PASSWORD)){
                hasPassword = true;
            }
        }
        return hasPassword && hasUsername;
    }
}
