package com.connectmedica_hackaton;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import com.connectmedica_hackaton.bluetoothConnection.BluetoothServer;
import com.connectmedica_hackaton.http.AbstractHttp;
import com.connectmedica_hackaton.http.HttpGetStats;
import com.connectmedica_hackaton.http.HttpMe;
import com.connectmedica_hackaton.http.HttpPostPuff;
import com.connectmedica_hackaton.model.User;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URL;
import java.util.Calendar;
import java.util.UUID;


public class MainActivity extends ActionBarActivity implements AbstractHttp.OnAjaxResult<JSONObject>
{
    private static final int REQUEST_ENABLE_BT = 42;
    private Calendar startTime = null;
    private long endTime = 0;
    private BluetoothServer server;
    private Thread ServerThr = new Thread(server);

    private class BTServerTask extends AsyncTask<Void, Void, BluetoothSocket>
    {
        private BluetoothServerSocket mmServerSocket;
        private UUID MY_UUID = UUID.fromString("fa87c0d0-afac-11de-8a39-0800200c9a66");
        private static final String NAME = "server";

        protected BluetoothSocket doInBackground(Void... urls)
        {
            BluetoothServerSocket tmp = null;
            BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

            try
            {
                tmp = mBluetoothAdapter.listenUsingRfcommWithServiceRecord(NAME, MY_UUID);
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }

            mmServerSocket = tmp;
            BluetoothSocket socket = null;

            // Keep listening until exception occurs or a socket is returned
            while (true)
            {
                try
                {
                    socket = mmServerSocket.accept();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                    break;
                }
                // If a connection was accepted
                if (socket != null)
                {
                    try
                    {
                        mmServerSocket.close();
                    }
                    catch (IOException exc)
                    {
                        exc.printStackTrace();
                    }

                    return socket;
                }
            }

            return socket;
        }

        protected void onPostExecute(BluetoothSocket result)
        {
            manageBTConnection(result);
        }
    }

    private void manageBTConnection(BluetoothSocket socket)
    {
        
    }
    private TextSwitcher textCount;
    private TextSwitcher textTime;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerThr.start();


        textCount = (TextSwitcher)findViewById(R.id.click_count);
        textTime  = (TextSwitcher)findViewById(R.id.seconds_count);

        ViewSwitcher.ViewFactory tFactory = new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                TextView t = new TextView(MainActivity.this);
                t.setGravity(Gravity.CENTER);
                t.setTextSize(60);
                return t;
            }
        };

        textCount.setFactory(tFactory);
        textTime.setFactory(tFactory);

        Animation animIn = AnimationUtils.loadAnimation(this, android.R.anim.fade_in);
        Animation animOut = AnimationUtils.loadAnimation(this, android.R.anim.fade_out);

        textCount.setInAnimation(animIn);
        textCount.setOutAnimation(animOut);

        textTime.setInAnimation(animIn);
        textTime.setOutAnimation(animOut);






        getData();
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
        int clicks = data.optJSONObject("today").optInt("puffs");
        textCount.setText("" + clicks);
        double milliseconds = data.optJSONObject("today").optDouble("milliseconds");
        double seconds = milliseconds / 1000;
        textTime.setText("" + seconds + " s");
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
