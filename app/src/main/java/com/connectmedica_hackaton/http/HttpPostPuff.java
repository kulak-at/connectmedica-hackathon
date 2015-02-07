package com.connectmedica_hackaton.http;

import android.content.Context;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.connectmedica_hackaton.model.Puff;

import org.json.JSONObject;

import java.util.Calendar;

public class HttpPostPuff extends AbstractHttp<JSONObject>
{
    public HttpPostPuff(Context context, Calendar startTime, long duration)
    {
        super(context);

        this.requestMethod = Method.POST;
        this.urlSuffix = "puff";
        this.requireAuth = true;

        Log.i("kulak", startTime.getTimeInMillis() + "");
        Log.i("kulak", duration + "");

        param("startTime", startTime.getTimeInMillis());
        param("duration", duration);
    }

    @Override
    protected void call(AjaxCallback<JSONObject> callback)
    {
        long expire = -1;
        mAq.ajax(prepareUrl(), JSONObject.class, expire, callback);
    }
}
