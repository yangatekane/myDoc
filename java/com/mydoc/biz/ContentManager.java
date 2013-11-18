package com.mydoc.biz;

import android.content.Context;
import android.util.Log;

import com.mydoc.dto.Content;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.StreamCorruptedException;
import java.util.List;

/**
 * Created by yanga on 2013/11/02.
 */
public class ContentManager {
    private Context context;
    private static final String TAG = ContentManager.class.getName();
    public ContentManager(Context application) {
        this.context = application;
    }
    public void saveConteInfo(String dentalFile, List<Content> contents){
        try {
            FileOutputStream fileOut = context.openFileOutput(dentalFile, Context.MODE_PRIVATE);
            ObjectOutputStream out = new ObjectOutputStream(fileOut);
            out.writeObject(contents);
            out.close();
            fileOut.close();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(),e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }
    public List<Content> getContentInfo(String dentalFile){
        List<Content> dentalInfo = null;
        try {
                FileInputStream fileIn = context.openFileInput (dentalFile);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                dentalInfo = (List<Content>) in.readObject();
                in.close();
                fileIn.close();
        } catch (ClassNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (OptionalDataException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (StreamCorruptedException e) {
            Log.e(TAG, e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, e.getMessage(), e);
        }
        return dentalInfo;
    }
}
