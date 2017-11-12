package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VacuumWorldClientManager {
    private Process process;
    private Socket clientSocket;
    private InputStream is;
    private OutputStream os;
    private ObjectInputStream ois;
    private ObjectOutputStream oos;
    
    public VacuumWorldClientManager(Socket clientSocket) throws IOException {
	this.clientSocket = clientSocket;
	this.is = this.clientSocket.getInputStream();
	this.os = this.clientSocket.getOutputStream();
	this.ois = new ObjectInputStream(this.is);
	this.oos = new ObjectOutputStream(this.os);
	this.process = createNewModelInstance();
    }

    private Process createNewModelInstance() {
	// TODO Auto-generated method stub
	return null;
    }
}