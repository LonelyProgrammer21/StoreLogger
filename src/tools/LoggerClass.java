/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package tools;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Calendar;
import java.util.GregorianCalendar;
/**
 *
 * @author lonelyprogrammer
 */
public class LoggerClass {
    
    private static String absoluteDirectory = "";
    private static final String fileName = "log.txt";
    private static File logFile = null;
    private static BufferedWriter writer = null;
    public static final GregorianCalendar cal = new GregorianCalendar();
    public static final String[] months = {"January", "February", "March", "April",
    "May", "June", "July", "August", "September", "October", "November",
    "December"};
    

    public static void setDefaultDirectory(String path){
    
        absoluteDirectory = path+System.getProperty("file.separator");
        logFile = new File(absoluteDirectory+fileName);
        
        try {
            
            if(!logFile.exists())
             logFile.createNewFile();
            
            writer = new BufferedWriter(new FileWriter(logFile, true));
        } catch (IOException ex) {
            writeLog(ex.getMessage());
        }
        
       
    }
    
    public static void writeLog(String type){
    
        String data;
        
        String date = months[cal.get(Calendar.MONTH)];
        int day = cal.get(Calendar.DATE);
        int hour = cal.get(Calendar.HOUR);
        int minutes = cal.get(Calendar.MINUTE);
        int seconds = cal.get(Calendar.SECOND);
        try{
            
            
            data = String.format("USER SELECT %s At [ %s, %d ] ON [ %d:%d:%d ]\n",
                type, date,day,hour,minutes,seconds);
            writer.write(data);
            writer.flush();
        
        }catch(IOException e){
        
            
        }
    
    }
 
    
}
