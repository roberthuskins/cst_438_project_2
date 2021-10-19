package csumb.edu.project2.Heroku;

import org.springframework.stereotype.Service;

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
}
