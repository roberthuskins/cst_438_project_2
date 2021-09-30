package csumb.edu.project2.objects;

import org.springframework.stereotype.Component;

@Component
public class Items {

    private int Price;
    private String Name;
    private String URL;


    public Items() {
        super();
    }

    public Items(int price, String name, String URL) {
        Price = price;
        Name = name;
        this.URL = URL;
    }

    public int getPrice() {
        return Price;
    }

    public void setPrice(int price) {
        Price = price;
    }

    public String getName() {
        return Name;
    }

    public void setName(String name) {
        Name = name;
    }

    public String getURL() {
        return URL;
    }

    public void setURL(String URL) {
        this.URL = URL;
    }
}
