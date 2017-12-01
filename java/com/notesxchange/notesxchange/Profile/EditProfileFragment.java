package com.notesxchange.notesxchange.Profile;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.ProviderQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import de.hdodenhof.circleimageview.CircleImageView;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Upload.UploadActivity;
import com.notesxchange.notesxchange.Utils.FirebaseMethods;
import com.notesxchange.notesxchange.Utils.UniversalImageLoader;
import com.notesxchange.notesxchange.dialogs.ConfirmPasswordDialog;
import com.notesxchange.notesxchange.models.User;
import com.notesxchange.notesxchange.models.UserAccountSettings;
import com.notesxchange.notesxchange.models.UserSettings;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Edit user profile
 */

public class EditProfileFragment extends Fragment implements
        ConfirmPasswordDialog.OnConfirmPasswordListener{


    @Override
    public void onConfirmPassword(String password) {
        Log.d(TAG, "onConfirmPassword: got the password: " + password);

        // Get auth credentials from the user for re-authentication. The example below shows
        // email and password credentials but there are multiple possible providers,
        // such as GoogleAuthProvider or FacebookAuthProvider.
        AuthCredential credential = EmailAuthProvider
                .getCredential(mAuth.getCurrentUser().getEmail(), password);

        ///////////////////// Prompt the user to re-provide their sign-in credentials
        mAuth.getCurrentUser().reauthenticate(credential)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if(task.isSuccessful()){
                            Log.d(TAG, "User re-authenticated.");

                            ///////////////////////check to see if the email is not already present in the database
                            mAuth.fetchProvidersForEmail(mEmail.getText().toString()).addOnCompleteListener(new OnCompleteListener<ProviderQueryResult>() {
                                @Override
                                public void onComplete(@NonNull Task<ProviderQueryResult> task) {
                                    if(task.isSuccessful()){
                                        try{
                                            if(task.getResult().getProviders().size() == 1){
                                                Log.d(TAG, "onComplete: that email is already in use.");
                                                Toast.makeText(getActivity(), "That email is already in use", Toast.LENGTH_SHORT).show();
                                            }
                                            else{
                                                Log.d(TAG, "onComplete: That email is available.");

                                                //////////////////////the email is available so update it
                                                mAuth.getCurrentUser().updateEmail(mEmail.getText().toString())
                                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                if (task.isSuccessful()) {
                                                                    Log.d(TAG, "User email address updated.");
                                                                    Toast.makeText(getActivity(), "email updated", Toast.LENGTH_SHORT).show();
                                                                    mFirebaseMethods.updateEmail(mEmail.getText().toString());
                                                                }
                                                            }
                                                        });
                                            }
                                        }catch (NullPointerException e){
                                            Log.e(TAG, "onComplete: NullPointerException: "  +e.getMessage() );
                                        }
                                    }
                                }
                            });





                        }else{
                            Log.d(TAG, "onComplete: re-authentication failed.");
                        }

                    }
                });
    }

    private static final String TAG = "EditProfileFragment";

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;
    private FirebaseMethods mFirebaseMethods;
    private String userID;


    //EditProfile Fragment widgets
    private EditText mDisplayName, mUsername, mEmail, mPhoneNumber;
    private Spinner mYear, mCollege, mBranch;
    private TextView mChangeProfilePhoto;
    private CircleImageView mProfilePhoto;


    //vars
    private UserSettings mUserSettings;
    private ArrayList<String> collegeList, yearList, branchList;
    private String collegeString, yearString, branchString;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_editprofile, container, false);
        mProfilePhoto = (CircleImageView) view.findViewById(R.id.profile_photo);
        mDisplayName = (EditText) view.findViewById(R.id.display_name);
        mUsername = (EditText) view.findViewById(R.id.username);
        mYear = (Spinner) view.findViewById(R.id.year);
        mCollege = (Spinner) view.findViewById(R.id.college);
        mBranch = (Spinner) view.findViewById(R.id.branch);
        mEmail = (EditText) view.findViewById(R.id.email);
        mPhoneNumber = (EditText) view.findViewById(R.id.phoneNumber);
        mChangeProfilePhoto = (TextView) view.findViewById(R.id.changeProfilePhoto);
        mFirebaseMethods = new FirebaseMethods(getActivity());
        collegeList = new ArrayList<>(Arrays.asList("Choose college"));
        yearList = new ArrayList<>(Arrays.asList("Choose year"));
        branchList = new ArrayList<>(Arrays.asList("Choose branch"));
        initList();


        //setProfileImage();
        setupFirebaseAuth();

        //back arrow for navigating back to "ProfileActivity"
        ImageView backArrow = (ImageView) view.findViewById(R.id.backArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back to ProfileActivity");
                getActivity().finish();
            }
        });

        ImageView checkmark = (ImageView) view.findViewById(R.id.saveChanges);
        checkmark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: attempting to save changes.");
                saveProfileSettings();
            }
        });

        return view;
    }

    //initialize list so that user can change data in edit profile
    private void initList() {
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

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, collegeList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mCollege.setAdapter(adapter);

        mCollege.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + collegeList.get(position));

                collegeString = collegeList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });


        ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, yearList);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mYear.setAdapter(adapter1);

        mYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + yearList.get(position));

                yearString = yearList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, branchList);
        adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBranch.setAdapter(adapter2);

        mBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + branchList.get(position));

                branchString = branchList.get(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }


    /**
     * Retrieves the data contained in the widgets and submits it to the database
     * Before donig so it chekcs to make sure the username chosen is unqiue
     */
    private void saveProfileSettings(){
        final String displayName = mDisplayName.getText().toString();
        final String username = mUsername.getText().toString();

        final String year = yearString;
        final String college = collegeString;
        final String branch = branchString;

        final String email = mEmail.getText().toString();
        final long phoneNumber = Long.parseLong(mPhoneNumber.getText().toString());


        //case1: if the user made a change to their username
        if(!mUserSettings.getUser().getUsername().equals(username)){

            checkIfUsernameExists(username);
        }
        //case2: if the user made a change to their email
        if(!mUserSettings.getUser().getEmail().equals(email)){

            // step1) Reauthenticate
            //          -Confirm the password and email
            ConfirmPasswordDialog dialog = new ConfirmPasswordDialog();
            dialog.show(getFragmentManager(), getString(R.string.confirm_password_dialog));
            dialog.setTargetFragment(EditProfileFragment.this, 1);


            // step2) check if the email already is registered
            //          -'fetchProvidersForEmail(String email)'
            // step3) change the email
            //          -submit the new email to the database and authentication
        }

        /**
         * change the rest of the settings that do not require uniqueness
         */
        if(!mUserSettings.getSettings().getDisplay_name().equals(displayName)){
            //update displayname
            mFirebaseMethods.updateUserAccountSettings(displayName, null, null, null, 0);
        }
        if(!mUserSettings.getSettings().getYear().equals(year)){
            //update website
            mFirebaseMethods.updateUserAccountSettings(null, year, null, null, 0);
        }
        if(!mUserSettings.getSettings().getCollege().equals(college)){
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, college, null, 0);
        }
        if(!mUserSettings.getSettings().getBranch().equals(branch)){
            //update description
            mFirebaseMethods.updateUserAccountSettings(null, null, null, branch, 0);
        }
        //if(!mUserSettings.getSettings().getProfile_photo().equals(phoneNumber)){
            //update phoneNumber
        //    mFirebaseMethods.updateUserAccountSettings(null, null, null, phoneNumber);
        //}
        String phoneNumerNew= String.valueOf(phoneNumber);
        if (!mPhoneNumber.equals(phoneNumerNew)){
            //update phoneNumber
            mFirebaseMethods.updateUserAccountSettings(null, null, null, null, phoneNumber);
        }

    }



    /**
     * Check is @param username already exists in teh database
     * @param username
     */
    private void checkIfUsernameExists(final String username) {
        Log.d(TAG, "checkIfUsernameExists: Checking if  " + username + " already exists.");

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        Query query = reference
                .child(getString(R.string.dbname_users))
                .orderByChild(getString(R.string.field_username))
                .equalTo(username);
        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                if(!dataSnapshot.exists()){
                    //add the username
                    mFirebaseMethods.updateUsername(username);
                    Toast.makeText(getActivity(), "saved username.", Toast.LENGTH_SHORT).show();

                }
                for(DataSnapshot singleSnapshot: dataSnapshot.getChildren()){
                    if (singleSnapshot.exists()){
                        Log.d(TAG, "checkIfUsernameExists: FOUND A MATCH: " + singleSnapshot.getValue(User.class).getUsername());
                        Toast.makeText(getActivity(), "That username already exists.", Toast.LENGTH_SHORT).show();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    //set profile data that had been given during registration
    private void setProfileWidgets(UserSettings userSettings){
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.toString());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getEmail());
        Log.d(TAG, "setProfileWidgets: setting widgets with data retrieving from firebase database: " + userSettings.getUser().getPhone_number());

        mUserSettings = userSettings;
        //User user = userSettings.getUser();
        UserAccountSettings settings = userSettings.getSettings();
        UniversalImageLoader.setImage(settings.getProfile_photo(), mProfilePhoto, null, "");
        mDisplayName.setText(settings.getDisplay_name());
        mUsername.setText(settings.getUsername());

        try {
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, yearList);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mYear.setAdapter(adapter);
            yearString = settings.getYear();
            int spinnerPosition = adapter.getPosition(settings.getYear());
            mYear.setSelection(spinnerPosition);
        }catch (NullPointerException e){
            Log.e(TAG, "setProfileWidgets: NullPointerException " + e.getMessage());
        }


        try{
            ArrayAdapter<String> adapter1 = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, collegeList);
            adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mCollege.setAdapter(adapter1);
            collegeString = settings.getCollege();
            int spinnerPosition1 = adapter1.getPosition(settings.getCollege());
            mCollege.setSelection(spinnerPosition1);
        }catch (NullPointerException e){
            Log.e(TAG, "setProfileWidgets: NullPointerException " + e.getMessage());
        }

        try {
            ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(getActivity(),
                    android.R.layout.simple_spinner_item, branchList);
            adapter2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            mBranch.setAdapter(adapter2);
            branchString = settings.getBranch();
            int spinnerPosition2 = adapter2.getPosition(settings.getBranch());
            mBranch.setSelection(spinnerPosition2);
        }catch (NullPointerException e){
            Log.e(TAG, "setProfileWidgets: NullPointerException " + e.getMessage());
        }


        mEmail.setText(userSettings.getUser().getEmail());
        mPhoneNumber.setText(String.valueOf(userSettings.getUser().getPhone_number()));

        mChangeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: changing profile photo");
                Intent intent = new Intent(getActivity(), UploadActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK); //268435456
                getActivity().startActivity(intent);
                getActivity().finish();
            }
        });

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
        userID = mAuth.getCurrentUser().getUid();

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

                //retrieve user information from the database
                setProfileWidgets(mFirebaseMethods.getUserSettings(dataSnapshot));

                //retrieve images for the user in question

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