package com.dupreinca.dupree.mh_bar_scann;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import android.os.Bundle;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.dupreinca.dupree.R;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

import static com.dupreinca.dupree.mh_utilities.Validate.isNumeric;

public class ScanBarcodeActivity extends Activity{

    SurfaceView cameraPreview;
    private static final int MY_PERMISSION_REQUEST_CAMERA = 2569;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_scan_barcode);

        cameraPreview = (SurfaceView) findViewById(R.id.camera_preview);

        createCameraSorce();
    }


    private void createCameraSorce() {

        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(this).build();
        final com.google.android.gms.vision.CameraSource cameraSource = new CameraSource.Builder(this,barcodeDetector)
                .setAutoFocusEnabled(true)
                .setRequestedPreviewSize(1600,1024)
                .setRequestedFps(15.0f)
                .build();


        cameraPreview.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                try {
                    if (ActivityCompat.checkSelfPermission(ScanBarcodeActivity.this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                        // TODO: Consider calling
                        //    ActivityCompat#requestPermissions
                        // here to request the missing permissions, and then overriding
                        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                        //                                          int[] grantResults)
                        // to handle the case where the user grants the permission. See the documentation
                        // for ActivityCompat#requestPermissions for more details.
                        ActivityCompat.requestPermissions(ScanBarcodeActivity.this, new String[]{Manifest.permission.CAMERA},
                                MY_PERMISSION_REQUEST_CAMERA);
                        return;
                    }
                    cameraSource.start(cameraPreview.getHolder());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {


            }

            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {
                cameraSource.stop();
            }
        });

        barcodeDetector.setProcessor(new Detector.Processor<Barcode>() {
            @Override

            public void release() {

            }

            @Override
            public void receiveDetections(Detector.Detections<Barcode> detections) {
                final SparseArray<Barcode> barcodes = detections.getDetectedItems();

                if(barcodes.size() > 0){
                    String[] valuesBarcode  = barcodes.valueAt(0).displayValue.replaceAll("[^\\p{L}\\p{Nd}]+", "/").split("/");
                    String cedula = "";
                    String primer_apellido ="";
                    String valorInicial = "";
                    String segundo_apellido = "";
                    String nombre = "";

                    if(valuesBarcode[1].equalsIgnoreCase("PubDSK") && valuesBarcode[3].length() <= 6 ){
                        valorInicial = valuesBarcode[4];
                        segundo_apellido = valuesBarcode[5];
                        nombre = valuesBarcode[6];
                    }else if(valuesBarcode[1].equalsIgnoreCase("PubDSK") && valuesBarcode[3].length() > 6 ){
                        valorInicial = valuesBarcode[3];
                        valorInicial = valorInicial.substring(10,valorInicial.length());
                        segundo_apellido = valuesBarcode[4];
                        nombre = valuesBarcode[5];
                    }else if(!valuesBarcode[1].equalsIgnoreCase("PubDSK") && valuesBarcode[2].length() > 6){
                        valorInicial = valuesBarcode[2];
                        valorInicial = valorInicial.substring(8,valorInicial.length());
                        segundo_apellido = valuesBarcode[3];
                        nombre = valuesBarcode[4];
                    }else {
                        valorInicial = valuesBarcode[2];
                        segundo_apellido = valuesBarcode[3];
                        nombre = valuesBarcode[4];
                    }

                    char[] stringToCharArray = valorInicial.toCharArray();

                    for (char output : stringToCharArray) {
                        if(isNumeric(Character.toString(output))) {
                            cedula += output;

                        }else{
                            primer_apellido += output;
                        }
                    }

                    if(cedula.charAt(0) == '0' && cedula.charAt(1) == '0') {
                        cedula = cedula.substring(2,cedula.length());
                    }

                    Intent intent = new Intent();
                    intent.putExtra("number", cedula);
                    intent.putExtra("name", nombre);
                    intent.putExtra("lastname", primer_apellido +" "+segundo_apellido);
                    intent.putExtra("barcode",barcodes.valueAt(0).rawValue);//get the lastest barcode from the array
                    setResult(CommonStatusCodes.SUCCESS,intent);
                    finish();
                }
            }
        });


    }
}
