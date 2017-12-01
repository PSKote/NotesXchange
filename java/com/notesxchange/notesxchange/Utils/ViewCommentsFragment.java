package com.notesxchange.notesxchange.Utils;


import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.TimeZone;

import com.notesxchange.notesxchange.Home.HomeActivity;
import com.notesxchange.notesxchange.Login.LoginActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Search.SearchActivityBranch;
import com.notesxchange.notesxchange.Search.SearchActivityCategory;
import com.notesxchange.notesxchange.Search.SearchActivityCollege;
import com.notesxchange.notesxchange.Search.SearchActivityYear;
import com.notesxchange.notesxchange.models.Comment;
import com.notesxchange.notesxchange.models.Photo;

/**
 * Display and type comments
 */

public class ViewCommentsFragment extends Fragment {

    private static final String TAG = "ViewCommentsFragment";

    public ViewCommentsFragment(){
        super();
        setArguments(new Bundle());
    }

    //firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private FirebaseDatabase mFirebaseDatabase;
    private DatabaseReference myRef;

    //widgets
    private ImageView mBackArrow, mCheckMark;
    private EditText mComment;
    private ListView mListView;

    //vars
    private Photo mPhoto;
    private ArrayList<Comment> mComments;
    private Context mContext;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_view_comments, container, false);
        mBackArrow = (ImageView) view.findViewById(R.id.backArrow);
        mCheckMark = (ImageView) view.findViewById(R.id.ivPostComment);
        mComment = (EditText) view.findViewById(R.id.comment);
        mListView = (ListView) view.findViewById(R.id.listView);
        mComments = new ArrayList<>();
        mContext = getActivity();


        try{
            mPhoto = getPhotoFromBundle();
            Log.d(TAG, "_*_*_*_*_*_*_*" + getPhotoFromBundle());
            setupFirebaseAuth();

        }catch (NullPointerException e){
            Log.e(TAG, "onCreateView: NullPointerException: " + e.getMessage() );
        }




        return view;
    }

    //setting up widgets
    private void setupWidgets(){

        CommentListAdapter adapter = new CommentListAdapter(mContext,
                R.layout.layout_comment, mComments);
        mListView.setAdapter(adapter);

        mCheckMark.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!mComment.getText().toString().equals("")){
                    Log.d(TAG, "onClick: attempting to submit new comment.");
                    addNewComment(mComment.getText().toString());

                    mComment.setText("");
                    closeKeyboard();
                }else{
                    Toast.makeText(getActivity(), "you can't post a blank comment", Toast.LENGTH_SHORT).show();
                }
            }
        });

        mBackArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating back");
                try {
                    if(getCallingActivityFromBundle().equals(getString(R.string.home_activity))){
                        getActivity().getSupportFragmentManager().popBackStack();
                        mComments.clear();
                        ((HomeActivity)getActivity()).showLayout();
                    }
                    else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_category))){
                        Log.d(TAG, "search_activity_category***********");
                        getActivity().getSupportFragmentManager().popBackStack();
                        mComments.clear();
                        ((SearchActivityCategory)getActivity()).showLayout();
                    }
                    else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_college))){
                        Log.d(TAG, "search_activity_college***********");
                        getActivity().getSupportFragmentManager().popBackStack();
                        mComments.clear();
                        ((SearchActivityCollege)getActivity()).showLayout();
                    }
                    else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_year))){
                        Log.d(TAG, "search_activity_year***********");
                        getActivity().getSupportFragmentManager().popBackStack();
                        mComments.clear();
                        ((SearchActivityYear)getActivity()).showLayout();
                    }
                    else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_branch))){
                        Log.d(TAG, "search_activity_branch***********");
                        getActivity().getSupportFragmentManager().popBackStack();
                        mComments.clear();
                        ((SearchActivityBranch)getActivity()).showLayout();
                    }
                    else{
                        getActivity().getSupportFragmentManager().popBackStack();
                    }
                }catch (NullPointerException e) {
                    getActivity().getSupportFragmentManager().popBackStack();
                }


            }
        });
    }

    @Override
    public void onResume() {

        super.onResume();

        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){

                    try {
                        if(getCallingActivityFromBundle().equals(getString(R.string.home_activity))){
                            getActivity().getSupportFragmentManager().popBackStack();
                            mComments.clear();
                            ((HomeActivity)getActivity()).showLayout();
                        }
                        else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_category))){
                            Log.d(TAG, "search_activity_category***********");
                            getActivity().getSupportFragmentManager().popBackStack();
                            mComments.clear();
                            ((SearchActivityCategory)getActivity()).showLayout();
                        }
                        else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_college))){
                            Log.d(TAG, "search_activity_college***********");
                            getActivity().getSupportFragmentManager().popBackStack();
                            mComments.clear();
                            ((SearchActivityCollege)getActivity()).showLayout();
                        }
                        else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_year))){
                            Log.d(TAG, "search_activity_year***********");
                            getActivity().getSupportFragmentManager().popBackStack();
                            mComments.clear();
                            ((SearchActivityYear)getActivity()).showLayout();
                        }
                        else if (getCallingActivityFromBundle().equals(getString(R.string.search_activity_branch))){
                            Log.d(TAG, "search_activity_branch***********");
                            getActivity().getSupportFragmentManager().popBackStack();
                            mComments.clear();
                            ((SearchActivityBranch)getActivity()).showLayout();
                        }
                        else{
                            getActivity().getSupportFragmentManager().popBackStack();
                        }
                    }catch (NullPointerException e) {
                        getActivity().getSupportFragmentManager().popBackStack();
                    }

                    return true;

                }

                return false;
            }
        });
    }

    private void closeKeyboard(){
        View view = getActivity().getCurrentFocus();
        if(view != null){
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }


    private void addNewComment(String newComment){
        Log.d(TAG, "addNewComment: adding new comment: " + newComment);

        String commentID = myRef.push().getKey();

        Comment comment = new Comment();
        comment.setComment(newComment);
        comment.setDate_created(getTimestamp());
        comment.setUser_id(FirebaseAuth.getInstance().getCurrentUser().getUid());

        //insert into photos node
        myRef.child(getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into user_photos node
        myRef.child(getString(R.string.dbname_user_photos))
                .child(mPhoto.getUser_id())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        String categorydb = mPhoto.getCategory().replace(".","").replace(" ","_");
        String yeardb = mPhoto.getYear().replace(".","").replace(" ","_");
        String branchdb = mPhoto.getBranch().replace(".","").replace(" ","_");
        String collegedb = mPhoto.getCollege().replace(".","").replace(" ","_");

        //insert into category node
        myRef.child(getString(R.string.dbname_category))
                .child(categorydb)
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into privacy node
        myRef.child(getString(R.string.dbname_privacy))
                .child(mPhoto.getPrivacy())
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into college node
        myRef.child(getString(R.string.dbname_college))
                .child(collegedb)
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into year node
        myRef.child(getString(R.string.dbname_year))
                .child(yeardb)
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);

        //insert into branch node
        myRef.child(getString(R.string.dbname_branch))
                .child(branchdb)
                .child(mPhoto.getPhoto_id())
                .child(getString(R.string.field_comments))
                .child(commentID)
                .setValue(comment);
    }

    private String getTimestamp(){
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.US);
        sdf.setTimeZone(TimeZone.getTimeZone("Asia/Calcutta"));
        return sdf.format(new Date());
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private String getCallingActivityFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            if(bundle.getString(getString(R.string.home_activity)) != null){
                return bundle.getString(getString(R.string.home_activity));
            }
            else if (bundle.getString(getString(R.string.search_activity_category)) != null){
                return bundle.getString(getString(R.string.search_activity_category));
            }
            else if (bundle.getString(getString(R.string.search_activity_college)) != null){
                return bundle.getString(getString(R.string.search_activity_college));
            }
            else if (bundle.getString(getString(R.string.search_activity_year)) != null){
                return bundle.getString(getString(R.string.search_activity_year));
            }
            else if (bundle.getString(getString(R.string.search_activity_branch)) != null){
                return bundle.getString(getString(R.string.search_activity_branch));
            }
            else {
                return null;
            }
        }else{
            return null;
        }
    }

    /**
     * retrieve the photo from the incoming bundle from profileActivity interface
     * @return
     */
    private Photo getPhotoFromBundle(){
        Log.d(TAG, "getPhotoFromBundle: arguments: " + getArguments());

        Bundle bundle = this.getArguments();
        if(bundle != null) {
            return bundle.getParcelable(getString(R.string.photo));
        }else{
            return null;
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

        if(mPhoto.getComments().size() == 0){
            Comment firstComment = new Comment();
            firstComment.setComment(mPhoto.getTitle());
            firstComment.setUser_id(mPhoto.getUser_id());
            firstComment.setDate_created(mPhoto.getDate_created());
            mComments.add(firstComment);
            mPhoto.setComments(mComments);
            setupWidgets();
        }


        myRef.child(mContext.getString(R.string.dbname_photos))
                .child(mPhoto.getPhoto_id())
                .child(mContext.getString(R.string.field_comments))
                .addChildEventListener(new ChildEventListener() {
                    @Override
                    public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                        Log.d(TAG, "onChildAdded: child added.");

                        Query query = myRef
                                .child(mContext.getString(R.string.dbname_photos))
                                .orderByChild(mContext.getString(R.string.field_photo_id))
                                .equalTo(mPhoto.getPhoto_id());
                        query.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for ( DataSnapshot singleSnapshot :  dataSnapshot.getChildren()){

                                    Photo photo = new Photo();
                                    Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                                    photo.setTitle(objectMap.get(mContext.getString(R.string.field_title)).toString());
                                    photo.setTags(objectMap.get(mContext.getString(R.string.field_tags)).toString());
                                    photo.setPhoto_id(objectMap.get(mContext.getString(R.string.field_photo_id)).toString());
                                    photo.setUser_id(objectMap.get(mContext.getString(R.string.field_user_id)).toString());
                                    photo.setDate_created(objectMap.get(mContext.getString(R.string.field_date_created)).toString());
                                    photo.setImage_path(objectMap.get(mContext.getString(R.string.field_image_path)).toString());
                                    photo.setCategory(objectMap.get(getString(R.string.field_category)).toString());
                                    photo.setPrivacy(objectMap.get(getString(R.string.field_privacy)).toString());
                                    photo.setCollege(objectMap.get(getString(R.string.field_college)).toString());
                                    photo.setYear(objectMap.get(getString(R.string.field_year)).toString());
                                    photo.setBranch(objectMap.get(getString(R.string.field_branch)).toString());


                                    mComments.clear();
                                    Comment firstComment = new Comment();
                                    firstComment.setComment(mPhoto.getTitle());
                                    firstComment.setUser_id(mPhoto.getUser_id());
                                    firstComment.setDate_created(mPhoto.getDate_created());
                                    mComments.add(firstComment);

                                    for (DataSnapshot dSnapshot : singleSnapshot
                                            .child(mContext.getString(R.string.field_comments)).getChildren()){
                                        Comment comment = new Comment();
                                        comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                                        comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                                        comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                                        mComments.add(comment);
                                    }

                                    photo.setComments(mComments);

                                    mPhoto = photo;

                                    setupWidgets();
//                    List<Like> likesList = new ArrayList<Like>();
//                    for (DataSnapshot dSnapshot : singleSnapshot
//                            .child(getString(R.string.field_likes)).getChildren()){
//                        Like like = new Like();
//                        like.setUser_id(dSnapshot.getValue(Like.class).getUser_id());
//                        likesList.add(like);
//                    }

                                }

                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                Log.d(TAG, "onCancelled: query cancelled.");
                            }
                        });
                    }

                    @Override
                    public void onChildChanged(DataSnapshot dataSnapshot, String s) {

                    }

                    @Override
                    public void onChildRemoved(DataSnapshot dataSnapshot) {

                    }

                    @Override
                    public void onChildMoved(DataSnapshot dataSnapshot, String s) {

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
            mComments.clear();
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

}