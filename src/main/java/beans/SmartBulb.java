package beans;

import com.sun.istack.internal.NotNull;

import javax.annotation.Generated;
import javax.persistence.*;

@MappedSuperclass
@Table(name = "SMART_BULB", schema = "LIGHTBULBS")
public class SmartBulb {

    @GeneratedValue( strategy = GenerationType.SEQUENCE, generator = "BULBSEQUENCE1")
    @SequenceGenerator(name="BULBSEQUENCE1", sequenceName="BULBSEQUENCE1", allocationSize=1)
    @Id
    @NotNull
    @Column(name = "OBJECTID", insertable = false)
    private long objectid;


    @NotNull
    @Column(name = "BULB_ID")
    private String id = "";

    @Column(name = "LABEL")
    private String label = "";
    @Column(name = "BULB_GROUP")
    private String group = "";

    @Column(name = "HUE")
    private int hue;
    @Column(name = "SATURATION")
    private int saturation;
    @Column(name = "BRIGHTNESS")
    private int brightness;
    @Column(name = "KELVIN")
    private int kelvin;

    @Column(name = "BULB_POWER")
    private boolean on;
    @Column(name = "BULB_TYPE")
    private String type = "";

    public SmartBulb() {
    }

    public SmartBulb(SmartBulb bulb) {
        this.objectid = bulb.objectid;
        this.id = bulb.id;
        this.label = bulb.label;
        this.group = bulb.group;
        this.hue = bulb.hue;
        this.saturation = bulb.saturation;
        this.brightness = bulb.brightness;
        this.kelvin = bulb.kelvin;
        this.on = bulb.on;
        this.type = bulb.type;
    }

    public SmartBulb(String id) {
        this.id = id;
    }

    public SmartBulb(String id, String label){
        this.id = id;
        this.label = label;
    }

    public long getObjectid() {
        return objectid;
    }

    public void setObjectid(long objectid) {
        this.objectid = objectid;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getGroup() {
        return group;
    }

    public void setGroup(String group) {
        this.group = group;
    }

    public int getHue() {
        return hue;
    }

    public void setHue(int hue) {
        this.hue = hue;
    }

    public int getSaturation() {
        return saturation;
    }

    public void setSaturation(int saturation) {
        this.saturation = saturation;
    }

    public int getBrightness() {
        return brightness;
    }

    public void setBrightness(int brightness) {
        this.brightness = brightness;
    }

    public int getKelvin() {
        return kelvin;
    }

    public void setKelvin(int kelvin) {
        this.kelvin = kelvin;
    }

    public boolean isOn() {
        return on;
    }

    public void setOn(boolean on) {
        this.on = on;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }


    public void update()
    {
        if(this instanceof HueBulb) {
            HueBulb hueBulb = (HueBulb) this;
            hueBulb.changeState();
        }

        if(this instanceof LifxBulb) {
            LifxBulb lifxBulb = (LifxBulb) this;
            lifxBulb.changeState();
        }
    }
}
