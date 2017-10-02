package blogging;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.omg.CORBA.SystemException;



public class Mysql {
	private static final String dbhost = "DBHOST";
	private static final String dbname = "DBNAME";
	private static final String dbuser = "DBUSER";
	private static final String dbpass = "DBPASS";
	
	private static Connection conn = null;
	
	private static PreparedStatement set_article_stmt = null;
	private static PreparedStatement get_article_id_stmt = null;
	private static PreparedStatement get_article_stmt = null;
	private static PreparedStatement get_article_count_stmt = null;
	private static PreparedStatement set_title_vector_stmt = null;
	private static PreparedStatement set_post_vector_stmt = null;
	private static PreparedStatement get_title_vector_stmt = null;
	private static PreparedStatement get_post_vector_stmt = null;
	private static PreparedStatement get_title_from_article_stmt = null;
	
	// DB INITIALIZE
	public static void init_conn() {
		try {
			Class.forName("com.mysql.jdbc.Driver").newInstance();
			conn = DriverManager.getConnection("jdbc:mysql://"+dbhost+"/"+dbname,dbuser,dbpass);
		} catch (Exception e) {
			System.err.println("Error: fail to load jdbc driver: "+e.getMessage());
			System.exit(1);
		}
		
		// PREPARE STATEMENTS
		try {
			set_article_stmt = conn.prepareStatement("INSERT INTO article (id,title,text) VALUES (?, ?, ?)");
			get_article_id_stmt = conn.prepareStatement("SELECT id FROM article WHERE id = ?");
			get_article_stmt = conn.prepareStatement("SELECT id,title,text FROM article");
			get_article_count_stmt = conn.prepareStatement("SELECT COUNT(*) FROM article");
			set_title_vector_stmt = conn.prepareStatement("INSERT INTO title (id, tvec) VALUES (?, ?)");
			set_post_vector_stmt = conn.prepareStatement("INSERT INTO post (id, pvec) VALUES (?, ?)");
			get_title_vector_stmt = conn.prepareStatement("SELECT tvec FROM title WHERE id = ?");
			get_post_vector_stmt = conn.prepareStatement("SELECT pvec FROM post WHERE id = ?");
			get_title_from_article_stmt = conn.prepareStatement("SELECT title FROM article WHERE id = ?");
		} catch (SQLException e) {
			System.err.println("Error: fail to prepare statement"+e.getMessage());
			System.exit(1);
		}
	}
	
	public static String get_title_from_article(int id) {
		try {
			get_title_from_article_stmt.setInt(1, id);
			ResultSet rs = get_title_from_article_stmt.executeQuery();
			if (rs.next())
				return rs.getString("title");
		} catch (SQLException e) {
			System.err.println("get_title_from_article: "+e.getMessage());
			System.exit(1);
		}
		
		return "";
	}
	
	public static double[] convertStrArr2DoubleArr(String[] strArr) {
		double[] dbArr = new double[strArr.length];
		
		for (int i = 0; i < strArr.length; i++) 
			dbArr[i] = Double.parseDouble(strArr[i]);
		
		return dbArr;
	}
	
	public static double[] get_post_vector(int id) {
		try {
			get_post_vector_stmt.setInt(1, id);
			ResultSet rs = get_post_vector_stmt.executeQuery();
			if (rs.next())
				return convertStrArr2DoubleArr(rs.getString("pvec").split("\t"));
		} catch (SQLException e) {
			System.err.println("get_post_vector: "+e.getMessage());
			System.exit(1);
		}
		
		return null;
	}
	
	public static double[] get_title_vector(int id) {
		try {
			get_title_vector_stmt.setInt(1, id);
			ResultSet rs = get_title_vector_stmt.executeQuery();
			if (rs.next())
				return convertStrArr2DoubleArr(rs.getString("tvec").split("\t"));
		} catch (SQLException e) {
			System.err.println("get_title_vector: "+e.getMessage());
			System.exit(1);
		}
		
		return null;
	}
	
	public static void set_post_vector(int id, String pvec) {
		try {
			set_post_vector_stmt.setInt(1, id);
			set_post_vector_stmt.setString(2, pvec);
			set_post_vector_stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("set_post_vector: "+e.getMessage());
			System.exit(1);
		}
	}
	
	public static void set_title_vector(int id, String tvec) {
		try {
			set_title_vector_stmt.setInt(1, id);
			set_title_vector_stmt.setString(2, tvec);
			set_title_vector_stmt.executeUpdate();
		} catch (SQLException e) {
			System.err.println("set_title_vector: "+e.getMessage());
			System.exit(1);
		}
	}
	
	public static int get_article_count() {
		try {
			ResultSet rs = get_article_count_stmt.executeQuery();
			if(rs.next())
				return rs.getInt(1);
		} catch (SQLException e) {
			System.err.println("get_article_count: "+e.getMessage());
			System.exit(1);
		}
		
		return -1;
	}
	
	public static String[][] get_article() {
		String[][] article = new String[get_article_count()][2];
		try {
			ResultSet rs = get_article_stmt.executeQuery();
			while(rs.next()) {
				article[rs.getInt("id")-1][0] = rs.getString("title");
				article[rs.getInt("id")-1][1] = rs.getString("text");
			}
		} catch (SQLException e) {
			System.err.println("get_article: "+e.getMessage());
			System.exit(1);
		}
		
		return article;
	}
	
	public static void set_article(int id, String title, String text) {
		try {
			set_article_stmt.setInt(1, id);
			set_article_stmt.setString(2, title);
			set_article_stmt.setString(3, text);
			set_article_stmt.executeUpdate();
			System.out.println(id+":\t"+title);
		} catch (SQLException e) {
			System.err.println("set_article: "+e.getMessage());
		}
	}
	
	public static boolean get_article_id(int id) {
		try {
			get_article_id_stmt.setInt(1, id);
			ResultSet rs = get_article_id_stmt.executeQuery();
			if(rs.next())
				return true;
		} catch (SQLException e) {
			System.err.println("get_article_id: "+e.getMessage());
			System.exit(1);
		}
		
		return false;
	}
	
}
