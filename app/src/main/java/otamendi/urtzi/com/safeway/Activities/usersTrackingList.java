package otamendi.urtzi.com.safeway.Activities;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import otamendi.urtzi.com.safeway.Adapter.linkedUsersAdapter;
import otamendi.urtzi.com.safeway.Adapter.trackingListAdapter;
import otamendi.urtzi.com.safeway.Domain.trackingSesion;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.ComplexCallback;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;

public class usersTrackingList extends AppCompatActivity {

    private String users_uid;
    private Toolbar toolbar;
    private RecyclerView trackingView;
    private List<String> keyList;
    private List<trackingSesion> trackingList;
    protected static final String TAG = "TRACKING SESSION LIST";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_users_tracking_list);
        Bundle extras = getIntent().getExtras();
        users_uid = extras.getString("users_uid");
        bindUI();
        setSupportActionBar(toolbar);
        configToolbar();
        getTrackingList();
    }

    private void bindUI() {
        toolbar = (Toolbar) findViewById(R.id.trackingList_toolbar);
        trackingView = (RecyclerView) findViewById(R.id.trackingList);
    }

    private void configToolbar() {
        Log.d(TAG, "+++++++ Configuring toolbar");

        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24px);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(usersTrackingList.this, linkedUsersList.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            }
        });

    }

    private void getTrackingList(){
        DatabaseService.getTrackingList(users_uid,getTrackingListCallback,displayErrorPage);
    }

    private ComplexCallback<List<trackingSesion>,List<String>> getTrackingListCallback = new ComplexCallback<List<trackingSesion>,List<String>>() {
        @Override
        public void callback(List<trackingSesion> sesions,List<String> keys) {
            trackingList = sesions;
            keyList= keys;
            configRecyclerView();
            Log.d(TAG, "Linked users ");
        }
    };

    private SimpleCallback<String> displayErrorPage = new SimpleCallback<String>() {
        @Override
        public void callback(String data) {
            Toast.makeText(usersTrackingList.this, R.string.error, Toast.LENGTH_LONG).show();
            Log.e(TAG, "Display Location----> error" + data.toString());
        }
    };

    ////////////////////////////////
    ////Config recycler view
    /////////////////////////////////
    private void configRecyclerView() {
        trackingListAdapter trackingListAdapter = new trackingListAdapter(trackingList,listenerRecyclerView, this);
        trackingView.setAdapter(trackingListAdapter);
        trackingView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        trackingView.addItemDecoration(
                new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));
    }

    ////////////////////////////////////////////////////////////
    /////////////  onRecyclerViewClickListener /////////
    ////////////////////////////////////////////////////////////

    private onRecyclerViewClickListener listenerRecyclerView = new onRecyclerViewClickListener() {
        @Override
        public void onClick(View view, int position) {
            Intent intent = new Intent(usersTrackingList.this, mapTrackingSesion.class);
            intent.putExtra("sesion_id",keyList.get(position));
            intent.putExtra("users_uid",users_uid);
            startActivity(intent);
        }
    };


}
