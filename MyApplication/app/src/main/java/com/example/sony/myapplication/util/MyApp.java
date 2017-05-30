package com.example.sony.myapplication.util;

import android.app.Application;

import org.json.JSONObject;

import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class MyApp extends Application{
    private Socket audioSocket;

    public Socket getSocket() {
        return audioSocket;
    }

    public void setSocket(int order, final int audio_port) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                //开启语音socket通道
                String ip = "162.105.175.115";
                audioSocket = null;
                try {
                    audioSocket = new Socket(ip,audio_port);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }).start();

        //建立连接后，立马发送order
        /*if (audioSocket.isConnected()) {
            OutputStream os = null;
            try {
                os = audioSocket.getOutputStream();
                JSONObject js = new JSONObject();
                js.put("order", order);
                byte[] sendp = js.toString().getBytes();
                os.write(sendp);
                os.flush();
                os.close();
            } catch (Exception e) {
                e.printStackTrace();
                if (os != null) {
                    try {
                        os.close();
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }*/
    }
}
