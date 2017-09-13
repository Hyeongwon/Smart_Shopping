package iot.byunhyeongwon.stockgo_app;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.util.ArrayList;

import vo.Store;


public class mapFragment extends Fragment implements OnMapReadyCallback {

    public mapFragment() {
        // Required empty public constructor
    }

    private GoogleMap mMap;
    ArrayList<Store> pList;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view =  inflater.inflate(R.layout.fragment_map, container, false);

        SupportMapFragment mapFragment = (SupportMapFragment) getChildFragmentManager()
                .findFragmentById(R.id.map_1);
        mapFragment.getMapAsync(this);

        return view;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLngs[] = new LatLng[pList.size()];
        MarkerOptions marker[] = new MarkerOptions[pList.size()];

        for(int i = 0; i < pList.size(); i++) {

            latLngs[i] = new LatLng(pList.get(i).getLatitude(), pList.get(i).getLogitude());
        }

        for(int i = 0; i < pList.size(); i++) {

            if(i % 2 == 0) {

                marker[i] = new MarkerOptions().position(latLngs[i]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_AZURE));

            } else {

                marker[i] = new MarkerOptions().position(latLngs[i]).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_ROSE));
            }
            mMap.addMarker(marker[i]);
        }

//        LatLng latLng = new LatLng(pList.get(0).getLatitude(), pList.get(0).getLogitude());


//        MarkerOptions marker = new MarkerOptions().position(latLng);
//
//        mMap.addMarker(marker);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLngs[0], 17));
    }

    @Override
    public void onAttach(Context context) {

        super.onAttach(context);

//        if(getActivity() != null && getActivity() instanceof MapsActivity) {
//
//            pList = ((MapsActivity)getActivity()).sList;
//
//            if(pList != null)
//                Log.e("1!!!!@#!@#!@#", " " + pList);
//
//            else
//                Log.e("!@#ASDASDASD", " asdasdasdasd");
//        }
    }

    @Override
    public void onStart() {

        super.onStart();

        if(getActivity() != null && getActivity() instanceof MapsActivity) {

            pList = ((MapsActivity)getActivity()).sList;
        }
    }
}
