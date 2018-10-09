package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cloudstrife9999.logutilities.LogUtils;

import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VacuumWorldRuntimeException;

public class VacuumWorldController {
    private int controllerPort;
    private String modelIp;
    private int modelPort;
    private volatile boolean stop;

    public VacuumWorldController(int controllerPort, String modelIp, int modelPort) {
	this.controllerPort = controllerPort;
	this.modelIp = modelIp;
	this.modelPort = modelPort;
	this.stop = false;
    }

    public void setStop() {
	this.stop = true;
    }
    
    public void removeStop() {
	this.stop = false;
    }
    
    public void init() throws IOException {
	try (ServerSocket controllerServer = new ServerSocket(this.controllerPort);) {
	    LogUtils.log("Controller here: waiting for a GUI to connect...");

	    while (!this.stop) {
		Socket socket = controllerServer.accept();
		LogUtils.log("Controller here: a GUI attempted a connection: " + socket.getRemoteSocketAddress());
		VacuumWorldClientManager manager = new VacuumWorldClientManager(socket, this.modelIp, this.modelPort);
		Thread t = new Thread(manager);
		t.start();
	    }
	}
	catch(Exception e) {
	    throw new VacuumWorldRuntimeException(e);
	}
    }
}