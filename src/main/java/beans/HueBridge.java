package beans;


import api.HttpsRequestManager;
import api.RequestManager;

import com.sun.istack.internal.NotNull;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import javax.persistence.*;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@XmlRootElement
@Entity
@Table(name = "HUE_BRIDGE", schema = "LIGHTBULBS")
public class HueBridge {

    @Id
    @NotNull
    @Column(name = "OBJECTID", insertable = false)
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "BRIDGE_SEQ")
    @SequenceGenerator(sequenceName = "huebridge_seq", allocationSize = 1, name = "BRIDGE_SEQ")
    private long objectid;

    @Column(name = "BULB_ID")
    private String id = "";
    @Column(name = "INTERNAL_IP_ADDRESS")
    private String internalIpAddress = "";
    @Column(name = "MACADDRESS")
    private String macaddress = "";

    @Column(name = "BULB_NAME")
    private String name = "";
    @Column(name = "BULB_USERNAME")
    private String username = "";
    @Column(name = "BULB_EXISTS")
    private boolean exists;
    @Column(name = "BULB_REACHABLE")
    private boolean reachable;

    private static Map<String, HueBridge> bridgeMap = new HashMap<>();
    public static HueBridge getBridge(String id){
        return bridgeMap.get(id);
    }

    public static void setBridges(Map<String, HueBridge> bridgeMap1)
    {
        bridgeMap = bridgeMap1;
    }

    public HueBridge(){

    }

    // Hue Bridge Methods
    public static void findBridges(){
        new Thread(()-> {
            HttpsRequestManager requestManager = new HttpsRequestManager("discovery.meethue.com/", "GET");
            try{
                JSONArray jsonArray = new JSONArray(requestManager.sendData());

                for(int i = 0; i < jsonArray.length(); i++){
                    JSONObject bridgeJson = jsonArray.getJSONObject(i);
                    String id = bridgeJson.getString("id");
                    HueBridge bridge = new HueBridge();
                    bridge.setId(id);
                    bridge.setInternalIpAddress(bridgeJson.getString("internalipaddress"));
                }
            }catch (Exception e){
                e.printStackTrace();
            }
        }).start();

    }

    public List<HueBulb> findBulbs(){
        List<HueBulb> bulbs = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/lights", "GET");
        String data = "";
        try{
            data += manager.sendData();

            JSONObject response = new JSONObject(data);
            bulbs.addAll(addBulbsFromJson(response));
        }catch (Exception e){
            e.printStackTrace();
        }

        return bulbs;
    }
    private List<HueBulb> addBulbsFromJson(JSONObject response) throws JSONException {
        List<HueBulb> bulbs = new ArrayList<>();
        int i = 1;
        while(response.has(i + "")){
            HueBulb bulb = parseBulbJSON(response.getJSONObject(i + ""), i, this.getId());
            if(bulb != null) {
                bulbs.add(bulb);
            }
            i++;
        }
        return bulbs;
    }
    private static HueBulb parseBulbJSON(JSONObject json, int id, String bridgeId){
        HueBulb bulb = null;
        try{
            bulb = new HueBulb(id + "", json.getString("name"));
            bulb.setBridgeId(bridgeId);
            JSONObject bulbState = json.getJSONObject("state");
            bulb.setOn(bulbState.getBoolean("on"));
            bulb.setBrightness(bulbState.getInt("bri"));
            bulb.setHue(bulbState.getInt("hue"));
            bulb.setSaturation(bulbState.getInt("sat"));
            bulb.setKelvin(bulbState.getInt("ct"));

        }catch (Exception e){
            e.printStackTrace();
        }

        return bulb;
    }


    public List<HueBulbGroup> findGroups(){
        List<HueBulbGroup> groups = new ArrayList<>();
        RequestManager manager = new RequestManager(this.getInternalIpAddress() + "/api/" + this.getUsername() + "/groups", "GET");
        try{
            String data = manager.sendData();
            if(!Common.isValidJsonObject(data))
                data += "}";

            JSONObject response = new JSONObject(data);
            int i = 1;
            while(response.has(i + "")){
                HueBulbGroup group = parseGroupJSON(response.getJSONObject(i + ""), i, this.getId());

                if(group != null) groups.add(group);
                i++;
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        return groups;
    }

    private static HueBulbGroup parseGroupJSON(JSONObject json, int id, String bridgeId){
        HueBulbGroup group = null;
        try{
            group = new HueBulbGroup(json.getString("name"));
            group.setType(json.getString("type"));

            JSONArray lightsArray = json.getJSONArray("lights");
            List<String> lights = new ArrayList<>();
            for(int i = 0; i < lightsArray.length(); i++)
                lights.add(lightsArray.getString(i));

            group.setLights(lights);
            JSONObject state = json.getJSONObject("action");
            group.setOn(state.getBoolean("on"));
            group.setBrightness(state.getInt("bri"));
            group.setHue(state.getInt("hue"));
            group.setSaturation(state.getInt("sat"));
            group.setKelvin(state.getInt("ct"));
            group.setBridgeId(bridgeId);
            group.setId(id + "");

        }catch (Exception e){
            e.printStackTrace();
        }

        return group;
    }


    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getInternalIpAddress() {
        return internalIpAddress;
    }

    public void setInternalIpAddress(String internalIpAddress) {
        this.internalIpAddress = internalIpAddress;
    }

    public String getMacaddress() {
        return macaddress;
    }

    public void setMacaddress(String macaddress) {
        this.macaddress = macaddress;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
