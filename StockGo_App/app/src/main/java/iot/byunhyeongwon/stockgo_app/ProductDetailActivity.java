package iot.byunhyeongwon.stockgo_app;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.os.Handler;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewFlipper;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;
import com.google.android.gms.common.data.DataHolder;

import org.w3c.dom.Text;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

import vo.Product;
import vo.Product_Store;
import vo.Store;

public class ProductDetailActivity extends FragmentActivity {

    TextView tv_product_name;
    TextView tv_product_price;
    TextView tv_product_stock;
    Product p;
    Button findBt;
    ViewFlipper viewFlipper;

    ArrayList<Store> sList;
    ArrayList<Product_Store> psList;

    double cur_longitude, cur_latitude;
    String cur_store_name;
    int cur_store_id;
    int cur_product_id;

    Handler handler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_detail);

        tv_product_name = (TextView) findViewById(R.id.tv_product_name);
        tv_product_price = (TextView) findViewById(R.id.tv_product_price);
        tv_product_stock = (TextView) findViewById(R.id.tv_product_stock);
        viewFlipper = (ViewFlipper) findViewById(R.id.view_flipper);

        //findBt = (Button) findViewById(R.id.findStore);

        Store_alarmFragment store_alarmFragment;

        FragmentManager fm;

        fm = getFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();

        fragmentTransaction.add(R.id.store_info_layout, new Store_infoFragment());
        fragmentTransaction.add(R.id.store_alarm_layout, store_alarmFragment = new Store_alarmFragment());

        Intent intent = getIntent();
        p = (Product) intent.getSerializableExtra("product");

        Thread t = new Thread(new Runnable() {
            @Override
            public void run() {
                // TODO Auto-generated method stub
                try{

                    final ImageView iv = (ImageView)findViewById(R.id.imageView1);
                    URL url = new URL("http://13.124.219.166:3000/images?pId=1");
                    InputStream is = url.openStream();
                    final Bitmap bm = BitmapFactory.decodeStream(is);
                    handler.post(new Runnable() {

                        @Override
                        public void run() {  // 화면에 그려줄 작업
                            Log.e("!!!!!!!!", "!!!!!!!!!!!!");
                            iv.setImageBitmap(bm);

                        }
                    });
                    iv.setImageBitmap(bm); //비트맵 객체로 보여주기
                } catch(Exception e){

                }

            }
        });

        t.start();

        //getBitmapFromURL("http://13.124.219.166:3000/images?pId=1");

        viewFlipper.startFlipping();
        viewFlipper.setFlipInterval(2000);

        tv_product_name.setText(p.getProduct_name());
        cur_product_id = p.getId();
        tv_product_price.setText(Integer.toString(p.getPrice()));

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

        for(int i = 0; i < psList.size(); i++) {

            if(psList.get(i).getStore_id() == cur_store_id) {

                if(psList.get(i).getStock() == 0) {

                    tv_product_stock.setText("0");

                } else {

                    if(store_alarmFragment != null) fragmentTransaction.detach(store_alarmFragment);
                    tv_product_stock.setText(psList.get(i).getStock());
                }
            }
        }

        fragmentTransaction.commit();


//        Toast.makeText(this, "latitude = " + cur_latitude + "\nlongitude = " + cur_longitude, Toast.LENGTH_LONG).show();
    }

//    public Bitmap getBitmapFromURL(String src) {
//        try {
//            java.net.URL url = new java.net.URL(src);
//            HttpURLConnection connection = (HttpURLConnection) url
//                    .openConnection();
//            connection.setDoInput(true);
//            connection.connect();
//            InputStream input = connection.getInputStream();
//            Bitmap myBitmap = BitmapFactory.decodeStream(input);
//            Log.e("????????", "???????????/");
//            return myBitmap;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null;
//        }
//    }

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

    public void deliverySetBtn(View view) {

        Toast.makeText(this, "딜리버리세팅", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();

        intent.setClass(this, DeliveryActivity.class);
        String store_addr = null;
        for(int i = 0; i < sList.size(); i++) {

            if(sList.get(i).getId() == cur_store_id) {

                store_addr = sList.get(i).getAddr();
            }
        }

        intent.putExtra("store_addr", store_addr);
        startActivity(intent);
    }
}
