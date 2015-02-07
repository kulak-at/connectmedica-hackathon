package com.connectmedica_hackaton;

import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.Toast;

import com.connectmedica_hackaton.http.AbstractHttp;
import com.connectmedica_hackaton.http.HttpMe;
import com.connectmedica_hackaton.model.User;

import org.json.JSONArray;

import java.util.Calendar;


public class MainActivity extends ActionBarActivity implements AbstractHttp.OnAjaxResult<JSONArray>
{
    private static final int REQUEST_ENABLE_BT = 42;
    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        FrameLayout debugFrame = (FrameLayout) findViewById(R.id.debugFrame);
        debugFrame.setOnClickListener(new DebugListener());
        Button button = (Button) debugFrame.findViewById(R.id.debugButton);
        final ImageView cigar = (ImageView)findViewById(R.id.ecigarrete);

        button.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar currentTime = Calendar.getInstance();
                    startTime = currentTime.getTimeInMillis();
                    cigar.setImageDrawable(getResources().getDrawable(R.drawable.epapturned_on));
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    Calendar currentTime = Calendar.getInstance();
                    endTime = currentTime.getTimeInMillis();

                    long diff = endTime - startTime;
                    Toast.makeText(MainActivity.this, diff + "", Toast.LENGTH_LONG).show();
                    cigar.setImageDrawable(getResources().getDrawable(R.drawable.epapturned_off));
                }

                return false;
            }
        });

        getData();
    }

    private void getData()
    {
        HttpMe ajax = new HttpMe(getApplicationContext());
        ajax.onResult(this);
        ajax.run();
    }

    @Override
    public void onResult(JSONArray data)
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
