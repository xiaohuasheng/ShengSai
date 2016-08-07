package xhs.com.clienttwo;

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

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

public class MainActivity extends AppCompatActivity implements Runnable{
    @Override
    public void run() {
        while (true)
        {
            if(socket != null) {
                Log.i("MyTag","********************socket is not null******************");
                if (socket.isConnected()) {
                    if (!socket.isInputShutdown()) {
                        try {
                                    /*
                                    byte[] buf = new byte[1024];
                                    int len = in.read();
                                    if (len != 0) {
                                        showData = new String(buf, 0, len);
                                        Message msg = new Message();
                                        msg.what = 1;
                                        msg.obj = showData;
                                        handler.sendMessage(msg);
                                        Log.i("MyTag",showData.toString());
                                    }
                                    */
                            showData = in.readLine();
                            if(showData != null)
                            {
                                Message msg = new Message();
                                msg.what = 1;
                                msg.obj = showData;
                                handler.sendMessage(msg);
                                Log.i("MyTag",showData.toString());
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "input is shutdown", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "socket doesn't connect", Toast.LENGTH_SHORT).show();
                }
            }
            else
            {
                Log.i("MyTag","socket is null");
            }
        }
    }

    private EditText edtSend;
    private Button btnSend;
    private TextView tvShow;

    private Socket socket = null;
    private final String IP = "114.215.97.243";
    private final int PORT = 1234;

    private OutputStream out;
    //private InputStream in;
    private BufferedReader in = null;
    private String sendData;
    private String showData;

    private final String ACCOUNT = "775079852@qq.com";
    private final String PASSWORD = "123";
    private final int isDevice = 0; //不是设备

    public Handler handler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 1)
            {
                tvShow.setText(msg.obj.toString());
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        edtSend = (EditText) findViewById(R.id.edtSend);
        btnSend = (Button) findViewById(R.id.btnSend);
        tvShow = (TextView) findViewById(R.id.tvShow);

        new Thread(){
            @Override
            public void run() {
                try {
                    socket = new Socket(IP, PORT);
                    out = socket.getOutputStream();
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "login");
                    jsonObject.put("account", ACCOUNT);
                    jsonObject.put("password", PASSWORD);
                    jsonObject.put("isDevice", isDevice);

                    sendData = jsonObject + "END";
                    out.write(sendData.getBytes());

                    //in =  socket.getInputStream();
                    in = new BufferedReader(new InputStreamReader(socket.getInputStream(), "UTF-8"));
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
                String message = edtSend.getText().toString();
                if(socket.isConnected())
                {
                    if(!socket.isOutputShutdown())
                    {
                        sendData = message + "END";
                        try {
                            out.write(sendData.getBytes());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this, "output is shutdown", Toast.LENGTH_SHORT).show();
                    }

                }
                else
                {
                    Toast.makeText(MainActivity.this, "socket doesn't connect", Toast.LENGTH_SHORT).show();
                }
            }
        });

        new Thread(MainActivity.this).start();
    }

}
