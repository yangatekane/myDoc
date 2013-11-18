package com.mydoc.dto;

import java.io.Serializable;

import retrofit.mime.TypedFile;

/**
 * Created by yanga on 2013/10/28.
 */
public class Content implements Serializable{
    public String fileName;
    public String fileSizeInBytes;
    public String file;

    public Content(String fileName, String fileSizeInBytes, String file) {
        this.fileName = fileName;
        this.fileSizeInBytes = fileSizeInBytes;
        this.file = file;
    }
}
