/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

/**
 *
 * @author lonelyprogrammer
 */
public class CustomerDebt {
    
    private String productName;
    private int quantity;
    private double total;
    private String date;

    public CustomerDebt(){}
    public CustomerDebt(String productName, int quantity, double total, String date) {
        this.productName = productName;
        this.quantity = quantity;
        this.total = total;
        this.date = date;
    }
    
    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
    
    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
    
}
