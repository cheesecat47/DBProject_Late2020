package phase3;

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
    public static AccountInfo accountInfo;


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
                    if (accountInfo.isStatus()) {
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
//            System.out.println("Connected to the DB: " + conn);
        } catch (SQLException e) {
            e.printStackTrace();
            System.err.println("Cannot get a connection: " + e.getMessage());
            System.exit(1);
        }
        System.out.println();

        try {
            conn.setAutoCommit(false);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void closeConnDB() {
        try {
            if (rs != null) {
                rs.close();
//                System.out.println("rs.close");
            }

            // Close the Statement object.
            if (stmt != null) {
                stmt.close();
//                System.out.println("stmt.close");
            }
            // Close the Connection object.
            if (conn != null) {
                conn.close();
//                System.out.println("conn.close");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public static boolean checkDateFormat(String bday) {
        // Date 형식 포맷 검사
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
        if (!checkDateFormat(account_bday)) {
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
            sql = "select * from account "
                    + "where account_id = '" + account_id
                    + "' and account_pw = '" + account_pw + "'";
//			System.out.println("sql: " + sql);

            rs = stmt.executeQuery(sql);
            if (rs.next()) {
                accountInfo = new AccountInfo(true,
                        rs.getString(1),
                        rs.getString(2),
                        rs.getString(3),
                        rs.getString(4),
                        rs.getString(5),
                        rs.getString(6),
                        rs.getString(7),
                        rs.getString(8),
                        rs.getString(9),
                        rs.getString(10)
                );
                return;
            }
            System.out.println("아이디 또는 비밀번호가 틀렸습니다.");
            accountInfo.setStatus(false);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void logout() {
        accountInfo.setStatus(false);
        accountInfo = null;
    }

    public static void after_login() {
        System.out.println("로그인에 성공했습니다.");
        System.out.println();

        while (accountInfo != null && accountInfo.isStatus()) {
            System.out.println("-------------------- ID: " + accountInfo.getId() + " --------------------");
            if (accountInfo.getIdentity().equals("customer")){
                System.out.println("A: 회원 관련 기능 / B: 영상물 관련 기능 / C: 평가 관련 기능 / 기타: 로그 아웃");
            } else if (accountInfo.getIdentity().equals("manager")){
                System.out.println("A: 회원 관련 기능 / B: 영상물 관련 기능 / C: 평가 관련 기능 / D: 관리자 기능 / 기타: 로그 아웃");
            }
            System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");

            String op = scanner.nextLine();
            System.out.println();
            if (accountInfo.getIdentity().equals("customer")){
                switch (op) {
                    case "A":
                    case "a":
                        accountFeatures();
                        break;
                    case "B":
                    case "b":
                        movieFeatures();
                        break;
                    case "C":
                    case "c":
                        rateFeatures();
                        break;
                    default:
                        System.out.println("로그아웃 합니다.");
                        logout();
                        return;
                } // end customer switch
            } else if (accountInfo.getIdentity().equals("manager")) {
                switch (op) {
                    case "A":
                    case "a":
                        accountFeatures();
                        break;
                    case "B":
                    case "b":
                        movieFeatures();
                        break;
                    case "C":
                    case "c":
                        rateFeatures();
                        break;
                    case "D":
                    case "d":
                        adminFeatures();
                        break;
                    default:
                        System.out.println("로그아웃 합니다.");
                        logout();
                        return;
                } // end manager switch
            }
            System.out.println();
        } // end while
    }

    public static void accountFeatures(){
        System.out.println();

        while (accountInfo != null && accountInfo.isStatus()) {
            System.out.println("-------------------- ID: " + accountInfo.getId() + " / 회원 관련 기능 --------------------");
            System.out.println("A: 회원 번호 수정 / B: 비밀 번호 수정 / C: 회원 탈퇴 / 기타: 뒤로 가기");
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
                    System.out.println("이전 메뉴로 돌아갑니다.");
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
        if (!accountInfo.getPw().equals(account_pw)) {
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
            if (!checkDateFormat(account_bday)) {
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
                + " where account_id = '" + accountInfo.getId() + "'";
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
        if (!accountInfo.getPw().equals(account_pw)) {
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
                    + "' where account_id = '" + accountInfo.getId() + "'";
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
            accountInfo.setPw(new_pw);

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

        if (account_pw.equals("") || !accountInfo.getPw().equals(account_pw)) {
            System.out.println("탈퇴가 취소되었습니다.");
            return;
        }

        try {
            sql = "delete from write_rate where account_id = '"
                + accountInfo.getId() + "'";
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");

            sql = "delete from account where account_id = '"
                    + accountInfo.getId() + "'";
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

    public static void movieFeatures(){
        System.out.println();

        while (accountInfo != null && accountInfo.isStatus()) {
            System.out.println("-------------------- ID: " + accountInfo.getId() + " / 영상물 관련 기능 --------------------");
            System.out.println("A: 전체 영상물 확인 / B: 영상물 제목 검색 / C: 영상물 조건 검색 / 기타: 뒤로 가기");
            System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");

            String op = scanner.nextLine();
            System.out.println();
            switch (op) {
                case "A":
                case "a":
                    searchAllMovie();
                    break;
                case "B":
                case "b":
                    searchMovieTitle();
                    break;
                case "C":
                case "c":
                    searchMovieCond();
                    break;
                default:
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
            } // end switch
            System.out.println();
        } // end while
    }

    public static void searchAllMovie(){
        System.out.println("전체 영상물을 검색합니다.");

        try {
            // 2.E 고려 안 한 것. 그냥 전체 쿼리.
            // sql = "select movie_title from movie order by movie_register_no";

            // 2.E 고려 한 것. 해당 회원이 평가한 영상은 검색에서 제외.
            sql = "select movie_register_no, movie_title from movie" +
                    " where movie_register_no not in (" +
                    " select movie_register_no from write_rate" +
                    " where account_id = '" + accountInfo.getId() + "'" +
                    ") order by movie_register_no";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            // https://wookoa.tistory.com/111
            rs.last();
            int rowCount = rs.getRow();
            if (rowCount == 0){
                System.out.println("검색된 영상물이 없습니다.");
                System.out.println();
                return;
            }
            rs.beforeFirst();

            while (rs.next()) {
                int rs1 = rs.getInt(1);
                String rs2 = rs.getString(2);
                System.out.printf("등록번호: %d / 제목: %s\n", rs1, rs2);
            }
            System.out.println();
            System.out.println(rowCount + "개의 영상물 검색이 완료되었습니다.");
            System.out.println();

            searchInResult(rs);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("영상물 검색 중 오류가 발생했습니다.");
        }
    }

    public static void searchMovieTitle(){
        System.out.println("영상물 제목으로 검색합니다.");

        try {
            System.out.print("검색어를 입력하세요: ");
            String movie_title = scanner.nextLine();

            // 2.E 고려 한 것. 해당 회원이 평가한 영상은 검색에서 제외.
            sql = "select movie_register_no, movie_title from movie" +
                    " where movie_title like '%" + movie_title + "%'" +
                    " and movie_register_no not in (" +
                    " select movie_register_no from write_rate" +
                    " where account_id = '" + accountInfo.getId() + "'" +
                    ") order by movie_register_no";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            rs.last();
            int rowCount = rs.getRow();
            if (rowCount == 0){
                System.out.println("검색된 영상물이 없습니다.");
                System.out.println();
                return;
            }
            rs.beforeFirst();

            while (rs.next()) {
                int rs1 = rs.getInt(1);
                String rs2 = rs.getString(2);
                System.out.printf("등록번호: %d / 제목: %s\n", rs1, rs2);
            }
            System.out.println();
            System.out.println(rowCount + "개의 영상물 검색이 완료되었습니다.");
            System.out.println();

            searchInResult(rs);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("영상물 검색 중 오류가 발생했습니다.");
        }
    }

    public static void searchMovieCond(){
        System.out.println("영상물 조건으로 검색합니다. 입력하지 않으면 해당 조건은 검색하지 않습니다.");

        try {
            // 2.E 고려 한 것. 해당 회원이 평가한 영상은 검색에서 제외.
            sql = "select m.movie_register_no, m.movie_title, v.version_country, v.version_name from movie m, category c, version v" +
                    " where m.movie_register_no = c.movie_register_no" +
                    " and m.movie_register_no = v.movie_register_no";

            System.out.print("종류를 입력하세요(" + getAllKind("movie_type", "movie") + "): ");
            String movie_type = scanner.nextLine();
            if (!movie_type.equals("")) {
                sql += " and m.movie_type = '" + movie_type + "'";
            }

            System.out.print("장르를 입력하세요(" + getAllKind("genre_name", "genre") + "): ");
            String genre_name = scanner.nextLine();
            if (!genre_name.equals("")) {
                sql += " and c.genre_name = '" + genre_name + "'";
            }

            System.out.print("상영 국가를 입력하세요(" + getAllKind("version_country", "version") + "): ");
            String version_country = scanner.nextLine();
            if (!version_country.equals("")) {
                sql += " and v.version_country = '" + version_country + "'";
            }

            sql += " and m.movie_register_no not in (" +
                    " select movie_register_no from write_rate" +
                    " where account_id = '" + accountInfo.getId() + "'" +
                    ") order by m.movie_register_no";
            System.out.println("sql: " + sql);

            rs = stmt.executeQuery(sql);

            rs.last();
            int rowCount = rs.getRow();
            if (rowCount == 0){
                System.out.println("검색된 영상물이 없습니다.");
                System.out.println();
                return;
            }
            rs.beforeFirst();

            while (rs.next()) {
                int rs1 = rs.getInt(1);
                String rs2 = rs.getString(2);
                String rs3 = rs.getString(3);
                String rs4 = rs.getString(4);
                System.out.printf("등록번호: %d / 제목: %s / 국가: %s / 버전명: %s\n", rs1, rs2, rs3, rs4);
            }
            System.out.println();
            System.out.println(rowCount + "개의 영상물 검색이 완료되었습니다.");
            System.out.println();

            searchInResult(rs);

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("영상물 검색 중 오류가 발생했습니다.");
        }
    }

    public static void searchInResult(ResultSet searched){
        System.out.print("검색한 영상물 목록에서 세부 정보를 보시겠습니까(Y/N)? ");
        String op = scanner.nextLine();
        switch (op) {
            case "Y":
            case "y":
                searchMovieDetail(searched);
                break;
            default:
        }
    }

    public static void searchMovieDetail(ResultSet searched){
        try {
            System.out.print("세부 정보를 보고 싶은 영상물의 등록번호를 입력해주세요: ");
            int movie_register_no = Integer.parseInt(scanner.nextLine());

            searched.beforeFirst();
            boolean isRegNoInResult = false;
            while (searched.next()) {
                int rs1 = rs.getInt(1);
                if (rs1 == movie_register_no) {
                    isRegNoInResult = true;
                    break;
                }
            }
            if (!isRegNoInResult) {
                System.out.println("검색 결과 중 해당 영상물이 없습니다.");
                return;
            }
            // 이제 검색 결과 중 특정 영상물 존재 확인. 그 세부 정보 출력.

            String movie_detail_info = "";

            sql = "select m.movie_register_no, m.movie_title, m.movie_type, m.movie_runtime, m.movie_start_year, c.genre_name" +
                    " FROM movie m, category c" +
                    " WHERE c.movie_register_no = m.movie_register_no" +
                    " and m.movie_register_no = '" + movie_register_no + "'";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                int rs1 = rs.getInt(1);
                String rs2 = rs.getString(2);
                String rs3 = rs.getString(3);
                String rs4 = rs.getString(4);
                String rs5 = rs.getString(5);
                String rs6 = rs.getString(6);
                movie_detail_info += "등록번호: " + rs1;
                movie_detail_info += " / 제목: " + rs2;
                movie_detail_info += " / 종류: " + rs3;
                movie_detail_info += " / 재생시간: " + rs4;
                movie_detail_info += " / 상영년도: " + rs5;
                movie_detail_info += " / 장르: " + rs6;
            }

            sql = "select avg(r.rating_score) as avg_rate" +
                    " FROM write_rate w, rating r" +
                    " WHERE w.rating_no = r.rating_no" +
                    " and w.movie_register_no = '" + movie_register_no + "'" +
                    " group by w.movie_register_no";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            if (rs.next()) {
                String rs1 = rs.getString(1);
                movie_detail_info += " / 평균평점: " + rs1;
            } else {
                movie_detail_info += " / 평균평점: '평가가 존재하지 않습니다.'";
            }

            System.out.println(movie_detail_info);
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("영상물 검색 중 오류가 발생했습니다.");
        }
    }

    public static void rateFeatures(){
        System.out.println();

        while (accountInfo != null && accountInfo.isStatus()) {
            System.out.println("-------------------- ID: " + accountInfo.getId() + " / 평가 관련 기능 --------------------");
            if (accountInfo.getIdentity().equals("customer")) {
                System.out.println("A: 나의 평가 내역 확인 / 기타: 뒤로 가기");
            } else if (accountInfo.getIdentity().equals("manager")) {
                System.out.println("A: 나의 평가 내역 확인 / B: 모든 평가 내역 확인 / 기타: 뒤로 가기");
            }

            System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");
            String op = scanner.nextLine();
            System.out.println();

            if (accountInfo.getIdentity().equals("customer")) {
                switch (op) {
                    case "A":
                    case "a":
                        viewMyRatings();
                        break;
                    default:
                        System.out.println("이전 메뉴로 돌아갑니다.");
                        return;
                } // end customer switch
            } else if (accountInfo.getIdentity().equals("manager")) {
                switch (op) {
                    case "A":
                    case "a":
                        viewMyRatings();
                        break;
                    case "B":
                    case "b":
                        viewAllRatings();
                        break;
                    default:
                        System.out.println("이전 메뉴로 돌아갑니다.");
                        return;
                } // end manager switch
            }
            System.out.println();
        } // end while
    }

    public static void viewMyRatings(){
        System.out.println("나의 평가 내역입니다.");

        try {
            sql = "select m.movie_title, r.rating_score, r.rating_description" +
                    " from movie m, rating r, write_rate w" +
                    " where m.movie_register_no = w.movie_register_no and r.rating_no = w.rating_no" +
                    " and w.account_id = '" + accountInfo.getId() + "'";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            rs.last();
            int rowCount = rs.getRow();
            if (rowCount == 0){
                System.out.println("검색된 평가 내역이 없습니다.");
                System.out.println();
                return;
            }
            rs.beforeFirst();

            while (rs.next()) {
                String rs1 = rs.getString(1);
                float rs2 = rs.getFloat(2);
                String rs3 = rs.getString(3);
                System.out.printf("제목: %s / 평점: %f / 내용: %s\n", rs1, rs2, rs3);
            }
            System.out.println();
            System.out.println(rowCount + "개의 영상물 검색이 완료되었습니다.");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("평가 내역 검색 중 오류가 발생했습니다.");
        }
    }

    public static void viewAllRatings(){
        System.out.println("모든 평가 내역입니다.");

        try {
            sql = "select w.account_id, m.movie_title, r.rating_score, r.rating_description" +
                    " from movie m, rating r, write_rate w" +
                    " where m.movie_register_no = w.movie_register_no and r.rating_no = w.rating_no";
//            System.out.println("sql: " + sql);
            rs = stmt.executeQuery(sql);

            rs.last();
            int rowCount = rs.getRow();
            if (rowCount == 0){
                System.out.println("검색된 평가 내역이 없습니다.");
                System.out.println();
                return;
            }
            rs.beforeFirst();

            while (rs.next()) {
                String rs1 = rs.getString(1);
                String rs2 = rs.getString(2);
                float rs3 = rs.getFloat(3);
                String rs4 = rs.getString(4);
                System.out.printf("평가자: %s / 제목: %s / 평점: %f / 내용: %s\n", rs1, rs2, rs3, rs4);
            }
            System.out.println();
            System.out.println(rowCount + "개의 영상물 검색이 완료되었습니다.");
            System.out.println();

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("평가 내역 검색 중 오류가 발생했습니다.");
        }
    }

    public static void adminFeatures(){
        System.out.println();

        while (accountInfo != null && accountInfo.isStatus()) {
            System.out.println("-------------------- ID: " + accountInfo.getId() + " / 관리자 기능 --------------------");
            System.out.println("A: 나의 평가 내역 확인 / B: 모든 평가 내역 확인 / 기타: 뒤로 가기");

            System.out.print("메뉴를 선택하세요(대소문자 모두 가능) >> ");
            String op = scanner.nextLine();
            System.out.println();

            switch (op) {
                case "A":
                case "a":
                    addNewMovieInfo();
                    break;
//                case "B":
//                case "b":
//                    modifyMovieInfo();
//                    break;
                default:
                    System.out.println("이전 메뉴로 돌아갑니다.");
                    return;
            } // end customer switch
            System.out.println();
        } // end while
    }

    public static void addNewMovieInfo() {
        System.out.println("새로운 영상물 등록");

        System.out.println("* 표시는 필수 입력 사항입니다.");
        System.out.print("* 영상물 제목을 입력하세요: ");
        String movie_title = scanner.nextLine();
        System.out.print("* 영상물 종류를 입력하세요: ");
        String movie_type = scanner.nextLine();
        System.out.print("* 상영 시간을 입력하세요(단위: 분): ");
        String movie_runtime = scanner.nextLine();
        System.out.print("상영 년도를 입력하세요(yyyy-mm-dd): ");
        String movie_start_year = scanner.nextLine();

        System.out.print("* 장르를 입력하세요(" + getAllKind("genre_name", "genre") + "): ");
        String genre_name = scanner.nextLine();

        System.out.println("버전 정보를 입력 받습니다.");
        System.out.print("상영 국가를 입력하세요(" + getAllKind("version_country", "version") + "): ");
        String version_country = scanner.nextLine();
        System.out.print("해당 버전의 이름을 입력하세요: ");
        String version_name = scanner.nextLine();

        if (movie_type.equals("TV Series")) {
            System.out.print("* 에피소드 이름을 입력하세요: ");
            String episode_name = scanner.nextLine();
        }

        // 필수 항목 검사
        if (movie_title.equals("") || movie_type.equals("")
                || movie_runtime.equals("") || genre_name.equals("")) {
            System.out.println("필수 항목을 입력하지 않았습니다.");
            return;
        }

        // 상영 년도 포맷 검사
        if (!checkDateFormat(movie_start_year)) {
            System.out.println("상영 년도를 형식에 맞게 입력해주세요.");
            return;
        }

//        // 필수 아닌 항목 입력을 안 했으면 "null"이라는 문자열로 바꾸기
//        if (account_address.equals("")) {
//            account_address = "null";
//        }
//        if (account_job.equals("")) {
//            account_job = "null";
//        }
//        System.out.println();
//
//        try {
//            sql = "insert into account values ('" + account_id
//                    + "', '" + account_pw + "', '" + account_name
//                    + "', TO_DATE('" + account_bday + "', 'yyyy-mm-dd'), '"
//                    + account_sex + "', '" + account_address + "', '"
//                    + account_phone + "', 'customer', '" + account_job
//                    + "', 'Basic')";
////			System.out.println("sql: " + sql);
//
//            int res = stmt.executeUpdate(sql);
//            System.out.println(res + " row updated.");
//            System.out.println("회원 가입이 완료되었습니다.");
//            conn.commit();
//        } catch (SQLException e) {
//            e.printStackTrace();
//        }

        System.out.println();
    }

    public static String getAllKind(String attr, String table) {
        String return_str = "";
        ArrayList<String> list = new ArrayList<>();
        try {
            String get_sql = "select distinct " + attr + " from " + table;
            rs = stmt.executeQuery(get_sql);

            while (rs.next()) {
                list.add(rs.getString(1));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return_str = String.join(", ", list);
        System.out.println("getAllKind: return_str: " + return_str);
        return return_str;
    }
}