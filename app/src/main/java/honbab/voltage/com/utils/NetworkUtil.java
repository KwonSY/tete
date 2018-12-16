package honbab.voltage.com.utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkUtil {

    public static boolean isNetworkPresent(Context context) {
        boolean isNetworkAvailable = false;
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        try {
            if (cm != null) {
                NetworkInfo netInfo = cm.getActiveNetworkInfo();
                if (netInfo != null) {
                    isNetworkAvailable = netInfo.isConnectedOrConnecting();
                }
            }
        } catch (Exception ex) {
            Log.e("abc", "network = " + ex.getMessage());
        }
        //check for wifi also
        Log.e("abc", "isNetworkAvailable = " + isNetworkAvailable);
        if(!isNetworkAvailable) {
            WifiManager connec = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//            Log.e("abc", "connec.isWifiEnabled = " + connec.isWifiEnabled());
            NetworkInfo.State wifi = cm.getNetworkInfo(1).getState();
            Log.e("abc", "wifi = " + wifi);
//            if (connec.isWifiEnabled() && wifi.toString().equalsIgnoreCase("CONNECTED")) {
            if (wifi.toString().equalsIgnoreCase("CONNECTED")) {
                isNetworkAvailable = true;
            } else {//DISCONNECTED
                isNetworkAvailable = false;
            }
        }

        Log.e("abc", "isNetworkAvailable = " + isNetworkAvailable);
        return isNetworkAvailable;
    }

    public static boolean isOnline(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        }
        return false;
    }
}