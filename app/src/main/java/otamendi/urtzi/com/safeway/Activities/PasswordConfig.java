package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import es.dmoral.toasty.Toasty;
import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.FirebasseMessaginService.FCMIdService;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;

public class PasswordConfig extends AppCompatActivity {

    private Toolbar toolbar;
    private Button passwordButton;
    private EditText passwordText;
    private String emergencyPhone;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_password_config);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
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

    private void configToolbar() {
        toolbar.setTitle(R.string.title_password_config);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    private void bindUI(){
        passwordButton = findViewById(R.id.setPassword);
        passwordText = findViewById(R.id.passText);
        toolbar= findViewById(R.id.passwordConfig_toolbar);
        Bundle extras = getIntent().getExtras();
       emergencyPhone = extras.getString("emergencyPhone");
    }

    private void setPassword(String pass){
        User user = new User(emergencyPhone, pass, FCMIdService.getToken());
        DatabaseService.saveUser(user);
        sendHome();

    }

    private void sendHome(){
        Intent intent = new Intent(PasswordConfig.this, safeWayHome.class);
        startActivity(intent);
        finish();
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
