package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.FirebaseTooManyRequestsException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.concurrent.Executor;
import java.util.concurrent.TimeUnit;
import android.util.Log;

import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.phoneAuthSignIn;

import static java.security.AccessController.getContext;

public class signIn extends AppCompatActivity {

    private Button verifyButton;
    private IntlPhoneInput phoneInputView;

    private static final String TAG = "SignIn";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if( user != null){
            Intent intent = new Intent ( signIn.this ,HomeActivity.class );
            startActivity(intent);
        }
        bindUI();
        verifyButton.setOnClickListener(
                new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        verifyNumber();
                    }
                }
        );
    }


    private void bindUI(){
        verifyButton= (Button) findViewById(R.id.verifyButton);
        phoneInputView= (IntlPhoneInput) findViewById(R.id.phone_input);
    }

    private void verifyNumber(){
        String myInternationalNumber;
        if(phoneInputView.isValid()) {
            myInternationalNumber = phoneInputView.getNumber();
            phoneAuth(myInternationalNumber);
        }else{
            Toast.makeText(this, "Numero no valido", Toast.LENGTH_SHORT).show();
        }
    }

    public void phoneAuth(String number){
        PhoneAuthProvider.getInstance().verifyPhoneNumber(
                number,        // Phone number to verify
                60,                 // Timeout duration
                TimeUnit.SECONDS,   // Unit of timeout
                this,               // Activity (for callback binding)
                new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {

                    @Override
                    public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
                        Log.d(TAG, "onVerificationCompleted:" + phoneAuthCredential);


                        signInWithPhoneAuthCredential(phoneAuthCredential);
                    }

                    @Override
                    public void onVerificationFailed(FirebaseException e) {
                        Log.w(TAG, "onVerificationFailed", e);

                        if (e instanceof FirebaseAuthInvalidCredentialsException) {
                            // Invalid request
                            // ...
                        } else if (e instanceof FirebaseTooManyRequestsException) {
                            // The SMS quota for the project has been exceeded
                            // ...
                        }
                    }



                    @Override
                    public void onCodeSent(String verificationId,
                                           PhoneAuthProvider.ForceResendingToken token) {

                        Log.d(TAG, "onCodeSent:" + verificationId);

                       // mVerificationId = verificationId;
                        //mResendToken = token;
                        createIntentSMS(verificationId);

                    }
                });
    }

    private void createIntentSMS(String verificationID){
        Intent intent = new Intent ( signIn.this ,SMS_Code.class );
        intent.putExtra( "verificationID", verificationID );
        startActivity(intent);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser user = task.getResult().getUser();
                            Intent intent = new Intent ( signIn.this ,HomeActivity.class );
                            startActivity(intent);
                            // ...
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



}
