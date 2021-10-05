package csumb.edu.project2.objects;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WishList {

    private String username;
    private String listName;
    private List<Item> items;

    public WishList() {
        super();
    }

    public WishList(String username, String listName, List<Item> items) {
        this.username = username;
        this.listName = listName;
        this.items = items;
    }

    public String getListName() {
        return listName;
    }

    public void setListName(String listName) {
        this.listName = listName;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public List<Item> getItems() {
        return items;
    }

    public void setItems(List<Item> items) {
        this.items = items;
    }
}
