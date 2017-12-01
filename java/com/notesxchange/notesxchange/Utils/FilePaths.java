package com.notesxchange.notesxchange.Utils;

import android.os.Environment;

/**
 * Path where images exists
 */

public class FilePaths {

    //"storage/emulated/0"
    public String ROOT_DIR = Environment.getExternalStorageDirectory().getPath();

    public String PICTURES = ROOT_DIR + "/Pictures";
    public String CAMERA = ROOT_DIR + "/DCIM/camera";
    public String DOWNLOAD = ROOT_DIR + "/Download";
    public String WHATSAPP = ROOT_DIR + "/WhatsApp/Media/WhatsApp Images";
    public String BLUETOOTH = ROOT_DIR + "/bluetooth";
    public String CAMSCANNER = ROOT_DIR + "/CamScanner/images";

    public String FIREBASE_IMAGE_STORAGE = "photos/users/";

}