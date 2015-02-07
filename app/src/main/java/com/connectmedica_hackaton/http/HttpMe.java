package com.connectmedica_hackaton.http;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.connectmedica_hackaton.model.User;

import org.json.JSONArray;

public class HttpMe extends AbstractHttp<JSONArray>
{
    public HttpMe(Context context)
    {
        super(context);

        this.urlSuffix = "me";
        requireAuth = true;
    }

    @Override
    protected void call(AjaxCallback<JSONArray> callback)
    {
        long expire = -1;
        mAq.ajax(prepareUrl(), JSONArray.class, expire, callback);
    }
}
