/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package products;

/**
 *
 * @author lonelyprogrammer
 */
public class Beer extends Products{

    public Beer(){}
    public Beer(String name, String productType, double price, int caseQuantity,
            int quantityPerCase,double itemTubo, double pricePerItem, double pricePerCase, int remainingCase,
            int remainingBottles){
    
        this.productname = name.toUpperCase().trim();
        this.productType = productType;
        this.puhunan = price;
        this.caseQuantity = caseQuantity;
        this.quantityPerCase = quantityPerCase;
        this.itemTubo = itemTubo;
        this.pricePerItem = pricePerItem;
        this.pricePerCase = pricePerCase;
        this.remainingBottles = remainingBottles;
        this.canBuyBulk = true;
    }
}
