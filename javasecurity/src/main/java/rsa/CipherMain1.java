package rsa;


public class CipherMain1 {
	public static void main(String[] args) {
		String plain1 = "안녕하세요. 홍길동입니다.";
		String cipher1 = CipherRSA.encrypt(plain1);	//암호화
		System.out.println("암호문: " + cipher1);
		String plain2 = CipherRSA.decrypt(cipher1);	//복호화
		System.out.println("복호화: " + plain2);
	}
}
