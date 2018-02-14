package com.major.kc.rpc;

import android.graphics.Color;      //Used to set color of text
import android.graphics.Typeface;   //Used to set Style of the text (BOLD)
import android.net.wifi.WifiInfo;
import android.support.v7.app.AppCompatActivity;        // Required to run Android application
import android.os.Bundle;                               // Required to run Android application
import android.view.View;       //Sets the view visible to the user
import android.os.Handler;      //Responsible for controlling the view
import android.view.WindowManager;
import android.widget.Button;           //Used to create Button
import android.widget.CompoundButton;   //Used for creating Switch
import android.widget.Switch;           //Used to create a switch/toggle button
import android.widget.TextView;         //Used to create a label
import android.hardware.Sensor;                 //Used to use accelerometer sensor
import android.hardware.SensorEvent;            //Used to tackle accelerometer events
import android.hardware.SensorEventListener;    //Used to check for accelerometer events
import android.hardware.SensorManager;          //Manages sensor events
import android.content.Context;         //Used to use vibrator service
import android.os.Vibrator;             //Handles phone vibrations
import java.io.OutputStream;            //Used to set up stream to send data
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;           //Used to create a server and enabling connections
import java.net.Socket;                 //Used to make connection to the client
import java.util.Enumeration;
import java.util.Locale;
import java.io.InputStream;             //Used to set up stream to receive data
import android.net.wifi.WifiManager;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SensorEventListener, View.OnClickListener {

    private Button b1,b2,b3,b4,b5,b6;
    private TextView tv1,tv2,tv3,tv4,tv5,tv6;
    private SensorManager sensorManager;
    private Vibrator v;
    private Switch s1;

    private boolean acc, disconnected;

    private OutputStream outputStream;
    private InputStream inputStream;

    private ServerSocket serverSocket;
    private Handler handler;

    WifiManager wm;
    String ip, in;

    private static final int serverPort = 7553;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        b1 = (Button)findViewById(R.id.button1);
        b1.setOnClickListener(this);
        b2 = (Button)findViewById(R.id.button2);
        b2.setOnClickListener(this);
        b3 = (Button)findViewById(R.id.button3);
        b3.setOnClickListener(this);
        b4 = (Button)findViewById(R.id.button4);
        b4.setOnClickListener(this);
        b5 = (Button)findViewById(R.id.button5);
        b5.setOnClickListener(this);
        b6 = (Button)findViewById(R.id.button6);
        b6.setOnClickListener(this);
        tv1 = (TextView)findViewById(R.id.textView1);
        tv2 = (TextView)findViewById(R.id.textView2);
        tv3 = (TextView)findViewById(R.id.textView3);
        tv4 = (TextView)findViewById(R.id.textView4);
        tv5 = (TextView)findViewById(R.id.textView5);
        tv6 = (TextView)findViewById(R.id.textView6);
        v = (Vibrator)this.getSystemService(Context.VIBRATOR_SERVICE);
        s1 = (Switch)findViewById(R.id.switch1);
        s1.setChecked(true);
        s1.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    acc = true;
                }else{
                    acc = false;
                    tv4.setText("");
                    tv5.setText("");
                }
            }
        });

        sensorManager.registerListener(this,sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER),SensorManager.SENSOR_DELAY_GAME);

        handler = new Handler();
        acc = true;
        disconnected = true;
        wm = (WifiManager)getSystemService(WIFI_SERVICE);

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

   public void onSensorChanged(SensorEvent event){
       if(event.sensor.getType() == Sensor.TYPE_ACCELEROMETER){
           getAccelerometer(event);
       }
   }

    private void getAccelerometer(SensorEvent event){
        float[] values = event.values;
        float x = values[0];
        float y = values[1];
        float z = values[2];
        tv1.setText("X: "+x);
        tv2.setText("Y: "+y);
        tv3.setText("Z: "+z);

        if(acc && outputStream!=null) {
            if (z > 8.9) {
                tv5.setText("Please lift your phone");
                tv5.setTextColor(Color.RED);
                tv5.setTextSize(25);
                tv5.setTypeface(null, Typeface.BOLD);
            } else {
                tv5.setText("");
                try {

                        if (z <= 8.9 && z > 8) {
                            //outputStream.write("7".getBytes());
                            if (y < -2) {
                                tv4.setText("Left Rotation");
                                outputStream.write("8".getBytes());
                            } else if (y > 2) {
                                tv4.setText("Right Rotation");
                                outputStream.write("9".getBytes());
                            } else {
                                tv4.setText("");
                            }
                        } else if (z <= 8 && z > 7) {
                            //outputStream.write("7".getBytes());
                            if (y < -3) {
                                tv4.setText("Left Rotation");
                                outputStream.write("8".getBytes());
                            } else if (y > 3) {
                                tv4.setText("Right Rotation");
                                outputStream.write("9".getBytes());
                            } else {
                                tv4.setText("");
                            }
                        } else if (z <= 7 && z > 6) {
                            if (y < -4) {
                                tv4.setText("Left Rotation");
                                outputStream.write("8".getBytes());
                            } else if (y > 4) {
                                tv4.setText("Right Rotation");
                                outputStream.write("9".getBytes());
                            } else {
                                tv4.setText("");
                            }
                        } else {
                            if (y < -5) {
                                tv4.setText("Left Rotation");
                                outputStream.write("8".getBytes());
                            } else if (y > 5) {
                                tv4.setText("Right Rotation");
                                outputStream.write("9".getBytes());
                            } else {
                                tv4.setText("");
                            }
                        }

                } catch(Exception e){
                    e.printStackTrace();
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        this.v.vibrate(50);
        if(outputStream != null) {
            try {
                switch (v.getId()) {
                    case R.id.button1:
                        outputStream.write("1".getBytes());
                        break;
                    case R.id.button2:
                        outputStream.write("2".getBytes());
                        break;
                    case R.id.button3:
                        outputStream.write("3".getBytes());
                        break;
                    case R.id.button4:
                        outputStream.write("4".getBytes());
                        break;
                    case R.id.button5:
                        outputStream.write("5".getBytes());
                        break;
                    case R.id.button6:
                        outputStream.write("6".getBytes());
                        break;
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public String getWifiIp() {
        try{
            for(Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();){
                NetworkInterface intf = en.nextElement();
                if(intf.getName().contains("wlan")){
                    for(Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();){
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        if(!inetAddress.isLoopbackAddress() && (inetAddress.getAddress().length == 4)){
                            return inetAddress.getHostAddress();
                        }
                    }
                }
            }
        } catch(Exception e){
            e.printStackTrace();
        }
        return "192.168.43.1";
    }

    private String getIP() {
        try {
            WifiManager wifiManager = (WifiManager) getSystemService(WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            int ipAddress = wifiInfo.getIpAddress();
            return String.format(Locale.getDefault(), "%d.%d.%d.%d",
                    (ipAddress & 0xff), (ipAddress >> 8 & 0xff),
                    (ipAddress >> 16 & 0xff), (ipAddress >> 24 & 0xff));
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    protected void onDestroy() {
        super.onDestroy();
        closeAll();
    }

    protected void onStart() {
        super.onStart();
        Thread fst = new Thread(new process());
        fst.start();

    }

    public void closeAll(){
        try {
            if(!serverSocket.isClosed()){
                serverSocket.close();
            }
        }catch(Exception e){
            e.printStackTrace();
        }
    }

    public class process implements Runnable {
        public void run() {
            try{
                    handler.post(new Runnable() {
                        public void run() {
                            tv6.setText("Creating ServerSocket");
                        }
                    });
                    if(serverSocket==null) {
                        serverSocket = new ServerSocket(serverPort);
                    }
                    handler.post(new Runnable() {
                        public void run() {
                            tv6.setText("ServerSocket Created");
                        }
                    });

                ip = getWifiIp();

                handler.post(new Runnable() {
                    public void run() {
                        tv6.setText("Server IP: " + ip);
                    }
                });

                    while(true) {
                        Socket client = serverSocket.accept();
                        disconnected = false;
                        handler.post(new Runnable() {
                            public void run() {
                                tv6.setText("CONNECTED");
                            }
                        });
                        try {
                            outputStream = client.getOutputStream();
                            inputStream = client.getInputStream();
                            handler.post(new Runnable() {
                                public void run() {
                                    tv6.setText("CONNECTED! GO!");
                                }
                            });

                                try{
                                    int x = inputStream.read();
                                    in = x + "";

                                    if(in.equals("100")){
                                        disconnected = true;
                                        handler.post(new Runnable() {
                                            public void run() {
                                                Toast.makeText(getApplicationContext(),"Disconnected", Toast.LENGTH_LONG).show();
                                                tv6.setText("Server IP: " + ip);
                                            }
                                        });
                                        inputStream.close();
                                        outputStream.close();
                                        client.close();
                                    }
                                }catch(Exception e){
                                    e.printStackTrace();
                                }



                        } catch (Exception e) {
                            handler.post(new Runnable() {
                                public void run() {
                                    tv6.setText("ERROR IN SETTING STREAMS");
                                }
                            });
                            e.printStackTrace();
                        }

                    }
            } catch(Exception e){
                e.printStackTrace();
            }
        }
    }

}
