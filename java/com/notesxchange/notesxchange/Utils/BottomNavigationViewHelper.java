package com.notesxchange.notesxchange.Utils;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.util.Log;
import android.view.MenuItem;

import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import com.notesxchange.notesxchange.Home.HomeActivity;
import com.notesxchange.notesxchange.Notification.NotificationActivity;
import com.notesxchange.notesxchange.Profile.ProfileActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Search.SearchActivity;
import com.notesxchange.notesxchange.Upload.UploadActivity;

/**
 * Creating Bottom navigation
 */

public class BottomNavigationViewHelper {

    private static final String TAG = "BottomNavigationViewHel";

    public static void setupBottomNavigationView(BottomNavigationViewEx bottomNavigationViewEx){
        Log.d(TAG, "setupBottomNavigationView: Setting up BottomNavigationView");
        bottomNavigationViewEx.enableAnimation(false);
        bottomNavigationViewEx.enableItemShiftingMode(false);
        bottomNavigationViewEx.enableShiftingMode(false);
        bottomNavigationViewEx.setTextVisibility(false);
    }

    /*
     * initialize the menu based on click
     */
    public static void enableNavigation(final Context context, BottomNavigationViewEx view){
        view.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()){

                    case R.id.ic_read:
                        Intent intent1 = new Intent(context, HomeActivity.class);//ACTIVITY_NUM = 0
                        context.startActivity(intent1);
                        //callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_search:
                        Intent intent2  = new Intent(context, SearchActivity.class);//ACTIVITY_NUM = 1
                        context.startActivity(intent2);
                        //callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

                    case R.id.ic_upload:
                        Intent intent3 = new Intent(context, UploadActivity.class);//ACTIVITY_NUM = 2
                        context.startActivity(intent3);
                        //callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;

//                    case R.id.ic_alert:
//                        Intent intent4 = new Intent(context, NotificationActivity.class);//ACTIVITY_NUM = 3
//                        context.startActivity(intent4);
//                        //callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
//                        break;

                    case R.id.ic_user:
                        Intent intent4 = new Intent(context, ProfileActivity.class);//ACTIVITY_NUM = 3
                        context.startActivity(intent4);
                        //callingActivity.overridePendingTransition(R.anim.fade_in, R.anim.fade_out);
                        break;
                }


                return false;
            }
        });
    }
}