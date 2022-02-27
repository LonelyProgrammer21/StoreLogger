/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package main;

import java.awt.HeadlessException;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.PriorityQueue;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.SpinnerNumberModel;
import javax.swing.table.DefaultTableModel;
import misc.Transactions;
import products.Beer;
import products.Products;
import products.PureDrinks;
import tools.Computator;
import tools.DataHandler;
import tools.LoggerClass;
import user.Customer;
import user.CustomerDebt;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

/**
 *
 * @author lonelyprogrammer
 * 
 * 
 */
public class Dashboard extends javax.swing.JFrame {

    /**
     * Creates new form Dashboard
     */
    private String message = "";
    private String name;
    private String productType;
    private double puhunan, tubo;
    private int caseQuantity, quantityPerCase;
    private int userChoice = 0;
    
    private DataHandler dataHandler = null;
    
    private DefaultTableModel tblModel = null;
    private DefaultTableModel tblCustomerModel = null;
    private DefaultTableModel tblCustomerInfoModel = null;
    private DefaultComboBoxModel cmbModel = null;
    private DefaultComboBoxModel<Integer> cmbDatesModel;
    private DefaultTableModel tblTransactionHistoryModel = null;
    private DefaultTableModel tblSelectedTransactHistoryModel = null;
    private DefaultComboBoxModel cmbProductListModel = null;
    private Vector modelData = null;
    
    private final DecimalFormat format = new DecimalFormat("##.##");
    private ArrayList<Products> products = null;
    private ArrayList<Customer> customers = null;
    private ArrayList<Transactions> transactionData = null;
    private HashMap<Integer,Vector> transactionTableModels;
    private HashMap<Integer,ArrayList<Transactions>> transactionHistory = null;
    private PriorityQueue queue = null;
    private JFileChooser fileChooser = null;
    private SpinnerNumberModel spnModel;
    private double total = 0, balance, investment, profit, currentMoney;
    private Products selectedProduct = null;
    private Customer selectedCustomer = null;
    private int quantity = 1, index, customerIndex;
    private Computator compute;
    double exchange = 0;
    private boolean addProdAction = false;
    private LocalDateTime time = LocalDateTime.now();
    private DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy");
    
    public Dashboard() {
        
        initComponents();
        Image icon = new ImageIcon("resources/icons/store.png").getImage();
        this.setIconImage(icon);
        this.setActionCommands();
 
        initData();
        txtDebtInfoArea.setEditable(false);
    }
    
    private void updateTransactionData(){
    
        
        lblAddedBalance.setText("");
        lblAddedCurrentMoney.setText("");
        lblAddedProfit.setText("");
        tblTransactionHistoryModel.setRowCount(0);
        tblSelectedTransactHistoryModel.setRowCount(0);
        cmbDates.removeAllItems();
        Set<Integer> dates = new HashSet();
        
        if(!transactionHistory.isEmpty()){
            
            
        
            for(int transactionDates : transactionHistory.keySet()){
            
                dates.add(transactionDates);
                
            }
            
          for(int retrievedDates : dates){
          cmbDatesModel.addElement(retrievedDates);
              modelData = new Vector();
              for(Transactions data : transactionHistory.get(retrievedDates)){
              
                  
                  if(data.getYear() == retrievedDates){
                  
                      modelData.add(data.getMonth());
                      modelData.add(data.getDate());
                      modelData.add(data.getYear());

                  }
                  
              }
              transactionTableModels.put(retrievedDates,modelData);
          }
        }
       
        if(cmbDatesModel.getSize() != 0){
       
            for(int i=0; i<cmbDatesModel.getSize(); i++){
            
                modelData = new Vector();
                for(int j=0; j<transactionTableModels.get(cmbDatesModel.getElementAt(i)).size(); j++){
            
                modelData.add(transactionTableModels.get(cmbDatesModel.getElementAt(i)).get(j));
                }
                tblTransactionHistoryModel.addRow(modelData);
            }
            
        }
        tblTransactionHistory.setModel(tblTransactionHistoryModel);
        cmbDates.setModel(cmbDatesModel);
    }
    
    private void getDate(){
    
        
    }
    
    private void updateModels(){
    
        queue = new PriorityQueue();
        for(Products e: products){
        
            queue.add(e.getName());
        }
        
        this.cmbProductListModel.removeAllElements();
        while(!queue.isEmpty()){
        
           cmbProductListModel.addElement(queue.poll());
        }
        
        if(cmbProductListModel.getSize() != 0){
        
            String firstProduct = cmbProductListModel.getElementAt(0)+"";
            for(Products e: products){
            
                if(e.getName().equalsIgnoreCase(firstProduct)){
                
                    total = e.getPricePerItem();
                    break;
                }
                
            }
            lblTotalPrice.setText(format.format(total) +" Php");
        }
    }
    
    private void initData(){
    
        this.dataHandler = new DataHandler();
        this.products = new ArrayList<>();
        this.fileChooser = new JFileChooser();
        this.customers = new ArrayList<>();
        transactionData = new ArrayList<>();
        transactionHistory = new HashMap<>();
        transactionTableModels = new HashMap<>();
        fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        
        
            if(!this.dataHandler.hasProductData()){
            
                JOptionPane.showMessageDialog(null, "No Items were found, please"
                        + " add an item to the products pane", "WARNING"
                        , JOptionPane.ERROR_MESSAGE);
               
            }else{
            
               this.products = this.dataHandler.retrievedData();
               
               //!!We stop at this point.
               if(this.dataHandler.haveErrors()){
               
                    JOptionPane.showMessageDialog(null, "Database Error Occured.", ""
                    + "WARNING", JOptionPane.ERROR_MESSAGE);
               }
               
            }
            
            if(this.dataHandler.hasCustomersData()){
                
               this.customers = dataHandler.customerData();
            }
            
            if(this.dataHandler.hasStoreCapital()){
            
                double[] data = this.dataHandler.generatestoreCapital();
                investment = data[0];
                balance = data[1];
                profit = data[2];
                currentMoney = data[3];
            }
            
 
        initModels();
        setModel();
        collectStoreItems();
    }
    
    private void setNewLocation(){

    
    }
    
    private void logData(String actionType){
        
       LoggerClass.writeLog(actionType);
                
    }
    
    private void initObjects(){
    
        String productName;
        int remainingCase;
        if(!products.isEmpty()){
            for(Products e: products){
        
               modelData = new Vector();
               productName = e.getName();
               modelData.add(e.getName());
               modelData.add(e.getProductType());
               modelData.add(format.format(e.getPricePerItem()));
               modelData.add(format.format(e.getPricePerCase()));
               remainingCase = e.getRemainingBottles() / e.getQuantityPerCase();
               e.setRemainingCase(remainingCase);
               modelData.add(e.getRemainingCase()+"");
               modelData.add(e.getRemainingBottles()+"");
               tblModel.addRow(modelData);
                        
            }
        }
        
        if(!customers.isEmpty()){
        
            for(Customer user: customers){
            
                
                modelData = new Vector();
                modelData.addElement(user.getName());
                modelData.addElement(format.format(user.getBalance()));
                this.tblCustomerModel.addRow(modelData);
            }
        }
        updateModels();
        
    }
    
    private void collectStoreItems(){
    
        investment = Computator.computeTotalInvestment(products);
        balance = Computator.computeTotalDebt(customers);
        lblCurrentMoney.setText(format.format(balance)+" Php");
        lblInvestment.setText(format.format(investment)+" Php");
        lblCurrentPaid.setText(format.format(currentMoney)+" Php");
        lblProfit.setText(format.format(Computator.computeProfit(products))+" Php");
        
    
    }
    
    private void setModel(){
    
        this.cmbProductType.setModel(cmbModel);
        tblProductLists.setModel(tblModel);
        this.tblCustomerList.setModel(tblCustomerModel);
        this.cmbProductName.setModel(this.cmbProductListModel);
        tblCustomerInfo.setModel(tblCustomerInfoModel);
        tblTransactionHistory.setModel(tblTransactionHistoryModel);
        tblSelectedTransactHistory.setModel(tblSelectedTransactHistoryModel);
    }
    
    private void initModels(){
        
        cmbModel = new DefaultComboBoxModel();
        modelData = new Vector();
        tblModel = new DefaultTableModel();
        tblCustomerModel = new DefaultTableModel();
        this.cmbProductListModel = new DefaultComboBoxModel<>();
        tblCustomerInfoModel = new DefaultTableModel();
        tblSelectedTransactHistoryModel = new DefaultTableModel();
        tblTransactionHistoryModel = new DefaultTableModel();
        cmbDatesModel = new DefaultComboBoxModel();
        
        lblTotalPrice.setText(format.format(total) + "Php");
        cmbModel.addElement("Beer");
        cmbModel.addElement("Pure Drinks");
        setTableModel();
    
    }
    
    private void setTableModel(){
    
        tblModel.addColumn("Product Name");
        tblModel.addColumn("Product Type");
        tblModel.addColumn("Price Per Bottles");
        tblModel.addColumn("Price Per Case");
        tblModel.addColumn("Case Remaining");
        tblModel.addColumn("Remaining Bottles");
        
        tblCustomerModel.addColumn("Customer Name");
        tblCustomerModel.addColumn("Balance");
        
        tblCustomerInfoModel.addColumn("Product Name");
        tblCustomerInfoModel.addColumn("Quantity");
        
        tblCustomerInfoModel.addColumn("Price");
        tblCustomerInfoModel.addColumn("Date");
        
        tblTransactionHistoryModel.addColumn("Month");
        tblTransactionHistoryModel.addColumn("Date");
        tblTransactionHistoryModel.addColumn("Year");
        
        tblSelectedTransactHistoryModel.addColumn("Product Name");
        tblSelectedTransactHistoryModel.addColumn("Quantity");
        tblSelectedTransactHistoryModel.addColumn("Price");
        tblSelectedTransactHistoryModel.addColumn("Time");
        
        
        
        
        if(!this.products.isEmpty() || !this.customers.isEmpty()){
        
           this.initObjects();
        
        }
        
    
    }
    
    private void resetInput(String typePane){
    
        
        switch(typePane){
        
            case "PRODUCTS" -> {
                this.txtProductName.setText("");
                this.txtPuhunan.setText("");
                this.txtTubo.setText("");
                this.spnCaseQuantity.setValue(1);
                this.spnQuantityPerCase.setValue(1);
               
            }
            case "BUY" -> {
                
                if(cmbProductListModel.getSize() != 0){
                
                this.txtCustomerName.setText("");
                this.txtCustomerMoney.setText("");
                this.cmbProductName.setSelectedIndex(0);
                this.lblExchange.setText("");
                this.lblTotalPrice.setText(format.format(total) + " Php");
                this.spnBuyQuantity.setValue(1);
                }
                
            }
            
            
        
        }
    
    }
    
    private void setActionCommands(){
    
        this.rdbAdd.setActionCommand("ADD");
        this.rdbUpdate.setActionCommand("UPDATE");
        this.rdbDelete.setActionCommand("DELETE");
    }
    
    private void haveData(String type){
    
            if(!this.dataHandler.hasProductData()){
                
                JOptionPane.showMessageDialog(null, "No items were found, "
                + "add an item first before updating it.","Info", 
                JOptionPane.INFORMATION_MESSAGE);
                
            }else {
            
            }
    }
    
    private String productFormat(Customer user){
    
        if(customers.isEmpty()) return "NO DATA.";
        return String.format("""
                             [CUSTOMER-NAME]: %s
                             [CUSTOMER ITEMS]: 
                             {
                             
                             %s
                             }
                             """, user.getName(),this.getFormattedItems(user.getDebtList()));
    }
    
    private void sendProductData(String type){
    
        Products item;
        message = "Please fill all the fields before continuing.";
        if(this.isInputValid(type)){
  
             name = txtProductName.getText().trim();
             productType = cmbProductType.getSelectedItem()+"";
             puhunan = Double.parseDouble(this.txtPuhunan.getText().trim());
             tubo = Double.parseDouble(this.txtTubo.getText().trim());
             caseQuantity = (int)this.spnCaseQuantity.getValue();
             quantityPerCase = (int)this.spnQuantityPerCase.getValue();
            String confirmation = 
                String.format("""
                    Overview
                    Product Name:%s
                    Product-Type:%s
                    Product Puhunan:%.2f
                    Product-Quantity:%d
                    Product-Item-Per-Case:%d
                    Product-Tubo:%.2f
                    Are you sure to continue?""",
                   name,productType,
                   puhunan,caseQuantity,
                   quantityPerCase,tubo);
            
            userChoice = JOptionPane.showConfirmDialog(null, confirmation, "Confirmation",
                    JOptionPane.YES_NO_OPTION,JOptionPane.QUESTION_MESSAGE);
            
            if(userChoice == JOptionPane.YES_OPTION){
           
               compute = new Computator(puhunan, tubo, caseQuantity,quantityPerCase);
            if(this.cmbProductType.getSelectedIndex() == 0){
                
                
               item = new Beer(name,productType,
                puhunan, caseQuantity,quantityPerCase,tubo,
                compute.getPricePerItem(),compute.getPricePerCase(),caseQuantity,
                compute.getTotalQuantity());
               
            }else {
            
                item = new PureDrinks(name,productType,puhunan, caseQuantity,
                quantityPerCase,tubo,compute.
                getPricePerItem(),caseQuantity,compute.getTotalQuantity());
                
            
            }
                //Override the profit if per item is selected.
                if(chbxPerItem.isSelected()){
                    item.setPricePerItem(tubo);
                    item.setPricePerCase(item.getPricePerItem() * item.getcaseQuantity());
                }
                this.products.add(item);
               
               modelData = new Vector();
               modelData.add(item.getName());
               modelData.add(item.getProductType());
               modelData.add(format.format(item.getPricePerItem()));
               modelData.add(format.format(item.getPricePerCase()));
               modelData.add(item.getcaseQuantity()+"");
               modelData.add(item.getRemainingBottles()+"");
               
               this.dataHandler.saveProductData(this.dataProductFormatter(item));
               
               this.tblModel.addRow(modelData);
               this.addProdAction = true;
                updateModels();
               investment += item.getPuhunan();
               lblInvestment.setText(format.format(investment));
               LoggerClass.writeLog(type);
               this.addProdAction = false;
            }
        }else {
        
        
            JOptionPane.showMessageDialog(null, message,"Info", 
                JOptionPane.INFORMATION_MESSAGE);
        }
    
    }
    
    private String getFormattedItems(ArrayList<CustomerDebt> items){
    
        String data = "";
        
        for(CustomerDebt item : items){
            
            data += String.format("[DATE]: %s\n", item.getDate());
            data += String.format("""
                                  [PRODUCT NAME]: %s
                                  [PRODUCT-Quantity]: %d
                                  [PRODUCT-TOTAL-PRICE]: %s
                                  
                                  """, item.getProductName(), item.getQuantity(),
                    format.format(item.getTotal()));
        }
        return data;
    }
    
    private String dataProductFormatter(Products product){
    
        if(products.isEmpty()) return "NO DATA.";
        return String.format("""
                             [PRODUCT-TYPE]: %s
                             [PRODUCT-NAME]: %s
                             [PRODUCT-CASE-QUANTITY]: %d
                             [PRODUCT-QUANTITY-PERCASE]: %d
                             [PRODUCT-TOTAL-REMAINING]: %d
                             [PRODUCT-PUHUNAN]: %.2f
                             [PRODUCT-TUBO]: %.2f
                             
                             """, product.getProductType(),product.getName(),
                        product.getcaseQuantity(),product.getQuantityPerCase(),
                        product.getRemainingBottles(),product.getPuhunan(),
                        product.getTubo());
    }
    
    
    private boolean isDuplicate(){
    
        boolean isDuplicate = false;
        for(Products e: products){
        
            if(e.getName().equalsIgnoreCase(txtProductName.getText().trim())){
            
                isDuplicate = true;
                message = "Item is already exists.";
                break;
            }
        }
        
        return isDuplicate;
    }
    
    private boolean isInputValid(String actionType){
    
    
        switch(actionType){
        
            case "PRODUCTSPANE":
                if(!this.txtProductName.getText().trim().isEmpty() 
                && !this.txtPuhunan.getText().trim().isEmpty()
                && !this.txtTubo.getText().trim().isEmpty()
                && (int)this.spnCaseQuantity.getValue() != 0
                && (int)this.spnQuantityPerCase.getValue() != 0)
                return !isDuplicate();
                
                
        }
        return false;
    }
    
    private void enableWidgets(){
    
        this.txtProductName.setEnabled(true);
        this.txtPuhunan.setEnabled(true);
        this.txtTubo.setEnabled(true);
        this.cmbProductType.setEnabled(true);
        this.spnCaseQuantity.setEnabled(true);
        this.spnQuantityPerCase.setEnabled(true);
    }
    
    private void updateCustomerWindow(){
    
        if(!customers.isEmpty()){
        
            for(int i=0; i<customers.size(); i++){
            
                selectedCustomer = customers.get(i);
                if(selectedCustomer.getDebtList().isEmpty()){
                
                    for(int j=0; i<tblCustomerList.getRowCount(); j++){
                    
                        if(tblCustomerList.getValueAt(i, 0).equals(selectedCustomer.getName())){
                       
                            this.tblCustomerModel.removeRow(i);
                            customers.remove(selectedCustomer);
                            break;
                        }
                    }
                }
            }
        }
    }
    
    private void updateCustomerTable(){
    
        tblCustomerModel.setRowCount(0);
        for(Customer user:customers){
        
            modelData = new Vector();
            modelData.add(user.getName());
            modelData.add(format.format(user.getBalance()));
            tblCustomerModel.addRow(modelData);
        
        }
        tblCustomerList.setModel(tblCustomerModel);
    
    }
    
    private Products getItem(String prodName){
    
        Products item = null;
        for(Products e: products){
        
            if(e.getName().equalsIgnoreCase(prodName)){
                item = e;
                break;
            }
        }
        return item;
    }
    
    private Customer getCustomer(String name){
    
        Customer user = null;
        for(Customer names : customers){
        
            if(names.getName().equalsIgnoreCase(name)){
            
                user = names;
                break;
            }
            
        }
        return user;
    }
    
    private void printTotalValues(){
        
       selectedProduct = getItem(cmbProductName.getSelectedItem()+"");
        if(chkboxCaseBuy.isSelected()){

            total = (selectedProduct.getPricePerCase()*quantity);
        }else {
        
            total = (selectedProduct.getPricePerItem() * quantity);
        }
    
        lblTotalPrice.setText(format.format(total) + " Php");
    
    }
    
    private void getDebtInfo(Customer name){
    
        txtDebtInfoArea.setText("");
        String data;
        data = "Total = " + format.format(name.getBalance()) + " Php\n";
        for(CustomerDebt info : name.getDebtList()){
        
            data += "Date: " + info.getDate() +"\n";
            data += "Product Name: " + info.getProductName()+"\n";
            data += "Product Quantity :" + format.format(info.getQuantity())+"\n";
            data += "Product Total Price :" + format.format(info.getTotal())+"\n\n\n";
        }
        txtDebtInfoArea.setText(data);
    }
    
    
    private void getUpdatedCompute(){
    
        if(!txtCustomerMoney.getText().trim().isEmpty()){
            
            currentMoney += total;
           
        }
        else{
        balance += total;
        }
        profit = Computator.computeProfit(products);
        lblCurrentMoney.setText(format.format(balance)+" Php");
        lblProfit.setText(format.format(profit)+" Php");
        lblCurrentPaid.setText(format.format(currentMoney)+" Php");
    }
    
    private void updateProduct(java.awt.event.KeyEvent evt){
    
        if(evt.getKeyChar() == KeyEvent.VK_ENTER){
        
            if(rdbAdd.isSelected())
            this.sendProductData("PRODUCTSPANE");
            else if(rdbUpdate.isSelected()){
            
                index = tblProductLists.getSelectedRow();
                try{
                
                    String productName = txtProductName.getText().trim().toUpperCase();
                    puhunan = Double.parseDouble(txtPuhunan.getText().trim());
                    caseQuantity = (int)spnCaseQuantity.getValue();
                    quantityPerCase = (int)spnQuantityPerCase.getValue();
                    profit = Double.parseDouble(txtTubo.getText());
                    
                    compute = new Computator(puhunan, profit, caseQuantity,quantityPerCase);
                    
                    products.get(index).setProductname(productName);
                    products.get(index).setProductType(cmbProductType.getSelectedItem()+"");
                    products.get(index).setCaseQuantity(caseQuantity);
                    products.get(index).setQuantityPerCase(quantityPerCase);
                    products.get(index).setPuhunan(puhunan);
                    products.get(index).setRemainingBottles(compute.getTotalQuantity());
                    products.get(index).setPricePerCase(compute.getPricePerCase());
                    products.get(index).setPricePerItem(compute.getPricePerItem());
                    products.get(index).setitemTubo(profit);
                    
                    message = "Product is now updated.";
                    JOptionPane.showMessageDialog(null, message,"Info", JOptionPane.INFORMATION_MESSAGE);
                }catch(NumberFormatException e){
                
                    message = "Error please double check your input.";
                    JOptionPane.showMessageDialog(null, message,"Warning", JOptionPane.WARNING_MESSAGE);
                }
                
            }
                updateProductsModel();
                this.resetInput("PRODUCTS");
        }
    }
    
    private void updateProductsModel(){
    
        tblModel.setRowCount(0);
        int remainingCase;
        for(Products e : products){
        
            modelData = new Vector();
               modelData.add(e.getName());
               modelData.add(e.getProductType());
               modelData.add(format.format(e.getPricePerItem()));
               modelData.add(format.format(e.getPricePerCase()));
               remainingCase = e.getRemainingBottles() / e.getQuantityPerCase();
               e.setRemainingCase(remainingCase);
               modelData.add(e.getRemainingCase()+"");
               modelData.add(e.getRemainingBottles()+"");
               tblModel.addRow(modelData);
            
        }
        tblProductLists.setModel(tblModel);
    }
  
    private void saveTransaction(String productName, int quantity, double price, boolean isBalance){
    
        
        String month = LoggerClass.months[LoggerClass.cal.get(Calendar.MONTH)];
        time = LocalDateTime.now();
        int date = LoggerClass.cal.get(Calendar.DATE);
        int year = LoggerClass.cal.get(Calendar.YEAR);
        
        timeFormatter = DateTimeFormatter.ofPattern("hh:mm:ss a");
        Transactions object = new Transactions(month,date,year, 
                timeFormatter.format(time),productName,price,quantity);
        if(isBalance){
            
            object.setTotalBalance(price);
        }else {
        
            selectedProduct = this.getItem(productName);
            
            double getProfitByQuantity = (selectedProduct.getPricePerCase() / 
                    selectedProduct.getQuantityPerCase()) * quantity;
            object.setProfit(getProfitByQuantity);
            
        }
        transactionData.add(object);
        
        if(transactionHistory.containsKey(year)){
            
            transactionHistory.get(year).add(object);
        }else {
        
            transactionHistory.put(year, new ArrayList<>(transactionData));
        }
        transactionData.clear();
        
        Dashboard.this.updateTransactionData();
    
    }
    private void buyItem(String productName, int itemQuantity, boolean isBalance){
        
        int remaining;
        for(Products e: products){
        
            if(e.getName().equalsIgnoreCase(productName)){
            
               if(e.getProductType().equalsIgnoreCase("Pure Drinks")){
               
                    if(e.getRemainingBottles() > 0){
                    
                        remaining = e.getRemainingBottles();
                        e.setRemainingBottles(remaining - itemQuantity);
                        message = "Success!";
                        saveTransaction(e.getProductname(),itemQuantity,total,isBalance);
                        JOptionPane.showMessageDialog(null, message, "Result", JOptionPane.INFORMATION_MESSAGE);
                        
                        break;
                    }else {
                        
                        message = "Item out of stock.";
                        JOptionPane.showMessageDialog(null,message,"Info", JOptionPane.INFORMATION_MESSAGE);
                    }
               }else if(e.getProductType().equalsIgnoreCase("Beer")){
               
                   if(e.getRemainingBottles() > 0){
                   
                       remaining = e.getRemainingBottles();
                       if(chkboxCaseBuy.isSelected()){
                       
                           e.setRemainingBottles(remaining - e.getQuantityPerCase());
                           e.setRemainingCase(e.getRemainingCase() - itemQuantity);
                           itemQuantity = e.getQuantityPerCase();
                           
                       }else {
                       
                           e.setRemainingBottles(remaining - itemQuantity);
                           
                       }
                       message = "Success!";
                       saveTransaction(e.getProductname(),itemQuantity,total, isBalance);
                        JOptionPane.showMessageDialog(null, message, "Result", JOptionPane.INFORMATION_MESSAGE);
                       
                        break;
                   }else {
                   
                       message = "Item out of stock.";
                        JOptionPane.showMessageDialog(null,message,"Info", JOptionPane.INFORMATION_MESSAGE);
                   }
               }
               
            }
        }
        getUpdatedCompute();
        updateProductsModel();
        resetInput("BUY");
    }
    
    private int isCustomerExist(String name){
    
        index = -1;
        int count = 0;
        if(customers.isEmpty())
            index = -1;
        
        for(Customer user : customers){
        
            if(user.getName().equalsIgnoreCase(name)){
            
                 index = count;
                 break;
            }
            count++;
        }
        return index;
    }
    
    private String formatStoreCapital(double puhunan, double balance, double profit, double currentMoney){
    
        return String.format("""
                             [STORE-PUHUNAN]: %s
                             [STORE-BALANCE]: %s
                             [STORE-PROFIT]: %s
                             [STORE-CURRENT-MONEY]: %s
                             """, format.format(puhunan), format.format(balance)
                             ,format.format(profit), format.format(currentMoney));
    }
    
    private void displayProductData(int index){
    
            cmbProductType.setSelectedItem(products.get(index).getProductType());
            txtProductName.setText(products.get(index).getName());
            txtPuhunan.setText(format.format(products.get(index).getPuhunan()));
            txtTubo.setText(format.format(products.get(index).getTubo()));
            spnCaseQuantity.setValue(products.get(index).getcaseQuantity());
            spnQuantityPerCase.setValue(products.get(index).getRemainingBottles());
            enableWidgets();
    }
    
    private void initializeCustomerInfo(){
    
       
        System.out.println("Customer name: "+ selectedCustomer.getName());
       ArrayList<CustomerDebt> data = selectedCustomer.getDebtList();
       lblCustomerName.setText(selectedCustomer.getName());
       tblCustomerInfoModel.setRowCount(0);
       for(CustomerDebt debt : data){
       
           modelData = new Vector();
           System.out.println(debt.getProductName());
           modelData.add(debt.getProductName());
           modelData.add(format.format(debt.getQuantity()));
           modelData.add(format.format(debt.getTotal()));
           modelData.add(debt.getDate());
           tblCustomerInfoModel.addRow(modelData);
       }
       
       tblCustomerInfo.setModel(tblCustomerInfoModel);
       lblProductName.setText("");
       spnProductQuantity.setValue(1);
       txtPrice.setText("");
       dialogShowDebtList.setLocationRelativeTo(null);
       dialogShowDebtList.setVisible(true);
    }
    
    private CustomerDebt getCustomerItemDebt(String name, ArrayList<CustomerDebt> list){
    
        CustomerDebt item = null;
        for(CustomerDebt items : list){
        
            if(items.getProductName().equalsIgnoreCase(name)){
            
                item = items;
                break;
            }
        }
        
        return item;
    }
    
    
    private void updateTransactionData(String month, int year){

        double totalBalance = 0;
        double addedCurrentMoney = 0;
        double addedProfit = 0;
        for(Transactions data : transactionHistory.get(year)){
        
            if(data.getMonth().equals(month)){
            
                modelData = new Vector();
                modelData.add(data.getProductName());
                modelData.add(format.format(data.getQuantity()));
                if(data.getTotalBalance() != 0)
                    modelData.add(format.format(data.getPrice())+ " (Balance)");
                else
                    modelData.add(format.format(data.getPrice()));
                
                modelData.add(data.getTime());
                if(data.getTotalBalance() != 0){
                
                    totalBalance += data.getTotalBalance();
                }else {
                
                    addedCurrentMoney += data.getPrice();
                }
                addedProfit += data.getProfit();
                tblSelectedTransactHistoryModel.addRow(modelData);
            }
            
        }
        
        lblAddedBalance.setText(format.format(totalBalance) + " Php");
        lblAddedCurrentMoney.setText(format.format(addedCurrentMoney) + " Php");
        lblAddedProfit.setText(format.format(addedProfit) + " Php");
        tblSelectedTransactHistory.setModel(tblSelectedTransactHistoryModel);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        btnGrp = new javax.swing.ButtonGroup();
        dialogShowDebtList = new javax.swing.JDialog();
        jScrollPane4 = new javax.swing.JScrollPane();
        tblCustomerInfo = new javax.swing.JTable();
        jLabel5 = new javax.swing.JLabel();
        lblCustomerName = new javax.swing.JLabel();
        jLabel9 = new javax.swing.JLabel();
        jLabel10 = new javax.swing.JLabel();
        btnEditName = new javax.swing.JButton();
        jLabel20 = new javax.swing.JLabel();
        lblProductName = new javax.swing.JLabel();
        chbxisPaid = new javax.swing.JCheckBox();
        btnUpdate = new javax.swing.JButton();
        btnClearInfo = new javax.swing.JButton();
        spnProductQuantity = new javax.swing.JSpinner();
        txtPrice = new javax.swing.JTextField();
        dialogSummaryWindow = new javax.swing.JDialog();
        mainTabPane = new javax.swing.JTabbedPane();
        jPanel2 = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jPanel4 = new javax.swing.JPanel();
        jLabel2 = new javax.swing.JLabel();
        lblCurrentMoney = new javax.swing.JLabel();
        jLabel11 = new javax.swing.JLabel();
        jButton1 = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        jLabel4 = new javax.swing.JLabel();
        lblInvestment = new javax.swing.JLabel();
        jLabel6 = new javax.swing.JLabel();
        jLabel7 = new javax.swing.JLabel();
        lblCurrentPaid = new javax.swing.JLabel();
        lblProfit = new javax.swing.JLabel();
        btnSummary = new javax.swing.JButton();
        jPanel1 = new javax.swing.JPanel();
        jPanel6 = new javax.swing.JPanel();
        jPanel15 = new javax.swing.JPanel();
        jLabel16 = new javax.swing.JLabel();
        lblExchange = new javax.swing.JLabel();
        lblTotalPrice = new javax.swing.JLabel();
        jLabel19 = new javax.swing.JLabel();
        cmbProductName = new javax.swing.JComboBox<>();
        txtCustomerName = new javax.swing.JTextField();
        jLabel21 = new javax.swing.JLabel();
        jLabel13 = new javax.swing.JLabel();
        spnBuyQuantity = new javax.swing.JSpinner();
        jLabel14 = new javax.swing.JLabel();
        jLabel15 = new javax.swing.JLabel();
        txtCustomerMoney = new javax.swing.JTextField();
        chkboxCaseBuy = new javax.swing.JCheckBox();
        jPanel14 = new javax.swing.JPanel();
        btnPlaceOrder = new javax.swing.JButton();
        jButton3 = new javax.swing.JButton();
        jLabel18 = new javax.swing.JLabel();
        jPanel3 = new javax.swing.JPanel();
        jLabel22 = new javax.swing.JLabel();
        jScrollPane1 = new javax.swing.JScrollPane();
        tblCustomerList = new javax.swing.JTable();
        jLabel23 = new javax.swing.JLabel();
        txtFindName = new javax.swing.JTextField();
        jLabel24 = new javax.swing.JLabel();
        jScrollPane3 = new javax.swing.JScrollPane();
        txtDebtInfoArea = new javax.swing.JTextArea();
        jLabel17 = new javax.swing.JLabel();
        jButton2 = new javax.swing.JButton();
        lblFindStatus = new javax.swing.JLabel();
        jPanel7 = new javax.swing.JPanel();
        jLabel26 = new javax.swing.JLabel();
        jPanel8 = new javax.swing.JPanel();
        jPanel9 = new javax.swing.JPanel();
        jPanel11 = new javax.swing.JPanel();
        jLabel27 = new javax.swing.JLabel();
        jLabel28 = new javax.swing.JLabel();
        jLabel29 = new javax.swing.JLabel();
        jLabel30 = new javax.swing.JLabel();
        jLabel31 = new javax.swing.JLabel();
        jLabel25 = new javax.swing.JLabel();
        jPanel12 = new javax.swing.JPanel();
        txtProductName = new javax.swing.JTextField();
        txtPuhunan = new javax.swing.JTextField();
        spnCaseQuantity = new javax.swing.JSpinner();
        spnQuantityPerCase = new javax.swing.JSpinner();
        cmbProductType = new javax.swing.JComboBox<>();
        jPanel13 = new javax.swing.JPanel();
        jLabel32 = new javax.swing.JLabel();
        txtTubo = new javax.swing.JTextField();
        chbxPerItem = new javax.swing.JCheckBox();
        jPanel10 = new javax.swing.JPanel();
        rdbAdd = new javax.swing.JRadioButton();
        rdbUpdate = new javax.swing.JRadioButton();
        rdbDelete = new javax.swing.JRadioButton();
        jScrollPane2 = new javax.swing.JScrollPane();
        tblProductLists = new javax.swing.JTable();
        jPanel16 = new javax.swing.JPanel();
        jPanel18 = new javax.swing.JPanel();
        jScrollPane5 = new javax.swing.JScrollPane();
        tblTransactionHistory = new javax.swing.JTable();
        jLabel8 = new javax.swing.JLabel();
        jScrollPane6 = new javax.swing.JScrollPane();
        tblSelectedTransactHistory = new javax.swing.JTable();
        jLabel12 = new javax.swing.JLabel();
        jLabel33 = new javax.swing.JLabel();
        jLabel34 = new javax.swing.JLabel();
        jLabel35 = new javax.swing.JLabel();
        lblAddedCurrentMoney = new javax.swing.JLabel();
        lblAddedBalance = new javax.swing.JLabel();
        jLabel36 = new javax.swing.JLabel();
        lblAddedProfit = new javax.swing.JLabel();
        cmbDates = new javax.swing.JComboBox<>();
        jPanel17 = new javax.swing.JPanel();
        jLabel3 = new javax.swing.JLabel();
        jMenuBar1 = new javax.swing.JMenuBar();
        jMenu1 = new javax.swing.JMenu();
        menuItemSelectDatabaseLocation = new javax.swing.JMenuItem();
        jMenu2 = new javax.swing.JMenu();

        dialogShowDebtList.setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        dialogShowDebtList.setAlwaysOnTop(true);
        dialogShowDebtList.setLocationByPlatform(true);
        dialogShowDebtList.setResizable(false);
        dialogShowDebtList.setSize(new java.awt.Dimension(690, 381));
        dialogShowDebtList.addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                dialogShowDebtListWindowClosing(evt);
            }
        });

        tblCustomerInfo.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblCustomerInfo.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerInfoMouseClicked(evt);
            }
        });
        jScrollPane4.setViewportView(tblCustomerInfo);

        jLabel5.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel5.setText("Customer Name:");

        lblCustomerName.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblCustomerName.setText("jLabel8");

        jLabel9.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel9.setText("Product Name:");

        jLabel10.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel10.setText("Quantity:");

        btnEditName.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnEditName.setText("Edit Name");
        btnEditName.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnEditNameActionPerformed(evt);
            }
        });

        jLabel20.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel20.setText("Price:");

        lblProductName.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N

        chbxisPaid.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        chbxisPaid.setText("Paid?");
        chbxisPaid.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chbxisPaidItemStateChanged(evt);
            }
        });

        btnUpdate.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnUpdate.setText("Update");
        btnUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnUpdateActionPerformed(evt);
            }
        });

        btnClearInfo.setFont(new java.awt.Font("Dialog", 1, 12)); // NOI18N
        btnClearInfo.setText("Clear");

        javax.swing.GroupLayout dialogShowDebtListLayout = new javax.swing.GroupLayout(dialogShowDebtList.getContentPane());
        dialogShowDebtList.getContentPane().setLayout(dialogShowDebtListLayout);
        dialogShowDebtListLayout.setHorizontalGroup(
            dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                        .addComponent(jScrollPane4)
                        .addContainerGap())
                    .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                        .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel20, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel10, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel5, javax.swing.GroupLayout.DEFAULT_SIZE, 185, Short.MAX_VALUE))
                        .addGap(56, 56, 56)
                        .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                                .addComponent(txtPrice)
                                .addGap(119, 119, 119)
                                .addComponent(chbxisPaid, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                    .addComponent(lblCustomerName, javax.swing.GroupLayout.DEFAULT_SIZE, 120, Short.MAX_VALUE)
                                    .addComponent(lblProductName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spnProductQuantity))
                                .addGap(119, 119, 119)
                                .addComponent(btnEditName, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addGap(93, 93, 93))))
            .addGroup(dialogShowDebtListLayout.createSequentialGroup()
                .addGap(239, 239, 239)
                .addComponent(btnUpdate)
                .addGap(77, 77, 77)
                .addComponent(btnClearInfo, javax.swing.GroupLayout.PREFERRED_SIZE, 85, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        dialogShowDebtListLayout.setVerticalGroup(
            dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, dialogShowDebtListLayout.createSequentialGroup()
                .addGap(33, 33, 33)
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel5)
                    .addComponent(lblCustomerName)
                    .addComponent(btnEditName))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(jLabel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProductName, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel10)
                    .addComponent(spnProductQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtPrice, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(jLabel20)
                        .addComponent(chbxisPaid)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(dialogShowDebtListLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(btnUpdate)
                    .addComponent(btnClearInfo))
                .addGap(31, 31, 31)
                .addComponent(jScrollPane4, javax.swing.GroupLayout.PREFERRED_SIZE, 141, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );

        javax.swing.GroupLayout dialogSummaryWindowLayout = new javax.swing.GroupLayout(dialogSummaryWindow.getContentPane());
        dialogSummaryWindow.getContentPane().setLayout(dialogSummaryWindowLayout);
        dialogSummaryWindowLayout.setHorizontalGroup(
            dialogSummaryWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 669, Short.MAX_VALUE)
        );
        dialogSummaryWindowLayout.setVerticalGroup(
            dialogSummaryWindowLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 368, Short.MAX_VALUE)
        );

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);
        setTitle("StoreLogger");
        setSize(new java.awt.Dimension(1083, 560));
        addWindowListener(new java.awt.event.WindowAdapter() {
            public void windowClosing(java.awt.event.WindowEvent evt) {
                formWindowClosing(evt);
            }
        });

        mainTabPane.setName(""); // NOI18N

        jPanel2.setBackground(new java.awt.Color(210, 231, 244));

        jLabel1.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel1.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-account-64.png"))); // NOI18N
        jLabel1.setText("Overview");

        jPanel4.setBackground(new java.awt.Color(210, 231, 244));
        jPanel4.setBorder(javax.swing.BorderFactory.createTitledBorder("Statistics"));

        jLabel2.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel2.setText("BALANCE:");

        lblCurrentMoney.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCurrentMoney.setText("jLabel3");

        jLabel11.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N

        jButton1.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jButton1.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/icons8-unloading-of-an-item-up-arrow-navigation-24.png"))); // NOI18N
        jButton1.setText("Show Transactions");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(jLabel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 206, Short.MAX_VALUE)
                        .addComponent(lblCurrentMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(jButton1)))
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addGap(116, 116, 116)
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(lblCurrentMoney, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jLabel2, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(jButton1)
                .addGap(13, 13, 13)
                .addComponent(jLabel11)
                .addGap(304, 304, 304))
        );

        jPanel5.setBackground(new java.awt.Color(210, 231, 244));
        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder("My Store"));

        jLabel4.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel4.setText("Investment:");

        lblInvestment.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblInvestment.setText("jLabel5");

        jLabel6.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel6.setText("Current Money:");

        jLabel7.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel7.setText("Profit:");

        lblCurrentPaid.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblCurrentPaid.setText("jLabel8");

        lblProfit.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        lblProfit.setText("jLabel9");

        btnSummary.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        btnSummary.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/summary.png"))); // NOI18N
        btnSummary.setText("Summary");

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel5Layout.createSequentialGroup()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(btnSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 157, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGap(36, 36, 36)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(jPanel5Layout.createSequentialGroup()
                                .addComponent(jLabel7, javax.swing.GroupLayout.PREFERRED_SIZE, 103, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(276, 276, 276)
                                .addComponent(lblProfit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel5Layout.createSequentialGroup()
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel4)
                                    .addComponent(jLabel6, javax.swing.GroupLayout.PREFERRED_SIZE, 158, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(223, 223, 223)
                                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(lblInvestment, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblCurrentPaid, javax.swing.GroupLayout.DEFAULT_SIZE, 77, Short.MAX_VALUE))))))
                .addGap(79, 79, 79))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addGap(122, 122, 122)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel4)
                    .addComponent(lblInvestment))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel6, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblCurrentPaid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel7, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(lblProfit, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(18, 18, 18)
                .addComponent(btnSummary, javax.swing.GroupLayout.PREFERRED_SIZE, 34, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(355, 355, 355))
        );

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jLabel1, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addComponent(jLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 147, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel5, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
        );

        mainTabPane.addTab("Overview", new javax.swing.ImageIcon(getClass().getResource("/icons/money-wallet-icon.png")), jPanel2); // NOI18N

        jPanel6.setBackground(new java.awt.Color(172, 203, 206));
        jPanel6.setBorder(javax.swing.BorderFactory.createTitledBorder("Customer Buy"));
        jPanel6.setPreferredSize(new java.awt.Dimension(1615, 537));

        jPanel15.setBackground(new java.awt.Color(172, 203, 206));
        jPanel15.setPreferredSize(new java.awt.Dimension(575, 374));

        jLabel16.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel16.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel16.setText("TOTAL PRICE:");
        jLabel16.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);

        lblExchange.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblExchange.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblExchange.setToolTipText("");

        lblTotalPrice.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblTotalPrice.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        lblTotalPrice.setToolTipText("Total price of the item'(s)");

        jLabel19.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel19.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel19.setText("Exchange:");

        cmbProductName.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProductName.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbProductNameItemStateChanged(evt);
            }
        });

        txtCustomerName.setToolTipText("Enter the customer name if kakilala if not type nothing.");

        jLabel21.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel21.setText("Customer Name:");

        jLabel13.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel13.setText("Product name:");

        spnBuyQuantity.setModel(new javax.swing.SpinnerNumberModel(1, 1, 24, 1));
        spnBuyQuantity.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                spnBuyQuantityStateChanged(evt);
            }
        });

        jLabel14.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel14.setText("Item Quantity");

        jLabel15.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel15.setText("Money:");

        txtCustomerMoney.setToolTipText("Enter nothing to considered it as utang");
        txtCustomerMoney.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                txtCustomerMoneyKeyReleased(evt);
            }
        });

        chkboxCaseBuy.setBackground(new java.awt.Color(172, 203, 206));
        chkboxCaseBuy.setText("Case");
        chkboxCaseBuy.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                chkboxCaseBuyItemStateChanged(evt);
            }
        });

        jPanel14.setBackground(new java.awt.Color(172, 203, 206));
        jPanel14.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 100, 5));

        btnPlaceOrder.setBackground(new java.awt.Color(144, 238, 154));
        btnPlaceOrder.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        btnPlaceOrder.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/check-1-icon.png"))); // NOI18N
        btnPlaceOrder.setText("Proceed");
        btnPlaceOrder.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnPlaceOrderActionPerformed(evt);
            }
        });
        jPanel14.add(btnPlaceOrder);

        jButton3.setBackground(new java.awt.Color(238, 58, 88));
        jButton3.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jButton3.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/Data-Clear-Filters-icon.png"))); // NOI18N
        jButton3.setText("Clear");
        jButton3.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton3ActionPerformed(evt);
            }
        });
        jPanel14.add(jButton3);

        javax.swing.GroupLayout jPanel15Layout = new javax.swing.GroupLayout(jPanel15);
        jPanel15.setLayout(jPanel15Layout);
        jPanel15Layout.setHorizontalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap(115, Short.MAX_VALUE)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 482, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(lblExchange, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel15Layout.createSequentialGroup()
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(jLabel13, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(jLabel15, javax.swing.GroupLayout.PREFERRED_SIZE, 164, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGap(31, 31, 31)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                                    .addComponent(cmbProductName, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(spnBuyQuantity)
                                    .addComponent(txtCustomerMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addGroup(jPanel15Layout.createSequentialGroup()
                                .addComponent(jLabel21)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, 204, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(chkboxCaseBuy, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                                .addGap(0, 178, Short.MAX_VALUE)
                                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                    .addComponent(jLabel19, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(jLabel16, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(lblTotalPrice, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 177, javax.swing.GroupLayout.PREFERRED_SIZE))))))
                .addGap(191, 191, 191))
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel15Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel14, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel15Layout.setVerticalGroup(
            jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel15Layout.createSequentialGroup()
                .addGap(76, 76, 76)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCustomerName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel21)
                    .addComponent(jLabel16))
                .addGap(18, 18, 18)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(cmbProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(jLabel13))
                    .addComponent(lblTotalPrice, javax.swing.GroupLayout.PREFERRED_SIZE, 41, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(spnBuyQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel14)
                    .addComponent(chkboxCaseBuy))
                .addGap(24, 24, 24)
                .addGroup(jPanel15Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(txtCustomerMoney, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel15)
                    .addComponent(jLabel19))
                .addGap(18, 18, 18)
                .addComponent(lblExchange, javax.swing.GroupLayout.PREFERRED_SIZE, 37, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel14, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 266, Short.MAX_VALUE))
        );

        jLabel18.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel18.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel18.setText("Customer Transaction");

        javax.swing.GroupLayout jPanel6Layout = new javax.swing.GroupLayout(jPanel6);
        jPanel6.setLayout(jPanel6Layout);
        jPanel6Layout.setHorizontalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 1068, Short.MAX_VALUE)
            .addComponent(jLabel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel6Layout.setVerticalGroup(
            jPanel6Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel6Layout.createSequentialGroup()
                .addComponent(jLabel18, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(29, 29, 29)
                .addComponent(jPanel15, javax.swing.GroupLayout.DEFAULT_SIZE, 642, Short.MAX_VALUE)
                .addContainerGap())
        );

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel6, javax.swing.GroupLayout.DEFAULT_SIZE, 819, Short.MAX_VALUE)
        );

        mainTabPane.addTab("Customer Buy", new javax.swing.ImageIcon(getClass().getResource("/icons/shop-cart-icon.png")), jPanel1); // NOI18N

        jPanel3.setBackground(new java.awt.Color(134, 187, 194));

        jLabel22.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel22.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel22.setText("Debt List");

        tblCustomerList.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblCustomerList.getTableHeader().setReorderingAllowed(false);
        tblCustomerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblCustomerListMouseClicked(evt);
            }
        });
        tblCustomerList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblCustomerListKeyPressed(evt);
            }
        });
        jScrollPane1.setViewportView(tblCustomerList);

        jLabel23.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel23.setText("Find Name:");

        jLabel24.setFont(new java.awt.Font("Dialog", 1, 14)); // NOI18N
        jLabel24.setText("Customer Names:");

        txtDebtInfoArea.setColumns(20);
        txtDebtInfoArea.setRows(5);
        jScrollPane3.setViewportView(txtDebtInfoArea);

        jLabel17.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel17.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel17.setText("Information:");

        jButton2.setFont(new java.awt.Font("Dialog", 1, 11)); // NOI18N
        jButton2.setIcon(new javax.swing.ImageIcon(getClass().getResource("/icons/search.png"))); // NOI18N
        jButton2.setText("Find");

        lblFindStatus.setFont(new java.awt.Font("Dialog", 3, 11)); // NOI18N
        lblFindStatus.setForeground(new java.awt.Color(255, 0, 51));
        lblFindStatus.setToolTipText("");

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel22, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel17, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jLabel24, javax.swing.GroupLayout.PREFERRED_SIZE, 140, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(jLabel23)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(lblFindStatus, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(txtFindName, javax.swing.GroupLayout.DEFAULT_SIZE, 152, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 99, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(6, 6, 6))
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane3)
                        .addContainerGap())
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 1054, Short.MAX_VALUE)
                        .addContainerGap())))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addComponent(jLabel22, javax.swing.GroupLayout.PREFERRED_SIZE, 53, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(lblFindStatus, javax.swing.GroupLayout.PREFERRED_SIZE, 14, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(2, 2, 2)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jButton2, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(txtFindName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(jLabel23)
                    .addComponent(jLabel24))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 261, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel17)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane3, javax.swing.GroupLayout.PREFERRED_SIZE, 209, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(202, Short.MAX_VALUE))
        );

        mainTabPane.addTab("Debt List", new javax.swing.ImageIcon(getClass().getResource("/icons/payment-icon.png")), jPanel3); // NOI18N

        jPanel7.setBackground(new java.awt.Color(111, 190, 121));

        jLabel26.setBackground(new java.awt.Color(219, 97, 143));
        jLabel26.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel26.setForeground(new java.awt.Color(0, 0, 0));
        jLabel26.setHorizontalAlignment(javax.swing.SwingConstants.CENTER);
        jLabel26.setText("Products Pane");

        jPanel8.setBackground(new java.awt.Color(111, 190, 121));

        javax.swing.GroupLayout jPanel8Layout = new javax.swing.GroupLayout(jPanel8);
        jPanel8.setLayout(jPanel8Layout);
        jPanel8Layout.setHorizontalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 7, Short.MAX_VALUE)
        );
        jPanel8Layout.setVerticalGroup(
            jPanel8Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 268, Short.MAX_VALUE)
        );

        jPanel9.setBackground(new java.awt.Color(111, 190, 121));

        jPanel11.setBackground(new java.awt.Color(111, 190, 121));

        jLabel27.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel27.setText("Product Name:");

        jLabel28.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel28.setText("Puhunan:");

        jLabel29.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel29.setText("Quantity (Case):");

        jLabel30.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel30.setText("Items Per Case:");

        jLabel31.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel31.setText("Product Type:");

        jLabel25.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel25.setText("Product Lists:");

        javax.swing.GroupLayout jPanel11Layout = new javax.swing.GroupLayout(jPanel11);
        jPanel11.setLayout(jPanel11Layout);
        jPanel11Layout.setHorizontalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel30, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel29, javax.swing.GroupLayout.DEFAULT_SIZE, 367, Short.MAX_VALUE)
            .addComponent(jLabel28, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel27, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel31, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addComponent(jLabel25, javax.swing.GroupLayout.PREFERRED_SIZE, 152, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, Short.MAX_VALUE))
        );
        jPanel11Layout.setVerticalGroup(
            jPanel11Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel11Layout.createSequentialGroup()
                .addGap(24, 24, 24)
                .addComponent(jLabel31)
                .addGap(18, 18, 18)
                .addComponent(jLabel27)
                .addGap(12, 12, 12)
                .addComponent(jLabel28)
                .addGap(23, 23, 23)
                .addComponent(jLabel29, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(jLabel30)
                .addGap(122, 122, 122)
                .addComponent(jLabel25)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel12.setBackground(new java.awt.Color(111, 190, 121));

        txtProductName.setEnabled(false);
        txtProductName.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtProductNameKeyPressed(evt);
            }
        });

        txtPuhunan.setEnabled(false);
        txtPuhunan.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtPuhunanKeyPressed(evt);
            }
        });

        spnCaseQuantity.setToolTipText("");
        spnCaseQuantity.setEnabled(false);
        spnCaseQuantity.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spnCaseQuantityKeyPressed(evt);
            }
        });

        spnQuantityPerCase.setModel(new javax.swing.SpinnerNumberModel(1, 1, 24, 1));
        spnQuantityPerCase.setToolTipText("");
        spnQuantityPerCase.setEnabled(false);
        spnQuantityPerCase.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spnQuantityPerCaseKeyPressed(evt);
            }
        });

        cmbProductType.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "Item 1", "Item 2", "Item 3", "Item 4" }));
        cmbProductType.setEnabled(false);
        cmbProductType.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                cmbProductTypeKeyPressed(evt);
            }
        });

        javax.swing.GroupLayout jPanel12Layout = new javax.swing.GroupLayout(jPanel12);
        jPanel12.setLayout(jPanel12Layout);
        jPanel12Layout.setHorizontalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(txtProductName, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(txtPuhunan)
                    .addGroup(jPanel12Layout.createSequentialGroup()
                        .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(spnQuantityPerCase, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addComponent(spnCaseQuantity, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 42, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addComponent(cmbProductType, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 128, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel12Layout.setVerticalGroup(
            jPanel12Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel12Layout.createSequentialGroup()
                .addGap(19, 19, 19)
                .addComponent(cmbProductType, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(txtProductName, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(txtPuhunan, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(spnCaseQuantity, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(18, 18, 18)
                .addComponent(spnQuantityPerCase, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel13.setBackground(new java.awt.Color(111, 190, 121));

        jLabel32.setFont(new java.awt.Font("Dialog", 0, 18)); // NOI18N
        jLabel32.setText("Tubo:");

        txtTubo.setAutoscrolls(false);
        txtTubo.setEnabled(false);
        txtTubo.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                txtTuboKeyPressed(evt);
            }
        });

        chbxPerItem.setBackground(new java.awt.Color(111, 190, 121));
        chbxPerItem.setText("Per Item?");

        javax.swing.GroupLayout jPanel13Layout = new javax.swing.GroupLayout(jPanel13);
        jPanel13.setLayout(jPanel13Layout);
        jPanel13Layout.setHorizontalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel32)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(chbxPerItem)
                    .addComponent(txtTubo, javax.swing.GroupLayout.PREFERRED_SIZE, 185, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(103, Short.MAX_VALUE))
        );
        jPanel13Layout.setVerticalGroup(
            jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel13Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel13Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel32)
                    .addComponent(txtTubo, javax.swing.GroupLayout.PREFERRED_SIZE, 23, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(chbxPerItem)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout jPanel9Layout = new javax.swing.GroupLayout(jPanel9);
        jPanel9.setLayout(jPanel9Layout);
        jPanel9Layout.setHorizontalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel11, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel12, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jPanel13, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );
        jPanel9Layout.setVerticalGroup(
            jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel9Layout.createSequentialGroup()
                .addGroup(jPanel9Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                    .addComponent(jPanel13, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel11, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel12, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, Short.MAX_VALUE))
        );

        jPanel10.setBackground(new java.awt.Color(111, 190, 121));
        jPanel10.setLayout(new java.awt.FlowLayout(java.awt.FlowLayout.CENTER, 10, 5));

        rdbAdd.setBackground(new java.awt.Color(111, 190, 121));
        btnGrp.add(rdbAdd);
        rdbAdd.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rdbAdd.setText("ADD");
        rdbAdd.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbAddActionPerformed(evt);
            }
        });
        jPanel10.add(rdbAdd);

        rdbUpdate.setBackground(new java.awt.Color(111, 190, 121));
        btnGrp.add(rdbUpdate);
        rdbUpdate.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rdbUpdate.setText("UPDATE");
        rdbUpdate.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbUpdateActionPerformed(evt);
            }
        });
        jPanel10.add(rdbUpdate);

        rdbDelete.setBackground(new java.awt.Color(111, 190, 121));
        btnGrp.add(rdbDelete);
        rdbDelete.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        rdbDelete.setText("DELETE");
        rdbDelete.setToolTipText("");
        rdbDelete.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rdbDeleteActionPerformed(evt);
            }
        });
        jPanel10.add(rdbDelete);

        tblProductLists.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblProductLists.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblProductListsMouseClicked(evt);
            }
        });
        tblProductLists.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tblProductListsKeyPressed(evt);
            }
        });
        jScrollPane2.setViewportView(tblProductLists);

        javax.swing.GroupLayout jPanel7Layout = new javax.swing.GroupLayout(jPanel7);
        jPanel7.setLayout(jPanel7Layout);
        jPanel7Layout.setHorizontalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jLabel26, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jPanel8, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 1053, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel10, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel9, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        jPanel7Layout.setVerticalGroup(
            jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel7Layout.createSequentialGroup()
                .addComponent(jLabel26, javax.swing.GroupLayout.PREFERRED_SIZE, 60, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel10, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel9, javax.swing.GroupLayout.PREFERRED_SIZE, 350, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel7Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addGap(0, 77, Short.MAX_VALUE)
                        .addComponent(jPanel8, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(jPanel7Layout.createSequentialGroup()
                        .addComponent(jScrollPane2, javax.swing.GroupLayout.PREFERRED_SIZE, 216, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        mainTabPane.addTab("Inventory", new javax.swing.ImageIcon(getClass().getResource("/icons/Custom-Icon-Design-Flatastic-4-Inventory-maintenance-0.png")), jPanel7); // NOI18N

        jPanel16.setBackground(new java.awt.Color(82, 140, 159));

        jPanel18.setBackground(new java.awt.Color(82, 140, 159));

        tblTransactionHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        tblTransactionHistory.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                tblTransactionHistoryMouseClicked(evt);
            }
        });
        jScrollPane5.setViewportView(tblTransactionHistory);

        jLabel8.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel8.setText("Summary:");
        jLabel8.setToolTipText("");

        tblSelectedTransactHistory.setModel(new javax.swing.table.DefaultTableModel(
            new Object [][] {

            },
            new String [] {

            }
        ));
        jScrollPane6.setViewportView(tblSelectedTransactHistory);

        jLabel12.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel12.setText("Transaction Dates:");
        jLabel12.setToolTipText("");

        jLabel33.setFont(new java.awt.Font("Dialog", 1, 18)); // NOI18N
        jLabel33.setText("Transaction History: ");
        jLabel33.setToolTipText("");

        jLabel34.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel34.setText("Added Balance:");

        jLabel35.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel35.setText("Added Money to your wallet:");

        lblAddedCurrentMoney.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblAddedCurrentMoney.setText("jLabel36");

        lblAddedBalance.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblAddedBalance.setText("jLabel36");

        jLabel36.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        jLabel36.setText("Added Profit: ");

        lblAddedProfit.setFont(new java.awt.Font("Dialog", 0, 14)); // NOI18N
        lblAddedProfit.setText("jLabel36");

        cmbDates.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                cmbDatesItemStateChanged(evt);
            }
        });

        javax.swing.GroupLayout jPanel18Layout = new javax.swing.GroupLayout(jPanel18);
        jPanel18.setLayout(jPanel18Layout);
        jPanel18Layout.setHorizontalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane5, javax.swing.GroupLayout.DEFAULT_SIZE, 1030, Short.MAX_VALUE)
                    .addComponent(jScrollPane6)
                    .addGroup(jPanel18Layout.createSequentialGroup()
                        .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel8, javax.swing.GroupLayout.PREFERRED_SIZE, 133, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel18Layout.createSequentialGroup()
                                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 145, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                                    .addComponent(lblAddedBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE))
                                .addGroup(javax.swing.GroupLayout.Alignment.LEADING, jPanel18Layout.createSequentialGroup()
                                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                        .addComponent(jLabel36)
                                        .addComponent(jLabel35))
                                    .addGap(18, 18, 18)
                                    .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                                        .addComponent(lblAddedCurrentMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)
                                        .addComponent(lblAddedProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 151, javax.swing.GroupLayout.PREFERRED_SIZE)))))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel18Layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cmbDates, javax.swing.GroupLayout.PREFERRED_SIZE, 113, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel18Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel12)
                    .addContainerGap(864, Short.MAX_VALUE)))
            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel18Layout.createSequentialGroup()
                    .addGap(22, 22, 22)
                    .addComponent(jLabel33, javax.swing.GroupLayout.PREFERRED_SIZE, 219, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addContainerGap(813, Short.MAX_VALUE)))
        );
        jPanel18Layout.setVerticalGroup(
            jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel18Layout.createSequentialGroup()
                .addGap(61, 61, 61)
                .addComponent(cmbDates, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane5, javax.swing.GroupLayout.PREFERRED_SIZE, 146, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(69, 69, 69)
                .addComponent(jScrollPane6, javax.swing.GroupLayout.PREFERRED_SIZE, 180, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(28, 28, 28)
                .addComponent(jLabel8)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel34, javax.swing.GroupLayout.PREFERRED_SIZE, 28, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(lblAddedBalance, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel35)
                    .addComponent(lblAddedCurrentMoney, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel36)
                    .addComponent(lblAddedProfit, javax.swing.GroupLayout.PREFERRED_SIZE, 16, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel18Layout.createSequentialGroup()
                    .addGap(58, 58, 58)
                    .addComponent(jLabel12)
                    .addContainerGap(573, Short.MAX_VALUE)))
            .addGroup(jPanel18Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                .addGroup(jPanel18Layout.createSequentialGroup()
                    .addGap(279, 279, 279)
                    .addComponent(jLabel33)
                    .addContainerGap(352, Short.MAX_VALUE)))
        );

        jPanel17.setBackground(new java.awt.Color(82, 140, 159));
        jPanel17.setBorder(javax.swing.BorderFactory.createEmptyBorder(25, 25, 25, 25));

        jLabel3.setFont(new java.awt.Font("Dialog", 0, 24)); // NOI18N
        jLabel3.setText("Transaction History");
        jPanel17.add(jLabel3);

        javax.swing.GroupLayout jPanel16Layout = new javax.swing.GroupLayout(jPanel16);
        jPanel16.setLayout(jPanel16Layout);
        jPanel16Layout.setHorizontalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jPanel17, javax.swing.GroupLayout.DEFAULT_SIZE, 1078, Short.MAX_VALUE)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel18, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addContainerGap())
        );
        jPanel16Layout.setVerticalGroup(
            jPanel16Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel16Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel17, javax.swing.GroupLayout.PREFERRED_SIZE, 92, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel18, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(52, Short.MAX_VALUE))
        );

        mainTabPane.addTab("Transaction History", new javax.swing.ImageIcon(getClass().getResource("/icons/history.png")), jPanel16); // NOI18N

        jMenu1.setText("File");

        menuItemSelectDatabaseLocation.setText("Select database location");
        jMenu1.add(menuItemSelectDatabaseLocation);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");
        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainTabPane)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGap(75, 75, 75)
                .addComponent(mainTabPane))
        );

        mainTabPane.getAccessibleContext().setAccessibleName("");

        pack();
        setLocationRelativeTo(null);
    }// </editor-fold>//GEN-END:initComponents

    private void rdbUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbUpdateActionPerformed
        // TODO add your handling code here:
//        this.productsPaneHandler(this.btnGrp.getSelection().getActionCommand());
        index = tblProductLists.getSelectedRow();
       
        this.resetInput("PRODUCTS");
        System.out.println(index != 1);
        if(index != -1){
        
            displayProductData(index);
        }
        
    }//GEN-LAST:event_rdbUpdateActionPerformed

    private void rdbAddActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbAddActionPerformed
        // TODO add your handling code here:
        this.enableWidgets();
        this.resetInput("PRODUCTS");
    }//GEN-LAST:event_rdbAddActionPerformed

    private void rdbDeleteActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rdbDeleteActionPerformed
        // TODO add your handling code here:
//        this.productsPaneHandler(this.btnGrp.getSelection().getActionCommand());
        this.resetInput("PRODUCTS");
    }//GEN-LAST:event_rdbDeleteActionPerformed

    private void jButton3ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton3ActionPerformed
        // TODO add your handling code here:
        this.resetInput("BUY");
    }//GEN-LAST:event_jButton3ActionPerformed

    private void tblProductListsMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblProductListsMouseClicked
        // TODO add your handling code here:
        index = tblProductLists.getSelectedRow();
        this.resetInput("PRODUCTS");
        if(rdbUpdate.isSelected()){
        
            displayProductData(index);
        }
    }//GEN-LAST:event_tblProductListsMouseClicked

    private void btnPlaceOrderActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnPlaceOrderActionPerformed
        // TODO add your handling code here:
        String prodName = cmbProductName.getSelectedItem()+"";
        int itemQuantity = (int)spnBuyQuantity.getValue();
        Customer customer = new Customer();
        if(!products.isEmpty()){
            
            
            if(txtCustomerMoney.getText().trim().isEmpty()){
            
                if(txtCustomerName.getText().trim().isEmpty()){
                        message = "Please enter the name of the customer if its utang :P";
                        JOptionPane.showMessageDialog(null, message, "Message",JOptionPane.INFORMATION_MESSAGE);
                }else {
                    
                    message = "Do you want to add it to the debt list?";
                    int choice = JOptionPane.showConfirmDialog(null, message,"Are you sure?", JOptionPane.YES_OPTION,JOptionPane.QUESTION_MESSAGE);
                
                    if(choice == JOptionPane.YES_OPTION){
                    
                        String customerName = txtCustomerName.getText().toUpperCase().trim();
                        CustomerDebt debt = new CustomerDebt(prodName,itemQuantity,total,
                        LoggerClass.months[LoggerClass.cal.get(Calendar.MONTH)]+","
                        +LoggerClass.cal.get(Calendar.DATE)+","+LoggerClass.cal.get(Calendar.YEAR));
                        
                        int indexNum = this.isCustomerExist(customerName);
                       if(indexNum != -1){
                           customers.get(indexNum).saveDebtInfo(debt);
                           buyItem(prodName, itemQuantity, true);
                           
                       }else {
                       
                        customer.setName(customerName);
                        customer.saveDebtInfo(debt);
                        customers.add(customer);
                        buyItem(prodName, itemQuantity, true);
                       }
                        
                       updateCustomerTable();
                        resetInput("BUY");
                        
                    }
                    
                }
            } else if(exchange < 0){
            
                message = "Insufficient Money";
                JOptionPane.showMessageDialog(null, message, "Message",JOptionPane.INFORMATION_MESSAGE);
            }else {
            
                buyItem(prodName, itemQuantity, false);
              
            }
            
        }else {
        
            message = "Product list is empty. Add new items to the inventory before buying";
            JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
        }
    }//GEN-LAST:event_btnPlaceOrderActionPerformed

    private void chkboxCaseBuyItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chkboxCaseBuyItemStateChanged
        // TODO add your handling code here:
        selectedProduct = getItem(cmbProductName.getSelectedItem()+"");
        if(chkboxCaseBuy.isSelected()){
       
            spnModel = new SpinnerNumberModel(1,1,selectedProduct.getcaseQuantity(),1);
            
        }else {
        
            spnModel = new SpinnerNumberModel(1,1,selectedProduct.getRemainingBottles(),1);
        }
       
            spnBuyQuantity.setModel(spnModel);
        printTotalValues();
    }//GEN-LAST:event_chkboxCaseBuyItemStateChanged

    private void cmbProductNameItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbProductNameItemStateChanged
        // TODO add your handling code here:
        if(!this.addProdAction){
            selectedProduct = getItem(cmbProductName.getSelectedItem()+"");
            spnModel = new SpinnerNumberModel(1,1,selectedProduct.getRemainingBottles(),1);
            if(chkboxCaseBuy.isSelected())
                spnModel = new SpinnerNumberModel(1,1,selectedProduct.getcaseQuantity(),1);
            spnBuyQuantity.setModel(spnModel);
            printTotalValues();
        }
        
    }//GEN-LAST:event_cmbProductNameItemStateChanged

    private void spnBuyQuantityStateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_spnBuyQuantityStateChanged
        // TODO add your handling code here:
        quantity = (int)spnBuyQuantity.getValue();
        printTotalValues();
    }//GEN-LAST:event_spnBuyQuantityStateChanged

    private void txtCustomerMoneyKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtCustomerMoneyKeyReleased
        // TODO add your handling code here:
        
        try {
        lblExchange.setText("");
            if(total != 0){
            
            double customerMoney = Double.parseDouble(txtCustomerMoney.getText().trim());
            exchange = (customerMoney - total);
            lblExchange.setText(format.format(exchange)+" Php");
            }
            
            
        }catch(NumberFormatException e){
        
            lblExchange.setText("Invalid input.");
        }
    }//GEN-LAST:event_txtCustomerMoneyKeyReleased

    private void tblProductListsKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblProductListsKeyPressed
        
        if(evt.getKeyChar() == KeyEvent.VK_ENTER){
        
            message = "Are you sure you want to delete this product?";
            var choice = JOptionPane.showConfirmDialog(null, message, "Action", JOptionPane.INFORMATION_MESSAGE);
            index = tblProductLists.getSelectedRow();
            if(choice == JOptionPane.YES_OPTION){
            
                products.remove(index);
                message = "The selected product is now removed.";
                JOptionPane.showMessageDialog(null, message, "Info", JOptionPane.INFORMATION_MESSAGE);
            }
            
        
        }
    }//GEN-LAST:event_tblProductListsKeyPressed

    private void tblCustomerListMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerListMouseClicked
        // TODO add your handling code here:
        String customerName = tblCustomerList.getValueAt(tblCustomerList.getSelectedRow(), 0)+"";
        Customer obj = null;
        index = tblCustomerList.getSelectedRow();
        customerIndex = index;
        System.out.println(customerIndex);
        for(Customer names : customers){
        
            if(names.getName().equalsIgnoreCase(customerName)){
            
                obj = names;
                break;
            }
        }
        
        getDebtInfo(obj);
        
    }//GEN-LAST:event_tblCustomerListMouseClicked

    private void formWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_formWindowClosing
        // TODO add your handling code here:
        ArrayList<String> allData = new ArrayList<>();
        String data = "";
        for(Products e: products){
        
          data += this.dataProductFormatter(e);
        }
        allData.add(data);
        data = "";
        for(Customer user: customers){
        
            data += this.productFormat(user);
        }
        
        allData.add(data);
        data = this.formatStoreCapital(investment,balance,profit,currentMoney);
        allData.add(data);
            
        this.dataHandler.updateData(allData);
        
    }//GEN-LAST:event_formWindowClosing

    private void txtTuboKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtTuboKeyPressed
        // TODO add your handling code here:
        updateProduct(evt);
    }//GEN-LAST:event_txtTuboKeyPressed

    private void txtProductNameKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtProductNameKeyPressed
        // TODO add your handling code here:
        updateProduct(evt);
    }//GEN-LAST:event_txtProductNameKeyPressed

    private void txtPuhunanKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_txtPuhunanKeyPressed
        // TODO add your handling code here:
      updateProduct(evt);
    }//GEN-LAST:event_txtPuhunanKeyPressed

    private void spnCaseQuantityKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spnCaseQuantityKeyPressed
        // TODO add your handling code here:
       updateProduct(evt);
    }//GEN-LAST:event_spnCaseQuantityKeyPressed

    private void spnQuantityPerCaseKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spnQuantityPerCaseKeyPressed
        // TODO add your handling code here:
        updateProduct(evt);
    }//GEN-LAST:event_spnQuantityPerCaseKeyPressed

    private void cmbProductTypeKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_cmbProductTypeKeyPressed
        // TODO add your handling code here:
        updateProduct(evt);
    }//GEN-LAST:event_cmbProductTypeKeyPressed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        mainTabPane.setSelectedIndex(4);
    }//GEN-LAST:event_jButton1ActionPerformed

    private void tblCustomerListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tblCustomerListKeyPressed
        // TODO add your handling code here:
        if(evt.getKeyChar() == KeyEvent.VK_ENTER){
        
            selectedCustomer = this.getCustomer(tblCustomerList.getValueAt(tblCustomerList.getSelectedRow(), 0)+"");
            initializeCustomerInfo();
        }
    }//GEN-LAST:event_tblCustomerListKeyPressed

    private void tblCustomerInfoMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblCustomerInfoMouseClicked
        // TODO add your handling code here:
        index = tblCustomerInfo.getSelectedRow();
        CustomerDebt info = selectedCustomer.getDebtList().get(index);
        lblProductName.setText(info.getProductName());
        spnProductQuantity.setModel(new SpinnerNumberModel(1,1,info.getQuantity(),1));
        spnProductQuantity.setValue(info.getQuantity());
        total = info.getTotal();
        txtPrice.setText(format.format(total));
        
        
    }//GEN-LAST:event_tblCustomerInfoMouseClicked

    private void btnEditNameActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnEditNameActionPerformed
        // TODO add your handling code here:
        message = "Enter the new name of the customer";
        
        try {
        
            String newName = JOptionPane.showInputDialog(null, message, "Enter name", 
                JOptionPane.QUESTION_MESSAGE).trim().toUpperCase();
            if(!newName.isEmpty()){
        
                message = "Are you sure you want to change the name to " + newName +" ?";
                if(JOptionPane.showConfirmDialog(null, message,"Alert", JOptionPane.YES_NO_OPTION)
                == JOptionPane.YES_OPTION){
        
                String oldName = customers.get(customerIndex).getName();
                customers.get(customerIndex).setName(newName);
                lblCustomerName.setText(newName);
                for(int i=0; i<tblCustomerModel.getRowCount(); i++){
                
                    System.out.println(tblCustomerModel.getValueAt(i, 0));
                    if(tblCustomerModel.getValueAt(i, 0).equals(oldName)){
                    
                        tblCustomerModel.setValueAt(newName, i, 0);
                        break;
                    }
                }
                
            }
        }else {
        
            message = "Name cannot be blank.";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        }catch(HeadlessException | NullPointerException e){
        
            message = "Name cannot be blank.";
            JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE);
        }
        
        
    }//GEN-LAST:event_btnEditNameActionPerformed

    private void chbxisPaidItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_chbxisPaidItemStateChanged
        // TODO add your handling code here:
        if(chbxisPaid.isSelected()){
        
            txtPrice.setText(format.format(total));
            txtPrice.setEditable(false);
        }else{
        
            txtPrice.setEditable(true);
        }
    }//GEN-LAST:event_chbxisPaidItemStateChanged

    private void btnUpdateActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnUpdateActionPerformed
        // TODO add your handling code here:'
        message = "Are you sure to paid this customer item?";
        int choice;
        if(!lblProductName.getText().isEmpty()){
        
            if(chbxisPaid.isSelected()){
        
            choice = JOptionPane.showConfirmDialog(null, message, "Confirm?",JOptionPane.YES_NO_OPTION);
            
            if(choice == JOptionPane.YES_OPTION){
            
              selectedCustomer.getDebtList().remove(this.getCustomerItemDebt(lblProductName.getText(), selectedCustomer.getDebtList()));
              for(int i=0; i<tblCustomerInfo.getRowCount(); i++){
              
                  if(tblCustomerInfo.getValueAt(i, 0).equals(lblProductName.getText())){
                  
                      System.out.println("Found You!");
                      tblCustomerInfoModel.removeRow(i);
                      break;
                  }
              }
                
            }
        }
        }else {
        
           message = "Select the product first before updating it.";
           JOptionPane.showMessageDialog(null, message, "Alert", JOptionPane.WARNING_MESSAGE);
        }
        
    }//GEN-LAST:event_btnUpdateActionPerformed

    private void dialogShowDebtListWindowClosing(java.awt.event.WindowEvent evt) {//GEN-FIRST:event_dialogShowDebtListWindowClosing
        // TODO add your handling code here:
        updateCustomerWindow();
        
    }//GEN-LAST:event_dialogShowDebtListWindowClosing

    private void cmbDatesItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_cmbDatesItemStateChanged
        // TODO add your handling code here:
    }//GEN-LAST:event_cmbDatesItemStateChanged

    private void tblTransactionHistoryMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_tblTransactionHistoryMouseClicked
        // TODO add your handling code here:
        tblSelectedTransactHistoryModel.setRowCount(0);
        String month = tblTransactionHistory.getValueAt(tblTransactionHistory.getSelectedRow(), 0)+"";
        int year = Integer.parseInt(tblTransactionHistory.getValueAt(tblTransactionHistory.getSelectedRow(), 2)+"");
        updateTransactionData(month,year);
        
    }//GEN-LAST:event_tblTransactionHistoryMouseClicked

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("System".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(Dashboard.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new Dashboard().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnClearInfo;
    private javax.swing.JButton btnEditName;
    private javax.swing.ButtonGroup btnGrp;
    private javax.swing.JButton btnPlaceOrder;
    private javax.swing.JButton btnSummary;
    private javax.swing.JButton btnUpdate;
    private javax.swing.JCheckBox chbxPerItem;
    private javax.swing.JCheckBox chbxisPaid;
    private javax.swing.JCheckBox chkboxCaseBuy;
    private javax.swing.JComboBox<Integer> cmbDates;
    private javax.swing.JComboBox<String> cmbProductName;
    private javax.swing.JComboBox<String> cmbProductType;
    private javax.swing.JDialog dialogShowDebtList;
    private javax.swing.JDialog dialogSummaryWindow;
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButton2;
    private javax.swing.JButton jButton3;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel10;
    private javax.swing.JLabel jLabel11;
    private javax.swing.JLabel jLabel12;
    private javax.swing.JLabel jLabel13;
    private javax.swing.JLabel jLabel14;
    private javax.swing.JLabel jLabel15;
    private javax.swing.JLabel jLabel16;
    private javax.swing.JLabel jLabel17;
    private javax.swing.JLabel jLabel18;
    private javax.swing.JLabel jLabel19;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel20;
    private javax.swing.JLabel jLabel21;
    private javax.swing.JLabel jLabel22;
    private javax.swing.JLabel jLabel23;
    private javax.swing.JLabel jLabel24;
    private javax.swing.JLabel jLabel25;
    private javax.swing.JLabel jLabel26;
    private javax.swing.JLabel jLabel27;
    private javax.swing.JLabel jLabel28;
    private javax.swing.JLabel jLabel29;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel30;
    private javax.swing.JLabel jLabel31;
    private javax.swing.JLabel jLabel32;
    private javax.swing.JLabel jLabel33;
    private javax.swing.JLabel jLabel34;
    private javax.swing.JLabel jLabel35;
    private javax.swing.JLabel jLabel36;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JLabel jLabel6;
    private javax.swing.JLabel jLabel7;
    private javax.swing.JLabel jLabel8;
    private javax.swing.JLabel jLabel9;
    private javax.swing.JMenu jMenu1;
    private javax.swing.JMenu jMenu2;
    private javax.swing.JMenuBar jMenuBar1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel10;
    private javax.swing.JPanel jPanel11;
    private javax.swing.JPanel jPanel12;
    private javax.swing.JPanel jPanel13;
    private javax.swing.JPanel jPanel14;
    private javax.swing.JPanel jPanel15;
    private javax.swing.JPanel jPanel16;
    private javax.swing.JPanel jPanel17;
    private javax.swing.JPanel jPanel18;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JPanel jPanel6;
    private javax.swing.JPanel jPanel7;
    private javax.swing.JPanel jPanel8;
    private javax.swing.JPanel jPanel9;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JScrollPane jScrollPane2;
    private javax.swing.JScrollPane jScrollPane3;
    private javax.swing.JScrollPane jScrollPane4;
    private javax.swing.JScrollPane jScrollPane5;
    private javax.swing.JScrollPane jScrollPane6;
    private javax.swing.JLabel lblAddedBalance;
    private javax.swing.JLabel lblAddedCurrentMoney;
    private javax.swing.JLabel lblAddedProfit;
    private javax.swing.JLabel lblCurrentMoney;
    private javax.swing.JLabel lblCurrentPaid;
    private javax.swing.JLabel lblCustomerName;
    private javax.swing.JLabel lblExchange;
    private javax.swing.JLabel lblFindStatus;
    private javax.swing.JLabel lblInvestment;
    private javax.swing.JLabel lblProductName;
    private javax.swing.JLabel lblProfit;
    private javax.swing.JLabel lblTotalPrice;
    private javax.swing.JTabbedPane mainTabPane;
    private javax.swing.JMenuItem menuItemSelectDatabaseLocation;
    private javax.swing.JRadioButton rdbAdd;
    private javax.swing.JRadioButton rdbDelete;
    private javax.swing.JRadioButton rdbUpdate;
    private javax.swing.JSpinner spnBuyQuantity;
    javax.swing.JSpinner spnCaseQuantity;
    private javax.swing.JSpinner spnProductQuantity;
    private javax.swing.JSpinner spnQuantityPerCase;
    private javax.swing.JTable tblCustomerInfo;
    private javax.swing.JTable tblCustomerList;
    private javax.swing.JTable tblProductLists;
    private javax.swing.JTable tblSelectedTransactHistory;
    private javax.swing.JTable tblTransactionHistory;
    private javax.swing.JTextField txtCustomerMoney;
    private javax.swing.JTextField txtCustomerName;
    private javax.swing.JTextArea txtDebtInfoArea;
    private javax.swing.JTextField txtFindName;
    private javax.swing.JTextField txtPrice;
    private javax.swing.JTextField txtProductName;
    private javax.swing.JTextField txtPuhunan;
    private javax.swing.JTextField txtTubo;
    // End of variables declaration//GEN-END:variables
}
