package com.example.mygpslogger;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileOutputStream;


public class FileManager {

    int index=0;
    Context ctx;
    Activity activity;

    private static String[] PERMISSIONS_STORAGE = {
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    public FileManager(Context ctx, Activity activity)
    {
        this.ctx=ctx;
        this.activity=activity;
    }

    void writeToFile(String dataString, String nameOfFile) {
        verifyStoragePermissions(activity);
        File f = new File("/sdcard/GPSLogger/"+nameOfFile+".txt");
        try {
            f.createNewFile();
            FileOutputStream fout = new FileOutputStream(f, true);
            fout.write(dataString.getBytes());
            fout.close();
        } catch (Exception e) {
            Toast.makeText(activity.getBaseContext(), e.getMessage() + "\n" + f.getPath(), Toast.LENGTH_LONG).show();
        }
    }

    public String getAvailableNameOfFile()
    {
        String fileName="LOG_FILE_";
        int ctr=0;
        File f = new File("/sdcard/GPSLogger/");
        String[] someFiles = f.list();
        while(true)
        {
            try {
                if (fileName + String.valueOf(index) == someFiles[ctr]) {
                    index++;
                    ctr++;
                } else {
                    index++;
                    break;
                }
            }
            catch (IndexOutOfBoundsException ex)
            {
                index++;
                break;
            }
        }
        return fileName+String.valueOf(index);
    }

    private static void verifyStoragePermissions(Activity activity) {
        int permission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                    activity,
                    PERMISSIONS_STORAGE,
                    1
            );
        }
    }
}
