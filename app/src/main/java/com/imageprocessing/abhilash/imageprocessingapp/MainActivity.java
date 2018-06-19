package com.imageprocessing.abhilash.imageprocessingapp;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.text.TextBlock;
import com.google.android.gms.vision.text.TextRecognizer;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    SurfaceView cameraView;
    TextView textView;
    CameraSource cameraSource;
    final int requestcameraupdateid = 1001;


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

     switch (requestCode)
     {
         case requestcameraupdateid:
             if(grantResults[0] ==PackageManager.PERMISSION_GRANTED)
             {
                 if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED)
                 {
                     return;
                 }
                 try {
                     cameraSource.start(cameraView.getHolder());
                 } catch (IOException e) {
                     e.printStackTrace();
                 }

             }
     }

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        cameraView = (SurfaceView) findViewById(R.id.surfaceview);
        textView = (TextView) findViewById(R.id.textview);
        TextRecognizer textRecognizer = new TextRecognizer.Builder(getApplicationContext()).build();
        if (!textRecognizer.isOperational()) {
            Toast.makeText(MainActivity.this, "Dependencies not loaded,", Toast.LENGTH_SHORT).show();

        } else {
            cameraSource = new CameraSource.Builder(getApplicationContext(), textRecognizer)
                    .setAutoFocusEnabled(true)
                    .setFacing(CameraSource.CAMERA_FACING_BACK)
                    .setRequestedFps(2.0f)
                    .setRequestedPreviewSize(1280, 1024)
                    .build();
            cameraView.getHolder().addCallback(new SurfaceHolder.Callback() {
                @Override
                public void surfaceCreated(SurfaceHolder surfaceHolder) {
                    try {
                        if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.

                           ActivityCompat.requestPermissions(MainActivity.this,new String[]{
                                   Manifest.permission.CAMERA
                           },requestcameraupdateid);
                            return;
                        }
                        cameraSource.start(cameraView.getHolder());
             } catch (IOException e) {
                 e.printStackTrace();
             }
         }

         @Override
         public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

         }

         @Override
         public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
                cameraSource.stop();
         }
     });

           textRecognizer.setProcessor(new Detector.Processor<TextBlock>() {
               @Override
               public void release() {

               }

               @Override
               public void receiveDetections(Detector.Detections<TextBlock> detections) {
                          final SparseArray<TextBlock> items=detections.getDetectedItems();
                          if(items.size() !=0)
                          {
                              textView.post(new Runnable() {
                                  @Override
                                  public void run() {
                                      StringBuilder stringBuilder=new StringBuilder();
                                      for(int i=0;i<items.size();i++)
                                      {
                                          TextBlock item= items.valueAt(i);
                                          stringBuilder.append(item.getValue());
                                          stringBuilder.append("\n");
                                      }
                                      textView.setText(stringBuilder.toString());
                                  }
                              });
                          }
               }
           });
 }

    }
}
