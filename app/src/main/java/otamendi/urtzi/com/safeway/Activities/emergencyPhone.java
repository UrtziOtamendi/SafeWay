package otamendi.urtzi.com.safeway.Activities;

import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.signInAuth;

public class emergencyPhone  extends Activity {


    private static final int RESULT_PICK_CONTACT = 10;
    private static final String TAG = "emergencyPhone";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pickContact();

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
            cursor.close();
            sendPasswordConfig(phoneNumber);


        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private void sendPasswordConfig(String phone){

        Intent intent = new Intent ( emergencyPhone.this ,PasswordConfig.class );
        intent.putExtra("emergencyPhone", phone);
        startActivity(intent);
    }

}
