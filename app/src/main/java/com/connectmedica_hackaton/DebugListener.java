package com.connectmedica_hackaton;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class DebugListener implements View.OnClickListener
{
    private static final int CLICKS_REQUIRED = 7;
    private static int clickCounter = 0;

    @Override
    public void onClick(View v)
    {
        TextView textView = (TextView) v.findViewById(R.id.debugTextView);
        Button button = (Button) v.findViewById(R.id.debugButton);

        if (clickCounter == CLICKS_REQUIRED)
        {
            textView.setVisibility(View.GONE);
            button.setVisibility(View.VISIBLE);
            return;
        }
        else if (clickCounter > 3)
        {
            textView.setVisibility(View.VISIBLE);
            textView.setText("You are: " + (CLICKS_REQUIRED - clickCounter)
                    + " steps away from becoming awesome!");

        }

        clickCounter++;
    }
}
