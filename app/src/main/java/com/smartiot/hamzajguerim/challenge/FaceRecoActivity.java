package com.smartiot.hamzajguerim.challenge;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.microsoft.projectoxford.face.FaceServiceClient;
import com.microsoft.projectoxford.face.contract.Face;
import com.microsoft.projectoxford.face.contract.VerifyResult;
import com.smartiot.hamzajguerim.challenge.app.MyApplication;
import com.smartiot.hamzajguerim.challenge.biometric.ImageHelper;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.UUID;


public class FaceRecoActivity extends AppCompatActivity {

    // Background task for face verification.
    private class VerificationTask extends AsyncTask<Void, String, VerifyResult> {
        // The IDs of two face to verify.
        private UUID mFaceId;
        private UUID mPersonId;
        private String mPersonGroupId;

        VerificationTask (UUID faceId, String personGroupId, UUID personId1) {
            mFaceId = faceId;
            mPersonGroupId = personGroupId;
            mPersonId = personId1;
        }

        @Override
        protected VerifyResult doInBackground(Void... params) {
            // Get an instance of face service client to detect faces in image.
            FaceServiceClient faceServiceClient = MyApplication.getFaceServiceClient();
            try{
                publishProgress("Verifying...");

                // Start verification.
                return faceServiceClient.verify(
                        mFaceId,      /* The face ID to verify */
                        mPersonId);     /* The person ID to verify */
            }  catch (Exception e) {
                publishProgress(e.getMessage());
                Log.e("Excdeption: ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            Log.v("Request: ", "Verifying face " + FaceRecoActivity.this.mFaceId + " and person " + mPersonId);
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressDialog.setMessage(progress[0]);
        }

        @Override
        protected void onPostExecute(VerifyResult result) {
            if (result != null) {
                Log.v("Response:", " Success. Face " + FaceRecoActivity.this.mFaceId + " "
                        + mPersonId + (result.isIdentical ? " " : " don't ")
                        + "belong to person "+ FaceRecoActivity.this.mPersonId);
            }

            // Show the result on screen when verification is done.
            setUiAfterVerification(result);
        }
    }

    // Background task of face detection.
    private class DetectionTask extends AsyncTask<InputStream, String, Face[]> {
        // Index indicates detecting in which of the two images.
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
                Log.e("Exception: ", e.getMessage());
                return null;
            }
        }

        @Override
        protected void onPreExecute() {
            progressDialog.show();
            Log.v("", "Request: Detecting in image");
        }

        @Override
        protected void onProgressUpdate(String... progress) {
            progressDialog.setMessage(progress[0]);
        }

        @Override
        protected void onPostExecute(Face[] result) {
            // Show the result on screen when detection is done.
            setUiAfterDetection(result, mSucceed);
        }
    }

    // Flag to indicate which task is to be performed.
    private static final int REQUEST_SELECT_IMAGE = 0;

    // The IDs of the two faces to be verified.
    private UUID mFaceId;

    // The two images from where we get the two faces to verify.
    private Bitmap mBitmap;


    // Progress dialog popped up when communicating with server.
    ProgressDialog progressDialog;

    String mPersonGroupId;
    UUID mPersonId;

    // When the activity is created, set all the member variables to initial state.
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_face_reco);

        progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Face Recognition...");

        clearDetectedFaces();

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    // select a person for verification
    void setPersonSelected(int position) {

    }

    // Called when image selection is done. Begin detecting if the image is selected successfully.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Index indicates which of the two images is selected.
        if (requestCode != REQUEST_SELECT_IMAGE) {
            return;
        }

        if(resultCode == RESULT_OK) {
            // If image is selected successfully, set the image URI and bitmap.
            Bitmap bitmap = ImageHelper.loadSizeLimitedBitmapFromUri(
                    data.getData(), getContentResolver());
            if (bitmap != null) {
                clearDetectedFaces();

                // Set the image to detect
                mBitmap = bitmap;
                mFaceId = null;

                // Add verification log.
                Log.v("", "Image"  + ": " + data.getData() + " resized to " + bitmap.getWidth()
                        + "x" + bitmap.getHeight());

                // Start detecting in image.
                detect(bitmap);
            }
        }
    }

    // Clear the detected faces indicated by index.
    private void clearDetectedFaces() {
    }

    // Called when the "Select Image" button is clicked in face face verification.
    public void selectImage() {
        Intent intent = new Intent(this, TakePictureActivity.class);
        startActivityForResult(intent, REQUEST_SELECT_IMAGE);
    }

    // Called when the "Verify" button is clicked.
    public void verify() {
        new VerificationTask(mFaceId, mPersonGroupId, mPersonId).execute();
    }




    // Show the result on screen when verification is done.
    private void setUiAfterVerification(VerifyResult result) {
        // Verification is done, hide the progress dialog.
        progressDialog.dismiss();


        // Show verification result.
        if (result != null) {
            DecimalFormat formatter = new DecimalFormat("#0.00");
            String verificationResult = (result.isIdentical ? "The same person": "Different persons")
                    + ". The confidence is " + formatter.format(result.confidence);
            Log.i("Info", verificationResult);
        }
    }

    // Show the result on screen when detection in image that indicated by index is done.
    private void setUiAfterDetection(Face[] result,boolean succeed) {

        if (succeed) {
            Log.v("Response:", " Success. Detected "
                    + result.length + " face(s) in image");

            Log.v("", result.length + " face" + (result.length != 1 ? "s": "")  + " detected");

            mBitmap = null;
        }

        if (result != null && result.length == 0) {
            Log.i("Info", "No face detected!");
        }

        progressDialog.dismiss();

    }

    // Start detecting in image specified by index.
    private void detect(Bitmap bitmap) {
        // Put the image into an input stream for detection.
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, output);
        ByteArrayInputStream inputStream = new ByteArrayInputStream(output.toByteArray());

        // Start a background task to detect faces in the image.
        new DetectionTask().execute(inputStream);

        // Set the status to show that detection starts.
        Log.v("", "Detecting...");
    }

}
