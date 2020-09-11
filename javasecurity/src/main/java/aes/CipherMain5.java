package aes;

//암호화된 파일을 복호화하여 p2.txt 파일에 저장하기
public class CipherMain5 {

	public static void main(String[] args) {
		String key = "abc1234567";
		CipherUtil.encryptFile("p1.txt","c.sec",key);
	}

}
