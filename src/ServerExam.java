
/*------------------------------------------------------------------
[ServerExam JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. main�Լ� �� : Ű����(login/sell&fin/refund/check/bar_check/exit)�� ���� Ŭ���̾�Ʈ �䱸���� ó��
 2. private static void LoginLogic(String id, String pw) : �α��� ����
 3. private static void SellProduct(String code, int quantity) : ���� ����
 4. private static void UpdateProduct(String code, int quantity) : ȯ�� ����
 5. private static void CheckProduct() : ��� Ȯ�� ����
 6. private static void CheckBarProduct(String code) : ���ڵ�� ��� Ȯ�� ����
 7. private static void flushBuffer(List<ProductDTO> result_list) : ProductDTO ��ȯ��� �� Client�� ����
 8. private static void loginflushBuffer(List<LoginDTO> result_list) : LoginDTO ��ȯ��� �� Client�� ����
 
// javac ClientExam.java -encoding UTF-8
// java -Dfile.encoding="UTF8" pos.ClientExam
 -------------------------------------------------------------------*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ServerExam {
	// ThreadPoolExecutor(int corePoolSize, int maximumPoolSize, long keepAliveTime,
	// TimeUnit unit, BlockingQueue <Runnable > workQueue)

	// corePoolSize(������ �ּ� Thread��.), maximumPoolSize: 10(�ִ� Thread ������)
	// corePoolSize �� ��� thread�� Busy ������ ��쿡, ���ο� �½�ũ�� ť������ ���
	// corePoolSize �� �Ѵ� thread�� �ۼ����� ����

	static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue());
	static final int PORT_NUM = 6060;

	// ���� ���� �� ���� ����
	private static ServerSocket serverSocket;
	private static Socket socket;

	private static BufferedReader bufferedReader;
	private static BufferedWriter bufferedWriter;
	
	private static ProductDTO dto;
	private static ProductDAO dao;

	public static void main(String[] args) {
		threadPool.execute(new Runnable() {
			public void run() {
				System.out.println("Thread " + Thread.currentThread().getId() + " Start");

				try {
					try {
						while (true) {
							// �������� ����
							serverSocket = new ServerSocket(PORT_NUM);
							System.out.println("\nŬ���̾�Ʈ ���� ��� ��...");
							
							// �����������κ��� ���� ��ü ��������
							socket = serverSocket.accept();
							bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							System.out.println(socket.getInetAddress() + "�� ���ӵǾ����ϴ�.");

							// Ŭ���̾�Ʈ�κ��� �޽��� �Է¹���
							String clientMessage = bufferedReader.readLine();

							// �Է¹��� ������ ���� �ֿܼ� ���
							System.out.println("Ŭ���̾�Ʈ�� ������ ���� : " + clientMessage);
							
							// Ŭ���̾�Ʈ���� ���� flag�� ���� ������ ���� ����
							/** �α��� ���� **/
							if (clientMessage.equals("login")) {
								int i = 1;
								String[] login = new String[3];

								String line = "";
								while ((line = bufferedReader.readLine()) != null) {
									if (i == 1) {
										//id
										login[i] = line;
										System.out.println(i + ": " + line);
										i++;
									} else {
										//pw
										login[i] = line;
										System.out.println(i + ": " + line);
										break;
									}
								}

								// id�� pw�� ����  �α��� ���� ����
								LoginLogic(login[1], login[2]);
								
								/** ���� ���� **/
							}else if (clientMessage.equals("sell")) {
								int i = 1;
								int prod_num = 0;
								int cn = 0;
								int qn = 0;

								String[] prod = new String[3];
								String[] code_arr = new String[100];
								String[] qunatity_arr = new String[100];

								String line = "";
								while ((line = bufferedReader.readLine()) != null) {
									if (i == 0) {
										// flag
										prod[i] = line;
										if (prod[i].equals("fin")) {
											break;
										}
										i++;
									} else if (i == 1) {
										// code
										prod[i] = line;
										code_arr[cn] = prod[i];
										System.out.println(i + ": " + prod[i]);
										cn++;
										i++;
										prod_num++;
									} else {
										// quantity
										prod[i] = line;
										qunatity_arr[qn] = prod[i];
										System.out.println(i + ": " + prod[i]);
										i = 0;
										qn++;
									}
								}

								// �Է��� ��ǰ ����ŭ loop
								for (int j = 0; j < prod_num; j++) {
									// �������� ����
									SellProduct(code_arr[j], Integer.parseInt(qunatity_arr[j]));
								}
								/** ȯ�� ���� **/
							} else if (clientMessage.equals("refund")) {
								int i = 1;
								String[] prod = new String[3];

								String line = "";
								while ((line = bufferedReader.readLine()) != null) {
									if (i == 1) {
										// code
										prod[i] = line;
										System.out.println(i + ": " + line);
										i++;
									} else {
										// quantity
										prod[i] = line;
										System.out.println(i + ": " + line);
										break;
									}
								}

								// code �� quantity�� ���� ȯ�� ����
								UpdateProduct(prod[1], Integer.parseInt(prod[2]));
								
								/** �����ȸ ���� **/
							} else if (clientMessage.equals("check")) {
								// �����ȸ ���� ���� 
								CheckProduct();
								
								/** Ư�� ��ǰ �����ȸ ���� **/
							} else if (clientMessage.equals("bar_check")) {
								int i = 1;
								String[] prod = new String[3];

								String line = "";
								while ((line = bufferedReader.readLine()) != null) {
									if (i == 1) { 
										// code
										prod[i] = line;
										System.out.println(i + ": " + line);
										break;
									}
								}

								// Ư�� ��ǰ ��� ��ȸ
								CheckBarProduct(prod[1]);
								
								/** ���� ���� **/
							} else if (clientMessage.equals("exit")) {
								System.out.println("\n���� ����...");
								bufferedReader.close();
								serverSocket.close();
								socket.close();// ���� ����
								break;
							}
							serverSocket.close();
							socket.close();// ���� ����
						}

					} catch (Exception e) {
						e.printStackTrace();
					}
				} catch (Exception e) {
				}

				System.out.println("Thread " + Thread.currentThread().getId() + " End");
			}
		});

	}

	/** �α��� ���� -- DAO�� ID,PW ���� �� SELECT �� ���޹��� >> Ŭ���̾�Ʈ�� ���� **/
	private static void LoginLogic(String id, String pw) {
		List<LoginDTO> result_list = new ArrayList<LoginDTO>();
		LoginDAO dao = new LoginDAO();
		LoginDTO dto = new LoginDTO();

		// DTO�� Ŭ���̾�Ʈ���� ��ǲ�� ID, PW set
		String user_id = id;
		String user_pw = pw;
		dto.setUser_id(user_id);
		dto.setUser_pw(user_pw);

		// ��� ����Ʈ�� DAO���� ��ȯ�� ����� ����
		result_list = dao.loginTry(dto);

		// ��� �� Ŭ���̾�Ʈ�� ����
		loginflushBuffer(result_list);

	}

	/** ���� ���� --> DAO�� ���ڵ�, ���� ���� �� �ǸŸ���Ʈ ���޹��� >> Ŭ���̾�Ʈ�� ���� **/
	private static void SellProduct(String code, int quantity) {
		dto = new ProductDTO();
		dao = new ProductDAO();
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		String prod_code = code;
		int prod_quantity = quantity;
		
		// DTO�� Ŭ���̾�Ʈ���� ��ǲ�� code, quantity set
		dto.setProd_code(prod_code);
		dto.setProd_quantity(prod_quantity);
		
		// ��� ����Ʈ�� DAO���� ��ȯ�� ����� ����
		result_list = dao.sellProductList(dto);
		
		// ��� �� Ŭ���̾�Ʈ�� ����
		flushBuffer(result_list);
		boolean ok = dao.sellProduct(dto);
		if (ok) {
			System.out.println("���� ����");
		} else {
			System.out.println("���� ����");
		}
	}

	/** ȯ�� ���� --> DAO�� ���ڵ�, ���� ���� �� ȯ�� ����(T/F) ���޹��� >> Ŭ���̾�Ʈ�� ���� **/
	private static void UpdateProduct(String code, int quantity) throws Exception {
		dto = new ProductDTO();
		dao = new ProductDAO();
		
		String prod_code = code;
		int prod_quantity = quantity;
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		// DTO�� Ŭ���̾�Ʈ���� ��ǲ�� code, quantity set
		dto.setProd_code(prod_code);
		dto.setProd_quantity(prod_quantity);

		// DAO���� ��ȯ�� ����� ����
		boolean ok = dao.updateProduct(dto);

		if (ok) { // true
			System.out.println("ȯ�� ����");
			bufferedWriter.write("ȯ�� ����");
			bufferedWriter.newLine(); // readLine()���� �����Ƿ� ���ٳ��� �˸�
			bufferedWriter.flush();

		} else { // false
			System.out.println("ȯ�� ����");
			bufferedWriter.write("ȯ�� ����");
			bufferedWriter.newLine(); // readLine()���� �����Ƿ� ���ٳ��� �˸�
			bufferedWriter.flush();
		}
	}

	/** ��� Ȯ�� ���� --> ��ü ��� ��ȸ **/
	private static void CheckProduct() {
		 dao = new ProductDAO();
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		// ��� ����Ʈ�� DAO���� ��ȯ�� ����� ����
		result_list = dao.getProductList();
		
		// ��� �� Ŭ���̾�Ʈ�� ����
		flushBuffer(result_list);

	}

	/** ���ڵ�� ��� Ȯ�� ���� --> �Է� ���ڵ� ��ǰ ��� ��ȸ **/
	private static void CheckBarProduct(String code) throws IOException {
		dto = new ProductDTO();
		 dao = new ProductDAO();
		String prod_code = code;

		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		// DTO�� Ŭ���̾�Ʈ���� ��ǲ�� code set
		dto.setProd_code(prod_code);

		// ��� ����Ʈ�� DAO���� ��ȯ�� ����� ����
		result_list = dao.getProductCheck(dto);

		// ��� �� Ŭ���̾�Ʈ�� ����
		flushBuffer(result_list);

	}
	
	/** LoginDTO ��ȯ��� �� Client�� ���� **/
	private static void loginflushBuffer(List<LoginDTO> result_list) {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// �������Ʈ�� ���̸�ŭ  flush
			for (int i = 0; i < result_list.size(); i++) {
				LoginDTO login = result_list.get(i);
				bufferedWriter.write(login.getUser_id() + " ");

				bufferedWriter.write(login.getUser_pw() + " ");

				bufferedWriter.newLine(); // readLine()���� �����Ƿ� ���ٳ��� �˸�
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** ProductDTO ��ȯ��� �� Client�� ���� **/
	private static void flushBuffer(List<ProductDTO> result_list) {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// �������Ʈ�� ���̸�ŭ  flush
			for (int i = 0; i < result_list.size(); i++) {
				ProductDTO prod = result_list.get(i);
				bufferedWriter.write(prod.getProd_code() + " ");

				bufferedWriter.write(prod.getProd_name() + " ");

				bufferedWriter.write(prod.getProd_price() + " ");

				bufferedWriter.write(prod.getProd_quantity() + " ");

				bufferedWriter.write(prod.getProd_weight() + " ");
				bufferedWriter.newLine(); // readLine()���� �����Ƿ� ���ٳ��� �˸�
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}