package com.spark.app.ocb.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;
import android.text.TextUtils;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

public class FileUtils {

    public static final int BUFF_SIZE = 1024;

    public static Context context;

    /*
     *
     */
    public static String openFileFromAssets(String fileName) throws IOException {
        AssetManager asm = context.getAssets();
        BufferedInputStream in = new BufferedInputStream(asm.open(fileName));

        return readTextFile(in);
    }

    /*
     *
     */
	public static String readTextFile(InputStream inputStream) throws IOException {

        StringBuffer sb = new StringBuffer();

        BufferedReader br = new BufferedReader(new InputStreamReader(inputStream));

        String line = null;
        while ((line = br.readLine()) != null){
            sb.append(line);
        }

        br.close();

        return sb.toString();

	}

    /*
     *
     */
    public static void copyStream(InputStream is, OutputStream os) {
        if (is == null || os == null) return;

        byte[] buffer = new byte[BUFF_SIZE];

        int n, readBytes = 0;
        try {
            while ((n = is.read(buffer)) != -1){
                readBytes += n;
                os.write(buffer);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /*
     *
     */
    public static boolean isExternalStorageReady(String type){
        return Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED);
    }

    /*
     *
     */
    public static String baseName(String fileName){
        File f = new File(fileName);
        return f.getName();
    }

    /*
     *
     */
    public static String baseNameWithoutExt(String fileName){
        String baseName = baseName(fileName);

        int i = baseName.lastIndexOf(".");
        if (i>0){
            return baseName.substring(0, i);
        }

        return baseName;
    }

    /*
     *
     */
    public String extension(String fileName){
        if (TextUtils.isEmpty(fileName)) return "";

        int i = fileName.lastIndexOf(".");
        if (i>0){
            return fileName.substring(i);
        }

        return "";
    }

}
