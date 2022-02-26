

package misc;

/**
 *
 * @author lonelyprogrammer
 */
public class Transactions {
    
    private String month;
    private int date;
    private int year;
    private String productName;
    private double price;
    private int quantity;
    private double totalBalance;
    private String time;
    private double profit;

    public double getProfit() {
        return profit;
    }

    public void setProfit(double profit) {
        this.profit = profit;
    }

    public Transactions(String month, int date, int year, String time, String productName, double price, int quantity) {
        this.month = month;
        this.date = date;
        this.year = year;
        this.productName = productName;
        this.price = price;
        this.quantity = quantity;
        this.time = time;
    }
    
    public double getTotalBalance() {
        return totalBalance;
    }

    public void setTotalBalance(double totalBalance) {
        this.totalBalance = totalBalance;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }
    
    public Transactions(){
    
        
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public int getDate() {
        return date;
    }

    public void setDate(int date) {
        this.date = date;
    }
    
    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }
    
}
