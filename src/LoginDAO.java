/*------------------------------------------------------------------
[LoginDAO JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. public Connection getConn() : DB���� �޼ҵ�
 2. public List loginTry(LoginDTO vLog) : [SELECT]���� ������ ��� �޼ҵ�
 -------------------------------------------------------------------*/
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class LoginDAO {
	private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	private static final String DB_URL = "jdbc:mariadb://localhost:3306/ldcc";

	private static final String USERNAME = "root"; // DB ID
	private static final String PASSWORD = "root"; // DB �н�����

	
	public LoginDAO() {

	}
	
	/** DB���� �޼ҵ� **/
	public Connection getConn() {
		Connection con = null;

		try {
			Class.forName(JDBC_DRIVER); // 1. ����̹� �ε�
			con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD); // 2. ����̹� ����

		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}
	
	/** [SELECT]���� ������ ��� �޼ҵ� **/
	public List loginTry(LoginDTO vLog) {
		System.out.println("dto=" + vLog.toString());
		
		Connection con = null; // ����
		PreparedStatement ps = null; // ���
		ResultSet rs = null; // ���
		List<LoginDTO> result_list = new ArrayList<LoginDTO>();

		try {
			// DB����
			con = getConn();
			
			// user_info���̺��� Ŭ���̾�Ʈ���� �Է��� id���� �� select
			String sql = "select * from user_info where user_id=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, vLog.getUser_id()); // id ����
			
			//���� ������ ����
			rs = ps.executeQuery();

			while (rs.next()) {
				// ���� ���� ����� ��ȯ
				LoginDTO login = new LoginDTO();
				String id = rs.getString("user_id");
				String pw = rs.getString("user_pw");
				
				// ���� ���� ����� set
				login.setUser_id(id);
				login.setUser_pw(pw);

				// ��� ����Ʈ�� add
				result_list.add(login);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		return result_list;
	}
}