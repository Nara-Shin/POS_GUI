/*------------------------------------------------------------------
[LoginDTO JAVA]

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
public class LoginDTO {
	private String user_id;
	private String user_pw;
	
	//Getter and Setter
	public String getUser_id() {
		return user_id;
	}
	public void setUser_id(String user_id) {
		this.user_id = user_id;
	}
	public String getUser_pw() {
		return user_pw;
	}
	public void setUser_pw(String user_pw) {
		this.user_pw = user_pw;
	}
	
	
	//toString
	@Override
	public String toString() {
		return "LoginDTO [user_id=" + user_id + ", user_pw=" + user_pw + "]";
	}
}