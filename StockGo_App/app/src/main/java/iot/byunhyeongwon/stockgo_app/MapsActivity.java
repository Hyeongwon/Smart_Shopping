package iot.byunhyeongwon.stockgo_app;

import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;
import java.util.Objects;

import vo.Product_Store;
import vo.Store;

public class MapsActivity extends FragmentActivity {

    ListView listView;
    ArrayList<Store> sList;
    ArrayList<Product_Store> psList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        listView = (ListView) findViewById(R.id.map_detail_list);

        Intent i = getIntent();
        sList = (ArrayList<Store>) i.getSerializableExtra("sList");
        psList = (ArrayList<Product_Store>) i.getSerializableExtra("psList");

        CustomAdapter adapter = new CustomAdapter(this, 0, sList);
        listView.setAdapter(adapter);

    }

    public ArrayList<Store> getStore() {

        for(int i = 0; i < sList.size(); i++) {
        }
        return sList;
    }

    private class CustomAdapter extends ArrayAdapter<Store> {
        private ArrayList<Store> items;

        public CustomAdapter(Context context, int textViewResourceId, ArrayList<Store> objects) {
            super(context, textViewResourceId, objects);
            this.items = objects;
        }

        public View getView(int position, View convertView, ViewGroup parent) {
            View v = convertView;
            if (v == null) {
                LayoutInflater vi = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
                v = vi.inflate(R.layout.map_detail_list_item, null);
            }

            TextView textView = (TextView)v.findViewById(R.id.map_detail_tv);
            textView.setText(items.get(position).getName() + "\t");
            textView.append(" " + psList.get(position).getStock() + "items Left\n");
            textView.append(" " + items.get(position).getAddr() + "\n");

            //final String text = items.get(position);
            Button saveBtn = (Button)v.findViewById(R.id.map_detail_saveBtn);
            saveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MapsActivity.this, "!!!!", Toast.LENGTH_SHORT).show();
                }
            });

            Button DeliveryBtn = (Button)v.findViewById(R.id.map_detail_deliveryBtn);
            DeliveryBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Toast.makeText(MapsActivity.this, "!!!!", Toast.LENGTH_SHORT).show();
                }
            });

            return v;
        }
    }
}
