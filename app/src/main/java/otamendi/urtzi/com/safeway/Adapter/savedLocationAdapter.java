package otamendi.urtzi.com.safeway.Adapter;

import android.app.Activity;
import android.content.Context;
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

public class savedLocationAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final static String TAG = "savedLocationAdapter";
    private Context context;
    private List<myLocation> locationList;
    private onRecyclerViewClickListener listener,listenerNewLocation;

    private int layout;
    private LatLng position;
    private Activity activity;

    public savedLocationAdapter(List<myLocation> locations, LatLng pos, int layout, Activity activity, onRecyclerViewClickListener listener, onRecyclerViewClickListener listener0) {

        locationList = locations;
        this.layout = layout;
        this.activity = activity;
        position = pos;
        this.listener = listener;
        this.listenerNewLocation= listener0;
    }

    @Override
    public int getItemViewType(int position) {
        int position2 = position % locationList.size();
        if (locationList.size() == 1) {
            return 0;
        }
        if (locationList.size() == position2+1) {
            return 0;
        }
        return 1;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 0:
                View v0 = LayoutInflater.from(activity).inflate(R.layout.view_save_new_location, parent, false);
                context = parent.getContext();
                return new ViewHolder0(v0, listenerNewLocation);

            case 1:
                View v = LayoutInflater.from(activity).inflate(layout, parent, false);
                context = parent.getContext();
                return new ViewHolder1(v, listener);

        }

        return null;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()){
            case 0:
                break;

            case 1:
                ViewHolder1 holder1= (ViewHolder1) holder;
                int position2 = position % locationList.size();
                holder1.itemView.setLongClickable(true);
                holder1.bind(locationList.get(position2));
                break;
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }


    public void deleteItem(int index) {
        locationList.remove(index);
        notifyItemRemoved(index);
    }

    public void updateItem(int index, String name) {
        locationList.get(index).setName(name);
        notifyItemChanged(index);
    }

    public void addItem(myLocation location) {
        locationList.add(locationList.size()-1,location);
        notifyItemInserted(locationList.size() - 2);
    }


    public class ViewHolder1 extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView address, name, kmToGo;
        private onRecyclerViewClickListener listener;


        public ViewHolder1(View itemView, onRecyclerViewClickListener listener) {
            super(itemView);
            address = itemView.findViewById(R.id.savedAddressName);
            name = itemView.findViewById(R.id.savedLocationName);
            kmToGo = itemView.findViewById(R.id.kmToGoal);
            this.listener = listener;
            itemView.setOnClickListener(this);

        }


        public void bind(final myLocation location) {
            String distance = mapsService.distanceToGoal(new LatLng(location.getLat(), location.getLon()), position);
            if (distance.compareTo("") == 0) {
                kmToGo.setText("");
            } else {
                kmToGo.setText(distance + " Km");
            }
            address.setText(location.getAddress());
            name.setText(location.getName());

        }

        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }


    }

    public class ViewHolder0 extends RecyclerView.ViewHolder implements View.OnClickListener {

        private onRecyclerViewClickListener listener;


        public ViewHolder0(View itemView, onRecyclerViewClickListener listener) {
            super(itemView);
            this.listener = listener;
            itemView.setOnClickListener(this);

        }

        public void bind(final myLocation location) {
            
        }


        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }


    }

}
