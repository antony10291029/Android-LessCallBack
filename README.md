<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	我们知道单层的回调非常容易阅读和维护。
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	在单层的回调可读性还是非常强的。
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	一旦进入多层嵌套，代码就会变成巨大的庞然大物，满眼都是{和}，分散我们编写程序的注意力。
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	<br />
	
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	我们可以通过注解分解来解嵌套
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	例如以下代码
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
</p>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_1_3182537" name="code" class="java">new WaitTimeTask(&quot;任务1&quot;).setListen(new Listen() {

			@Override
			public void finsh(Task t) {
				System.out.println(&quot;任务1完成&quot;);
				new WaitTimeTask(&quot;任务2&quot;).setListen(new Listen() {

					@Override
					public void finsh(Task t) {
						System.out.println(&quot;任务2完成&quot;);
					}
				}).start();
			}
		}).start();</pre>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_5_8678223" name="code" class="java">执行效果</pre>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_3_3978141" name="code" class="java">任务1正在执行一个任务
等待了1370毫秒
任务1完成
任务2正在执行一个任务
等待了1102毫秒
任务2完成</pre>
<br />
实际我们可以用类似goto的方式来简化这种结构例如
<p>
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
</p>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_4_771775" name="code" class="java">public class Test2 {
	@TaskFlow(thread = true, id = 0)
	public void doTask1() {
		System.out.println(Thread.currentThread().getName()+&quot;:&quot;+&quot;任务1&quot;);
		waitTime();
	}
	@TaskFlow(thread = false, id = 1)
	public void doTask1Finish() {
		System.out.println(Thread.currentThread().getName()+&quot;:&quot;+&quot;任务1执行结束&quot;);
	}
	@TaskFlow(thread = true, id = 2)
	public void doTask2() {
		System.out.println(Thread.currentThread().getName()+&quot;:&quot;+&quot;任务2&quot;);
		waitTime();
	}
	@TaskFlow(thread = false, id = 3)
	public void doTask2Finish() {
		System.out.println(Thread.currentThread().getName()+&quot;:&quot;+&quot;任务2执行结束&quot;);
	}
	public static void main(String[] args) {
		TaskFlowUtils.inject(new Test2()).run();;
	}
	public static void waitTime(){
		int x = new Random().nextInt(1000) + 500;
		try {
			System.out.println(&quot;等待&quot;+x+&quot;毫秒&quot;);
			Thread.sleep(x);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}</pre>
<br />

<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_5_8678223" name="code" class="java">执行效果</pre>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_6_2440215" name="code" class="java">线程:任务1
等待515毫秒
ui线程:任务1执行结束
线程:任务2
等待655毫秒
ui线程:任务2执行结束
</pre>
<br />
现在有一个简单的功能通过请求http获得本机IP地址
<p>
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	再通过本机IP去查所在城市
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
</p>
<pre code_snippet_id="1630449" snippet_file_name="blog_20160331_7_7282001" name="code" class="java">public class HttpActivity extends AppCompatActivity {

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
            tv.setText(&quot;&quot;);
            TaskFlowTools.inject(this).run();
        }
        String ip;
        String city;

        @TaskFlow(thread = true, id = 0)
        public void sendIP() {
            String json = new HttpRequest().sendGet(&quot;http://pv.sohu.com/cityjson&quot;, &quot;ie=utf-8&quot;);
            ip = JsonPathGeneric.getGenericInString(json.split(&quot;=&quot;)[1], &quot;$.cip&quot;);
            append(&quot;http://pv.sohu.com/cityjson?ie=utf-8&quot;);
            append(json);
        }

        @TaskFlow(thread = false, id = 1)
        public void getIP() {
            tv.append(Html.fromHtml(&quot;&lt;font color='red'&gt;&quot;+&quot;\n&quot;+&quot;ip:&quot;+ip+&quot;\n&quot;+&quot;&lt;/font&gt;&quot;));
        }

        @TaskFlow(thread = true, id = 2)
        public void sendCity() {
            String json = new HttpRequest().sendGet(&quot;http://int.dpool.sina.com.cn/iplookup/iplookup.php&quot;, &quot;format=js&amp;ip=&quot; + ip);
            city = JsonPathGeneric.getGenericInString(json.split(&quot;=&quot;)[1], &quot;$.city&quot;);
            append(&quot;http://int.dpool.sina.com.cn/iplookup/iplookup.php?format=js&amp;ip=&quot; + ip);
            append(json);
        }

        @TaskFlow(thread = false, id = 3)
        public void getCity() {
            tv.append(Html.fromHtml(&quot;&lt;font color='red'&gt;&quot;+&quot;\n&quot;+&quot;city:&quot;+city+&quot;\n&quot;+&quot;&lt;/font&gt;&quot;));
        }

        public void append(final String str) {
            tv.post(new Runnable() {
                @Override
                public void run() {
                    tv.append(&quot;\n&quot;+str+&quot;\n&quot;);
                }
            });
        }
    };


}
</pre>
<br />
运行后的效果
<p>
</p>
<p style="margin-top:0px; margin-bottom:0px; padding-top:0px; padding-bottom:0px; font-family:Arial; font-size:14px; line-height:26px">
	<img src="http://img.blog.csdn.net/20160331152120898?watermark/2/text/aHR0cDovL2Jsb2cuY3Nkbi5uZXQv/font/5a6L5L2T/fontsize/400/fill/I0JBQkFCMA==/dissolve/70/gravity/Center" alt="" /><br />
	
</p>
