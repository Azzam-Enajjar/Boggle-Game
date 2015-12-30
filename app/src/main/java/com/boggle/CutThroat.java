package com.boggle;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothServerSocket;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.Set;
import java.util.UUID;


public class CutThroat extends ActionBarActivity {

    public  ArrayList<String> wordlist = new ArrayList<String>();
    public  static ArrayList<String> solution = new ArrayList<String>();
    public  static ArrayList<String> answerServer = new ArrayList<String>();
    public  static ArrayList<String> answerClient = new ArrayList<String>();
    public  String currentAnswer = new String();

    public StringBuilder BuildAnswer = new StringBuilder();
    public int prevx, prevy;
    public boolean isFirstWordPressed ;
    public StringBuilder dictionaryEntry = new StringBuilder();

    private CountDownTimer countDownTimer;
    public TextView textView;
    public StringBuilder buf = new StringBuilder();
    public String[] dictWords;
    public ArrayList<String> dictionary = new ArrayList<String>();
    public boolean[] isPressed = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    public StringBuilder displayListBld = new StringBuilder();
    private final long startTime = 60 * 1000;
    private final long interval = 1 * 1000;
    public int acceptStringsCounter =0;
    public static StringBuilder solutions = new StringBuilder();
    private Set<BluetoothDevice> Devices;
    // public  BluetoothServerSocket mmServerSocket;
    public BluetoothAdapter MBT = BluetoothAdapter.getDefaultAdapter();
    public UUID myUUID = UUID.fromString("00002415-0000-1000-8000-00805F9B34FB");
    public ArrayList list = new ArrayList();
    public final int MESSAGE_READ =1;
    public final int SUCCESS_CONNECT = 0;
    public final int MESSAGE_TIMER = 2;
    private final int SERVER_SUCCESS = 2;
    private static final int MESSAGE_CLIENT_READY = 3;
    public BluetoothSocket writerClientSocket = null;
    public BluetoothSocket writerServerSocket = null;
    public int mode = 0 ;
    public static String[] boggleBoard = {"A","A","A","A","B","B","C","C","D","D","D","E","E","E","E","E","E","F","F","G","G","H","I","I","I","I","J","K","K","L","L","L","M","M","N","N","N","N","O","O","O","O","P","P","Q","R","R","R","R","S","S","S","S","T","T","T","T","U","U","V","V","W","W","X","Y","Y","Z"};
    String boardStringArray;
    public int counter =0;
    public boolean readFlag = false;
    String Answers;
    public static String device = "";
    public boolean isTimer = false;
    public boolean isClientReady = false;
    public StringBuilder strbldr = new StringBuilder();
    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;
    private int shaked = 0;

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){

                case SUCCESS_CONNECT:

                    Toast.makeText(getApplicationContext(),"Connection Established!",Toast.LENGTH_SHORT).show();

                    break;

                case MESSAGE_READ:
                    sendObject reader = (sendObject)msg.obj;


                    String recv = reader.sendBytes;

                    String check1 = "1TimerStart";
                    String check2 = "2ClientReady";
                    //Toast.makeText(getApplicationContext(),recv,Toast.LENGTH_SHORT).show();
                    if(recv.contains("1") == true){
                        //start the timer and populate the buttonnoselectborder at the client
//                        Toast.makeText(getApplicationContext(),"1. Timer Start",Toast.LENGTH_SHORT).show();
                        CutThroat.this.runOnUiThread(new Runnable() {
                            public void run() {
                                GamePlayClient(boardStringArray);
                            }
                        });


                        break;
                    }

                    else if(recv.contains("2") == true){
                        // Toast.makeText(getApplicationContext(),"2. Client Ready",Toast.LENGTH_SHORT).show();


                        ConnectedThread timerSend = new ConnectedThread(reader.sendSocket);
                        timerSend.write("1TimerStart".getBytes());
                        CutThroat.this.runOnUiThread(new Runnable() {
                            public void run() {
                                GamePlayServer(strbldr.toString());
                            }
                        });
                    }

                    else if(recv.contains("3") == true){
                        int start = 1;
                        int end = 1;
                        for(int i=1; i<recv.length();i++)
                        {
                            if(recv.charAt(i)=='3') {
                                end = i;
                                break;
                            }
                        }
                        final String realanswer = recv.substring(start,end);

                        CutThroat.this.runOnUiThread(new Runnable() {
                            public void run() {
                                answerClient.add(realanswer);
                                //Toast.makeText(getApplicationContext(), answerClient.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }

                    else if(recv.contains("4") == true){
                        int start = 1;
                        int end = 1;
                        for(int i=1; i<recv.length();i++)
                        {
                            if(recv.charAt(i)=='4') {
                                end = i;
                                break;
                            }
                        }
                        final String realanswer = recv.substring(start,end);

                        CutThroat.this.runOnUiThread(new Runnable() {
                            public void run() {
                                answerServer.add(realanswer);
                                //Toast.makeText(getApplicationContext(), answerServer.toString(), Toast.LENGTH_SHORT).show();
                            }
                        });

                    }
                    else {
                        //Toast.makeText(getApplicationContext(), recv, Toast.LENGTH_SHORT).show();
                        //  boardStringArray[counter] = recv;
                        counter++;
                        boardStringArray = recv;
                        if (counter == boggleBoard.length) {
                            readFlag = true;
                        }
                    }
                    break;
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_cut_throat);
        device="";

        wordlist = new ArrayList<String>();
        solution = new ArrayList<String>();
        answerServer = new ArrayList<String>();
        answerClient = new ArrayList<String>();
        currentAnswer = "";
        solutions = new StringBuilder();
        shaked = 0;

        final RelativeLayout connectLayer =  (RelativeLayout) findViewById(R.id.RL_Connect);
        final RelativeLayout ctPlayLayer =  (RelativeLayout) findViewById(R.id.RL_ctPlay);
        final RelativeLayout rlShake = (RelativeLayout) findViewById(R.id.RL_Shake);
        connectLayer.setVisibility(View.VISIBLE);
        ctPlayLayer.setVisibility(View.INVISIBLE);
        rlShake.setVisibility(View.INVISIBLE);

        final Button Search = (Button)findViewById(R.id.search_button);
        final Button Play = (Button)findViewById(R.id.Play);
        final ListView BTdevices = (ListView)findViewById(R.id.PairedList);
        final BluetoothAdapter MBT = BluetoothAdapter.getDefaultAdapter();
        final Button Host = (Button)findViewById(R.id.HostBtn);
        final Button CheckScore = (Button)findViewById(R.id.checkScores);
        TextView score = (TextView)findViewById(R.id.score);
        final TextView Shake = (TextView) findViewById(R.id.textViewShake);
        final TextView textView = (TextView)findViewById(R.id.textView);
      //  final Button boggle = (Button) findViewById(R.id.Boggle);
       // boggle.setVisibility(View.INVISIBLE);

        final Button Solver = (Button) findViewById(R.id.SolveButton);
        final TextView Display = (TextView) findViewById(R.id.Entry);
        final Button Submit = (Button)findViewById(R.id.submitButton);
        final Button b1 = (Button) findViewById(R.id.button1);
        final Button b2 = (Button) findViewById(R.id.button2);
        final Button b3 = (Button) findViewById(R.id.button3);
        final Button b4 = (Button) findViewById(R.id.button4);
        final Button b5 = (Button) findViewById(R.id.button5);
        final Button b6 = (Button) findViewById(R.id.button6);
        final Button b7 = (Button) findViewById(R.id.button7);
        final Button b8 = (Button) findViewById(R.id.button8);
        final Button b9 = (Button) findViewById(R.id.button9);
        final Button b10 = (Button) findViewById(R.id.button10);
        final Button b11 = (Button) findViewById(R.id.button11);
        final Button b12 = (Button) findViewById(R.id.button12);
        final Button b13 = (Button) findViewById(R.id.button13);
        final Button b14 = (Button) findViewById(R.id.button14);
        final Button b15 = (Button) findViewById(R.id.button15);
        final Button b16 = (Button) findViewById(R.id.button16);

        b1.setBackgroundResource(R.drawable.buttonnoselectborder);
        b2.setBackgroundResource(R.drawable.buttonnoselectborder);
        b3.setBackgroundResource(R.drawable.buttonnoselectborder);
        b4.setBackgroundResource(R.drawable.buttonnoselectborder);
        b5.setBackgroundResource(R.drawable.buttonnoselectborder);
        b6.setBackgroundResource(R.drawable.buttonnoselectborder);
        b7.setBackgroundResource(R.drawable.buttonnoselectborder);
        b8.setBackgroundResource(R.drawable.buttonnoselectborder);
        b9.setBackgroundResource(R.drawable.buttonnoselectborder);
        b10.setBackgroundResource(R.drawable.buttonnoselectborder);
        b11.setBackgroundResource(R.drawable.buttonnoselectborder);
        b12.setBackgroundResource(R.drawable.buttonnoselectborder);
        b13.setBackgroundResource(R.drawable.buttonnoselectborder);
        b14.setBackgroundResource(R.drawable.buttonnoselectborder);
        b15.setBackgroundResource(R.drawable.buttonnoselectborder);
        b16.setBackgroundResource(R.drawable.buttonnoselectborder);
        Search.setBackgroundResource(R.drawable.join);
        Host.setBackgroundResource(R.drawable.host);
        Play.setBackgroundResource(R.drawable.play);



        InputStream in = getResources().openRawResource(getResources().getIdentifier("words", "raw", getPackageName()));
        if (in != null) {
            InputStreamReader tmp = new InputStreamReader(in);
            BufferedReader reader = new BufferedReader(tmp);
            String str;

            try {
                while ((str = reader.readLine()) != null) {
                    buf.append(str.toUpperCase() + " ");

                }
            } catch (IOException e) {

            }
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dictWords = buf.toString().split(" ");

        }

        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if(shaked == 0) {
                    // Toast.makeText(getApplicationContext(), "In onshake Event", Toast.LENGTH_SHORT).show();
                    shaked = 1;
                    connectLayer.setVisibility(View.INVISIBLE);
                    ctPlayLayer.setVisibility(View.VISIBLE);
                    rlShake.setVisibility(View.INVISIBLE);
                    if(mode == 0) {//server
                        Boggle();
                        int i=0;
                        ConnectedThread connected = new ConnectedThread(writerServerSocket);
                        for(i = 0; i<boggleBoard.length; i++) {
                            strbldr.append(boggleBoard[i] + " ");
                        }
                        connected.write(strbldr.toString().getBytes());



                    }
                    else {

                        ConnectedThread clientready = new ConnectedThread(writerClientSocket);
                        clientready.write("2ClientReady".getBytes());
                    }
                }

            }
        });

        CheckScore.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ConnectedThread clientScore = new ConnectedThread(writerClientSocket);
                clientScore.write(Answers.getBytes());
            }
        });

        Search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Search.setBackgroundResource(R.drawable.joinselect);
                ////////////
                if(!MBT.isEnabled()){
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn,0);
                    //  Toast.makeText(getApplicationContext(), "Turning Bluetooth on.", Toast.LENGTH_LONG).show();
                }
                /////////////
                mode =1;
                Devices = MBT.getBondedDevices();

                for(BluetoothDevice bt : Devices){
                    // if(bt.getName().equals("Galaxy")){
                    //   ConnectThread connect = new ConnectThread(bt);
                    // connect.start();
                    //}

                    list.add(bt.getName());}
                Toast.makeText(getApplicationContext(),"Showing Paired Devices to Connect to..", Toast.LENGTH_SHORT).show();
                final ArrayAdapter adapter = new ArrayAdapter(CutThroat.this,android.R.layout.simple_list_item_1, list);
                BTdevices.setAdapter(adapter);

            }
        });

        BTdevices.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                device = BTdevices.getItemAtPosition(position).toString();
                Devices = MBT.getBondedDevices();
                for(BluetoothDevice bt : Devices){
                    if(bt.getName().equals(device)){
                        ConnectThread connect = new ConnectThread(bt);
                        connect.start();
                    }
                }
            }
        });

        Host.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Host.setBackgroundResource(R.drawable.hostselect);
                ////////////////////////
                if(!MBT.isEnabled()){
                    Intent turnOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                    startActivityForResult(turnOn,0);
                    // Toast.makeText(getApplicationContext(), "Turning Bluetooth on.", Toast.LENGTH_LONG).show();
                }
                /////////////

                mode = 0;
                AcceptThread accept = new AcceptThread();
                accept.start();

            }
        });

        Play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Play.setBackgroundResource(R.drawable.playselect);
                connectLayer.setVisibility(View.INVISIBLE);
                ctPlayLayer.setVisibility(View.INVISIBLE);
                rlShake.setVisibility(View.VISIBLE);

            }

        });

        Submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(),"SubmitPressed",Toast.LENGTH_SHORT).show();

                boolean dictReturn;
                prevx = 0;
                prevy = 0;
                isFirstWordPressed = false;

                b1.setBackgroundResource(R.drawable.buttonnoselectborder);
                b2.setBackgroundResource(R.drawable.buttonnoselectborder);
                b3.setBackgroundResource(R.drawable.buttonnoselectborder);
                b4.setBackgroundResource(R.drawable.buttonnoselectborder);
                b5.setBackgroundResource(R.drawable.buttonnoselectborder);
                b6.setBackgroundResource(R.drawable.buttonnoselectborder);
                b7.setBackgroundResource(R.drawable.buttonnoselectborder);
                b8.setBackgroundResource(R.drawable.buttonnoselectborder);
                b9.setBackgroundResource(R.drawable.buttonnoselectborder);
                b10.setBackgroundResource(R.drawable.buttonnoselectborder);
                b11.setBackgroundResource(R.drawable.buttonnoselectborder);
                b12.setBackgroundResource(R.drawable.buttonnoselectborder);
                b13.setBackgroundResource(R.drawable.buttonnoselectborder);
                b14.setBackgroundResource(R.drawable.buttonnoselectborder);
                b15.setBackgroundResource(R.drawable.buttonnoselectborder);
                b16.setBackgroundResource(R.drawable.buttonnoselectborder);

                if(mode == 0)//server
                {
                    if (BuildAnswer.length() > 2) {
                        dictionaryEntry.append(BuildAnswer);
                        dictReturn = checkPresent();
                        if (dictReturn) {
                            if(answerServer.contains(BuildAnswer.toString()) == false) {//check server's own answer
                                if(answerClient.contains(BuildAnswer.toString()) == false)//check client's answer
                                {
                                    //if answer is not in client answer nor server answer
                                    //send BuildAnswer to server
                                    String cans = "4"+BuildAnswer.toString()+"4";
                                    ConnectedThread sendAnswerstoClient = new ConnectedThread(writerServerSocket);
                                    sendAnswerstoClient.write(cans.getBytes());


                                    displayListBld.append(dictionaryEntry.toString() + System.getProperty("line.separator"));
                                    Display.setText(displayListBld.toString());
                                    answerServer.add(BuildAnswer.toString());
                                }
                                /*else
                                {
                                    Toast.makeText(getApplicationContext(), "Your opponent have already submitted this word", Toast.LENGTH_SHORT).show();
                                    Display.setText(displayListBld.toString());
                                }*/
                            }
                            else{
                                Toast.makeText(getApplicationContext(), "You have already submitted this word", Toast.LENGTH_SHORT).show();
                                Display.setText(displayListBld.toString());
                            }

                        }
                        else{
                            Display.setText(displayListBld.toString());
                            currentAnswer = "";
                        }

                    }
                    else {
                        Toast.makeText(getApplicationContext(), "Word must be at least 3 characters long!", Toast.LENGTH_SHORT).show();
                        Display.setText(displayListBld.toString());
                        currentAnswer = "";
                    }
                    BuildAnswer.setLength(0);
                    dictionaryEntry.setLength(0);
                    for (int i =0; i<16; i++){
                        isPressed[i]=false;
                    }
                }

                else {//client
                    if (BuildAnswer.length() > 2) {
                        dictionaryEntry.append(BuildAnswer);
                        dictReturn = checkPresent();
                        if (dictReturn) {
                            if (answerClient.contains(BuildAnswer.toString()) == false)
                            {
                                if(answerServer.contains(BuildAnswer.toString()) == false)
                                {
                                    //if answer is not in client answer nor server answer
                                    //send BuildAnswer to server
                                    String cans = "3"+BuildAnswer.toString()+"3";
                                    ConnectedThread sendAnswerstoServer = new ConnectedThread(writerClientSocket);
                                    sendAnswerstoServer.write(cans.getBytes());

                                    displayListBld.append(dictionaryEntry.toString() + System.getProperty("line.separator"));
                                    Display.setText(displayListBld.toString());
                                    answerClient.add(BuildAnswer.toString());
                                }
                                /*else
                                {
                                    Toast.makeText(getApplicationContext(), "Your opponent have already submitted this word", Toast.LENGTH_SHORT).show();
                                    Display.setText(displayListBld.toString());
                                }*/
                            } else {
                                Toast.makeText(getApplicationContext(), "You have already submitted this word", Toast.LENGTH_SHORT).show();
                                Display.setText(displayListBld.toString());
                            }
                        } else {
                            Display.setText(displayListBld.toString());
                            currentAnswer = "";
                        }

                    } else {
                        Toast.makeText(getApplicationContext(), "Word must be at least 3 characters long!", Toast.LENGTH_SHORT).show();
                        Display.setText(displayListBld.toString());
                        currentAnswer = "";
                    }
                    BuildAnswer.setLength(0);
                    dictionaryEntry.setLength(0);
                    for (int i = 0; i < 16; i++) {
                        isPressed[i] = false;
                    }

                  /*  if(currentAnswer.length()>= 3) {
                    answer.add(currentAnswer);
                    currentAnswer = "";
                }
                else{
                    currentAnswer = "";
                    Toast.makeText(getApplicationContext(), "Words needs to be 3-letter long min.", Toast.LENGTH_SHORT).show();
                }*/
                }

            }
        });

    }

    private boolean checkPresent(){
        boolean isPresent;
        try {
            isPresent = isInDictionary();
            if(isPresent){
                /*acceptedStrings[acceptStringsCounter]=BuildAnswer.toString();
                acceptStringsCounter++;
                    */
                //BuildAnswer.setLength(0);
                //Toast.makeText(getApplicationContext(),"Bravo!!",Toast.LENGTH_SHORT).show();
                return true;
            }
            else{
                Toast.makeText(getApplicationContext(),"Word does not exist",Toast.LENGTH_SHORT).show();

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        // Entry.setText(BuildAnswer);
        return false;
    }

    private boolean isInDictionary() throws IOException {
        int leng = dictWords.length;
        int i = 0, j=0;
        int temp;
        while(leng != 0) {
            if(dictionaryEntry.toString().compareTo(dictWords[i])== 0){
                return true;
            }
            i++;
            leng--;
        }
        /*AlertDialog alertDialog1 = new AlertDialog.Builder(Game.this).create();
        alertDialog1.setMessage("the length now is " + leng);
        alertDialog1.show();*/
        return false;
    }



    private void GamePlayServer(String boggleString){
        textView = (TextView)findViewById(R.id.textView);
        countDownTimer = new CountDownTimerActivity(startTime, interval);
        textView.setText(textView.getText() + String.valueOf(startTime/1000));
        countDownTimer.start();

        final TextView Shake = (TextView) findViewById(R.id.textViewShake);
        //final TextView text = (TextView)findViewById(R.id.test);
        final Button boggle = (Button) findViewById(R.id.Boggle);
        final Button Solver = (Button) findViewById(R.id.SolveButton);
        final TextView Display = (TextView) findViewById(R.id.Entry);
        final Button Submit = (Button)findViewById(R.id.submitButton);
        final Button b1 = (Button) findViewById(R.id.button1);
        final Button b2 = (Button) findViewById(R.id.button2);
        final Button b3 = (Button) findViewById(R.id.button3);
        final Button b4 = (Button) findViewById(R.id.button4);
        final Button b5 = (Button) findViewById(R.id.button5);
        final Button b6 = (Button) findViewById(R.id.button6);
        final Button b7 = (Button) findViewById(R.id.button7);
        final Button b8 = (Button) findViewById(R.id.button8);
        final Button b9 = (Button) findViewById(R.id.button9);
        final Button b10 = (Button) findViewById(R.id.button10);
        final Button b11 = (Button) findViewById(R.id.button11);
        final Button b12 = (Button) findViewById(R.id.button12);
        final Button b13 = (Button) findViewById(R.id.button13);
        final Button b14 = (Button) findViewById(R.id.button14);
        final Button b15 = (Button) findViewById(R.id.button15);
        final Button b16 = (Button) findViewById(R.id.button16);
        boggleBoard = boggleString.split(" ");
        b1.setText(boggleBoard[0]);
        b2.setText(boggleBoard[1]);
        b3.setText(boggleBoard[2]);
        b4.setText(boggleBoard[3]);
        b5.setText(boggleBoard[4]);
        b6.setText(boggleBoard[5]);
        b7.setText("E");//(boggleBoard[6]);
        b8.setText(boggleBoard[7]);
        b9.setText(boggleBoard[8]);
        b10.setText(boggleBoard[9]);
        b11.setText(boggleBoard[10]);
        b12.setText(boggleBoard[11]);
        b13.setText(boggleBoard[13]);
        b14.setText(boggleBoard[14]);
        b15.setText(boggleBoard[15]);
        b16.setText(boggleBoard[16]);


        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(),"b1 clicked! host!",Toast.LENGTH_SHORT).show();
                if(isPressed[0] == false) {
                    boolean adjacent = true;
                    //boolean dictReturn;
                    String str = b1.getText().toString();
                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 0;
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer.toString());
                        // b1.setBackgroundColor(Color.WHITE);
                        b1.setBackgroundResource(R.drawable.buttonselect);
                        isFirstWordPressed = true;
                        isPressed[0]=true;
                        currentAnswer += b1.getText().toString();
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    /*AlertDialog alertDialog1 = new AlertDialog.Builder(Game.this).create();
                    alertDialog1.setMessage("Can't use the same tile again!");
                    alertDialog1.show();*/
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[1]== false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b2.getText().toString();


                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 1;
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer.toString());

                        //b2.setBackgroundColor(Color.WHITE);
                        b2.setBackgroundResource(R.drawable.buttonselect);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[1]=true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPressed[2] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b3.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 2;
                        //b3.setBackgroundColor(Color.WHITE);
                        b3.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[2]=true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[3]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b4.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 3;
                        //b4.setBackgroundColor(Color.WHITE);
                        b4.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[3] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }

            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[4] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b5.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 0;
                        //b5.setBackgroundColor(Color.WHITE);
                        b5.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[4] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }

            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[5] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b6.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 1;
                        //b6.setBackgroundColor(Color.WHITE);
                        b6.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[5] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[6]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b7.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 2;
                        // b7.setBackgroundColor(Color.WHITE);
                        b7.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[6] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    //Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[7]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b8.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 3;
                        //b8.setBackgroundColor(Color.WHITE);
                        b8.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[7] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[8]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b9.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 2;
                        prevy = 0;
                        //b9.setBackgroundColor(Color.WHITE);
                        b9.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[8] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                    isFirstWordPressed = true;
                    isPressed[8] = true;
                }
                else {
                    //Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[9]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b10.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        //b10.setBackgroundColor(Color.WHITE);
                        b10.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 1;
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[9] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[10]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b11.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        //b11.setBackgroundColor(Color.WHITE);
                        b11.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 2;
                        isFirstWordPressed = true;
                        isPressed[10] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[11]== false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b12.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        //b12.setBackgroundColor(Color.WHITE);
                        b12.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 3;
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[11] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[12]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b13.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 0;
                        //b13.setBackgroundColor(Color.WHITE);
                        b13.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[12] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[13]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b14.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 1;
                        // b14.setBackgroundColor(Color.WHITE);
                        b14.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[13] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[14]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b15.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 2;
                        //b15.setBackgroundColor(Color.WHITE);
                        b15.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[14] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[15]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b16.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 3;
                        //b16.setBackgroundColor(Color.WHITE);
                        b16.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[15] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    public class CountDownTimerActivity extends CountDownTimer {
        public CountDownTimerActivity(long startTime, long interval) {
            super(startTime, interval);
        }
        @Override
        public void onFinish() {
            textView.setText("Time's up!");
            Intent i = new Intent(CutThroat.this, MultiplayerScore.class);
            i.putExtra("ClientAns",answerClient);
            i.putExtra("ServerAns", answerServer);
            i.putExtra("WhoAreYou", mode);
            startActivity(i);

/*          new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(CutThroat.this, scoreBoard.class);
                    startActivity(i);

                    finish();
                }
            }, 0);*/
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textView.setText("" + millisUntilFinished/1000);
        }
    }



    private void GamePlayClient(String str){
        textView = (TextView)findViewById(R.id.textView);
        countDownTimer = new CountDownTimerActivity(startTime, interval);
        textView.setText(textView.getText() + String.valueOf(startTime/1000));
        countDownTimer.start();

        final TextView Shake = (TextView) findViewById(R.id.textViewShake);
        //final TextView text = (TextView)findViewById(R.id.test);
        final Button boggle = (Button) findViewById(R.id.Boggle);
        final Button Solver = (Button) findViewById(R.id.SolveButton);
        final TextView Display = (TextView) findViewById(R.id.Entry);
        final Button Submit = (Button)findViewById(R.id.submitButton);
        final Button b1 = (Button) findViewById(R.id.button1);
        final Button b2 = (Button) findViewById(R.id.button2);
        final Button b3 = (Button) findViewById(R.id.button3);
        final Button b4 = (Button) findViewById(R.id.button4);
        final Button b5 = (Button) findViewById(R.id.button5);
        final Button b6 = (Button) findViewById(R.id.button6);
        final Button b7 = (Button) findViewById(R.id.button7);
        final Button b8 = (Button) findViewById(R.id.button8);
        final Button b9 = (Button) findViewById(R.id.button9);
        final Button b10 = (Button) findViewById(R.id.button10);
        final Button b11 = (Button) findViewById(R.id.button11);
        final Button b12 = (Button) findViewById(R.id.button12);
        final Button b13 = (Button) findViewById(R.id.button13);
        final Button b14 = (Button) findViewById(R.id.button14);
        final Button b15 = (Button) findViewById(R.id.button15);
        final Button b16 = (Button) findViewById(R.id.button16);
        boggleBoard = str.split(" ");
        b1.setText(boggleBoard[0]);
        b2.setText(boggleBoard[1]);
        b3.setText(boggleBoard[2]);
        b4.setText(boggleBoard[3]);
        b5.setText(boggleBoard[4]);
        b6.setText(boggleBoard[5]);
        b7.setText("E");//(boggleBoard[6]);
        b8.setText(boggleBoard[7]);
        b9.setText(boggleBoard[8]);
        b10.setText(boggleBoard[9]);
        b11.setText(boggleBoard[10]);
        b12.setText(boggleBoard[11]);
        b13.setText(boggleBoard[13]);
        b14.setText(boggleBoard[14]);
        b15.setText(boggleBoard[15]);
        b16.setText(boggleBoard[16]);



        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(),"b1 clicked! guest!",Toast.LENGTH_SHORT).show();
                if(isPressed[0] == false) {
                    boolean adjacent = true;
                    //boolean dictReturn;
                    String str = b1.getText().toString();
                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 0;
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer.toString());
                        // b1.setBackgroundColor(Color.WHITE);
                        b1.setBackgroundResource(R.drawable.buttonselect);
                        isFirstWordPressed = true;
                        isPressed[0]=true;
                        currentAnswer += b1.getText().toString();
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else{
                    /*AlertDialog alertDialog1 = new AlertDialog.Builder(Game.this).create();
                    alertDialog1.setMessage("Can't use the same tile again!");
                    alertDialog1.show();*/
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[1]== false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b2.getText().toString();


                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 1;
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer.toString());

                        //b2.setBackgroundColor(Color.WHITE);
                        b2.setBackgroundResource(R.drawable.buttonselect);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[1]=true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPressed[2] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b3.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 2;
                        //b3.setBackgroundColor(Color.WHITE);
                        b3.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[2]=true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });

        b4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[3]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b4.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(0, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 0;
                        prevy = 3;
                        //b4.setBackgroundColor(Color.WHITE);
                        b4.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[3] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }

            }
        });
        b5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[4] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b5.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 0;
                        //b5.setBackgroundColor(Color.WHITE);
                        b5.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[4] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }

            }
        });
        b6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[5] == false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b6.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 1;
                        //b6.setBackgroundColor(Color.WHITE);
                        b6.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[5] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[6]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b7.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 2;
                        // b7.setBackgroundColor(Color.WHITE);
                        b7.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[6] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    //Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[7]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b8.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(1, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 3;
                        //b8.setBackgroundColor(Color.WHITE);
                        b8.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[7] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[8]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b9.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 2;
                        prevy = 0;
                        //b9.setBackgroundColor(Color.WHITE);
                        b9.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[8] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                    isFirstWordPressed = true;
                    isPressed[8] = true;
                }
                else {
                    //Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[9]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b10.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        //b10.setBackgroundColor(Color.WHITE);
                        b10.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 1;
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[9] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b11.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[10]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b11.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        //b11.setBackgroundColor(Color.WHITE);
                        b11.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 2;
                        isFirstWordPressed = true;
                        isPressed[10] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }
                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b12.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[11]== false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b12.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(2, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        //b12.setBackgroundColor(Color.WHITE);
                        b12.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        prevx = 2;
                        prevy = 3;
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[11] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b13.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[12]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b13.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 0;
                        //b13.setBackgroundColor(Color.WHITE);
                        b13.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[12] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b14.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[13]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b14.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 1;
                        // b14.setBackgroundColor(Color.WHITE);
                        b14.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[13] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b15.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[14]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b15.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 2;
                        //b15.setBackgroundColor(Color.WHITE);
                        b15.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[14] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });
        b16.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isPressed[15]==false) {
                    boolean adjacent = true;
                    boolean dictReturn;
                    String str = b16.getText().toString();

                    if (isFirstWordPressed) {
                        adjacent = AdjacentorNot(3, 3, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 3;
                        prevy = 3;
                        //b16.setBackgroundColor(Color.WHITE);
                        b16.setBackgroundResource(R.drawable.buttonselect);
                        BuildAnswer.append(str);
                        Display.setText(BuildAnswer);
                        currentAnswer += b1.getText().toString();
                        isFirstWordPressed = true;
                        isPressed[15] = true;
                    } else {
                        //Toast.makeText(getApplicationContext(), "That's not adjacent man!", Toast.LENGTH_SHORT).show();
                    }

                }
                else {
                    Toast.makeText(getApplicationContext(), "Can't use the same tile again", Toast.LENGTH_SHORT).show();
                }
            }
        });




    }

    private void Boggle(){

        int index;
        String temp;
        Random random = new Random();
        for (int i = boggleBoard.length - 1; i > 0; i--)
        {
            index = random.nextInt(i + 1);
            temp = boggleBoard[index];
            boggleBoard[index] = boggleBoard[i];
            boggleBoard[i] = temp;
        }
    }


    private boolean AdjacentorNot(int x, int y, int x1, int y1){

        if(x1==x-1 && y1==y-1)
            return true;
        else if(x1==x && y1==y-1)
            return true;
        else if(x1==x+1 && y1==y-1)
            return true;
        else if(x1==x-1 && y1==y)
            return true;
        else if(x1==x+1 && y1==y)
            return true;
        else if(x1==x-1 && y1==y+1)
            return true;
        else if(x1==x && y1==y+1)
            return true;
        else if(x1==x+1 && y1==y+1)
            return true;
        else
            return false;
    }


    private void sendClientToMain(BluetoothSocket mmSocket) {
        writerClientSocket = mmSocket;
    }

    private void sendServerToMain(BluetoothSocket socket) {
        writerServerSocket = socket;
    }


    public void manageConnectedSocket(final BluetoothSocket socket) {
        ConnectedThread boss = new ConnectedThread(socket);
        boss.start();

    }

    private class AcceptThread extends Thread {
        private final BluetoothServerSocket mmServerSocket;
        BluetoothSocket socket = null;
        UUID myUUID = UUID.fromString("00002415-0000-1000-8000-00805F9B34FB");
        public AcceptThread() {
            // Use a temporary object that is later assigned to mmServerSocket,
            // because mmServerSocket is final
            BluetoothServerSocket tmp = null;
            try {
                // MY_UUID is the app's UUID string, also used by the client code
                tmp = MBT.listenUsingRfcommWithServiceRecord("Multiplayer", myUUID);
            } catch (IOException e) { }
            mmServerSocket = tmp;
        }

        public void run() {
            //BluetoothSocket socket = null;
            // Keep listening until exception occurs or a socket is returned
            while (true) {
                try {
                    socket= mmServerSocket.accept();
                } catch (IOException e) {
                    break;
                }
                // If a connection was accepted
                if (socket != null) {
                    // Do work to manage the connection (in a separate thread)
                    CutThroat.this.runOnUiThread(new Runnable() {
                        public void run() {

                            Toast.makeText(getApplication(), "Connection Established", Toast.LENGTH_SHORT).show();
                        }
                    });
                    sendServerToMain(socket);
                    manageConnectedSocket(socket);
                    mHandler.obtainMessage(SERVER_SUCCESS,socket).sendToTarget();
                    try {
                        mmServerSocket.close();

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    break;
                }
            }
        }

        /** Will cancel the listening socket, and cause the thread to finish */
        public void cancel() {
            try {
                mmServerSocket.close();
            } catch (IOException e) { }
        }
    }
    @Override
    protected void onResume() {
        super.onResume();
        mSensorManager.registerListener(mShakeDetector, mAccelerometer, SensorManager.SENSOR_DELAY_UI);
    }

    @Override
    protected void onPause() {
        mSensorManager.unregisterListener(mShakeDetector);
        super.onPause();
    }
    private class ConnectThread extends Thread {
        private final BluetoothSocket mmSocket;
        private final BluetoothDevice mmDevice;

        public ConnectThread(BluetoothDevice device) {
            // Use a temporary object that is later assigned to mmSocket,
            // because mmSocket is final
            BluetoothSocket tmp = null;
            mmDevice = device;

            // Get a BluetoothSocket to connect with the given BluetoothDevice
            try {
                // MyUUID is the app's UUID string, also used by the server code
                tmp = device.createRfcommSocketToServiceRecord(myUUID);
            } catch (IOException e) { }
            mmSocket = tmp;
        }

        public void run() {
            // Cancel discovery because it will slow down the connection
            MBT.cancelDiscovery();

            try {
                // Connect the device through the socket. This will block
                // until it succeeds or throws an exception
                mmSocket.connect();
                // ClientSocket = mmSocket;

            } catch (IOException connectException) {
                // Unable to connect; close the socket and get out
                try {
                    mmSocket.close();
                } catch (IOException closeException) { }
                return;
            }

            // Do work to manage the connection (in a separate thread)
            sendClientToMain(mmSocket);
            manageConnectedSocket(mmSocket);
            mHandler.obtainMessage(SUCCESS_CONNECT,mmSocket).sendToTarget();
        }

        /** Will cancel an in-progress connection, and close the socket */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }



    public class sendObject{

        String sendBytes;
        BluetoothSocket sendSocket;
        sendObject(String byteSrc, BluetoothSocket skcSrc)
        {
            sendBytes= byteSrc;
            sendSocket = skcSrc;
        }

    }
    public byte[] serialize() throws IOException {
        ByteArrayOutputStream b = new ByteArrayOutputStream();
        ObjectOutputStream o = new ObjectOutputStream(b);
        o.writeObject(this);
        return b.toByteArray();
    }

    class ConnectedThread extends Thread {//never stops....running in background at all times
        private final BluetoothSocket mmSocket;
        private final InputStream mmInStream;
        private final OutputStream mmOutStream;
        public ConnectedThread(BluetoothSocket socket) {
            mmSocket = socket;
            InputStream tmpIn = null;
            OutputStream tmpOut = null;

            // Get the input and output streams, using temp objects because
            // member streams are final
            try {
                tmpIn = socket.getInputStream();
                tmpOut = socket.getOutputStream();
            } catch (IOException e) { }

            mmInStream = tmpIn;
            mmOutStream = tmpOut;
        }

        public void run() {
            final byte[] buffer = new byte[1024];  // buffer store for the stream
            int bytes; // bytes returned from read()

            // Keep listening to the InputStream until an exception occurs
            while (true) {
                try {
                    // Read from the InputStream
                    bytes = mmInStream.read(buffer);
                    String bufferStr = new String(buffer);
                    // Send the obtained bytes to the UI activity
                    sendObject temp = new sendObject(bufferStr,mmSocket);
                    mHandler.obtainMessage(MESSAGE_READ, bytes, -1, temp).sendToTarget();


                } catch (IOException e) {
                    break;
                }
            }
        }

        /* Call this from the main activity to send data to the remote device */
        public void write(byte[] MESSAGE_WRITE) {
            try {
                mmOutStream.write(MESSAGE_WRITE);
            } catch (IOException e) { }
        }

        /* Call this from the main activity to shutdown the connection */
        public void cancel() {
            try {
                mmSocket.close();
            } catch (IOException e) { }
        }
    }

}
