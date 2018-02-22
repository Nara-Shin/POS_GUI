/*------------------------------------------------------------------
[ProductDAO JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. public Connection getConn() : DB���� �޼ҵ�
 2. public List sellProductList(ProductDTO vProd) : [SELECT]��������Ʈ ���
 3. public boolean sellProduct(ProductDTO vProd) : [UPDATE]��ǰ ����
 4. public boolean updateProduct(ProductDTO vProd) : [UPDATE]��ǰ ȯ��
 5. public List getProductCheck(ProductDTO vProd) : [SELECT]�� ǰ���� ������ ��� �޼ҵ�
 6. public List getProductList() : [SELECT]��ü ��� ��ȸ  >> �ڵ� �� ����
 7. public void setProd(ResultSet rs, ProductDTO dto) : Select ����� DTO�� set
 -------------------------------------------------------------------*/

//�̸� ��Ģ : ���̺��DAO , ���̺��DTO
//CRUD : Create;insert , Read;Select, Update, delete

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//DB ó��
public class ProductDAO {
	private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	private static final String DB_URL = "jdbc:mariadb://localhost:3306/ldcc";

	private static final String USERNAME = "root"; // DB ID
	private static final String PASSWORD = "root"; // DB �н�����
	
	private static ProductDTO dto;

	public ProductDAO() {

	}

	/** DB���� �޼ҵ� */
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

	/** [UPDATE]��ǰ ���� **/
	public boolean sellProduct(ProductDTO vProd) {
		boolean ok = false;
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConn();
			// ��ǰ ���� -> ���ڵ带 ���� ���� ���ҽ�Ŵ
			String sql = "UPDATE prod_info SET prod_quantity=prod_quantity-? WHERE prod_code=?";

			ps = con.prepareStatement(sql);
			ps.setInt(1, vProd.getProd_quantity());
			ps.setString(2, vProd.getProd_code());

			// ���� ����� ����
			int r = ps.executeUpdate(); // ���� -> ����
			// 1~n: ���� , 0 : ����

			if (r > 0)
				ok = true; // ������ �����Ǹ� ok���� true�� ����

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok;
	}

	/** [SELECT]��������Ʈ ��� **/
	public List sellProductList(ProductDTO vProd) {
		System.out.println("dto=" + vProd.toStringUpdate());

		dto = new ProductDTO();
		Connection con = null; // ����
		PreparedStatement ps = null; // ���
		ResultSet rs = null; // ���

		List<ProductDTO> result_list = new ArrayList<ProductDTO>();

		try {
			// ����
			con = getConn();
			// ���� �׸�� ����Ʈ ��ȸ
			String sql = "select * from prod_info where prod_code=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, vProd.getProd_code());
			// ������� ����
			rs = ps.executeQuery();

			while (rs.next()) {
				String code = rs.getString("prod_code");
				String name = rs.getString("prod_name");
				int price = rs.getInt("prod_price");
				int weight = rs.getInt("prod_weight");
				int quantity = rs.getInt("prod_quantity");
				dto.setProd_code(code);
				dto.setProd_name(name);
				dto.setProd_price(price);
				dto.setProd_quantity(weight);
				dto.setProd_weight(quantity);
				result_list.add(dto);
			} // while
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��� ����Ʈ ��ȯ
		return result_list;
	}

	/** [UPDATE]��ǰ ȯ�� **/
	public boolean updateProduct(ProductDTO vProd) {
		System.out.println("dto=" + vProd.toStringUpdate());
		
		boolean ok = false;
		Connection con = null;
		PreparedStatement ps = null;
		
		try {
			// ����
			con = getConn();
			// ��ǰ ȯ���� ���� ����. ���ڵ带 �̿��Ͽ� ���� ������Ŵ
			String sql = "UPDATE prod_info SET prod_quantity=prod_quantity+? WHERE prod_code=?";

			ps = con.prepareStatement(sql);
			ps.setInt(1, vProd.getProd_quantity());
			ps.setString(2, vProd.getProd_code());

			// ���� ��� ����
			int r = ps.executeUpdate(); // ���� -> ����
			// 1~n: ���� , 0 : ����

			if (r > 0)
				ok = true; // ������ �����Ǹ� ok���� true�� ����

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok;
	}
	
	/** [SELECT]��ü ��� ��ȸ >> �ڵ� �� ���� **/
	public List getProductList() {
		Connection con = null; // ����
		PreparedStatement ps = null; // ���
		ResultSet rs = null; // ���
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		try {

			con = getConn();
			String sql = "select * from prod_info order by prod_code asc";
			ps = con.prepareStatement(sql);
			rs = ps.executeQuery();
			while (rs.next()) {
				dto = new ProductDTO();
				// result set
				setProd(rs, dto);
				result_list.add(dto);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��� ����Ʈ ��ȯ
		return result_list;
	}

	/** [SELECT]�� ǰ���� ������ ��� �޼ҵ� **/
	public List getProductCheck(ProductDTO vProd) {
		Connection con = null; // ����
		PreparedStatement ps = null; // ���
		ResultSet rs = null; // ���
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();

		try {
			// ����
			con = getConn();
			// ���ڵ带 ���� �ش� ��ǰ select
			String sql = "select * from prod_info where prod_code=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, vProd.getProd_code());

			rs = ps.executeQuery();

			while (rs.next()) {
				// result set
				dto = new ProductDTO();
				setProd(rs, dto);
				result_list.add(dto);
			}

		} catch (Exception e) {
			e.printStackTrace();
		}
		// ��� ����Ʈ ��ȯ
		return result_list;
	}
	
	/** Select ����� DTO�� set **/
	public void setProd(ResultSet rs, ProductDTO dto) {
		String code;
		try {
			code = rs.getString("prod_code");
			String name = rs.getString("prod_name");
			int price = rs.getInt("prod_price");
			int weight = rs.getInt("prod_weight");
			int quantity = rs.getInt("prod_quantity");
			dto.setProd_code(code);
			dto.setProd_name(name);
			dto.setProd_price(price);
			dto.setProd_quantity(weight);
			dto.setProd_weight(quantity);
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}