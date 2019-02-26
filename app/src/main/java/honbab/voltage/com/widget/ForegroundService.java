package honbab.voltage.com.widget;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import java.util.List;

public class ForegroundService extends Service {

    public static final String ACTION_NORMAL = "normal";
    public static final String ACTION_NO_ID = "noid";
    public static final String ACTION_NO_ICON = "noicon";
    public static final String ACTION_SECOND_SERVICE = "secondservice";

    private Handler handler;
    private Toast toast;

    @Override
    public void onCreate() {
        super.onCreate();

        handler = new Handler();

        showMessage("Service created");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        handler.removeCallbacksAndMessages(null);
        handler = null;

        if (toast != null) {
            toast.cancel();
            toast = null;
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            String action = intent.getAction();
            if (action != null)
                processAction(action);
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void processAction(final String action) {
        if (tryGetForegroundState()) {
            showMessage("Removing from foreground");

            stopForeground(true);
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    processAction(action);
                }
            }, 1000);

            return;
        }

        switch (action) {
            case ACTION_NORMAL:
                startForeground(1, new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("title")
                        .setContentText("text")
                        .getNotification());
                break;
            case ACTION_NO_ID:
                startForeground(0, new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("title")
                        .setContentText("text")
                        .getNotification());
                break;
            case ACTION_NO_ICON:
                startForeground(1, new Notification.Builder(this)
                        .setContentTitle("title")
                        .setContentText("text")
                        .getNotification());

                break;
            case ACTION_SECOND_SERVICE:
                startForeground(1, new Notification.Builder(this)
                        .setSmallIcon(android.R.drawable.ic_dialog_info)
                        .setContentTitle("title")
                        .setContentText("text")
                        .getNotification());

                if (startService(new Intent(this, NotificationHidingService.class)) == null)
                    throw new RuntimeException();

                break;
            default:
                throw new RuntimeException("Unknown action: " + action);
        }

        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showMessage(tryGetForegroundState() ? "In foreground" : "Not in foreground");
            }
        }, 1000);

    }

    private Boolean tryGetForegroundState() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        ComponentName componentName = new ComponentName(ForegroundService.this, ForegroundService.class);

        List<ActivityManager.RunningServiceInfo> runningServices = activityManager.getRunningServices(Integer.MAX_VALUE);
        for (ActivityManager.RunningServiceInfo service : runningServices) {
            if (componentName.equals(service.service))
                return service.foreground;
        }

        throw new RuntimeException("Couldn't find running service info");
    }

    private void showMessage(String text) {
        if (toast == null)
            toast = Toast.makeText(this, text, Toast.LENGTH_SHORT);
        else
            toast.setText(text);

        toast.show();
        Log.i(ForegroundService.class.getSimpleName(), text); //If the user has turned off app notifications, the toast won't show, so doing this as a backup
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}