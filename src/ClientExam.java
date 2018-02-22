
/*------------------------------------------------------------------
[ClientExam JAVA]

Project    : LDCC_POS
Version : 1.0
Last change : 2018/02/22
Developer : Nara Shin
-------------------------------------------------------------------*/
/*------------------------------------------------------------------
 [Table of contents]

 1.  public void login() : �α��� �Լ�
 2.  public void draw_menu() : ���θ޴� �Լ�
 3.  public void func_sell() : ���� ������ �Լ�
 4.  public void func_refund() : ȯ�� ������ �Լ�
 5.  public void func_check() : ������ ������ �Լ�
 6.  public void setTableForm(BufferedReader bufferedReader) : ���̺� ������ �Լ�
     - ����������/ȯ������������ ȣ��
 7.  public void setBarcode(String flag, String code, String quantity) : ������ ���� ���ڵ� �� ���� write
 8.  public void open() throws Exception, IOException : Socket, bufferedWriter, bufferedReader ����
 9.  public void close() : Socket, bufferedWriter, bufferedReader �ݱ�
 10. class Keyin : ���� input
 -------------------------------------------------------------------*/
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.Socket;
import java.util.Scanner;
import java.util.StringTokenizer;

public class ClientExam {
	// �������� ����
	private static ClientExam Cli;
	private static String flag;

	private static Socket socket;
	private BufferedReader bufferedReader;
	private BufferedWriter bufferedWriter;

	private static String IP = "127.0.0.1"; // IP
	private static int PORT_NUM = 6060; // PORT��ȣ

	Scanner sc = new Scanner(System.in);

	// �����Լ�
	public static void main(String[] args) {
		Cli = new ClientExam();

		// �α���
		Cli.login();
	}

	/** �α��� �Լ� **/
	public void login() {
		System.out.println("\n============================");
		System.out.println("|     POS �α��� ������    |");
		System.out.println("============================");
		String[] login = new String[3];
		String[] login_result = new String[3];
		Cli = new ClientExam();

		// �α��� ������ ������ ���ѹݺ�
		while (true) {
			try {
				open();

				// ������ login���� ���� �˸��� flag
				login[0] = "login";

				// ID, PW �Է�
				System.out.print("ID : ");
				login[1] = sc.nextLine();
				System.out.print("PW : ");
				login[2] = sc.nextLine();

				// ������ �Է� �� ����
				bufferedWriter.write(login[0]);
				bufferedWriter.newLine();
				bufferedWriter.write(login[1]);
				bufferedWriter.newLine();
				bufferedWriter.write(login[2]);
				bufferedWriter.newLine();
				bufferedWriter.flush();

				// �����κ��� �� ����
				String line = "";
				int i = 0;
				try {
					while ((line = bufferedReader.readLine()) != null) {
						StringTokenizer st = new StringTokenizer(line);
						// ���̻� ���ڰ� ������ ���� �ݺ�
						while (st.hasMoreTokens()) {
							if (i == 0) {
								// id
								login_result[i] = st.nextToken();
								i++;
							} else {
								// pw
								login_result[i] = st.nextToken();
								break;
							}
						}
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				// �Է��� ID��, �������� �� DB�� ID�� �����ϰ� && PW�� ������ ��� �α��� ����
				if (login[1].equals(login_result[0]) && login[2].equals(login_result[1])) {
					System.out.println("�ڷα��� ������\n");
					Cli.draw_menu();
				} else {
					// ID�� �ٸ��ų�, PW�� �ٸ����
					// ID�� �������� �ʴ� ���
					System.out.println("�ٷα��� ���С�\n ���̵� Ȥ�� ��й�ȣ�� �ٽ� Ȯ�����ּ���.\n");
				}

				close();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

	}

	/** ���θ޴� �Լ� >> �޴� ������ �� ���� �޴��� �Լ� ���� **/
	public void draw_menu() {
		int swValue;
		Cli = new ClientExam();

		while (true) {
			// Display menu graphics
			System.out.println("\n============================");
			System.out.println("|      POS ���� ������     |");
			System.out.println("============================");
			System.out.println("| Options:                 |");
			System.out.println("|        1. ����           |");
			System.out.println("|        2. ȯ�� �� ��ȯ   |");
			System.out.println("|        3. ������       |");
			System.out.println("|        4. ����           |");
			System.out.println("============================");
			swValue = Keyin.inInt(" �� �޴� ���� : ");

			switch (swValue) {
			case 1: // ����
				Cli.func_sell();
				continue;
			case 2: // ȯ�� �� ��ȯ
				Cli.func_refund();
				continue;
			case 3: // ������
				Cli.func_check();
				continue;
			case 4: // ����
				System.out.println("�����α׷� �����");
				String[] prod = new String[1];
				try {
					open();

					// ������ ���� flag ����
					prod[0] = "exit";

					bufferedWriter.write(prod[0]);
					bufferedWriter.newLine();
					bufferedWriter.flush();

					close();

				} catch (Exception e) {
					e.printStackTrace();
				}

				// ���α׷� ����
				System.exit(0);
				break;
			default:
				System.out.println("�޴��� ��ȣ�� �������ּ���");
				continue; // This break is not really necessary
			}
			break;
		}
	}

	/** ���� ������ �Լ� **/
	public void func_sell() {
		int swValue;
		while (true) {
			System.out.println("\n============================");
			System.out.println("|     POS ���� ������      |");
			System.out.println("============================");
			System.out.println("| Options:                 |");
			System.out.println("|        1. ���ڵ��Է�/����|");
			System.out.println("|        2. �ڷΰ���       |");
			System.out.println("============================");
			swValue = Keyin.inInt(" �� �޴� ���� : ");

			switch (swValue) {
			case 1:
				System.out.println("�ڹ��ڵ� �Է��ϱ��");
				int str;
				int quitWord = 0;
				int[] prod = new int[2];
				

				try {
					open();
					System.out.print("�ذ��� �� 0 �Է�\n");
					// ���ڵ� ��� ��� �����ս� ( ���� ���� ������ �Է� )
					while (true) {
						// ������ �Ǹ� flag ����
						flag = "sell";
						prod[0] = Keyin.inInt(" ���ڵ� ��ȣ : ");
						
						str = prod[0];
						if (str==quitWord) {
							flag = "fin";
							bufferedWriter.write(flag);
							bufferedWriter.newLine();
							break;
						}
						
						prod[1] = Keyin.inInt(" ���� : ");

						// flag, ���ڵ� ��ȣ, ���� ���� bufferedWriter�� write
						setBarcode(flag, prod[0], prod[1]);
					}

					bufferedWriter.flush();

					// ������ �׸� ���̺�� ���
					System.out.print("\n�ڰ��� ����Ʈ��\n");
					setTableForm(bufferedReader);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			case 2:
				System.out.println("�ڵڷ� �����");
				break;
			default:
				System.out.println("�޴��� ��ȣ�� �������ּ���");
				continue; // This break is not really necessary
			}
			break;
		}

	}

	/** ȯ�� ������ �Լ� **/
	public void func_refund() {
		int swValue;

		while (true) {
			System.out.println("\n============================");
			System.out.println("|     POS ȯ�� ������      |");
			System.out.println("============================");
			System.out.println("| Options:                 |");
			System.out.println("|        1. ȯ���ϱ�       |");
			System.out.println("|        2. ��ȯ�ϱ�       |");
			System.out.println("|        3. �ڷΰ���       |");
			System.out.println("============================");
			swValue = Keyin.inInt(" �� �޴� ���� : ");

			switch (swValue) {
			case 1:
				System.out.println("��ȯ���ϱ��");
				int[] prod = new int[2];
				try {
					open();

					// ������ ȯ�� flag ����
					flag = "refund";
					prod[0] = Keyin.inInt(" ���ڵ� ��ȣ  : ");
					prod[1] = Keyin.inInt(" ����  : ");

					// flag, ���ڵ� ��ȣ, ���� ���� bufferedWriter�� write
					setBarcode(flag, prod[0], prod[1]);
					bufferedWriter.flush();

					// �������� �޽��� �Է¹���
					String serverMessage = bufferedReader.readLine();

					// �Է¹��� ������ ���� �ֿܼ� ���
					System.out.println("ȯ�Ұ�� : " + serverMessage);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			case 2:
				// ȯ�� �� �����ϴ� ����
				System.out.println("�ڱ�ȯ�ϱ��");
				int[] change_prod = new int[2];
				try {
					open();

					// ������ ȯ�� flag ����
					flag = "refund";
					change_prod[0] = Keyin.inInt("��ǰ�� ��ǰ ���ڵ� ��ȣ  : ");
					change_prod[1] = Keyin.inInt("����  : ");

					// flag, ���ڵ� ��ȣ, ���� ���� bufferedWriter�� write
					setBarcode(flag, change_prod[0], change_prod[1]);
					bufferedWriter.flush();

					close();

					// ���� Ŀ�ؼ��� ���� ���� delay
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}

					open();
					// ������ ���� flag ����
					flag = "sell";
					change_prod[0] = Keyin.inInt("��ȯ�� ��ǰ ���ڵ� ��ȣ  : ");
					change_prod[1] = Keyin.inInt("����  : ");

					// flag, ���ڵ� ��ȣ, ���� ���� bufferedWriter�� write
					setBarcode(flag, change_prod[0], change_prod[1]);

					// ���� ������ �ٸ���, �ϳ��� ���� �����ϹǷ�
					// �ϳ� �Է� �� ������ fin�÷��� ����
					flag = "fin";
					bufferedWriter.write(flag);
					bufferedWriter.newLine();

					bufferedWriter.flush();

					// ȯ�� �Ϸ� �� ����Ʈ ���
					System.out.print("\n��ȯ�� ����Ʈ��\n");
					setTableForm(bufferedReader);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			case 3:
				System.out.println("�ڵڷ� �����");
				break;
			default:
				System.out.println("�޴��� ��ȣ�� �������ּ���");
				continue; // This break is not really necessary
			}
			break;
		}
	}

	/** ������ ������ �Լ� **/
	public void func_check() {
		int swValue;

		while (true) {
			System.out.println("\n============================");
			System.out.println("|    POS ������ ������   |");
			System.out.println("============================");
			System.out.println("| Options:                 |");
			System.out.println("|        1. ��ü ǰ�� ��ȸ |");
			System.out.println("|        2. Ư�� ǰ�� ��ȸ |");
			System.out.println("|        3. �ڷΰ���       |");
			System.out.println("============================");
			swValue = Keyin.inInt(" �� �޴� ���� : ");

			switch (swValue) {
			case 1:
				System.out.println("����ü ǰ�� ��ȸ��");
				try {
					open();

					// ������ ���Ȯ�� flag ����
					flag = "check";
					bufferedWriter.write(flag);
					bufferedWriter.newLine();
					bufferedWriter.flush();

					// Response�� ���� ��� ���̺� �ε�
					setTableForm(bufferedReader);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			case 2:
				System.out.println("��Ư�� ǰ�� ��ȸ��");
				int bar_num;
				
				try {
					open();

					// ������ Ư�� ��ǰ ���Ȯ�� flag ����
					flag = "bar_check";
					bar_num = Keyin.inInt("���ڵ� ��ȣ : ");

					bufferedWriter.write(flag);
					bufferedWriter.newLine();
					bufferedWriter.write(String.valueOf(bar_num));
					bufferedWriter.newLine();
					bufferedWriter.flush();

					// Response�� ���� ��� ���̺� �ε�
					setTableForm(bufferedReader);
					close();
				} catch (Exception e) {
					e.printStackTrace();
				}
				continue;
			case 3:
				System.out.println("�ڵڷ� �����");
				break;
			default:
				System.out.println("�޴��� ��ȣ�� �������ּ���");
				continue; // This break is not really necessary
			}
			break;
		}
	}

	/** ���̺� ������ �Լ� **/
	public void setTableForm(BufferedReader bufferedReader) {
		// ��� ��
		System.out.format("------------------------------------------------------%n");
		System.out.println("���ڵ�" + "\t\t" + "ǰ��" + "\t\t" + "����" + "\t" + "�߷�" + "\t" + "����");
		System.out.format("------------------------------------------------------%n");

		String line = "";
		String[] prod_list = new String[5];
		int i = 0;
		try {
			while ((line = bufferedReader.readLine()) != null) {
				StringTokenizer st = new StringTokenizer(line);
				while (st.hasMoreTokens()) { // ���̻� ���ڰ� ������ ���� �ݺ�

					if (i == 0) {
						// code
						prod_list[i] = st.nextToken();
						i++;
					} else if (i == 1) {
						// name
						prod_list[i] = st.nextToken();
						i++;
					} else if (i == 2) {
						// price
						prod_list[i] = st.nextToken();
						i++;
					} else if (i == 3) {
						// weight
						prod_list[i] = st.nextToken();
						i++;
					} else if (i == 4) {
						// quantity
						prod_list[i] = st.nextToken();
						System.out.println(prod_list[0] + "\t\t" + prod_list[1] + "\t\t" + prod_list[2] + "\t"
								+ prod_list[3] + "\t" + prod_list[4]);
						i = 0;
						break;
					}
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		System.out.format("------------------------------------------------------%n");
	}

	/** ������ ���� ���ڵ� �� ���� write **/
	public void setBarcode(String flag, int code, int quantity) {
		try {
			bufferedWriter.write(flag);
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(code));
			bufferedWriter.newLine();
			bufferedWriter.write(String.valueOf(quantity));
			bufferedWriter.newLine();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** Socket, bufferedWriter, bufferedReader ���� **/
	public void open() throws Exception, IOException {
		socket = new Socket(IP, PORT_NUM);
		bufferedWriter = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
		bufferedReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
	}

	/** Socket, bufferedWriter, bufferedReader �ݱ� **/
	public void close() {
		try {
			bufferedWriter.close();
			bufferedReader.close();
			socket.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

// Ű���� �Է� ó��
class Keyin {
	// *******************************
	// support methods
	// *******************************
	// Method to display the user's prompt string
	public static void printPrompt(String prompt) {
		System.out.print(prompt + " ");
		System.out.flush();
	}

	// Method to make sure no data is available in the
	// input stream
	public static void inputFlush() {
		int dummy;

		try {
			while ((System.in.available()) != 0)
				dummy = System.in.read();
		} catch (java.io.IOException e) {
			System.out.println("Input error");
		}
	}

	// ********************************
	// data input methods for
	// string, int, char, and double
	// ********************************
	public static String inString(String prompt) {
		inputFlush();
		printPrompt(prompt);
		return inString();
	}

	public static String inString() {
		int aChar;
		String s = "";
		boolean finished = false;

		while (!finished) {
			try {
				aChar = System.in.read();
				if (aChar < 0 || (char) aChar == '\n')
					finished = true;
				else if ((char) aChar != '\r')
					s = s + (char) aChar; // Enter into string
			}

			catch (java.io.IOException e) {
				System.out.println("Input error");
				finished = true;
			}
		}
		return s;
	}

	public static int inInt(String prompt) {
		while (true) {
			inputFlush();
			printPrompt(prompt);
			try {
				return Integer.valueOf(inString().trim()).intValue();
			}

			catch (NumberFormatException e) {
				System.out.println("Invalid input. Not an integer");
			}
		}
	}

	public static char inChar(String prompt) {
		int aChar = 0;

		inputFlush();
		printPrompt(prompt);

		try {
			aChar = System.in.read();
		}

		catch (java.io.IOException e) {
			System.out.println("Input error");
		}
		inputFlush();
		return (char) aChar;
	}

	public static double inDouble(String prompt) {
		while (true) {
			inputFlush();
			printPrompt(prompt);
			try {
				return Double.valueOf(inString().trim()).doubleValue();
			}

			catch (NumberFormatException e) {
				System.out.println("Invalid input. Not a floating point number");
			}
		}
	}
}