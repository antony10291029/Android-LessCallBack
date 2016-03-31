package test;

import java.io.File;
import java.io.FileFilter;
import java.util.Random;

import test.Task.Listen;

public class Test1 {

	/**
	 * @param args
	 */
	// 这种层层嵌套的代码给开发带来了很多问题，主要体现在：
	// 1.代码可能性变差
	// 2.调试困难
	// 3.出现异常后难以排查
	public static void main(String[] args) {
		// http://www.jb51.net/article/55598.htm
		new WaitTimeTask("任务1").setListen(new Listen() {

			@Override
			public void finsh(Task t) {
				System.out.println("任务1完成");
				new WaitTimeTask("任务2").setListen(new Listen() {

					@Override
					public void finsh(Task t) {
						System.out.println("任务2完成");
					}
				}).start();
			}
		}).start();

		

	}

	
	
	
}

class WaitTimeTask extends Task {

	public WaitTimeTask(String name) {
		// TODO Auto-generated constructor stub
		this.name = name;
	}

	@Override
	protected void dotask() {
		// TODO Auto-generated method stub
		// .................do anything
		System.out.println(this.getName() + "正在执行一个任务");
		int x = new Random().nextInt(1000) + 500;
		try {
			Thread.sleep(x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("等待了" + x + "毫秒");

		// .................do anything
	}

}

abstract class Task implements Runnable {

	public void start() {
		new Thread(this).start();
	}

	public interface Listen {
		void finsh(Task t);
	}

	public String name;
	public Listen listen;

	@Override
	public void run() {
		// TODO Auto-generated method stub
		// .................do anything
		dotask();
		// 回调
		if (listen != null)
			listen.finsh(this);
	}

	protected abstract void dotask();

	public Listen getListen() {
		return listen;
	}

	public Task setListen(Listen listen) {
		this.listen = listen;
		return this;
	}

	public String getName() {
		return name;
	}

	public Task setName(String name) {
		this.name = name;
		return this;
	}

}
