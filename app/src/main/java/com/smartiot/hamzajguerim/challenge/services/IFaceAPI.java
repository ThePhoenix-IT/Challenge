package com.smartiot.hamzajguerim.challenge.services;

import android.graphics.Bitmap;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public interface IFaceAPI {
    void addFace(String face_id, Bitmap imageBitmap);
    void faceDetection(Bitmap imageBitmap);
    void faceRecognition(Bitmap imageBitmap);
}
