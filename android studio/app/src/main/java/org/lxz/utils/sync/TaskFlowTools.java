package org.lxz.utils.sync;

import android.os.Handler;
import android.os.Looper;

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

public class TaskFlowTools implements Runnable {

    @Retention(RetentionPolicy.RUNTIME)
    @Target(ElementType.METHOD)
    public static @interface TaskFlow {
        boolean thread() default false;

        int id();
    }

    private TaskFlowTools() {

    }

    public Object obj;

    public class TaskFunc {
        Object object;
        TaskFlow flow;
        Method method;
    }

    boolean isAutoIncrement = true;
    boolean isNextStep = true;
    OnTaskFlowExceptionListen onTaskFlowExceptionListen;
    private Handler handler = new Handler();

    public TaskFlowTools setAutoIncrement(boolean auto) {
        isAutoIncrement = auto;
        return this;
    }

    public boolean isAutoIncrement() {
        return isAutoIncrement;
    }

    public List<TaskFunc> list = new ArrayList<TaskFunc>();

    public static TaskFlowTools inject(Object obj) {
        TaskFlowTools task = new TaskFlowTools();
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
                TaskFunc func = new TaskFunc();
                func.flow = flow;
                func.method = m;
                func.object = obj;
                list.add(func);
            }
        }
        System.out.println("-->size:" + list.size());
        Collections.sort(list, new Comparator<TaskFunc>() {

            @Override
            public int compare(TaskFunc o1, TaskFunc o2) {
                // TODO Auto-generated method stub
                return o1.flow.id() - o2.flow.id();
            }

        });
        flow_index = 0;
        execute();
    }

    private int flow_index = 0;

    private TaskFunc func = null;

    private synchronized void execute() {
        if (flow_index < list.size()) {
            if (isAutoIncrement) {
                func = list.get(flow_index++);
            } else {
                func = list.get(flow_index);
            }
            System.out.println("flow_index:"+flow_index+"  flowid-->"+func.flow.id());
            if (func.flow.thread()) {
                Thread thread = new Thread(new Runnable() {

                    @Override
                    public void run() {

                        try {
                            func.method.invoke(func.object);
                            if (isNextStep) {
                                execute();
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                            if (onTaskFlowExceptionListen != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onTaskFlowExceptionListen.onException(func, e)) {
                                            execute();
                                        }
                                    }
                                });
                            }
                        }

                    }
                });
                thread.start();
            } else {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            func.method.invoke(func.object);
                            if (isNextStep) {
                                execute();
                            }
                        } catch (final Exception e) {
                            e.printStackTrace();
                            if (onTaskFlowExceptionListen != null) {
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if (onTaskFlowExceptionListen.onException(func, e)) {
                                            execute();
                                        }
                                    }
                                });
                            }
                        }

                    }
                });

            }
        }
    }

    public boolean isNextStep() {
        return isNextStep;
    }

    public TaskFlowTools setNextStep(boolean isNextStep) {
        this.isNextStep = isNextStep;
        return this;
    }

    public TaskFlowTools nextStep() {
        setNextStep(true);
        execute();
        return this;
    }

    ReentrantLock lock;
    Condition cond;

    public void wakeUp() {
        if (lock == null) {
            throw new RuntimeException("You must first call waitFor");
        }
        lock.lock();
        cond.signal();
        lock.unlock();
    }


    public void waitFor() {
        if(Thread.currentThread() == Looper.getMainLooper().getThread())
        {
            throw new RuntimeException("Can no longer wait for the main thread, can only be in the thread.");
        }
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


    public interface OnTaskFlowExceptionListen {
        boolean onException(TaskFunc func, Exception e);
    }

    public OnTaskFlowExceptionListen getOnTaskFlowExceptionListen() {
        return onTaskFlowExceptionListen;
    }

    public TaskFlowTools setOnTaskFlowExceptionListen(OnTaskFlowExceptionListen onTaskFlowExceptionListen) {
        this.onTaskFlowExceptionListen = onTaskFlowExceptionListen;
        return this;
    }
}
