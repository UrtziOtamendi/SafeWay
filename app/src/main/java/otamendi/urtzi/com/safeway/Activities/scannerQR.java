package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class scannerQR  extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    private static final String TAG = "QR escaner";
    private ZXingScannerView mScannerView;
    private String name;
    private String senderName;
    private String senderUID;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
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
            Log.v(TAG, rawResult.getText()); // Prints scan results
            Log.v(TAG, rawResult.getBarcodeFormat().toString()); // Prints the scan format (qrcode, pdf417 etc.)

            String[] split= rawResult.getText().split("\\s+");

            senderName= split[0];
            Log.d(TAG, "sender name--->"+ senderName);
            senderUID= split[1];
            DatabaseService.userExists(senderUID, getUserCallback, errorToastCallback);
        }catch (Exception e){
            Toast.makeText(scannerQR.this,R.string.wrong_qr, Toast.LENGTH_LONG).show();
        }

    }

    public SimpleCallback<Boolean> getUserCallback= new  SimpleCallback<Boolean>(){
        @Override
        public void callback(Boolean data) {
            if(data){
                DatabaseService.getUsersLinks(geUserLinkers, errorToastCallback);
            }else{
                Toast.makeText(scannerQR.this, R.string.user_not_found, Toast.LENGTH_LONG).show();
            }
        }
    };

    public SimpleCallback<linkedID> geUserLinkers= new SimpleCallback<linkedID>() {
        @Override
        public void callback(linkedID data) {
                getLinkedUser(data);
        }
    };

    public  SimpleCallback<String> errorToastCallback= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(scannerQR.this,data, Toast.LENGTH_LONG).show();
            returnSettings();
        }
    };

    private void getLinkedUser(linkedID linkedID) {
        linkedID newLink;
        if(linkedID==null){
            Log.d(TAG,"OldDated user don't have any linked ID" + (linkedID ==null));
            newLink= new linkedID(name,senderUID);
        }
        else{
            if(linkedID.getLink1()==null){
                Log.d(TAG,"OldDated user don't have any linked ID" + (linkedID.getLink1()==null));
                newLink= new linkedID(name,senderUID);
            }else{
                newLink= new linkedID(linkedID.getName1(),linkedID.getLink1(),name,senderUID);
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