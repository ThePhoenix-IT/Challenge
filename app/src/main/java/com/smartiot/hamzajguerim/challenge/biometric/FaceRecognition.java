package com.smartiot.hamzajguerim.challenge.biometric;

import android.graphics.Bitmap;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.smartiot.hamzajguerim.challenge.app.MyApplication;
import com.smartiot.hamzajguerim.challenge.services.IFaceAPI;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public class FaceRecognition implements IFaceAPI {
    @Override
    public void addFace(String face_id, Bitmap imageBitmap) {

        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
        //faceServiceClient.createFaceList(face_id);
        //faceServiceClient.addFacesToFaceList();
    }

    @Override
    public void faceDetection(Bitmap imageBitmap) {

    }

    @Override
    public void faceRecognition(Bitmap imageBitmap) {

    }
}
