/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package products;

/**
 *
 * @author lonelyprogrammer
 */
public class PureDrinks extends Products{

    
    public PureDrinks(){}
   
    public PureDrinks(String name, String productType, double price, int caseQuantity,
        int quantityPerCase,double itemTubo, double pricePerItem
        ,int remainingCase,int remainingBottles){
    
        this.productname = name;
        this.productType = productType;
        this.puhunan = price;
        this.caseQuantity = caseQuantity;
        this.quantityPerCase = quantityPerCase;
        this.remainingBottles = remainingBottles;
        this.itemTubo = itemTubo;
        this.pricePerItem = pricePerItem;
        this.remainingBottles = remainingBottles;
        this.canBuyBulk = false;
    }
}
