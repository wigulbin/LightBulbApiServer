package api;

import org.glassfish.jersey.client.ClientResponse;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;
import org.json.JSONObject;

import javax.net.ssl.HostnameVerifier;
import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.DataInputStream;
import java.io.OutputStreamWriter;
import java.net.URL;


public class HttpsRequestManager {
    private JSONObject data;
    private String type;
    private String url;

    public HttpsRequestManager(String urlString, String type){
        System.out.println("here");
        try{
            this.url = urlString;
            data = new JSONObject();
            this.type = type;
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public boolean addData(String key, String value){
        try{
            data.put(key, value);
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }
        return true;
    }


    public String sendData(){
        try
        {
            SSLContext sc = SSLContext.getInstance("TLSv1");
            System.setProperty("https.protocols", "TLSv1");
            TrustManager[] trustAllCerts = { new InsecureTrustManager() };
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HostnameVerifier allHostsValid = new InsecureHostnameVerifier();

            Client client = ClientBuilder.newBuilder().sslContext(sc).hostnameVerifier(allHostsValid).build();
            HttpAuthenticationFeature feature = HttpAuthenticationFeature.universalBuilder()
                    .credentialsForBasic("", "").credentials("", "").build();


            client.register(feature);
            final Response response = client
                    .target("https://" + url)
                    .request().get();

            if (response.getStatus() != 200) { throw new RuntimeException("Failed : HTTP error code : "
                    + response.getStatus()); }
            String output = response.readEntity(String.class);
            System.out.println("Output from Server .... \n");
            System.out.println(output);
            client.close();

            return output;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            return "";
        }
    }

    private void handleMessageSend(){
    }
}
