package TopologyAPI;

import java.util.ArrayList;
import java.io.IOException;

public final class TopologyManager {
    // Private Constructor to prevent the user from instantiating this class
    private TopologyManager() {

    }

    public static void readJSON(String path) throws IOException {
        DataBase.topologies.add(JsonReadWriteHelper.read(path));
    }

    public static void writeJSON(String topologyID, String path) throws TopologyIDNotFoundException, IOException {
        Topology topology = searchTopologyID(topologyID);
        if (topology != null) {
            JsonReadWriteHelper.write(topology, path);
        } else {
            throw new TopologyIDNotFoundException("No such topology with this ID");
        }
    }

    public static void deleteTopology(String topologyID) throws TopologyIDNotFoundException {
        Topology topology = searchTopologyID(topologyID);
        if (topology != null) {
            DataBase.topologies.remove(topology);
        } else {
            throw new TopologyIDNotFoundException("No such topology with this ID");
        }
    }

    public static ArrayList<Topology> queryTopologies() {
        return new ArrayList<>(DataBase.topologies);
    }

    public static ArrayList<Device> queryDevices(String topologyID) throws TopologyIDNotFoundException {
        Topology topology = searchTopologyID(topologyID);
        if (topology != null) {
            return topology.getComponents();
        } else {
            throw new TopologyIDNotFoundException("No such topology with this ID");
        }
    }

    public static ArrayList<Device> queryDevicesWithNetListNode(String topologyID, String node) throws TopologyIDNotFoundException {
        // search for the topology with its ID in topologies list
        Topology topology = searchTopologyID(topologyID);
        if (topology != null) {
            return topology.getDevicesWithNetListNode(node);
        } else {
            throw new TopologyIDNotFoundException("No such topology with this ID");
        }
    }

    private static Topology searchTopologyID(String topologyID) {
        for (var topology: DataBase.topologies) {
            if (topology.getID().equals(topologyID)) {
                return topology;
            }
        }
        return null;
    }
}