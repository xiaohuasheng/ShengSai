package xhs.com.multithreadclient;

import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

public class MainActivity extends AppCompatActivity {
    private EditText edtInput;
    private Button btnSend;
    private TextView tvShow;

    private Button btnLongitude;
    private Button btnLatitude;

    Handler handler;
    ClientThread clientThread;

    private byte[] gpsData = new byte[]{0x4D,0x00,0x50,0x35,0x68,0x23,0x03,0x00,0x41,(byte) 0x96,0x50,0x06,0x00,0x7c,0x31,0x31,0x33,0x2e,0x32,0x33,0x34,0x35,0x36,0x37,0x7c,0x32,0x33,0x2e,0x31,0x32,0x33,0x34,0x35,0x36,0x7c,0x38,0x30,0x7c,0x31,0x36,0x34,0x7c,0x31,0x32,0x30,0x7c,0x34,0x36,0x30,0x7c,0x30,0x30,0x7c,0x33,0x32,0x38,0x37,0x7c,0x35,0x32,0x31,0x33,0x7c,(byte) 0xac,0x7c,0x32,0x30,0x31,0x34,0x30,0x39,0x32,0x34,0x31,0x34,0x33,0x38,0x32,0x38,0x7c,0x37,0x35,(byte) 0xF6,(byte) 0xAD,0x0D};
    private String longitude;
    private String latitude;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtInput = (EditText) findViewById(R.id.edtInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvShow = (TextView) findViewById(R.id.tvShow);

        btnLongitude = (Button) findViewById(R.id.btnLongitude);
        btnLatitude = (Button) findViewById(R.id.btnLatitude);
        //获取纬度
        btnLongitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongitudeLatitude loc = new LongitudeLatitude(gpsData);
                longitude = loc.getLongitude();
                Toast.makeText(MainActivity.this, longitude, Toast.LENGTH_SHORT).show();
                //Toast.makeText(MainActivity.this, "click", Toast.LENGTH_SHORT).show();
            }
        });
        //获取经度
        btnLatitude.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LongitudeLatitude loc = new LongitudeLatitude(gpsData);
                latitude = loc.getLatitude();
                Toast.makeText(MainActivity.this, latitude, Toast.LENGTH_SHORT).show();
            }
        });





        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    //tvShow.append("\n" + msg.obj.toString());

                    //Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    tvShow.setText(msg.obj.toString());

                    JSONObject json = null;
                    try {
                        json = new JSONObject(msg.obj.toString());
                        String type = json.getString("type");
                        String data = json.getString("data");

                        //Toast.makeText(MainActivity.this, type, Toast.LENGTH_SHORT).show();
                        //Toast.makeText(MainActivity.this, data, Toast.LENGTH_SHORT).show();
                        switch (type)
                        {
                            case "gps":
                                //解析gps数据
                                gpsData = data.getBytes();
                                break;
                        }

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    //业务处理
                }
            }
        };

        clientThread = new ClientThread(handler);
        new Thread(clientThread).start();

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Message msg = new Message();
                msg.what = 0x345;
                msg.obj = edtInput.getText().toString();
                //Toast.makeText(MainActivity.this, edtInput.getText().toString(), Toast.LENGTH_SHORT).show();
                clientThread.revHandler.sendMessage(msg);
                edtInput.setText("");
            }
        });


    }
}
