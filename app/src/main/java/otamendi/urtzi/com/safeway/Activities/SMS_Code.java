package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.concurrent.Executor;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class SMS_Code extends AppCompatActivity {


    private Toolbar toolbar;
    private static final String TAG = "SMS_Code";
    private CodeInputView codeView;
    private Button SMSverifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms__code);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        Bundle extras = getIntent().getExtras();
        final String verificationID = extras.getString("verificationID");
        SMSverifyButton.setOnClickListener(  new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                verifySMS(verificationID);
            }
        });
    }
    public void bindUI(){
        toolbar = (Toolbar) findViewById(R.id.smsCode_toolbar);
       codeView= (CodeInputView) findViewById(R.id.smsInput);
        SMSverifyButton= (Button) findViewById(R.id.SMSverifyButton);
    }
    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        toolbar.setTitle(R.string.title_activity_sms_code);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


    }


    private void verifySMS(final String verificationId) {
                String code = codeView.getCode();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d(TAG, "signInWithCredential:success");
                            DatabaseService.Configured(succesConfig, errorToast);
                        } else {
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                            }
                        }
                    }
                });
    }

    private void sendHome(){
        Intent intent = new Intent ( SMS_Code.this ,safeWayHome.class );
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void pickContact(){
        Intent intent = new Intent(SMS_Code.this, emergencyPhone.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }

    public SimpleCallback<String> errorToast= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(SMS_Code.this,data, Toast.LENGTH_LONG).show();
            finish();
        }
    };

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




}
