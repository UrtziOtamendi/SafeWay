package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMService;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.AuthService;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.permissionModule;

public class SplashScreen extends AppCompatActivity {

    private static final String TAG = "SplashScreen";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);
        ImageView imageView = (ImageView) findViewById(R.id.loading_gif);
        Glide.with(this).load(R.raw.loading).into(imageView);
        if (AuthService.SignedIn()) {
            FCMService.AutoInitEnable();
            DatabaseService.Configured(succesConfig, errorToast);
        }else{
            sendSignIn();
        }

    }



    public  SimpleCallback<Boolean> succesConfig= new SimpleCallback<Boolean>() {
        @Override
        public void callback(Boolean data) {
            if(data){
                sendHome();
            }else{
                pickContact();
            }
        }
    };

    private void pickContact() {
        Intent intent = new Intent(SplashScreen.this, emergencyPhone.class);
        startActivity(intent);
        finish();

    }


    private void sendHome() {
        Intent intent = new Intent(SplashScreen.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    private void sendSignIn(){
        Intent intent = new Intent(SplashScreen.this, signIn.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    public SimpleCallback<String> errorToast= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(SplashScreen.this,data, Toast.LENGTH_LONG).show();
            finish();
        }
    };


}
