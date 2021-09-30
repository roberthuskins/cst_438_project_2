package csumb.edu.project2.objects;

import org.springframework.stereotype.Component;

@Component
public class Item {

    private int price;
    private String name;
    private String shopURL;
    private String imageURL;

    public Item() {
        super();
    }

    public Item(int price, String name, String shopURL, String imageURL) {
        this.price = price;
        this.name = name;
        this.shopURL = shopURL;
        this.imageURL = imageURL;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getShopURL() {
        return shopURL;
    }

    public void setShopURL(String shopURL) {
        this.shopURL = shopURL;
    }
}