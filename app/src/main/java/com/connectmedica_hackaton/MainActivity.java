package com.connectmedica_hackaton;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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

import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements AbstractHttp.OnAjaxResult<JSONObject>
{
    private static final int REQUEST_ENABLE_BT = 42;
    private Calendar startTime = null;
    private long endTime = 0;
    private BluetoothServer server;
    private Thread ServerThr = new Thread(server);

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ServerThr.start();
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
