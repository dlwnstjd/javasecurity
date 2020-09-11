package aes;

/*
 * 공개키 암호화
 */
public class CipherMain2 {
	public static void main(String[] args) {
		String plain1 = "안녕하세요. 홍길동입니다.";
		String key = "abc1234567";
		String cipher1 = CipherUtil.encrypt(plain1,key);	//암호화
		System.out.println("암호문: " + cipher1);
		String plain2 = CipherUtil.decrypt(cipher1,key);	//복호화
		System.out.println("복호화: " + plain2);
	}
	
}
