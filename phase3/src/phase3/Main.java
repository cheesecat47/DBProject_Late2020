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
                    if (loginInfo.isStatus()) {
                        after_login();
                    } else {
                        System.out.println("로그인에 실패했습니다.");
                    }
                    break;
                default:
                    System.out.println("프로그램을 종료합니다.");

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
        // 생년월일 포맷 검사
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
        if (!checkBdayFormat(account_bday)) {
            System.out.println("생년월일을 형식에 맞게 입력해주세요.");
            return;
        }

        // 성별 검사. M이나 F가 아니면 NULL 입력, m이나 f면 대문자로 바꾸기.
        account_sex = checkSex(account_sex);

        // 전화번호 포맷 검사
        if (!checkPhoneFormat(account_phone)) {
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
            System.out.println("회원 가입이 완료되었습니다.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.println();
    }

    public static void logIn() {
//        System.out.println("로그인");
        System.out.print("아이디를 입력하세요: ");
        String account_id = scanner.nextLine();
        System.out.print("비밀번호를 입력하세요: ");
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
            System.out.println("아이디 또는 비밀번호가 틀렸습니다.");
            loginInfo.setStatus(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        loginInfo = null;
    }

    public static void after_login() {
        System.out.println("로그인에 성공했습니다.");
        System.out.println();

        while (loginInfo != null && loginInfo.isStatus()) {
            System.out.println("----------------------------------------");
            System.out.println("A: 회원 번호 수정 / B: 비밀 번호 수정 / C: 회원 탈퇴 / 기타: 로그 아웃");
            System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");

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
                    System.out.println("로그아웃 합니다.");
                    logout();
                    return;
            } // end switch
            System.out.println();
        } // end while
    }

    public static void changeAccountInfo() {
        System.out.println("회원 정보를 수정합니다. 입력하지 않으면 변경되지 않습니다.");
        System.out.print("비밀번호를 입력하세요: ");
        String account_pw = scanner.nextLine();
        // 비밀번호 틀리면 변경 불가
        if (!loginInfo.getPw().equals(account_pw)) {
            System.out.println("비밀번호가 틀렸습니다.");
            return;
        }

        sql = "update account set ";
        ArrayList<String> toBeUpdated = new ArrayList<>();

        System.out.print("이름을 입력하세요: ");
        String account_name = scanner.nextLine();
        if (!account_name.equals("")) {
            toBeUpdated.add("account_name = '" + account_name + "'");
        }

        System.out.print("생년월일을 입력하세요(yyyy-mm-dd): ");
        String account_bday = scanner.nextLine();
        if (!account_bday.equals("")) {
            // 생년월일 입력했는데 포맷 안 맞으면 변경 불가
            if (!checkBdayFormat(account_bday)) {
                System.out.println("생년월일을 형식에 맞게 입력해주세요.");
                return;
            }
            toBeUpdated.add("account_bday = TO_DATE('" + account_bday + "', 'yyyy-mm-dd')");
        }

        System.out.print("성별을 입력하세요(M/F): ");
        String account_sex = scanner.nextLine();
        if (!checkSex(account_sex).equals("null")) {
            toBeUpdated.add("account_sex = '" + account_sex + "'");
        }

        System.out.print("주소를 입력하세요: ");
        String account_address = scanner.nextLine();
        if (!account_address.equals("")) {
            toBeUpdated.add("account_address = '" + account_address + "'");
        }

        System.out.print("전화번호를 입력하세요(000-0000): ");
        String account_phone = scanner.nextLine();
        if (!account_phone.equals("")) {
            // 전화번호 입력했는데 포맷 안 맞으면 변경 불가
            if (!checkPhoneFormat(account_phone)) {
                System.out.println("전화번호를 형식에 맞게 입력해주세요.");
                return;
            }
            toBeUpdated.add("account_phone = '" + account_phone + "'");
        }

        System.out.print("직업을 입력하세요: ");
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
            System.out.println("회원 정보 수정이 완료되었습니다.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("회원 정보 수정 중 오류가 발생했습니다.");
        }
    }

    public static void changeAccountPW() {
        System.out.println("비밀 번호를 수정합니다. 입력하지 않으면 변경되지 않습니다.");
        System.out.print("현재 비밀 번호를 입력하세요: ");
        String account_pw = scanner.nextLine();

        if (account_pw.equals("")) {
            System.out.println("비밀 번호가 변경되지 않습니다.");
            return;
        }

        // 비밀번호 틀리면 변경 불가
        if (!loginInfo.getPw().equals(account_pw)) {
            System.out.println("비밀 번호가 틀렸습니다.");
            return;
        }

        System.out.print("새 비밀 번호를 입력하세요: ");
        String new_pw = scanner.nextLine();
        System.out.print("한 번 더 입력하세요: ");
        String new_pw2 = scanner.nextLine();

        if (!new_pw.equals(new_pw2)) {
            System.out.println("비밀 번호가 다릅니다.");
            return;
        }

        try {
            sql = "update account set account_pw = '" + new_pw
                    + "' where account_id = '" + loginInfo.getId() + "'";
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
            loginInfo.setPw(new_pw);

            System.out.println("비밀 번호 수정이 완료되었습니다.");
            conn.commit();
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("비밀 번호 수정 중 오류가 발생했습니다.");
        }
    }

    public static void deleteAccount(){
        System.out.println("회원에서 탈퇴합니다. 입력하지 않으면 변경되지 않습니다.");
        System.out.print("비밀 번호를 입력하세요: ");
        String account_pw = scanner.nextLine();

        if (account_pw.equals("") || !loginInfo.getPw().equals(account_pw)) {
            System.out.println("탈퇴가 취소되었습니다.");
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

            System.out.println("회원 탈퇴가 완료되었습니다.");
            conn.commit();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("회원 탈퇴 중 오류가 발생했습니다.");
        }

        logout();
    }
}