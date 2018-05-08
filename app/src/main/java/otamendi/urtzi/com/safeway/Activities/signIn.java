package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import net.rimoto.intlphoneinput.IntlPhoneInput;

import java.util.concurrent.TimeUnit;
import android.util.Log;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.signInAuth;

public class signIn extends AppCompatActivity {

    private Button verifyButton;
    private IntlPhoneInput phoneInputView;

    private User user;
    private static final String TAG = "SignIn";
    private static final int RESULT_PICK_CONTACT = 10;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        if( signInAuth.SignedIn()){
            if(!signInAuth.Configured()){
                pickContact();

            }else{

                sendHome();
            }
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
                            // Sign in success, update UI with the signed-in User's information
                            Log.d(TAG, "signInWithCredential:success");

                            FirebaseUser userF = task.getResult().getUser();
                            user = new User();
                            if(!signInAuth.Configured()){
                                pickContact();
                            }else{
                                sendHome();
                            }
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
        Intent intent = new Intent ( signIn.this ,HomeActivity.class );
        startActivity(intent);
    }


    private void pickContact(){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Log.d(TAG, "----- Contact picked");
                    emergencyContact(data);
                    break;
            }
        } else {
            Log.e(TAG, "Failed to pick contact");
        }
    }

    private void emergencyContact(Intent data){
        try{
            Uri uri= data.getData();
            Cursor cursor= getContentResolver().query(uri,null,null,null, null);
            cursor.moveToFirst();
            int phone= cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);
            String phoneNumber= cursor.getString(phone);
            Log.d(TAG, "----- Number " + phoneNumber);
            user= new User();
            user.setEmergencyNumber(phoneNumber);
            cursor.close();
            signInAuth.saveUser(user);
            sendHome();

        }catch (Exception e){
            e.printStackTrace();
        }
    }

}


