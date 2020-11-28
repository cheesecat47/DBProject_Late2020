package phase3;

import java.sql.*;
import java.util.ArrayList;

public class DB {
    private Connection conn = null;
    private Statement stmt = null;
    private Savepoint savepoint = null;

    public DB() throws ClassNotFoundException{
        Class.forName("oracle.jdbc.driver.OracleDriver");
    }

    public void connectToDB() {
        System.out.println("DB: connectToDB");

        try {
            String URL = "jdbc:oracle:thin:@localhost:16000:xe";
            String USER = "team9";
            String USER_PW = "dbteam9";

            conn = DriverManager.getConnection(URL, USER, USER_PW);
            System.out.println("DB: connectToDB: conn: " + conn);

            conn.setAutoCommit(false);
            stmt = conn.createStatement(
                    ResultSet.TYPE_SCROLL_INSENSITIVE,
                    ResultSet.CONCUR_UPDATABLE
            );
            System.out.println("DB: connectToDB: stmt: " + stmt);

            savepoint = conn.setSavepoint("savepoint");
            System.out.println("DB: connectToDB: savepoint: " + savepoint);
        } catch (SQLException e) {
            System.err.println("DB: connectToDB: Cannot get a connection: " + e.getMessage());
        }
        System.out.println();
    }

    public void closeConnDB() {
        System.out.println("DB: closeConnDB");

        try {
            if (savepoint != null) {
//                conn.releaseSavepoint(savepoint);
                System.out.println("DB: closeConnDB: rs.close");
            }

            // Close the Statement object.
            if (stmt != null) {
                stmt.close();
                System.out.println("DB: closeConnDB: stmt.close");
            }
            // Close the Connection object.
            if (conn != null) {
                conn.close();
                System.out.println("DB: closeConnDB: conn.close");
            }
        } catch (SQLException e) {
            System.err.println("DB: closeConnDB: " + e.getMessage());
        }
    }

    public void commit() throws SQLException {
        try {
            conn.commit();
        } catch (SQLException e) {
            System.err.println("DB: commit: " + e.getMessage());
            throw e;
        }
    }

    public void rollback(){
        try {
            if (savepoint != null) {
                System.out.println("DB: rollback: 오류가 있었습니다. 이전 상태로 돌아갑니다.");
                conn.rollback(savepoint);
            }
            closeConnDB();
        } catch (SQLException e) {
            System.err.println("DB: rollback: " + e.getMessage());
        }
    }

    public void executeUpdate(String sql) throws SQLException {
        System.out.println("DB: executeUpdate: sql: " + sql);

        try {
            int res = stmt.executeUpdate(sql);
            System.out.println(res + " row updated.");
        } catch (SQLException e) {
            System.err.println("DB: executeUpdate: " + e.getMessage());
            throw e;
        }
    }

    public ResultSet executeQuery(String sql) {
        System.out.println("DB: executeQuery: sql: " + sql);
        ResultSet rs = null;

        try {
            rs = stmt.executeQuery(sql);
        } catch (SQLException e) {
            System.err.println("DB: executeQuery: " + e.getMessage());
        }

        return rs;
    }

    public void executeBatch(ArrayList<String> batch) throws SQLException {
        System.out.println("DB: executeBatch: batch: " + batch);

        try {
            for (String sql : batch) {
                stmt.addBatch(sql);
            }

            int[] count = stmt.executeBatch();
            System.out.println(count.length + " row updated.");

        } catch (SQLException e) {
            System.err.println("DB: executeBatch: " + e.getMessage());
            throw e;
        }
    }
}
