package otamendi.urtzi.com.safeway.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;
import com.raycoarana.codeinputview.CodeInputView;

import java.util.concurrent.Executor;

import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.signInAuth;

public class SMS_Code extends AppCompatActivity {



    private static final String TAG = "SMS_Code";
    private CodeInputView codeView;
    private Button SMSverifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sms__code);
        bindUI();
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
       codeView= (CodeInputView) findViewById(R.id.smsInput);
        SMSverifyButton= (Button) findViewById(R.id.SMSverifyButton);
    }



    private void verifySMS(final String verificationId) {
                String code = codeView.getCode();
                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
                signInWithPhoneAuthCredential(credential);
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener((Executor) this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            if(signInAuth.Configured())
                            sendHome();
                        } else {
                            // Sign in failed, display a message and update the UI
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            if (task.getException() instanceof FirebaseAuthInvalidCredentialsException) {
                                // The verification code entered was invalid
                            }
                        }
                    }
                });
    }

    private void sendHome(){
        Intent intent = new Intent ( SMS_Code.this ,HomeActivity.class );
        startActivity(intent);
    }
}
