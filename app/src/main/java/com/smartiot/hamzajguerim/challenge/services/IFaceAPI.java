package com.smartiot.hamzajguerim.challenge.services;

import android.graphics.Bitmap;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public interface IFaceAPI {
    String deleteFaceList(String face_list_id);
    void addFace(String face_id, Bitmap imageBitmap);
    void faceDetection(Bitmap imageBitmap);
    void faceRecognition(Bitmap imageBitmap);
}
