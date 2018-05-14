package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.widget.Button;

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
import otamendi.urtzi.com.safeway.Utils.signInAuth;

public class SMS_Code extends Activity {



    private static final String TAG = "SMS_Code";
    private CodeInputView codeView;
    private Button SMSverifyButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        hideActionBar();
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
                            Configured();
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
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();
    }

    private void pickContact(){
        Intent intent = new Intent(SMS_Code.this, emergencyPhone.class);
        startActivity(intent);
        finish();

    }

    public void Configured() {
        FirebaseUser userF = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference mDatabase = FirebaseDatabase.getInstance().getReference();
        DatabaseReference userData = mDatabase.child("users").child(userF.getUid());
        Log.d("signInAuth", "-----------> " + userData.toString());
        userData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                User user = dataSnapshot.getValue(User.class);
                if (user != null) {
                    sendHome();
                }else{
                    pickContact();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

                Log.d("sigmInAuth", "Configured-------> " + databaseError.toString());
            }
        });
    }

    private void hideActionBar(){
        requestWindowFeature(Window.FEATURE_NO_TITLE);
    }

}
