package TopologyAPI;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public final class JsonReadWriteHelper {
    private JsonReadWriteHelper() {

    }

    public static Topology read(String path) throws IOException {
        String json = fileToString(path);
        Map<String, Object> jsonTopology = new Gson().fromJson(json, Map.class);
        String topologyID = (String) jsonTopology.get("id");
        var jsonDevices = new Gson().fromJson(jsonTopology.get("components").toString(), ArrayList.class);
        ArrayList<Device> objectDevices = new ArrayList<>();

        for (var jsonDevice: jsonDevices) {
            objectDevices.add(getDeviceObjectFromDeviceJson(jsonDevice.toString()));
        }
        return new Topology(topologyID, objectDevices);
    }

    public static void write(Topology topology, String path) throws IOException {
        String jsonString = getJsonString(topology);
        new File(path);
        BufferedWriter writer = new BufferedWriter(new FileWriter(path));
        writer.write(jsonString);
        writer.close();
    }

    private static String fileToString(String path) throws IOException {
        return new String(Files.readAllBytes(Paths.get(path)));
    }

    private static Device getDeviceObjectFromDeviceJson(String json) {
        String type = null, ID = null;
        Limit limit = null;
        HashMap<String, String> netList = null;
        Map <String, Object> jsonDevice = new Gson().fromJson(json, Map.class);

        for (Map.Entry<String, Object> field: jsonDevice.entrySet()) {
            switch (field.getKey()) {
                case "type" -> type = (String) field.getValue();
                case "id" -> ID = (String) field.getValue();
                case "netlist" -> netList = getNetListObjectFromNetListJson(field.getValue().toString());
                default -> limit = getLimitObjectFromLimitJson(field.getValue().toString(), field.getKey());
            }
        }
        return new Device(type, ID, limit, netList);
    }

    private static HashMap<String, String> getNetListObjectFromNetListJson(String json) {
        HashMap<String, String> objectNetList = new HashMap<>();
        Map <String, Object> jsonNetList = new Gson().fromJson(json, Map.class);

        for (Map.Entry<String, Object> node: jsonNetList.entrySet()) {
            objectNetList.put(node.getKey(), (String) node.getValue());
        }
        return objectNetList;
    }

    private static Limit getLimitObjectFromLimitJson(String json, String type) {
        Map<String, Double> jsonLimit = new Gson().fromJson(json, Map.class);
        double defaultValue, minValue, maxValue;

        defaultValue = jsonLimit.get("default");
        minValue = jsonLimit.get("min");
        maxValue = jsonLimit.get("max");

        return new Limit(type, defaultValue, minValue, maxValue);
    }

    private static String getJsonString(Topology topology) {
        HashMap<String, Object> topologyMap = new HashMap<>();
        topologyMap.put("id", topology.getID());
        topologyMap.put("components", getComponentsMap(topology.getComponents()));
        return new GsonBuilder().setPrettyPrinting().create().toJson(topologyMap);
    }

    private static ArrayList<Object> getComponentsMap(ArrayList<Device> deviceArrayList) {
        ArrayList<Object> componentsMap = new ArrayList<>();

        for (Device deviceObject: deviceArrayList) {
            HashMap<String, Object> limitMap = new HashMap<>();
            HashMap<String, Object> deviceMap = new HashMap<>();

            Limit limitObject = deviceObject.getLimit();
            limitMap.put("default", limitObject.getDefaultValue());
            limitMap.put("min", limitObject.getMinValue());
            limitMap.put("max", limitObject.getMaxValue());

            deviceMap.put("id", deviceObject.getID());
            deviceMap.put("type", deviceObject.getType());
            deviceMap.put("netlist", deviceObject.getNetList());
            deviceMap.put(limitObject.getType(), limitMap);

            componentsMap.add(deviceMap);
        }
        return  componentsMap;
    }
}
