package hash;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Scanner;

/*
 * 1. 화면에서 아이디와 비밀번호를 입력받아서 해당 아이디가 usersecurity 테이블에 없으면 "아이디 확인" 출력
 * 2. 해당 아이디의 비밀번호를 비교해서 맞으면 "반갑습니다. 아이디님" 출력
 * 3. 해당 아이디의 비밀번호를 비교해서 틀리면 "비밀번호 확인" 출력
 */
public class DigestMain3 {
	public static void main(String[] args) throws ClassNotFoundException, SQLException, NoSuchAlgorithmException {
		Class.forName("org.mariadb.jdbc.Driver");
		while(true) {
			Connection conn = DriverManager.getConnection
					("jdbc:mariadb://localhost:3306/classdb","scott","1234");
			PreparedStatement pstmt = conn.prepareStatement
					("select password from usersecurity where userid=?");
			Scanner scan = new Scanner(System.in);
			System.out.println("아이디를 입력하세요");
			String dbId = scan.nextLine();
			pstmt.setString(1, dbId);
			System.out.println("비밀번호를 입력하세요");
			String dbPw = scan.nextLine();		
			ResultSet rs = pstmt.executeQuery();
			
			if(rs.next()) {
				//md: SHA-256 알고리즘을 실행하는 해쉬암호 객체
				MessageDigest md = MessageDigest.getInstance("SHA-256");
				String hashpass = "";
				byte[] plain = dbPw.getBytes();
				byte[] hash = md.digest(plain);	//해쉬 샐행문
				for(byte b : hash) {
					hashpass += String.format("%02X", b);
				}
				if(rs.getString(1).equals(hashpass)) {
					System.out.println("반갑습니다. " + dbId + "님");
				}else {
					System.out.println("비밀번호가 틀렸습니다. 다시 입력해주세요");
				}			
			}else {	//id없음
				System.out.println("아이디 없음.");
			}
		}
		
	}

}
