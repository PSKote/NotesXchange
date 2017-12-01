package com.notesxchange.notesxchange.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import com.notesxchange.notesxchange.Home.HomeActivity;
import com.notesxchange.notesxchange.Profile.ProfileActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Utils.FirebaseMethods;
import com.notesxchange.notesxchange.Utils.StringManipulation;
import com.notesxchange.notesxchange.Utils.UniversalImageLoader;
import com.notesxchange.notesxchange.models.UserAccountSettings;
import com.notesxchange.notesxchange.models.UserSettings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Tag images here
 */

public class NextActivity extends AppCompatActivity {

    private static final String TAG = "NextActivity";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;

    //widgets
    private EditText mTitle;
    private EditText mTags;
    private Spinner mCategory, mPrivacy, mCollege, mYear, mBranch;

    //vars
    private String mAppend = "file:/";
    private int imageCount = 0;
    private int countSetFlag = 0;
    private String imgUrl;
    private Bitmap bitmap;
    private Intent intent;
    private ProgressBar mProgressBar;
    private TextView mUploading;
    private ArrayList<String> collegeList, yearList, branchList, privacyList, categoryList;
    private String college, year, branch, category, privacy;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreate is getting called..............");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_next);
        mFirebaseMethods = new FirebaseMethods(NextActivity.this);
        mTitle = (EditText) findViewById(R.id.title) ;
        mTags = (EditText) findViewById(R.id.tags);
        mCategory = (Spinner) findViewById(R.id.spinnerCategory);
        mPrivacy = (Spinner) findViewById(R.id.spinnerPrivacy);
        mCollege = (Spinner) findViewById(R.id.spinnerCollege);
        mYear = (Spinner) findViewById(R.id.spinnerYear);
        mBranch = (Spinner) findViewById(R.id.spinnerBranch);
        mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
        mUploading = (TextView) findViewById(R.id.uploading);

        collegeList = new ArrayList<>(Arrays.asList("Choose college"));
        yearList = new ArrayList<>(Arrays.asList("Choose year"));
        branchList = new ArrayList<>(Arrays.asList("Choose branch"));
        privacyList = new ArrayList<>(Arrays.asList("Public"));
        categoryList = new ArrayList<>(Arrays.asList("Choose Category"));

        mUploading.setVisibility(View.GONE);
        mProgressBar.setVisibility(View.GONE);

        setupFirebaseAuth();
        initList();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });


        //Click share to uplaod to database
        TextView share = (TextView) findViewById(R.id.tvShare);
        share.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");
                //upload the image to firebase
                Toast.makeText(NextActivity.this, "Attempting to upload new photo", Toast.LENGTH_SHORT).show();
                String title = mTitle.getText().toString();
                String tags = mTags.getText().toString();
                String tag = StringManipulation.getTags(tags);

                if(intent.hasExtra(getString(R.string.selected_image))){
                    imgUrl = intent.getStringExtra(getString(R.string.selected_image));
                    mUploading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), title, imageCount,
                            imgUrl, null, tag, category, privacy, college, year, branch);
                    mUploading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }
                else if(intent.hasExtra(getString(R.string.selected_bitmap))){
                    //bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
                    imgUrl = intent.getStringExtra(getString(R.string.selected_bitmap));
                    mUploading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
//                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), title, imageCount, null, bitmap,
//                            tag, category, privacy, college, year, branch);
                    mFirebaseMethods.uploadNewPhoto(getString(R.string.new_photo), title, imageCount, imgUrl, null,
                            tag, category, privacy, college, year, branch);
                    mUploading.setVisibility(View.VISIBLE);
                    mProgressBar.setVisibility(View.VISIBLE);
                }



            }
        });

        setImage();
        System.gc();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent;
        intent = getIntent();
        if (intent.hasExtra(getString(R.string.calling_activity))){

        }
        else {
            intent = new Intent(NextActivity.this, UploadActivity.class);
            startActivity(intent);
            finish();
        }
    }

    /*
     * Initialize all the list required for tagging
     */
    private void initList(){
        collegeList.add("IIIT, Bangalore");
        collegeList.add("Other");
        yearList.add("1st year M.Tech");
        yearList.add("2nd year M.Tech");
        yearList.add("1st year iM.Tech");
        yearList.add("2nd year iM.Tech");
        yearList.add("3rd year iM.Tech");
        yearList.add("4th year iM.Tech");
        yearList.add("5th year iM.Tech");
        yearList.add("Other");
        branchList.add("Electronics and Communication");
        branchList.add("Computer Science");
        branchList.add("Electronic System Design");
        branchList.add("Information Technology");
        branchList.add("Other");
        categoryList.add("Art & Photos");
        categoryList.add("Automotive");
        categoryList.add("Business");
        categoryList.add("Career");
        categoryList.add("Data Analytics");
        categoryList.add("Design");
        categoryList.add("Devices & Hardware");
        categoryList.add("Economy & Finance");
        categoryList.add("Education");
        categoryList.add("Engineering");
        categoryList.add("Environment & Food");
        categoryList.add("Government & Nonprofit");
        categoryList.add("Health and Medicine");
        categoryList.add("Internet");
        categoryList.add("Law");
        categoryList.add("Leadership & Management");
        categoryList.add("Lifestyle");
        categoryList.add("Marketing");
        categoryList.add("Mathematics");
        categoryList.add("Mobile");
        categoryList.add("News & Politics");
        categoryList.add("Science");
        categoryList.add("Social Science");
        categoryList.add("Software");
        categoryList.add("Technology");
        categoryList.add("Travel");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, collegeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCollege.setAdapter(adapter);

        mCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + collegeList.get(position));

                college = collegeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, yearList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYear.setAdapter(adapter1);

        mYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + yearList.get(position));

                year = yearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, branchList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBranch.setAdapter(adapter2);

        mBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + branchList.get(position));

                branch = branchList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter3 = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, categoryList);
        adapter3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCategory.setAdapter(adapter3);

        mCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + categoryList.get(position));

                category = categoryList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter4 = new ArrayAdapter<String>(NextActivity.this,
                android.R.layout.simple_spinner_item, privacyList);
        adapter4.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mPrivacy.setAdapter(adapter4);

        mPrivacy.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + privacyList.get(position));

                privacy = privacyList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


    }



    private void someMethod(){
        /*
            Step 1)
            Create a data model for Photos
            Step 2)
            Add properties to the Photo Objects (caption, date, imageUrl, photo_id, tags, user_id)
            Step 3)
            Count the number of photos that the user already has.
            Step 4)
            a) Upload the photo to Firebase Storage
            b) insert into 'photos' node
            c) insert into 'user_photos' node
         */

    }


    /**
     * gets the image url from the incoming intent and displays the chosen image
     */
    private void setImage(){
        intent = getIntent();
        ImageView image = (ImageView) findViewById(R.id.imageShare);

        if(intent.hasExtra(getString(R.string.selected_image))){
            imgUrl = intent.getStringExtra(getString(R.string.selected_image));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }
        else if(intent.hasExtra(getString(R.string.selected_bitmap))){
//            bitmap = (Bitmap) intent.getParcelableExtra(getString(R.string.selected_bitmap));
//            Log.d(TAG, "setImage: got new bitmap");
//            image.setImageBitmap(bitmap);

            imgUrl = intent.getStringExtra(getString(R.string.selected_bitmap));
            Log.d(TAG, "setImage: got new image url: " + imgUrl);
            UniversalImageLoader.setImage(imgUrl, image, null, mAppend);
        }
    }

     /*
     ------------------------------------ Firebase ---------------------------------------------
     */

    /**
     * Setup the firebase auth object
     */
    private void setupFirebaseAuth(){
        Log.d(TAG, "setupFirebaseAuth: setting up firebase auth.");
        mAuth = FirebaseAuth.getInstance();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        myRef = mFirebaseDatabase.getReference();
        //Log.d(TAG, "onDataChange: image count: " + imageCount);

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();


                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged:signed_out");
                }
                // ...
            }
        };


        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                imageCount = mFirebaseMethods.getImageCount(dataSnapshot);
                countSetFlag = 1;
                Log.d(TAG, "onDataChange: image count I'm getiing called:.............. " + imageCount);

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}