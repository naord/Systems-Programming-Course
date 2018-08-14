package bgu.spl181.net.impl.MovieRentalService;

import java.util.ArrayList;
import java.util.List;

public class MovieTemplate {
    private String id;
    private String name;
    private int price;
    private List<String> bannedCountries = new ArrayList<String>();
    private int availableAmount;
    private int totalAmount;

    public MovieTemplate(String id, String name, int price, ArrayList bannedCountries, int availableAmount, int totalAmount){
        this.id = id;
        this.name = name;
        this.price = price;
        this.bannedCountries = bannedCountries;
        this.availableAmount = availableAmount;
        this.totalAmount = totalAmount;
    }

    public String getName() {
        return name;
    }

    public String getId() {
            return id;
    }

    synchronized public int getAvailableAmount() {
        return availableAmount;
    }

    public int getPrice() {
        return price;
    }

    public int getTotalAmount() {
        return totalAmount;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public void reduceAvailableAmount(){
        this.availableAmount--;
    }

    public void increaseAvailableAmount() {
        this.availableAmount++;
    }

    public List<String> getBannedCountries() {
        return bannedCountries;
    }
}
