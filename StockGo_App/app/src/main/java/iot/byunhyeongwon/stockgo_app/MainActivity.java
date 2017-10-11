package iot.byunhyeongwon.stockgo_app;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.GlideDrawableImageViewTarget;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import vo.Product;
import vo.Product_Store;
import vo.Store;
import vo.User;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    EditText editText;
    TextView textView;

    Product p = new Product();
    User user = new User();
    ArrayList<Product_Store> psList = new ArrayList<Product_Store>();
    ArrayList<Store> sList = new ArrayList<Store>();

    private LocationManager locationManager;
    private LocationListener locationListener;

    double latitude;
    double longitude;

    //TODO For NFC TAG
    NfcAdapter nfcAdapter;
    IntentFilter[] fillterArr;
    PendingIntent pIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        //textView = (TextView) findViewById(R.id.textView);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);


        //TODO For Animation
        ImageView imageView = (ImageView) findViewById(R.id.nfcImg);
        GlideDrawableImageViewTarget glideDrawableImageViewTarget =
                new GlideDrawableImageViewTarget(imageView);
        Glide.with(this).load(R.raw.test).into(glideDrawableImageViewTarget);

        //TODO For side navi-bar
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        settingGPS();

        //TODO User current location
        Location userLocation = getMyLocation();

        if( userLocation != null ) {
            //TODO Config user location
            latitude = userLocation.getLatitude();
            longitude = userLocation.getLongitude();

            Log.e("latitude", " " + latitude);
            Log.e("longitude", " " + longitude);
        }

        Log.e("latitude", " " + latitude);
        Log.e("longitude", " " + longitude);

        nfcAdapter = NfcAdapter.getDefaultAdapter(this);


        if(nfcAdapter == null) {

            Toast.makeText(this, "This device does not support NFC ...!!!", Toast.LENGTH_SHORT).show();
            finish();
        }

        Intent i = new Intent(this, this.getClass());
        i.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        pIntent = PendingIntent.getActivity(this, 0, i, PendingIntent.FLAG_CANCEL_CURRENT);

        IntentFilter ndefD = new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED);

        try {
            ndefD.addDataType("text/plain");
            fillterArr = new IntentFilter[]{};

        } catch (IntentFilter.MalformedMimeTypeException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void onResume() {

        super.onResume();
        nfcAdapter.enableForegroundDispatch(this, pIntent, fillterArr, null);
    }

    @Override
    protected void onNewIntent(Intent intent) {

        super.onNewIntent(intent);
        processIntent(intent);
    }

    @Override
    protected void onPause() {

        super.onPause();
        nfcAdapter.disableForegroundDispatch(this);
    }

    private void processIntent(Intent i) {

        if (i != null && NfcAdapter.ACTION_NDEF_DISCOVERED.equals(i.getAction()) ||
                NfcAdapter.ACTION_TAG_DISCOVERED.equals(i.getAction()) ||
                NfcAdapter.ACTION_TECH_DISCOVERED.equals(i.getAction())) {

            Parcelable[] rawMessages =
                    i.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
            NdefMessage ndefMsg = (NdefMessage) rawMessages[0];
            NdefRecord[] recArr = ndefMsg.getRecords();
            NdefRecord textRecord = recArr[0];

            byte[] ndef_type = textRecord.getType();
            String text_type = new String(ndef_type);

            if (text_type.equals("T")) {

                byte[] byteArr = textRecord.getPayload();
                String textString = new String(byteArr, 3, byteArr.length - 3);

                GetJson(textString);

            } else {

                Toast.makeText(this, "Unknown Tag...!!!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    //TODO Get data from server
    private void GetJson(String product_name){

        String url = editText.getText().toString();

        String query = null;

        try {

            query = URLEncoder.encode(product_name, "utf-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        url = url + query;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            JSONArray jarray = new JSONArray(response);
                            psList.clear();
                            sList.clear();

                            for (int i = 0; i < jarray.length(); i++) {

                                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                                p.setId(jObject.getInt("id"));
                                p.setProduct_name(jObject.getString("product_name"));
                                p.setPrice(jObject.getInt("price"));

                                Product_Store ps = new Product_Store();
                                ps.setProduct_id(jObject.getInt("product_id"));
                                ps.setStore_id(jObject.getInt("store_id"));
                                ps.setStock(jObject.getInt("stock"));
                                psList.add(ps);

                                Store s = new Store();
                                s.setId(jObject.getInt("store_id"));
                                s.setName(jObject.getString("name"));
                                s.setAddr(jObject.getString("addr"));
                                s.setLatitude(jObject.getDouble("latitude"));
                                s.setLogitude(jObject.getDouble("longitude"));

                                sList.add(s);

                                //latitude = jObject.getDouble("latitude");
                            }

                            Log.e("latitude", " " + latitude);
                            Log.e("longitude", " " + longitude);

                            Intent i = new Intent();

                            i.setClass(MainActivity.this, ProductDetailActivity.class);
                            i.putExtra("user", user);
                            i.putExtra("product", p);
                            i.putExtra("psList", psList);
                            i.putExtra("sList", sList);
                            i.putExtra("cur_longitude", longitude);
                            i.putExtra("cur_latitude", latitude);

                            startActivity(i);

                            //i.putExtra()
//                            i.putExtra("longitude", longitude);
//                            i.putExtra("latitude", latitude);
//
//                            i.setClass(MainActivity.this, MapsActivity.class);
//                            startActivity(i);
                        } catch (Exception e) {

                            e.printStackTrace();
                        }

                    }
                },

                new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        error.printStackTrace();
                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {

                Map<String, String> params = new HashMap<>();

                return params;
            }
        };

        request.setShouldCache(false);
        Volley.newRequestQueue(this).add(request);
    }

    private Location getMyLocation() {
        Location currentLocation = null;
        // Register the listener with the Location Manager to receive location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // 사용자 권한 요청
            //ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, MInteger.REQUEST_CODE_LOCATION);
        }
        else {

            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListener);

            // 수동으로 위치 구하기
            String locationProvider = LocationManager.GPS_PROVIDER;
            currentLocation = locationManager.getLastKnownLocation(locationProvider);
            if (currentLocation != null) {
                double lng = currentLocation.getLongitude();
                double lat = currentLocation.getLatitude();
                Log.d("Main", "longtitude=" + lng + ", latitude=" + lat);
            }
        }
        return currentLocation;
    }

    private void settingGPS() {
        // Acquire a reference to the system Location Manager
        locationManager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);

        locationListener = new LocationListener() {
            public void onLocationChanged(Location location) {
                double latitude = location.getLatitude();
                double longitude = location.getLongitude();
                // TODO 위도, 경도로 하고 싶은 것
            }

            public void onStatusChanged(String provider, int status, Bundle extras) {
            }

            public void onProviderEnabled(String provider) {
            }

            public void onProviderDisabled(String provider) {
            }
        };
    }

    public void search(final View view) {

        AlertDialog.Builder alertdialog = new AlertDialog.Builder(MainActivity.this);
        final EditText et = new EditText(MainActivity.this);
        //다이얼로그의 내용을 설정합니다.
        alertdialog.setTitle("아이템 검색");
        alertdialog.setMessage("직접 입력하세요.");

        alertdialog.setView(et);

        //확인 버튼
        alertdialog.setPositiveButton("확인", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                String product_name = et.getText().toString();
                GetJson(product_name);
            }
        });

        //취소 버튼
        alertdialog.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //취소 버튼이 눌렸을 때 토스트를 띄워줍니다.
                Toast.makeText(MainActivity.this, "취소", Toast.LENGTH_SHORT).show();
            }
        });

        AlertDialog alert = alertdialog.create();
        alert.show();

        //reqBtn(view);
    }

    public void history(View view) {

    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_camera) {
            // Handle the camera action
        } else if (id == R.id.nav_gallery) {

        } else if (id == R.id.nav_slideshow) {

        } else if (id == R.id.nav_manage) {

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_send) {

        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public void makeLogin(View view) {

        TextView textView = (TextView) findViewById(R.id.makeLogin);
        Toast.makeText(this, "!!!!!!", Toast.LENGTH_SHORT).show();

        Intent intent = new Intent();
        intent.setClass(this, LoginActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(resultCode == RESULT_OK) {

            user = (User) data.getSerializableExtra("user");

            TextView name_textView = (TextView) findViewById(R.id.makeLogin);
            TextView email_textView = (TextView) findViewById(R.id.textView_email);

            name_textView.setEnabled(false);
            name_textView.setText(user.getName());
            email_textView.setText(user.getEmail());
        }
    }
}