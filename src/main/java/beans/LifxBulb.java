package beans;

import api.HSBK;
import api.LifxWrapper;
import com.sun.istack.internal.NotNull;
import interfaces.Changeable;

import javax.persistence.*;
import java.util.*;

public class LifxBulb extends SmartBulb implements Changeable {

    private String location = "";

    public LifxBulb(){super();};
    public LifxBulb(String mac){
        super(mac);
    }
    public LifxBulb(String mac, String label){
        super(mac, label);
    }

    private final static int brightMax = 65535;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LifxBulb lifxBulb = (LifxBulb) o;
        return Objects.equals(getId(), lifxBulb.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getId());
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }


    //Bulb network methods
    public static List<LifxBulb> findAllBulbs(){
        Set<String> macSet = new HashSet<>();
        List<LifxBulb> bulbs = new ArrayList<>();
        List<String> macAddresses = LifxWrapper.getAllMacAddresses();
        for (String macAddress : macAddresses) {
            if(macSet.add(macAddress)){
                final LifxBulb bulb = new LifxBulb(macAddress);
                String label = LifxWrapper.getLabel(macAddress);
                bulb.setLabel(label.replaceAll("\\u0000", ""));
                String group = LifxWrapper.getGroup(macAddress);
                bulb.setGroup(group.replaceAll("\\u0000", ""));

                HSBK hsbk = LifxWrapper.getHSBK(macAddress);
                bulb.setHue(hsbk.getHue());
                bulb.setSaturation(hsbk.getSaturation());
                bulb.setBrightness(hsbk.getBrightness());
                bulb.setKelvin(hsbk.getKelvin());

                bulbs.add(bulb);
            }
        }

        return bulbs;
    }


    public void changePower(){
        LifxBulb bulb = this;
        new Thread(() -> LifxWrapper.setPower(bulb.getId(), bulb.isOn(), 500)).start();
    }

    public void changeHsbk(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeBrightness(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeHue(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeSaturation(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeKelvin(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    public void changeState(){
        LifxBulb bulb = this;
        new Thread(() ->  LifxWrapper.setHSBK(bulb)).start();
    }

    @Override
    public void incrementHue(int amount) {
        LifxBulb bulb = this;
        bulb.setHue(amount + bulb.getHue());
        bulb.changeState();
    }

    @Override
    public void incrementSaturation(int amount) {
        LifxBulb bulb = this;
        bulb.setSaturation(amount + bulb.getSaturation());
        bulb.changeState();
    }

    @Override
    public void incrementKelvin(int amount) {
        LifxBulb bulb = this;
        bulb.setKelvin(amount + bulb.getKelvin());
        bulb.changeState();

    }

    @Override
    public void incrementBrightness(int amount) {
        LifxBulb bulb = this;
        bulb.setBrightness(bulb.getBrightness() + amount);
        bulb.changeState();
    }

    @Override
    public int retrieveBrightMax() {
        return brightMax;
    }
}

