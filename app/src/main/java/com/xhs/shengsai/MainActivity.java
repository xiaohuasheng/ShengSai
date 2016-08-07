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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.StreamCorruptedException;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {
    private EditText edtContent;
    private Button btnSend;
    private TextView tvShow;

    private final String ip = "114.215.97.243";
    private final int port = 1234;
    private Socket socket;
    private OutputStream  out;
    private InputStream in;

    private final int device_id = 1234; //设备id
    private final int isDevice = 1; //区分是设备还是用户

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtContent = (EditText) findViewById(R.id.edtContent);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvShow = (TextView) findViewById(R.id.tvShow);

        new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket(ip, port);

                    //登录
                    out = socket.getOutputStream();
                    JSONObject loginData = new JSONObject();
                    loginData.put("type", "login");
                    loginData.put("device_id",device_id);
                    loginData.put("isDevice", isDevice);
                    loginData.put("content", "xxx");
                    //String loginData = "{'type':'login','device_id':'"+device_id+"','isDevice':'"+isDevice+"','content':'xxx'}"+"END";
                    String sendData = loginData + "END";//协议决定，END作为消息结尾
                    out.write(sendData.getBytes());
                    out.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (JSONException e) {
                    e.printStackTrace();
                }

            }
        }.start();


        btnSend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //String content = edtContent.getText().toString();
                byte[] gps = new byte[]{0x4D,0x00,0x50,0x35,0x68,0x23,0x03,0x00,0x41,(byte) 0x96,0x50,0x06,0x00,0x7c,0x31,0x31,0x33,0x2e,0x32,0x33,0x34,0x35,0x36,0x37,0x7c,0x32,0x33,0x2e,0x31,0x32,0x33,0x34,0x35,0x36,0x7c,0x38,0x30,0x7c,0x31,0x36,0x34,0x7c,0x31,0x32,0x30,0x7c,0x34,0x36,0x30,0x7c,0x30,0x30,0x7c,0x33,0x32,0x38,0x37,0x7c,0x35,0x32,0x31,0x33,0x7c,(byte) 0xac,0x7c,0x32,0x30,0x31,0x34,0x30,0x39,0x32,0x34,0x31,0x34,0x33,0x38,0x32,0x38,0x7c,0x37,0x35,(byte) 0xF6,(byte) 0xAD,0x0D};
                JSONObject jsonObject = new JSONObject();
                try {
                    jsonObject.put("type", "send");
                    jsonObject.put("gps", gps);
                    jsonObject.put("isDevice", isDevice);
                } catch (JSONException e) {
                    e.printStackTrace();
                }

                String data = jsonObject + "END";
                new SocketTask(tvShow,data,socket,out,in).execute();
            }
        });
    }

    public void receiveData()
    {
        while (true) {
            try {
                in = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = in.read(buf);
                String result = new String(buf, 0, len);
                tvShow.setText(result);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    private class SocketTask extends AsyncTask<String,String,String>
    {
        private final String ip = "114.215.97.243";
        private final int port = 1234;
        private TextView tvShow;
        private String content;
        private Socket socket;
        private OutputStream out;
        private InputStream in;
        public SocketTask(TextView tvShow, String content, Socket socket, OutputStream out, InputStream in) {
            this.tvShow = tvShow;
            this.content = content;
            this.socket = socket;
            this.out = out;
            this.in = in;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(String result) {
            //tvShow.setText(result);
        }

        @Override
        protected String doInBackground(String... strings) {
            String result = null;
            //Socket socket;
            try {
/*                socket = new Socket(ip,port);
                socket.setSoTimeout(10000);*/
                out = socket.getOutputStream();
                //String message = "{'type':'login','device_id':"+device_id+",'isDevice':"+isDevice+"}";
                out.write(content.getBytes());
                //Log.i("MyTag","*******have connected*******");

                in = socket.getInputStream();
                byte[] buf = new byte[1024];
                int len = in.read(buf);

                result = new String(buf, 0, len);
                //socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return result;
        }
    }
}
