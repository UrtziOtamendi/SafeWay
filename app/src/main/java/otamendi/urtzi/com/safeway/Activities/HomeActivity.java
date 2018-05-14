package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;


import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.generateQR;

public class HomeActivity extends Activity {


    private static final String TAG = "HOME";


    private EditText codeNameQR;
    private Button createQR,readQR;
    private LinearLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        //Get elements
        bindUI();
        createQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String name= codeNameQR.getText().toString();
                if(name==null || name==""){
                   Log.w(TAG,"---> Nombre nullo o vacio");
                }else{

                    setQR(name);
                }
            }
        });

        readQR.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getLinkedUser();
            }
        });
    }

    private void bindUI(){
        imageLayout = findViewById(R.id.imageLayout);
        codeNameQR = findViewById(R.id.nameQR);
        createQR= findViewById(R.id.createQR);
        readQR= findViewById(R.id.readQR);
    }

    private void setQR(String linker){
        Bitmap qr = generateQR.generateFromString(linker);
        ImageView imageQR = new ImageView(this);
        imageQR.setImageBitmap(qr);
        imageLayout.addView(imageQR);
    }


    private void getLinkedUser() {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase= FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData= mDatabase.child("linkedID").child(userF.getUid());

        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                linkedID linkedID= dataSnapshot.getValue(linkedID.class);
                checkLinkAvailable(linkedID);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Toast.makeText(HomeActivity.this,"An error occurred! Try again later", Toast.LENGTH_LONG).show();
                Log.d(TAG, "GetLinked-------> "+databaseError.toString() );
            }
        });


    }

    private void checkLinkAvailable(linkedID linker){
        if(linker!=null) {
            if (linker.getLink1() == null || linker.getLink2() == null) {
                sendToLink();
            } else {
                Toast.makeText(HomeActivity.this, "You have linked accounts already", Toast.LENGTH_LONG).show();
            }
        }else{
            sendToLink();
        }

    }

    private void sendToLink( ){
        Intent intent= new Intent(HomeActivity.this, Auth_Link.class);
        startActivity(intent);
    }




}
