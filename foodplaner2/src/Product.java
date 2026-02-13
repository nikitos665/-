import java.io.Serializable;

public class Product implements Serializable {
    private String name;
    private double price;
    private String day;
    private boolean purchased;
    private int quantity;
    private double totalPrice;

    public Product(String name, double price, String day) {
        this.name = name;
        this.price = price;
        this.day = day;
        this.purchased = false;
        this.quantity = 1;
        this.totalPrice = price;
    }

    // Геттеры и сеттеры
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public double getPrice() { return price; }
    public void setPrice(double price) {
        this.price = price;
        updateTotalPrice();
    }

    public String getDay() { return day; }
    public void setDay(String day) { this.day = day; }

    public boolean isPurchased() { return purchased; }
    public void setPurchased(boolean purchased) { this.purchased = purchased; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) {
        this.quantity = quantity;
        updateTotalPrice();
    }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    private void updateTotalPrice() {
        this.totalPrice = this.price * this.quantity;
    }

    @Override
    public String toString() {
        if (quantity > 1) {
            return name + " - " + quantity + " шт x " + price + " руб. = " + totalPrice + " руб.";
        }
        return name + " - " + price + " руб.";
    }
}