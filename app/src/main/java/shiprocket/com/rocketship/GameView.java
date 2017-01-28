package shiprocket.com.rocketship;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Tracker;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

/**
 * Created by apple on 28/01/17.
 */

public class GameView extends SurfaceView implements Runnable {

    volatile boolean playing;
    private Thread gameThread = null;
    private Player player;

    private Paint paint;
    private Canvas canvas;
    private SurfaceHolder surfaceHolder;

    private Enemy enemies;

    //created a reference of the class Friend
    private Friend friend;

    private FirebaseAnalytics mFirebaseAnalytics;

    private ArrayList<Star> stars = new
            ArrayList<Star>();

    //defining a boom object to display blast
    private Boom boom;

    private FriendBoom friendBoom;

    //a screenX holder
    int screenX;

    //to count the number of Misses
    int countMisses;

    //indicator that the enemy has just entered the game screen
    boolean flag ;

    //an indicator if the game is Over
    private boolean isGameOver ;

    //the score holder
    int score;

    //the high Scores Holder
    int highScore[] = new int[4];

    private String name;
    private String user;

    //Shared Prefernces to store the High Scores
    SharedPreferences sharedPreferences;

    //the mediaplayer objects to configure the background music
    static MediaPlayer gameOnsound;
    final MediaPlayer killedEnemysound;
    final MediaPlayer gameOversound;

    private Tracker mTracker;

    private static int noOfLives;
    //context to be used in onTouchEvent to cause the activity transition from GameAvtivity to MainActivity.
    Context context;

    private FirebaseDatabase database;
    private DatabaseReference myRef;

    public GameView(Context context, int screenX, int screenY) {
        super(context);
        player = new Player(context, screenX, screenY);

        surfaceHolder = getHolder();
        paint = new Paint();

        int starNums = 100;
        for (int i = 0; i < starNums; i++) {
            Star s = new Star(screenX, screenY);
            stars.add(s);
        }

        //single enemy initialization
        enemies = new Enemy(context, screenX, screenY);

        //initializing boom object
        boom = new Boom(context);

        //initializing the Friend class object
        friend = new Friend(context, screenX, screenY);

        this.screenX = screenX;

        countMisses = 1;

        isGameOver = false;

        //setting the score to 0 initially
        score = 0;

        noOfLives = 3;

        sharedPreferences = context.getSharedPreferences("SHAR_PREF_NAME",Context.MODE_PRIVATE);

//initializing the array high scores with the previous values
        highScore[0] = sharedPreferences.getInt("score1",0);
        highScore[1] = sharedPreferences.getInt("score2",0);
        highScore[2] = sharedPreferences.getInt("score3",0);
        highScore[3] = sharedPreferences.getInt("score4",0);

        //initializing the media players for the game sounds
        gameOnsound = MediaPlayer.create(context,R.raw.gameon);
        killedEnemysound = MediaPlayer.create(context,R.raw.killedenemy);
        gameOversound = MediaPlayer.create(context,R.raw.gameover);

//starting the game music as the game starts
        gameOnsound.start();

        //initializing context
        this.context = context;
    }


    @Override
    public void run() {
        while (playing) {
            update();
            draw();
            control();
        }
    }

    private void update() {
        //incrementing score as time passes
        score++;
        if(score >= countMisses*1000) {
            noOfLives++;
            countMisses *= 10;
        }
        player.update();

        //setting boom outside the screen
        boom.setX(-250);
        boom.setY(-250);

        for (Star s : stars) {
            s.update(player.getSpeed());
        }

        //setting the flag true when the enemy just enters the screen
        if(friend.getX()==screenX){
            flag = true;
        }

        enemies.update(player.getSpeed());
        //if collision occurs with player
        if (Rect.intersects(player.getDetectCollision(), enemies.getDetectCollision())) {
            //displaying boom at that location
            boom.setX(enemies.getX());
            boom.setY(enemies.getY());

            //playing a sound at the collision between player and the enemy
            killedEnemysound.start();
            enemies.setX(-200);
        }

//        else{// the condition where player misses the enemy
//            //if the enemy has just entered
//            if(flag){
//                //if player's x coordinate is equal to enemies's y coordinate
//                if(player.getDetectCollision().exactCenterX()>=enemies.getDetectCollision().exactCenterX()){
//
//                    //increment countMisses
//                    countMisses++;
//
//                    //setting the flag false so that the else part is executed only when new enemy enters the screen
//                    flag = false;
//
//                    //if no of Misses is equal to 3, then game is over.
//                    if(countMisses==3){
//
//                        //setting playing false to stop the game.
//                        playing = false;
//                        isGameOver = true;
//
//
//                        //stopping the gameon music
//                        gameOnsound.stop();
//                        //play the game over sound
//                        gameOversound.start();
//
//                        //Assigning the scores to the highscore integer array
//                        for(int i=0;i<4;i++){
//                            if(highScore[i]<score){
//
//                                final int finalI = i;
//                                highScore[i] = score;
//                                break;
//                            }
//                        }
//
//                        //storing the scores through shared Preferences
//                        SharedPreferences.Editor e = sharedPreferences.edit();
//
//                        for(int i=0;i<4;i++){
//
//                            int j = i+1;
//                            e.putInt("score"+j,highScore[i]);
//                        }
//                        e.apply();
//
//                    }
//
//                }
//            }
//
//        }

        //updating the friend ships coordinates
        friend.update(player.getSpeed());
        //checking for a collision between player and a friend
        if(flag) {
            if (Rect.intersects(player.getDetectCollision(), friend.getDetectCollision())) {
                boom.setX(friend.getX());
                boom.setY(friend.getY());
                noOfLives--;
                flag = false;
                if (noOfLives == 0)
                    endGame();

            }
        }
    }

    private void endGame(){
        //displaying the boom at the collision
        boom.setX(friend.getX());
        boom.setY(friend.getY());
        //setting playing false to stop the game
        playing = false;
        //setting the isGameOver true as the game is over
        isGameOver = true;

        //stopping the gameon music
        gameOnsound.stop();
        //play the game over sound
        gameOversound.start();

        //Assigning the scores to the highscore integer array
        for(int i=0;i<4;i++){

            if(highScore[i]<score){
                final int finalI = i;
                highScore[i] = score;
                break;
            }
        }


        //storing the scores through shared Preferences
        SharedPreferences.Editor e = sharedPreferences.edit();

        for(int i=0;i<4;i++){
            int j = i+1;
            e.putInt("score"+j,highScore[i]);
        }
        e.apply();
    }

    private void control() {
        try {
            gameThread.sleep(17);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void pause() {
        playing = false;
        gameOnsound.pause();
        try {
            gameThread.join();
        } catch (InterruptedException e) {
        }
    }

    public void resume() {
        playing = true;
        gameOnsound.start();
        gameThread = new Thread(this);
        gameThread.start();
    }

    @Override
    public boolean onTouchEvent(MotionEvent motionEvent) {
        switch (motionEvent.getAction() & MotionEvent.ACTION_MASK) {
            case MotionEvent.ACTION_UP:
                player.stopBoosting();
                break;
            case MotionEvent.ACTION_DOWN:
                player.setBoosting();
                break;

        }
        //if the game's over, tappin on game Over screen sends you to MainActivity
        if(isGameOver){
            if(motionEvent.getAction()==MotionEvent.ACTION_DOWN){
                final AppController application = (AppController) context.getApplicationContext();
                mTracker = application.getDefaultTracker();

                mTracker.setScreenName("Game Over");
                mTracker.send(new HitBuilders.ScreenViewBuilder().build());
//
                mTracker.send(new HitBuilders.EventBuilder()
                    .setCategory("User")
                    .setLabel(highScore[0]+"")
                    .setAction(application.getAndroidId())
                    .build());

                user = sharedPreferences.getString("name", "guest");

//                Bundle bundle = new Bundle();
//                if(score >= 1000) {
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_newbie));
//                }
//                else if(score >= 2500){
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_kiddo));
//                }
//
//                else if(score >= 5000){
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_grown_up));
//                }
//
//                else if(score >= 7000){
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_matured));
//                }
//
//                else if(score >= 10000){
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_well_learned));
//                }
//
//                else if(score >= 15000){
//                    bundle.putString(FirebaseAnalytics.Param.ACHIEVEMENT_ID,
//                            getResources().getString(R.string.achievement_mastering));
//                }
//                mFirebaseAnalytics.logEvent(FirebaseAnalytics.Event.UNLOCK_ACHIEVEMENT, bundle);

                if(user.equalsIgnoreCase("guest")) {
                    final Dialog dialog = new Dialog(context); // Context, this, etc.
                    dialog.setContentView(R.layout.dialog_username);
                    final EditText userName = (EditText) dialog.findViewById(R.id.editUserName);
                    Button btnSubmit = (Button) dialog.findViewById(R.id.btnSubmit);
                    btnSubmit.setOnClickListener(new OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            name = userName.getText().toString();

                            if (name.isEmpty()) {
                                name = "guest";
                                Toast.makeText(context, "Enter UserName", Toast.LENGTH_LONG).show();
                            }
                            dialog.dismiss();

                            sharedPreferences.edit().putString("name", name).commit();
                            sendValueToFirebase(application.getAndroidId(), name, highScore[0]);
                            Intent intent = new Intent(context, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                            context.startActivity(intent);
                        }
                    });
                    dialog.setTitle("Enter User Name for Leaderboard");
                    dialog.setCancelable(true);
                    dialog.show();
                }
                else {
                    sendValueToFirebase(application.getAndroidId(), user, highScore[0]);
                    Intent intent = new Intent(context, MainActivity.class);
                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                }


            }
        }
        return true;
    }

//    private void saveToFirebaseAndLocal(){
//
//    }

    private void sendValueToFirebase(String id, String name, int score){
        database = FirebaseDatabase.getInstance();

        // Get a reference to the todoItems child items it the database
        myRef = database.getReference("leaderboard/");

//        String id = AppController.getInstance().getAndroidId();
        Score scores = new Score();
        scores.setId(id);
        scores.setName(name + "");
        scores.setScore(String.valueOf(score));

        myRef.child(id).setValue(scores);
    }

    private void draw() {
        if (surfaceHolder.getSurface().isValid()) {
            canvas = surfaceHolder.lockCanvas();
            canvas.drawColor(Color.BLACK);

            paint.setColor(Color.WHITE);
            paint.setTextSize(20);

            for (Star s : stars) {
                paint.setStrokeWidth(s.getStarWidth());
                canvas.drawPoint(s.getX(), s.getY(), paint);
            }

            //drawing the score on the game screen
            paint.setTextSize(30);
            canvas.drawText("Score:"+score,100,50,paint);

            canvas.drawText("Life Remaining:" + noOfLives, 1600,50,paint);

            canvas.drawBitmap(
                    player.getBitmap(),
                    player.getX(),
                    player.getY(),
                    paint);


            canvas.drawBitmap(
                    enemies.getBitmap(),
                    enemies.getX(),
                    enemies.getY(),
                    paint
            );


            //drawing boom image
            canvas.drawBitmap(
                    boom.getBitmap(),
                    boom.getX(),
                    boom.getY(),
                    paint
            );


            //drawing friends image
            canvas.drawBitmap(
                    friend.getBitmap(),
                    friend.getX(),
                    friend.getY(),
                    paint
            );
            //draw game Over when the game is over
            if(isGameOver){
                paint.setTextSize(150);
                paint.setTextAlign(Paint.Align.CENTER);

                int yPos=(int) ((canvas.getHeight() / 2) - ((paint.descent() + paint.ascent()) / 2));
                canvas.drawText("Game Over",canvas.getWidth()/2,yPos,paint);
            }

            surfaceHolder.unlockCanvasAndPost(canvas);

        }
    }

    //stop the music on exit
    public static void stopMusic(){
        gameOnsound.stop();
    }
}