package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.cloudstrife9999.logutilities.LogUtils;

public class VacuumWorldController {
    private ServerSocket controllerServer;
    private int controllerPort;
    private String modelIp;
    private int modelPort;
    
    public VacuumWorldController(int controllerPort, String modelIp, int modelPort) {
	this.controllerPort = controllerPort;
	this.modelIp = modelIp;
	this.modelPort = modelPort;
    }
    
    public void init() throws IOException {
	this.controllerServer = new ServerSocket(this.controllerPort);
        
	LogUtils.log("Controller here: waiting for a GUI to connect...");
	
        while(true) {
            Socket socket = this.controllerServer.accept();
            LogUtils.log("Controller here: a GUI attempted a connection: " + socket.getRemoteSocketAddress());
            VacuumWorldClientManager manager = new VacuumWorldClientManager(socket, this.modelIp, this.modelPort);
            Thread t = new Thread(manager);
            t.start();
        }
    }
}