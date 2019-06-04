package honbab.voltage.com.firebase;

import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.MainActivity2;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;

import static honbab.voltage.com.utils.BadgeUtil.setBadge;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
//    Context mActContext;
//    int cnt_badge = 0;
//    private NotificationManager mManager;
    private static final int NOTIFICATION_MAX_CHARACTERS = 30;

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        // TODO(developer): Handle FCM messages here.
        // If the application is in the foreground handle both data and notification messages here.
        // Also if you intend on generating your own notifications as a result of a received FCM
        // message, here is where that should be initiated. See sendNotification method below.

        Map<String, String> data = remoteMessage.getData();
//        for (Map.Entry<String,String> entry : data.entrySet()) {
//            String key = entry.getKey();
//            String value = entry.getValue();
//            // do stuff
//            Log.e("abc", "key = " + key + " , value = " + value);
//        }

        Log.e("abc", "remoteMessage.getMessageId = " + remoteMessage.getMessageId());
        Log.e("abc", "remoteMessage.getData().size() = " + remoteMessage.getData().size());
        if (remoteMessage.getData().size() > 0) {
            Log.e("abc", "Message data payload: " + remoteMessage.getData());

            if (/* Check if data needs to be processed by long running job */ true) {

                // For long-running tasks (10 seconds or more) use Firebase Job Dispatcher.
//                scheduleJob();
            } else {
                // Handle message within 10 seconds
//                handleNow();
            }
        }

        Log.e("abc", "From: " + remoteMessage.getFrom());
        //This will give you the Text property in the curl request(Sample Message):

        //This is where you get your click_action
        if (remoteMessage.getNotification() != null) {
            RemoteMessage.Notification notification = remoteMessage.getNotification();

//            String message = remoteMessage.getNotification().getBody();
//            String click_action = remoteMessage.getNotification().getClickAction();
//            Log.e("abc", "notification = " + notification);
            Log.e("abc", "notification getTitle: " + notification.getTitle());
            Log.e("abc", "notification Body: " + notification.getBody());
//            Log.e("abc", "Notification Click Action: " + click_action);
            Log.e("abc", "Statics.to_id = " + Statics.to_id);
            if (Statics.to_id == null || Statics.to_id.equals("") || Statics.to_id.equals("null")) {
                //앱 내 채팅밖
//                sendNotification(this, notification, data);
                Log.e("abc", "data = " + data.toString());
//                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(data);
                Log.e("abc", object.toString());
//                String title = object.optString("title","");
//                String actionCode = object.optString("action_code", "");
//                String msg = object.optString("body", "");

                String title = notification.getTitle();
                String msg = notification.getBody();
                Log.e("abc", "title = " + title + ", "+ msg);

                if (remoteMessage.getData().containsKey("badge")) {
                    int badge = Integer.parseInt(remoteMessage.getData().get("badge"));
                    //Log.d("notificationNUmber", ":" + badge);
                    setBadge(getApplicationContext(), badge);
//                    Prefs.putBoolean(Constant.HAS_BADGE,true);
//                    try {
//                        Badges.setBadge(this, 1);
//                    } catch (BadgesNotSupportedException e) {
//                        e.printStackTrace();
//                    }
                }
//                if (!(title.equals("") && msg.equals("") && actionCode.equals(""))) {
//                if (!(title.equals("") && msg.equals(""))) {
//                    Log.e("abc", "createNotification = ");
//                    createNotification(null, msg, title);
//                }
//                else {
//                    //Log.e("Notification", "Invalid Data");
//                }

//                startForegroundService();
                Log.d("abc", "Message Notification Body: " + remoteMessage.getNotification().getBody());
                sendNotification(this, notification);

            } else if (Statics.to_id.equals(data.get("toId"))) {//현재 대화창

            } else {//다른 대화창
//                sendNotification(this, notification, data);
            }
        }
    }

//    private void startForegroundService() {
//        Intent notificationIntent = new Intent(this, MainActivity.class);
//        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);
//
//        RemoteViews remoteViews = new RemoteViews(getPackageName(), R.layout.notification_service);
//
//        NotificationCompat.Builder builder;
//        if (Build.VERSION.SDK_INT >= 26) {
//            String CHANNEL_ID = "snwodeer_service_channel";
//            NotificationChannel channel = new NotificationChannel(CHANNEL_ID,
//                    "SnowDeer Service Channel",
//                    NotificationManager.IMPORTANCE_DEFAULT);
//
//            ((NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE))
//                    .createNotificationChannel(channel);
//
//            builder = new Builder(this, CHANNEL_ID);
//        } else {
//            builder = new Builder(this);
//        }
//        builder.setSmallIcon(R.mipmap.ic_launcher)
//                .setContent(remoteViews)
//                .setContentIntent(pendingIntent);
//
//        startForeground(1, builder.build());
//    }



    private void sendNotification(Context mContext, RemoteMessage.Notification notification) {
//        RemoteMessage.Notification notification =

        Intent intent = new Intent(this, MainActivity2.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Create the pending intent to launch the activity
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0 /* Request code */, intent, PendingIntent.FLAG_ONE_SHOT);

//        String author = data.get(JSON_KEY_AUTHOR);
//        String message = data.get(JSON_KEY_MESSAGE);
        String author = notification.getTitle();
        String message = notification.getBody();

        // If the message is longer than the max number of characters we want in our
        // notification, truncate it and add the unicode character for ellipsis
        if (message.length() > NOTIFICATION_MAX_CHARACTERS) {
            message = message.substring(0, NOTIFICATION_MAX_CHARACTERS) + "\u2026";
        }

        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentTitle(String.format(getString(R.string.notification_message), author))
                .setContentTitle(notification.getTitle())
                .setContentText(message)
                .setAutoCancel(true)
                .setSound(defaultSoundUri)
                .setContentIntent(pendingIntent);

//        NotificationManager notificationManager = (NotificationManager) mContext.getSystemService(Context.NOTIFICATION_SERVICE);
//        notificationManager.notify(0 /* ID of notification */, notificationBuilder.build());
    }




    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data         FCM data payload received.
     */
    private void sendNotificationxxxx(Context context,
                                  RemoteMessage.Notification notification,
                                  Map<String, String> data) {

        Bitmap icon = BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher);
//        Drawable icon = new BitmapDrawable(getResources(), icon2);
        Log.e("abc", "notification.getClickAction() = " + notification.getClickAction());

        Intent intent;
        if (notification.getClickAction().equals("ChatActivity")) {
            intent = new Intent(this, ChatActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            intent.putExtra("toId", data.get("toId"));
        } else {
            intent = new Intent(this, MainActivity2.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        }
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);//PendingIntent.FLAG_UPDATE_CURRENT);

//        Log.e("abc", "data.get(\"badge_cnt\") = " + data.get("badge_cnt"));
        Log.e("abc", "notification.getTitle() = " + notification.getTitle());
        Log.e("abc", "notification.getBody() = " + notification.getBody());
        String channelId = getString(R.string.default_notification_channel_id);
        Uri defaultSoundUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this, channelId)
                .setVisibility(Notification.VISIBILITY_PUBLIC)
                .setContentTitle(notification.getTitle())
                .setContentText(notification.getBody())
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setContentInfo(notification.getTitle())
                .setSmallIcon(R.mipmap.ic_launcher)//R.mipmap.ic_launcher
//                .setSound(defaultSoundUri)
//                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setSound(Uri.parse("android.resource://" + context.getPackageName() + "/" + R.raw.alarm_godmuk1))
                .setLargeIcon(icon)
                .setPriority(Notification.PRIORITY_MAX)
                .setColor(Color.WHITE)
//                .setLights(Color.RED, 1000, 300)
//                .setDefaults(Notification.DEFAULT_VIBRATE);
//                .setDefaults(Notification.DEFAULT_ALL)
                .setNumber(Integer.parseInt(data.get("badge_cnt")));

//        notification = new NotificationCompat.Builder(getApplicationContext())
//                .setContentTitle("hahaha")
//                .setTicker("Runnndini")
//                .setContentText("Click")
//                .setSmallIcon(R.mipmap.ic_launcher)
//                .setContentIntent(pendingIntent)
//                .setOngoing(true).build();

        if (Statics.to_id == null || Statics.to_id.equals("")) {//앱 내 채팅 밖
//            sendNotification(this, notification, data);
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);

            startForeground(1, notificationBuilder.build());
//            startForeground(Constants.NOTIFICATION_ID_FOREGROUND_SERVICE, notificationBuilder.build());
        } else if (Statics.to_id.equals(data.get("toId"))) {
            //현재 대화창
            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        } else {
            //다른 채팅방
//            sendNotification(this, notification, data);
//            notificationBuilder.setDefaults(Notification.DEFAULT_ALL);
//            notificationBuilder.setPriority(Notification.PRIORITY_MAX);
        }

        NotificationCompat.InboxStyle inboxStyle = new NotificationCompat.InboxStyle();
        String[] events = new String[6];
        // Sets a title for the Inbox in expanded layout
        inboxStyle.setBigContentTitle(notification.getTitle());
        // Moves events into the expanded layout
        for (int i = 0; i < events.length; i++) {
            inboxStyle.addLine(events[i]);
        }
        // Moves the expanded layout object into the notification object.
        notificationBuilder.setStyle(inboxStyle);

        try {
            Badges.setBadge(this, 1);
        } catch (BadgesNotSupportedException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onNewToken(String token) {
        // If you want to send messages to this application instance or
        // manage this apps subscriptions on the server side, send the
        // Instance ID token to your app server.
        Log.e("abc", "onNewToken = " + token);
        sendRegistrationToServer(token);
    }

    private void sendRegistrationToServer(String token) {
        // TODO: Implement this method to send token to your app server.
    }

    /*
    private int loadFirebaseCntUnread(Context mContext) {
        Log.e("abc", "loadFirebaseCntUnread = " + cnt_badge);
//        cnt_badge = 0;
//        HashMap<String, Integer> chatListHash = new HashMap<>();

        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("user-messages").child(Statics.my_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                String toId = dataSnapshot.getKey();
                Log.e("abc", "messages = " + cnt_badge);

                mDatabase.child("user-messages").child(Statics.my_id).child(toId).limitToLast(50).addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        Object o = dataSnapshot.getValue();
                        int plus = Integer.parseInt(o.toString());
                        Log.e("abc", "plus = " + plus);

                        if (plus > 0)
                            cnt_badge++;

                        Log.e("abc", "badge_cnt = " + cnt_badge);
                        setBadge(mContext, cnt_badge);
                    }

                    @Override
                    public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {
                        if (cnt_badge > 0)
                            cnt_badge = 0;

                        Log.e("abc", "onChildChanged = " + cnt_badge);
                        Object o = dataSnapshot.getValue();
                        int plus = Integer.parseInt(o.toString());
                        Log.e("abc", "onChildChanged plus = " + plus);

                        if (plus > 0)
                            cnt_badge++;

                        setBadge(mContext, cnt_badge);
                    }

                    @Override
                    public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot dataSnapshot, @Nullable String s) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

        return cnt_badge;
    }
    */

//    private NotificationManager getManager() {
//        if (mManager == null) {
//            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//        }
//        return mManager;
//    }
}