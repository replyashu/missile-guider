package shiprocket.com.ashu;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity implements View.OnClickListener {

    // play image button
    private ImageButton buttonPlay;
    //high score button
    private ImageButton buttonScore;
    //instructions button
    private ImageButton buttonInstructions;



    private AppController appController;


    private Dialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        Fabric.with(this, new Crashlytics());
        Appsee.start(getString(R.string.com_appsee_apikey));

        setContentView(R.layout.activity_main);

        //setting the orientation to landscape
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);

        //getting the button
        buttonPlay = (ImageButton) findViewById(R.id.buttonPlay);

        //initializing the highscore button
        buttonScore = (ImageButton) findViewById(R.id.buttonScore);

        //initializing the instructions button
        buttonInstructions = (ImageButton) findViewById(R.id.buttonHow);

        //setting the on click listener to high score button
        buttonScore.setOnClickListener(this);
        //setting the on click listener to play now button
        buttonPlay.setOnClickListener(this);
        buttonInstructions.setOnClickListener(this);

        appController = (AppController) getApplication();

    }

    @Override
    protected void onResume() {
        super.onResume();

    }

    // the onclick methods
    @Override
    public void onClick(View v) {

        if (v == buttonPlay) {
            //the transition from MainActivity to GameActivity
            startActivity(new Intent(MainActivity.this, GameActivity.class));
            finish();
        }
        if (v == buttonScore) {

            //the transition from MainActivity to HighScore activity
            startActivity(new Intent(MainActivity.this, HighScore.class));
            finish();
        }
        if(v == buttonInstructions){
            dialog = new Dialog(this); // Context, this, etc.
            dialog.setContentView(R.layout.dialog_instruction);
            dialog.setTitle("How To Play");
            dialog.setCancelable(true);
            dialog.show();
        }


    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Are you sure you want to exit?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        GameView.stopMusic();
                        Intent startMain = new Intent(Intent.ACTION_MAIN);
                        startMain.addCategory(Intent.CATEGORY_HOME);
                        startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(startMain);
                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if(isFinishing()){
            if (dialog!= null) {
                dialog.dismiss();
                dialog= null;
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (dialog != null) {
            dialog.dismiss();
            dialog = null;
        }
    }
}