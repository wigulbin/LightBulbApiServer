package api;

import beans.HueBridge;
import beans.HueBulb;
import beans.HueBulbGroup;

public class HueWrapper {
    private String url = "";
    private RequestManager manager;

    public HueWrapper(HueBulb bulb){
        HueBridge bridge = HueBridge.getBridge(bulb.getBridgeId());
        this.url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/lights/" + bulb.getId() + "/state";
        this.manager = new RequestManager(url, "PUT");
    }
    public HueWrapper(HueBulbGroup group){
        HueBridge bridge = HueBridge.getBridge(group.getBridgeId());
        String url = bridge.getInternalIpAddress() + "/api/" + bridge.getUsername() + "/groups/" + group.getId() + "/action";
        this.manager = new RequestManager(url, "PUT");
    }

    public HueWrapper changePower(boolean on){
        RequestManager manager = this.manager;
        manager.addData("on", on);
        return this;
    }
    public HueWrapper changeBrightness(int brightness){
        RequestManager manager = this.manager;
        manager.addData("bri", brightness);
        return this;
    }
    public HueWrapper changeHue(int hue){
        RequestManager manager = this.manager;
        manager.addData("hue", hue);
        return this;
    }
    public HueWrapper changeSaturation(int saturation){
        RequestManager manager = this.manager;
        manager.addData("sat", saturation);
        return this;
    }
    public HueWrapper changeKelvin(int kelvin){
        RequestManager manager = this.manager;
        manager.addData("ct", kelvin);
        return this;
    }

    public HueWrapper incrementBrightness(int amount){
        RequestManager manager = this.manager;
        manager.addData("bri_inc", amount);
        return this;
    }
    public HueWrapper incrementHue(int amount){
        RequestManager manager = this.manager;
        manager.addData("hue_inc", amount);
        return this;
    }
    public HueWrapper incrementSaturation(int amount){
        RequestManager manager = this.manager;
        manager.addData("sat_inc", amount);
        return this;
    }
    public HueWrapper incrementKelvin(int amount){
        RequestManager manager = this.manager;
        manager.addData("ct_inc", amount);
        return this;
    }
    public void changeState(boolean on, int brightness, int hue, int saturation){
        changePower(on);
        changeBrightness(brightness);
        changeHue(hue);
        changeSaturation(saturation);
        send();
    }

    public void send(){
        System.out.println(manager.sendData());
    }
}
