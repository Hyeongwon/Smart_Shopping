package iot.byunhyeongwon.stockgo_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

public class DeliveryActivity extends Activity {

    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Intent i = getIntent();
        String store_addr = i.getStringExtra("store_addr");

        textView = (TextView) findViewById(R.id.store_addr);
        textView.append(store_addr);

    }

    public void deliveryOkBtn(View view) {


    }

    public void deliveryCancelBtn(View view) {


    }
}
