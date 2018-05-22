package otamendi.urtzi.com.safeway.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v4.view.PagerAdapter;

import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.model.LatLng;

import java.util.List;

import otamendi.urtzi.com.safeway.Domain.myLocation;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.mapsService;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;

public class savedLocationAdapter extends RecyclerView.Adapter<savedLocationAdapter.ViewHolder>  {

    private final static String TAG="savedLocationAdapter";
    private Context context;
    private List<myLocation> locationList;
    private onRecyclerViewClickListener listener;

    private int layout;
    private LatLng position;
    private Activity activity;

    public savedLocationAdapter(List<myLocation> locations, LatLng pos, int layout, Activity activity, onRecyclerViewClickListener listener ){

        locationList=locations;
        this.layout=layout;
        this.activity=activity;
        position=pos;
        this.listener=listener;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(layout, parent, false);
        context = parent.getContext();
        ViewHolder vh = new ViewHolder(v, listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.bind(locationList.get(position));

    }

    @Override
    public int getItemCount() {
        return locationList.size();
    }




    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        public TextView address,name,kmToGo, startStop;
        private onRecyclerViewClickListener listener;

        public ViewHolder(View itemView, onRecyclerViewClickListener listener) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.savedAddressName);
            name = (TextView) itemView.findViewById(R.id.savedLocationName);
            kmToGo = (TextView) itemView.findViewById(R.id.kmToGoal);
            startStop = (TextView) itemView.findViewById(R.id.homeStartStop);
            this.listener= listener;
            itemView.setOnClickListener(this);

        }


        public void bind(final myLocation location ) {
            String distance = mapsService.distanceToGoal(new LatLng(location.getLat(),location.getLon()),position);
            if(distance.compareTo("")==0) {
                kmToGo.setText("");
            }else{
                kmToGo.setText(distance + " Km");
            }
            address.setText(location.getAddress());
            name.setText(location.getName());
            startStop.setText(R.string.start_button);
        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }

}
