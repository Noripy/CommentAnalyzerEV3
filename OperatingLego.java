package legoEv3;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;

import lejos.hardware.Sound;
import lejos.hardware.lcd.LCD;
import lejos.hardware.motor.Motor;
import lejos.hardware.port.SensorPort;
import lejos.hardware.sensor.EV3ColorSensor;
import lejos.hardware.sensor.EV3UltrasonicSensor;
import lejos.robotics.Color;
import lejos.robotics.RegulatedMotor;
import lejos.robotics.SampleProvider;
import lejos.utility.Delay;

public class OperatingLego {
	static RegulatedMotor motorLeftWheel;
	static RegulatedMotor motorRightWheel;

	static int leftWheelSpeed;
	static int rightWheelSpeed;
	static LightDetection ld;
	static DistanceDetection dd;
	static ReadInstruction ri;
	final int term_width = 5;
	boolean restart_state = false;
	boolean game_start = false;
	boolean isNowGame = false;
	
	BufferedWriter bw;
	boolean isGoal = false;
	int commentCounter = 0;
	final int endOfCommentGetting = 20;
	
	public static void main(String[] args) throws IOException {
		
	      new OperatingLego().testDrive6();
	      
	}
/*	
	private void testDrive5() throws IOException {
		init();
		//System.out.println("Init passed.");
		
		start();
		while (true) {
			//System.out.println("running.");
//			int kernel = Integer.parseInt(this.br.readLine());
//			this.processcommand(kernel);
			if (isGoal) {
				break;
			}
		}
		//del();
		//closeSocket();
		System.exit(0);
	}
*/
	private void testDrive6() throws IOException {
		init();

		while (true) {
			if (game_start && !isNowGame) {
				start();
				isNowGame = true;
			}
			
			if (isGoal) {
				isNowGame = false;
				game_start = false;
				break;
			}
		}
		System.exit(0);
	}

	void init() throws IOException {
		
		motorLeftWheel = Motor.B;
		motorRightWheel = Motor.C;
		
		leftWheelSpeed = 30;
		rightWheelSpeed = 30;
		ld = new LightDetection(this);
		dd = new DistanceDetection(this);
		ri = new ReadInstruction(this);
		ri.start();
		bw = new BufferedWriter(new OutputStreamWriter(ri.socket.getOutputStream()));		
		
		ld.start();
		dd.start();
		
		motorLeftWheel.setSpeed(leftWheelSpeed);
		motorRightWheel.setSpeed(rightWheelSpeed);
		
		game_start = false;
		isNowGame = false;
	}

	// start running
	void start() {
		motorLeftWheel.forward();
		motorRightWheel.forward();
	}

	// increase left wheel speed
	synchronized void increaseLeftWheelSpeed(int width) {
		leftWheelSpeed += width;
		if (leftWheelSpeed >= 0) {
			motorLeftWheel.setSpeed(leftWheelSpeed);
			motorLeftWheel.forward();
		} else {
			if (leftWheelSpeed > 100) {
				leftWheelSpeed = 100;
			}
			motorLeftWheel.setSpeed(leftWheelSpeed);
			motorLeftWheel.backward();
		}
		int percent = Math.abs(leftWheelSpeed);
		motorLeftWheel.setSpeed(percent);
		LCD.clear(0, 3, 4);
		LCD.clear(0, 4, 4);
		LCD.drawInt(leftWheelSpeed, 0, 3);
		LCD.drawInt(rightWheelSpeed, 0, 4);
		sendCarInfo();
	}

	private void sendCarInfo() {
		try {

			String car_info = leftWheelSpeed + "," + rightWheelSpeed;
			LCD.drawString(car_info, 0, 5);
			//dos.writeChars(car_info);
			//dos.flush();
			
			bw.write(car_info);
			bw.flush();

		} catch (IOException ioe) {
			System.out.println("IO Exception writing bytes:");
			System.out.println(ioe.getMessage());
		}
	}

	// decrease left wheel speed
	synchronized void decreaseLeftWheelSpeed(int width) {
		leftWheelSpeed -= width;
		if (leftWheelSpeed >= 0) {
			motorLeftWheel.setSpeed(leftWheelSpeed);
			motorLeftWheel.forward();
		} else {
			if (leftWheelSpeed < -100) {
				leftWheelSpeed = -100;
			}
			motorLeftWheel.setSpeed(leftWheelSpeed);
			motorLeftWheel.backward();
		}
		int percent = Math.abs(leftWheelSpeed);
		motorLeftWheel.setSpeed(percent);
		LCD.clear(0, 3, 4);
		LCD.clear(0, 4, 4);
		LCD.drawInt(leftWheelSpeed, 0, 3);
		LCD.drawInt(rightWheelSpeed, 0, 4);
		sendCarInfo();
	}

	// increase right wheel speed
	synchronized void increaseRightWheelSpeed(int width) {
		rightWheelSpeed += width;
		if (rightWheelSpeed >= 0) {
			motorRightWheel.setSpeed(leftWheelSpeed);
			motorRightWheel.forward();
		} else {
			if (rightWheelSpeed > 100) {
				rightWheelSpeed = 100;
			}
			motorRightWheel.setSpeed(leftWheelSpeed);			
			motorRightWheel.backward();
		}
		int percent = Math.abs(rightWheelSpeed);
		motorRightWheel.setSpeed(percent);
		LCD.clear(0, 3, 4);
		LCD.clear(0, 4, 4);
		LCD.drawInt(leftWheelSpeed, 0, 3);
		LCD.drawInt(rightWheelSpeed, 0, 4);
		sendCarInfo();
	}

	// decrease right wheel speed
	synchronized void decreaseRightWheelSpeed(int width) {
		rightWheelSpeed -= width;
		if (rightWheelSpeed >= 0) {
			motorRightWheel.setSpeed(leftWheelSpeed);
			motorRightWheel.forward();
		} else {
			if (rightWheelSpeed < -100) {
				rightWheelSpeed = -100;
			}
			motorRightWheel.setSpeed(leftWheelSpeed);
			motorRightWheel.backward();
		}
		int percent = Math.abs(rightWheelSpeed);
		motorRightWheel.setSpeed(percent);
		LCD.clear(0, 3, 4);
		LCD.clear(0, 4, 4);
		LCD.drawInt(leftWheelSpeed, 0, 3);
		LCD.drawInt(rightWheelSpeed, 0, 4);
		sendCarInfo();
	}

	synchronized void avoidObject() {
		Sound.playTone(500, 2000);
		motorLeftWheel.setSpeed(60);
		motorRightWheel.setSpeed(60);
		motorLeftWheel.backward();
		motorRightWheel.backward();
		try {
			Thread.sleep(2000);
		} catch (Exception e) {

		}
		stopCar();
		sendCarInfo();
	}

	synchronized void goalNotification() {
		Sound.playTone(415, 2000);
		stopCar();
		sendGoalInfo();
	}

	private void sendGoalInfo() {

		try {
			String car_info1 = "GOAL";
			LCD.drawString(car_info1, 0, 5);
			bw.write(car_info1);
			bw.flush();
			
			//dos.writeChars(car_info);
			//dos.flush();

		} catch (IOException ioe) {
			System.out.println("IO Exception writing bytes:");
			System.out.println(ioe.getMessage());
		}
	}

	synchronized void avoidProscess() {
		stopThreadsWithoutSonic();
		avoidObject();
		// try {
		// Thread.sleep(2000);
		// } catch (Exception e) {
		// }
		restartThreadsWithoutSonic();
	}

	synchronized void goalProcess() {
		stopThreadsWithoutLight();
		goalNotification();
		// try {
		// Thread.sleep(2000);
		// } catch (Exception e) {
		//

	}

	void stopThreadsWithoutSonic() {
		dd.continueDD = false;
		ld.continueLD = false;
		ri.continueRI = false;
	}

	void restartThreadsWithoutSonic() {
		try {
			//br.reset();
			ri.br.reset();
			
			//ri.dis.reset();
		} catch (Exception e) {

		}
		ri.continueRI = true;
		dd.continueDD = true;
		ld.continueLD = true;

	}

	void stopThreadsWithoutLight() {
		restart_state = true;
		dd.continueDD = false;
		ld.continueLD = false;

	}

	void restartThreadsWithoutLight() {
		try {
			//br.reset();
			ri.br.reset();
			
			//ri.dis.reset();
			
		} catch (Exception e) {

		}
		restart_state = false;
		dd.continueDD = true;
		ld.continueLD = true;

	}

	synchronized void stopCar() {
		leftWheelSpeed = 0;
		rightWheelSpeed = 0;
		motorLeftWheel.setSpeed(leftWheelSpeed);
		motorRightWheel.setSpeed(rightWheelSpeed);
	}

	// delete process
	void del() throws IOException{
		ld.running = false;
		dd.running = false;
		ri.running = false;
		bw.close();
		bw = null;
		// sc.running = false;
		motorLeftWheel.close();
		motorRightWheel.close();
		//System.exit(0);
	}

	void closeSocket() throws IOException{
		//bw.close();
		ri.serverSocket.close();
		ri.socket.close();

	}
	
	public void processcommand(int instruction) throws IOException {
		System.out.println("instruction:"+instruction);
		if (!restart_state && isNowGame) {
			int ten_digit = instruction / 10;
			int one_digit = instruction % 10;
			int width = term_width * one_digit;
			//System.out.println(ten_digit);
			switch (ten_digit) {
			case 1:
				increaseLeftWheelSpeed(width);
				break;
			case 2:
				decreaseLeftWheelSpeed(width);
				break;
			case 3:
				increaseRightWheelSpeed(width);
				break;
			case 4:
				decreaseRightWheelSpeed(width);
				break;
			}
		} else {//車型ロボットの車輪制御以外の処理
			switch (instruction) {
			case 1:
				del();				
				break;
			case 2:
				restartThreadsWithoutLight();
				break;
			case 7://Proc.GAME_START.ordinal()
				game_start = true;
				break;
			default:
				break;
			}
		}
	}

}

class LightDetection extends Thread implements Runnable{
	
	public final int blackValue = 7;
	EV3ColorSensor lightS1 = new EV3ColorSensor(SensorPort.S1);
	OperatingLego main;
	public int floorValue;
	boolean running;
	boolean continueLD;

	LightDetection(OperatingLego operatingLego) {
		//lightS1 = new EV3ColorSensor(SensorPort.S1);
		lightS1.setFloodlight(Color.WHITE);
		
		main = operatingLego;
		floorValue = Color.WHITE;
		running = true;
		continueLD = true;
	}

	public void run() {
		//int floorValue = 100;
		while (running) {
			if (continueLD) {
				// LCD.clear();
				floorValue = lightS1.getColorID();
				//LCD.clear(0, 1, 3);
				//LCD.drawInt(floorValue, 0, 1);
				if (isGoal(floorValue)) {
					main.goalProcess();
					main.isGoal = true;

				}
				try {
					Thread.sleep(500);
				} catch (Exception e) {
					break;
				}
			}
		}
	}

	boolean isGoal(int floor) {
		if (floor == blackValue) {
			return true;
		} else {
			return false;
		}
	}
}

class DistanceDetection extends Thread{
	final int distance = 2;
	EV3UltrasonicSensor sonicS2 = new EV3UltrasonicSensor(SensorPort.S2);
	OperatingLego main;
	boolean running;
	boolean continueDD;

	DistanceDetection(OperatingLego operatingLego) {
		//sonicS2 = new EV3UltrasonicSensor(SensorPort.S2);
		main = operatingLego;
		running = true;
		continueDD = true;
	}
	
	public void run() {
		//int sonicvalue = 255;

		float frequency = 1;

		// ref:https://www.slideshare.net/OracleMiddleJP/lego-mindstormslejos-hands-on-lab
		SampleProvider sp2 = sonicS2.getDistanceMode();
		float[] sampleDist = new float[sp2.sampleSize()];
		
		while (running) {
			if (continueDD) {
				// LCD.clear();
				// sonicvalue = sonicS2.getDistance();
				sp2.fetchSample(sampleDist, 0);
				int centimeter = (int) (sampleDist[0] * 100);
				// 1m��1.000 (MIN:3cm MAX:250cm)
				LCD.clear(0, 2, 3);
				LCD.drawString("Distance: " + centimeter + "cm", 0, 2);
				if (isClose(centimeter)) {
					main.avoidProscess();
				}
				try {
					Thread.sleep(500);
				} catch (Exception e) {

				}
			}
			Delay.msDelay((long) (200 / frequency));

		}
	}

	boolean isClose(int cm) {
		if (cm < distance) {
			return true;
		} else {
			return false;
		}

		/*
		 * if (sample[0] < 0.1) { Sound.playTone(2000, 100); }else if (sample[0]
		 * < 0.3) { Sound.playTone(500, 100); }
		 */

		/*
		 * if (sonicvalue < distance) { return true; } else { return false; }
		 */
	}
}

class ReadInstruction extends Thread implements Runnable{
	//DataInputStream dis;
	BufferedReader br;
	OperatingLego main;
	boolean running;
	boolean continueRI;
	Socket socket;
	ServerSocket serverSocket;
	
	ReadInstruction(OperatingLego operatingLego) throws IOException{
		main = operatingLego;
		try {
			serverSocket = new ServerSocket(174);
			LCD.clear();
			LCD.drawString("Please Wait...", 0, 0);
			socket = serverSocket.accept();
			LCD.clear();
			
			br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			if(br == null){
				System.out.println("missing...");
			}else{

				running = true;
				continueRI = true;

			}
			
		} catch (Exception e) {
			System.out.println(e);
		}
	}

	@Override public void run() {
		int instruction = 100;
		while (running) {
			try {
			//	Thread.sleep(500);

				instruction = br.read();
				
			} catch (Exception e) {
				System.out.println(e);
			}
			if (continueRI) {
				// System.out.print(instruction);
				try {
					main.processcommand(instruction);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				main.commentCounter++;
			}
		}
		try {
			//dis.close();
			//main.br.close();
			br.close();
			if ( socket != null ) {
				socket.close();
				socket = null;
			}
			if ( serverSocket != null ) {
				serverSocket.close();
				serverSocket = null;
			}
			main.del();

		} catch (Exception e) {

		}
	}
}
