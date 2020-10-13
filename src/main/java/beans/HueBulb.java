package beans;


import api.HueWrapper;
import com.sun.istack.internal.NotNull;
import interfaces.Changeable;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Entity
@Table(name = "HUE_BULB", schema = "LIGHTBULBS")
public class HueBulb extends SmartBulb implements Changeable {

    @Column(name = "BRIDGE_ID")
    private String bridgeId = "";

    public HueBulb(){
        super();
    }
    public HueBulb(String id, String name){
        super (id, name);
    }
    public HueBulb(SmartBulb smartBulb){
        super (smartBulb);
    }


    private final static int brightMax = 254;

    public String getBridgeId() {
        return bridgeId;
    }

    public void setBridgeId(String bridgeId) {
        this.bridgeId = bridgeId;
    }



    public void changePower(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changePower(bulb.isOn()).send()).start();
    }
    public void changeBrightness(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeBrightness(bulb.getBrightness()).send()).start();
    }
    public void changeHue(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeHue(bulb.getHue()).send()).start();
    }
    public void changeSaturation(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeSaturation(bulb.getSaturation()).send()).start();
    }
    public void changeKelvin(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeKelvin(bulb.getKelvin()).send()).start();
    }
    public void changeState(){
        HueBulb bulb = this;
        new Thread(() -> new HueWrapper(bulb).changeState(bulb.isOn(), bulb.getBrightness(), bulb.getHue(), bulb.getSaturation())).start();
    }

    @Override
    public void incrementHue(int amount) {
        HueBulb bulb = this;
        bulb.setHue(amount + bulb.getHue());
        new Thread(() -> new HueWrapper(bulb).incrementHue(amount).send()).start();
    }

    @Override
    public void incrementSaturation(int amount) {
        HueBulb bulb = this;
        bulb.setSaturation(amount + bulb.getSaturation());
        new Thread(() -> new HueWrapper(bulb).incrementSaturation(amount).send()).start();
    }

    @Override
    public void incrementKelvin(int amount) {
        HueBulb bulb = this;
        bulb.setKelvin(amount + bulb.getKelvin());
        new Thread(() -> new HueWrapper(bulb).incrementKelvin(amount).send()).start();

    }

    @Override
    public void incrementBrightness(int amount) {
        HueBulb bulb = this;
        bulb.setBrightness(bulb.getBrightness() + amount);
        new Thread(() -> new HueWrapper(bulb).incrementBrightness(amount).send()).start();
    }

    @Override
    public int retrieveBrightMax() {
        return brightMax;
    }


}
