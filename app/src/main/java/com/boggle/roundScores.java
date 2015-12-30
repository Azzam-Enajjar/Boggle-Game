package com.boggle;

import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableRow.LayoutParams;
import android.widget.*;

import java.util.*;


public class roundScores extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_round_scores);

        int rCount = MainActivity.roundCount;
        int rNumber = MainActivity.roundNumber;
        List<Integer> rScores = MainActivity.roundScores;

        TableLayout tl = (TableLayout) findViewById(R.id.tlRoundScores);
        Button NextRound = (Button) findViewById(R.id.btnNextRound);
        Button Quit = (Button) findViewById(R.id.btnQuit);
        Button OK = (Button) findViewById(R.id.btnOK);

        if(rCount == rNumber)
        {
            NextRound.setVisibility(View.INVISIBLE);
            Quit.setVisibility(View.INVISIBLE);
            OK.setVisibility(View.VISIBLE);
        }
        else
        {
            NextRound.setVisibility(View.VISIBLE);
            Quit.setVisibility(View.VISIBLE);
            OK.setVisibility(View.INVISIBLE);
        }

        for(int i = 0; i < rCount; ++i) {

            TableRow tr = new TableRow(this);
            LayoutParams lp = new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT);
            tr.setLayoutParams(lp);

            TextView tvLeft = new TextView(this);
            tvLeft.setLayoutParams(lp);
            tvLeft.setTextSize(16);
            tvLeft.setTextColor(Color.YELLOW);
            tvLeft.setText(Integer.toString(i+1));
            TextView tvCenter = new TextView(this);
            tvCenter.setLayoutParams(lp);
            tvCenter.setTextSize(16);
            tvCenter.setTextColor(Color.YELLOW);
            tvCenter.setText("   ");

            int temp = rScores.get(i);

            TextView tvRight = new TextView(this);
            tvRight.setLayoutParams(lp);
            tvRight.setText(Integer.toString(temp));
            tvRight.setTextSize(16);
            tvRight.setTextColor(Color.YELLOW);

            tr.addView(tvLeft);
            tr.addView(tvCenter);
            tr.addView(tvRight);
            tl.addView(tr, new TableLayout.LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.WRAP_CONTENT));
        }


        Quit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {


                MainActivity.roundCount = 0;
                MainActivity.roundNumber = 0;
                MainActivity.roundScores = new ArrayList<Integer>();
                MainActivity.level = 0; // 1 for easy, 2 for hard
                MainActivity.wordlist = new ArrayList<String>();
                MainActivity.mode = 0;
                MainActivity.solution = new ArrayList<String>();
                MainActivity.state = 0;// 1-Menu1; 2-Menu2; 3-Menu3; 4-Play


                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(roundScores.this, MainActivity.class);
                        startActivity(i);

                        finish();
                    }
                }, 0);
            }
        });


        NextRound.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(MainActivity.level == 2) {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent i = new Intent(roundScores.this, Game.class);
                            startActivity(i);

                            finish();
                        }
                    }, 0);
                }
                else
                {
                    new Handler().postDelayed(new Runnable() {
                        public void run() {
                            Intent i = new Intent(roundScores.this, gameEasy.class);
                            startActivity(i);

                            finish();
                        }
                    }, 0);
                }
            }
        });

        OK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                MainActivity.roundCount = 0;
                MainActivity.roundNumber = 0;
                MainActivity.roundScores = new ArrayList<Integer>();
                MainActivity.level = 0; // 1 for easy, 2 for hard
                MainActivity.wordlist = new ArrayList<String>();
                MainActivity.mode = 0;
                MainActivity.solution = new ArrayList<String>();
                MainActivity.state = 0;// 1-Menu1; 2-Menu2; 3-Menu3; 4-Play


                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(roundScores.this, MainActivity.class);
                        startActivity(i);

                        finish();
                    }
                }, 0);
            }
        });










    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_round_scores, menu);
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
