package db;

import java.sql.*;

public class DButils {

    private static final String url = configc.SQL_URL;
    private static final String user = configc.SQL_USR;
    private static final String password = configc.SQL_PWD;

    private static Connection conn = null;

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    /**
     * 获取jdbc的连接对象
     * 单例模式：在整个应用中只返回一个对象
     * @return Connection
     */
    public static Connection getConn() {
        try {
            // 当conn对象不等于null且对象未被关闭,conn.close()并不会把conn对象置为null，只是状态的改变
            if(null != conn && !conn.isClosed()) {
                return conn;
            }else{
                conn = DriverManager.getConnection(url, user, password);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return conn;
    }

    /**
     * 关闭jdbc资源
     * @param rs
     * @param pstmt
     * @param conn
     */
    public static void close(ResultSet rs, PreparedStatement pstmt, Connection conn) {
        try {
            if(null != rs) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }finally {
            try {
                if(null != pstmt) {
                    pstmt.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }finally {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
