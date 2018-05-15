package otamendi.urtzi.com.safeway.Activities;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.zxing.Result;

import me.dm7.barcodescanner.zxing.ZXingScannerView;
import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.Utils.AuthService;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.linkUsers;

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
            DatabaseService.getUser(new SimpleCallback<User>(){
                @Override
                public void callback(User data) {
                    getLinkedUser(data);
                }
            }, errorToast );
        }catch (Exception e){
            Toast.makeText(scannerQR.this,"Wrong QR code", Toast.LENGTH_LONG).show();
        }

    }


    public  SimpleCallback<String> errorToast= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(scannerQR.this,data, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    private void getLinkedUser(User user) {
        linkedID linkedID= user.getLink();
        linkedID newLink;
        if(linkedID==null){
            newLink= new linkedID(name,senderUID);
        }
        else{
            if(linkedID.getLink1()==null){
                newLink= new linkedID(name,senderUID);
            }else{
                newLink= new linkedID(linkedID.getName1(),linkedID.getLink1(),name,senderUID);
            }
        }
        user.updateLink(newLink);
        DatabaseService.saveUser(user);
        linkUsers.linkReceptor(senderUID,senderName);
        finish();

    }


}