package phase3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.Scanner;
import java.util.regex.Pattern;


public class Main {
//	public static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
	public static final String URL = "jdbc:oracle:thin:@localhost:1600:xe";
	public static final String USER = "team9";
	public static final String USER_PW = "dbteam9";
	public static Scanner scanner = null;
	public static Connection conn = null;
	public static Statement stmt = null;
	public static String sql = "";
    public static ResultSet rs = null;
    

	public static void main(String[] args) {
		scanner = new Scanner(System.in);
		System.out.println("COMP322 Database Phase3 - Team9");
		System.out.println();
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			System.err.println("error = " + e.getMessage());
			System.exit(1);
		}
		
		// conn
	    try {
	        conn = DriverManager.getConnection(URL, USER, USER_PW);
	        System.out.println("Connected to the DB: " + conn);
	    } catch (SQLException e) {
	        e.printStackTrace();
	        System.err.println("Cannot get a connection: " + e.getMessage());
	        System.exit(1);
	    }
	    System.out.println();
	
	    try{
	        conn.setAutoCommit(false);
	        stmt = conn.createStatement();
	    } catch (SQLException e) {
	        e.printStackTrace();
	    }
		
	    
		while (true) {	
			System.out.println("----------------------------------------");
			System.out.println("A: 회원 가입 / B: 로그인 / 기타: 종료");
			System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");
			
			String op = scanner.nextLine();
			System.out.println();
			switch (op) {
				case "A":
				case "a":
					signUp();
					break;
				case "B":
				case "b":
					logIn();
					break;
				default:
					System.out.println("프로그램을 종료합니다.");
					
					scanner.close();
			        // Release database resources.
			        try {
			            // Close the Statement object.
			            stmt.close();
			            System.out.println("stmt.close");
			            // Close the Connection object.
			            conn.close();
			            System.out.println("conn.close");
			        } catch (SQLException e) {
			            e.printStackTrace();
			        }
					System.exit(0);
			} // end switch
			System.out.println();
		} // end while
		


//        String sql = "select * from movie";
//        ResultSet rs;
//		try {
//			rs = stmt.executeQuery(sql);
//			while (rs.next()) {
//				System.out.println(rs.toString());
//			}
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//        System.out.println();
//
//
//        // Release database resources.
//        try {
//            // Close the Statement object.
//            stmt.close();
//            System.out.println("stmt.close");
//            // Close the Connection object.
//            conn.close();
//            System.out.println("conn.close");
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }
	}
	
	public static void signUp() {
		System.out.println("회원가입");
		System.out.println("* 표시는 필수 입력 사항입니다.");
		System.out.print("* 아이디를 입력하세요: ");
		String account_id = scanner.nextLine();
		System.out.print("* 비밀번호를 입력하세요: ");
		String account_pw = scanner.nextLine();
		System.out.print("* 이름을 입력하세요: ");
		String account_name = scanner.nextLine();
		System.out.print("생년월일을 입력하세요(yyyy-mm-dd): ");
		String account_bday = scanner.nextLine();
		System.out.print("성별을 입력하세요(M/F): ");
		String account_sex = scanner.nextLine();
		System.out.print("주소를 입력하세요: ");
		String account_address = scanner.nextLine();
		System.out.print("* 전화번호를 입력하세요(000-0000): ");
		String account_phone = scanner.nextLine();
		System.out.print("직업을 입력하세요: ");
		String account_job = scanner.nextLine();
		
		// 필수 항목 검사
		if (account_id.equals("") || account_pw.equals("") 
				|| account_name.equals("") || account_phone.equals("")) {
			System.out.println("필수 항목을 입력하지 않았습니다.");
			return;
		}
		
		// 생년월일 포맷 검사
		// https://coding-factory.tistory.com/529
		String bday_pattern = "^\\d{4}-\\d{2}-\\d{2}$";
		if (!Pattern.matches(bday_pattern, account_bday)) {
			System.out.println("생년월일을 형식에 맞게 입력해주세요.");
			return;
		}
		
		// 성별 검사. M이나 F가 아니면 NULL 입력, m이나 f면 대문자로 바꾸기.
		if (account_sex.equals("")) {
			account_sex = "null";
		} else if (account_sex.equals("m") || account_sex.equals("M") 
				|| account_sex.equals("f") || account_sex.equals("F")) {
			account_sex = account_sex.toUpperCase();
		}
		
		// 전화번호 포맷 검사
		String phone_pattern = "^\\d{3}-\\d{4}$";
		if (!Pattern.matches(phone_pattern, account_phone)) {
			System.out.println("전화번호를 형식에 맞게 입력해주세요.");
			return;
		}
		
		// 필수 아닌 항목 입력을 안 했으면 "null"이라는 문자열로 바꾸기
		if (account_address.equals("")) {
			account_address = "null";
		}
		if (account_job.equals("")) {
			account_job = "null";
		}
		System.out.println();
		
		System.out.println("입력하신 정보입니다.");
		System.out.println("아이디: " + account_id);
		System.out.println("비밀번호: " + account_pw);
		System.out.println("이름: " + account_name);
		System.out.println("생년월일: " + account_bday);
		System.out.println("성별: " + account_sex);
		System.out.println("주소: " + account_address);
		System.out.println("전화번호: " + account_phone);
		System.out.println("직업: " + account_job);
		// 권한 입력 및 멤버십 등급은 회원가입 시에는 표시하지 않습니다.
		
		try {
			sql = "insert into account values ('" + account_id 
				+ "', '" + account_pw + "', '" + account_name
				+ "', TO_DATE('" + account_bday + "', 'yyyy-mm-dd'), '"
				+ account_sex + "', '" + account_address + "', '"
				+ account_phone + "', 'customer', '" + account_job 
				+ "', 'Basic')";
			System.out.println("sql: " + sql);
			int res = stmt.executeUpdate(sql);
			System.out.println("회원 가입이 완료되었습니다.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	public static void logIn() {
		System.out.println("로그인");
	}
}
