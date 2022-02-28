/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;

import java.util.ArrayList;
import products.Products;
import user.Customer;

/**
 *
 * @author lonelyprogrammer
 */
public class Computator {
    
    private double pricePerCase;
    private final double pricePerQuantity;
    private final int totalQuantity;
    
    public Computator(double puhunan, double tubo, int caseQuantity, int quantityPerCase){
    
        pricePerCase = puhunan / caseQuantity;
        pricePerQuantity = pricePerCase / quantityPerCase;
        totalQuantity = quantityPerCase * caseQuantity;
       
    }
    
    public Computator(){
    
        this.pricePerCase = 0;
        this.pricePerQuantity = 0;
        this.totalQuantity = 0;
    }
    
    public double getPricePerCase(){
    
    
        return this.pricePerCase;
    }
    
    
    
    public double getPricePerItem(){
    
    
        return this.pricePerQuantity;
    }
    
    public int getTotalQuantity(){
    
        return this.totalQuantity;
    }
    
    public static double computeProfit(ArrayList<Products> items){
    
        double total = 0;
        int bottleTotalQuantity = 0;
        for(Products item : items){
        
            bottleTotalQuantity = item.getQuantityPerCase() * item.getcaseQuantity();
            for(int i=item.getRemainingBottles(); i<bottleTotalQuantity; i++){
            
                total += item.getItemTubo();
            }
            
        }
        return total;
    }
    
    
    public static double computeTotalInvestment(ArrayList<Products> investmentValues){
        
        double total = 0;
        
        for(Products money : investmentValues){
        
            total += money.getPuhunan();   
            
        }
        return total;
    }
    
    public static double computeTotalDebt(ArrayList<Customer> customerDebt){
    
        double total = 0;
        
        for(Customer debt: customerDebt){
        
            total += debt.getBalance();
        }
    
        return total;
    }
}
