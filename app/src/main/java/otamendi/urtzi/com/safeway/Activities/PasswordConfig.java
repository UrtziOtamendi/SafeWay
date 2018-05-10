package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;


import com.google.firebase.auth.FirebaseAuth;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.Encrypt;
import otamendi.urtzi.com.safeway.Utils.signInAuth;

public class PasswordConfig extends AppCompatActivity {


    private Button passwordButton;
    private EditText passwordText;
    private String emergencyPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_config);
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
        User user = new User(emergencyPhone, pass);
        signInAuth.saveUser(user);
        sendHome();

    }

    private void sendHome(){
        Intent intent = new Intent(PasswordConfig.this, HomeActivity.class);
        startActivity(intent);
        finish();
    }
}
