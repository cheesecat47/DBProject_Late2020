package phase3;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;

public class Main {
//	public static final String URL = "jdbc:oracle:thin:@localhost:1521:orcl";
	public static final String URL = "jdbc:oracle:thin:@localhost:1600:xe";
	public static final String USER = "team9";
	public static final String USER_PW = "dbteam9";

	public static void main(String[] args) {
		Connection conn = null;
		Statement stmt = null;
		
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

        
        String sql = "select * from movie";
        ResultSet rs;
		try {
			rs = stmt.executeQuery(sql);
			while (rs.next()) {
				System.out.println(rs.toString());
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
        System.out.println();


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
	}

}
