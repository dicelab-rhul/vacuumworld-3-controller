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
    
    public VacuumWorldClientManager(Socket clientSocket) throws IOException {
	this.viewSocket = clientSocket;
	this.fromView = this.viewSocket.getInputStream();
	this.toView = this.viewSocket.getOutputStream();
	this.fromViewObjectStream = new ObjectInputStream(this.fromView);
	this.toViewObjectStream = new ObjectOutputStream(this.toView);
	
	//createNewModelInstance();
	connectToModel();
    }

    private void connectToModel() throws IOException {
	this.modelSocket = new Socket("127.0.0.1", 17777);
	this.toModel = this.modelSocket.getOutputStream();
	this.fromModel = this.modelSocket.getInputStream();
	this.fromModelObjectStream = new ObjectInputStream(this.fromModel);
	this.toModelObjectStream = new ObjectOutputStream(this.toModel);
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
	doHandshake();
	
	while(!this.stop) {
	    cycle();
	}
    }

    private void doHandshake() {
	receiveHVC();
	receiveHMC();
	receiveHVM();
	receiveHMV();
	
	LogUtils.log("Controller here: handshake completed!");
    }

    private void receiveHVC() {
	try {
	    this.latestFromView = (VacuumWorldMessage) this.fromViewObjectStream.readObject();
	    parseHVC();
	    sendHCM();
	}
	catch(Exception e) {
	    LogUtils.log(e);
	}	
    }

    private void sendHCM() throws IOException {
	sendTo(this.toModelObjectStream, new VacuumWorldMessage(VWMessageCodes.HELLO_MODEL_FROM_CONTROLLER, null));
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
	sendTo(this.toViewObjectStream, new VacuumWorldMessage(VWMessageCodes.HELLO_VIEW_FROM_CONTROLLER, null));
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
	sendTo(this.toModelObjectStream, this.latestFromView);
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
	sendTo(this.toViewObjectStream, this.latestFromModel);
    }
    
    private void sendTo(ObjectOutputStream to, VacuumWorldMessage message) throws IOException {
	to.reset();
	to.writeObject(message);
	to.flush();
    }

    private void cycle() {
	// TODO Auto-generated method stub
    }
    
    private void parseHVC() {
	parseMessageType(VWMessageCodes.HELLO_CONTROLLER_FROM_VIEW, this.latestFromView);
    }

    private void parseHMC() {
	parseMessageType(VWMessageCodes.HELLO_CONTROLLER_FROM_MODEL, this.latestFromModel);
    }
    
    private void parseHVM() {
	parseMessageType(VWMessageCodes.HELLO_MODEL_FROM_VIEW, this.latestFromView);
    }

    private void parseHMV() {
	parseMessageType(VWMessageCodes.HELLO_VIEW_FROM_MODEL, this.latestFromModel);
    }
    
    
    private void parseMessageType(VWMessageCodes expected, VacuumWorldMessage message) {
	VWMessageCodes receivedCode = message.getCode();
	
	if(!expected.equals(receivedCode)) {
	    throw new IllegalArgumentException("Expected" + expected + ", got " + receivedCode + " instead.");
	}
	else {
	    LogUtils.log("Controller here: received " + receivedCode);
	}
    }
}