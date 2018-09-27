package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;

import org.cloudstrife9999.logutilities.LogUtils;

public class Main {
    
    private Main() {}
    
    public static void main(String[] args) throws IOException {
	LogUtils.log("Controller started.");
	
        VacuumWorldController controller = new VacuumWorldController(13337, "127.0.0.1", 17777);
        controller.init();
    }
}