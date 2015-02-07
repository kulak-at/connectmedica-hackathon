package com.connectmedica_hackaton;

public class App
{
    private static final String BASE_URL = "http://10.100.0.161:3666/";
    private static final String API_VERSION = "api/";
    public static final String USER_TOKEN = "54d5fa9ab7a3ff6420577d46";

    public static String getBaseUrl()
    {
        return BASE_URL;
    }

    public static String getApiVersion()
    {
        return API_VERSION;
    }
}
