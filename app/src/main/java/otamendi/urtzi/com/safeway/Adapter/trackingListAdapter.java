package otamendi.urtzi.com.safeway.Adapter;

import android.app.Activity;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import otamendi.urtzi.com.safeway.Domain.trackingSesion;
import otamendi.urtzi.com.safeway.R;
import otamendi.urtzi.com.safeway.Utils.DatabaseService;
import otamendi.urtzi.com.safeway.Utils.SimpleCallback;
import otamendi.urtzi.com.safeway.Utils.onRecyclerViewClickListener;

public class trackingListAdapter  extends RecyclerView.Adapter<trackingListAdapter.ViewHolder> {
    private final static String TAG="trackingListAdapter";
    private List<trackingSesion> trackingList;
    private onRecyclerViewClickListener listener;
    private Activity activity;
    private Context context;

    public trackingListAdapter(List<trackingSesion> trackingList, onRecyclerViewClickListener listener, Activity activity) {
        this.trackingList = trackingList;
        this.listener = listener;
        this.activity = activity;
    }

    @Override
    public trackingListAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(activity).inflate(R.layout.view_tracking_list, parent, false);
        context = parent.getContext();
        trackingListAdapter.ViewHolder vh = new trackingListAdapter.ViewHolder(v, listener);
        return vh;
    }

    @Override
    public void onBindViewHolder(trackingListAdapter.ViewHolder holder, int position) {
        holder.bind(trackingList.get(position));

    }

    @Override
    public int getItemCount() {
        return trackingList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  implements View.OnClickListener{

        public TextView address,endDate,startDate;
        private onRecyclerViewClickListener listener;

        public ViewHolder(View itemView, onRecyclerViewClickListener listener) {
            super(itemView);
            address = (TextView) itemView.findViewById(R.id.address_text);
            endDate = (TextView) itemView.findViewById(R.id.end_date);
            startDate = (TextView) itemView.findViewById(R.id.start_date);

            this.listener= listener;
            itemView.setOnClickListener(this);

        }


        public void bind(final trackingSesion tracking ) {
            address.setText(tracking.getDestination().getAddress());
            DateFormat df = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            endDate.setText( df.format(tracking.getEnded()));
            startDate.setText( df.format(tracking.getStarted()));

        }



        @Override
        public void onClick(View view) {
            listener.onClick(view, getAdapterPosition());
        }
    }



}
