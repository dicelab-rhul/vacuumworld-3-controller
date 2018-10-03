package uk.ac.rhul.cs.dice.vacuumworldcontroller;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.cloudstrife9999.logutilities.LogUtils;

import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VWMessageCodes;
import uk.ac.rhul.cs.dice.vacuumworld.vwcommon.VacuumWorldMessage;

public class VacuumWorldClientManager implements Runnable {
    private Process modelProcess;
    private Socket modelSocket;
    private String modelIp;
    private int modelPort;
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
    private volatile boolean stop;
    private VacuumWorldMessage latestFromView;
    private VacuumWorldMessage latestFromModel;
    
    public VacuumWorldClientManager(Socket clientSocket, String modelIp, int modelPort) throws IOException {
	this.viewSocket = clientSocket;
	this.fromView = this.viewSocket.getInputStream();
	this.toView = this.viewSocket.getOutputStream();
	this.fromViewObjectStream = new ObjectInputStream(this.fromView);
	this.toViewObjectStream = new ObjectOutputStream(this.toView);
	
	this.modelIp = modelIp;
	this.modelPort = modelPort;
	//createNewModelInstance();
	connectToModel();
    }

    private void connectToModel() throws IOException {
	LogUtils.log("Controller here: attepting to connect to the model at " + this.modelIp + ":" + this.modelPort + "...");
	
	this.modelSocket = new Socket(this.modelIp, this.modelPort);
	this.toModel = this.modelSocket.getOutputStream();
	this.fromModel = this.modelSocket.getInputStream();
	this.fromModelObjectStream = new ObjectInputStream(this.fromModel);
	this.toModelObjectStream = new ObjectOutputStream(this.toModel);
	
	LogUtils.log("Controller here: connected to the model at " + this.modelIp + ":" + this.modelPort + ".");
    }

    /*private void createNewModelInstance() throws IOException {
	this.modelProcess = createModelProcess();
	this.fromModel = this.modelProcess.getInputStream();
	this.errorsFromModel = this.modelProcess.getErrorStream();
	this.toModel = this.modelProcess.getOutputStream();
	this.fromModelObjectStream = new ObjectInputStream(this.fromModel);
	this.toModelObjectStream = new ObjectOutputStream(this.toModel);
    }

    private Process createModelProcess() throws IOException {
	return Runtime.getRuntime().exec(new String[] {"java", "-jar", "vw3.jar"});
    }*/
    
    public boolean hasToStop() {
	return this.stop;
    }
    
    public void stop() {
	this.stop = true;
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

    @Override
    public void run() {
	LogUtils.log("Controller here: thread running! Currently managing a GUI.");
	
	doHandshake();
	
	while(!this.stop) {
	    cycle();
	}
    }

    private void doHandshake() {
	LogUtils.log("Controller here: waiting for the first handshake message from the GUI...");
	
	receiveHVC();
	receiveHMC();
	receiveHVM();
	receiveHMV();
	
	LogUtils.log("Controller here: handshakes completed!");
    }

    private void receiveHVC() {
	try {
	    this.latestFromView = (VacuumWorldMessage) this.fromViewObjectStream.readObject();
	    parseHVC();
	    
	    LogUtils.log("Controller here: initiating handshake with the model...");
	    
	    sendHCM();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}	
    }

    private void sendHCM() throws IOException {
	sendTo(this.toModelObjectStream, new VacuumWorldMessage(VWMessageCodes.HELLO_MODEL_FROM_CONTROLLER, null), "model");
    }

    private void receiveHMC() {
	try {
	    this.latestFromModel = (VacuumWorldMessage) this.fromModelObjectStream.readObject();
	    parseHMC();
	    sendHCV();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
    }

    private void sendHCV() throws IOException {
	sendTo(this.toViewObjectStream, new VacuumWorldMessage(VWMessageCodes.HELLO_VIEW_FROM_CONTROLLER, null), "view");
    }

    private void receiveHVM() {
	try {
	    this.latestFromView = (VacuumWorldMessage) this.fromViewObjectStream.readObject();
	    parseHVM();
	    sendHVM();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
    }

    private void sendHVM() throws IOException {
	sendTo(this.toModelObjectStream, this.latestFromView, "model");
    }

    private void receiveHMV() {
	try {
	    this.latestFromModel = (VacuumWorldMessage) this.fromModelObjectStream.readObject();
	    parseHMV();
	    sendHMV();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
    }

    private void sendHMV() throws IOException {
	sendTo(this.toViewObjectStream, this.latestFromModel, "view");
    }
    
    private void sendTo(ObjectOutputStream to, VacuumWorldMessage message, String recipient) throws IOException {
	LogUtils.log("Controller here: sending " + message.getCode() + " to the " + recipient + "...");
	
	to.reset();
	to.writeObject(message);
	to.flush();
    }

    private void cycle() {
	try {
	    waitForView();
	    waitForModel();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}
	
	/*
	try {
	    LogUtils.log("Controller here: I am idle...");
		
	    Thread.sleep(5000);
	}
	catch(InterruptedException e) {
	    Thread.currentThread().interrupt();
	}*/
    }
    
    private void waitForView() throws IOException {
	try {
	    LogUtils.log("Controller here: waiting for view...");
	    
	    VacuumWorldMessage message = (VacuumWorldMessage) this.fromViewObjectStream.readObject();
	    
	    this.toModelObjectStream.reset();
	    this.toModelObjectStream.writeObject(message);
	    this.toModelObjectStream.flush();
	}
	catch(Exception e) {
	    shutdown(e);
	}
    }

    private void shutdown(Exception e) {
	LogUtils.fakeLog(e);
	
	System.exit(0);
    }

    private void waitForModel() throws IOException {
	try {
	    LogUtils.log("Controller here: waiting for model...");
	    
	    VacuumWorldMessage message = (VacuumWorldMessage) this.fromModelObjectStream.readObject();
	    
	    LogUtils.log(message.getContent().toString());
	    
	    this.toViewObjectStream.reset();
	    this.toViewObjectStream.writeObject(message);
	    this.toViewObjectStream.flush();
	}
	catch(Exception e) {
	    shutdown(e);
	}
    }

    private void parseHVC() {
	parseMessageType(VWMessageCodes.HELLO_CONTROLLER_FROM_VIEW, this.latestFromView, "view");
    }

    private void parseHMC() {
	parseMessageType(VWMessageCodes.HELLO_CONTROLLER_FROM_MODEL, this.latestFromModel, "model");
    }
    
    private void parseHVM() {
	parseMessageType(VWMessageCodes.HELLO_MODEL_FROM_VIEW, this.latestFromView, "view");
    }

    private void parseHMV() {
	parseMessageType(VWMessageCodes.HELLO_VIEW_FROM_MODEL, this.latestFromModel, "model");
    }
    
    
    private void parseMessageType(VWMessageCodes expected, VacuumWorldMessage message, String sender) {
	VWMessageCodes receivedCode = message.getCode();
	
	if(!expected.equals(receivedCode)) {
	    throw new IllegalArgumentException("Expected" + expected + ", got " + receivedCode + " instead.");
	}
	else {
	    LogUtils.log("Controller here: received " + receivedCode + " from the " + sender);
	}
    }
}