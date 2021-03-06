package idl.Data;

import java.util.Date;

public class Item {
    private int id;
    private String uuid;
    private String type;
    private String value;
    private int qty;
    private int status;
    private Date created;

    public static String ITEM = "ITEM";
    public static String HEAL = "HEAL";
    public static String MONEY = "MONEY";
    public static String EFFECT = "EFFECT";
    public static String EXPERIENCE = "EXPERIENCE";
    public static String PERMISSION = "PERMISSION";

    public Item(int id, String uuid, String type, String value, int qty, int status, Date created) {
        this.setId(id);
        this.setUuid(uuid);
        this.setType(type);
        this.setValue(value);
        this.setQty(qty);
        this.setStatus(status);
        this.setCreated(created);
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public int getQty() {
        return qty;
    }

    public void setQty(int qty) {
        this.qty = qty;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public Date getCreated() {
        return created;
    }

    public void setCreated(Date created) {
        this.created = created;
    }
}
