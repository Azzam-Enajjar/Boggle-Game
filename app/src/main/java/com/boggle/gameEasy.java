package com.boggle;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.text.method.ScrollingMovementMethod;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Random;


public class gameEasy extends Activity {
    public ArrayList<String> dic = new ArrayList<String>();
    public  ArrayList<String> wordlist = new ArrayList<String>();
    public  static ArrayList<String> solution = new ArrayList<String>();
    public  static ArrayList<String> answer = new ArrayList<String>();
    public  String currentAnswer = new String();
    public static String[] boggleBoard = {"A","A","A","A","B","B","C","C","D","D","D","E","E","E","E","E","E","F","F","G","G","H","I","I","I","I","J","K","K","L","L","L","M","M","N","N","N","N","O","O","O","O","P","P","Q","R","R","R","R","S","S","S","S","T","T","T","T","U","U","V","V","W","W","X","Y","Y","Z"};
    public StringBuilder BuildAnswer = new StringBuilder();
    public String BoggleString;
    public String[][] buttonIdArray;
    public int prevx, prevy;
    public boolean isFirstWordPressed ;
    public StringBuilder dictionaryEntry = new StringBuilder();

    // The following are used for the shake detection
    private ShakeDetector mShakeDetector;
    private SensorManager mSensorManager;
    private Sensor mAccelerometer;


    // public CharSequence[] acceptedStrings;
    private CountDownTimer countDownTimer;
    public TextView textView;
    public StringBuilder buf = new StringBuilder();
    public String[] dictWords;
    public boolean[] isPressed = {false,false,false,false,false,false,false,false,false,false,false,false,false,false,false,false};
    public StringBuilder displayListBld = new StringBuilder();
    private final long startTime = 30 * 1000;
    private final long interval = 1 * 1000;
    public int acceptStringsCounter =0;
    public static StringBuilder solutions = new StringBuilder();
    public int shaked = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_easy);
///music
        if(MainActivity.keepMusicGoing) {
            if(!MainActivity.mediaPlayer.isPlaying()){
                MainActivity.mediaPlayer.reset();

            }
        }


        wordlist = new ArrayList<String>();
        solution = new ArrayList<String>();
        solutions = new StringBuilder();
        answer = new ArrayList<String>();
        currentAnswer = "";
        MainActivity.roundNumber++;
        final TextView Shake = (TextView) findViewById(R.id.textViewShake);

        final Button Solver = (Button) findViewById(R.id.SolveButton);
        final TextView Display = (TextView) findViewById(R.id.Entry);
        Display.setMovementMethod(new ScrollingMovementMethod());
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
  //      boggle.setVisibility(View.INVISIBLE);
        Submit.setVisibility(View.INVISIBLE);
        // ShakeDetector initialization
        mSensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        mAccelerometer = mSensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        mShakeDetector = new ShakeDetector(new ShakeDetector.OnShakeListener() {
            @Override
            public void onShake() {
                if (shaked == 0) {
                    shaked = 1;
                    // boggle.setVisibility(View.VISIBLE);
                    Toast.makeText(getApplicationContext(), "Its Shaking!", Toast.LENGTH_SHORT).show();
                    Shake.setVisibility(View.INVISIBLE);
                    textView = (TextView) findViewById(R.id.textView);
                    countDownTimer = new CountDownTimerActivity(startTime, interval);
                    textView.setText(textView.getText() + String.valueOf(startTime / 1000));

                 //   boggle.setVisibility(View.INVISIBLE);
                    Submit.setVisibility(View.VISIBLE);
                    Display.setVisibility(View.VISIBLE);
                    textView.setVisibility(View.VISIBLE);
                    //to open the dictionary file and save the data in a StringBuilder named buf
                    InputStream in = getResources().openRawResource(getResources().getIdentifier("words", "raw", getPackageName()));
                    if (in != null) {
                        InputStreamReader tmp = new InputStreamReader(in);
                        BufferedReader reader = new BufferedReader(tmp);
                        String str;

                        try {
                            while ((str = reader.readLine()) != null) {
                                buf.append(str.toUpperCase() + " ");
                                dic.add(str.toUpperCase());
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
                    countDownTimer.start();
                    Boggle();
                    isFirstWordPressed = false;
                    prevx = 0;
                    prevy = 0;

                    b1.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b2.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b3.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b4.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b5.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b6.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b7.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b8.setBackgroundResource(R.drawable.buttonnoselectborder);
                    b9.setBackgroundResource(R.drawable.buttonnoselectborder);

                    b1.setText(boggleBoard[0]);
                    b2.setText(boggleBoard[1]);
                    b3.setText(boggleBoard[2]);
                    b4.setText(boggleBoard[3]);
                    b5.setText(boggleBoard[4]);
                    b6.setText(boggleBoard[5]);
                    b7.setText(boggleBoard[6]);
                    b8.setText(boggleBoard[7]);
                    b9.setText(boggleBoard[8]);
                    BuildAnswer.setLength(0);

                    for (int i = 0; i < 9; i++) {
                        isPressed[i] = false;
                    }

                    new solveBoard().execute();
                }
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

                if (BuildAnswer.length() > 2) {
                    dictionaryEntry.append(BuildAnswer);
                    dictReturn = checkPresent();
                    if (dictReturn) {
                        if(answer.contains(BuildAnswer.toString()) == false) {
                            displayListBld.append(dictionaryEntry.toString() + System.getProperty("line.separator"));
                            Display.setText(displayListBld.toString());
                            answer.add(BuildAnswer.toString());
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
        });

        b1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
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
                        adjacent = AdjacentorNot(1, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 0;
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
                        adjacent = AdjacentorNot(1, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 1;
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
                        adjacent = AdjacentorNot(1, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 1;
                        prevy = 2;
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
                        adjacent = AdjacentorNot(2, 0, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 2;
                        prevy = 0;
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
                        adjacent = AdjacentorNot(2, 1, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 2;
                        prevy = 1;
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
                        adjacent = AdjacentorNot(2, 2, prevx, prevy);
                    }
                    if (adjacent) {
                        prevx = 2;
                        prevy = 2;
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
    private boolean isInDictionary() throws IOException {
        int leng = dictWords.length;
        int i = 0, j=0;
        int temp;
       /* AlertDialog alertDialog = new AlertDialog.Builder(Game.this).create();
        alertDialog.setMessage("the length is " + leng);
        alertDialog.show();*/
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
    public class CountDownTimerActivity extends CountDownTimer {
        public CountDownTimerActivity(long startTime, long interval) {
            super(startTime, interval);
        }

        @Override
        public void onFinish() {
            textView.setText("Time's up!");



            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(gameEasy.this, scoreBoard.class);
                    startActivity(i);

                    finish();
                }
            }, 0);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            textView.setText("" + millisUntilFinished/1000);
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
        boggleBoard[4]="E";
    }

    private class solveBoard extends AsyncTask<Void, Void, Void> {
        @Override
        protected Void doInBackground(Void... params) {
            Collection<Vertex> items;
            String[][] grid = {{gameEasy.boggleBoard[0], gameEasy.boggleBoard[1], gameEasy.boggleBoard[2]},
                    {gameEasy.boggleBoard[3],gameEasy.boggleBoard[4], gameEasy.boggleBoard[5]},
                    {gameEasy.boggleBoard[6], gameEasy.boggleBoard[7],gameEasy.boggleBoard[8]}};

            Vertex[][] boardg = new Vertex[3][3];
            items = new ArrayList<Vertex>(9);
            for (int i = 0; i < grid.length; i++) {
                for (int j = 0; j < grid[i].length; j++) {
                    boardg[i][j] = new Vertex(grid[i][j]);
                    items.add(boardg[i][j]);
                }
            }

            int len = boardg.length;
            for (int i = 0; i < boardg.length; i++) {
                for (int j = 0; j < boardg[i].length; j++) {
                    //line above(i-1)
                    if (i - 1 >= 0 && j - 1 >= 0) {
                        if (boardg[i][j].inNeighbor(boardg[i - 1][j - 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i - 1][j - 1]);
                    }
                    if (i - 1 >= 0) {
                        if (boardg[i][j].inNeighbor(boardg[i - 1][j]) == false)
                            boardg[i][j].addNeighbor(boardg[i - 1][j]);
                    }
                    if (i - 1 >= 0 && j + 1 <= len - 1) {
                        if (boardg[i][j].inNeighbor(boardg[i - 1][j + 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i - 1][j + 1]);
                    }

                    //same line(same i)
                    if (j - 1 >= 0) {
                        if (boardg[i][j].inNeighbor(boardg[i][j - 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i][j - 1]);
                    }
                    if (j + 1 <= len - 1) {
                        if (boardg[i][j].inNeighbor(boardg[i][j + 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i][j + 1]);
                    }

                    //line below(i+1)
                    if (i + 1 <= len - 1 && j - 1 >= 0) {
                        if (boardg[i][j].inNeighbor(boardg[i + 1][j - 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i + 1][j - 1]);
                    }
                    if (i + 1 <= len - 1) {
                        if (boardg[i][j].inNeighbor(boardg[i + 1][j]) == false)
                            boardg[i][j].addNeighbor(boardg[i + 1][j]);
                    }
                    if (i + 1 <= len - 1 && j + 1 < len) {
                        if (boardg[i][j].inNeighbor(boardg[i + 1][j + 1]) == false)
                            boardg[i][j].addNeighbor(boardg[i + 1][j + 1]);
                    }
                }
            }

            for (Vertex vert: items){
                dfs(vert, "");
            }

            return null;
        }


        public String inDic(String s)
        {
            String temp;
            int len = s.length();
            for(int i=0; i<dic.size();++i)
            {

                temp = dic.get(i);
                if(temp.equals(s))
                {
                    return s;
                }
                if(temp.length()>= len && temp.substring(0,len).equals(s))
                {
                    return "1";
                }
                if(temp.compareTo(s) > 0)
                {
                    return "0";
                }
            }
            return "0";
        }
        public void dfs(Vertex u, String currentWord){
            String currentLetter = u.visit();
            currentWord= currentWord + currentLetter;


            String result = inDic(currentWord);
            if(result.equals("0"))
            {
                return;
            }
            else if(result.substring(0,1).equals("1")==false)
            {
                if(currentWord.length()>2)
                {
                    if(solution.contains(currentWord)==false) {
                        solution.add(currentWord);
                        solutions.append(currentWord + "   ");
                    }
                }
            }

            u.visited = true;
            Iterable<Vertex> vertices =u.getVertices();
            for (Vertex v : vertices){
                if (v.isVisited() != true){
                    dfs(v,currentWord);
                }
            }
            u.visited = false;

        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_game_easy, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
