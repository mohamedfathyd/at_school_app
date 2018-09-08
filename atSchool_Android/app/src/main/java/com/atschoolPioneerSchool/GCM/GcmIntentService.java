package com.atschoolPioneerSchool.GCM;

import android.app.ActivityManager;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.media.AudioManager;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.NotificationCompat;
import android.R;
import android.support.v4.app.NotificationManagerCompat;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityMain;
import com.atschoolPioneerSchool.ActivityNotification;
import com.atschoolPioneerSchool.data.Constant;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.Student;
import com.google.android.gms.gcm.GcmListenerService;

import java.util.List;
import java.util.Random;

import static android.media.AudioManager.*;

/**
 * Created by user on 07/06/2017.
 */

public class GcmIntentService extends GcmListenerService {

    Random r = new Random();
    private static String loginUserMasterId = "";

    @Override
    public void onMessageReceived(String from, Bundle data) {

        String Sender_User_Master_Id = data.getString("Sender_User_Master_Id");
        String Receiver_User_Master_Id = data.getString("Receiver_User_Master_Id");
        String Send_Date = data.getString("Send_Date");
        String Send_Time = data.getString("Send_Time");
        String Text_Message = data.getString("Text_Message");
        String Attached_File_Name = data.getString("Attached_File_Name");
        String NotificationType = data.getString("NotificationType");
        String ext1 = data.getString("ext1");
        String ext2 = data.getString("ext2");
        String Sender_GCM = data.getString("Sender_GCM");
        String Sender_Name = data.getString("Sender_Name");
        String Attached_File_Extension = data.getString("Attached_File_Extension");


        Class toIntent = ActivityMain.class;
        String title = "Chat ";
        String body = "";
        Intent intent = new Intent(getBaseContext(), ActivityChatDetails.class);
        if (NotificationType.equals("Chat")) {

            title = Sender_Name;
            body = Text_Message;

            intent = new Intent(getBaseContext(), ActivityChatDetails.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);


            Student friend = new Student(Sender_Name, 1, Integer.valueOf(Sender_User_Master_Id));
            friend.GCM_Token = Sender_GCM;

            intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
            // startActivity(intent);

            //check if app is running
            if (isAppRunning(getBaseContext(), Sender_User_Master_Id)) {

               /* Intent notificationIntent = new Intent(getBaseContext(), ActivityChatDetails.class);
                notificationIntent.setAction(Long.toString(System.currentTimeMillis()));
                PendingIntent.getActivity(getBaseContext(), 0, notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);*/

                // startActivity(intent);

                if (Constant.CurrentActivityChatDetails != null) {

                    MessageDetails objMsg = new MessageDetails(0, Send_Date + "  " + Send_Time,
                            friend, Text_Message, loginUserMasterId.equals(Sender_User_Master_Id) ? true : false
                            , Attached_File_Name, Attached_File_Extension);


                    Constant.CurrentActivityChatDetails.AddNewItemFromNotificationChat(objMsg);
                }

                return;

            }

        } else //NotificationType.equals("Notification")
        {
            title = getString(com.atschoolPioneerSchool.R.string.School_Notification);
            body = Text_Message;
            Student friend = new Student("Notifications", 1, 2625);
            intent = new Intent(getBaseContext(), ActivityNotification.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

            intent.putExtra(ActivityChatDetails.KEY_FRIEND, friend);
            // startActivity(intent);

        }

        PendingIntent pIntent = PendingIntent.getActivity(getBaseContext(), (int) System.currentTimeMillis(), intent, 0);


        NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        Uri uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);

        NotificationCompat.Builder mBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.drawable.btn_star_big_on)
                .setContentTitle(title)
                .setContentIntent(pIntent)
                .setAutoCancel(true)

                .setContentText(body);

        mBuilder.setSound(Settings.System.DEFAULT_NOTIFICATION_URI);

        mBuilder.setDefaults(Notification.DEFAULT_ALL);
        notificationManager.notify(r.nextInt(), mBuilder.build());

        //asdfrwerwerwer


        //werwerwerwerwer






    /*    SharedPreferences preferences = getBaseContext().getSharedPreferences("atSchool", MODE_PRIVATE);
        int counter = preferences.getInt("badgeCounter", 0);
        preferences.edit().putInt("badgeCounter", (counter + 1)).commit();
        BadgeHelper.setBadge(getBaseContext(), (counter + 1));
        */

        SharedPreferences preferences = getBaseContext().getSharedPreferences("atSchool", MODE_PRIVATE);
        int counter = preferences.getInt("badgeCounter", 0);
        preferences.edit().putInt("badgeCounter", 0).commit();
        BadgeHelper.setBadge(getBaseContext(), 0);
    }

    public static boolean isAppRunning(Context context, String prmSender_User_Master_Id) {
        // check with the first task(task in the foreground)
        // in the returned list of tasks
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningTaskInfo> services = activityManager.getRunningTasks(Integer.MAX_VALUE);

        if (services.get(0).topActivity.getPackageName().toString().equalsIgnoreCase(context.getPackageName().toString())) {
            //check if opend the chat activity
            if (services.get(0).topActivity.getClassName().equals("com.atschoolPioneerSchool.ActivityChatDetails")) {

                //check if opend the same contact in chat
                SharedPreferences sharedpref = context.getSharedPreferences("atSchool", Context.MODE_PRIVATE);
                loginUserMasterId = sharedpref.getString("USER_MASTER_Id", "").trim();

                String Last_Chat_Receiver_User_Master_Id = sharedpref.getString("Last_Chat_Receiver_User_Master_Id", "").toString();

                if (Last_Chat_Receiver_User_Master_Id.equals(prmSender_User_Master_Id)) {
                    return true;
                }
            }
        }
        return false;
    }


}