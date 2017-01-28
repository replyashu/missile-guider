package shiprocket.com.rocketship;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;

/**
 * Created by apple on 28/01/17.
 */

public class HighScore extends AppCompatActivity {

    TextView textView,textView2,textView3,textView4;

    SharedPreferences sharedPreferences;

    ImageButton playNow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_score);

        //initializing the textViews
        textView = (TextView) findViewById(R.id.textView);
        textView2 = (TextView) findViewById(R.id.textView2);
        textView3 = (TextView) findViewById(R.id.textView3);
        textView4 = (TextView) findViewById(R.id.textView4);

        sharedPreferences  = getSharedPreferences("SHAR_PREF_NAME", Context.MODE_PRIVATE);

        //setting the values to the textViews
        textView.setText( "HIGH SCORES \n\n\n" + "1."+sharedPreferences.getInt("score1",0));
        textView2.setText("2."+sharedPreferences.getInt("score2",0));
        textView3.setText("3."+sharedPreferences.getInt("score3",0));
        textView4.setText("4."+sharedPreferences.getInt("score4",0));

        playNow = (ImageButton) findViewById(R.id.buttonPlay);

        playNow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HighScore.this, GameActivity.class));

            }
        });

    }
}
