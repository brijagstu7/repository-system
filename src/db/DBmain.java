package db;

import java.sql.*;

public class DBmain{

    static final String url = configc.SQL_URL;


    public static void main(String[] args) {
        String sql = "select * from x";

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection conn = DriverManager.getConnection(url,"root","");

            PreparedStatement pstmt = conn.prepareStatement(sql);

            ResultSet rs = pstmt.executeQuery();

			/*

			USAGES OF ResultSet:

				 rs.getInt(1)	rs.getInt(2)

		rs   ->								(initially)
				+---------------+------+
	rs.next()->	| a             | b    |
				+---------------+------+
				|    1          |    2 |
				+---------------+------+




			 */
            rs.next();
            System.out.println(rs.getInt(1));

        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
        }


    }
}
