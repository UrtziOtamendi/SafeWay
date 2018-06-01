package otamendi.urtzi.com.safeway.Activities;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
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

import otamendi.urtzi.com.safeway.Domain.User;
import otamendi.urtzi.com.safeway.Domain.linkedID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.MainApplication;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.permissionModule;

public class settingsActivity extends AppCompatActivity {

    private static final String TAG="SETTING";
    private ConstraintLayout changePass, changeNumber,link1, link2;
    private ImageView link1_add, link2_add, link1_link, link2_link;
    private TextView link1_name, link2_name;
    private Toolbar toolbar;
    private String name;
    private IntentIntegrator qrScan;
    private static final int REQUEST_QR_BAR=49374;
    private String senderUID,senderName;

    private linkedID linkers;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        qrScan= new IntentIntegrator(this);
        DatabaseService.getUsersLinks(getUsersLinksCallback, displayErrorPage);
        link1.setOnClickListener(link1OnClick);
        link2.setOnClickListener(link2OnClick);
        changePass.setOnClickListener(changePassListener);
        changeNumber.setOnClickListener(changeEmergencyNumber);
    }

    private void bindUI(){
        changePass = findViewById(R.id.changePass);
        changeNumber = findViewById(R.id.changeEmergencyNumber);
        link1 = findViewById(R.id.linkedUser1);
        link1.setVisibility(View.INVISIBLE);
        link2 = findViewById(R.id.linkedUser2);
        link2.setVisibility(View.INVISIBLE);
        link1_add = findViewById(R.id.linkedUser1_add_image);
        link2_add = findViewById(R.id.linkedUser2_add_image);
        link1_link = findViewById(R.id.linkedUser1_link_image);
        link2_link = findViewById(R.id.linkedUser2_link_image);
        link1_name = findViewById(R.id.linkedUser1_name);
        link2_name = findViewById(R.id.linkedUser2_name);
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



    private SimpleCallback<linkedID> getUsersLinksCallback = new SimpleCallback<linkedID>() {
        @Override
        public void callback(linkedID data) {
            if(data!=null){
                Log.e(TAG, data.toString());
                linkers= data;
            }
            displayLinkers();
        }
    };


    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Log.e(TAG, data);
            Toast.makeText(settingsActivity.this, R.string.error, Toast.LENGTH_LONG).show();
            finish();
        }
    };


    private void displayLinkers(){
        link1.setVisibility(View.VISIBLE);
        link2.setVisibility(View.VISIBLE);
        if(linkers==null) return;
        if(linkers.getName1()==null) return;
        if(linkers.getName1().equals("")==false) {
            link1_add.setImageResource(R.drawable.ic_baseline_black_delete_24px);
            link1_link.setImageResource(R.drawable.ic_baseline_link_24px);
            link1_name.setText(linkers.getName1());
        }
        if(linkers.getName2()==null) return;
        if(linkers.getName2().equals("")==false) {
            link2_add.setImageResource(R.drawable.ic_baseline_black_delete_24px);
            link2_link.setImageResource(R.drawable.ic_baseline_link_24px);
            link2_name.setText(linkers.getName2());
        }
    }


    private View.OnClickListener link1OnClick=  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(linkers==null){
                linkUser();
                return;
            }
            if(linkers.getName1()==null){
                linkUser();
                return;
            }
            if(linkers.getName1().equals("")==false) {
                deleteLink(1);
                return;
            }
        }
    };

    private View.OnClickListener link2OnClick=  new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            if(linkers==null){
                linkUser();
                return;
            }
            if(linkers.getName2()==null){
                linkUser();
                return;
            }
            if(linkers.getName2().equals("")==false) {
                deleteLink(2);
                return;
            }
        }
    };


    private  void deleteLink(final int linkNumb){
        new AlertDialog.Builder(this)
                .setTitle(R.string.delete_link_title)
                .setMessage(R.string.delete_link_message)
                .setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        linkedID newLink= linkers;
                        String linkUID;
                        if(linkNumb==1){
                             newLink= new linkedID(linkers.getName2(), linkers.getLink2());
                             linkUID=linkers.getLink1();
                        }else{
                             newLink= new linkedID(linkers.getName1(), linkers.getLink1());
                            linkUID=linkers.getLink2();
                        }
                        DatabaseService.saveLinks(newLink);
                        DatabaseService.unlink(linkUID);
                        Intent intent = new Intent(settingsActivity.this, settingsActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();
                    }
                })
                .setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }
    private void linkUser(){
        final EditText nameInput = new EditText(settingsActivity.this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(settingsActivity.this)
                .setTitle(R.string.set_new_location_title)
                .setMessage(R.string.set_new_location_message)
                .setView(nameInput)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                             name = nameInput.getText().toString();
                            if (name == null || name.equals("")) {
                                Log.d(TAG, "-----> Name empty");
                                Toast.makeText(settingsActivity.this, R.string.users_name_empty, Toast.LENGTH_SHORT).show();
                            } else {
                                String[] permission =permissionModule.checkCamera();
                                if(permission.length==0){
                                    qrScan.initiateScan();
                                }else{
                                    ActivityCompat.requestPermissions(settingsActivity.this, permission, permissionModule.PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "eerror" + e.toString());
                            Toast.makeText(settingsActivity.this, R.string.error, Toast.LENGTH_LONG).show();

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
                                    Toast.makeText(settingsActivity.this, R.string.password_empty, Toast.LENGTH_SHORT).show();
                                } else {
                                    DatabaseService.savePassword(name);
                                }
                            } catch (Exception e) {
                                Log.e(TAG, "error" + e.toString());
                                Toast.makeText(settingsActivity.this, R.string.error, Toast.LENGTH_LONG).show();
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
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {
                case RESULT_PICK_CONTACT:
                    Log.d(TAG, "----- Contact picked");
                    emergencyContact(data);
                    break;

                case REQUEST_QR_BAR:
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() == null) {
                            Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                        } else {
                            try {

                                Log.e("QRSCAAN",result.getContents());
                                String rawResult= result.getContents();
                                String[] split= rawResult.split("\\s+");
                                senderName= split[0];
                                Log.d(TAG, "sender name--->"+ senderName);
                                senderUID= split[1];
                                DatabaseService.userExists(senderUID, getUserCallback, displayErrorPage);


                            } catch (Exception e) {
                                Log.e(TAG, e.toString());
                                Toast.makeText(this, R.string.error, Toast.LENGTH_LONG).show();
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
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
            DatabaseService.saveEmergencyNumber(phoneNumber);

        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case permissionModule.PERMISSIONS_REQUEST_CAMERA: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted
                    qrScan.initiateScan();
                } else {
                    Toast.makeText(this,R.string.no_camera_permission, Toast.LENGTH_LONG).show();

                }
            }
        }
    }

    public SimpleCallback<Boolean> getUserCallback= new  SimpleCallback<Boolean>(){
        @Override
        public void callback(Boolean data) {
            if(data){
                DatabaseService.getUsersLinks(geUserLinkers, displayErrorPage);
            }else{
                Toast.makeText(settingsActivity.this, R.string.user_not_found, Toast.LENGTH_LONG).show();
            }
        }
    };

    public SimpleCallback<linkedID> geUserLinkers= new SimpleCallback<linkedID>() {
        @Override
        public void callback(linkedID data) {
            getLinkedUser(data);
        }
    };

    private void getLinkedUser(linkedID linkedID) {
        linkedID newLink;
        if(linkedID==null){
            Log.d(TAG,"OldDated user don't have any linked ID" + (linkedID ==null));
            newLink= new linkedID(name,senderUID);
        }
        else{
            if(linkedID.getLink1()==null){
                Log.d(TAG,"OldDated user don't have any linked ID" + (linkedID.getLink1()==null));
                newLink= new linkedID(name,senderUID);
            }else{
                if(linkedID.getLink1().equals(senderUID)==true){
                    Toast.makeText(settingsActivity.this, R.string.already_linked, Toast.LENGTH_LONG).show();
                    return;
                }else{
                    newLink= new linkedID(linkedID.getName1(),linkedID.getLink1(),name,senderUID);
                }
            }
        }

        DatabaseService.saveLinks(newLink);
        DatabaseService.linkReceptor(senderUID,senderName);
        returnHome();

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
