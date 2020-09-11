package rsa;

import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.PrivateKey;
import java.security.PublicKey;

import javax.crypto.Cipher;

/*
 * RSA: 공개키 암호 알고리즘. 비대칭키.
 * 		공개키로 암호화 -> 개인키로 복호화 가능 : 기밀성
 * 		개인키로 암호화 -> 공개키로 복호화 가능 : 부인방지
 */

public class CipherRSA {
	static Cipher cipher;
	static PrivateKey priKey;	//개인키	
	static PublicKey pubKey;	//공개키
	
	static {	//static 초기화 블럭: static 변수의 초기화 
		try {
			cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
			//공개키 방식의 알고리즘 방식에서 사용되는 키 생성 객체
			KeyPairGenerator key =
					KeyPairGenerator.getInstance("RSA");
			//2048비트로 키 크기를 설정
			key.initialize(2048);
			KeyPair keyPair = key.genKeyPair();
			priKey = keyPair.getPrivate();	//개인키
			pubKey = keyPair.getPublic();	//공개키
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static String encrypt(String plain) {
		byte[] cipherMsg = new byte[1024];
		try {
			//암호화 모드 설정
			cipher.init(Cipher.ENCRYPT_MODE, pubKey);
			cipherMsg = cipher.doFinal(plain.getBytes());
		}catch(Exception e) {
			e.printStackTrace();
		}
		return byteToHex(cipherMsg);
	}
	private static String byteToHex(byte[] cipherMsg) {
		if(cipherMsg == null) return null;
		int len =cipherMsg.length;
		String str = "";
		for(byte b : cipherMsg) {
			str += String.format("%02X", b);
		}
		return str;
	}
	
	public static String decrypt(String cipherMsg) {
		byte[] plainMsg = new byte[1024];
		try {
			cipher.init(Cipher.DECRYPT_MODE, priKey);	//복호화
			//바이트형을 문자열로 변환
			plainMsg = cipher.doFinal(hexToByte(cipherMsg.trim()));
		}catch(Exception e) {
			e.printStackTrace();
		}
		return new String(plainMsg).trim();
	}
	private static byte[] hexToByte(String str) {
		if(str == null || str.length() < 2) return null;
		int len = str.length() /2;
		byte[] buf = new byte[len];
		for(int i = 0; i <len; i++) {
			buf[i] = (byte)Integer.parseInt(str.substring(i*2,i*2+2),16);
		}
		return buf;
	}
	

}
