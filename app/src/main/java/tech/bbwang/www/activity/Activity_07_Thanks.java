package tech.bbwang.www.activity;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import tech.bbwang.www.R;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;

public class Activity_07_Thanks extends SuperActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_07_thanks);
		new Thread() {
			public void run() {
				try {
					CountDown1(5);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}.start();
		super.onCreate(savedInstanceState);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
//		getMenuInflater().inflate(R.menu.thanks, menu);
		return true;
	}
	
	private int limitSec = 5;
	private int curSec;
	ScheduledExecutorService exec = null;

	public void CountDown1(int limitSec) throws InterruptedException {
		this.limitSec = limitSec;
		this.curSec = limitSec;

		exec = Executors.newScheduledThreadPool(1);
		exec.scheduleAtFixedRate(new Task(), 0, 1, TimeUnit.SECONDS);
		TimeUnit.SECONDS.sleep(this.limitSec); // 暂停本线程
		exec.shutdownNow();

		Intent intent = new Intent();
		intent.setClass(Activity_07_Thanks.this, Activity_02_Welcome.class);
		this.startActivity(intent);
		
	}

	private class Task implements Runnable {
		public void run() {
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					curSec = curSec - 1;
				}
			});
		}
	}

}
