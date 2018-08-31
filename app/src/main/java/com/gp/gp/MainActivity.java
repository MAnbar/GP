package com.gp.gp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetector;
import com.google.firebase.ml.vision.face.FirebaseVisionFaceDetectorOptions;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    Bitmap myBitmap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView myImageView = (ImageView) findViewById(R.id.imgview);
        myImageView.setImageResource(R.drawable.test1);

        Button button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Load the Image
                ImageView myImageView = (ImageView) findViewById(R.id.imgview);
                BitmapFactory.Options bitmapFactoryoptions = new BitmapFactory.Options();
                bitmapFactoryoptions.inMutable = true;
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.test1,
                        bitmapFactoryoptions);

                //Create a Paint object for drawing overlay
                Paint myRectPaint = new Paint();
                myRectPaint.setStrokeWidth(10);
                myRectPaint.setColor(Color.RED);
                myRectPaint.setStyle(Paint.Style.STROKE);

                //Create a Canvas object for drawing on
                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

//                //Customize Face Detector
//                FirebaseVisionFaceDetectorOptions visionFaceDetectorOptions =
//                        new FirebaseVisionFaceDetectorOptions.Builder()
//                                .setModeType(FirebaseVisionFaceDetectorOptions.ACCURATE_MODE)
//                                .setLandmarkType(FirebaseVisionFaceDetectorOptions.ALL_LANDMARKS)
//                                .setClassificationType(FirebaseVisionFaceDetectorOptions.ALL_CLASSIFICATIONS)
//                                .setMinFaceSize(0.15f)
//                                .setTrackingEnabled(true)
//                                .build();

//                FirebaseVisionFaceDetector faceDetector = FirebaseVision.getInstance().getVisionFaceDetector(visionFaceDetectorOptions);

                //Create the Face Detector
                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext())
                        .setMode(FaceDetector.ACCURATE_MODE)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setClassificationType(FaceDetector.ALL_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(true)
                        .build();



                if (!faceDetector.isOperational()) {
                    new AlertDialog.Builder(v.getContext()).setMessage("Could not set up the face detector!").show();
                    return;
                }

                //Detect the Faces
                Frame frame = new Frame.Builder().setBitmap(myBitmap).build();
                SparseArray<Face> faces = faceDetector.detect(frame);

                //Draw Rectangles on the Faces
                for (int i = 0; i < faces.size(); i++) {
                    Face thisFace = faces.valueAt(i);
                    List<Landmark> landmarks= thisFace.getLandmarks();
                    for(Landmark mark : landmarks){
                        if(mark.getType()==Landmark.LEFT_MOUTH || mark.getType()==Landmark.RIGHT_MOUTH||mark.getType()==Landmark.BOTTOM_MOUTH){
                            System.out.println(mark.getType()+" "+mark.getPosition());
                            tempCanvas.drawPoint(mark.getPosition().x,mark.getPosition().y,myRectPaint);
                        }
                    }
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myRectPaint);
                }
                faceDetector.release();
                myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));
            }
        });

    }
}
