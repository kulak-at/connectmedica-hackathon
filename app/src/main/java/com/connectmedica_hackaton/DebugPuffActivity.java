package com.connectmedica_hackaton;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.Calendar;


public class DebugPuffActivity extends ActionBarActivity
{
    private long startTime = 0;
    private long endTime = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_debug_puff);

        final ImageView cigarette = (ImageView) findViewById(R.id.imgCigarette);

        cigarette.setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View v, MotionEvent event)
            {
                if (event.getAction() == MotionEvent.ACTION_DOWN) {
                    Calendar currentTime = Calendar.getInstance();
                    startTime = currentTime.getTimeInMillis();
                    cigarette.setBackgroundResource(R.drawable.epapturned_on);
                    cigarette.invalidate();

                    return true;
                }
                else if (event.getAction() == MotionEvent.ACTION_UP)
                {
                    cigarette.setBackgroundResource(R.drawable.epapturned_off);
                    cigarette.invalidate();
                    Calendar currentTime = Calendar.getInstance();
                    endTime = currentTime.getTimeInMillis();

                    long diff = endTime - startTime;
                    Toast.makeText(DebugPuffActivity.this, diff + "", Toast.LENGTH_LONG).show();

                    return true;
                }

                return false;
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu)
    {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_debug_puff, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item)
    {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
