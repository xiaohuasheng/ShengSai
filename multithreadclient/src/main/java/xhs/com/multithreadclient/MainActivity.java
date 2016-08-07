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

public class MainActivity extends AppCompatActivity {
    private EditText edtInput;
    private Button btnSend;
    private TextView tvShow;

    Handler handler;
    ClientThread clientThread;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtInput = (EditText) findViewById(R.id.edtInput);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvShow = (TextView) findViewById(R.id.tvShow);

        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x123) {
                    //tvShow.append("\n" + msg.obj.toString());

                    Toast.makeText(MainActivity.this, msg.obj.toString(), Toast.LENGTH_SHORT).show();
                    tvShow.setText(msg.obj.toString());
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
