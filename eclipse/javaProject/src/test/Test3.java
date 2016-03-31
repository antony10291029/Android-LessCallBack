package test;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock.ReadLock;

import javax.swing.JOptionPane;

import test.TaskFlowUtils.TaskFlow;

public class Test3 {

	TaskFlowUtils taskTools = TaskFlowUtils.inject(this)
			.setAutoIncrement(false);

	@TaskFlow(thread = true, id = 0)
	public void doTask1() {
		/** 模拟android */
		new Thread() {
			@Override
			public void run() {
				int n = JOptionPane.showConfirmDialog(null, "你高兴吗?", "标题",
						JOptionPane.YES_NO_OPTION);
				if (n == 0) {
					taskTools.setStepByID(1);
				} else if (n == 1) {
					taskTools.setStepByID(2);
				} else {
					taskTools.setStepByID(3);
				}
				taskTools.wakeUp();
			}
		}.start();
		taskTools.waitFor();

	}

	@TaskFlow(thread = false, id = 1)
	public void doTask2() {
		System.out.println(Thread.currentThread().getName() + " 我很高兴");
		taskTools.setStepByID(3);
		taskTools.setAutoIncrement(true);
	}

	@TaskFlow(thread = true, id = 2)
	public void doTask3() {
		System.out.println(Thread.currentThread().getName() + " 我很不高兴");
		taskTools.setStepByID(3);
		taskTools.setAutoIncrement(true);
	}

	@TaskFlow(thread = true, id = 3)
	public void doTask4() {
		System.out.println(Thread.currentThread().getName() + "退出");
	}

	public static void main(String[] args) {
		new Test3().taskTools.run();
	}
}
