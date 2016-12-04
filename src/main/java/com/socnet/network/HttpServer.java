/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.socnet.network;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author fallgamlet
 */
public class HttpServer implements Runnable {
    public interface Handler {
        String getResponce(String request);
        void onError(Throwable e);
    }
    
    //region Fields
    private Socket mSocket;
    private InputStream mInStream;
    private OutputStream mOutStream;
    private Handler mListener;
    //endregion
    
    public HttpServer(Socket socket, Handler listener) throws Throwable {
        mSocket = socket;
        mInStream = mSocket.getInputStream();
        mOutStream = mSocket.getOutputStream();
        mListener = listener;
    }

    @Override
    public void run() {
        try {
                String request = readInputHeaders();
                writeResponse(mListener.getResponce(request));
            } catch (Throwable t) {
                System.err.println(t);
            } finally {
                try {
                    mSocket.close();
                } catch (Throwable t) {
                    System.err.println(t);
                }
            }
            System.err.println("Client processing finished");
    }
    
    private void writeResponse(String s) throws Throwable {
        String response = "HTTP/1.1 200 OK\r\n" +
                "Server: SocNetApp\r\n" +
                "Content-Type: text/html\r\n" +
                "Content-Length: " + s.length() + "\r\n" +
                "Connection: close\r\n\r\n";
        String result = response + s;
        mOutStream.write(result.getBytes());
        mOutStream.flush();
    }

    private String readInputHeaders() throws Throwable {
        BufferedReader br = new BufferedReader(new InputStreamReader(mInStream));
        StringBuffer buffer = new StringBuffer();
        while(true) {
            String s = br.readLine();
            if(s == null || s.trim().length() == 0) {
                break;
            }
            buffer.append("\n").append(s);
        }
        return buffer.toString();
    }
    
    public static Thread createHttpServer(int port, Handler listener) throws Throwable {
        Thread thread = new Thread(new Runnable(){
            @Override
            public void run() {
                try {
                    ServerSocket ss = new ServerSocket(port);
                    while (true) {
                        Socket s = ss.accept();
                        System.err.println("Client accepted");
                        new Thread(new HttpServer(s, listener)).start();
                    }
                } catch (Throwable e) {
                    listener.onError(e);
                }
            }
        });
        return thread;
    }
}
