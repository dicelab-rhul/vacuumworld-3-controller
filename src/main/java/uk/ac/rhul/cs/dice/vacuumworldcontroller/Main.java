package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.cloudstrife9999.logutilities.LogUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

public class Main {
    private static final String HOST = "127.0.0.1";
    private static final String CONFIG_FILE_PATH = "config.json";
    
    private Main() {}
    
    public static void main(String[] args) throws IOException {
	LogUtils.log("Controller started.");
	
	String[] ports = getPorts();
	
	if (!checkPorts(ports[0], ports[1])) {
	    LogUtils.log("Malformed or illegal details have been provided. Please edit " + CONFIG_FILE_PATH + " and retry.");
	}
	else {
	    VacuumWorldController controller = new VacuumWorldController(Integer.valueOf(ports[0]), HOST, Integer.valueOf(ports[1]));
	    controller.init();
	}
    }
    
    private static String[] getPorts() {
	try {
	    JSONTokener tokener = new JSONTokener(new FileInputStream(CONFIG_FILE_PATH));
	    JSONObject root = new JSONObject(tokener);
	    
	    return new String[] {root.getString("controller_port"), root.getString("model_port")};
	}
	catch(FileNotFoundException e) {
	    LogUtils.fakeLog(e);
	    LogUtils.log(CONFIG_FILE_PATH + " was not found.");
	    
	    return new String[] {null, null};
	}
	catch(Exception e) {
	    LogUtils.fakeLog(e);
	    
	    return new String[] {null, null};
	}
    }
    
    private static boolean checkPorts(String modelPort, String environmentPort) {
	if (modelPort == null || environmentPort == null) {
	    return false;
	}

	if (!testPort(modelPort)) {
	    return false;
	}

	return testPort(environmentPort);
    }

    private static boolean testPort(String portRepresentation) {
	try {
	    int port = Integer.parseInt(portRepresentation);

	    return port > 0 && port < 65536;
	}
	catch (NumberFormatException e) {
	    LogUtils.fakeLog(e);

	    return false;
	}
    }
}