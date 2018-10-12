package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.cloudstrife9999.logutilities.LogUtils;
import org.json.JSONObject;
import org.json.JSONTokener;

import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VWJSON;

public class Main {
    private static final String CONFIG_FILE_PATH = "config.json";
    
    private Main() {}
    
    public static void main(String[] args) throws IOException {
	LogUtils.enableVerbose();
	LogUtils.log("Controller started.");
	
	String[] details = getDetails();
	
	if (!checkPorts(details[0], details[1])) {
	    LogUtils.log("Malformed or illegal details have been provided. Please edit " + CONFIG_FILE_PATH + " and retry.");
	}
	else {
	    VacuumWorldController controller = new VacuumWorldController(Integer.valueOf(details[0]), details[2], Integer.valueOf(details[1]));
	    controller.init();
	}
    }
    
    private static String[] getDetails() {
	try {
	    JSONTokener tokener = new JSONTokener(new FileInputStream(CONFIG_FILE_PATH));
	    JSONObject root = new JSONObject(tokener);
	    
	    return new String[] {root.getString(VWJSON.CONTROLLER_PORT), root.getString(VWJSON.MODEL_PORT), root.getString(VWJSON.MODEL_HOSTNAME)};
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