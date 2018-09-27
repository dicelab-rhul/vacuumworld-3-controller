package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.net.ServerSocket;

public class VacuumWorldController {
    
    private VacuumWorldController() {}
    
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(13337);
        
        /*while(true) {
            VacuumWorldClientManager manager = new VacuumWorldClientManager(server.accept());
            Thread t = new Thread(manager);
            t.start();
        }*/
        VacuumWorldClientManager manager = new VacuumWorldClientManager(server.accept());
        manager.run();
        //TODO close server
    }
}