package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;

public class Main {
    
    private Main() {}
    
    public static void main(String[] args) throws IOException {
        VacuumWorldController controller = new VacuumWorldController(13337, 17777);
        controller.init();
    }
}