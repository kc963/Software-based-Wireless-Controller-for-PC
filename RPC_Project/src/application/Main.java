package application;
	
import java.awt.AWTException;
import java.awt.Robot;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import javax.swing.JOptionPane;
import javafx.application.Application;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.VPos;
import javafx.stage.Stage;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.scene.text.Font;
import javafx.scene.text.Text;


public class Main extends Application {
	
	Label space;// = new Label("");
	Text heading;// = new Text("RPC");
	TextField ip;// = new TextField();
	Button connect;// = new Button("Connect");
	Button disconnect;// = new Button("Disconnect");
	Label status;// = new Label("Status :");
	Label status_value;// = new Label("Not Connected");
	Label label[];// = new Label[6];
	Label value[];// = new Label[6];
	Button button[];// = new Button[6];
	Button reset[];// = new Button[6];
	KeyEvent events[];// = new KeyEvent[6];
	
	private InputStream input;
	private String message;
	private String serverIP;
	private Socket connection;
	private Robot robot;
	boolean connected;
	Thread connect_th, disconnect_th;
	boolean keeprunning;
	
	public void initializer(){
		message = "";
		serverIP = "";
		connected = false;
		keeprunning = false;
		//disconnect.setDisable(true);
		/*connect_th = new Thread(new Runnable(){
			public void run(){
				connectToServer();
				setupStreams();
				beginChat();
			}
		});
		disconnect_th = new Thread(new Runnable(){
			public void run(){
				closeAll();
			}
		});
		*/
	}
	
	@Override
	public void start(Stage primaryStage) {
		try {
			initializer();
			connection = null;
			space = new Label("");
			space.setMinWidth(50);
			
			heading = new Text("RPC");
			heading.setFont(new Font(35));
			heading.setUnderline(true);
			
			ip = new TextField();
			ip.setPromptText("Enter IP Address");
			
			connect = new Button("Connect");
			connect.setOnMouseClicked(e -> {
				if(ip.getText().length()<1){
					JOptionPane.showMessageDialog(null, "Enter IP Address");
				}else{
					serverIP = ip.getText();
					keeprunning = true;
					//connect_th.start();
					changeStatusText("Attempting Connection", Color.CORNFLOWERBLUE);
					connectToServer();
					setupStreams();
					beginChat();
				}
			});
			disconnect = new Button("Disconnect");
			disconnect.setOnMouseClicked(e -> {
				if(connected){
					//disconnect_th.start();
					closeAll();
				}
			});
			
			status = new Label("Status :");
			status_value = new Label("Not Connected");
			status_value.setTextFill(Color.RED);
			
			label = new Label[6];
			value = new Label[6];
			button = new Button[6];
			reset = new Button[6];
			events = new KeyEvent[6];
			
			for(int i=0;i<6;i++){
				label[i] = new Label("Button " + (i+1));
				value[i] = new Label("");
				value[i].setId("" + (i+1));
				button[i] = new Button("Set Key");
				button[i].setId(""+(i+1));
				reset[i] = new Button("X");
				reset[i].setId(""+(i+51));
				events[i] = null;
			}
			
			Line line = new Line();
			line.setStrokeWidth(2);
			line.setStroke(Color.BLUE);
						
			GridPane root = new GridPane();
			root.setMinSize(533, 300);
			//root.setGridLinesVisible(true);
			root.setPadding(new Insets(15,15,15,15));
			root.setVgap(15);
			root.setHgap(10);
			root.setAlignment(Pos.CENTER);
			
			root.add(heading, 0, 0, 10, 1);
			root.setHalignment(heading, HPos.CENTER);
			root.setValignment(heading, VPos.CENTER);
			root.add(ip, 2, 2, 3, 1);
			root.setHalignment(ip, HPos.RIGHT);
			root.setValignment(ip, VPos.CENTER);
			root.add(connect, 5, 2, 2, 1);
			root.setHalignment(connect, HPos.LEFT);
			root.setValignment(connect, VPos.CENTER);
			root.add(disconnect, 7, 2, 2, 1);
			root.setHalignment(connect, HPos.LEFT);
			root.setValignment(connect, VPos.CENTER);
			
			root.add(label[0], 1, 4);
			root.add(value[0], 2, 4);
			root.add(button[0], 3, 4);
			root.add(reset[0], 4, 4);
			root.add(label[1], 1, 5);
			root.add(value[1], 2, 5);
			root.add(button[1], 3, 5);
			root.add(reset[1], 4, 5);
			root.add(label[2], 1, 6);
			root.add(value[2], 2, 6);
			root.add(button[2], 3, 6);
			root.add(reset[2], 4, 6);

			root.add(space, 4, 2);
			
			root.add(label[3], 6, 4);
			root.add(value[3], 7, 4);
			root.add(button[3], 8, 4);
			root.add(reset[3], 9, 4);
			root.add(label[4], 6, 5);
			root.add(value[4], 7, 5);
			root.add(button[4], 8, 5);
			root.add(reset[4], 9, 5);
			root.add(label[5], 6, 6);
			root.add(value[5], 7, 6);
			root.add(button[5], 8, 6);
			root.add(reset[5], 9, 6);
			
			root.add(status, 1, 7, 4, 1);
			root.setHalignment(status, HPos.RIGHT);
			root.setValignment(status, VPos.CENTER);
			root.add(status_value, 5, 7, 5, 1);
			root.setHalignment(status_value, HPos.LEFT);
			root.setValignment(status_value, VPos.CENTER);
			
			root.add(line, 0, 8, 10, 1);
			
			EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>(){

				@Override
				public void handle(KeyEvent event) {
					// TODO Auto-generated method stub
					Label b = (Label)event.getSource();
					String x = (b).getId();
					int l = Integer.parseInt(x) - 1;
					events[l] = event;
					value[l].setText(event.getCode().toString());
					heading.requestFocus();
				}
				
			};
			
			EventHandler<MouseEvent> mouseEventHandler = new EventHandler<MouseEvent>(){

				@Override
				public void handle(MouseEvent event) {
					// TODO Auto-generated method stub
					String index = ((Button)event.getSource()).getId();
					int in = Integer.parseInt(index)-1;
					if((in-50)<0){
						value[in].setText("Press any key");
						//button[in].addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
						//button[in].setDisable(true);
						value[in].requestFocus();
					} else {
						int ind = in - 50;
						value[ind].setText("");
						//button[ind].removeEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
						//button[ind].setDisable(false);
					}
				}
				
			};
			
			
			for(int i=0;i<6;i++){
				button[i].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
				//value[i].setDisable(true);
				value[i].addEventHandler(KeyEvent.KEY_PRESSED, keyEventHandler);
				reset[i].addEventHandler(MouseEvent.MOUSE_CLICKED, mouseEventHandler);
			}
			
			Scene scene = new Scene(root,600,300);
			scene.getStylesheets().add(getClass().getResource("application.css").toExternalForm());
			primaryStage.setScene(scene);
			primaryStage.setTitle("Remote PC Connection");
			primaryStage.show();
			disconnect.setDisable(true);
			heading.requestFocus();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) {
		launch(args);
	}
	
	public void connectToServer(){
		try {
			connection = new Socket(InetAddress.getByName(serverIP),7553);
			String device = connection.getInetAddress().getHostName();
			System.out.println("Connected to: "+ device);
			/*
			status_value.setText("Connected to " + device);
			status_value.setTextFill(Color.GREEN);
			*/
			//changeStatusText("Connected to :" + device, Color.GREEN);
			connected = true;
			connect.setDisable(true);
			disconnect.setDisable(false);
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Enter Valid IP Address");
			e.printStackTrace();
			changeStatusText("Not Connected", Color.RED);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(null, "Some error occurred");
			e.printStackTrace();
			changeStatusText("Not Connected", Color.RED);
		}
	}
	
	public void setupStreams(){
		if(connected){
			try {
				input = connection.getInputStream();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				JOptionPane.showMessageDialog(null, "Error in setting up streams");
				/*
				status_value.setText("Not Connected");
				status_value.setTextFill(Color.RED);
				*/
				changeStatusText("Not Connected", Color.RED);
				e.printStackTrace();
			}
		}
	}
	
	public void beginChat(){
		while(!message.equals("48") && keeprunning && connected){
			try {
				int x = input.read();
				message = x + "";
				robot = new Robot();
				if(x<55){
					robot.keyPress(events[x-49].getCode().impl_getCode());
					robot.keyRelease(events[x-49].getCode().impl_getCode());
				}else{
				switch(x){
					case 55:robot.keyPress(java.awt.event.KeyEvent.VK_UP);
							robot.keyRelease(java.awt.event.KeyEvent.VK_UP);
							break;
					case 56:robot.keyPress(java.awt.event.KeyEvent.VK_LEFT);
							robot.keyRelease(java.awt.event.KeyEvent.VK_LEFT);
							break;
					case 57:robot.keyPress(java.awt.event.KeyEvent.VK_RIGHT);
							robot.keyRelease(java.awt.event.KeyEvent.VK_RIGHT);
							break;
				}
				}
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
	}
	
	public void closeAll(){
		try{
			input.close();
			connection.close();
			connected = false;
			//connect.setDisable(false);
			//disconnect.setDisable(true);
			/*
			status_value.setText("Not Connected");
			status_value.setTextFill(Color.RED);
			*/
			//changeStatusText("Not Connected", Color.RED);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public void changeStatusText(String str, Color c){
		status_value.setText(str);
		status_value.setTextFill(c);
	}
}
