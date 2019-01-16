package share.test;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import com.hc.share.util.Utils;

public class Md5TestMain {
	public static String encoderMd5Base64(String str) throws NoSuchAlgorithmException, UnsupportedEncodingException {
		return Base64.getEncoder().encodeToString(MessageDigest.getInstance("MD5").digest(str.getBytes("utf-8")));
	}

	public static void main(String[] args) {
		String str = "hanchen";
		try {
			System.out.println(Md5TestMain.encoderMd5Base64(str));
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		System.out.println(Utils.randomString());
	}
}
