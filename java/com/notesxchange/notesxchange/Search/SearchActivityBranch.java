package com.notesxchange.notesxchange.Search;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Utils.BranchfeedListAdapter;
import com.notesxchange.notesxchange.Utils.ViewCommentsFragment;
import com.notesxchange.notesxchange.models.Comment;
import com.notesxchange.notesxchange.models.Photo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;


/*
 * Search by branch
 */
public class SearchActivityBranch extends AppCompatActivity implements
        BranchfeedListAdapter.OnLoadMoreItemsListener{

    @Override
    public void onLoadMoreItems() {

        displayMorePhotos();
    }

    private static final String TAG = "SearchActivityBranch";

    //vars
    private Context mContext = SearchActivityBranch.this;
    private ArrayList<String> branchList;
    private String branch;
    private Spinner mBranch;
    private ArrayList<Photo> mPhotos;
    private ArrayList<Photo> mPaginatedPhotos;
    //private ArrayList<String> mFollowing;
    private ListView mListView;
    private BranchfeedListAdapter mAdapter;
    private int mResults;
    private String branchdb;

    //widgets
    private FrameLayout mFrameLayout;
    private RelativeLayout mRelativeLayout;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_branch);
        Log.d(TAG, "onCreate: started.");

        mFrameLayout = (FrameLayout) findViewById(R.id.container);
        mRelativeLayout = (RelativeLayout) findViewById(R.id.relLayoutParent);

        mBranch = (Spinner) findViewById(R.id.spinnerBranch);
        branchList = new ArrayList<>(Arrays.asList("Choose Branch"));
        mListView = (ListView) findViewById(R.id.listView);
        //mFollowing = new ArrayList<>();
        mPhotos = new ArrayList<>();

        ImageView backArrow = (ImageView) findViewById(R.id.ivBackArrow);
        backArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the activity");
                finish();
            }
        });

        initList();
        //getFollowing();
        getPhotos();

    }

    /*
     * Intialize list of branches to select
     */
    private void initList() {
        branchList.add("Electronics and Communication");
        branchList.add("Computer Science");
        branchList.add("Electronic System Design");
        branchList.add("Information Technology");
        branchList.add("Other");

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(SearchActivityBranch.this,
                android.R.layout.simple_spinner_item, branchList);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mBranch.setAdapter(adapter);

        mBranch.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + branchList.get(position));

                branch = branchList.get(position);
                branchdb = branch.replace(".","").replace(" ","_");
                mPhotos.clear();
                getPhotos();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    /*
     * get photos for selected branch from database
     */
    private void getPhotos(){
        Log.d(TAG, "getPhotos: getting photos");
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        //for(int i = 0; i < mFollowing.size(); i++){
        //final int count = i;
        try {
            Query query = reference
                    //.child(getString(R.string.dbname_photos));
                    .child(getString(R.string.dbname_branch))
                    .child(branchdb);
//                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid());
//                    .child(getString(R.string.dbname_user_photos))
//                    .child(mFollowing.get(i))
//                    .orderByChild(getString(R.string.field_user_id))
//                    .equalTo(mFollowing.get(i));
            query.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    for(DataSnapshot singleSnapshot : dataSnapshot.getChildren()){

                        Photo photo = new Photo();
                        Map<String, Object> objectMap = (HashMap<String, Object>) singleSnapshot.getValue();

                        photo.setTitle(objectMap.get(getString(R.string.field_title)).toString());
                        photo.setTags(objectMap.get(getString(R.string.field_tags)).toString());
                        photo.setPhoto_id(objectMap.get(getString(R.string.field_photo_id)).toString());
                        photo.setUser_id(objectMap.get(getString(R.string.field_user_id)).toString());
                        photo.setDate_created(objectMap.get(getString(R.string.field_date_created)).toString());
                        photo.setImage_path(objectMap.get(getString(R.string.field_image_path)).toString());
                        photo.setCategory(objectMap.get(getString(R.string.field_category)).toString());
                        photo.setPrivacy(objectMap.get(getString(R.string.field_privacy)).toString());
                        photo.setCollege(objectMap.get(getString(R.string.field_college)).toString());
                        photo.setYear(objectMap.get(getString(R.string.field_year)).toString());
                        photo.setBranch(objectMap.get(getString(R.string.field_branch)).toString());

                        ArrayList<Comment> comments = new ArrayList<Comment>();
                        for (DataSnapshot dSnapshot : singleSnapshot
                                .child(getString(R.string.field_comments)).getChildren()){
                            Comment comment = new Comment();
                            comment.setUser_id(dSnapshot.getValue(Comment.class).getUser_id());
                            comment.setComment(dSnapshot.getValue(Comment.class).getComment());
                            comment.setDate_created(dSnapshot.getValue(Comment.class).getDate_created());
                            comments.add(comment);
                        }

                        photo.setComments(comments);
                        mPhotos.add(photo);
                    }
                    //if(count >= mFollowing.size() -1){
                    //display our photos
                    displayPhotos();
                    //}
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (NullPointerException e) {
            Toast.makeText(mContext, "Choose Branch", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Pagination Logic
     */
    private void displayPhotos(){
        mPaginatedPhotos = new ArrayList<>();
        if(mPhotos != null){
            try{
                Collections.sort(mPhotos, new Comparator<Photo>() {
                    @Override
                    public int compare(Photo o1, Photo o2) {
                        return o2.getDate_created().compareTo(o1.getDate_created());
                    }
                });

                int iterations = mPhotos.size();

                if(iterations > 10){
                    iterations = 10;
                }

                mResults = 10;
                for(int i = 0; i < iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }

                mAdapter = new BranchfeedListAdapter(mContext, R.layout.layout_mainfeed_listitem, mPaginatedPhotos);
                mListView.setAdapter(mAdapter);

            }catch (NullPointerException e){
                Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
            }
        }
    }

    public void displayMorePhotos(){
        Log.d(TAG, "displayMorePhotos: displaying more photos");

        try{

            if(mPhotos.size() > mResults && mPhotos.size() > 0){

                int iterations;
                if(mPhotos.size() > (mResults + 10)){
                    Log.d(TAG, "displayMorePhotos: there are greater than 10 more photos");
                    iterations = 10;
                }else{
                    Log.d(TAG, "displayMorePhotos: there is less than 10 more photos");
                    iterations = mPhotos.size() - mResults;
                }

                //add the new photos to the paginated results
                for(int i = mResults; i < mResults + iterations; i++){
                    mPaginatedPhotos.add(mPhotos.get(i));
                }
                mResults = mResults + iterations;
                mAdapter.notifyDataSetChanged();
            }
        }catch (NullPointerException e){
            Log.e(TAG, "displayPhotos: NullPointerException: " + e.getMessage() );
        }catch (IndexOutOfBoundsException e){
            Log.e(TAG, "displayPhotos: IndexOutOfBoundsException: " + e.getMessage() );
        }
    }

    /*
     * Comment icon is selected
     */
    public void onCommentThreadSelected(Photo photo, String callingActivity){
        Log.d(TAG, "onCommentThreadSelected: selected a coemment thread");

        ViewCommentsFragment fragment  = new ViewCommentsFragment();
        Bundle args = new Bundle();
        args.putParcelable(getString(R.string.photo), photo);
        args.putString(getString(R.string.search_activity_branch), getString(R.string.search_activity_branch));
        fragment.setArguments(args);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        //transaction.show(fragment);
        transaction.addToBackStack(getString(R.string.view_comments_fragment));
        transaction.commit();

    }

    public void hideLayout(){
        Log.d(TAG, "hideLayout: hiding layout");
        mRelativeLayout.setVisibility(View.GONE);
        mFrameLayout.setVisibility(View.VISIBLE);
        mListView.setVisibility(View.GONE);
    }

    public void showLayout(){
        Log.d(TAG, "hideLayout: showing layout");
        mRelativeLayout.setVisibility(View.VISIBLE);
        mFrameLayout.setVisibility(View.GONE);
        mListView.setVisibility(View.VISIBLE);
    }
}