package com.notesxchange.notesxchange.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

import com.notesxchange.notesxchange.Profile.AccountSettingsActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Utils.FilePaths;
import com.notesxchange.notesxchange.Utils.FileSearch;
import com.notesxchange.notesxchange.Utils.GridImageAdapter;

/**
 * Displays images available i nphone gallery
 */

public class GalleryFragment extends Fragment {
    private static final String TAG = "GalleryFragment";


    //constants
    private static final int NUM_GRID_COLUMNS = 3;


    //widgets
    private GridView gridView;
    private ImageView galleryImage;
    private ProgressBar mProgressBar;
    private Spinner directorySpinner;

    //vars
    private ArrayList<String> directories;
    private String mAppend = "file:/";
    private String mSelectedImage;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_gallery, container, false);
        galleryImage = (ImageView) view.findViewById(R.id.galleryImageView);
        gridView = (GridView) view.findViewById(R.id.gridView);
        directorySpinner = (Spinner) view.findViewById(R.id.spinnerDirectory);
        mProgressBar = (ProgressBar) view.findViewById(R.id.progressBar);
        mProgressBar.setVisibility(View.GONE);
        directories = new ArrayList<>();
        Log.d(TAG, "onCreateView: started.");

        ImageView shareClose = (ImageView) view.findViewById(R.id.ivCloseShare);
        shareClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: closing the gallery fragment.");
                getActivity().finish();
            }
        });


        //Go to tagging screen on selecting image
        TextView nextScreen = (TextView) view.findViewById(R.id.tvNext);
        nextScreen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: navigating to the final share screen.");

                if(isRootTask()){
                    if(mSelectedImage != null){
                        Intent intent = new Intent(getActivity(), NextActivity.class);
                        intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                        startActivity(intent);
                    }
                    else {
                        Toast.makeText(getActivity(), "You have not selected an image", Toast.LENGTH_SHORT).show();
                    }
                }else{
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_image), mSelectedImage);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();
                }

            }
        });

        init();

        return view;
    }

    private boolean isRootTask(){
        if(((UploadActivity)getActivity()).getTask() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    //Initialize folders consisting of images
    private void init(){
        FilePaths filePaths = new FilePaths();

        //check for other folders indide "/storage/emulated/0/pictures"
        if(FileSearch.getDirectoryPaths(filePaths.PICTURES) != null){
            directories = FileSearch.getDirectoryPaths(filePaths.PICTURES);
        }

        directories.add(filePaths.CAMERA);
        File checkExistence = new File(filePaths.WHATSAPP);
        if (checkExistence.exists()){
            directories.add(filePaths.WHATSAPP);
        }

        checkExistence = new File(filePaths.BLUETOOTH);
        if (checkExistence.exists()){
            directories.add(filePaths.BLUETOOTH);
        }

        checkExistence = new File(filePaths.CAMSCANNER);
        if (checkExistence.exists()){
            directories.add(filePaths.CAMSCANNER);
        }

        checkExistence = new File(filePaths.DOWNLOAD);
        if (checkExistence.exists()){
            directories.add(filePaths.DOWNLOAD);
        }


        ArrayList<String> directoryNames = new ArrayList<>();
        for(int i = 0; i < directories.size(); i++){

            int index = directories.get(i).lastIndexOf("/");
            String string = directories.get(i).substring(index).replace("/", "");
            directoryNames.add(string);
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(),
                android.R.layout.simple_spinner_item, directoryNames);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        directorySpinner.setAdapter(adapter);

        directorySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected: " + directories.get(position));

                //setup our image grid for the directory chosen
                setupGridView(directories.get(position));
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }


    /*
     * Grid view to dispaly images
     */
    private void setupGridView(String selectedDirectory){
        Log.d(TAG, "setupGridView: directory chosen: " + selectedDirectory);
        final ArrayList<String> imgURLs = FileSearch.getFilePaths(selectedDirectory);
        //final ArrayList<String> imgURLs = new ArrayList<String>(unSortedimgURLs.size());

        Log.d(TAG, "Starting to convert path to File -------------");

        ArrayList<File> compareImageFile = new ArrayList<File>(imgURLs.size());
        for (String newImageURL : imgURLs) {
            File newImageFile = new File(newImageURL);
            compareImageFile.add(newImageFile);
        }

        Log.d(TAG, "Sorting Image Files *************");

        Collections.sort(compareImageFile, new Comparator<File>(){
            public int compare(File img1, File img2)
            {
                //return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
                if ((img1).lastModified() > (img2).lastModified()) {
                    return -1;
                } else if ((img1).lastModified() < (img2).lastModified()) {
                    return +1;
                } else {
                    return 0;
                }
            }
        });


        Log.d(TAG, "Getting path of Image files *************");

        int i=0;
        for(File f: compareImageFile) {
            imgURLs.set(i, f.getAbsolutePath());
            i++;
        }

/*************************************************
//        Collections.sort(imgURLs, new Comparator<String>(){
//            public int compare(String img_1, String img_2)
//            {
//                Path img1 = Paths.get(img_1);
//                Path img2 = Paths.get(img_2);
//                //return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
//                if (Files.getLastModifiedTime(img1) > Files.getLastModifiedTime(img2)) {
//                    return -1;
//                } else if (Files.getLastModifiedTime(img1) < Files.getLastModifiedTime(img2)) {
//                    return +1;
//                } else {
//                    return 0;
//                }
//            }
//        });
 ***********************************************/


        //set the grid column width
        System.gc();
        int gridWidth = getResources().getDisplayMetrics().widthPixels;
        int imageWidth = gridWidth/NUM_GRID_COLUMNS;
        gridView.setColumnWidth(imageWidth);

        Log.d(TAG, "********* I'm hogging memory *************");

        //use the grid adapter to adapter the images to gridview
        GridImageAdapter adapter = new GridImageAdapter(getActivity(), R.layout.layout_grid_imageview, mAppend, imgURLs);
        gridView.setAdapter(adapter);

        Log.d(TAG, "------------ I'm out of it -------------");

        //set the first image to be displayed when the activity fragment view is inflated
        try {
            setImage(imgURLs.get(0), galleryImage, mAppend);
            mSelectedImage = imgURLs.get(0);
            System.gc();
        }catch (IndexOutOfBoundsException e) {
            Log.e(TAG, "setupGridView: ArrayIndexOutOfBoundsException" + e.getMessage());
        }


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.d(TAG, "onItemClick: selected an image: " + imgURLs.get(position));

                setImage(imgURLs.get(position), galleryImage, mAppend);
                mSelectedImage = imgURLs.get(position);
                System.gc();
            }
        });

    }


    private void setImage(String imgURL, ImageView image, String append){
        Log.d(TAG, "setImage: setting image");

        ImageLoader imageLoader = ImageLoader.getInstance();

        imageLoader.displayImage(append + imgURL, image, new ImageLoadingListener() {
            @Override
            public void onLoadingStarted(String imageUri, View view) {
                mProgressBar.setVisibility(View.VISIBLE);
            }

            @Override
            public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }

            @Override
            public void onLoadingCancelled(String imageUri, View view) {
                mProgressBar.setVisibility(View.INVISIBLE);
            }
        });
    }
}