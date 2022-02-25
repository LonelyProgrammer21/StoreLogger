/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package user;

import java.util.ArrayList;

/**
 *
 * @author lonelyprogrammer
 */
public class Customer {
    
    private String name;
    private double balance;
    private ArrayList<CustomerDebt> debtList;
    
    public Customer(){
    
        initData();
    }

    public Customer(String name, double balance) {
        super();
        this.name = name;
        this.balance = balance;
    }
    
    private void initData(){
    
        debtList = new ArrayList<>();
    }
    
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getBalance() {
        return balance;
    }

    private void setBalance(double balance) {
        this.balance = balance;
    }
    
    public void saveDebtInfo(CustomerDebt info){
    
        
        this.debtList.add(info);
        double totalDebt = 0;
        for(CustomerDebt items : debtList){
        
            totalDebt += items.getTotal();
        }
        setBalance(totalDebt);
    }
    
    public ArrayList<CustomerDebt> getDebtList(){
    
        return this.debtList;
    }
    
}
