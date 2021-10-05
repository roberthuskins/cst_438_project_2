package csumb.edu.project2.objects;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WishList {


    private String username;
    private List<ArrayList> items;

    public WishList() {
        super();
    }

    public WishList(String username, List<ArrayList> items) {
        this.username = username;
        this.items = items;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<ArrayList> getItems() {
        return items;
    }

    public void setItems(List<ArrayList> items) {
        this.items = items;
    }
}
