package TopologyAPI;

public class TopologyIDNotFoundException extends Exception {
    TopologyIDNotFoundException() {
        super();
    }

    TopologyIDNotFoundException(String message) {
        super(message);
    }
}
