package honbab.voltage.com.network;

import android.content.Context;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import honbab.voltage.com.data.UserData;

public class RestClient2 {

    private boolean authentication;
    private ArrayList<UserData> headers;

    private String jsonBody;
    private String message;

    private ArrayList<UserData> params;
    private String response;
    private int responseCode;

    private String url;

    // HTTP Basic Authentication
    private String username;
    private String password;

    protected Context context;

    public RestClient2(String url) {
        this.url = url;
        params = new ArrayList<UserData>();
        headers = new ArrayList<UserData>();

//        client = new Retrofit.Builder()
//                .baseUrl(sBaseUrl)
//                .addConverterFactory(GsonConverterFactory.create())
//                .client(httpClient.build())
//                .build();
    }
    //Be warned that this is sent in clear text, don't use basic auth unless you have to.
    public void addBasicAuthentication(String user, String pass) {
        authentication = true;
        username = user;
        password = pass;
    }

    public void addHeader(String name, String value) {
//        headers.add(new BasicNameValuePair(name, value));
    }

    public void addParam(String name, String value) {
//        params.add(new BasicNameValuePair(name, value));
    }

//    public void execute(RequestMethod method)
//            throws Exception {
//        switch (method) {
//            case GET: {
//                HttpGet request = new HttpGet(url + addGetParams());
//                request = (HttpGet) addHeaderParams(request);
//                executeRequest(request, url);
//                break;
//            }
//            case POST: {
//                HttpPost request = new HttpPost(url);
//                request = (HttpPost) addHeaderParams(request);
//                request = (HttpPost) addBodyParams(request);
//                executeRequest(request, url);
//                break;
//            }
//            case PUT: {
//                HttpPut request = new HttpPut(url);
//                request = (HttpPut) addHeaderParams(request);
//                request = (HttpPut) addBodyParams(request);
//                executeRequest(request, url);
//                break;
//            }
//            case DELETE: {
//                HttpDelete request = new HttpDelete(url);
//                request = (HttpDelete) addHeaderParams(request);
//                executeRequest(request, url);
//            }
//        }
//    }
//
//    private HttpUriRequest addHeaderParams(HttpUriRequest request)
//            throws Exception {
//        for (UserData h : headers) {
//            request.addHeader(h.getName(), h.getValue());
//        }
//
//        if (authentication) {
//
//            UsernamePasswordCredentials creds = new UsernamePasswordCredentials(
//                    username, password);
//            request.addHeader(new BasicScheme().authenticate(creds, request));
//        }
//
//        return request;
//    }
//
//    private HttpUriRequest addBodyParams(HttpUriRequest request)
//            throws Exception {
//        if (jsonBody != null) {
//            request.addHeader("Content-Type", "application/json");
//            if (request instanceof HttpPost)
//                ((HttpPost) request).setEntity(new StringEntity(jsonBody,
//                        "UTF-8"));
//            else if (request instanceof HttpPut)
//                ((HttpPut) request).setEntity(new StringEntity(jsonBody,
//                        "UTF-8"));
//
//        } else if (!params.isEmpty()) {
//            if (request instanceof HttpPost)
//                ((HttpPost) request).setEntity(new UrlEncodedFormEntity(params,
//                        HTTP.UTF_8));
//            else if (request instanceof HttpPut)
//                ((HttpPut) request).setEntity(new UrlEncodedFormEntity(params,
//                        HTTP.UTF_8));
//        }
//        return request;
//    }
//
//    private String addGetParams()
//            throws Exception {
//        StringBuffer combinedParams = new StringBuffer();
//        if (!params.isEmpty()) {
//            combinedParams.append("?");
//            for (UserData p : params) {
//                combinedParams.append((combinedParams.length() > 1 ? "&" : "")
//                        + p.getName() + "="
//                        + URLEncoder.encode(p.getValue(), "UTF-8"));
//            }
//        }
//        return combinedParams.toString();
//    }

    public String getErrorMessage() {
        return message;
    }

    public String getResponse() {
        return response;
    }

    public int getResponseCode() {
        return responseCode;
    }

    public void setContext(Context ctx) {
        context = ctx;
    }

    public void setJSONString(String data) {
        jsonBody = data;
    }

//    private void executeRequest(HttpUriRequest request, String url) {
//
//        DefaultHttpClient client = new DefaultHttpClient();
//        HttpParams params = client.getParams();
//
//        // Setting 30 second timeouts
//        HttpConnectionParams.setConnectionTimeout(params, 30 * 1000);
//        HttpConnectionParams.setSoTimeout(params, 30 * 1000);
//
//        HttpResponse httpResponse;
//
//        try {
//            httpResponse = client.execute(request);
//            responseCode = httpResponse.getStatusLine().getStatusCode();
//            message = httpResponse.getStatusLine().getReasonPhrase();
//
//            HttpEntity entity = httpResponse.getEntity();
//
//            if (entity != null) {
//
//                InputStream instream = entity.getContent();
//                response = convertStreamToString(instream);
//
//                // Closing the input stream will trigger connection release
//                instream.close();
//            }
//
//        } catch (ClientProtocolException e) {
//            client.getConnectionManager().shutdown();
//            e.printStackTrace();
//        } catch (IOException e) {
//            client.getConnectionManager().shutdown();
//            e.printStackTrace();
//        }
//    }

    private static String convertStreamToString(InputStream is) {

        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }
}