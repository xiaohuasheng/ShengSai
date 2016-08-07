package xhs.com.multithreadclient;
/**
 * Created by user on 2016/8/7.
 */
import java.io.UnsupportedEncodingException;
        import java.nio.ByteBuffer;

public class LongitudeLatitude {
    public static final byte COMMAND = 0x06;

    private byte[] byteWatchID = new byte[8];
    private byte[] byteLongitude;
    private byte[] byteLatitude;

    public String watchID;
    public byte state;
    public String longitude;
    public String latitude;


    protected String bytesToHexString(byte[] data) {
        String result = "";
        for (int i = 0; i < data.length; i++) {
            if (data[i]<0)
                result += Integer.toHexString(256+data[i]);
            else if (data[i]<16)
                result += "0" + Integer.toHexString(data[i]);
            else
                result += Integer.toHexString(data[i]);
        }
        return result.toUpperCase();
    }
    public LongitudeLatitude() {
    }

    public LongitudeLatitude(byte[] data) {
        ByteBuffer buffer = ByteBuffer.wrap(data);
        buffer.get();
        short len = buffer.getShort();
        buffer.get(byteWatchID);
        String ssss = bytesToHexString(byteWatchID);
        watchID = ssss.substring(0, ssss.length() - 1);
        buffer.get();
        state = buffer.get();
        buffer.get();

        ByteBuffer bb = ByteBuffer.allocate(50);
        byte b = buffer.get();
        while(b!='|') {
            bb.put(b);
            b = buffer.get();
        }
        bb.flip();
        byteLongitude = new byte[bb.limit()];
        bb.get(byteLongitude);
        try {
            longitude = new String(byteLongitude, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            longitude = "0";
        }

        bb.clear();
        b = buffer.get();
        while(b!='|') {
            bb.put(b);
            b = buffer.get();
        }
        bb.flip();
        byteLatitude = new byte[bb.limit()];
        bb.get(byteLatitude);
        try {
            latitude = new String(byteLatitude, "UTF-8").trim();
        } catch (UnsupportedEncodingException e) {
            latitude = "0";
        }

        bb.clear();
        b = buffer.get();
        while(b!='|') {
            bb.put(b);
            b = buffer.get();
        }
        bb.flip();
    }

    public String getLongitude() {
        return longitude;
    }

    public String getLatitude() {
        return latitude;
    }

    /*
    public static void main(String[] args) {
        byte[] data = new byte[]{0x4D,0x00,0x50,0x35,0x68,0x23,0x03,0x00,0x41,(byte) 0x96,0x50,0x06,0x00,0x7c,0x31,0x31,0x33,0x2e,0x32,0x33,0x34,0x35,0x36,0x37,0x7c,0x32,0x33,0x2e,0x31,0x32,0x33,0x34,0x35,0x36,0x7c,0x38,0x30,0x7c,0x31,0x36,0x34,0x7c,0x31,0x32,0x30,0x7c,0x34,0x36,0x30,0x7c,0x30,0x30,0x7c,0x33,0x32,0x38,0x37,0x7c,0x35,0x32,0x31,0x33,0x7c,(byte) 0xac,0x7c,0x32,0x30,0x31,0x34,0x30,0x39,0x32,0x34,0x31,0x34,0x33,0x38,0x32,0x38,0x7c,0x37,0x35,(byte) 0xF6,(byte) 0xAD,0x0D};
//		for (int i = 0; i < data.length; i++) {
//			System.out.print(data[i] + " ");
//		}
        LongitudeLatitude loc = new LongitudeLatitude(data);

        System.out.println(loc.longitude);
        System.out.println(loc.latitude);
    }
    */

}



