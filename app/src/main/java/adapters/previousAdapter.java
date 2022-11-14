package adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.uber_clone.R;

import java.util.List;

import model.PreviousRide;
import model.RideList;

public class previousAdapter extends ArrayAdapter<PreviousRide> {
    Context context;
    int resource;
    List<PreviousRide> myAddress;
    PreviousRide address;
    TextView City,rideFare;
    public previousAdapter(@NonNull Context context, int resource, @NonNull List<PreviousRide> myAddress) {
        super(context, resource, myAddress);
        this.context=context;
        this.resource=resource;
        this.myAddress=myAddress;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater inflator= LayoutInflater.from(context);
        View  view=inflator.inflate(resource,null);

        City=view.findViewById(R.id.cityName);
        rideFare=view.findViewById(R.id.rideFare);

        address=myAddress.get(position);
        City.setText(address.getCity());
        rideFare.setText("₹. "+address.getFare()+"/-");

        return view;
    }

}
