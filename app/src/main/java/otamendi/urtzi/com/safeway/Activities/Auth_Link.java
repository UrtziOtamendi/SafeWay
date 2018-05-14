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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.R;

public class Auth_Link extends Activity {

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
                    Toast.makeText(Auth_Link.this,"Incorrect Name", Toast.LENGTH_LONG).show();

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
        Intent intent = new Intent(Auth_Link.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void readQR(String name){
        Intent intent = new Intent(Auth_Link.this, scannerQR.class);
        intent.putExtra("linkedUserName", name);
        startActivity(intent);
    }





}
