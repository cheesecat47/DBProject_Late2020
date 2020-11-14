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
			System.out.println("A: ȸ�� ���� / B: �α��� / ��Ÿ: ����");
			System.out.print("�޴��� �����ϼ���(��ҹ��� ��� ����) >> ");
			
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
					System.out.println("���α׷��� �����մϴ�.");
					
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
		System.out.println("ȸ������");
		System.out.println("* ǥ�ô� �ʼ� �Է� �����Դϴ�.");
		System.out.print("* ���̵� �Է��ϼ���: ");
		String account_id = scanner.nextLine();
		System.out.print("* ��й�ȣ�� �Է��ϼ���: ");
		String account_pw = scanner.nextLine();
		System.out.print("* �̸��� �Է��ϼ���: ");
		String account_name = scanner.nextLine();
		System.out.print("��������� �Է��ϼ���(yyyy-mm-dd): ");
		String account_bday = scanner.nextLine();
		System.out.print("������ �Է��ϼ���(M/F): ");
		String account_sex = scanner.nextLine();
		System.out.print("�ּҸ� �Է��ϼ���: ");
		String account_address = scanner.nextLine();
		System.out.print("* ��ȭ��ȣ�� �Է��ϼ���(000-0000): ");
		String account_phone = scanner.nextLine();
		System.out.print("������ �Է��ϼ���: ");
		String account_job = scanner.nextLine();
		
		// �ʼ� �׸� �˻�
		if (account_id.equals("") || account_pw.equals("") 
				|| account_name.equals("") || account_phone.equals("")) {
			System.out.println("�ʼ� �׸��� �Է����� �ʾҽ��ϴ�.");
			return;
		}
		
		// ������� ���� �˻�
		// https://coding-factory.tistory.com/529
		String bday_pattern = "^\\d{4}-\\d{2}-\\d{2}$";
		if (!Pattern.matches(bday_pattern, account_bday)) {
			System.out.println("��������� ���Ŀ� �°� �Է����ּ���.");
			return;
		}
		
		// ���� �˻�. M�̳� F�� �ƴϸ� NULL �Է�, m�̳� f�� �빮�ڷ� �ٲٱ�.
		if (account_sex.equals("")) {
			account_sex = "null";
		} else if (account_sex.equals("m") || account_sex.equals("M") 
				|| account_sex.equals("f") || account_sex.equals("F")) {
			account_sex = account_sex.toUpperCase();
		}
		
		// ��ȭ��ȣ ���� �˻�
		String phone_pattern = "^\\d{3}-\\d{4}$";
		if (!Pattern.matches(phone_pattern, account_phone)) {
			System.out.println("��ȭ��ȣ�� ���Ŀ� �°� �Է����ּ���.");
			return;
		}
		
		// �ʼ� �ƴ� �׸� �Է��� �� ������ "null"�̶�� ���ڿ��� �ٲٱ�
		if (account_address.equals("")) {
			account_address = "null";
		}
		if (account_job.equals("")) {
			account_job = "null";
		}
		System.out.println();
		
		System.out.println("�Է��Ͻ� �����Դϴ�.");
		System.out.println("���̵�: " + account_id);
		System.out.println("��й�ȣ: " + account_pw);
		System.out.println("�̸�: " + account_name);
		System.out.println("�������: " + account_bday);
		System.out.println("����: " + account_sex);
		System.out.println("�ּ�: " + account_address);
		System.out.println("��ȭ��ȣ: " + account_phone);
		System.out.println("����: " + account_job);
		// ���� �Է� �� ����� ����� ȸ������ �ÿ��� ǥ������ �ʽ��ϴ�.
		
		try {
			sql = "insert into account values ('" + account_id 
				+ "', '" + account_pw + "', '" + account_name
				+ "', TO_DATE('" + account_bday + "', 'yyyy-mm-dd'), '"
				+ account_sex + "', '" + account_address + "', '"
				+ account_phone + "', 'customer', '" + account_job 
				+ "', 'Basic')";
			System.out.println("sql: " + sql);
			int res = stmt.executeUpdate(sql);
			System.out.println("ȸ�� ������ �Ϸ�Ǿ����ϴ�.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		System.out.println();
	}

	public static void logIn() {
		System.out.println("�α���");
	}
}
