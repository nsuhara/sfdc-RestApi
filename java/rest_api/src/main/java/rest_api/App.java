package rest_api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    static final String HOST = "<パラメータ参照>";
    static final String CLIENT_ID = "<パラメータ参照>";
    static final String CLIENT_SECRET = "<パラメータ参照>";
    static final String USERNAME = "<パラメータ参照>";
    static final String PASSWORD_AND_SECURITY_TOKEN = "<パラメータ参照>";

    static final String GRANT_SERVICE = "/services/oauth2/token?grant_type=password";

    public static void main(String[] args) {
        String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36";
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("User-Agent", UA));

        HttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();

        String loginURL = "https://" + HOST + GRANT_SERVICE + "&client_id=" + CLIENT_ID + "&client_secret="
                + CLIENT_SECRET + "&username=" + USERNAME + "&password=" + PASSWORD_AND_SECURITY_TOKEN;

        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;

        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            // Handle protocol exception
        } catch (IOException ioException) {
            // Handle system IO exception
        }

        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: " + statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }

        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            // Handle system IO exception
        }

        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;

        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginInstanceUrl = jsonObject.getString("instance_url");
            loginAccessToken = jsonObject.getString("access_token");
        } catch (JSONException jsonException) {
            // Handle JSON exception
        }

        System.out.println(response.getStatusLine());

        httpPost.releaseConnection();
        try {
            final URIBuilder builder = new URIBuilder(loginInstanceUrl);
            final String SOQL = "SELECT Id, Name, LastLoginDate FROM User WHERE Name = \'nsuhara\'";
            builder.setPath("/services/data/v45.0/query/").setParameter("q", SOQL);
            final HttpGet get = new HttpGet(builder.build());
            get.setHeader("Authorization", "Bearer " + loginAccessToken);

            final HttpResponse queryResponse = httpClient.execute(get);

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode queryResults = mapper.readValue(queryResponse.getEntity().getContent(), JsonNode.class);

            System.out.println("queryResults:" + queryResults);
        } catch (Exception e) {
            // Handle exception
        }
    }
}
