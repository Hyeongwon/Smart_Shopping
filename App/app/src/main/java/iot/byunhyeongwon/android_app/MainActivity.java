package iot.byunhyeongwon.android_app;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.Inet4Address;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends Activity {

    EditText editText;
    TextView textView;

    int product_id;
    String product_name;
    int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = (EditText) findViewById(R.id.editText);
        textView = (TextView) findViewById(R.id.textView);
    }

    public void reqBtn(View view) {

        String url = editText.getText().toString();

        String query = null;

        try {

            query = URLEncoder.encode("빵", "utf-8");

        } catch (UnsupportedEncodingException e) {

            e.printStackTrace();
        }
        url = url + query;

        StringRequest request = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {

                        try {

                            println("OnResponse() call...!!! : " + response);

                            JSONArray jarray = new JSONArray(response);

                            for(int i=0; i < jarray.length(); i++){

                                JSONObject jObject = jarray.getJSONObject(i);  // JSONObject 추출
                                product_id = jObject.getInt("id");
                                product_name = jObject.getString("product_name");
                                price = jObject.getInt("price");

                                Log.e("product_id = ", Integer.toString(product_id));
                                Log.e("product_name = ", product_name);
                                Log.e("price = ", Integer.toString(price));
                                //latitude = jObject.getDouble("latitude");
                            }

//                            Intent i = new Intent();
//                            i.putExtra("longitude", longitude);
//                            i.putExtra("latitude", latitude);
//
//                            i.setClass(MainActivity.this, MapsActivity.class);
//                            startActivity(i);
                        }catch (Exception e) {

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
        println("request to web server" + url);
    }

    public void println(final String data) {

        textView.append(data + '\n');
    }
}
