package api;

import org.glassfish.jersey.client.ClientResponse;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Form;
import javax.ws.rs.core.MultivaluedHashMap;
import javax.ws.rs.core.MultivaluedMap;
import javax.ws.rs.core.Response;

public class RequestManager {
    private HttpURLConnection connection;
    private JSONObject data;
    private String type;
    private Client client;
    private String urlString = "";

    public RequestManager(String urlString, String type){
        try{
            this.client = ClientBuilder.newClient();
            this.urlString = "http://" + urlString;

            data = new JSONObject();
            this.type = type;

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    public String sendData(){
        WebTarget webTarget = client.target(urlString);

        String input = data.toString();
        String output = "";
        Response response = null;
        if(type.equalsIgnoreCase("PUT"))
            response = webTarget.request("application/json").put(Entity.json(input));
        if(type.equalsIgnoreCase("POST"))
            response = webTarget.request("application/json").post(Entity.json(input));
        if(type.equals("GET"))
            response = webTarget.request("application/json").get();
        if(response != null)
        {
            output = response.readEntity(String.class);
            System.out.println("Output from Server .... \n");
            System.out.println(output);
        }

        client.close();

        return output;
    }

    private void handleMessageSend(){
        try(OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream())){
            writer.write(data.toString());
            writer.flush();
            connection.connect();
        }catch (Exception e){
            e.printStackTrace();
        }
    }



    public boolean addData(String key, String value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addData(String key, boolean value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public boolean addData(String key, int value){
        try{
            data.put(key, value);
        }catch (JSONException e){
            e.printStackTrace();
            return false;
        }
        return true;
    }
}
