package com.xhs.shengsai;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText edtContent;
    private Button btnSend;
    private TextView tvShow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvShow = (TextView) findViewById(R.id.tvShow);

        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String content = edtContent.getText().toString();
                new SocketTask(tvShow,content).execute();
            }
        });
    }

    private class SocketTask extends AsyncTask<String,String,String>
    {
        private final String ip = "114.215.97.243";
        private final int port = 1234;
        private TextView tvShow;
        private String content;
        public SocketTask(TextView tvShow, String content) {
            this.tvShow = tvShow;
            this.content = content;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            tvShow.setText(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            Socket socket;
            try {
                socket = new Socket(ip,port);
                socket.setSoTimeout(10000);
                OutputStream out = socket.getOutputStream();
                String sendData = content + "END"; //以END为结束符
                out.write(sendData.getBytes());
                //Log.i("MyTag","*******have connected*******");

                InputStream in = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = in.read(buf);

                result = new String(buf, 0, len);
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
