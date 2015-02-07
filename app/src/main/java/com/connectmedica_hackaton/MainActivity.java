package com.connectmedica_hackaton;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.connectmedica_hackaton.bluetoothConnection.BluetoothServer;
import com.connectmedica_hackaton.http.AbstractHttp;
import com.connectmedica_hackaton.http.HttpGetStats;
import com.connectmedica_hackaton.http.HttpMe;
import com.connectmedica_hackaton.http.HttpPostPuff;
import com.connectmedica_hackaton.model.User;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.StringReader;
import java.net.URL;
import java.util.Calendar;
import java.util.Set;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements AbstractHttp.OnAjaxResult<JSONObject>
{
    private static final int REQUEST_ENABLE_BT = 42;
    private long startTime = 0;
    private long endTime = 0;
    private BluetoothServer server;
    private Thread ServerThr = new Thread(server);

    private class BTServerTask extends AsyncTask<Void, Void, BluetoothSocket>
    {
        private BluetoothServerSocket mmServerSocket;
        private UUID MY_UUID = UUID.fromString("0000110a-0000-1000-8000-00805f9b34fb");
        private static final String ADDRESS = "00:12:6F:26:7A:47";
        // fa87c0d0-afac-11de-8a39-0800200c9a66
        private static final String NAME = "server";
        private BluetoothDevice ourDevice = null;

        protected BluetoothSocket doInBackground(Void... urls)
        {

            BluetoothServerSocket tmp = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

            if (pairedDevices.size() > 0) {
                // Loop through paired devices
                for (BluetoothDevice device : pairedDevices) {
                    if (device.getAddress().equals(ADDRESS))
                        ourDevice = device;
                }


                mmServerSocket = tmp;
                BluetoothSocket socket = null;

                try {
                    socket = ourDevice.createRfcommSocketToServiceRecord(ourDevice.getUuids()[0].getUuid());
                    socket.connect();

                    return socket;
                } catch (IOException e) {
                    e.printStackTrace();
                }

                return socket;
            }

            return null;
        }

        protected void onPostExecute(BluetoothSocket result)
        {
            manageBTConnection(result);
        }
    }

    private void manageBTConnection(BluetoothSocket socket)
    {
        final BluetoothSocket connectionSocket = socket;
        InputStream inputStream = null;
        OutputStream outputStream = null;

        // Get the input and output streams, using temp objects because
        // member streams are final
        try
        {
            inputStream = connectionSocket.getInputStream();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }

        final InputStream input = inputStream;

        Thread workerThread = new Thread() {

            @Override
            public void run()
            {
                byte[] buffer = new byte[1024];  // buffer store for the stream
                int bytes; // bytes returned from read()
                BufferedReader r = new BufferedReader(new InputStreamReader(input));
                String line;

                // Keep listening to the InputStream until an exception occurs
                while (true)
                {
                    try
                    {
                        line = r.readLine();

//                        bytes = input.read(buffer);

                        // Send the obtained bytes to the UI activity
                        Log.d("mazurek", line);
                        sendPuffData(line);

                    } catch (IOException e) {
                        e.printStackTrace();
                        break;
                    }
                }

                try
                {
                    connectionSocket.close();
                    r.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

        workerThread.start();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
//        ServerThr.start();
//        getData();

//        BTServerTask task = new BTServerTask();
//        task.execute();
    }

    protected void sendPuffData(String message)
    {
        if (message.equals("wcislem przyciska"))
        {
            Calendar currentTime = Calendar.getInstance();
            startTime = currentTime.getTimeInMillis();
        }
        else if (message.equals("wycislem przyciska"))
        {
            Calendar currentTime = Calendar.getInstance();
            endTime = currentTime.getTimeInMillis();

            long diff = endTime - startTime;

            HttpPostPuff puff = new HttpPostPuff(this, startTime, diff);

            puff.onResult(new AbstractHttp.OnAjaxResult<JSONObject>()
            {
                @Override
                public void onResult(JSONObject data)
                {
                    refreshView(data);
                }

                @Override
                public void onError(String message)
                {

                    Log.e("mazurek", message);
                }
            });

            puff.run();
        }
    }

    private void getData()
    {
        HttpGetStats stats = new HttpGetStats(getApplicationContext());
        stats.onResult(new AbstractHttp.OnAjaxResult<JSONObject>() {
            @Override
            public void onResult(JSONObject data) {
                refreshView(data);
            }

            @Override
            public void onError(String message) {
                delayGetData();
            }
        });
        stats.run();
    }

    private void delayGetData() {
        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        getData();
                    }
                }
        , 1000);

    }

    private void refreshView(JSONObject data) {
        TextView click_counter = (TextView)findViewById(R.id.click_count);
        TextView time_counter  = (TextView)findViewById(R.id.seconds_count);
        int clicks = data.optJSONObject("today").optInt("puffs");
        click_counter.setText("" + clicks);
        double milliseconds = data.optJSONObject("today").optDouble("milliseconds");
        double seconds = milliseconds / 1000;
        time_counter.setText("" + seconds + " s");
        delayGetData();
    }

    @Override
    public void onResult(JSONObject data)
    {
        Toast.makeText(this, data.toString(), Toast.LENGTH_LONG).show();
    }

    @Override
    public void onError(String message)
    {
        Toast.makeText(this, "Error", Toast.LENGTH_LONG).show();
    }

    public void startBluetooth(View button)
    {
        BluetoothAdapter adapter = BluetoothAdapter.getDefaultAdapter();

        if (!adapter.isEnabled())
        {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    public void startDebugActivity(View button)
    {
        Intent startIntent = new Intent(this, DebugPuffActivity.class);
        startActivity(startIntent);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && requestCode == REQUEST_ENABLE_BT)
        {
            Toast.makeText(this, "Bluetooth enabled.", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
