package shiprocket.com.rocketship;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.provider.Settings;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import com.appsee.Appsee;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import io.fabric.sdk.android.Fabric;

public class MainActivity extends Activity implements View.OnClickListener {

    // play image button
    private ImageButton buttonPlay;
    //high score button
    private ImageButton buttonScore;
    //instructions button
    private ImageButton buttonInstructions;

    private Tracker mTracker;

    private FirebaseAnalytics mFirebaseAnalytics;

    private AppController appController;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    static{
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }

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
        mTracker = appController.getDefaultTracker();

        mTracker.setScreenName("MainActivity");
        mTracker.send(new HitBuilders.ScreenViewBuilder().build());

        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

        sendValueToFirebaseDb();

        MobileAds.initialize(getApplicationContext(), "ca-app-pub-3940256099942544~3347511713");

        AdView mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
//         AdRequest adRequest =new  AdRequest.Builder().addTestDevice("A2E80971A6E57AE52CFED664F815B18A").build();
        mAdView.loadAd(adRequest);
    }


    private void sendValueToFirebaseDb(){
        database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("users/");

//        String id = AppController.getInstance().getAndroidId();
        String id = Settings.Secure.getString(getApplication().getContentResolver(), Settings.Secure.ANDROID_ID);

        User user = new User();

        user.setId(id);
        user.setName("guest");
        myRef.child(id).setValue(user);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mTracker.send(new HitBuilders.EventBuilder()
                .setCategory("User")
                .setLabel(appController.getAndroidId())
                .setAction("Came Now")
                .build());
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
        }
        if(v == buttonInstructions){
            final Dialog dialog = new Dialog(this); // Context, this, etc.
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
}