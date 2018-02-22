import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class ThreadPool {
	public static void main(String argc[]) {
		System.out.println("메인스레드 시작...");
		ServerExam server = new ServerExam();
		
		ExecutorService execService = Executors.newFixedThreadPool(10); 
		
		execService.execute(new MyThreadTask());
		execService.execute(new MyThreadTask());
		
	
		execService.shutdown();
		
		System.out.println("메인스레드 종료...");
	}
}

class MyThreadTask implements Runnable {	
	private static int count = 0;
	private int id;
	@Override
	public void run(){
		for(int i = 0; i<5; i++) {
			System.out.println("<" + id + ">TICK TICK " + i);
			try {
				TimeUnit.MICROSECONDS.sleep((long)Math.random()*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public MyThreadTask() {
		this.id = ++count;
	}
}