package com.socnet;

import com.socnet.db.DBHelper;
import java.sql.Connection;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Main {
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Runnable task = () -> {
            String threadName = Thread.currentThread().getName();
            System.out.println("Hello " + threadName);
        };

        task.run();

        Thread thread = new Thread(task);
        thread.start();

        System.out.println("Done!");
        
        try {
            DBHelper dbClient = new DBHelper();
            System.out.println("DBConnection"+ dbClient.toString());            
        } catch (Exception e) {
            System.err.println("DBError"+ e.toString());
        }

    }
}
