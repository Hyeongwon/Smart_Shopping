package iot.byunhyeongwon.stockgo_app;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import vo.User;

public class DeliveryActivity extends Activity {

    TextView textView_name;
    TextView textView_addr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_delivery);

        Intent i = getIntent();
        String store_addr = i.getStringExtra("store_addr");
        User u = (User) i.getSerializableExtra("user");

        if(u.getName() == null) {

            Toast.makeText(this, "로그인 해주세요", Toast.LENGTH_SHORT).show();
            finish();

        } else {

            textView_name = (TextView) findViewById(R.id.user_name);
            textView_name.append(u.getName());

            textView_addr = (TextView) findViewById(R.id.user_addr);
            textView_addr.append(u.getAddr());
        }
    }

    public void deliveryOkBtn(View view) {


    }

    public void deliveryCancelBtn(View view) {

        finish();

    }
}
