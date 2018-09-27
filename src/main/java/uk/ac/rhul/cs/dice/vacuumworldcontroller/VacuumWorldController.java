package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.net.ServerSocket;

public class VacuumWorldController {
    private ServerSocket controllerServer;
    private int controllerPort;
    private int modelPort;
    
    public VacuumWorldController(int controllerPort, int modelPort) {
	this.controllerPort = controllerPort;
	this.modelPort = modelPort;
    }
    
    public void init() throws IOException {
	this.controllerServer = new ServerSocket(this.controllerPort);
        
        while(true) {
            VacuumWorldClientManager manager = new VacuumWorldClientManager(this.controllerServer.accept(), this.modelPort);
            Thread t = new Thread(manager);
            t.start();
        }
    }
}