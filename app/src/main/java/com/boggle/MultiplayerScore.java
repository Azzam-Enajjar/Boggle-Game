package com.boggle;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.util.ArrayList;


public class MultiplayerScore extends ActionBarActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_score);

        ArrayList<String> ClientAns = getIntent().getStringArrayListExtra("ClientAns");
        ArrayList<String> ServerAns = getIntent().getStringArrayListExtra("ServerAns");
        int mode = getIntent().getIntExtra("WhoAreYou",1);

        final TextView youAns = (TextView) findViewById(R.id.textView2);
        final TextView oppAns = (TextView) findViewById(R.id.textView3);
        final TextView youScr = (TextView) findViewById(R.id.textView6);
        final TextView oppScr = (TextView) findViewById(R.id.textView7);
        final Button okbtn = (Button)findViewById(R.id.button);

        String ca = "";
        int cscore = 0;
        for(String s: ClientAns)
        {
            int slen = s.length();
            switch (slen) {
                case 3:
                    cscore = cscore + 1;
                    break;
                case 4:
                    cscore = cscore + 1;
                    break;
                case 5:
                    cscore = cscore + 2;
                    break;
                case 6:
                    cscore = cscore + 3;
                    break;
                case 7:
                    cscore = cscore + 5;
                    break;
                case 8:
                    cscore = cscore + 11;
                    break;

                default:
                    cscore = cscore + 11;
            }
            ca = ca + s + System.getProperty("line.separator");
        }

        String sa = "";
        int sscore = 0;
        for(String s: ServerAns)
        {
            int slen = s.length();
            switch (slen) {
                case 3:
                    sscore = sscore + 1;
                    break;
                case 4:
                    sscore = sscore + 1;
                    break;
                case 5:
                    sscore = sscore + 2;
                    break;
                case 6:
                    sscore = sscore + 3;
                    break;
                case 7:
                    sscore = sscore + 5;
                    break;
                case 8:
                    sscore = sscore + 11;
                    break;

                default:
                    sscore = sscore + 11;
            }
            sa = sa + s + System.getProperty("line.separator");
        }

        if(mode == 1)//client
        {
            youAns.setText(ca);
            oppAns.setText(sa);
            youScr.setText(Integer.toString(cscore));
            oppScr.setText(Integer.toString(sscore));

            if(cscore>sscore){

                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("You Win!");
                alertDialog1.show();
            }
            else if(sscore>cscore){
                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("Opponent Wins!");
                alertDialog1.show();
            }
            else{
                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("It's a Tie!");
                alertDialog1.show();

            }


        }
        else
        {
            youAns.setText(sa);
            oppAns.setText(ca);
            youScr.setText(Integer.toString(sscore));
            oppScr.setText(Integer.toString(cscore));

            if(cscore>sscore){

                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("Opponent Wins!");
                alertDialog1.show();
            }
            else if(sscore>cscore){
                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("You Win!");
                alertDialog1.show();
            }
            else{
                AlertDialog alertDialog1 = new AlertDialog.Builder(MultiplayerScore.this).create();
                alertDialog1.setMessage("It's a Tie!");
                alertDialog1.show();

            }
        }

     okbtn.setOnClickListener(new View.OnClickListener() {
         @Override
         public void onClick(View v) {
             Intent main = new Intent(MultiplayerScore.this, MainActivity.class);
             startActivity(main);
         }
     });




    }


}
