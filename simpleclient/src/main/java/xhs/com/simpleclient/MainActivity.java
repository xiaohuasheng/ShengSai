package xhs.com.simpleclient;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tvShow = (TextView) findViewById(R.id.tvShow);

        new Thread(){
            @Override
            public void run() {
                Socket socket = null;
                try {
                    socket = new Socket("114.215.97.243", 1234);

                    //接收数据
                    InputStream in = socket.getInputStream();
                    byte[] buf = new byte[1024];
                    int len = in.read(buf);
                    String text = new String(buf, 0, len);
                    tvShow.setText(text);
                    socket.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }
}
