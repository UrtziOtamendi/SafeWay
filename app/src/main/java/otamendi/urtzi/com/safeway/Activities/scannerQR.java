package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

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

import java.nio.ByteBuffer;
import java.util.List;

import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;
import otamendi.urtzi.com.safeway.Domain.receptorID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class scannerQR  extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "QR escaner";
    private ZXingScannerView mScannerView;
    private String name;
    private String senderName;
    private String senderUID;
    private FirebaseVisionBarcodeDetectorOptions options;
    private FirebaseVisionImage image;
    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
         options =
                new FirebaseVisionBarcodeDetectorOptions.Builder()
                        .setBarcodeFormats(FirebaseVisionBarcode.FORMAT_QR_CODE)
                        .build();

        mScannerView = new ZXingScannerView(this);   // Programmatically initialize the scanner view
        setContentView(mScannerView);                // Set the scanner view as the content view


        Bundle extras = getIntent().getExtras();
        name = extras.getString("linkedUserName");
    }

    @Override
    public void onResume() {
        super.onResume();
        mScannerView.setResultHandler(this); // Register ourselves as a handler for scan results.
        mScannerView.startCamera();          // Start camera on resume
    }

    @Override
    public void onPause() {
        super.onPause();
        mScannerView.stopCamera();           // Stop camera on pause
    }

    @Override
    public void handleResult(Result rawResult) {
        // Do something with the result here

        try{
            FirebaseVisionImageMetadata metadata = new FirebaseVisionImageMetadata.Builder()
                    .setWidth(1000)
                    .setHeight(1000)
                    .setFormat(FirebaseVisionImageMetadata.IMAGE_FORMAT_NV21)
                    .setRotation(0)
                    .build();

            ByteBuffer buffer = ByteBuffer.wrap( rawResult.getRawBytes());
            image = FirebaseVisionImage.fromByteBuffer(buffer, metadata);

            FirebaseVisionBarcodeDetector detector = FirebaseVision.getInstance()
                    .getVisionBarcodeDetector(options);

            Task<List<FirebaseVisionBarcode>> result = detector.detectInImage(image)
                    .addOnSuccessListener(new OnSuccessListener<List<FirebaseVisionBarcode>>() {
                        @Override
                        public void onSuccess(List<FirebaseVisionBarcode> barcodes) {
                            for (FirebaseVisionBarcode barcode: barcodes) {
                                Rect bounds = barcode.getBoundingBox();
                                Point[] corners = barcode.getCornerPoints();

                                String rawResult = barcode.getRawValue();

                                Log.v(TAG, rawResult);
                                String[] split= rawResult.split("\\s+");
                                senderName= split[0];
                                Log.d(TAG, "sender name--->"+ senderName);
                                senderUID= split[1];
                                DatabaseService.userExists(senderUID, getUserCallback, errorToastCallback);


                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            // Task failed with an exception
                            // ...
                        }
                    });

        }catch (Exception e){
            Toasty.error(scannerQR.this,getResources().getString( R.string.wrong_qr), Toast.LENGTH_LONG, true).show();

        }

    }

    public SimpleCallback<Boolean> getUserCallback= new  SimpleCallback<Boolean>(){
        @Override
        public void callback(Boolean data) {
            if(data){
                DatabaseService.getUsersLinks(geUserLinkers, errorToastCallback);
            }else{
                Toasty.error(scannerQR.this,getResources().getString( R.string.user_not_found), Toast.LENGTH_LONG, true).show();


            }
        }
    };

    public SimpleCallback<receptorID> geUserLinkers= new SimpleCallback<receptorID>() {
        @Override
        public void callback(receptorID data) {
                getLinkedUser(data);
        }
    };

    public  SimpleCallback<String> errorToastCallback= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toasty.error(scannerQR.this,data, Toast.LENGTH_LONG, true).show();

            returnSettings();
        }
    };

    private void getLinkedUser(receptorID receptorID) {
        receptorID newLink;
        if(receptorID ==null){
            Log.d(TAG,"OldDated user don't have any linked ID" + (receptorID ==null));
            newLink= new receptorID(name,senderUID);
        }
        else{
            if(receptorID.getLink1()==null){
                Log.d(TAG,"OldDated user don't have any linked ID" + (receptorID.getLink1()==null));
                newLink= new receptorID(name,senderUID);
            }else{
                newLink= new receptorID(receptorID.getName1(), receptorID.getLink1(),name,senderUID);
            }
        }

        DatabaseService.saveLinks(newLink);
        DatabaseService.linkReceptor(senderUID,senderName);
        returnSettings();

    }

    private void returnSettings(){
        Intent intent = new Intent(scannerQR.this, settingsActivity.class);
        startActivity(intent);

    }


}