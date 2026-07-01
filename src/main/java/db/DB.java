package db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * 数据库连接工具类
 * 作用：
 * 1. 加载 MySQL 驱动
 * 2. 获取数据库连接
 * 3. 关闭数据库资源
 *
 * 当前连接的数据库：storage_management
 */
public class DB {

    /**
     * 数据库连接地址
     *
     * storage_management 是你刚刚创建成功的数据库名
     * useUnicode=true 和 characterEncoding=utf8 用于支持中文
     * serverTimezone=Asia/Shanghai 用于避免时区报错
     * useSSL=false 用于关闭 SSL 警告
     * allowPublicKeyRetrieval=true 用于避免部分 MySQL 8 登录错误
     */
    private static final String URL =
            "jdbc:mysql://localhost:3306/storage_management"
                    + "?useUnicode=true"
                    + "&characterEncoding=utf8"
                    + "&serverTimezone=Asia/Shanghai"
                    + "&useSSL=false"
                    + "&allowPublicKeyRetrieval=true";

    /**
     * 数据库用户名
     * 如果你的 MySQL 用户名不是 root，需要改这里
     */
    private static final String USERNAME = "root";

    /**
     * 数据库密码
     * 这里一定要改成你自己 MySQL 的密码
     */
    private static final String PASSWORD = "87998082345";

    //MySQL bin 目录路径常量
    private static final String MYSQL_BIN_PATH = "C:\\Program Files\\MySQL\\MySQL Server 8.0\\bin\\";

    /**
     * 静态代码块：类加载时自动加载 MySQL 驱动
     */
    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            System.out.println("MySQL 驱动加载成功");
        } catch (ClassNotFoundException e) {
            System.out.println("MySQL 驱动加载失败");
            e.printStackTrace();
        }
    }

    /**
     * 获取数据库连接
     *
     * @return Connection 数据库连接对象
     */
    public static Connection getConnection() {
        Connection conn = null;

        try {
            conn = DriverManager.getConnection(URL, USERNAME, PASSWORD);
        } catch (SQLException e) {
            System.out.println("数据库连接失败");
            e.printStackTrace();
        }

        return conn;
    }

    /**
     * 关闭 Connection
     *
     * @param conn 数据库连接对象
     */
    public static void close(Connection conn) {
        if (conn != null) {
            try {
                conn.close();
            } catch (SQLException e) {
                System.out.println("关闭 Connection 失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 关闭 Statement 和 Connection
     *
     * @param stmt SQL 执行对象
     * @param conn 数据库连接对象
     */
    public static void close(Statement stmt, Connection conn) {
        if (stmt != null) {
            try {
                stmt.close();
            } catch (SQLException e) {
                System.out.println("关闭 Statement 失败");
                e.printStackTrace();
            }
        }

        close(conn);
    }

    /**
     * 关闭 ResultSet、Statement 和 Connection
     *
     * @param rs   查询结果集
     * @param stmt SQL 执行对象
     * @param conn 数据库连接对象
     */
    public static void close(ResultSet rs, Statement stmt, Connection conn) {
        if (rs != null) {
            try {
                rs.close();
            } catch (SQLException e) {
                System.out.println("关闭 ResultSet 失败");
                e.printStackTrace();
            }
        }

        close(stmt, conn);
    }

    /**
     * 回滚事务
     * 后面做采购入库、销售出库时会用到
     *
     * @param conn 数据库连接对象
     */
    public static void rollback(Connection conn) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException e) {
                System.out.println("事务回滚失败");
                e.printStackTrace();
            }
        }
    }

    /**
     * 测试数据库是否连接成功
     * 右键运行这个 main 方法，如果控制台输出“数据库连接成功”，说明 DB.java 没问题
     */
    public static void main(String[] args) {
        Connection conn = getConnection();

        if (conn != null) {
            System.out.println("数据库连接成功");
            close(conn);
        } else {
            System.out.println("数据库连接失败，请检查数据库名、用户名、密码和 MySQL 服务");
        }
    }

    /** 获取数据库用户名 */
    public static String getUsername() {
        return USERNAME;
    }

    /** 获取数据库密码 */
    public static String getPassword() {
        return PASSWORD;
    }

    /** 获取 MySQL bin 目录路径（用于备份恢复） */
    public static String getMysqlBinPath() {
        return MYSQL_BIN_PATH;
    }

    /** 获取数据库名称（可从 URL 中解析，也可直接返回常量） */
    public static String getDbName() {
        // 从 URL 中提取数据库名，或直接返回
        return "storage_management";
    }
}