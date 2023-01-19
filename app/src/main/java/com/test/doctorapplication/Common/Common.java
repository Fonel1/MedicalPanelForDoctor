package com.test.doctorapplication.Common;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.text.TextUtils;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.test.doctorapplication.Model.AppointmentInformation;
import com.test.doctorapplication.Model.Clinic;
import com.test.doctorapplication.Model.Doctor;
import com.test.doctorapplication.Model.MyToken;
import com.test.doctorapplication.Model.SentTestInformation;
import com.test.doctorapplication.R;
import com.test.doctorapplication.Service.MyFCMService;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import io.paperdb.Paper;

public class Common {
    public static final int DATA_SLOT_TOTAL = 16;
    public static final Object DISABLE_TAG = "DISABLE";
    public static final String LOG_KEY = "LOG";
    public static final String CITY_KEY = "STATE";
    public static final String CLINIC_KEY = "CLINIC";
    public static final String DOCTOR_KEY = "DOCTOR";
    public static final String TITLE_KEY = "title";
    public static final String CONTENT_KEY = "content";
    public static final int MAX_NOTIFICATION_PER_LOAD = 10;
    public static String city = "";
    public static Clinic currentClinic;
    public static Doctor currentDoctor;
    public static SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd_MM_yyyy");
    public static Calendar currentData = Calendar.getInstance();
    public static AppointmentInformation currentAppointmentInfo;
    public static String test = "";
    public static String currentTest;

    public static String convertDataSlotToString(int slot) {
        switch (slot) {
            case 0:
                return "9:00-9:30";
            case 1:
                return "9:30-10:00";
            case 2:
                return "10:00-10:30";
            case 3:
                return "10:30-11:00";
            case 4:
                return "11:00-11:30";
            case 5:
                return "11:30-12:00";
            case 6:
                return "12:00-12:30";
            case 7:
                return "12:30-13:00";
            case 8:
                return "13:00-13:30";
            case 9:
                return "13:30-14:00";
            case 10:
                return "14:00-14:30";
            case 11:
                return "14:30-15:00";
            case 12:
                return "15:00-15:30";
            case 13:
                return "15:30-16:00";
            case 14:
                return "16:00-16:30";
            case 15:
                return "16:30-17:00";
            default:
                return "zamkniete";
        }
    }

    public static void showNotification(Context context, int noti_id, String title, String content, Intent intent) {

        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,
                    noti_id,
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID = "MedPan_channel_1";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "MedPan", NotificationManager.IMPORTANCE_DEFAULT);

            notificationChannel.setDescription("Aplikacja dla Lekarza");
            notificationChannel.enableLights(true);
            notificationChannel.enableVibration(false);


            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, NOTIFICATION_CHANNEL_ID);

        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(false)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher));

        if (pendingIntent != null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();

        notificationManager.notify(noti_id, notification);

    }

    public enum TOKEN_TYPE{
        PATIENT,
        DOCTOR
    }

    public static void updateToken(Context context, String s) {
        Paper.init(context);
        String user = Paper.book().read(Common.LOG_KEY);
        if (user != null)
        {
            if (!TextUtils.isEmpty(user))
            {
                MyToken myToken = new MyToken();
                myToken.setToken(s);
                myToken.setTokenType(TOKEN_TYPE.DOCTOR);
                myToken.setUserName(user);

                FirebaseFirestore.getInstance()
                        .collection("Tokens")
                        .document(user)
                        .set(myToken)
                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {

                            }
                        });
            }
        }
    }
}