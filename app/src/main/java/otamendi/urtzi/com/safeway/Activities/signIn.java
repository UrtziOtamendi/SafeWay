package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
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
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.concurrent.TimeUnit;

import android.util.Log;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.AuthService;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;

public class signIn extends Activity {

    private static final String TAG = "SignIn";
    private static final int RESULT_PICK_CONTACT = 10;
    private Button verifyButton;
    private IntlPhoneInput phoneInputView;
    private User user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
        setContentView(R.layout.activity_sign_in);
        if (AuthService.SignedIn()) {
            DatabaseService.Configured(succesConfig, errorToast);
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


    private void bindUI() {
        verifyButton = (Button) findViewById(R.id.verifyButton);
        phoneInputView = (IntlPhoneInput) findViewById(R.id.phone_input);
    }

    private void verifyNumber() {
        String myInternationalNumber;
        if (phoneInputView.isValid()) {
            myInternationalNumber = phoneInputView.getNumber();
            phoneAuth(myInternationalNumber);
        } else {
            Toast.makeText(this, "Numero no valido", Toast.LENGTH_SHORT).show();
        }
    }

    public void phoneAuth(String number) {
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

    private void createIntentSMS(String verificationID) {
        Intent intent = new Intent(signIn.this, SMS_Code.class);
        intent.putExtra("verificationID", verificationID);
        startActivity(intent);

    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithCredential:success");
                            DatabaseService.Configured(succesConfig, errorToast);

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


    public  SimpleCallback<String> errorToast= new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(signIn.this,data, Toast.LENGTH_LONG).show();
            finish();
        }
    };

    public  SimpleCallback<User> succesConfig= new SimpleCallback<User>() {
        @Override
        public void callback(User data) {
            if(data!=null){
                sendHome();
            }else{
                pickContact();
            }
        }
    };


    private void sendHome() {
        Intent intent = new Intent(signIn.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }


    private void pickContact() {
        Intent intent = new Intent(signIn.this, emergencyPhone.class);

        startActivity(intent);
        finish();

    }



    private void hideActionBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }


}


