package test;
import java.util.Random;

import test.TaskFlowUtils.TaskFlow;

public class Test2 {
	@TaskFlow(thread = true, id = 0)
	public void doTask1() {
		System.out.println(Thread.currentThread().getName()+":"+"任务1");
		waitTime();
	}
	@TaskFlow(thread = false, id = 1)
	public void doTask1Finish() {
		System.out.println(Thread.currentThread().getName()+":"+"任务1执行结束");
	}
	@TaskFlow(thread = true, id = 2)
	public void doTask2() {
		System.out.println(Thread.currentThread().getName()+":"+"任务2");
		waitTime();
	}
	@TaskFlow(thread = false, id = 3)
	public void doTask2Finish() {
		System.out.println(Thread.currentThread().getName()+":"+"任务2执行结束");
	}
	public static void main(String[] args) {
		TaskFlowUtils.inject(new Test2()).run();;
	}
	public static void waitTime(){
		int x = new Random().nextInt(1000) + 500;
		try {
			System.out.println("等待"+x+"毫秒");
			Thread.sleep(x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
