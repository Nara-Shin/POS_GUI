
/*------------------------------------------------------------------
[ServerExam JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1. main함수 안 : 키워드(login/sell&fin/refund/check/bar_check/exit)를 통해 클라이언트 요구사항 처리
 2. private static void LoginLogic(String id, String pw) : 로그인 로직
 3. private static void SellProduct(String code, int quantity) : 결제 로직
 4. private static void UpdateProduct(String code, int quantity) : 환불 로직
 5. private static void CheckProduct() : 재고 확인 로직
 6. private static void CheckBarProduct(String code) : 바코드로 재고 확인 로직
 7. private static void flushBuffer(List<ProductDTO> result_list) : ProductDTO 반환결과 값 Client로 전달
 8. private static void loginflushBuffer(List<LoginDTO> result_list) : LoginDTO 반환결과 값 Client로 전달
 
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

	// corePoolSize(실행할 최소 Thread수.), maximumPoolSize: 10(최대 Thread 지원수)
	// corePoolSize 의 모든 thread가 Busy 상태인 경우에, 새로운 태스크는 큐내에서 대기
	// corePoolSize 를 넘는 thread는 작성되지 않음

	static ThreadPoolExecutor threadPool = new ThreadPoolExecutor(1, 10, 60, TimeUnit.SECONDS,
			new LinkedBlockingQueue());
	static final int PORT_NUM = 6060;

	// 서버 소켓 및 소켓 선언
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
							// 서버소켓 선언
							serverSocket = new ServerSocket(PORT_NUM);
							System.out.println("\n클라이언트 접속 대기 중...");
							
							// 서버소켓으로부터 소켓 객체 가져오기
							socket = serverSocket.accept();
							bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
							bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
							System.out.println(socket.getInetAddress() + "가 접속되었습니다.");

							// 클라이언트로부터 메시지 입력받음
							String clientMessage = bufferedReader.readLine();

							// 입력받은 내용을 서버 콘솔에 출력
							System.out.println("클라이언트가 보내온 내용 : " + clientMessage);
							
							// 클라이언트에서 보낸 flag를 통한 각각의 로직 실행
							/** 로그인 로직 **/
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

								// id와 pw를 통한  로그인 로직 실행
								LoginLogic(login[1], login[2]);
								
								/** 결제 로직 **/
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

								// 입력한 제품 수만큼 loop
								for (int j = 0; j < prod_num; j++) {
									// 결제로직 실행
									SellProduct(code_arr[j], Integer.parseInt(qunatity_arr[j]));
								}
								/** 환불 로직 **/
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

								// code 와 quantity를 통한 환불 로직
								UpdateProduct(prod[1], Integer.parseInt(prod[2]));
								
								/** 재고조회 로직 **/
							} else if (clientMessage.equals("check")) {
								// 재고조회 로직 실행 
								CheckProduct();
								
								/** 특정 상품 재고조회 로직 **/
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

								// 특정 상품 재고 조회
								CheckBarProduct(prod[1]);
								
								/** 종료 로직 **/
							} else if (clientMessage.equals("exit")) {
								System.out.println("\n접속 종료...");
								bufferedReader.close();
								serverSocket.close();
								socket.close();// 접속 종료
								break;
							}
							serverSocket.close();
							socket.close();// 접속 종료
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

	/** 로그인 로직 -- DAO에 ID,PW 전달 후 SELECT 값 전달받음 >> 클라이언트로 전달 **/
	private static void LoginLogic(String id, String pw) {
		List<LoginDTO> result_list = new ArrayList<LoginDTO>();
		LoginDAO dao = new LoginDAO();
		LoginDTO dto = new LoginDTO();

		// DTO에 클라이언트에서 인풋한 ID, PW set
		String user_id = id;
		String user_pw = pw;
		dto.setUser_id(user_id);
		dto.setUser_pw(user_pw);

		// 결과 리스트에 DAO에서 반환한 결과값 받음
		result_list = dao.loginTry(dto);

		// 결과 값 클라이언트로 전송
		loginflushBuffer(result_list);

	}

	/** 결제 로직 --> DAO에 바코드, 수량 전달 후 판매리스트 전달받음 >> 클라이언트로 전달 **/
	private static void SellProduct(String code, int quantity) {
		dto = new ProductDTO();
		dao = new ProductDAO();
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		String prod_code = code;
		int prod_quantity = quantity;
		
		// DTO에 클라이언트에서 인풋한 code, quantity set
		dto.setProd_code(prod_code);
		dto.setProd_quantity(prod_quantity);
		
		// 결과 리스트에 DAO에서 반환한 결과값 받음
		result_list = dao.sellProductList(dto);
		
		// 결과 값 클라이언트로 전송
		flushBuffer(result_list);
		boolean ok = dao.sellProduct(dto);
		if (ok) {
			System.out.println("결제 성공");
		} else {
			System.out.println("결제 실패");
		}
	}

	/** 환불 로직 --> DAO에 바코드, 수량 전달 후 환불 여부(T/F) 전달받음 >> 클라이언트로 전달 **/
	private static void UpdateProduct(String code, int quantity) throws Exception {
		dto = new ProductDTO();
		dao = new ProductDAO();
		
		String prod_code = code;
		int prod_quantity = quantity;
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		
		// DTO에 클라이언트에서 인풋한 code, quantity set
		dto.setProd_code(prod_code);
		dto.setProd_quantity(prod_quantity);

		// DAO에서 반환한 결과값 받음
		boolean ok = dao.updateProduct(dto);

		if (ok) { // true
			System.out.println("환불 성공");
			bufferedWriter.write("환불 성공");
			bufferedWriter.newLine(); // readLine()으로 읽으므로 한줄끝을 알림
			bufferedWriter.flush();

		} else { // false
			System.out.println("환불 실패");
			bufferedWriter.write("환불 실패");
			bufferedWriter.newLine(); // readLine()으로 읽으므로 한줄끝을 알림
			bufferedWriter.flush();
		}
	}

	/** 재고 확인 로직 --> 전체 재고 조회 **/
	private static void CheckProduct() {
		 dao = new ProductDAO();
		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		// 결과 리스트에 DAO에서 반환한 결과값 받음
		result_list = dao.getProductList();
		
		// 결과 값 클라이언트로 전송
		flushBuffer(result_list);

	}

	/** 바코드로 재고 확인 로직 --> 입력 바코드 상품 재고 조회 **/
	private static void CheckBarProduct(String code) throws IOException {
		dto = new ProductDTO();
		 dao = new ProductDAO();
		String prod_code = code;

		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));

		List<ProductDTO> result_list = new ArrayList<ProductDTO>();
		
		// DTO에 클라이언트에서 인풋한 code set
		dto.setProd_code(prod_code);

		// 결과 리스트에 DAO에서 반환한 결과값 받음
		result_list = dao.getProductCheck(dto);

		// 결과 값 클라이언트로 전송
		flushBuffer(result_list);

	}
	
	/** LoginDTO 반환결과 값 Client로 전달 **/
	private static void loginflushBuffer(List<LoginDTO> result_list) {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// 결과리스트의 길이만큼  flush
			for (int i = 0; i < result_list.size(); i++) {
				LoginDTO login = result_list.get(i);
				bufferedWriter.write(login.getUser_id() + " ");

				bufferedWriter.write(login.getUser_pw() + " ");

				bufferedWriter.newLine(); // readLine()으로 읽으므로 한줄끝을 알림
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** ProductDTO 반환결과 값 Client로 전달 **/
	private static void flushBuffer(List<ProductDTO> result_list) {
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
			bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
			
			// 결과리스트의 길이만큼  flush
			for (int i = 0; i < result_list.size(); i++) {
				ProductDTO prod = result_list.get(i);
				bufferedWriter.write(prod.getProd_code() + " ");

				bufferedWriter.write(prod.getProd_name() + " ");

				bufferedWriter.write(prod.getProd_price() + " ");

				bufferedWriter.write(prod.getProd_quantity() + " ");

				bufferedWriter.write(prod.getProd_weight() + " ");
				bufferedWriter.newLine(); // readLine()으로 읽으므로 한줄끝을 알림
			}
			bufferedWriter.flush();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}