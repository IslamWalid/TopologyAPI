package TopologyAPI;

import java.util.HashMap;

public class Device {
    private String type;
    private String ID;
    private Limit limit;
    private HashMap<String, String> netList;

    public Device(String type, String ID, Limit limit, HashMap<String, String> netList) {
        this.type = type;
        this.ID = ID;
        this.limit = limit;
        this.netList = new HashMap<>(netList);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getID() {
        return ID;
    }

    public void setID(String ID) {
        this.ID = ID;
    }

    public Limit getLimit() {
        return limit;
    }

    public void setLimit(Limit limit) {
        this.limit = limit;
    }

    public HashMap<String, String> getNetList() {
        return new HashMap<>(netList);
    }

    public void setNetList(HashMap<String, String> netList) {
        this.netList = new HashMap<>(netList);
    }

    public boolean isConnectedToNode(String node) {
        return netList.containsValue(node);
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        Device device = (Device) obj;
        return type.equals(device.getType()) &&
                ID.equals(device.getID()) &&
                limit.equals(device.getLimit()) &&
                netList.equals(device.getNetList());
    }
}