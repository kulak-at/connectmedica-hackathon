package com.connectmedica_hackaton.http;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import android.content.Intent;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.google.gson.Gson;

import javax.xml.transform.Transformer;

public abstract class AbstractHttp<Type>
{
    public interface OnAjaxResult<Type>
    {
        public void onResult(Type data);
        public void onError(String message);
    }

    public class GsonTransformer implements com.androidquery.callback.Transformer
    {
        @Override
        public <T> T transform(String url, Class<T> type, String encoding, byte[] data, AjaxStatus status)
        {
            Gson g = new Gson();
            return g.fromJson(new String(data), type);
        }
    }


    AQuery mAq;
    public enum Method {
        GET, POST, PUT, DELETE;
    };

    protected String url_sufix;
    protected boolean requireAuth;
    protected Method requestMethod;
    private Map<String, Object> mValues;

    private OnAjaxResult<Type> mOnResult;

    public AbstractHttp(Context context) {
        mAq = new AQuery(context);
        mValues = new HashMap<String, Object>();
        requestMethod = Method.GET;
    }

    public void onResult(OnAjaxResult<Type> onResultInterface) {
        mOnResult = onResultInterface;
    }

    public void run() {
        AjaxCallback<Type> callback = new AjaxCallback<Type>() {
            @Override
            public void callback(String url, Type object, AjaxStatus status) {
                AbstractHttp.this.callback(url, object, status);
            }
        };

        if(requireAuth) {
            callback.header("Token", "");   // TODO
        }

        callback.url(prepareUrl());
        GsonTransformer transformer = new GsonTransformer();
        callback.transformer(transformer);


        if(requestMethod != Method.GET && requestMethod != Method.DELETE) {
            callback.params(mValues);
        }
        callback.method(getMethod());

        call(callback);
    }

    abstract protected void call(AjaxCallback<Type> callback);

    private void callback(String url, Type data, AjaxStatus status) {
        switch (status.getCode()) {
            case 200:
                if(mOnResult != null) {
                    mOnResult.onResult(data);
                }
                break;
            case 401:
                if(requireAuth) {
                    handleUnauth();
                }
                break;
            default:
                if(mOnResult != null) {
                    mOnResult.onError(status.getMessage()); // TODO: better handling for errors
                }
                break;
        }
    }

    private void handleUnauth() {
        Intent intent = new Intent(EvenUpActivity.CUSTOM_ACTION);
        intent.setType("content://unauth");
        EvenApp.getInstance().sendBroadcast(intent);
    }

    private int getMethod() {
        switch (requestMethod) {
            case GET:
                return AQuery.METHOD_GET;
            case POST:
                return AQuery.METHOD_POST;
            case PUT:
                return AQuery.METHOD_PUT;
            case DELETE:
                return AQuery.METHOD_DELETE;
            default:
                return AQuery.METHOD_GET;
        }
    }

    protected String prepareUrl()
    {
        EvenApp app = EvenApp.getInstance();

        String url = app.getBaseUrl() + app.getApiVersion() + url_sufix;

        if(requestMethod != Method.GET && requestMethod != Method.DELETE) {
            return url;
        }

        String ret = url + "?";
        String separator = "";
        for(String key : mValues.keySet()) {
            ret += separator + key + "=" + mValues.get(key);
            separator = "&";
        }
        Log.d("kulak", ret);
        return ret;
    }

    protected void param(String key, Object value) {
        mValues.put(key, value);
    }

}

