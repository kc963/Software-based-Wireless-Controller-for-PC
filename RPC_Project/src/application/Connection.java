package application;

import java.io.*;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.JOptionPane;

import javafx.scene.paint.Color;

import java.awt.Robot;
import java.awt.event.KeyEvent;

public class Connection {

	private ObjectOutputStream output;
	private InputStream input;
	private String message = "";
	private String serverIP;
	private Socket connection;
	private Robot robot;
	boolean connected;
	Main object;
	
	
	public static void main(String args[]){
		Connection runner = new Connection();
	}
	
	
	public Connection(){
		serverIP = "";
		object = new Main();
		
		connected = false;
		
		object.connect.setOnMouseClicked(e -> {
			serverIP = object.ip.getText();
			if(serverIP.length()<3){
				JOptionPane.showMessageDialog(null, "Enter IP Address");
			}else{
				startRunning();
			}
		});
		object.disconnect.setOnMouseClicked(e -> {
			if(connected){
				closeCrap();
			}
		});
	}
	
	public void startRunning(){
		try{
			connectToServer();
			setupStreams();
			whileChatting();
		}catch(EOFException eofE){
			System.out.println("\n Client Terminated the connection");
		}catch(IOException ioE){
			ioE.printStackTrace();
		}catch(Exception e){
			System.out.println("\n Connection not established.");
			e.printStackTrace();
		}finally{
			closeCrap();
		}
	}
	
	//connect to server
	private void connectToServer(){
		System.out.println("Attempting connection... \n");
		try{
			connection = new Socket(InetAddress.getByName(serverIP),7553);
			String device = connection.getInetAddress().getHostName();
			System.out.println("Connected to: "+ device);
			object.status_value.setText("Connected to " + device);
			object.status_value.setTextFill(Color.GREEN);
			connected = true;
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Connection Error\nEnter valid IP Address");
		}
	}
	
	//setup stream to send and receive messages
	private void setupStreams(){
		System.out.println("\nConnecting Streams...");
		//output = new ObjectOutputStream(connection.getOutputStream());
		//output.flush();
		try{
			input = connection.getInputStream();
			System.out.println("\n Your streams are now good to go! \n");
		}catch(Exception e){
			JOptionPane.showMessageDialog(null, "Error in streams setup");
			try {
				connection.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
			object.status_value.setText("Not Connected");
			object.status_value.setTextFill(Color.RED);
		}
	}
	
	//while chatting with server
	private void whileChatting() throws IOException{
		do{
			try{
				int x = input.read();//ASCII data 49 = 1
				message = x + "";
				System.out.println("\n" + message);
				robot = new Robot();
				if(x<55){
					robot.keyPress(object.events[x-49].getCode().impl_getCode());
					robot.keyRelease(object.events[x-49].getCode().impl_getCode());
				}
				switch(x){
					case 55:robot.keyPress(KeyEvent.VK_UP);
							robot.keyRelease(KeyEvent.VK_UP);
							break;
					case 56:robot.keyPress(KeyEvent.VK_LEFT);
							robot.keyRelease(KeyEvent.VK_LEFT);
							break;
					case 57:robot.keyPress(KeyEvent.VK_RIGHT);
							robot.keyRelease(KeyEvent.VK_RIGHT);
							break;
				}
			}catch(Exception cnfE){
				System.out.println("\n IDK that object type");
			}
		}while(!message.equals("48"));
	}
	
	private void closeCrap(){
		System.out.println("\n Closing connections...");
		try{
			//output.close();
			input.close();
			connection.close();
			System.out.println("\nConnections Closed\n");
			object.status_value.setText("Not Connected");
			object.status_value.setTextFill(Color.RED);
			connected = false;
		}catch(IOException ioE){
			ioE.printStackTrace();
		}
	}
	
}