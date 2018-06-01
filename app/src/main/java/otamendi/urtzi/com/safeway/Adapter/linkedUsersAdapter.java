package otamendi.urtzi.com.safeway.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.mapsService;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;

public class linkedUsersAdapter  extends RecyclerView.Adapter<linkedUsersAdapter.ViewHolder> {
    private final static String TAG="linkedUsersAdapter";
    private List<String[]> userList;
    private onRecyclerViewClickListener listener;
    private Activity activity;
    private Context context;


    public linkedUsersAdapter(List<String[]> userList, onRecyclerViewClickListener listener, Activity activity ){
        this.activity=activity;
        this.userList=userList;
        this.listener=listener;
    }

    @Override
    public linkedUsersAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.view_linked_user, parent, false);
        context = parent.getContext();
        linkedUsersAdapter.ViewHolder vh = new linkedUsersAdapter.ViewHolder(v, listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(linkedUsersAdapter.ViewHolder holder, int position) {
        holder.bind(userList.get(position));

    }

    @Override
    public int getItemCount() {
        return userList.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        public TextView name;
        public ImageView image;
        private onRecyclerViewClickListener listener;
        public Boolean tracking=false;

        public ViewHolder(View itemView, onRecyclerViewClickListener listener) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.users_name);
            image = (ImageView) itemView.findViewById(R.id.gps_icon);
            this.listener= listener;
            itemView.setOnClickListener(this);

        }


        public void bind(final String[] user ) {
            name.setText(user[1]);
            DatabaseService.isTracking(user[0],isTrackingCallback );

        }

        private SimpleCallback<Boolean> isTrackingCallback= new SimpleCallback<Boolean>() {
            @Override
            public void callback(Boolean data) {
                if(data){
                    tracking=true;
                    image.setImageResource(R.drawable.ic_gps_fixed_24px);
                }
            }
        };



        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }


}
