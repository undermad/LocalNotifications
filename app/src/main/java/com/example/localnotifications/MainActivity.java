package com.example.localnotifications;

import android.Manifest;
import android.app.*;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Icon;
import android.os.Build;
import android.widget.Button;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;
import org.jetbrains.annotations.NotNull;

import java.util.Calendar;

@RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
public class MainActivity extends AppCompatActivity {

    private Button button, buttonEveryDayNoti;
    private ConstraintLayout constraintLayout;
    public final String CHANNEL_ID = "1";
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = findViewById(R.id.button);
        buttonEveryDayNoti = findViewById(R.id.buttonEveryDayNoti);
        constraintLayout = findViewById(R.id.con);


        buttonEveryDayNoti.setOnClickListener(v -> {
            /*
            The POST_NOTIFICATION permission requires API 33 and above.
            we should handle this with an if statement
            */
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

                if (ContextCompat.checkSelfPermission(
                        MainActivity.this,
                        Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

                    /*
                    In an educational UI, explain to the user why your app requires this
                    permission for a specific feature to behave as expected, and what
                    features are disabled if it's declined. In this UI, include a
                    "cancel" or "no thanks" button that lets the user continue
                    using your app without granting the permission. So we show a snackbar message for this.
                    If you want you can create dialog message or bottom sheet dialog
                    */
                    if (shouldShowRequestPermissionRationale(Manifest.permission.POST_NOTIFICATIONS)) {

                        Snackbar.make(
                                constraintLayout,
                                "Please, allow the permission for notifications",
                                Snackbar.LENGTH_LONG
                        ).setAction("Allow", v1 -> {
                            ActivityCompat.requestPermissions(
                                    MainActivity.this,
                                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                                    2);
                        }).show();
                    } else {
                        ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 2);
                    }

                } else {
                    setNotificationTime();
                }

            } else {
                setNotificationTime();
            }


        });


        button.setOnClickListener(v -> {
            counter++;
            button.setText("" + counter);

            if (counter % 5 == 0) {
                startNotification();
            }
        });


    }

    public void startNotification() {

        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    1);
        } else {

            // OPEN MAIN ACTIVITY AFTER CLICKING ON NOTIFICATION
            Intent intent = new Intent(this, MainActivity.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(
                    this,
                    0,
                    intent,
                    PendingIntent.FLAG_IMMUTABLE);


            // ADD ACTION BUTTON TO NOTIFICATION
            Intent actionIntent = new Intent(this, Receiver.class);
            actionIntent.putExtra("toast", "This is notification toast message!");
            PendingIntent actionPendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    actionIntent,
                    PendingIntent.FLAG_IMMUTABLE
            );
            Notification.Action action = new Notification.Action.Builder(
                    Icon.createWithResource(
                            this,
                            R.drawable.baseline_notifications_24),
                    "Toast Message",
                    actionPendingIntent
            ).build();

            // ADD DISMISS ACTION BUTTON TO NOTIFICATION
            Intent dismissIntent = new Intent(this, ReceiverDismiss.class);
            PendingIntent dissmissPendingIntent = PendingIntent.getBroadcast(
                    this,
                    0,
                    dismissIntent,
                    PendingIntent.FLAG_IMMUTABLE);
            Notification.Action dismissAction = new Notification.Action.Builder(
                    Icon.createWithResource(
                            this,
                            R.drawable.baseline_notifications_24),
                    "Dismiss",
                    dissmissPendingIntent
            ).build();

            // ADD CUSTOM IMAGE / TEXT
            Bitmap icon = BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher_foreground);
            String longText = getResources().getString(R.string.long_text);


            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,
                    "firstNotification",
                    NotificationManager.IMPORTANCE_DEFAULT);

            NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
            manager.createNotificationChannel(channel);

            Notification.Builder builder = new Notification.Builder(MainActivity.this, CHANNEL_ID);
            builder.setSmallIcon(R.drawable.baseline_notifications_24)
                    .setContentTitle("First Notification!")
                    .setContentText("Notification Text")
                    .setPriority(Notification.PRIORITY_DEFAULT)
                    .setContentIntent(pendingIntent)
                    .addAction(action)
                    .addAction(dismissAction)
                    .setColor(Color.BLUE)
                    .setLargeIcon(icon)
                    .setAutoCancel(true)
//                    .setStyle(new Notification.BigPictureStyle().bigPicture(icon))
                    .setStyle(new Notification.BigTextStyle().bigText(getResources().getString(R.string.long_text)));


            NotificationManagerCompat compat = NotificationManagerCompat.from(MainActivity.this);
            compat.notify(1, builder.build());
        }

    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull @NotNull String[] permissions, @NonNull @NotNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 1 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            startNotification();
        }
        if (requestCode == 2 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            setNotificationTime();
        }

    }

    public void setNotificationTime() {

        if (ContextCompat.checkSelfPermission(
                MainActivity.this,
                Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{Manifest.permission.POST_NOTIFICATIONS},
                    2);
        } else {

            Calendar calendar = Calendar.getInstance();
            int currentHour = calendar.get(Calendar.HOUR);
            int currentMinute = calendar.get(Calendar.MINUTE);

            MaterialTimePicker timePicker = new MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_12H)
                    .setHour(currentHour)
                    .setMinute(currentMinute)
                    .setTitleText("Set notification time")
                    .build();

            timePicker.show(getSupportFragmentManager(), "1");

            timePicker.addOnPositiveButtonClickListener(v -> {

                calendar.set(Calendar.HOUR, timePicker.getHour());
                calendar.set(Calendar.MINUTE, timePicker.getMinute());
                calendar.set(Calendar.SECOND, 0);
                calendar.set(Calendar.MILLISECOND, 0);

                Intent intent = new Intent(getApplicationContext(), NotificationReceiver.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(
                        getApplicationContext(),
                        100,
                        intent,
                        PendingIntent.FLAG_IMMUTABLE);

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                alarmManager.setRepeating(
                        AlarmManager.RTC_WAKEUP,
                        calendar.getTimeInMillis(),
                        AlarmManager.INTERVAL_DAY,
                        pendingIntent);
            });
        }

    }


}