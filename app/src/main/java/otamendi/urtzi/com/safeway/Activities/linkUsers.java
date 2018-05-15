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

import otamendi.urtzi.com.safeway.R;

public class linkUsers extends Activity {

    private Button skipButton;
    private EditText linkedUserName;
    private FloatingActionButton scann;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth__link);
        bindUI();
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
                    readQR(name);
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





}
