package shiprocket.com.ashu;

import android.app.Application;
import android.net.ConnectivityManager;
import android.provider.Settings;
import android.widget.Toast;



/**
 * Created by apple on 29/01/17.
 */

public class AppController extends Application {



    private static AppController mInstance;


    public static synchronized AppController getInstance() {
        return mInstance;
    }


    public String getAndroidId(){
        String android_id = Settings.Secure.getString(getApplicationContext().getContentResolver(),
                Settings.Secure.ANDROID_ID);
        return android_id;
    }

    public final boolean isInternetOn() {

        // get Connectivity Manager object to check connection
        ConnectivityManager connec =
                (ConnectivityManager) mInstance.getSystemService(mInstance.getBaseContext().CONNECTIVITY_SERVICE);

        // Check for network connections
        if (connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTED ||
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTING ||
                connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.CONNECTED) {

            // if connected with internet


            return true;

        } else if (
                connec.getNetworkInfo(0).getState() == android.net.NetworkInfo.State.DISCONNECTED ||
                        connec.getNetworkInfo(1).getState() == android.net.NetworkInfo.State.DISCONNECTED) {

            Toast.makeText(mInstance, " Please Turn your Internet On ", Toast.LENGTH_LONG).show();
            return false;
        }
        return false;
    }
}
