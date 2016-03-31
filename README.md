我们知道单层的回调非常容易阅读和维护。
在单层的回调可读性还是非常强的。
一旦进入多层嵌套，代码就会变成巨大的庞然大物，满眼都是{和}，分散我们编写程序的注意力。

我们可以通过注解分解来解嵌套
例如以下代码
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
执行效果
任务1正在执行一个任务
等待了1370毫秒
任务1完成
任务2正在执行一个任务
等待了1102毫秒
任务2完成

实际我们可以用类似goto的方式来简化这种结构例如
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

执行效果
线程:任务1
等待515毫秒
ui线程:任务1执行结束
线程:任务2
等待655毫秒
ui线程:任务2执行结束

现在有一个简单的功能通过请求http获得本机IP地址
再通过本机IP去查所在城市
public class HttpActivity extends AppCompatActivity {

    private TextView tv;
    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tv = (TextView) findViewById(R.id.text);
        btn = (Button) findViewById(R.id.btn);
        btn.setOnClickListener(http);
    }

    View.OnClickListener http = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            ip = null;
            city = null;
            tv.setText("");
            TaskFlowTools.inject(this).run();
        }
        String ip;
        String city;

        @TaskFlow(thread = true, id = 0)
        public void sendIP() {
            String json = new HttpRequest().sendGet("http://pv.sohu.com/cityjson", "ie=utf-8");
            ip = JsonPathGeneric.getGenericInString(json.split("=")[1], "$.cip");
            append("http://pv.sohu.com/cityjson?ie=utf-8");
            append(json);
        }

        @TaskFlow(thread = false, id = 1)
        public void getIP() {
            tv.append(Html.fromHtml("<font color='red'>"+"\n"+"ip:"+ip+"\n"+"</font>"));
        }

        @TaskFlow(thread = true, id = 2)
        public void sendCity() {
            String json = new HttpRequest().sendGet("http://int.dpool.sina.com.cn/iplookup/iplookup.php", "format=js&ip=" + ip);
            city = JsonPathGeneric.getGenericInString(json.split("=")[1], "$.city");
            append("http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&ip=" + ip);
            append(json);
        }

        @TaskFlow(thread = false, id = 3)
        public void getCity() {
            tv.append(Html.fromHtml("<font color='red'>"+"\n"+"city:"+city+"\n"+"</font>"));
        }

        public void append(final String str) {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.append("\n"+str+"\n");
                }
            });
        }
    };


}

运行后的效果

