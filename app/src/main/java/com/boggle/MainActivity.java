package com.boggle;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;


public class MainActivity extends ActionBarActivity {

    public static int roundCount = 0;
    public static int roundNumber = 0;
    public static List<Integer> roundScores = new ArrayList<Integer>();
    public static int level = 0; // 1 for easy, 2 for hard
    public static ArrayList<String> wordlist = new ArrayList<String>();
    public static int mode = 0; // 1 for single player, 2 for multiple player
    public static ArrayList<String> solution = new ArrayList<String>();
    public static char[][] boardrep; //instansiated after level were chosen = new ch   private MusicService mServ;
    public static int state = 0;// 1-Menu1; 2-Menu2; 3-Menu3; 4-Play
    private boolean mIsBound = false;
    public static boolean keepMusicGoing = true;
    static  MediaPlayer mediaPlayer;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button_bk = (Button) findViewById(R.id.button_back);
        final Button button_hp = (Button) findViewById(R.id.button_help);
        final Button button_ok = (Button) findViewById(R.id.button_ok);
        final TextView txthelp = (TextView) findViewById(R.id.texthelp);
        txthelp.setVisibility(View.INVISIBLE);
//////background music////
        if(keepMusicGoing) {
            mediaPlayer = MediaPlayer.create(this, R.raw.audio);
            if(!mediaPlayer.isPlaying()) {

                mediaPlayer.start();
                mediaPlayer.setLooping(true);
            }
        }


        State_Menu1();



        final Button button_sp = (Button) findViewById(R.id.botton_sp);
        button_sp.setBackgroundResource(R.drawable.single);
        button_sp.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                button_sp.setBackgroundResource(R.drawable.singleselected);
                State_Menu2();
                mode = 1;
            }
        });
        final Button button_mp = (Button) findViewById(R.id.botton_mp);
        button_mp.setBackgroundResource(R.drawable.multi);
        button_mp.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                button_mp.setBackgroundResource(R.drawable.multiselected);
                Intent multintent = new Intent(MainActivity.this, MultiplayerMenu.class);
                startActivity(multintent);

                //State_Menu2();
                mode = 2;
            }
        });

        Button button_easy = (Button) findViewById(R.id.botton_easy);
        button_easy.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                State_Menu3();
                level = 1;
            }
        });
        Button button_hard = (Button) findViewById(R.id.botton_hard);
        button_hard.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                State_Menu3();
                level = 2;
            }
        });


        Button button_roundsubmit = (Button) findViewById(R.id.botton_roundsubmit);
        button_roundsubmit.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                Spinner spinner_rounds = (Spinner) findViewById(R.id.spinner_rounds);
                String temp = String.valueOf(spinner_rounds.getSelectedItem());
                roundCount = Integer.parseInt(temp);
                State_Play();
            }
        });

        Button button_back = (Button) findViewById(R.id.button_back);
        button_back.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                switch(state) {
                    case 0:
                        State_Menu1(); break;
                    case 1:
                        State_Menu1(); break;
                    case 2:
                        State_Menu1(); break;
                    case 3:
                        State_Menu2(); break;
                    case 4:
                        State_Menu3(); break;
                    default:
                        State_Menu1(); break;
                }

            }
        });

        final Button buttonhelp = (Button) findViewById(R.id.button_help);
        final RelativeLayout Menu1 =  (RelativeLayout) findViewById(R.id.RL_Menu1);

        buttonhelp.setOnClickListener(new OnClickListener()
        {
            public void onClick(View v)
            {
                txthelp.setVisibility(View.VISIBLE);
                txthelp.setText("Players then have 3 minutes to select as many words of 3 or more letters as they can.Repeated words are not allowed.The words must be formed from sequentially adjacent cubes. A cube is adjacent to another if they are horizontal, vertical or diagonal neighbors. Words can be in singular, plural or otherderived forms. Names and abbreviations are not valid. Each cube letter can be used only once in a single word. The score of each valid word is counted based on its length, 1 point for 3 or 4 letter words, 2 points for 5 letter words, 3 points for 6 letter words, 5 points for 7 letter words, and 11 points for words of 8 or more letters.");
                Menu1.setVisibility(View.INVISIBLE);
                buttonhelp.setVisibility(View.INVISIBLE);
                button_ok.setVisibility(View.VISIBLE);
            }
        });

        button_ok.setOnClickListener((new OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu1.setVisibility(View.VISIBLE);
                txthelp.setVisibility(View.INVISIBLE);
                button_ok.setVisibility(View.INVISIBLE);
                buttonhelp.setVisibility(View.VISIBLE);

            }
        }));
    }

    public void State_Menu1()
    {
        state = 1;
        Button button_bk = (Button) findViewById(R.id.button_back);
        button_bk.setVisibility(View.INVISIBLE);
        Button button_ok = (Button) findViewById(R.id.button_ok);
        button_bk.setVisibility(View.INVISIBLE);
        Button button_hp = (Button) findViewById(R.id.button_help);
        button_hp.setVisibility(View.VISIBLE);
        //TextView txthelp = (TextView) findViewById(R.id.texthelp);
        //txthelp.setVisibility(View.INVISIBLE);

        RelativeLayout Menu1 =  (RelativeLayout) findViewById(R.id.RL_Menu1);
        RelativeLayout Menu2 =  (RelativeLayout) findViewById(R.id.RL_Menu2);
        RelativeLayout Menu3 =  (RelativeLayout) findViewById(R.id.RL_Menu3);
        RelativeLayout Play =  (RelativeLayout) findViewById(R.id.RL_Play);
        Menu1.setVisibility(View.VISIBLE);
        Menu2.setVisibility(View.INVISIBLE);
        Menu3.setVisibility(View.INVISIBLE);
        Play.setVisibility(View.INVISIBLE);
        mode = 0;
        level = 0;
        roundCount = 0;
        roundNumber = 0;
    }

    public void State_Menu2()
    {
        Button button_bk = (Button) findViewById(R.id.button_back);
        button_bk.setVisibility(View.VISIBLE);
        Button button_hp = (Button) findViewById(R.id.button_help);
        button_hp.setVisibility(View.INVISIBLE);
        Button button_ok = (Button) findViewById(R.id.button_ok);
        button_bk.setVisibility(View.INVISIBLE);
        TextView txthelp = (TextView) findViewById(R.id.texthelp);
        txthelp.setVisibility(View.INVISIBLE);

        state = 2;
        RelativeLayout Menu1 =  (RelativeLayout) findViewById(R.id.RL_Menu1);
        RelativeLayout Menu2 =  (RelativeLayout) findViewById(R.id.RL_Menu2);
        RelativeLayout Menu3 =  (RelativeLayout) findViewById(R.id.RL_Menu3);
        RelativeLayout Play =  (RelativeLayout) findViewById(R.id.RL_Play);
        Menu1.setVisibility(View.INVISIBLE);
        Menu2.setVisibility(View.VISIBLE);
        Menu3.setVisibility(View.INVISIBLE);
        Play.setVisibility(View.INVISIBLE);
        level = 0;
        roundCount = 0;
        roundNumber = 0;


    }

    public void State_Menu3()
    {
        Button button_bk = (Button) findViewById(R.id.button_back);
        button_bk.setVisibility(View.VISIBLE);
        Button button_hp = (Button) findViewById(R.id.button_help);
        button_hp.setVisibility(View.INVISIBLE);
        Button button_ok = (Button) findViewById(R.id.button_ok);
        button_bk.setVisibility(View.INVISIBLE);
        TextView txthelp = (TextView) findViewById(R.id.texthelp);
        txthelp.setVisibility(View.INVISIBLE);

        state = 3;
        RelativeLayout Menu1 =  (RelativeLayout) findViewById(R.id.RL_Menu1);
        RelativeLayout Menu2 =  (RelativeLayout) findViewById(R.id.RL_Menu2);
        RelativeLayout Menu3 =  (RelativeLayout) findViewById(R.id.RL_Menu3);
        RelativeLayout Play =  (RelativeLayout) findViewById(R.id.RL_Play);
        Menu1.setVisibility(View.INVISIBLE);
        Menu2.setVisibility(View.INVISIBLE);
        Menu3.setVisibility(View.VISIBLE);
        Play.setVisibility(View.INVISIBLE);

        Spinner spinner_rounds = (Spinner) findViewById(R.id.spinner_rounds);
        String[] items = new String[]{"1", "2", "3", "4", "5"};
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, items);
        spinner_rounds.setAdapter(adapter);

        roundCount = 0;
        roundNumber = 0;
    }

    public void  State_Play()
    {
        state = 4;
        if(level == 1)
        {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(MainActivity.this, gameEasy.class);
                    startActivity(i);

                    finish();
                }
            }, 0);

        }
        /*
        RelativeLayout Menu1 =  (RelativeLayout) findViewById(R.id.RL_Menu1);
        RelativeLayout Menu2 =  (RelativeLayout) findViewById(R.id.RL_Menu2);
        RelativeLayout Menu3 =  (RelativeLayout) findViewById(R.id.RL_Menu3);
        RelativeLayout Play =  (RelativeLayout) findViewById(R.id.RL_Play);
        Menu1.setVisibility(View.INVISIBLE);
        Menu2.setVisibility(View.INVISIBLE);
        Menu3.setVisibility(View.INVISIBLE);
        Play.setVisibility(View.VISIBLE);*/
        else {
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    Intent i = new Intent(MainActivity.this, Game.class);
                    startActivity(i);

                    finish();
                }
            }, 0);
        }
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
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
