package otamendi.urtzi.com.safeway.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
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

import es.dmoral.toasty.Toasty;
import otamendi.urtzi.com.safeway.Domain.receptorID;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.permissionModule;

public class receptorsUsers extends AppCompatActivity {

    private ImageView link1_add, link2_add, link1_link, link2_link;
    private TextView link1_name, link2_name;
    private Toolbar toolbar;
    private receptorID linkers;
    private IntentIntegrator qrScan;
    private String senderUID,senderName;
    private String name;
    private ConstraintLayout link1, link2;
    private static final int REQUEST_QR_BAR=49374;
    private static final String TAG="Receptors";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_receptors_users);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        qrScan= new IntentIntegrator(this);
        DatabaseService.getUsersLinks(getUsersLinksCallback, displayErrorPage);
        link1.setOnClickListener(link1OnClick);
        link2.setOnClickListener(link2OnClick);
    }

    private void bindUI(){
        link1= findViewById(R.id.linkedUser1);
        link2= findViewById(R.id.linkedUser2);
        link1_add = findViewById(R.id.linkedUser1_add_image);
        link2_add = findViewById(R.id.linkedUser2_add_image);
        link1_link = findViewById(R.id.linkedUser1_link_image);
        link2_link = findViewById(R.id.linkedUser2_link_image);
        link1_name = findViewById(R.id.linkedUser1_name);
        link2_name = findViewById(R.id.linkedUser2_name);
        toolbar= findViewById(R.id.receptors_toolbar);
    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.receptors_title);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(receptorsUsers.this, safeWayHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });
    }
    private SimpleCallback<receptorID> getUsersLinksCallback = new SimpleCallback<receptorID>() {
        @Override
        public void callback(receptorID data) {
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

            Toasty.error(receptorsUsers.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();
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
                        receptorID newLink= linkers;
                        String linkUID;
                        if(linkNumb==1){
                            newLink= new receptorID(linkers.getName2(), linkers.getLink2());
                            linkUID=linkers.getLink1();
                        }else{
                            newLink= new receptorID(linkers.getName1(), linkers.getLink1());
                            linkUID=linkers.getLink2();
                        }
                        DatabaseService.saveLinks(newLink);
                        DatabaseService.unlink(linkUID);
                        Intent intent = new Intent(receptorsUsers.this, settingsActivity.class);
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
        final EditText nameInput = new EditText(receptorsUsers.this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);
        new AlertDialog.Builder(receptorsUsers.this)
                .setTitle(R.string.link_new_user_title)
                .setMessage(R.string.link_new_user_message)
                .setView(nameInput)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        try {
                            name = nameInput.getText().toString();
                            if (name == null || name.equals("")) {
                                Log.d(TAG, "-----> Name empty");
                                Toasty.warning(receptorsUsers.this, getResources().getString(R.string.users_name_empty), Toast.LENGTH_LONG,true).show();

                            } else {
                                String[] permission = permissionModule.checkCamera();
                                if(permission.length==0){
                                    qrScan.initiateScan();
                                }else{
                                    ActivityCompat.requestPermissions(receptorsUsers.this, permission, permissionModule.PERMISSIONS_REQUEST_CAMERA);
                                }

                            }
                        } catch (Exception e) {
                            Log.e(TAG, "eerror" + e.toString());
                            Toasty.error(receptorsUsers.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // check whether the result is ok
        if (resultCode == RESULT_OK) {

            Log.e(TAG," " +requestCode);
            // Check for the request code, we might be usign multiple startActivityForReslut
            switch (requestCode) {

                case REQUEST_QR_BAR:
                    Log.e(TAG," " +REQUEST_QR_BAR);
                    IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
                    if (result != null) {
                        if (result.getContents() == null) {
                            Toasty.error(receptorsUsers.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();
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
                                Toasty.error(receptorsUsers.this, getResources().getString(R.string.error), Toast.LENGTH_LONG,true).show();
                            }
                        }
                    } else {
                        super.onActivityResult(requestCode, resultCode, data);
                    }
                    break;
            }
        } else {
            Log.e(TAG, "Failed ");
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
                    Toasty.error(receptorsUsers.this, getResources().getString(R.string.no_camera_permission), Toast.LENGTH_LONG,true).show();

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
                Toasty.error(receptorsUsers.this, getResources().getString(R.string.user_not_found), Toast.LENGTH_LONG,true).show();

            }
        }
    };

    public SimpleCallback<receptorID> geUserLinkers= new SimpleCallback<receptorID>() {
        @Override
        public void callback(receptorID data) {
            getLinkedUser(data);
        }
    };



    private void getLinkedUser(receptorID receptorID) {
        receptorID newLink;
        if(receptorID ==null){
            Log.d(TAG,"OldDated user don't have any linked ID" + (receptorID ==null));
            newLink= new receptorID(name,senderUID);
        }
        else{
            if(receptorID.getLink1()==null){
                Log.d(TAG,"OldDated user don't have any linked ID" + (receptorID.getLink1()==null));
                newLink= new receptorID(name,senderUID);
            }else{
                if(receptorID.getLink1().equals(senderUID)==true){
                    Toasty.error(receptorsUsers.this, getResources().getString(R.string.already_linked), Toast.LENGTH_LONG,true).show();

                    return;
                }else{
                    newLink= new receptorID(receptorID.getName1(), receptorID.getLink1(),name,senderUID);
                }
            }
        }

        DatabaseService.saveLinks(newLink);
        DatabaseService.linkReceptor(senderUID,senderName);
        returnHome();

    }
    private void returnHome(){
        Intent intent = new Intent(receptorsUsers.this, linkedUsersList.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        startActivity(intent);
        finish();

    }


}
