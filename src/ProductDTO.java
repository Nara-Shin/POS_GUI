/*------------------------------------------------------------------
[ProductDTO JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. Getter and Setter
 2. toString -- DAO에서 사용
 -------------------------------------------------------------------*/
public class ProductDTO {
	private String prod_code;
	private String prod_name;
	private int prod_price;
	private int prod_weight;
	private int prod_quantity;

	// DTO 객체 확인

	public String getProd_code() {
		return prod_code;
	}

	public void setProd_code(String prod_code) {
		this.prod_code = prod_code;
	}

	public String getProd_name() {
		return prod_name;
	}

	public void setProd_name(String prod_name) {
		this.prod_name = prod_name;
	}

	public int getProd_price() {
		return prod_price;
	}

	public void setProd_price(int prod_price) {
		this.prod_price = prod_price;
	}

	public int getProd_weight() {
		return prod_weight;
	}

	public void setProd_weight(int prod_weight) {
		this.prod_weight = prod_weight;
	}

	public int getProd_quantity() {
		return prod_quantity;
	}

	public void setProd_quantity(int prod_quantity) {
		this.prod_quantity = prod_quantity;
	}

	@Override
	public String toString() {
		return "ProductDTO [prod_code=" + prod_code + ", prod_name=" + prod_name + ", prod_price=" + prod_price
				+ ", prod_weight=" + prod_weight + ", prod_quantity=" + prod_quantity + "]";
	}

	public String toStringUpdate() {
		return "ProductDTO [prod_code=" + prod_code + ", prod_quantity=" + prod_quantity + "]";
	}

}