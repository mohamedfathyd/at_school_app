package com.atschoolPioneerSchool.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.content.res.TypedArray;
import android.util.DisplayMetrics;
import android.util.Log;

import com.atschoolPioneerSchool.ActivityChatDetails;
import com.atschoolPioneerSchool.ActivityMain;
import com.atschoolPioneerSchool.R;
import com.atschoolPioneerSchool.model.Channel;
import com.atschoolPioneerSchool.model.Student;
import com.atschoolPioneerSchool.model.Friend_photos;
import com.atschoolPioneerSchool.model.ItemModel;
import com.atschoolPioneerSchool.model.MessageDetails;
import com.atschoolPioneerSchool.model.News;
import com.atschoolPioneerSchool.model.Student;

import java.sql.Ref;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Random;

@SuppressWarnings("ResourceType")
public class Constant {

    public static ActivityChatDetails CurrentActivityChatDetails;
    public static boolean runFirstOne = true;
    public static String USER_TYPE_Id = "";
    public static String AccountName = "";
    public static String InterfaceLang = "";

    public static Student SelectedStudent;
    public static List<Student> StudentList = new ArrayList<>();
    public static String[] VideosURL = new String[]{};
    public static String[] IMAGES_Description = new String[]{};
    public static String[] IMAGES_DescriptionA = new String[]{};
    public static String[] IMAGES = new String[]{
            // Heavy images
            "https://lh5.googleusercontent.com/-7qZeDtRKFKc/URquWZT1gOI/AAAAAAAAAbs/hqWgteyNXsg/s1024/Another%252520Rockaway%252520Sunset.jpg",
            "https://lh3.googleusercontent.com/--L0Km39l5J8/URquXHGcdNI/AAAAAAAAAbs/3ZrSJNrSomQ/s1024/Antelope%252520Butte.jpg",
            "https://lh6.googleusercontent.com/-8HO-4vIFnlw/URquZnsFgtI/AAAAAAAAAbs/WT8jViTF7vw/s1024/Antelope%252520Hallway.jpg",
            "https://lh4.googleusercontent.com/-WIuWgVcU3Qw/URqubRVcj4I/AAAAAAAAAbs/YvbwgGjwdIQ/s1024/Antelope%252520Walls.jpg",
            "https://lh6.googleusercontent.com/-UBmLbPELvoQ/URqucCdv0kI/AAAAAAAAAbs/IdNhr2VQoQs/s1024/Apre%2525CC%252580s%252520la%252520Pluie.jpg",
            "https://lh3.googleusercontent.com/-s-AFpvgSeew/URquc6dF-JI/AAAAAAAAAbs/Mt3xNGRUd68/s1024/Backlit%252520Cloud.jpg",
            "https://lh5.googleusercontent.com/-bvmif9a9YOQ/URquea3heHI/AAAAAAAAAbs/rcr6wyeQtAo/s1024/Bee%252520and%252520Flower.jpg",

    };

    public static void ChangeLanguage(Context ctx) {

        SharedPreferences sharedpref = ctx.getSharedPreferences("atSchool", Context.MODE_PRIVATE);
        String lang = sharedpref.getString("switchLang", "").trim();
        Locale myLocale = new Locale(lang);

        Resources res = ctx.getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();

        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        conf.setLayoutDirection(myLocale);

    }

    public static boolean isArabic = false;

    public static float getAPIVerison() {

        Float f = null;
        try {
            StringBuilder strBuild = new StringBuilder();
            strBuild.append(android.os.Build.VERSION.RELEASE.substring(0, 2));
            f = new Float(strBuild.toString());
        } catch (NumberFormatException e) {
            Log.e("", "erro ao recuperar a versão da API" + e.getMessage());
        }

        return f.floatValue();
    }

    private static Random rnd = new Random();

    public static String formatTime(long time) {
        // income time
        Calendar date = Calendar.getInstance();
        date.setTimeInMillis(time);

        // current time
        Calendar curDate = Calendar.getInstance();
        curDate.setTimeInMillis(System.currentTimeMillis());

        SimpleDateFormat dateFormat = null;
        if (date.get(Calendar.YEAR) == curDate.get(Calendar.YEAR)) {
            if (date.get(Calendar.DAY_OF_YEAR) == curDate.get(Calendar.DAY_OF_YEAR)) {
                dateFormat = new SimpleDateFormat("h:mm a", Locale.US);
            } else {
                dateFormat = new SimpleDateFormat("MMM d", Locale.US);
            }
        } else {
            dateFormat = new SimpleDateFormat("MMM yyyy", Locale.US);
        }
        return dateFormat.format(time);
    }

    //get at school main menu items
    public static List<ItemModel> getItemMainMenu(Context ctx) {


        List<ItemModel> items = new ArrayList<>();


        //fill main menu
        TypedArray img_c_f = ctx.getResources().obtainTypedArray(R.array.item_atschool_menu_Images);
        String[] name_f = ctx.getResources().getStringArray(R.array.str_atschool_menu);
        String[] description_f = ctx.getResources().getStringArray(R.array.str_atschool_menu_description);
        List<Integer> img_mix = mixImgC(img_c_f);

        SharedPreferences sharedpref = ctx.getSharedPreferences("atSchool", Context.MODE_PRIVATE);

        String ViewReceiptVoucherInMobile = sharedpref.getString("ViewReceiptVoucherInMobile", "");
        String ViewAccountStatementInMobile = sharedpref.getString("ViewAccountStatementInMobile", "");
        String ViewMaintenanceInMobile = sharedpref.getString("ViewMaintenanceInMobile", "");
        String ViewCallSonsInMobile = sharedpref.getString("ViewCallSonsInMobile", "");
        String ViewNewsInMobile = sharedpref.getString("ViewNewsInMobile", "");
        String ViewBussesInMobile = sharedpref.getString("ViewBussesInMobile", "");
        String ViewCommunicationInMobile = sharedpref.getString("ViewCommunicationInMobile", "");
        String ViewStudentsInMobile = sharedpref.getString("ViewStudentsInMobile", "");
        String ViewImageGalleryInMobile = sharedpref.getString("ViewImageGalleryInMobile", "");
        String ViewSuggestionInMobile = sharedpref.getString("ViewSuggestionInMobile", "");
        String ViewFaceBookInMobile = sharedpref.getString("ViewFaceBookInMobile", "");
        String ViewYouTubeInMobile = sharedpref.getString("ViewYouTubeInMobile", "");
        String ViewWebsiteInMobile = sharedpref.getString("ViewWebsiteInMobile", "");
        String ViewNotificationInMobile = sharedpref.getString("ViewNotificationInMobile", "");
        String ViewComplaintInMobile = sharedpref.getString("ViewComplaintInMobile", "");
        String ViewBusLocationInMobile = sharedpref.getString("ViewBusLocationInMobile", "");
        String ViewMarefahInMobile = sharedpref.getString("ViewMarefahInMobile", "");

        String ViewPicarsImagesInMobile = sharedpref.getString("ViewPicarsImagesInMobile", "");
        String Use__Bus_Assistant = sharedpref.getString("Use__Bus_Assistant", "");


        if (ViewStudentsInMobile.equals("1")) {
            //fill student data
            for (int i = 0; i < StudentList.size(); i++) {
                if (StudentList.get(i).StudentId > 0) {

                    ItemModel item = new ItemModel(StudentList.get(i).StudentId, img_mix.get(16), StudentList.get(i).StuName, 9999
                            , ctx.getString(R.string.menu_Main), getRandomLikes(), StudentList.get(i).ClassName + ' ' + StudentList.get(i).SectionName);
                    item.StudentImageName = StudentList.get(i).StudentImageName;
                    item.isKid = true;
                    items.add(item);
                }
            }
        }

        for (int i = 0; i < name_f.length; i++) {

            ItemModel item = new ItemModel(Long.parseLong("1" + i), img_mix.get(i), name_f[i], 9999, ctx.getString(R.string.menu_Main), getRandomLikes(), description_f[i]);

            if (name_f[i].equals("News") || name_f[i].equals("الأخبار")) {

                if (ViewNewsInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Buses") || name_f[i].equals("الباصات")) {

                if (ViewBussesInMobile.equals("1")) {
                    if (Use__Bus_Assistant.equals("1")) {
                        items.add(item);
                    }
                }

            } else if (name_f[i].equals("Bus Location") || name_f[i].equals("موقع الباص")) {

                if (ViewBusLocationInMobile.equals("1")) {
                    if (Constant.StudentList.size() > 0) {
                        if (Constant.StudentList.get(0).StudentId > 0) {
                            items.add(item);
                        }
                    }
                }

            } else if (name_f[i].equals("M3refah") || name_f[i].equals("معرفه")) {

                if (ViewMarefahInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Communication") || name_f[i].equals("التواصل")) {

                if (ViewCommunicationInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Images Gallery") || name_f[i].equals("معرض الصور")) {

                if (ViewImageGalleryInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Suggestion") || name_f[i].equals("إقتراح")) {

                if (ViewSuggestionInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Facebook") || name_f[i].equals("الفيسبوك")) {

                if (ViewFaceBookInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("YouTube") || name_f[i].equals("اليوتيوب")) {

                if (ViewYouTubeInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Website") || name_f[i].equals("الموقع الكتروني")) {

                if (ViewWebsiteInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Notifications") || name_f[i].equals("التنبيهات")) {

                if (ViewNotificationInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Complaint") || name_f[i].equals("شكوى")) {

                if (ViewComplaintInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Receipt Voucher") || name_f[i].equals("سندات القبض")) {

                if (ViewReceiptVoucherInMobile.equals("1")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Account Statement") || name_f[i].equals("كشف حساب")) {

                if (ViewAccountStatementInMobile.equals("1")) {
                    items.add(item);
                }

            } else if ((name_f[i].equals("Maintenance") || name_f[i].equals("الصيانه"))) {

                if (ViewMaintenanceInMobile.equals("1") && !USER_TYPE_Id.equals("5")) {
                    items.add(item);
                }

            } else if (name_f[i].equals("Call your Sons") || name_f[i].equals("إستدعاء الأبناء")) {

                if (ViewCallSonsInMobile.equals("1")) {
                    items.add(item);
                }

            } else {

                items.add(item);
            }
        }
        return items;
    }


    public static List<MessageDetails> getMessageDetailsData(Context ctx, Student friend) {
        List<MessageDetails> items = new ArrayList<>();
        String s_date[] = ctx.getResources().getStringArray(R.array.message_details_date);
        String s_content[] = ctx.getResources().getStringArray(R.array.message_details_content);

        items.add(new MessageDetails(0, s_date[0], friend, s_content[0], false));
        items.add(new MessageDetails(1, s_date[1], friend, s_content[1], true));
        items.add(new MessageDetails(2, s_date[2], friend, s_content[2], false));

        return items;
    }

    public static List<Friend_photos> getFriendsAlbumData(Context ctx) {
        List<Friend_photos> items = new ArrayList<>();
        String album_name[] = ctx.getResources().getStringArray(R.array.friend_photo_album_name);
        TypedArray photo = ctx.getResources().obtainTypedArray(R.array.friend_photo_album_photo);
        for (int i = 0; i < 5; i++) {
            items.add(new Friend_photos(i, album_name[i], photo.getResourceId(i, -1), (5 + i)));
        }
        return items;
    }

    public static List<Student> getFriendsData(Context ctx) {
        List<Student> items = new ArrayList<>();
        String s_arr[] = ctx.getResources().getStringArray(R.array.people_names);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.people_photos);
        //for (int i = 0; i < s_arr.length; i++) {
        for (int i = 0; i < 5; i++) {
            Student fr = new Student(i, s_arr[i], drw_arr.getResourceId(i, -1));
            items.add(fr);
        }
        return items;
    }


    private static List<Integer> mixImgC(TypedArray f_arr) {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < f_arr.length(); i++) {
            data.add(f_arr.getResourceId(i, -1));
        }
        return data;
    }

    private static List<Integer> mixImg(TypedArray f_arr, TypedArray s_arr) {
        List<Integer> data = new ArrayList<>();
        for (int i = 0; i < f_arr.length(); i++) {
            data.add(f_arr.getResourceId(i, -1));
        }
        for (int i = 0; i < s_arr.length(); i++) {
            data.add(s_arr.getResourceId(i, -1));
        }
        return data;
    }

    private static List<String> mixStr(String[] f_str, String[] s_str) {
        List<String> data = new ArrayList<>();
        for (int i = 0; i < f_str.length; i++) {
            data.add(f_str[i]);
        }
        for (int i = 0; i < s_str.length; i++) {
            data.add(s_str[i]);
        }
        return data;
    }

    private static int getRandomIndex(Random r, int min, int max) {
        return r.nextInt(max - min) + min;
    }

    private static long getRandomLikes() {
        return getRandomIndex(rnd, 10, 250);
    }

    public static String getRandomSales() {
        return getRandomIndex(rnd, 2, 1000) + " Sales";
    }

    public static String getRandomReviews() {
        return getRandomIndex(rnd, 0, 800) + " Reviews";
    }


    //News

    private static Random r = new Random();

    public static List<String> getHomeCatgeory(Context ctx) {
        List<String> items = new ArrayList<>();
        String name_arr[] = ctx.getResources().getStringArray(R.array.home_category);
        for (int i = 0; i < name_arr.length; i++) {
            items.add(name_arr[i]);
        }
        return items;
    }

    public static List<Channel> getChannelData(Context ctx) {
        List<Channel> items = new ArrayList<>();
        String name_arr[] = ctx.getResources().getStringArray(R.array.channel_name);
        String color_arr[] = ctx.getResources().getStringArray(R.array.channel_color);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.channel_icon);
        for (int i = 0; i < name_arr.length; i++) {
            Channel ch = new Channel(name_arr[i], color_arr[i], drw_arr.getResourceId(i, -1));
            items.add(ch);
        }
        return items;
    }

    public static List<News> getNewsPolitics(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_p);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_p);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(0));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getNewsEntertainment(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_e);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_e);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(1));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getNewsScience(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_sc);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_sc);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(2));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getNewsSport(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_sp);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_sp);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(3));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getNewsBusiness(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_b);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_b);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(4));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getNewsTechnology(Context ctx) {
        List<News> items = new ArrayList<>();
        String title_arr[] = ctx.getResources().getStringArray(R.array.news_title_t);
        TypedArray drw_arr = ctx.getResources().obtainTypedArray(R.array.news_img_t);
        String content = ctx.getString(R.string.very_long_lorem_ipsum);
        for (int i = 0; i < title_arr.length; i++) {
            News n = new News(title_arr[i], getRandomDate(ctx), drw_arr.getResourceId(i, -1), content, getChannelData(ctx).get(5));
            items.add(n);
        }
        Collections.shuffle(items);
        return items;
    }

    public static List<News> getAllNews(Context ctx) {
        List<News> items = new ArrayList<>();
        items.addAll(getNewsPolitics(ctx));
        items.addAll(getNewsEntertainment(ctx));
        items.addAll(getNewsScience(ctx));
        items.addAll(getNewsSport(ctx));
        items.addAll(getNewsBusiness(ctx));
        items.addAll(getNewsTechnology(ctx));
        Collections.shuffle(items);
        return items;
    }

    public static String getRandomDate(Context ctx) {
        String date_arr[] = ctx.getResources().getStringArray(R.array.general_date);
        return date_arr[getRandomIndex(0, date_arr.length - 1)];
    }

    private static int getRandomIndex(int min, int max) {
        return r.nextInt(max - min) + min;
    }

    public static class Extra {
        public static final String FRAGMENT_INDEX = "com.nostra13.example.universalimageloader.FRAGMENT_INDEX";
        public static final String IMAGE_POSITION = "com.nostra13.example.universalimageloader.IMAGE_POSITION";
    }
}
