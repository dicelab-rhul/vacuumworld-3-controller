package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

public class VacuumWorldClientManager {
    private Process modelProcess;
    private InputStream fromModel;
    private InputStream errorsFromModel;
    private OutputStream toModel;
    private ObjectInputStream fromModelObjectStream;
    private ObjectOutputStream toModelObjectStream;
    private Socket viewSocket;
    private InputStream fromView;
    private OutputStream toView;
    private ObjectInputStream fromViewObjectStream;
    private ObjectOutputStream toViewObjectStream;
    
    public VacuumWorldClientManager(Socket clientSocket) throws IOException {
	this.viewSocket = clientSocket;
	this.fromView = this.viewSocket.getInputStream();
	this.toView = this.viewSocket.getOutputStream();
	this.fromViewObjectStream = new ObjectInputStream(this.fromView);
	this.toViewObjectStream = new ObjectOutputStream(this.toView);
	
	createNewModelInstance();
    }

    private void createNewModelInstance() throws IOException {
	this.modelProcess = createModelProcess();
	this.fromModel = this.modelProcess.getInputStream();
	this.errorsFromModel = this.modelProcess.getErrorStream();
	this.toModel = this.modelProcess.getOutputStream();
	this.fromModelObjectStream = new ObjectInputStream(this.fromModel);
	this.toModelObjectStream = new ObjectOutputStream(this.toModel);
    }

    private Process createModelProcess() throws IOException {
	return Runtime.getRuntime().exec(new String[] {"java", "-jar", "vw3.jar"});
    }
    
    public Process getModelProcess() {
	return this.modelProcess;
    }
    
    public InputStream getFromModel() {
	return this.fromModel;
    }
    
    public InputStream getErrorsFromModel() {
	return this.errorsFromModel;
    }
    
    public OutputStream getToModel() {
	return this.toModel;
    }
    
    public ObjectInputStream getFromModelObjectStream() {
	return this.fromModelObjectStream;
    }
    
    public ObjectOutputStream getToModelObjectStream() {
	return this.toModelObjectStream;
    }
    
    public Socket getViewSocket() {
	return this.viewSocket;
    }
    
    public InputStream getFromView() {
	return this.fromView;
    }
    
    public OutputStream getToView() {
	return this.toView;
    }
    
    public ObjectInputStream getFromViewObjectStream() {
	return this.fromViewObjectStream;
    }
    
    public ObjectOutputStream getToViewObjectStream() {
	return this.toViewObjectStream;
    }
}