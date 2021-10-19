package csumb.edu.project2.objects;

import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class WishList {


    private String username;
    private String listName;
    private List<Item> items;
    private boolean isPublic;

    public WishList() {
        super();
    }

    public WishList(String username, String listName, List<Item> items, boolean isPublic) {
        this.username = username;
        this.listName = listName;
        this.items = items;
        this.isPublic = isPublic;
    }

    public boolean getIsPublic() {
        return isPublic;
    }

    public void setIsPublic(boolean isPublic) {
        this.isPublic = isPublic;
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
