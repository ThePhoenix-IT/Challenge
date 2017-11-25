package com.smartiot.hamzajguerim.challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.SimilarFace;
import com.smartiot.hamzajguerim.challenge.app.MyApplication;

import java.util.Arrays;
import java.util.UUID;

import static com.smartiot.hamzajguerim.challenge.StartScreenActivity.REQUEST_IMAGE_CAPTURE;

public class FaceRecoActivity extends AppCompatActivity {

    private Bitmap imageBitmap;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        dispatchTakePictureIntent();
    }


    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);

        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            imageBitmap = (Bitmap) extras.get("data");
        }
    }
    // Background task for finding personal similar faces.
    private class FindPersonalSimilarFaceTask extends AsyncTask<UUID, String, SimilarFace[]> {
        private boolean mSucceed = true;

        @Override
        protected SimilarFace[] doInBackground(UUID... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
            Log.i("Request:", " Find matchPerson similar faces to " + params[0].toString() +
                    " in " + (params.length - 1) + " face(s)");
            try {
                publishProgress("Finding Similar Faces...");

                UUID[] faceIds = Arrays.copyOfRange(params, 1, params.length);
                // Start find similar faces.
                return faceServiceClient.findSimilar(
                        params[0],  /* The target face ID */
                        faceIds,    /*candidate faces */
                        4 /*max number of candidate returned*/
                );
            } catch (Exception e) {
                mSucceed = false;
                publishProgress(e.getMessage());
                Log.e("Exception", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            mProgressDialog.show();
        }
        @Override
        protected void onProgressUpdate(String... values) {
            // Show the status of background find similar face task on screen.
            setUiDuringBackgroundTask(values[0]);
        }

        @Override
        protected void onPostExecute(SimilarFace[] result) {
            if (mSucceed) {
                String resultString = "Found "
                        + (result == null ? "0": result.length)
                        + " matchPerson similar face" + ((result != null && result.length != 1)? "s": "");
                Log.i("Response:"," Success. " + resultString);
                Log.i("",resultString);
            }

            // Show the result on screen when verification is done.
            setUiAfterFindPersonalSimilarFaces(result);
        }
    }

    void setUiAfterFindPersonalSimilarFaces(SimilarFace[] result) {
        mProgressDialog.dismiss();
    }

    void setUiDuringBackgroundTask(String progress) {
        mProgressDialog.setMessage(progress);

        Log.i("Progress:", progress);
    }

    private void setDetectionStatus() {
        if (mBitmap == null && mTargetBitmap == null) {
            mProgressDialog.dismiss();
            Log.i("Info: ", "Detection is done");
        } else {
            mProgressDialog.setMessage("Detecting...");
            Log.i("Info: ", "Detecting...");
        }
    }

    // The faces in this image are added to the face collection in which to find similar faces.
    Bitmap mBitmap;

    // The faces in this image are added to the face collection in which to find similar faces.
    Bitmap mTargetBitmap;

    // The face collection view adapter.
    //FaceListAdapter mFaceListAdapter;

    // The face collection view adapter.
    //FaceListAdapter mTargetFaceListAdapter;

    // The face collection view adapter.
    //SimilarFaceListAdapter mSimilarFaceListAdapter;

    // Flag to indicate which task is to be performed.
    protected static final int REQUEST_ADD_FACE = 0;

    // Flag to indicate which task is to be performed.
    protected static final int REQUEST_SELECT_IMAGE = 1;

    // The ID of the target face to find similar face.
    private UUID mFaceId;

    // Progress dialog popped up when communicating with server.
    ProgressDialog mProgressDialog;
}
