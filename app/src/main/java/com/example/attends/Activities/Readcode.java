package com.example.attends.Activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.attends.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.ml.vision.FirebaseVision;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcode;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetector;
import com.google.firebase.ml.vision.barcode.FirebaseVisionBarcodeDetectorOptions;
import com.google.firebase.ml.vision.common.FirebaseVisionImage;
import com.google.firebase.ml.vision.common.FirebaseVisionImageMetadata;
import com.google.zxing.Result;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;
import com.karumi.dexter.listener.single.PermissionListener;
import com.otaliastudios.cameraview.CameraView;
import com.otaliastudios.cameraview.Frame;
import com.otaliastudios.cameraview.FrameProcessor;

import java.util.List;

import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Readcode extends AppCompatActivity  {
CameraView camera_View;
boolean isDetected = false;
Button btn_start;
FirebaseVisionBarcodeDetectorOptions options;
FirebaseVisionBarcodeDetector detector;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_readcode);
        Dexter.withActivity(this)
                .withPermission(new String[] {Manifest.permission.CAMERA,Manifest.permission.RECORD_AUDIO})
               .withListener(new MultiplePermissionsListener() {
                   @Override
                   public void onPermissionsChecked(MultiplePermissionsReport report) {
                      setupCamera();
                   }

                   @Override
                   public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {

                   }
               }).check();

    }

    private void setupCamera() {
        btn_start = (Button)findViewById(R.id.view);

        btn_start.setEnabled(isDetected);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                isDetected = !isDetected;
                btn_start.setEnabled(isDetected);
            }
        });
        camera_View = (CameraView)findViewById(R.id.cameraview);
       camera_View.setLifecycleOwner(this);
       camera_View.addFrameProcessor(new FrameProcessor() {
           @Override
           public void process(@NonNull Frame frame) {
               processImage(getVisionImageFromFrame(frame));
           }
       });
       options = new  FirebaseVisionBarcodeDetectorOptions.Builder()
               .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
               .build();

       detector = FirebaseVision.getInstance().getVisionBarcodeDetector(options);

    }

    private void processImage(FirebaseVisionImage image) {
        if (!isDetected){
            Task<List<FirebaseVisionBarcode>> listTask = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
                          processResult(firebaseVisionBarcodes);
                        }
                    })
                    .addOnCompleteListener(new OnFailureList♦ener(){
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(Readcode.this, "nnnn", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    private void processResult(List<FirebaseVisionBarcode> firebaseVisionBarcodes) {
        if (firebaseVisionBarcodes.size() > 0){
            isDetected = true;
            btn_start.setEnabled(isDetected);
            for (FirebaseVisionBarcode item: firebaseVisionBarcodes)
            {
                int value_type = item.getValueType();
                switch (value_type)
                {
                    case  FirebaseVisionBarcode.TYPE_TEXT:
                    {
                        createDialog(item.getRawValue());
                    }
                    break;
                    case  FirebaseVisionBarcode.TYPE_URL:
                    {
                     //start Browser intent
                        Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(item.getRawValue()));
                        startActivity(intent);
                    }
                    break;
                    case  FirebaseVisionBarcode.TYPE_CONTACT_INFO:
                    {
                      String info =  new StringBuilder("Name: ")
                              .append(item.getContactInfo().getName() .getFormattedName())
                              .append("\n")
                              .append("Address: ")
                              .append(item.getContactInfo().getAddresses().get(0).getAddressLines()[0])
                              .append("\n")
                              .append("Email: ")
                              .append(item.getContactInfo().getEmails().get(0).getAddress())
                              .toString();
                      createDialog(info);
                    }
                    break;
                    default:
                        break;
                }
            }
        }
    }

    private void createDialog(String text) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(text)
                .setPositiveButton("ok", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private FirebaseVisionImage getVisionImageFromFrame(Frame frame) {
        byte[] data = frame.getData();
        FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                .setHeight(frame.getSize().getHeight())
                .setWidth(frame.getSize().getWidth())
              //  .setRotation(frame.getRotation())//only use it if you want to work on land scape mode - for prtrail, domnt use
        .build();
                return FirebaseVisionImage.fromByteArray(data,metadata);
    }

}
