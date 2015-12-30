package com.boggle;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.*;

import java.util.ArrayList;


public class scoreBoard extends ActionBarActivity {
    //public final Button   buttonOK = (Button)findViewById(R.id.btnOK);


    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_score_board);
        TextView textView = (TextView) findViewById(R.id.textView);
        TextView textScore = (TextView) findViewById(R.id.textScore);
        TextView textWord = (TextView) findViewById(R.id.textWord);
        TextView textWord1 = (TextView) findViewById(R.id.textWord1);
        TextView textGrid = (TextView) findViewById(R.id.textGrid);
        TextView textDict = (TextView) findViewById(R.id.textDict);
        Button buttonOK = (Button) findViewById(R.id.btnOK);

        for(int i = 0; i < MainActivity.roundCount; ++i)
        {
            MainActivity.roundScores.add(0);
        }


        int size;
        if(MainActivity.level == 2) {
            size = Game.answer.size();
            for(String s: Game.answer)
            {
                if(Game.solution.size() == 0) {
                    Game.solution.add(s);
                    Game.solutions.append(s + " ");
                }
                else if(Game.solution.contains(s) == false) {
                    Game.solution.add(s);
                    Game.solutions.append(s + " ");
                }
            }

            textDict.setText(Game.solutions.toString());
        }
        else {
            size = gameEasy.answer.size();

            for(String s: gameEasy.answer)
            {
                if(gameEasy.solution.size() == 0) {
                    gameEasy.solution.add(s);
                    gameEasy.solutions.append(s + " ");
                }
                else if(gameEasy.solution.contains(s) == false) {
                    gameEasy.solution.add(s);
                    gameEasy.solutions.append(s + " ");
                }
            }
            textDict.setText(gameEasy.solutions.toString());
        }

        int score = 0;
        String s;
        for (int i = 0; i < size; ++i) {
            if(MainActivity.level == 2)
                s = Game.answer.get(i);
            else
                s = gameEasy.answer.get(i);
            int slen = s.length();
            switch (slen) {
                case 3:
                    score = score + 1;
                    break;
                case 4:
                    score = score + 1;
                    break;
                case 5:
                    score = score + 2;
                    break;
                case 6:
                    score = score + 3;
                    break;
                case 7:
                    score = score + 5;
                    break;
                case 8:
                    score = score + 11;
                    break;

                default:
                    score = score + 11;
            }
        }
        textScore.setText(Integer.toString(score));
        String ansdisplay = new String();

        for (int i = 0; i < size; ++i) {
            if(MainActivity.level == 2)
                ansdisplay += Game.answer.get(i) + "\n";
            else
                ansdisplay += gameEasy.answer.get(i) + "\n";
        }
        textWord.setText(ansdisplay);

        MainActivity.roundScores.set(MainActivity.roundNumber-1, score);


        buttonOK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new Handler().postDelayed(new Runnable() {
                    public void run() {
                        Intent i = new Intent(scoreBoard.this, roundScores.class);
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
        getMenuInflater().inflate(R.menu.menu_score_board, menu);
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
