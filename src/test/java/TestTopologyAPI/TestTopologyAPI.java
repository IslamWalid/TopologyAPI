package TestTopologyAPI;

import org.junit.Assert;
import TopologyAPI.*;

import org.junit.Test;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class TestTopologyAPI {
    @Test
    public void testReadJSON() throws IOException, TopologyIDNotFoundException {
        // Construct the expected Topology
        Topology expectedTopology = buildExpectedTopology();
        // Read the expected topology from the reference json file
        TopologyManager.readJSON("src/test/resources/refTopology.json");
        // Get the actual topology the that the API have read
        Topology actualTopology = TopologyManager.queryTopologies().get(0);
        TopologyManager.deleteTopology("top1");
        // Compare the actual topology with the expected topology
        Assert.assertTrue(actualTopology.equals(expectedTopology));
    }

    @Test
    public void testWriteJSON() throws IOException, TopologyIDNotFoundException {
        Topology expectedTopology, actualTopology;

        // Read the expected topology from refTopology.json
        TopologyManager.readJSON("src/test/resources/refTopology.json");
        expectedTopology = TopologyManager.queryTopologies().get(0);

        // Write the expected topology from memory to genTopology.json file
        TopologyManager.writeJSON("top1", "src/test/resources/genTopology.json");

        // Remove the expected topology from topologies list
        TopologyManager.deleteTopology(expectedTopology.getID());

        // Read the written topology in genTopology.json to compare it with the expected topology
        TopologyManager.readJSON("src/test/resources/genTopology.json");
        actualTopology = TopologyManager.queryTopologies().get(0);
        TopologyManager.deleteTopology("top1");

        // Compare the actual topology with the expected topology
        Assert.assertTrue(actualTopology.equals(expectedTopology));
    }

    @Test
    public void testDeleteTopology() throws IOException, TopologyIDNotFoundException {
        // Read topology and store it in memory
        TopologyManager.readJSON("src/main/resources/topology.json");
        // Make sure that the size of topologies list increased by 1
        Assert.assertEquals(TopologyManager.queryTopologies().size(), 1);
        // Delete the topology
        TopologyManager.deleteTopology("top1");
        // Make sure that the size of topologies list decreased by 1
        Assert.assertEquals(TopologyManager.queryTopologies().size(), 0);
    }

    @Test
    public void testQueryTopologies() throws IOException, TopologyIDNotFoundException {
        // Construct the expected topology
        Topology expectedTopology = buildExpectedTopology();
        // Read the topology from topology.json into memory
        TopologyManager.readJSON("src/main/resources/topology.json");
        ArrayList<Topology> actualTopologiesList = TopologyManager.queryTopologies();
        // Make sure the topology in the topologies list is similar to the expected topology
        Assert.assertTrue(expectedTopology.equals(actualTopologiesList.get(0)));
        TopologyManager.deleteTopology("top1");
    }

    @Test
    public void testQueryDevices() throws IOException, TopologyIDNotFoundException {
        // Construct the expected topology
        Topology expectedTopology = buildExpectedTopology();
        // Read the topology from topology.json into memory
        TopologyManager.readJSON("src/main/resources/topology.json");

        // Construct the expected and the actual list
        ArrayList<Device> actualDevicesList = TopologyManager.queryDevices("top1");
        ArrayList<Device> expectedDevicesList = expectedTopology.getComponents();
        TopologyManager.deleteTopology("top1");

        // Make sure that the actual and expected list are the same
        Assert.assertEquals(actualDevicesList.size(), expectedDevicesList.size());
        for (int i = 0; i < actualDevicesList.size(); ++i) {
            Assert.assertTrue(actualDevicesList.get(i).equals(expectedDevicesList.get(i)));
        }
    }

    @Test
    public void testQueryDevicesWithNetListNode() throws IOException, TopologyIDNotFoundException {
        // Construct the expected topology
        Topology expectedTopology = buildExpectedTopology();
        // Read the topology from topology.json into memory
        TopologyManager.readJSON("src/main/resources/topology.json");

        ArrayList<Device> expectedDevicesList = expectedTopology.getDevicesWithNetListNode("n1");
        ArrayList<Device> actualDevicesList = TopologyManager.queryDevicesWithNetListNode("top1", "n1");
        TopologyManager.deleteTopology("top1");

        Assert.assertEquals(expectedDevicesList.size(), actualDevicesList.size());
        for (int i = 0; i < expectedDevicesList.size(); ++i) {
            Assert.assertTrue(expectedDevicesList.get(i).equals(actualDevicesList.get(i)));
        }
    }

    private Topology buildExpectedTopology() {
        // Construction resistor limits
        Limit resistorLimit = new Limit("resistance", 100, 10, 1000);
        // Constructing nMos limits
        Limit nMosLimit = new Limit("m(l)", 1.5, 1, 2);
        HashMap<String, String> resistorNetList = new HashMap<>();
        HashMap<String, String> nMosNetList = new HashMap<>();
        ArrayList<Device> topologyComponents = new ArrayList<>();

        // Constructing resistor net list
        resistorNetList.put("t1", "vdd");
        resistorNetList.put("t2", "n1");

        // Constructing nMos net list
        nMosNetList.put("drain", "n1");
        nMosNetList.put("gate", "vin");
        nMosNetList.put("source", "vss");

        // Constructing components
        topologyComponents.add(new Device("resistor", "res1", resistorLimit, resistorNetList));
        topologyComponents.add(new Device("nmos", "m1", nMosLimit, nMosNetList));

        return new Topology("top1", topologyComponents);
    }
}
