package com.notesxchange.notesxchange.Search;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.ittianyu.bottomnavigationviewex.BottomNavigationViewEx;

import com.notesxchange.notesxchange.Home.HomeActivity;
import com.notesxchange.notesxchange.Login.LoginActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Utils.BottomNavigationViewHelper;

/**
 * Gives choice to search
 */

public class SearchActivity extends AppCompatActivity{
    private static final String TAG = "SearchActivity";
    private static final int ACTIVITY_NUM = 1;

    private Context mContext = SearchActivity.this;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Log.d(TAG, "onCreate: started.");

        setupBottomNavigationView();

        /*
         * Sets up all search button
         */
        Button btnSearchCollege = (Button) findViewById(R.id.btn_searchCollege);
        btnSearchCollege.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                Intent intent = new Intent(SearchActivity.this, SearchActivityCollege.class);
                startActivity(intent);

            }
        });

        Button btnSearchBranch = (Button) findViewById(R.id.btn_searchBranch);
        btnSearchBranch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                Intent intent = new Intent(SearchActivity.this, SearchActivityBranch.class);
                startActivity(intent);

            }
        });

        Button btnSearchCategory = (Button) findViewById(R.id.btn_searchCategory);
        btnSearchCategory.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                Intent intent = new Intent(SearchActivity.this, SearchActivityCategory.class);
                startActivity(intent);

            }
        });

        Button btnSearchYear = (Button) findViewById(R.id.btn_searchYear);
        btnSearchYear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to log in.");

                Intent intent = new Intent(SearchActivity.this, SearchActivityYear.class);
                startActivity(intent);

            }
        });

    }

    /**
     * BottomNavigationView setup
     */
    private void setupBottomNavigationView(){
        Log.d(TAG, "setupBottomNavigationView: setting up BottomNavigationView");
        BottomNavigationViewEx bottomNavigationViewEx = (BottomNavigationViewEx) findViewById(R.id.bottomNavViewBar);
        BottomNavigationViewHelper.setupBottomNavigationView(bottomNavigationViewEx);
        BottomNavigationViewHelper.enableNavigation(mContext, bottomNavigationViewEx);
        Menu menu = bottomNavigationViewEx.getMenu();
        MenuItem menuItem = menu.getItem(ACTIVITY_NUM);
        menuItem.setChecked(true);
    }
}