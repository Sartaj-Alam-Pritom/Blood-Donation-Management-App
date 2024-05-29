package com.example.blooddonationapp.ui;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.IBinder;
import android.util.Log;

import androidx.annotation.Nullable;
import androidx.core.app.NotificationCompat;

import com.example.blooddonationapp.R;

public class CountdownService extends Service {

    private static final int NOTIFICATION_ID = 1;
    private static final String NOTIFICATION_CHANNEL_ID = "countdown_channel";
    private static final String NOTIFICATION_CHANNEL_NAME = "Countdown Notification Channel";

    private CountDownTimer countDownTimer;
    private OnTickListener onTickListener;

    @Override
    public void onCreate() {
        super.onCreate();
        createNotificationChannel();
        registerReceiver(countdownReceiver, new IntentFilter("countdown_update"));
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        startForeground(NOTIFICATION_ID, createNotification());

        long countdownDuration = intent.getLongExtra("countdown_duration", 0);

        countDownTimer = new CountDownTimer(countdownDuration, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                sendCountdownUpdate(millisUntilFinished);
            }

            @Override
            public void onFinish() {
                // Countdown finished, send a broadcast
                Intent countdownFinishedIntent = new Intent("countdown_finished");
                sendBroadcast(countdownFinishedIntent);
                stopForeground(true);
                stopSelf();
            }
        }.start();

        return START_NOT_STICKY;
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(
                    NOTIFICATION_CHANNEL_ID,
                    NOTIFICATION_CHANNEL_NAME,
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(channel);
            }
        }
    }

    private Notification createNotification() {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                .setContentTitle("Countdown Timer")
                .setContentText("Countdown is running")
                .setSmallIcon(R.drawable.logo5);

        return builder.build();
    }

    private void sendCountdownUpdate(long millisUntilFinished) {
        Intent intent = new Intent("countdown_update");
        intent.putExtra("countdown_time_left", millisUntilFinished);
        sendBroadcast(intent);
    }

    public void setOnTickListener(OnTickListener listener) {
        this.onTickListener = listener;
    }

    public interface OnTickListener {
        void onTick(long millisUntilFinished);

        void onFinish();
    }

    private BroadcastReceiver countdownReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null) {
                if (intent.getAction().equals("countdown_update")) {
                    long timeLeft = intent.getLongExtra("countdown_time_left", 0);
                    if (onTickListener != null) {
                        onTickListener.onTick(timeLeft);
                    }
                } else if (intent.getAction().equals("countdown_finished")) {
                    if (onTickListener != null) {
                        onTickListener.onFinish();
                    }
                }
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(countdownReceiver);
    }
}
