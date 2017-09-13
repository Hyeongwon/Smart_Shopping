package iot.byunhyeongwon.stockgo_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import vo.Product;
import vo.Product_Store;
import vo.Store;

public class ProductDetailActivity extends FragmentActivity {

    TextView tv;
    Product p;
    Button findBt;

    ArrayList<Store> sList;
    ArrayList<Product_Store> psList;

    double cur_longitude, cur_latitude;
    String cur_store_name;
    int cur_store_id;
    int cur_product_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tv = (TextView) findViewById(R.id.tv_product_detail);
        //findBt = (Button) findViewById(R.id.findStore);

        Store_alarmFragment store_alarmFragment;
        FragmentManager fm;

        fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.add(R.id.store_info_layout, new Store_infoFragment());
        fragmentTransaction.add(R.id.store_alarm_layout, store_alarmFragment = new Store_alarmFragment());

        Intent intent = getIntent();
        p = (Product) intent.getSerializableExtra("product");

        tv.setText("아이템 명 = " + p.getProduct_name() + "\n");
        //tv.append(Integer.toString(p.getId()) + "\n");
        cur_product_id = p.getId();
        tv.append("가격 = " + Integer.toString(p.getPrice()) + "\n");

        psList = (ArrayList<Product_Store>) intent.getSerializableExtra("psList");
        sList = (ArrayList<Store>) intent.getSerializableExtra("sList");

//        for (int i = 0; i < psList.size(); i++) {
//
//            tv.append(Integer.toString(psList.get(i).getProduct_id()) + "\n");
//            tv.append(Integer.toString(psList.get(i).getStore_id()) + "\n");
//            tv.append(Integer.toString(psList.get(i).getStock()) + "\n");
//            tv.append(Double.toString(sList.get(i).getLatitude())+ "\n");
//        }

        cur_latitude = intent.getDoubleExtra("cur_latitude", 0.0);
        cur_longitude = intent.getDoubleExtra("cur_longitude", 0.0) * (-1.0);

        cur_latitude = Double.parseDouble(String.format("%.6f", cur_latitude));
        cur_longitude = Double.parseDouble(String.format("%.6f", cur_longitude));

        cur_store_name = get_store_name();

        tv.append("store_name = " + cur_store_name + "\n");

        for(int i = 0; i < psList.size(); i++) {

            if(psList.get(i).getStore_id() == cur_store_id) {

                if(psList.get(i).getStock() == 0) {

                    tv.append("Out of Stock" + "\n");

                } else {

                    if(store_alarmFragment != null) fragmentTransaction.detach(store_alarmFragment);
                    tv.append("InStock" + "\n");
                }
            }
        }

        fragmentTransaction.commit();


//        Toast.makeText(this, "latitude = " + cur_latitude + "\nlongitude = " + cur_longitude, Toast.LENGTH_LONG).show();
    }

    private static double distance(double lat1, double lon1, double lat2, double lon2, String unit) {

        double theta = lon1 - lon2;
        double dist = Math.sin(deg2rad(lat1)) * Math.sin(deg2rad(lat2)) + Math.cos(deg2rad(lat1)) * Math.cos(deg2rad(lat2)) * Math.cos(deg2rad(theta));

        dist = Math.acos(dist);
        dist = rad2deg(dist);
        dist = dist * 60 * 1.1515;

        if (unit == "kilometer") {
            dist = dist * 1.609344;
        } else if(unit == "meter"){
            dist = dist * 1609.344;
        }

        return (dist);
    }


    // This function converts decimal degrees to radians
    private static double deg2rad(double deg) {
        return (deg * Math.PI / 180.0);
    }

    // This function converts radians to decimal degrees
    private static double rad2deg(double rad) {
        return (rad * 180 / Math.PI);
    }

    private String get_store_name() {

        String cur_store_name = null;

        double min_dis = distance(cur_latitude, cur_longitude ,sList.get(0).getLatitude(), sList.get(0).getLogitude(), "kilometer");

        for(int i = 1; i < sList.size(); i++) {

            if(min_dis > distance(cur_latitude, cur_longitude ,sList.get(i).getLatitude(), sList.get(i).getLogitude(), "kilometer")) {

                min_dis = distance(cur_latitude, cur_longitude ,sList.get(i).getLatitude(), sList.get(i).getLogitude(), "kilometer");
                cur_store_name = sList.get(i).getName();
                cur_store_id = sList.get(i).getId();
            }
        }

        return cur_store_name;
    }

    public void findStore(View view) {

        Toast.makeText(this, "!!!!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(this, MapsActivity.class);

        ArrayList<Store> temp_sList = sList;

        for(int i = 0; i < temp_sList.size(); i++) {

            if(cur_store_id == temp_sList.get(i).getId()) {

                temp_sList.remove(i);
            }
        }

        intent.putExtra("sList", temp_sList);
        intent.putExtra("psList", psList);
        startActivity(intent);

    }

    @Override
    protected void onStop() {

        super.onStop();
    }
}
