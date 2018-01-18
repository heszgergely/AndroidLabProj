package com.example.hesz.labproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by gergelyhesz on 18/01/2018.
 */


public class DownloadBgService extends Service {

    private static final String TAG = "DownloadBgService";
    NotificationManager Notifi_M;
    DownloadBgThread thread;
    Notification Notifi ;

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG,"Service started");
        Notifi_M = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        myServiceHandler handler = new myServiceHandler();
        thread = new DownloadBgThread(handler, this);
        thread.start();
        return START_STICKY;
    }



    public void onDestroy() {
        thread.stopForever();
        thread = null;
    }


    class myServiceHandler extends Handler {



        @Override
        public void handleMessage(android.os.Message msg) {

            String message = msg.obj.toString();
            Intent intent = new Intent(DownloadBgService.this, BottomNavigationActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(DownloadBgService.this, 0, intent,PendingIntent.FLAG_UPDATE_CURRENT);

            Notifi = new Notification.Builder(getApplicationContext())
                    .setContentTitle("LabProject")
                    .setContentText(message)
                    .setSmallIcon(R.mipmap.ic_launcher)
                    .setContentIntent(pendingIntent)
                    .setVibrate(new long[]{0, 500, 0, 500})
                    .build();


            Notifi.flags = Notification.FLAG_AUTO_CANCEL;

            Notifi_M.notify( 777 , Notifi);



            Vibrator vibrator = (Vibrator) getSystemService(VIBRATOR_SERVICE);
            long[] vibrationPattern = {0, 500, 50, 300};
            //-1 - don't repeat
            final int indexInPatternToRepeat = -1;
            vibrator.vibrate(vibrationPattern, indexInPatternToRepeat);


            Toast.makeText(DownloadBgService.this, message, Toast.LENGTH_LONG).show();
        }


    }
}

