package group7.tcss450.uw.edu.chatapp.Utils.firebase;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import group7.tcss450.uw.edu.chatapp.Activities.ChatListActivity;
import group7.tcss450.uw.edu.chatapp.R;

public class MyFirebaseMessagingService extends FirebaseMessagingService {
    private static final String TAG = "MyFirebaseMessagingService";

    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);
        Log.d(TAG, "From: " + remoteMessage.getFrom());
        if(remoteMessage.getData().size() > 0) {
            Log.d(TAG, "Message data: " + remoteMessage.getData());
        }

        if (remoteMessage.getNotification() != null) {
            Log.d(TAG, "Message body: " + remoteMessage.getNotification().getBody());
            sendNotification(remoteMessage.getNotification().getBody());
        }
    }


    private void sendNotification(String body) {
        Intent intent = new Intent(this, ChatListActivity.class);
        intent.setFlags((Intent.FLAG_ACTIVITY_CLEAR_TOP));
        PendingIntent pendingIntent = PendingIntent
                .getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT);
        Uri notificationSound = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
        NotificationCompat.Builder notifiBuilder =  new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setContentTitle("Firebase Cloud Messaging")
                .setAutoCancel(true)
                .setContentText(body)
                .setContentIntent(pendingIntent)
                .setSound(notificationSound);
        NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(0, notifiBuilder.build());
    }
}
