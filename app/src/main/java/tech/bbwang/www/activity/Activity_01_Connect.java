package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;

public class Activity_01_Connect extends SuperActivity implements OnClickListener {

	private static TextView coffe_machine_init_status = null;
	private static Activity_01_Connect myself = null;
	public static Handler welcomeHandler = new Handler() {

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);

			switch (msg.what) {
			case 1:// 自检完成无故障正常启动
					// coffe_machine_init_status.setText("开机自检中完成");
					// try {
					// Thread.sleep(2000);
					// } catch (InterruptedException e) {
					// e.printStackTrace();
					// }
				((ColetApplication) myself.getApplication()).stopSendOK();
				Intent intent = new Intent();
				intent.setClass(myself, Activity_02_Welcome.class);
				myself.startActivity(intent);
				break;
			case 2:// 正常自检
				String progress = msg.getData().getString("progress");
				progress = String.format(myself.getResources().getString(R.string.message_init_check), progress);
				coffe_machine_init_status.setText(progress);
				break;
			case 3:// 自检有故障
				break;
			default:
				break;

			}

		}

	};

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		main = getLayoutInflater().inflate(R.layout.activity_01_connect, null);
		main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		main.setOnClickListener(this);
		setContentView(main);
		coffe_machine_init_status = (TextView) this.findViewById(R.id.coffe_machine_init_status);
		myself = this;

		if (checkActiveStatus() == true) {
			((ColetApplication) myself.getApplication()).startSendOK();
		} else {
			new AlertDialog.Builder(Activity_01_Connect.this).setTitle("系统提示")
			// 设置对话框标题
					.setMessage("终端尚未激活或因程序再安装丢失，请先前往设置激活终端")
					// 设置显示的内容
					.setPositiveButton("确定", new DialogInterface.OnClickListener() {
						@Override
						public void onClick(DialogInterface dialog, int which) {
							Intent intent = new Intent();
							intent.setClass(Activity_01_Connect.this, AdminActivity.class);
							Activity_01_Connect.this.startActivity(intent);
						}

					}).show();

		}
		super.onCreate(savedInstanceState);
	}

	private boolean checkActiveStatus() {
		boolean ret = false;
		int activeStatus = (ColetApplication.getApp().getConfigFile().getActiveStatus());
		switch (activeStatus) {
		case 0:
			// 0：合法编号，激活成功。
		case 1:
			// 1：已被激活，拒绝激活。
			ret = true;
			break;
		case 2:
			// 2：已被冻结，拒绝激活。
		case 3:
			// 3：已被停用，拒绝激活。
		case 4:
			// 4：非法编号，拒绝激活。
		case 999:
			ret = false;
			break;
		}

		return ret;
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		// getMenuInflater().inflate(R.menu.connect, menu);
		return true;
	}

}
