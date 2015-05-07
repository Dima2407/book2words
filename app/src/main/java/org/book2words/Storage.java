package org.book2words;

import android.os.Environment;

import java.io.File;

public class Storage {

    public static File createCoverFile(long id, String extension) {
        File root = new File(Environment.getExternalStorageDirectory(), ".b2w" + File.separator + "covers");
        root.mkdirs();

        return new File(root, id + extension);
    }

    public static String imageCoverUri(long id){
        File root = new File(Environment.getExternalStorageDirectory(), ".b2w" + File.separator + "covers");
        File file = new File(root, id + ".jpg");
        if(file.exists()){
            return "file://"+file.getAbsolutePath();
        }else {
            file = new File(root, id + ".png");
            if(file.exists()){
                return "file://"+file.getAbsolutePath();
            }
        }
        return "";
    }
}
