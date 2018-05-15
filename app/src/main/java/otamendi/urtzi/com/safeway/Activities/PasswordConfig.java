package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMIdService;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.AuthService;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;

public class PasswordConfig extends Activity {


    private Button passwordButton;
    private EditText passwordText;
    private String emergencyPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_config);
        //getActionBar().setDisplayHomeAsUpEnabled(true);

        bindUI();
        passwordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String pass= passwordText.getText().toString();
                if(pass!=null && pass!=""){
                    setPassword(pass);
                }
            }
        });
    }



    private void bindUI(){
        passwordButton = findViewById(R.id.setPassword);
        passwordText = findViewById(R.id.passText);
        Bundle extras = getIntent().getExtras();
       emergencyPhone = extras.getString("emergencyPhone");

    }

    private void setPassword(String pass){
       // Encrypt encryptModule = new Encrypt();
       // String encryptedPass= new String(encryptModule.encrypt(pass, FirebaseAuth.getInstance().getUid()));
        User user = new User(emergencyPhone, pass, FCMIdService.getToken());
        DatabaseService.saveUser(user);
        sendHome();

    }

    private void sendHome(){
        Intent intent = new Intent(PasswordConfig.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }

    private void hideActionBar(){
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_FULLSCREEN);
         getActionBar().hide();
    }

    @Override
    public boolean onNavigateUp(){
        Intent intent = new Intent(PasswordConfig.this, emergencyPhone.class);
        startActivity(intent);
        finish();
        finish();
        return true;
    }


}
