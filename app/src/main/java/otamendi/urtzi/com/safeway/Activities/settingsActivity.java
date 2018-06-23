package otamendi.urtzi.com.safeway.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import es.dmoral.toasty.Toasty;
import otamendi.urtzi.com.safeway.Domain.receptorID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.permissionModule;

public class settingsActivity extends AppCompatActivity {

    private static final String TAG="SETTING";
    private ConstraintLayout changePass, changeNumber;

    private Toolbar toolbar;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        changePass.setOnClickListener(changePassListener);
        changeNumber.setOnClickListener(changeEmergencyNumber);
    }

    private void bindUI(){
        changePass = findViewById(R.id.changePass);
        changeNumber = findViewById(R.id.changeEmergencyNumber);

        toolbar= findViewById(R.id.settings_toolbar);
    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.settings_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(settingsActivity.this, safeWayHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }




    private View.OnClickListener changePassListener=  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            final EditText nameInput = new EditText(settingsActivity.this);
            nameInput.setInputType(InputType.TYPE_TEXT_VARIATION_PASSWORD);
            new AlertDialog.Builder(settingsActivity.this)
                    .setTitle(R.string.change_pass_title)
                    .setMessage(R.string.change_pass_message)
                    .setView(nameInput)
                    .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            try {
                                String name = nameInput.getText().toString();
                                if (name == null || name.equals("")) {
                                    Log.d(TAG, "-----> Name empty");
                                    Toasty.warning(settingsActivity.this, getResources().getString(R.string.password_empty), Toast.LENGTH_LONG,true).show();

                                } else {
                                    DatabaseService.savePassword(name);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "error" + e.toString());
                                Toasty.error(settingsActivity.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();
                            }
                        }
                    })
                    .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {

                        }
                    })
                    .create()
                    .show();
        }
    };

    private View.OnClickListener changeEmergencyNumber=  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            pickContact();

        }
    };

    private static final int RESULT_PICK_CONTACT = 10;


    private void pickContact(){
        Intent contactPickerIntent = new Intent(Intent.ACTION_PICK,
                ContactsContract.CommonDataKinds.Phone.CONTENT_URI);
        contactPickerIntent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        contactPickerIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        contactPickerIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivityForResult(contactPickerIntent, RESULT_PICK_CONTACT);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {

            Log.e(TAG," " +requestCode);
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Log.d(TAG, "----- Contact picked");
                    emergencyContact(data);
                    break;


            }
        } else {
            Log.e(TAG, "Failed ");
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
            DatabaseService.saveEmergencyNumber(phoneNumber);

        }catch (Exception e){
            e.printStackTrace();
        }
    }








    private void returnHome(){
        Intent intent = new Intent(settingsActivity.this, safeWayHome.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }


}
