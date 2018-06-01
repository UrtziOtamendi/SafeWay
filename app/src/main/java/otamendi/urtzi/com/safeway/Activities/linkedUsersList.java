package otamendi.urtzi.com.safeway.Activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PagerSnapHelper;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.util.HashMap;
import java.util.List;

import otamendi.urtzi.com.safeway.Adapter.linkedUsersAdapter;
import otamendi.urtzi.com.safeway.Adapter.savedLocationAdapter;
import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.generateQR;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;
import otamendi.urtzi.com.safeway.Utils.sharedPreferences;

public class linkedUsersList extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView usersView;
    private FloatingActionButton linkNewUser;
    private List<String[]> linkedUsersList;
    protected static final String TAG = "LINKED USERS LIST";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_liked_users_list);
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        linkNewUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                newUsersNameRequest();
            }
        });

        getLinkedUser();
    }

    private void bindUI() {
        toolbar = (Toolbar) findViewById(R.id.linkedUsersList_toolbar);
        usersView = (RecyclerView) findViewById(R.id.linkedUsersList);
        linkNewUser = (FloatingActionButton) findViewById(R.id.linkNewUser_fab);

    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");
        getSupportActionBar().setDisplayShowTitleEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_linked_Users_list);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(linkedUsersList.this, safeWayHome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    private void newUsersNameRequest() {

        final EditText nameInput = new EditText(this);
        nameInput.setInputType(InputType.TYPE_CLASS_TEXT);

        // Explanation
        new AlertDialog.Builder(this)
                .setTitle(R.string.link_new_user_title)
                .setMessage(R.string.link_new_user_message)
                .setView(nameInput)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String name = nameInput.getText().toString();
                        if (name == null || name.equals("")) {
                            Log.d(TAG, "-----> Name empty");
                            Toast.makeText(linkedUsersList.this, R.string.users_name_empty, Toast.LENGTH_SHORT).show();
                        } else {
                            createQR(name);
                        }
                    }
                })
                .setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        finish();
                    }
                })
                .create()
                .show();
    }

    private void createQR(String name) {
        Bitmap qr = generateQR.generateFromString(name);
        ImageView imageQR = new ImageView(this);
        imageQR.setImageBitmap(qr);
        // Explanation
        new AlertDialog.Builder(this)
                .setTitle(R.string.QR)
                .setMessage(R.string.QR_message)
                .setView(imageQR)
                .setNeutralButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                    }
                })
                .create()
                .show();
    }


    /// Get linked users

    private void getLinkedUser() {
        DatabaseService.getLinkedUsers(getLinkedUsersCallback, displayErrorPage);
    }

    private SimpleCallback<List<String[]>> getLinkedUsersCallback = new SimpleCallback<List<String[]>>() {
        @Override
        public void callback(List<String[]> data) {
            linkedUsersList = data;
            configRecyclerView();
            Log.d(TAG, "Linked users ");
        }
    };

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(linkedUsersList.this, R.string.error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Display Location----> error" + data.toString());
        }
    };

    ////////////////////////////////
    ////Config recycler view
    /////////////////////////////////
    private void configRecyclerView() {
        linkedUsersAdapter linkedUsersAdapter = new linkedUsersAdapter(linkedUsersList, listenerRecyclerView, this);
        usersView.setAdapter(linkedUsersAdapter);
        usersView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        usersView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }


    ////////////////////////////////////////////////////////////
    /////////////  onRecyclerViewClickListener /////////
    ////////////////////////////////////////////////////////////

    private int selectedPosition;
    private onRecyclerViewClickListener listenerRecyclerView = new onRecyclerViewClickListener() {
        @Override
        public void onClick(View view, int position) {
            selectedPosition = position;
            DatabaseService.isTracking((linkedUsersList.get(position))[0], isTrackingCallback);

        }

    };

    private SimpleCallback<Boolean> isTrackingCallback = new SimpleCallback<Boolean>() {
        @Override
        public void callback(Boolean data) {
            if (data) {
                Intent intent = new Intent(linkedUsersList.this, trackingLiveWay.class);
                intent.putExtra("users_uid", (linkedUsersList.get(selectedPosition))[0]);
                startActivity(intent);
            } else {
                Intent intent = new Intent(linkedUsersList.this, usersTrackingList.class);
                intent.putExtra("users_uid", (linkedUsersList.get(selectedPosition))[0]);
                startActivity(intent);
            }
        }
    };

}

