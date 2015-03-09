package com.spark.app.ocb.util;

import android.content.Context;
import android.content.res.AssetManager;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class FileUtils {

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
	public static String readTextFile(InputStream inputStream) {
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		byte buf[] = new byte[1024];
		int len;
		try {
			while ((len = inputStream.read(buf)) != -1) {
				outputStream.write(buf, 0, len);
			}
			outputStream.close();
			inputStream.close();
		} catch (IOException e) {
		}
		return outputStream.toString();

	}
	
}
