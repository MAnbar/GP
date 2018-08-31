package com.gp.gp;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.util.SparseArray;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.google.android.gms.vision.Frame;
import com.google.android.gms.vision.face.Face;
import com.google.android.gms.vision.face.FaceDetector;
import com.google.android.gms.vision.face.Landmark;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ImageView myImageView = findViewById(R.id.imgview);
        myImageView.setImageResource(R.drawable.test1);

        Button button = findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Load the Image
                ImageView myImageView = findViewById(R.id.imgview);
                BitmapFactory.Options bitmapFactoryoptions = new BitmapFactory.Options();
                bitmapFactoryoptions.inMutable = true;
                Bitmap myBitmap = BitmapFactory.decodeResource(
                        getApplicationContext().getResources(),
                        R.drawable.test1,
                        bitmapFactoryoptions);

                //Create a Paint object for drawing overlay
                Paint myPaint = new Paint();
                myPaint.setStrokeWidth(8);
                myPaint.setColor(Color.GREEN);
                myPaint.setStyle(Paint.Style.STROKE);

                //Create a Canvas object for drawing on
                Bitmap tempBitmap = Bitmap.createBitmap(myBitmap.getWidth(), myBitmap.getHeight(), Bitmap.Config.RGB_565);
                Canvas tempCanvas = new Canvas(tempBitmap);
                tempCanvas.drawBitmap(myBitmap, 0, 0, null);

                //Create the Face Detector
                FaceDetector faceDetector = new
                        FaceDetector.Builder(getApplicationContext())
                        .setMode(FaceDetector.ACCURATE_MODE)
                        .setLandmarkType(FaceDetector.ALL_LANDMARKS)
                        .setClassificationType(FaceDetector.NO_CLASSIFICATIONS)
                        .setMinFaceSize(0.15f)
                        .setTrackingEnabled(false)
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

                    List<Landmark> landmarks = thisFace.getLandmarks();
                    for (Landmark landmark : landmarks) {
                        int type = landmark.getType();
                        if (type == Landmark.BOTTOM_MOUTH || type == Landmark.LEFT_MOUTH || type == Landmark.RIGHT_MOUTH) {

                            tempCanvas.drawPoint(landmark.getPosition().x, landmark.getPosition().y, myPaint);
                        }

                    }
                    float x1 = thisFace.getPosition().x;
                    float y1 = thisFace.getPosition().y;
                    float x2 = x1 + thisFace.getWidth();
                    float y2 = y1 + thisFace.getHeight();
                    tempCanvas.drawRoundRect(new RectF(x1, y1, x2, y2), 2, 2, myPaint);
                }
                myImageView.setImageDrawable(new BitmapDrawable(getResources(), tempBitmap));

                faceDetector.release();
            }
        });

    }
}