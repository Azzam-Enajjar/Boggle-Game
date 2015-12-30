package com.boggle;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;


public class MultiplayerMenu extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiplayer_menu);

        final Button btCutThroat = (Button) findViewById(R.id.buttonCT);
        final Button btNormal = (Button) findViewById(R.id.buttonNor);
        btCutThroat.setBackgroundResource(R.drawable.cuththroatmultiplayer);
        btNormal.setBackgroundResource(R.drawable.normalmultiplayer);

        btCutThroat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btCutThroat.setBackgroundResource(R.drawable.cutthroatselected);


                Intent cutthroat = new Intent(MultiplayerMenu.this, CutThroat.class);
                startActivity(cutthroat);
            }
        });

        btNormal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btNormal.setBackgroundResource(R.drawable.normalselected);
                Intent normal = new Intent(MultiplayerMenu.this, MultiplayerNormal.class);
                startActivity(normal);

            }
        });



    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multiplayer_menu, menu);
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
