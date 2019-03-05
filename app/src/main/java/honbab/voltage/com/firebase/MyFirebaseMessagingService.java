package honbab.voltage.com.firebase;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.github.arturogutierrez.Badges;
import com.github.arturogutierrez.BadgesNotSupportedException;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONObject;

import java.util.Map;

import honbab.voltage.com.tete.ChatActivity;
import honbab.voltage.com.tete.MainActivity;
import honbab.voltage.com.tete.R;
import honbab.voltage.com.tete.Statics;
import honbab.voltage.com.utils.BadgeUtil;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    /**
     * Called when message is received.
     *
     * @param remoteMessage Object representing the message received from Firebase Cloud Messaging.
     */
//    Context mActContext;
    int cnt_badge = 0;

//    public MyFirebaseMessagingService(Context mContext) {
//        this.mActContext = mContext;
//    }

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
            Log.e("abc", "notification = " + notification);
            Log.e("abc", "Notification Message getTitle: " + remoteMessage.getNotification().getTitle());
            Log.e("abc", "Notification Message Body: " + remoteMessage.getNotification().getBody());
//            Log.e("abc", "Notification Click Action: " + click_action);
            Log.e("abc", "Statics.to_id = " + Statics.to_id);
            if (Statics.to_id == null || Statics.to_id.equals("") || Statics.to_id.equals("null")) {//앱 내 채팅밖
//                sendNotification(this, notification, data);

//                Map<String, String> params = remoteMessage.getData();
                JSONObject object = new JSONObject(data);
                Log.e("abc", object.toString());
                String title = object.optString("title","");
                String actionCode = object.optString("action_code", "");
                String msg = object.optString("body", "");
                if (remoteMessage.getData().containsKey("badge")) {
                    int badge = Integer.parseInt(remoteMessage.getData().get("badge"));
                    //Log.d("notificationNUmber", ":" + badge);
//                    setBadge(getApplicationContext(), badge);
                    try {
                        Badges.setBadge(this, 1);
                    } catch (BadgesNotSupportedException e) {
                        e.printStackTrace();
                    }
//                    Prefs.putBoolean(Constant.HAS_BADGE,true);
                }
                if (!(title.equals("") && msg.equals("") && actionCode.equals(""))) {
                    createNotification(actionCode, msg, title);
                }
                else {
                    //Log.e("Notification", "Invalid Data");
                }
            } else if (Statics.to_id.equals(data.get("toId"))) {//현재 대화창

            } else {//다른 대화창
                sendNotification(this, notification, data);
            }
        }
    }

    /**
     * Create and show a custom notification containing the received FCM message.
     *
     * @param notification FCM notification payload received.
     * @param data         FCM data payload received.
     */
    private void sendNotification(Context context,
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
            intent = new Intent(this, MainActivity.class);
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



//        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//
//        // Notification Channel is required for Android O and above
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            NotificationChannel channel = new NotificationChannel(
//                    channelId, "channel_name", NotificationManager.IMPORTANCE_DEFAULT
//            );
//            channel.setDescription("channel description");
//            channel.setShowBadge(true);
//            channel.canShowBadge();
//            channel.enableLights(true);
//            channel.setLightColor(Color.RED);
//            channel.enableVibration(true);
//            channel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            channel.setVibrationPattern(new long[]{100, 200, 300, 400, 500});
//            notificationManager.createNotificationChannel(channel);
//        }
////        int numMessages = 0;
////        notificationBuilder.setNumber(++numMessages);
//        notificationManager.notify(0, notificationBuilder.build());





//        SharedPreferences settings = this.getSharedPreferences("LAUNCHER_BADGE", Context.MODE_PRIVATE);
//        int currentBdg = settings.getInt("CURRENT_BADGE", 0);
//        BadgeProviderFactory badgeFactory = new BadgeProviderFactory(context);
//        BadgeProvider badgeProvider = badgeFactory.getBadgeProvider();
//        badgeProvider.setBadge(Integer.parseInt(data.get("badge_cnt")));
//        try {
//
//        } catch (UnsupportedOperationException exception) {
//            throw new BadgesNotSupportedException();
//        }
//        Log.e("abc", "currentBdg: " + currentBdg);
//        BadgeUtil.setBadge(context, currentBdg+1);


//        loadFirebaseCntUnread(this);
        try {
            Badges.setBadge(this, 1);
        } catch (BadgesNotSupportedException e) {
            e.printStackTrace();
        }

    }

    // NEW!!
    public void createNotification(String action_code, String msg, String title) {
        String channelId = getString(R.string.default_notification_channel_id);

        Intent intent = null;
        intent = new Intent(this, ChatActivity.class);
//        intent.putExtra(Constants.ACTION_CODE, action_code);
        PendingIntent contentIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel androidChannel = new NotificationChannel(channelId, title, android.app.NotificationManager.IMPORTANCE_DEFAULT);
            // Sets whether notifications posted to this channel should display notification lights
            androidChannel.enableLights(true);
            // Sets whether notification posted to this channel should vibrate.
            androidChannel.enableVibration(true);
            // Sets the notification light color for notifications posted to this channel
            androidChannel.setLightColor(Color.GREEN);

            // Sets whether notifications posted to this channel appear on the lockscreen or not
            androidChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);
//            getManager().createNotificationChannel(androidChannel);
            Notification.Builder nb = new Notification.Builder(getApplicationContext(), channelId)
                    .setContentTitle(title)
                    .setContentText(msg)
                    .setTicker(title)
                    .setShowWhen(true)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                            R.mipmap.ic_launcher_round))
                    .setAutoCancel(true)
                    .setContentIntent(contentIntent);

//            getManager().notify(101, nb.build());

        } else {
//            try {
//
//                @SuppressLint({"NewApi", "LocalSuppress"}) android.support.v4.app.NotificationCompat.Builder notificationBuilder = new android.support.v4.app.NotificationCompat.Builder(this).setLargeIcon(BitmapFactory.decodeResource(getResources(), R.mipmap.ic_launcher))
//                        .setSmallIcon(R.mipmap.ic_launcher)
//                        .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
//                                R.mipmap.ic_launcher_round))
//                        .setContentTitle(title)
//                        .setTicker(title)
//                        .setContentText(msg)
//                        .setShowWhen(true)
//                        .setContentIntent(contentIntent)
//                        .setLights(0xFF760193, 300, 1000)
//                        .setAutoCancel(true).setVibrate(new long[]{200, 400});
//                            /*.setSound(Uri.parse("android.resource://"
//                                    + getApplicationContext().getPackageName() + "/" + R.raw.tone));*/
//
//
//                NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
//                notificationManager.notify((int) System.currentTimeMillis() /* ID of notification */, notificationBuilder.build());
//            } catch (SecurityException se) {
//                se.printStackTrace();
//            }
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

    private int loadFirebaseCntUnread(Context context) {
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
                        BadgeUtil.setBadge(context, cnt_badge);
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

                        BadgeUtil.setBadge(context, cnt_badge);
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
}