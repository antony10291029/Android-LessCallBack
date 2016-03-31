package test;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class TaskFlowUtils implements Runnable {

	@Retention(RetentionPolicy.RUNTIME)
	// 注解会在class中存在，运行时可通过反射获取
	@Target(ElementType.METHOD)
	public static @interface TaskFlow {
		boolean thread();

		int id();
	}

	private TaskFlowUtils() {

	}

	public Object obj;

	public class MapFunc {
		Object object;
		TaskFlow flow;
		Method method;
	}

	boolean isAutoIncrement = true;

	public TaskFlowUtils setAutoIncrement(boolean auto) {
		isAutoIncrement = auto;
		return this;
	}

	public boolean isAutoIncrement() {
		return isAutoIncrement;
	}

	public List<MapFunc> list = new ArrayList<MapFunc>();

	public static TaskFlowUtils inject(Object obj) {
		TaskFlowUtils task = new TaskFlowUtils();
		task.obj = obj;
		return task;
	}

	public void run() {
		worker(obj);
	}

	public void worker(Object obj) {
		for (Method m : obj.getClass().getMethods()) {
			TaskFlow flow = m.getAnnotation(TaskFlow.class);
			if (flow != null) {
				MapFunc func = new MapFunc();
				func.flow = flow;
				func.method = m;
				func.object = obj;
				list.add(func);
			}
		}
		Collections.sort(list, new Comparator<MapFunc>() {

			@Override
			public int compare(MapFunc o1, MapFunc o2) {
				// TODO Auto-generated method stub
				return o1.flow.id() - o2.flow.id();
			}

		});
		flow_index = 0;
		execute();
	}

	private int flow_index = 0;

	private MapFunc func=null;
	private void execute() {
		if (flow_index < list.size()) {
			if (isAutoIncrement) {
				func = list.get(flow_index++);
			} else {
				func = list.get(flow_index);
			}
			if (func.flow.thread()) {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						try {
							func.method.invoke(func.object);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						execute();

					}
				});
				thread.setName("线程");
				thread.start();
			} else {
				Thread thread = new Thread(new Runnable() {

					@Override
					public void run() {

						try {
							func.method.invoke(func.object);
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						execute();

					}
				});
				thread.setName("ui线程");
				thread.start();
			}
		}
	}

	/** 用于唤醒 */
	ReentrantLock lock;
	Condition cond;

	public void wakeUp() {
		if (lock == null) {
			lock = new ReentrantLock();
			cond = lock.newCondition();
		}
		lock.lock();
		cond.signal();
		lock.unlock();
	}

	/** 用于唤醒 */
	public void waitFor() {
		if (lock == null) {
			lock = new ReentrantLock();
			cond = lock.newCondition();
		}
		lock.lock();
		try {
			cond.await();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		lock.unlock();
	}

	public void setStepByID(int id) {
		boolean b = false;
		for (int i = 0, j = list.size(); i < j; i++) {
			if (list.get(i).flow.id() == id) {
				flow_index = i;
				b = true;
				break;
			}
		}
		if (b == false) {
			throw new RuntimeException("task flow id " + id + " is no found");
		}
	}
}
