package shiprocket.com.ashu;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;

/**
 * Created by apple on 28/01/17.
 */

public class Player {
    private Bitmap bitmap;
    private int x;
    private int y;
    private int speed = 0;
    private boolean boosting;
    private final int GRAVITY = -15;
    private int maxY;
    private int minY;

    private final int MIN_SPEED = 5;
    private final int MAX_SPEED = 200;

    private Rect detectCollision;

    public Player(Context context, int screenX, int screenY) {
        x = 75;
        y = 50;
        speed = 1;

        SharedPreferences sp = context.getSharedPreferences("Purchase", 0);
        String whichShip = sp.getString("newship","default");

        if(whichShip.contains("default"))
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.player);
        else
            bitmap = BitmapFactory.decodeResource(context.getResources(), R.drawable.mymiss);
        maxY = screenY - bitmap.getHeight();
        minY = 0;
        boosting = false;



        //initializing rect object
        detectCollision =  new Rect(x, y, bitmap.getWidth(), bitmap.getHeight());
    }

    public void setBoosting() {
        boosting = true;
    }

    public void stopBoosting() {
        boosting = false;
    }

    public void update() {
        if (boosting) {
            speed += 2;
        } else {
            speed -= 2;
        }

        if (speed > MAX_SPEED) {
            speed = MAX_SPEED;
        }

        if (speed < MIN_SPEED) {
            speed = MIN_SPEED;
        }

        y -= speed + GRAVITY;

        if (y < minY) {
            y = minY;
        }
        if (y > maxY) {
            y = maxY;
        }

        //adding top, left, bottom and right to the rect object
        detectCollision.left = x;
        detectCollision.top = y;
        detectCollision.right = x + bitmap.getWidth();
        detectCollision.bottom = y + bitmap.getHeight();

    }

    //one more getter for getting the rect object
    public Rect getDetectCollision() {
        return detectCollision;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public int getSpeed() {
        return speed;
    }
}
