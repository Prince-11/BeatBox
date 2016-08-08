package com.opentok.android.demo.services;

import android.content.Context;
import android.os.AsyncTask;
import com.opentok.android.demo.ssl.EasySSLSocketFactory;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpVersion;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by shankar_r on 5/27/2016.
 */
public class OpenTokArchiveTask extends AsyncTask<String, Void, String> {

    private ClientConnectionManager clientConnectionManager;
    private HttpContext context;
    private HttpParams params;
    DefaultHttpClient httpClient;
    private Context cont;

    public OpenTokArchiveTask(Context cc) {
        this.cont = cc;
    }

    @Override
    protected String doInBackground(String... params) {
        String response = setup();
        return response;
    }

    private String setup() {
        SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(new Scheme("https", new EasySSLSocketFactory(), 443));
        params = new BasicHttpParams();
        HttpProtocolParams.setVersion(params, HttpVersion.HTTP_1_1);
        HttpProtocolParams.setContentCharset(params, "application/json");
        CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
        context = new BasicHttpContext();
        context.setAttribute("https.auth.credentials-provider", credentialsProvider);
        HttpResponse response = getResponseFromUrl("https://api.opentok.com/v2/partner/45592312/archive");
        HttpEntity responseEntity = response.getEntity();
        try {
            if (responseEntity != null) {
                String res = EntityUtils.toString(responseEntity);
                System.out.println("Responce:::" + res);
                return res;
            }
        } catch (Exception e) {
            System.out.println("Exception:::" + e.toString());
        }
        return null;
    }

    public HttpResponse getResponseFromUrl(String url) {
        JSONObject json = null;
        try {
            json = new JSONObject();
            json.put("sessionId", "2_MX40NTU5MjMxMn5-MTQ2NDg2NzA4NzA4OX50QmNwRTVtcXNqR0dKK0U4ZFRueXRYMDR-fg");
            json.put("hasAudio", true);
            json.put("hasVideo", true);
            json.put("outputMode", "composed");
            System.out.println("Json:::" + json.toString());
        } catch (JSONException ex) {

        }
        httpClient = new DefaultHttpClient(clientConnectionManager, params);
        HttpPost httpPostHC4 = new HttpPost(url);
        try {
            StringEntity paramsEntiity = new StringEntity(json.toString());
            httpPostHC4.setHeader("X-TB-PARTNER-AUTH", "45592312:1a2802a0c588c13932cc570291a9bfb7e9fe063e");
            httpPostHC4.setHeader("Content-type", "application/json");
            httpPostHC4.setEntity(paramsEntiity);
            HttpResponse response = httpClient.execute(httpPostHC4, context);
            return response;
        } catch (Exception e) {
            System.out.println("Responce:::" + e.toString());
        }
        return null;

    }
}
