package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.support.design.widget.FloatingActionButton;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import org.json.JSONException;
import org.json.JSONObject;

import otamendi.urtzi.com.safeway.R;

public class linkUsers extends Activity {

    private Button skipButton;
    private EditText linkedUserName;
    private FloatingActionButton scann;
    private IntentIntegrator qrScan;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth__link);
        bindUI();
        qrScan= new IntentIntegrator(this);
        skipButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendHome();
            }
        });
        scann.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= linkedUserName.getText().toString();
                Log.d("AUTH_LINK", "name--->"+ name);
                if(name!= null && !name.equals("")){
                   // readQR(name);
                    qrScan.initiateScan();
                }else{
                    Toast.makeText(linkUsers.this,"Incorrect Name", Toast.LENGTH_LONG).show();

                }
            }
        });
    }

    private void bindUI(){
        skipButton= findViewById(R.id.skipLinker);
        linkedUserName= findViewById(R.id.linkedUserName);
        scann= findViewById(R.id.scannQR);
    }

    private void sendHome() {
        Intent intent = new Intent(linkUsers.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void readQR(String name){
        Intent intent = new Intent(linkUsers.this, scannerQR.class);
        intent.putExtra("linkedUserName", name);
        startActivity(intent);
    }

    //Getting the scan results
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            //if qrcode has nothing in it
            if (result.getContents() == null) {
                Toast.makeText(this, "Result Not Found", Toast.LENGTH_LONG).show();
            } else {
                //if qr contains data
                try {
                    //converting the data to json
                    Log.e("ok"," " + RESULT_OK);
                    Log.e("QRSCAAAAAN","res" +resultCode);
                    Log.e("QRSCAAAAAN","req " +requestCode);
                    Log.e("QRSCAAAAAN",result.toString());

                    //setting values to textviews

                } catch (Exception e) {
                    e.printStackTrace();
                    //if control comes here
                    //that means the encoded format not matches
                    //in this case you can display whatever data is available on the qrcode
                    //to a toast
                    Toast.makeText(this, result.getContents(), Toast.LENGTH_LONG).show();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }



}
