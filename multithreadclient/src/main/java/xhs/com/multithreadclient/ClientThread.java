package xhs.com.multithreadclient;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

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
    InputStream in = null;
    OutputStream os = null;

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
        } catch (IOException e) {
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
                    while (true)
                    {
                        if (socket.isConnected())
                        {
                            if(!socket.isInputShutdown())
                            {
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
                        os.write((msg.obj.toString() + "END").getBytes("UTF-8"));
                        //os.write("HelloEND".getBytes("UTF-8"));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        Looper.loop();
    }
}
