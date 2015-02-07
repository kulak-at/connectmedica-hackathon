package com.connectmedica_hackaton.http;

import android.content.Context;

import com.androidquery.callback.AjaxCallback;
import com.connectmedica_hackaton.model.Puff;

public class HttpPostPuff extends AbstractHttp<Puff>
{
    public HttpPostPuff(Context context, long startTime, long duration)
    {
        super(context);

        this.requestMethod = Method.POST;
        this.urlSuffix = "puff";
        this.requireAuth = true;

        param("startTime", startTime);
        param("duration", duration);
    }

    @Override
    protected void call(AjaxCallback<Puff> callback)
    {
        long expire = -1;
        mAq.ajax(prepareUrl(), Puff.class, expire, callback);
    }
}
