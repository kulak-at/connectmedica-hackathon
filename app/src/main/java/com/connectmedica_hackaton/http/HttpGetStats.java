package com.connectmedica_hackaton.http;

import android.content.Context;
import android.util.Log;

import com.androidquery.callback.AjaxCallback;
import com.connectmedica_hackaton.model.Puff;

import org.json.JSONObject;

import java.util.Calendar;

public class HttpGetStats extends AbstractHttp<JSONObject>
{
    public HttpGetStats(Context context)
    {
        super(context);

        this.requestMethod = Method.GET;
        this.urlSuffix = "me/stats";
        this.requireAuth = true;
    }

    @Override
    protected void call(AjaxCallback<JSONObject> callback)
    {
        long expire = -1;
        mAq.ajax(prepareUrl(), JSONObject.class, expire, callback);
    }
}
