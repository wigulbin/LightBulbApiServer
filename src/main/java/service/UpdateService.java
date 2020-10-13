package service;

import api.HttpsRequestManager;
import api.RequestManager;
import beans.*;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.container.AsyncResponse;
import javax.ws.rs.container.Suspended;
import javax.ws.rs.core.MediaType;
import java.util.ArrayList;
import java.util.List;

@Path("update")
@Stateless
public class UpdateService {

    @PersistenceContext(unitName = "persistence_unit")
    private EntityManager entityManager;


    @GET
    @Produces({MediaType.APPLICATION_JSON})
    public List<HueBridge> updateBridges(){
        try {
            return findBridges();
        }
        catch (Exception e){
            return new ArrayList<>();
        }
    }

    @GET
    @Path("bridges")
    public List<HueBridge> detectBridges(){
        HttpsRequestManager requestManager = new HttpsRequestManager("discovery.meethue.com/", "GET");
        List<HueBridge> bridges = new ArrayList<>();
        try{
            JSONArray jsonArray = new JSONArray(requestManager.sendData());

            for(int i = 0; i < jsonArray.length(); i++){
                JSONObject bridgeJson = jsonArray.getJSONObject(i);
                String id = bridgeJson.getString("id");
                HueBridge bridge = new HueBridge();
                bridge.setId(id);
                bridge.setInternalIpAddress(bridgeJson.getString("internalipaddress"));
                entityManager.persist(bridge);
                bridges.add(bridge);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return bridges;
    }

    @GET
    @Path("{bridgeid}")
    public String registerBridge(@PathParam("bridgeid") String bridgeid){
        List<HueBridge> hueBridges = entityManager.createQuery(
                "SELECT c FROM HueBridge c WHERE c.id LIKE :bridgeid")
                .setParameter("bridgeid", bridgeid)
                .setMaxResults(1)
                .getResultList();


        HueBridge bridge = hueBridges.stream().findFirst().orElse(null);
        if(bridge != null && bridge.getUsername().length() == 0)
        {
            RequestManager manager = new RequestManager(bridge.getInternalIpAddress() + "/api", "POST");
            manager.addData("devicetype", "my_hue_app#android will");
            try{
                JSONObject json = new JSONArray(manager.sendData()).getJSONObject(0);
                if(json != null){
                    if(json.has("error")) {
                        JSONObject error = json.getJSONObject("error");
                        return error.getString("description");
                    }
                    if(json.has("success")){
                        String username = json.getJSONObject("success").getString("username");
                        bridge.setUsername(username);
                        entityManager.persist(bridge);
                        return "Bridge Linked";
                    }
                }
            } catch (Exception e){
                e.printStackTrace();
            }
        }

        if(bridge.getUsername().length() > 0)
            return "Bridge already linked";

        return "Not able to be linked at this time";
    }


    public List<HueBridge> findBridges(){
        HttpsRequestManager requestManager = new HttpsRequestManager("discovery.meethue.com/", "GET");
        List<HueBridge> bridges = new ArrayList<>();
        JSONArray jsonArray = new JSONArray(requestManager.sendData());

        for(int i = 0; i < jsonArray.length(); i++){
            JSONObject bridgeJson = jsonArray.getJSONObject(i);
            String id = bridgeJson.getString("id");
            HueBridge bridge = new HueBridge();
            bridge.setId(id);
            bridge.setInternalIpAddress(bridgeJson.getString("internalipaddress"));
            try
            {
                List<HueBridge> foundBridges = entityManager.createQuery(
                        "SELECT c FROM HueBridge c WHERE c.id LIKE :bridgeid")
                        .setParameter("bridgeid", id)
                        .setMaxResults(1)
                        .getResultList();
                if(foundBridges.size() < 1)
                    entityManager.persist(bridge);
                else
                {
                    HueBridge foundBridge = foundBridges.get(0);
                    foundBridge.setInternalIpAddress(bridge.getInternalIpAddress());
                    foundBridge.setMacaddress(bridge.getMacaddress());
                    bridges.add(foundBridge);
                    entityManager.persist(foundBridge);
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }

        return bridges;
    }
}
