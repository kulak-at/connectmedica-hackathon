package com.connectmedica_hackaton.http;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.connectmedica_hackaton.model.User;

import org.json.JSONArray;
import org.json.JSONObject;

public class HttpMe extends AbstractHttp<JSONObject>
{
    public HttpMe(Context context)
    {
        super(context);

        this.urlSuffix = "me";
        requireAuth = true;
    }

    @Override
    protected void call(AjaxCallback<JSONObject> callback)
    {
        long expire = -1;
        mAq.ajax(prepareUrl(), JSONObject.class, expire, callback);
    }
}
