/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package products;

/**
 *
 * @author lonelyprogrammer
 */
public abstract class Products {
    
    protected String productname;
    protected double puhunan;
    protected String productType;
    protected int caseQuantity;
    protected int quantityPerCase;
    protected int remainingQuantities;
    protected double itemTubo;
    protected double pricePerItem, pricePerCase;
    protected int remainingCase, remainingBottles;
    protected boolean canBuyBulk;

    public boolean isCanBuyBulk() {
        return canBuyBulk;
    }

    public void setCanBuyBulk(boolean canBuyBulk) {
        this.canBuyBulk = canBuyBulk;
    }

    public String getProductname() {
        return productname;
    }

    public void setProductname(String productname) {
        this.productname = productname;
    }

    public int getQuantityPerCase() {
        return quantityPerCase;
    }

    public void setQuantityPerCase(int quantityPerCase) {
        this.quantityPerCase = quantityPerCase;
    }

    public int getRemainingQuantities() {
        return remainingQuantities;
    }

    public void setRemainingQuantities(int remainingQuantities) {
        this.remainingQuantities = remainingQuantities;
    }

    public double getItemTubo() {
        return itemTubo;
    }

    public void setItemTubo(double itemTubo) {
        this.itemTubo = itemTubo;
    }

    public int getRemainingCase() {
        return remainingCase;
    }

    public void setRemainingCase(int remainingCase) {
        this.remainingCase = remainingCase;
    }

    public int getRemainingBottles() {
        return remainingBottles;
    }

    public void setRemainingBottles(int remainingBottles) {
        this.remainingBottles = remainingBottles;
    }
    
    public void setName(String name) {
       
        this.productname = name;
    }

    public void setPuhunan(double price) {
       
        this.puhunan = price;
    }

    public void setCaseQuantity(int quantity) {
        
        this.caseQuantity = quantity;
    }

    public void setquantityPerCase(int quantity) {
        
        this.quantityPerCase = quantity;
    }

    public String getName() {
        return this.productname;
    }

    public double getPuhunan() {
        return this.puhunan;
    }

    public int getcaseQuantity() {
        return this.caseQuantity;
    }

    public int getquantityPerCase() {
        return this.quantityPerCase;
    }
    
    public void setitemTubo(double tubo){
    
        this.itemTubo = tubo;
    
    }
    
    public double getTubo(){
    
        return itemTubo;
    
    }
    
    public void setProductType(String name){
    
        this.productType = name;
    
    }
    
    public String getProductType(){
    
        return this.productType;
    
    }
    
    public void setPricePerItem(double price){
    
        this.pricePerItem = price;
    
    }
    
    public double getPricePerItem(){
    
        return this.pricePerItem;
    
    }
    
    public void setPricePerCase(double price){
    
        this.pricePerCase = price;
    
    }
    
    public double getPricePerCase(){
    
    
        return this.pricePerCase;
    }
    
}
