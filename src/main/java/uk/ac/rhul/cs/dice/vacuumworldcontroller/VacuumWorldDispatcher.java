package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.net.ServerSocket;
import java.util.ArrayList;
import java.util.List;

public class VacuumWorldDispatcher {
    private final int port;
    private ServerSocket server;
    private List<VacuumWorldClientManager> clientManagers;
    private volatile boolean stop;
    
    public VacuumWorldDispatcher(int port) {
	this.clientManagers = new ArrayList<>();
	this.port = port;
    }
    
    public void start() throws IOException {
	//TODO start message
	
	while(!this.stop) {
	    this.server = new ServerSocket(this.port);
	    this.clientManagers.add(new VacuumWorldClientManager(this.server.accept()));
	    //TODO new client message
	}
	
	//TODO stop message
    }
}