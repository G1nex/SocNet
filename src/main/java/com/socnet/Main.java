package com.socnet;

import com.socnet.db.DBHelper;
import com.socnet.network.HttpServer;

public class Main {
    /**
     * @param args the command line arguments
     */
    @SuppressWarnings("empty-statement")
    public static void main(String[] args) {
        try {
            DBHelper dbClient = new DBHelper();
        } catch (Exception ex) {
            System.err.println(ex);
        }
        
        
        try {
            HttpServer.createHttpServer(8888, new HttpServer.Handler() {
                @Override
                public String getResponce(String request) {
                    System.out.println("Request: "+request);
                    return "{\"id\":123, \"message\":\""+request+"\"}";
                }
                
                @Override
                public void onError(Throwable e) {
                    System.err.println("HttpServer error: "+e.toString());
                }
            }).start();
            
            
            long tick=0;
            while(true) {
                Thread.sleep(15000);
                System.out.println("Main thread tick... "+tick);
                tick++;
            }
        } catch (Throwable e) {
            System.err.println(e);
        }
                
    }
}
