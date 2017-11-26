package com.smartiot.hamzajguerim.challenge;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.AddPersistedFaceResult;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.FaceRectangle;
import com.smartiot.hamzajguerim.challenge.app.MyApplication;
import com.smartiot.hamzajguerim.challenge.biometric.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class AddFaceToPersonActivity extends AppCompatActivity {

    // Background task of adding a face to person.
    class AddFaceTask extends AsyncTask<Void, String, Boolean> {
        List<Integer> mFaceIndices;
        AddFaceTask(List<Integer> faceIndices) {
            mFaceIndices = faceIndices;
        }

        @Override
        protected Boolean doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
            try{
                publishProgress("Adding face...");
                UUID personId = UUID.fromString(mPersonId);

                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
                InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());

                /*for (Integer index: mFaceIndices) {
                    FaceRectangle faceRect = mFaceGridViewAdapter.faceRectList.get(index);
                    Log.v("Request:", " Adding face to person " + mPersonId);
                    // Start the request to add face.
                    AddPersistedFaceResult result = faceServiceClient.addPersonFace(
                            mPersonGroupId,
                            personId,
                            imageInputStream,
                            "User data",
                            faceRect);

                    //mFaceGridViewAdapter.faceIdList.set(index, result.persistedFaceId);
                }*/
                return true;
            } catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("Exception: ", e.getMessage());
                return false;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(Boolean result) {
            setUiAfterAddingFace(result, mFaceIndices);
        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        private boolean mSucceed = true;

        @Override
        protected Face[] doInBackground(InputStream... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
            try{
                publishProgress("Detecting...");

                // Start detection.
                return faceServiceClient.detect(
                        params[0],  /* Input stream of image to detect */
                        true,       /* Whether to return face ID */
                        false,       /* Whether to return face landmarks */
                        /* Which face attributes to analyze, currently we support:
                           age,gender,headPose,smile,facialHair */
                        null);
            }  catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                Log.e("Exception:", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            setUiBeforeBackgroundTask();
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            setUiDuringBackgroundTask(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] faces) {
            if (mSucceed) {
                Log.i("Response:", " Success. Detected " + (faces == null ? 0 : faces.length)
                        + " Face(s)");
            }

            // Show the result on screen when detection is done.
            setUiAfterDetection(faces, mSucceed);
        }
    }

    private void setUiBeforeBackgroundTask() {
        mProgressDialog.show();
    }

    // Show the status of background detection task on screen.
    private void setUiDuringBackgroundTask(String progress) {
        mProgressDialog.setMessage(progress);
        Log.i("", progress);
    }

    private void setUiAfterAddingFace(boolean succeed, List<Integer> faceIndices) {
        mProgressDialog.dismiss();
    }

    // Show the result on screen when detection is done.
    private void setUiAfterDetection(Face[] result, boolean succeed) {
        mProgressDialog.dismiss();

        if (succeed) {
            // Set the information about the detection result.
            if (result != null) {
                Log.i("", result.length + " face"
                        + (result.length != 1 ? "s" : "") + " detected");
            } else {
                Log.i("","0 face detected");
            }


        }
    }

    //String mPersonGroupId;
    String mPersonId;
    String mImageUriStr;
    Bitmap mBitmap;

    // Progress dialog popped up when communicating with server.
    ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_face_to_person);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            mPersonId = bundle.getString("PersonId");
            //mPersonGroupId = bundle.getString("PersonGroupId");
            mImageUriStr = bundle.getString("ImageUriStr");
        }

        mProgressDialog = new ProgressDialog(this);
        mProgressDialog.setTitle("Add Face");
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("PersonId", mPersonId);
        //outState.putString("PersonGroupId", mPersonGroupId);
        outState.putString("ImageUriStr", mImageUriStr);
    }

    @Override
    protected void onRestoreInstanceState(@NonNull Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        mPersonId = savedInstanceState.getString("PersonId");
        //mPersonGroupId = savedInstanceState.getString("PersonGroupId");
        mImageUriStr = savedInstanceState.getString("ImageUriStr");
    }

    @Override
    protected void onResume() {
        super.onResume();

        Uri imageUri = Uri.parse(mImageUriStr);
        mBitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                imageUri, getContentResolver());
        if (mBitmap != null) {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            mBitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            InputStream imageInputStream = new ByteArrayInputStream(stream.toByteArray());
            Log.v("Request:", " Detecting " + mImageUriStr);
            new DetectionTask().execute(imageInputStream);
        }
    }

    public void doneAndSave() {
        List<Integer> faceIndices = new ArrayList<>();

        faceIndices.add(0);

        if (faceIndices.size() > 0) {
            new AddFaceTask(faceIndices).execute();
        } else {
            finish();
        }
    }


}
