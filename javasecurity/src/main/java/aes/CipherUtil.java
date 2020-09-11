package aes;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.security.Key;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.CipherOutputStream;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

public class CipherUtil {
	private static byte[] randomKey;
	private final static byte[] iv = new byte[] {
			(byte) 0x8E, 0x12, 0x39, (byte)0x9C,
			0x07, 0x72, 0x6F, (byte)0x5A,
			(byte)0x8E, 0x12, 0x39, (byte)0x9C,
			0x07, 0x72, 0x6F, (byte)0x5A};
	static Cipher cipher;
	static {	//static 초기화 블럭: static 변수의 초기화 
		try {
			cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	public static byte[] getRandomKey(String algo) 
			throws NoSuchAlgorithmException {
		//AES용 암호화 키를 생성 객체
		KeyGenerator keyGen = KeyGenerator.getInstance(algo);
		keyGen.init(128);	//AES 알고리즘: 128비트 ~ 192비트 키로 설정 가능
		SecretKey key = keyGen.generateKey();
		return key.getEncoded();	//AES용 키
	}
	public static String encrypt(String plain) {
		byte[] cipherMsg = new byte[1024];
		try {
			randomKey = getRandomKey("AES");
			Key key = new SecretKeySpec(randomKey, "AES");
			//초기화 벡터 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			//Cipher.ENCRYPT_MODE: 암호화 모드
			cipher.init(Cipher.ENCRYPT_MODE, key, paramSpec);
			cipherMsg = cipher.doFinal(plain.getBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim();
	}
	private static String byteToHex(byte[] cipherMsg) {
		if(cipherMsg == null) return null;
		String str = "";
		for(byte b : cipherMsg) {
			str += String.format("%02X", b);
		}
		return str;
	}
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			Key key = new SecretKeySpec(randomKey, "AES");
			AlgorithmParameterSpec paramSpec =
					new IvParameterSpec(iv);
			//Cipher.DECRYPT_MODE: 복호화 모드
			cipher.init(Cipher.DECRYPT_MODE, key, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim()));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	//str: E1 38 F6 ...
	//	   1110001 00111000 11110110
	private static byte[] hexToByte(String str) {
		if(str == null || str.length() < 2) return null;
		int len = str.length() /2;
		byte[] buf = new byte[len];
		for(int i = 0; i <len; i++) {
			buf[i] = (byte)Integer.parseInt(str.substring(i*2,i*2+2),16);
		}
		return buf;
	}
	//128비트 = 16바이트
	public static String encrypt(String plain, String key) {
		byte[] cipherMsg = new byte[1024];
		try {
			randomKey = getRandomKey("AES");
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			//초기화 벡터 설정
			AlgorithmParameterSpec paramSpec = new IvParameterSpec(iv);
			//Cipher.ENCRYPT_MODE: 암호화 모드
			cipher.init(Cipher.ENCRYPT_MODE, genKey, paramSpec);
			cipherMsg = cipher.doFinal(plain.getBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg).trim();
	}
	private static byte[] makeKey(String key) {
		int len = key.length();
		char ch = 'A';
		for(int i=len;i<16;i++) key += ch++;
		return key.substring(0,16).getBytes();
	}
	public static String decrypt(String cipherMsg, String key) {
		byte[] plainMsg = new byte[1024];
		try {
			Key genKey = new SecretKeySpec(makeKey(key), "AES");
			AlgorithmParameterSpec paramSpec =
					new IvParameterSpec(iv);
			//Cipher.DECRYPT_MODE: 복호화 모드
			cipher.init(Cipher.DECRYPT_MODE, genKey, paramSpec);
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim()));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	public static String makehash(String msg) throws NoSuchAlgorithmException {
		MessageDigest md = MessageDigest.getInstance("SHA-256");
		byte[] plain = msg.getBytes();
		byte[] hash = md.digest(plain);
		return byteToHex(hash);
	}
	public static void encryptFile(String plainFile, String cipherFile, String strkey) {
		try {
			getKey(strkey);
			ObjectInputStream ois =
					new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key)ois.readObject();
			ois.close();
			AlgorithmParameterSpec paramSpec = 
					new IvParameterSpec(iv);
			cipher.init(Cipher.ENCRYPT_MODE,key,paramSpec);
			FileInputStream fis = new FileInputStream(plainFile);
			FileOutputStream fos = new FileOutputStream(cipherFile);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while((len = fis.read(buf)) != -1) {
				cos.write(buf,0,len);
			}
			fis.close();
			cos.flush();
			fos.flush();
			cos.close();
			fos.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		
	}
	private static void getKey(String key) throws Exception {
		Key genkey = new SecretKeySpec(makeKey(key), "AES");
		ObjectOutputStream out = new ObjectOutputStream
				(new FileOutputStream("key.ser"));
		out.writeObject(genkey);
		out.flush();
		out.close();
	}

	public static void decryptFile(String cipherFile, String plainFile, String strkey) {
		try {
			getKey(strkey);	//key 파일에 등록
			ObjectInputStream ois =
					new ObjectInputStream(new FileInputStream("key.ser"));
			Key key = (Key)ois.readObject();
			ois.close();
			AlgorithmParameterSpec paramSpec = 
					new IvParameterSpec(iv);
			cipher.init(Cipher.DECRYPT_MODE,key,paramSpec);
			FileInputStream fis = new FileInputStream(plainFile);
			FileOutputStream fos = new FileOutputStream(cipherFile);
			CipherOutputStream cos = new CipherOutputStream(fos, cipher);
			byte[] buf = new byte[1024];
			int len;
			while((len = fis.read(buf)) != -1) {
				cos.write(buf,0,len);
			}
			fis.close();
			cos.flush();
			fos.flush();
			cos.close();
			fos.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
}
