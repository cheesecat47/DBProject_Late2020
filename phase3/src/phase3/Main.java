package phase3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
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
    public static LoginInfo loginInfo = new LoginInfo();


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
        connectToDB();

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
                    if (loginInfo.isStatus()) {
                        after_login();
                    } else {
                        System.out.println("�α��ο� �����߽��ϴ�.");
                    }
                    break;
                default:
                    System.out.println("���α׷��� �����մϴ�.");

                    scanner.close();
                    // Release database resources.
                    closeConnDB();

                    System.exit(0);
            } // end switch
            System.out.println();
        } // end while
    }


    public static void connectToDB() {
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

        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnDB() {
        try {
            // Close the Statement object.
            if (stmt != null) {
                stmt.close();
                System.out.println("stmt.close");
            }
            // Close the Connection object.
            if (conn != null) {
                conn.close();
                System.out.println("conn.close");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean checkBdayFormat(String bday) {
        // ������� ���� �˻�
        // https://coding-factory.tistory.com/529
        String bday_pattern = "^\\d{4}-\\d{2}-\\d{2}$";
        return Pattern.matches(bday_pattern, bday);
    }

    public static String checkSex(String sex) {
        if (sex.equals("m") || sex.equals("M")
                || sex.equals("f") || sex.equals("F")) {
            return sex.toUpperCase();
        } else {
            return "null";
        }
    }

    public static boolean checkPhoneFormat(String phone) {
        String phone_pattern = "^\\d{3}-\\d{4}$";
        return Pattern.matches(phone_pattern, phone);
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
        if (!checkBdayFormat(account_bday)) {
            System.out.println("��������� ���Ŀ� �°� �Է����ּ���.");
            return;
        }

        // ���� �˻�. M�̳� F�� �ƴϸ� NULL �Է�, m�̳� f�� �빮�ڷ� �ٲٱ�.
        account_sex = checkSex(account_sex);

        // ��ȭ��ȣ ���� �˻�
        if (!checkPhoneFormat(account_phone)) {
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
        System.out.println();

        try {
            sql = "insert into account values ('" + account_id
                    + "', '" + account_pw + "', '" + account_name
                    + "', TO_DATE('" + account_bday + "', 'yyyy-mm-dd'), '"
                    + account_sex + "', '" + account_address + "', '"
                    + account_phone + "', 'customer', '" + account_job
                    + "', 'Basic')";
//			System.out.println("sql: " + sql);

            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
            System.out.println("ȸ�� ������ �Ϸ�Ǿ����ϴ�.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    public static void logIn() {
//        System.out.println("�α���");
        System.out.print("���̵� �Է��ϼ���: ");
        String account_id = scanner.nextLine();
        System.out.print("��й�ȣ�� �Է��ϼ���: ");
        String account_pw = scanner.nextLine();

        try {
            sql = "select account_id, account_pw from account "
                    + "where account_id = '" + account_id
                    + "' and account_pw = '" + account_pw + "'";
//			System.out.println("sql: " + sql);

            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                String rs1 = rs.getString(1);
                String rs2 = rs.getString(2);
//                System.out.printf("ID: %s, PW: %s\n", rs1, rs2);
                loginInfo.setStatus(true);
                loginInfo.setId(rs1);
                loginInfo.setPw(rs2);
                return;
            }
            System.out.println("���̵� �Ǵ� ��й�ȣ�� Ʋ�Ƚ��ϴ�.");
            loginInfo.setStatus(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        loginInfo = null;
    }

    public static void after_login() {
        System.out.println("�α��ο� �����߽��ϴ�.");
        System.out.println();

        while (loginInfo != null && loginInfo.isStatus()) {
            System.out.println("----------------------------------------");
            System.out.println("A: ȸ�� ��ȣ ���� / B: ��� ��ȣ ���� / C: ȸ�� Ż�� / ��Ÿ: �α� �ƿ�");
            System.out.print("�޴��� �����ϼ���(��ҹ��� ��� ����) >> ");

            String op = scanner.nextLine();
            System.out.println();
            switch (op) {
                case "A":
                case "a":
                    changeAccountInfo();
                    break;
                case "B":
                case "b":
                    changeAccountPW();
                    break;
                case "C":
                case "c":
                    deleteAccount();
                    break;
                default:
                    System.out.println("�α׾ƿ� �մϴ�.");
                    logout();
                    return;
            } // end switch
            System.out.println();
        } // end while
    }

    public static void changeAccountInfo() {
        System.out.println("ȸ�� ������ �����մϴ�. �Է����� ������ ������� �ʽ��ϴ�.");
        System.out.print("��й�ȣ�� �Է��ϼ���: ");
        String account_pw = scanner.nextLine();
        // ��й�ȣ Ʋ���� ���� �Ұ�
        if (!loginInfo.getPw().equals(account_pw)) {
            System.out.println("��й�ȣ�� Ʋ�Ƚ��ϴ�.");
            return;
        }

        sql = "update account set ";
        ArrayList<String> toBeUpdated = new ArrayList<>();

        System.out.print("�̸��� �Է��ϼ���: ");
        String account_name = scanner.nextLine();
        if (!account_name.equals("")) {
            toBeUpdated.add("account_name = '" + account_name + "'");
        }

        System.out.print("��������� �Է��ϼ���(yyyy-mm-dd): ");
        String account_bday = scanner.nextLine();
        if (!account_bday.equals("")) {
            // ������� �Է��ߴµ� ���� �� ������ ���� �Ұ�
            if (!checkBdayFormat(account_bday)) {
                System.out.println("��������� ���Ŀ� �°� �Է����ּ���.");
                return;
            }
            toBeUpdated.add("account_bday = TO_DATE('" + account_bday + "', 'yyyy-mm-dd')");
        }

        System.out.print("������ �Է��ϼ���(M/F): ");
        String account_sex = scanner.nextLine();
        if (!checkSex(account_sex).equals("null")) {
            toBeUpdated.add("account_sex = '" + account_sex + "'");
        }

        System.out.print("�ּҸ� �Է��ϼ���: ");
        String account_address = scanner.nextLine();
        if (!account_address.equals("")) {
            toBeUpdated.add("account_address = '" + account_address + "'");
        }

        System.out.print("��ȭ��ȣ�� �Է��ϼ���(000-0000): ");
        String account_phone = scanner.nextLine();
        if (!account_phone.equals("")) {
            // ��ȭ��ȣ �Է��ߴµ� ���� �� ������ ���� �Ұ�
            if (!checkPhoneFormat(account_phone)) {
                System.out.println("��ȭ��ȣ�� ���Ŀ� �°� �Է����ּ���.");
                return;
            }
            toBeUpdated.add("account_phone = '" + account_phone + "'");
        }

        System.out.print("������ �Է��ϼ���: ");
        String account_job = scanner.nextLine();
        if (!account_job.equals("")) {
            toBeUpdated.add("account_job = '" + account_job + "'");
        }

//        System.out.println("toBeUpdated: " + toBeUpdated);
        String toBeUpdatedStr = String.join(", ", toBeUpdated);
//        System.out.println("toBeUpdatedStr: " + toBeUpdatedStr);
        sql += toBeUpdatedStr
                + " where account_id = '" + loginInfo.getId() + "'";
        System.out.println("sql: " + sql);
        
        try {
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
            System.out.println("ȸ�� ���� ������ �Ϸ�Ǿ����ϴ�.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("ȸ�� ���� ���� �� ������ �߻��߽��ϴ�.");
        }
    }

    public static void changeAccountPW() {
        System.out.println("��� ��ȣ�� �����մϴ�. �Է����� ������ ������� �ʽ��ϴ�.");
        System.out.print("���� ��� ��ȣ�� �Է��ϼ���: ");
        String account_pw = scanner.nextLine();

        if (account_pw.equals("")) {
            System.out.println("��� ��ȣ�� ������� �ʽ��ϴ�.");
            return;
        }

        // ��й�ȣ Ʋ���� ���� �Ұ�
        if (!loginInfo.getPw().equals(account_pw)) {
            System.out.println("��� ��ȣ�� Ʋ�Ƚ��ϴ�.");
            return;
        }

        System.out.print("�� ��� ��ȣ�� �Է��ϼ���: ");
        String new_pw = scanner.nextLine();
        System.out.print("�� �� �� �Է��ϼ���: ");
        String new_pw2 = scanner.nextLine();

        if (!new_pw.equals(new_pw2)) {
            System.out.println("��� ��ȣ�� �ٸ��ϴ�.");
            return;
        }

        try {
            sql = "update account set account_pw = '" + new_pw
                    + "' where account_id = '" + loginInfo.getId() + "'";
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
            loginInfo.setPw(new_pw);

            System.out.println("��� ��ȣ ������ �Ϸ�Ǿ����ϴ�.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("��� ��ȣ ���� �� ������ �߻��߽��ϴ�.");
        }
    }

    public static void deleteAccount(){
        System.out.println("ȸ������ Ż���մϴ�. �Է����� ������ ������� �ʽ��ϴ�.");
        System.out.print("��� ��ȣ�� �Է��ϼ���: ");
        String account_pw = scanner.nextLine();

        if (account_pw.equals("") || !loginInfo.getPw().equals(account_pw)) {
            System.out.println("Ż�� ��ҵǾ����ϴ�.");
            return;
        }

        try {
            sql = "delete from write_rate where account_id = '"
                + loginInfo.getId() + "'";
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");

            sql = "delete from account where account_id = '"
                    + loginInfo.getId() + "'";
            res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");

            System.out.println("ȸ�� Ż�� �Ϸ�Ǿ����ϴ�.");
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("ȸ�� Ż�� �� ������ �߻��߽��ϴ�.");
        }

        logout();
    }
}