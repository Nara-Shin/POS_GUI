/*------------------------------------------------------------------
[ProductDAO JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. public Connection getConn() : DB연결 메소드
 2. public List sellProductList(ProductDTO vProd) : [SELECT]결제리스트 출력
 3. public boolean sellProduct(ProductDTO vProd) : [UPDATE]상품 결제
 4. public boolean updateProduct(ProductDTO vProd) : [UPDATE]상품 환불
 5. public List getProductCheck(ProductDTO vProd) : [SELECT]한 품목의 정보를 얻는 메소드
 6. public List getProductList() : [SELECT]전체 재고 조회  >> 코드 순 정렬
 7. public void setProd(ResultSet rs, ProductDTO dto) : Select 결과값 DTO에 set
 -------------------------------------------------------------------*/

//이름 규칙 : 테이블명DAO , 테이블명DTO
//CRUD : Create;insert , Read;Select, Update, delete

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

//DB 처리
public class ProductDAO {
	private static final String JDBC_DRIVER = "org.mariadb.jdbc.Driver";
	private static final String DB_URL = "jdbc:mariadb://localhost:3306/ldcc";

	private static final String USERNAME = "root"; // DB ID
	private static final String PASSWORD = "root"; // DB 패스워드
	
	private static ProductDTO dto;

	public ProductDAO() {

	}

	/** DB연결 메소드 */
	public Connection getConn() {
		Connection con = null;

		try {
			Class.forName(JDBC_DRIVER); // 1. 드라이버 로딩
			con = DriverManager.getConnection(DB_URL, USERNAME, PASSWORD); // 2. 드라이버 연결

		} catch (Exception e) {
			e.printStackTrace();
		}

		return con;
	}

	/** [UPDATE]상품 결제 **/
	public boolean sellProduct(ProductDTO vProd) {
		boolean ok = false;
		Connection con = null;
		PreparedStatement ps = null;

		try {
			con = getConn();
			// 상품 결제 -> 바코드를 통해 수량 감소시킴
			String sql = "UPDATE prod_info SET prod_quantity=prod_quantity-? WHERE prod_code=?";

			ps = con.prepareStatement(sql);
			ps.setInt(1, vProd.getProd_quantity());
			ps.setString(2, vProd.getProd_code());

			// 쿼리 결과문 저장
			int r = ps.executeUpdate(); // 실행 -> 수정
			// 1~n: 성공 , 0 : 실패

			if (r > 0)
				ok = true; // 수정이 성공되면 ok값을 true로 변경

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok;
	}

	/** [SELECT]결제리스트 출력 **/
	public List sellProductList(ProductDTO vProd) {
		System.out.println("dto=" + vProd.toStringUpdate());

		dto = new ProductDTO();
		Connection con = null; // 연결
		PreparedStatement ps = null; // 명령
		ResultSet rs = null; // 결과

		List<ProductDTO> result_list = new ArrayList<ProductDTO>();

		try {
			// 연결
			con = getConn();
			// 결제 항목들 리스트 조회
			String sql = "select * from prod_info where prod_code=?";
			ps = con.prepareStatement(sql);
			ps.setString(1, vProd.getProd_code());
			// 쿼리결과 저장
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
		// 결과 리스트 반환
		return result_list;
	}

	/** [UPDATE]상품 환불 **/
	public boolean updateProduct(ProductDTO vProd) {
		System.out.println("dto=" + vProd.toStringUpdate());
		
		boolean ok = false;
		Connection con = null;
		PreparedStatement ps = null;
		
		try {
			// 연결
			con = getConn();
			// 상품 환불을 위한 쿼리. 바코드를 이용하여 수량 증가시킴
			String sql = "UPDATE prod_info SET prod_quantity=prod_quantity+? WHERE prod_code=?";

			ps = con.prepareStatement(sql);
			ps.setInt(1, vProd.getProd_quantity());
			ps.setString(2, vProd.getProd_code());

			// 실행 결과 저장
			int r = ps.executeUpdate(); // 실행 -> 수정
			// 1~n: 성공 , 0 : 실패

			if (r > 0)
				ok = true; // 수정이 성공되면 ok값을 true로 변경

		} catch (Exception e) {
			e.printStackTrace();
		}

		return ok;
	}
	
	/** [SELECT]전체 재고 조회 >> 코드 순 정렬 **/
	public List getProductList() {
		Connection con = null; // 연결
		PreparedStatement ps = null; // 명령
		ResultSet rs = null; // 결과
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
		// 결과 리스트 반환
		return result_list;
	}

	/** [SELECT]한 품목의 정보를 얻는 메소드 **/
	public List getProductCheck(ProductDTO vProd) {
		Connection con = null; // 연결
		PreparedStatement ps = null; // 명령
		ResultSet rs = null; // 결과
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();

		try {
			// 연결
			con = getConn();
			// 바코드를 통해 해당 상품 select
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
		// 결과 리스트 반환
		return result_list;
	}
	
	/** Select 결과값 DTO에 set **/
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