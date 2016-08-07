package xhs.com.multithreadclient;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;

/**
 * Created by user on 2016/8/7.
 */
public class ClientThread implements Runnable {
    private Socket socket;
    private Handler handler;
    public Handler revHandler;

    //BufferedReader br = null;
    private InputStream in = null;
    private OutputStream os = null;

    private static final String HOST = "114.215.97.243";
    private static final int PORT = 1234;

    private static final String ACCOUNT = "775079852@qq.com";
    private static final String PASSWORD = "123";
    private static final int ISDEVICE = 0;

    public ClientThread(Handler handler) {
        this.handler = handler;
    }

    @Override
    public void run() {
        try {
            socket = new Socket("114.215.97.243", 1234);
            //br = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            in = socket.getInputStream();
            os = socket.getOutputStream();

            //发送登录数据
            JSONObject loginData = new JSONObject();
            loginData.put("type", "login");
            loginData.put("account", ACCOUNT);
            loginData.put("password", PASSWORD);
            loginData.put("isDevice", ISDEVICE);
            String packedLoginData = loginData + "END";
            os.write(packedLoginData.getBytes("UTF-8"));
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        new Thread() {
            @Override
            public void run() {
                String content = null;
                try {
                    /*
                    while ((content = br.readLine()) != null)
                    {
                        Message msg = new Message();
                        msg.what = 0x123;
                        msg.obj = content;
                        handler.sendMessage(msg);
                    }
                    */
                    while (true) {
                        if (socket.isConnected()) {
                            if (!socket.isInputShutdown()) {
                                byte[] buf = new byte[1024];
                                int len = in.read(buf);
                                String text = new String(buf, 0, len);
                                Message msg = new Message();
                                msg.what = 0x123;
                                msg.obj = text;
                                handler.sendMessage(msg);
                            }
                        }
                    }

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();

        Looper.prepare();

        revHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == 0x345) {
                    try {
                        JSONObject jsonObject = new JSONObject();
                        jsonObject.put("isDevice", ISDEVICE);
                        jsonObject.put("type", "send");
                        jsonObject.put("content", msg.obj.toString());
                        String sendData = jsonObject + "END";
                        os.write(sendData.getBytes("UTF-8"));
                        //os.write((msg.obj.toString() + "END").getBytes("UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Looper.loop();
    }
}
