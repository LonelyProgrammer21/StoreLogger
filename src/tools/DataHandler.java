/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Scanner;
import java.util.Set;
import misc.Transactions;
import products.Beer;
import products.Products;
import products.PureDrinks;
import user.Customer;
import user.CustomerDebt;

/**
 *
 * @author lonelyprogrammer
 */
public final class DataHandler {
    
    private final String[] fileNames = {"productsFile.txt","customersDataFile.txt","storeTotalCapital.txt", "transactionData.txt"};
    private final int ARRSIZE = fileNames.length;
    
    private final BufferedWriter[] writers = new BufferedWriter[ARRSIZE];
    private final BufferedReader[] readers = new BufferedReader[ARRSIZE];
    private final File[] storeloggerFiles = new File[ARRSIZE];
    
    private final String[] filePaths = new String[ARRSIZE];
    private final String USERENV[] = {"HOME", "USERPROFILE"};

    private boolean haveError = false;
    private final boolean[] hasData = new boolean[ARRSIZE];
    private final Scanner[] sc = new Scanner[ARRSIZE];
    private String extractedData;
    public DataHandler(){
    
        this.checkData();
    
    }
    
    
    public void checkData(){
    
       String osType = System.getProperty("os.name");
       String separator = System.getProperty("file.separator");
       
       switch(osType.toUpperCase()){
       
           case "LINUX" -> {
               
                for(int i=0; i < fileNames.length; i++){
                
                    filePaths[i] = System.getenv(USERENV[0])+separator+"StoreLoggerFile"+separator+fileNames[i];
                }
               
               initData();
            }
           case "WINDOWS" -> {

                for(int i=0; i < fileNames.length; i++){
                
                    filePaths[i] = System.getenv(USERENV[1])+separator+"StoreLoggerFile"+separator+fileNames[i];
                }
               initData();
            }
           default -> {
        
                   initData();
            }
       
       }
    
    }
    
    public void saveProductData(String data){
    
        
        this.hasData[0] = true;
        
        try{
        
            writers[0].write(data);
            writers[0].flush();
        
        }catch(IOException e){}
    }
    
    public void updateData(ArrayList<String> data){
    
        try{
        
            for(int i=0; i<data.size(); i++){
        
                writers[i] = new BufferedWriter(new FileWriter(filePaths[i]));
                writers[i].write(data.get(i));
                writers[i].flush();
        
            }
        }catch(IOException ex){
            
            
        }
        
       
        
    }
    
    public void saveCustomerData(String data){
    
        this.hasData[1] = true;
        try {
        
            writers[1].write(data);
            writers[1].flush();
            
        }catch(IOException e){
        
        }
    
    }
    
    public void initData(){
    
       try{

            for(int i=0; i < fileNames.length; i++){
            
                storeloggerFiles[i] = new File(filePaths[i]);
                System.out.println(filePaths[i]);
                if(!this.storeloggerFiles[i].getParentFile().exists()){
                
                    this.storeloggerFiles[i].getParentFile().mkdirs();
                }
                if(!this.storeloggerFiles[i].exists()){
                
                    this.storeloggerFiles[i].createNewFile();
                }
                
              
                
                readers[i] = new BufferedReader(new FileReader(storeloggerFiles[i]));
                writers[i] = new BufferedWriter(new FileWriter(storeloggerFiles[i],true));
                sc[i] = new Scanner(storeloggerFiles[i]);
                hasData[i] = false;
            }
           
           
           LoggerClass.setDefaultDirectory(storeloggerFiles[0].getParent());
            
           scanData();
       }catch(IOException ex){
       
           haveError = true;
       }
    
    }
    
    private void scanData(){
    
        for(int i=0; i < sc.length; i++){
        
            if(sc[i].hasNextLine()){
            
                hasData[i] = true;
            }
        }
        
    }
    
    public HashMap<Integer,ArrayList<Transactions>> retrieveTransactions(){
    
        HashMap<Integer,ArrayList<Transactions>> transactionCollections = new HashMap<>();
        ArrayList<Transactions> data = new ArrayList<>();
        Transactions transactionData = null;
        Set<Integer> yearSet = new HashSet();
        
        while(sc[3].hasNextLine()){
        
            extractedData = sc[3].nextLine();
            if(extractedData.startsWith("[")){
            
                transactionData = new Transactions();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setProductName(extractedData);
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setQuantity(Integer.parseInt(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setPrice(Double.parseDouble(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setTotalBalance(Double.parseDouble(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setProfit(Double.parseDouble(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setMonth(extractedData);
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setDate(Integer.parseInt(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setYear(Integer.parseInt(extractedData));
                yearSet.add(Integer.parseInt(extractedData));
                extractedData = sc[3].nextLine();
                extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                transactionData.setTime(extractedData);
                data.add(transactionData);
                
            }
            
            for(int years : yearSet){
            
                ArrayList<Transactions> temp = new ArrayList<>();
                for(Transactions e : data){
                
                    if(e.getYear() == years){
                    
                        temp.add(e);
                    }
                    transactionCollections.put(years, temp);
                }
            }
            
        }
        
        for(ArrayList<Transactions> e: transactionCollections.values()){
        
            for(Transactions f : e){
            
                System.out.println(f.getProductName());
            }
        }
        return transactionCollections;
    }
    
    public ArrayList<Products> retrievedData(){
    
        ArrayList<Products> productsCollection = new ArrayList<>();
        Products products = null;
        int count = 0;
        try{
            
            while(sc[0].hasNextLine()){
        
            String data = sc[0].nextLine();
            if(data.contains("[")){
            
                
                if(data.contains("[PRODUCT-TYPE]: ")){
                
                     String prodType = data.substring(data.indexOf(":")+1).toUpperCase().trim();
                     switch(prodType){
                     
                         case "BEER" -> {
                             products = new Beer();
                             products.setProductType("Beer");
                        }
                         case "PURE DRINKS" -> {
                             products = new PureDrinks();
                             products.setProductType("Pure Drinks");
                        }
                         
                     }
                     
                     continue;
                }
               
                extractedData = data.substring(data.indexOf(":")+1).trim();
                switch(count){
                    
                        case 0 -> products.setName(extractedData);
                        case 1 -> products.setCaseQuantity(Integer.parseInt(extractedData));
                        case 2 -> products.setquantityPerCase(Integer.parseInt(extractedData));
                        case 3 -> products.setRemainingBottles(Integer.parseInt(extractedData));
                        case 4 -> products.setPuhunan(Double.parseDouble(extractedData));
                        case 5 -> products.setitemTubo(Double.parseDouble(extractedData));
                    }

                count++;
            }else {
                count = 0;
                Computator compute = new Computator(products.getPuhunan(),products.getTubo(),
                products.getcaseQuantity(), products.getquantityPerCase());
                products.setPricePerCase(compute.getPricePerCase());
                products.setPricePerItem(compute.getPricePerItem());
                productsCollection.add(products);

            }
            
        }
        
        }catch(NumberFormatException | NullPointerException e){
        
            this.haveError = true;
        }
        sc[0].reset();
        return productsCollection;
    }
    
    public double[] generatestoreCapital(){
    
        double[] storeInfo = new double[4];
        String data = "";
        
        for(int i=0; i<storeInfo.length; i++){
        
            data = sc[2].nextLine();
            storeInfo[i] = Double.parseDouble(data.substring(data.indexOf(": ")+1).trim());
            
        }
        
        return storeInfo;
    }
    
    public ArrayList<Customer> customerData(){
    
        ArrayList<Customer> customers = new ArrayList<>();
        boolean onItemField = false;
        Customer users = null;

        while(sc[1].hasNextLine()){
        
          extractedData = sc[1].nextLine();
          if(extractedData.contains("[CUSTOMER-NAME]")){
          
              users = new Customer();
              extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
              users.setName(extractedData);
              
          }else {
          
              if(extractedData.contains("{")){
              
                  onItemField = true;
                  continue;
              }else if(extractedData.contains("}")){
              
                  onItemField = false;
                  customers.add(users);
                  
              }
              if(onItemField){
              
                  if(extractedData.contains("[DATE]: ")){
                  
                      CustomerDebt items = new CustomerDebt();
                      extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                      items.setDate(extractedData);
                      extractedData = sc[1].nextLine();
                      extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                      items.setProductName(extractedData);
                      extractedData = sc[1].nextLine();
                      extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                      items.setQuantity(Integer.parseInt(extractedData));
                      extractedData = sc[1].nextLine();
                      extractedData = extractedData.substring(extractedData.indexOf(": ")+1).trim();
                      items.setTotal(Double.parseDouble(extractedData));
                      users.saveDebtInfo(items);
                  }
              }
          }
        }
        
        return customers;
    }
    
    public boolean hasProductData(){
    
    
        return this.hasData[0];
    }
    
    public boolean hasCustomersData(){
    
        return this.hasData[1];
    }
    
    public boolean haveErrors(){
    
        return haveError;
    }
    
    public boolean hasStoreCapital(){
    
    
        return this.hasData[2];
    }
    
    public boolean hasTransactionData(){
    
        return this.hasData[3];
    }
    
}
