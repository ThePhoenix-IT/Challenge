package com.smartiot.hamzajguerim.challenge.biometric;

import android.graphics.Bitmap;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.rest.ClientException;
import com.smartiot.hamzajguerim.challenge.app.MyApplication;
import com.smartiot.hamzajguerim.challenge.services.IFaceAPI;

import java.io.IOException;

/**
 * Created by hamzajguerim on 2017-11-25.
 */

public class FaceRecognition implements IFaceAPI {
    @Override
    public String deleteFaceList(String face_list_id) {
        FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
        try {
            Log.i("Info: ", "Deleting selected person groups...");
            Log.v("Request: ", "Delete Group " + face_list_id);

            faceServiceClient.deleteFaceList(face_list_id);
            return face_list_id;
        } catch (Exception e) {
            Log.e("Exception: ", e.getMessage());
            return null;
        }
    }

    @Override
    public void addFace(String face_id, Bitmap imageBitmap) {

        // Get an instance of face service client to detect faces in image.
        FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
        boolean exist = false;
        try {
            faceServiceClient.getFaceList(face_id);
            exist  = true;
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(exist) {
            deleteFaceList(face_id);
        }
        try {
            faceServiceClient.createFaceList(face_id, face_id, "face list for " + face_id);
        } catch (ClientException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void faceDetection(Bitmap imageBitmap) {

    }

    @Override
    public void faceRecognition(Bitmap imageBitmap) {

    }
}
