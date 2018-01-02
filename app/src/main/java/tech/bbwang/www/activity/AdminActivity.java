package tech.bbwang.www.activity;

import tech.bbwang.www.R;
import tech.bbwang.www.sqlite.DBAd;
import tech.bbwang.www.sqlite.DBMenu;
import tech.bbwang.www.util.ConfigFileUtil;
import tech.bbwang.www.util.DateUtil;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.ws.ActiveInfo;
import tech.bbwang.www.ws.WSUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

public class AdminActivity extends Activity {

	
	final ConfigFileUtil configFile = ColetApplication.getApp().getConfigFile();
	
	private void showActiveInfo(){
		String activeInfo = "";
		switch( configFile.getActiveStatus()){
		case 0:
		case 1:activeInfo="已激活";
		break;
		case 2:activeInfo="已冻结,请联系管理员";break;
		case 3:activeInfo="已停用,请联系管理员";break;
		case 4:activeInfo="非法编号,请联系管理员";break;
		case 999:activeInfo="未激活";break;
		}
		((TextView)this.findViewById(R.id.textActiveStatus)).setText(activeInfo);
		((TextView)this.findViewById(R.id.textFranchise)).setText(configFile.getFranchiseId());
	}
	
	private void showHeartBeatInfo(){
		((TextView) this.findViewById(R.id.textHeartBeat)).setText(configFile.getHeartbeatPluse()+" 分钟");
	}
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_admin);
		showActiveInfo();
		((Button) this.findViewById(R.id.button1)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				Intent intent = new Intent(AdminActivity.this, Activity_02_Welcome.class);
				startActivity(intent);
				AdminActivity.this.finish();
			}
		});

		((Button) this.findViewById(R.id.setHeartBeat)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				final LinearLayout mulayout = new LinearLayout(AdminActivity.this);
				mulayout.setOrientation(LinearLayout.VERTICAL);
				final EditText inputServer = new EditText(AdminActivity.this);
				final TextView info = new TextView(AdminActivity.this);
				info.setText("单位（分钟）");
				mulayout.addView(info);
				mulayout.addView(inputServer);
				
		        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
		        builder.setTitle("请输入新的心跳时间").setIcon(android.R.drawable.ic_dialog_info).setView(mulayout)
		                .setNegativeButton("取消", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
			            String heartbeat = inputServer.getText().toString();
			            if( heartbeat.length() > 0 ){
			            	ColetApplication.getApp().getConfigFile().setHeartbeatPluse(heartbeat);
			            	showHeartBeatInfo();
			            	AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminActivity.this);
					        builder2.setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("心跳时间设定成功").setPositiveButton("确定",null);
					        builder2.show();
			            }

		            }
		        });
		        builder.show();
		        
	
			}
		});
//		if(configFile.getActiveStatus()==999){
//			activeBtn.setEnabled(true);
//		}else{
//			activeBtn.setEnabled(false);
//		}
		
		((Button) this.findViewById(R.id.active)).setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View arg0) {
				/*status为： 
				   0：合法编号，激活成功。
				   1：已被激活，拒绝激活。
				   2：已被冻结，拒绝激活。
				   3：已被停用，拒绝激活。
				   4：非法编号，拒绝激活。
				   */
				final LinearLayout mulayout = new LinearLayout(AdminActivity.this);
				mulayout.setOrientation(LinearLayout.VERTICAL);
				final EditText inputServer = new EditText(AdminActivity.this);
				final TextView info = new TextView(AdminActivity.this);
				info.setText("注意：终端编号需要事先在运营商云后台终端管理处登记才能激活！");
				mulayout.addView(inputServer);
				mulayout.addView(info);
				
		        AlertDialog.Builder builder = new AlertDialog.Builder(AdminActivity.this);
		        builder.setTitle("请输入运营商编号").setIcon(android.R.drawable.ic_dialog_info).setView(mulayout)
		                .setNegativeButton("取消", null);
		        builder.setPositiveButton("确定", new DialogInterface.OnClickListener() {
		            public void onClick(DialogInterface dialog, int which) {
			            String franchiseId = inputServer.getText().toString();
			            if( franchiseId.length() > 0 ){
				   			ActiveInfo activeinfo = WSUtil.getInstance().registTerminal( franchiseId, SystemUtil.getIMEI(AdminActivity.this), 0);
							if(activeinfo.getStatus() < 0 ){
								AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminActivity.this);
						        builder2.setTitle("错误").setIcon(android.R.drawable.ic_dialog_alert).setMessage(activeinfo.getMessage()).setPositiveButton("确定",null);
						        builder2.show();
							}else{
					   			configFile.setActiveStatus(activeinfo.getStatus());
								configFile.setFranchiseId(franchiseId);
								showActiveInfo();
								String message = "";
								switch(activeinfo.getStatus()){
								case 0:message="终端激活成功！";break;
								case 1:message="终端激活确认成功！";break;
								case 2:message="终端已被冻结，无法激活，请联系运营商！";break;
								case 3:message="终端已被停用，无法激活，请联系运营商！";break;
								case 4:message="不是有效的终端编号，无法激活，请联系运营商！";break;
								}
								AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminActivity.this);
						        builder2.setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage(message).setPositiveButton("确定",null);
						        builder2.show();
							}

			            }

		            }
		        });
		        builder.show();
		        
	
			}
		});

		((Button) this.findViewById(R.id.exitApp)).setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				
				AlertDialog.Builder builder2 = new AlertDialog.Builder(AdminActivity.this);
		        builder2.setTitle("提示").setIcon(android.R.drawable.ic_dialog_info).setMessage("您确定要退出程序吗？").setPositiveButton("确定", new DialogInterface.OnClickListener() {

					@Override
					public void onClick(DialogInterface dialog,int which) {
						//android.os.Process.killProcess(android.os.Process.myPid()); // 获取PID
						//System.exit(0);
//						finish();
						int currentVersion = android.os.Build.VERSION.SDK_INT; 
			            if (currentVersion > android.os.Build.VERSION_CODES.ECLAIR_MR1) { 
			                Intent startMain = new Intent(Intent.ACTION_MAIN); 
			                startMain.addCategory(Intent.CATEGORY_HOME); 
			                startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK); 
			                startActivity(startMain); 
			                System.exit(0); }else{
			                	ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);     
			                	manager.killBackgroundProcesses(getPackageName()); 
			                }

					}
				}).setNegativeButton("取消", null);
		        builder2.show();

			}
		});
			
		
		
		
		
		
		((TextView) this.findViewById(R.id.textMacUUID)).setText(SystemUtil.getIMEI(this));
		((TextView) this.findViewById(R.id.textlabelAppVersion)).setText(SystemUtil.getVersionName(this));
		
		DBAd ad = (DBAd) ColetApplication.getApp().getAddao().getLastAd();
		DBMenu menu = (DBMenu) ColetApplication.getApp().getMdao().getLastMenu();
//		((TextView) this.findViewById(R.id.textLocalADVersion)).setText(ad.getAd_version());

		((TextView) this.findViewById(R.id.textHeartBeat)).setText(configFile.getHeartbeatPluse()+" 分钟");
		((TextView) this.findViewById(R.id.textApiServer)).setText(configFile.getApiUrl());
		
//		if (ad.getCreateTime() == null) {
//			((TextView) this.findViewById(R.id.textLocalADVersionUpdateTime)).setText("无");
//			((TextView) this.findViewById(R.id.textLocalADName)).setText("无");
//		} else {
//			((TextView) this.findViewById(R.id.textLocalADVersionUpdateTime)).setText(DateUtil.sdf_yyyy_MM_dd_HH_mm_ss.format(ad.getCreateTime()));
//			((TextView) this.findViewById(R.id.textLocalADName)).setText(ad.getAd_name());
//		}

		((TextView) this.findViewById(R.id.textLocalMenuVersion)).setText(menu.getMenu_version());
		if (menu.getCreateTime() == null) {
			((TextView) this.findViewById(R.id.textLocalMemuVersionUpdateTime)).setText("无");
			((TextView) this.findViewById(R.id.textLocalMemuName)).setText("无");
		} else {
			((TextView) this.findViewById(R.id.textLocalMemuVersionUpdateTime))
					.setText(DateUtil.sdf_yyyy_MM_dd_HH_mm_ss.format(menu.getCreateTime()));
			((TextView) this.findViewById(R.id.textLocalMemuName)).setText(menu.getMenu_name());
		}

//		if (ad.getAd_data_list() == null) {
//			((TextView) this.findViewById(R.id.textLocalADNumer)).setText("0.0.1");
//		} else {
//			((TextView) this.findViewById(R.id.textLocalADNumer)).setText(String.valueOf(ad.getAd_data_list().size()));
//		}

		if (menu.getGood_data_list() == null) {
			((TextView) this.findViewById(R.id.textLocalMenuNumer)).setText("4");
		} else {
			((TextView) this.findViewById(R.id.textLocalMenuNumer)).setText(String.valueOf(menu.getGood_data_list().size()));
		}
		
//		((TextView)this.findViewById(R.id.textADSwitchTime)).setText(configFile.getADSwitchTime()+"秒");
		//SystemUtil.startAnimationForAd(configFile.getAnimAdId())
//		((TextView)this.findViewById(R.id.textADSwitchEffect)).setText(configFile.getADSwitchEffect());
		((TextView)this.findViewById(R.id.textalipayServer)).setText(configFile.getAliPayUrl());
		((TextView)this.findViewById(R.id.textWechatpayServer)).setText(configFile.getWechatPayUrl());
	}

}
