package otamendi.urtzi.com.safeway.Activities;

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


import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.generateQR;

public class HomeActivity extends AppCompatActivity {


    private static final String TAG = "HOME";
    private BottomNavigationView.OnNavigationItemSelectedListener mOnNavigationItemSelectedListener
            = new BottomNavigationView.OnNavigationItemSelectedListener() {

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            switch (item.getItemId()) {
                case R.id.navigation_home:
                    return true;
                case R.id.navigation_dashboard:
                    return true;
                case R.id.navigation_notifications:
                    return true;
            }
            return false;
        }
    };

    private EditText codeNameQR;
    private Button createQR,readQR;
    private LinearLayout imageLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        BottomNavigationView navigation = (BottomNavigationView) findViewById(R.id.navigation);
        navigation.setOnNavigationItemSelectedListener(mOnNavigationItemSelectedListener);

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
               readQR();
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


    private void readQR(){
        Intent intent = new Intent(HomeActivity.this, scannerQR.class);
        startActivity(intent);
    }




}
