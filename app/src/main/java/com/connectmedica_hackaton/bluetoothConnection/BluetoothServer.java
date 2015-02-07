package com.connectmedica_hackaton.bluetoothConnection;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;

import java.io.IOError;
import java.io.IOException;
import java.util.UUID;

/**
 * Created by KosmatyKosmos on 07.02.15.
 */
public class BluetoothServer extends ActionBarActivity implements Runnable {
    private BluetoothAdapter adapter= BluetoothAdapter.getDefaultAdapter();
    private Intent intent= new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
    private BluetoothServerSocket serverSocket;
    private UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
    private static final int REQUEST_ENABLE_BT = 42;

    public BluetoothServer(){
    if(!adapter.isEnabled()){
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent,REQUEST_ENABLE_BT );
    }
        setVisibility();




    }
    private void setVisibility(){
        Intent discoverableIntent = new
        Intent(BluetoothAdapter.ACTION_REQUEST_DISCOVERABLE);
        discoverableIntent.putExtra(BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION, 666);

        startActivity(discoverableIntent);
        setServerMode();
    }

    private void setServerMode(){
        try {
            serverSocket = adapter.listenUsingRfcommWithServiceRecord("ServerHackation", MY_UUID);
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        while(true){
            try {
                Thread runnableThread = new Thread(new BluetoothThread(serverSocket.accept()));
            }catch(IOException e){
                e.printStackTrace();
            }
        }


    }
}
