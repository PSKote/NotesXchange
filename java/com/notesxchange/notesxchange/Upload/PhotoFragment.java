package com.notesxchange.notesxchange.Upload;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.content.FileProvider;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.notesxchange.notesxchange.Home.HomeActivity;
import com.notesxchange.notesxchange.Profile.AccountSettingsActivity;
import com.notesxchange.notesxchange.R;
import com.notesxchange.notesxchange.Utils.Permissions;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Uses camera for clicking images
 */

public class PhotoFragment extends Fragment {
    private static final String TAG = "PhotoFragment";

    //constant
    private static final int PHOTO_FRAGMENT_NUM = 1;
    private static final int GALLERY_FRAGMENT_NUM = 2;
    private static final int  CAMERA_REQUEST_CODE = 5;

    //vars
    private String mCurrentPhotoPath;
    private URL mSelectedImage;


    @Nullable
    @Override
    public View onCreateView(final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_photo, container, false);
        Log.d(TAG, "onCreateView: started.");

        Button btnLaunchCamera = (Button) view.findViewById(R.id.btnLaunchCamera);
        btnLaunchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: launching camera.");

                if(((UploadActivity)getActivity()).getCurrentTabNumber() == PHOTO_FRAGMENT_NUM){
                    if(((UploadActivity)getActivity()).checkPermissions(Permissions.CAMERA_PERMISSION[0])){
                        Log.d(TAG, "onClick: starting camera");
//                        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                        startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        // Ensure that there's a camera activity to handle the intent
                        if (takePictureIntent.resolveActivity(getActivity().getPackageManager()) != null) {
                            // Create the File where the photo should go
                            File photoFile = null;
                            try {
                                photoFile = createImageFile();
                            } catch (IOException ex) {
                                // Error occurred while creating the File
                            }
                            // Continue only if the File was successfully created
                            if (photoFile != null) {
//                                Uri photoURI = FileProvider.getUriForFile(getActivity(),
//                                        "com.notesxchange.notesxchange.fileprovider",
//                                        photoFile);
                                Uri photoURI = Uri.fromFile(photoFile);
                                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                                startActivityForResult(takePictureIntent, CAMERA_REQUEST_CODE);
                            }
                        }

                    }else{
                        Intent intent = new Intent(getActivity(), UploadActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                    }
                }
                //Toast.makeText(getActivity(), "Our next update will have this feature.", Toast.LENGTH_SHORT).show();
            }
        });

        return view;
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

                    // handle back button
                    Intent intent = new Intent(getActivity(), UploadActivity.class);
                    startActivity(intent);

                    return true;

                }

                return false;
            }
        });
    }

    /*
     * Create a copy of image to improve quality of image upload
     */
    private File createImageFile() throws IOException {
        // Create an image file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
//        String root = Environment.getExternalStorageDirectory().getPath();
//        File storageDir = new File(root + "/Download");
//        File storageDir = getActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES);
//        File image = File.createTempFile(
//                imageFileName,  /* prefix */
//                ".jpg",         /* suffix */
//                storageDir      /* directory */
//        );

        String root = Environment.getExternalStorageDirectory().getPath();
        File myDir = new File(root + "/NotesXchange");

        Log.d(TAG, "creating folder complete"+myDir);

        if (!myDir.exists()) {
            myDir.mkdirs();
        }
        String name = new Date().toString() + ".jpg";
        name = name.replace(" ", "_");
        File image = new File(myDir + File.separator + "IMG_" + name);

        // Save a file: path for use with ACTION_VIEW intents
        mCurrentPhotoPath = image.getAbsolutePath();
        return image;
    }

    private boolean isRootTask(){
        if(((UploadActivity)getActivity()).getTask() == 0){
            return true;
        }
        else{
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == CAMERA_REQUEST_CODE){
            Log.d(TAG, "onActivityResult: done taking a photo.");
            Log.d(TAG, "onActivityResult: attempting to navigate to final share screen.");

            Bitmap bitmap = null;
            try {
                //bitmap = (Bitmap) data.getExtras().get("data");
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inPreferredConfig = Bitmap.Config.ARGB_8888;
                bitmap = BitmapFactory.decodeFile(mCurrentPhotoPath, options);
            }catch (NullPointerException e) {
                Log.e(TAG, "onActivityResult: NullPointerException" + e.getMessage());
            }


            if(isRootTask()){
                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
//                    Intent intent = new Intent(getActivity(), NextActivity.class);
//                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
//                    startActivity(intent);

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(mCurrentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    getActivity().sendBroadcast(mediaScanIntent);

                    Log.d(TAG, "Image URL " + mCurrentPhotoPath);

                    try {
                        mSelectedImage = new URL(mCurrentPhotoPath);
                    }
                    catch (MalformedURLException e){
                        Log.e(TAG, "onActivityResult: MalformedURLException" + e.getMessage());
                    }
                    Intent intent = new Intent(getActivity(), NextActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), mCurrentPhotoPath);
                    startActivity(intent);

                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }
            }else{
//                try{
//                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
//                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
//                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
//                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
//                    startActivity(intent);
//                    getActivity().finish();
//                }catch (NullPointerException e){
//                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
//                }

                try{
                    Log.d(TAG, "onActivityResult: received new bitmap from camera: " + bitmap);
//                    Intent intent = new Intent(getActivity(), NextActivity.class);
//                    intent.putExtra(getString(R.string.selected_bitmap), bitmap);
//                    startActivity(intent);

                    Intent mediaScanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                    File f = new File(mCurrentPhotoPath);
                    Uri contentUri = Uri.fromFile(f);
                    mediaScanIntent.setData(contentUri);
                    getActivity().sendBroadcast(mediaScanIntent);

                    Log.d(TAG, "Image URL " + mCurrentPhotoPath);
                    Intent intent = new Intent(getActivity(), AccountSettingsActivity.class);
                    intent.putExtra(getString(R.string.selected_bitmap), mCurrentPhotoPath);
                    intent.putExtra(getString(R.string.return_to_fragment), getString(R.string.edit_profile_fragment));
                    startActivity(intent);
                    getActivity().finish();

                }catch (NullPointerException e){
                    Log.d(TAG, "onActivityResult: NullPointerException: " + e.getMessage());
                }

            }
        }
    }
}