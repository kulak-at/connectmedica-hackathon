package com.connectmedica_hackaton.bluetoothConnection;

import android.bluetooth.BluetoothSocket;

import java.io.IOError;
import java.io.IOException;

/**
 * Created by KosmatyKosmos on 07.02.15.
 */
public class BluetoothThread implements Runnable{
    private BluetoothSocket rareSocket=null;

    public BluetoothThread(BluetoothSocket socket){
        rareSocket=socket;
    }
    @Override
    public void run() {
       if(!rareSocket.isConnected()){
           try {
               rareSocket.connect();
           }catch (IOException e){
               e.printStackTrace();
           }
           System.out.println(this.toString() + " " + rareSocket.getRemoteDevice().toString());
       }
    }

    @Override
    public String toString() {
        return rareSocket.toString();
    }
}
