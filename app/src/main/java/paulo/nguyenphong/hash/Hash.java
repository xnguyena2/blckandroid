package paulo.nguyenphong.hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Nguyen Phong on 12/30/2016.
 */

public class Hash {
    public static String createHash(){
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd-MM-yyyy-hh-mm-ss");
        String timeStampe = simpleDateFormat.format(new Date());
        String result = null;

        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("MD5");
            md.update(timeStampe.getBytes());

            byte byteData[] = md.digest();

            //convert the byte to hex format method 1
            StringBuffer sb = new StringBuffer();
            for (int i = 0; i < byteData.length; i++) {
                sb.append(Integer.toString((byteData[i] & 0xff) + 0x100, 16).substring(1));
            }
            result = sb.toString();
        } catch (NoSuchAlgorithmException e) {
            result = null;
            e.printStackTrace();
        }
        return result;
    }
}
