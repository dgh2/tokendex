package com.scanner.tokendex;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import org.opencv.android.BaseLoaderCallback;
import org.opencv.android.CameraBridgeViewBase;
import org.opencv.android.CameraBridgeViewBase.CvCameraViewListener;
import org.opencv.android.JavaCameraView;
import org.opencv.android.LoaderCallbackInterface;
import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.DMatch;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDMatch;
import org.opencv.core.MatOfKeyPoint;
import org.opencv.core.Scalar;
import org.opencv.features2d.DescriptorExtractor;
import org.opencv.features2d.DescriptorMatcher;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.imgproc.Imgproc;

import java.io.IOException;

public class ScannerActivity extends Activity implements CvCameraViewListener, View.OnClickListener {
    private static final String TAG = "ScannerActivity";

    private Mat frame;


    private static final double SCALED_DOWN_MAX_IMAGE_WIDTH = 480;
    private static final double SCALED_DOWN_MAX_IMAGE_HEIGHT = 270;

    private Mat drawImage = null;
    private Mat trainingImage;
    private Mat tempImage;
    private Mat descriptors;
    private Mat trainDescriptors;
    private MatOfDMatch descriptorMatches;

    private FeatureDetector featureDetector;
    private MatOfKeyPoint keyPoints;
    private DescriptorExtractor descriptorExtractor;
    private DescriptorMatcher descriptorMatcher;

    private CameraBridgeViewBase mOpenCvCameraView;
    private BaseLoaderCallback mLoaderCallback = new BaseLoaderCallback(this) {
        @Override
        public void onManagerConnected(int status) {
            switch (status) {
                case LoaderCallbackInterface.SUCCESS:
                    Log.i(TAG, "OpenCV loaded successfully");
                    mOpenCvCameraView.enableView();
                    break;
                default:
                    super.onManagerConnected(status);
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        Log.d(TAG, "Creating and setting view");
        mOpenCvCameraView = new JavaCameraView(this, -1);
        mOpenCvCameraView.enableFpsMeter();
        setContentView(mOpenCvCameraView);
        mOpenCvCameraView.setCvCameraViewListener(this);
        mOpenCvCameraView.setOnClickListener(this);
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!OpenCVLoader.initDebug()) {
            Log.d(TAG, "Internal OpenCV library not found. Using OpenCV Manager for initialization");
            OpenCVLoader.initAsync(OpenCVLoader.OPENCV_VERSION_3_0_0, this, mLoaderCallback);
        } else {
            Log.d(TAG, "OpenCV library found inside package. Using it!");
            mLoaderCallback.onManagerConnected(LoaderCallbackInterface.SUCCESS);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (mOpenCvCameraView != null) {
            mOpenCvCameraView.disableView();
        }
    }

    @Override
    public void onClick(View view) {
//        scannerView
//        if (temp_hasData) {
//            sendDebugIntent();
//        } else {
//            Toast.makeText(scannerView.getContext(), getResources().getString(R.string.no_result_toast), Toast.LENGTH_LONG).show();
//        }
        sendDebugIntent();
    }

    @Deprecated
    private void sendDebugIntent() {
        Intent intent = new Intent(this, ScanResultActivity.class);
        intent.putExtra("com.tokendex.TokenName", "Cookie of Gratitude");
        intent.putExtra("Cookies", "Delicious");
        intent.putExtra("Delicious", "Cookies");
        startActivity(intent);
    }

    @Override
    public void onCameraViewStarted(int width, int height) {
        featureDetector = FeatureDetector.create(FeatureDetector.BRISK);
        descriptorExtractor = DescriptorExtractor.create(DescriptorExtractor.ORB);
        descriptorMatcher = DescriptorMatcher.create(DescriptorMatcher.BRUTEFORCE_HAMMING);
        keyPoints = new MatOfKeyPoint();
        descriptors = new Mat();
        tempImage = new Mat();
        trainingImage = new Mat();
        trainDescriptors = new Mat();
        descriptorMatches = new MatOfDMatch();
        try {
            initializeTrainDesctiptors(getTrainingResource());
        } catch (IOException e) {
            Log.e(TAG, "Error getting training resource!", e);
        }
    }

    @Override
    public void onCameraViewStopped() {
    }

    @Override
    public Mat onCameraFrame(Mat frame) {
//        List<MatOfPoint> contours = new ArrayList<>();
//        Mat hierarchy = new Mat();
//
//        Imgproc.resize(frame, tempImage, getScaledDownSize(frame), 0, 0, Imgproc.INTER_AREA);
//        Imgproc.cvtColor(tempImage, tempImage, Imgproc.COLOR_BGR2GRAY);
//        Imgproc.blur(tempImage, tempImage, new Size(7, 7));
////        Imgproc.GaussianBlur(tempImage, tempImage, new Size(5, 5), 5, 5);
//        Imgproc.Canny(tempImage, tempImage, 50, 150);
//        Imgproc.threshold(tempImage, tempImage, 50, 255, Imgproc.THRESH_BINARY);
//        Imgproc.resize(tempImage, tempImage, frame.size(), 0, 0, Imgproc.INTER_AREA);
//
//        // find contours
////        Imgproc.findContours(tempImage, contours, hierarchy, Imgproc.RETR_CCOMP, Imgproc.CHAIN_APPROX_SIMPLE);
//        Imgproc.findContours(tempImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_NONE);
//
//        // if any contours exist...
//        if (hierarchy.size().height > 0 && hierarchy.size().width > 0) {
//            // display each contour in blue (red?)
//            for (int idx = 0; idx >= 0; idx = (int) hierarchy.get(0, idx)[0]) {
//                Imgproc.drawContours(frame, contours, idx, new Scalar(250, 0, 0));
//            }
//        }
//        return frame;


//        FeatureDetector ffd = FeatureDetector.create(FeatureDetector.FAST);
//        ffd.detect(/*Mat*/ images, /*MatOfKeyPoint*/ keyPoints, /*Mat*/ mask);
//        return frame;

//        final Mat rgba = ((CameraBridgeViewBase.CvCameraViewFrame) frame).rgba();
        Imgproc.cvtColor(frame, tempImage, Imgproc.COLOR_RGBA2GRAY);
        featureDetector.detect(tempImage, keyPoints);
        Features2d.drawKeypoints(frame, keyPoints, frame);
        descriptorExtractor.compute(tempImage, keyPoints, descriptors);
        descriptorMatcher.match(descriptors, trainDescriptors, descriptorMatches);
        return frame;
    }

//    private Size getScaledDownSize(final Mat frame) {
//        double wRatio = SCALED_DOWN_MAX_IMAGE_WIDTH / frame.width();
//        double hRatio = SCALED_DOWN_MAX_IMAGE_HEIGHT / frame.height();
//
//        if (wRatio >= hRatio) {
//            return new Size(frame.width() * wRatio, frame.height() * wRatio);
//        } else {
//            return new Size(frame.width() * hRatio, frame.height() * hRatio);
//        }
//    }

    private void initializeTrainDesctiptors(Mat frame) {
        if (trainDescriptors.empty()) {
            Imgproc.cvtColor(frame, trainingImage, Imgproc.COLOR_RGBA2GRAY);
            featureDetector.detect(trainingImage, keyPoints);
            Features2d.drawKeypoints(trainingImage, keyPoints, trainingImage);
            descriptorExtractor.compute(trainingImage, keyPoints, trainDescriptors);
        }
    }

    private Mat getTrainingResource() throws IOException {
        if (drawImage == null) {
            drawImage = new Mat();
            Utils.bitmapToMat(BitmapFactory.decodeResource(getResources(), R.drawable.drawable_token_base_with_symbols), drawImage);
        }
        return drawImage;
    }
}
