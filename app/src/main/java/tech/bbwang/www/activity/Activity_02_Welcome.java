package tech.bbwang.www.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import tech.bbwang.www.R;
import tech.bbwang.www.downloader.DownloadUtil;
import tech.bbwang.www.sqlite.DBGood;
import tech.bbwang.www.sqlite.DBMenu;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.UiEffectUtil;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.lid.lib.LabelImageView;
import com.lid.lib.LabelViewHelper;

public class Activity_02_Welcome extends SuperActivity implements
		OnClickListener, OnTouchListener{

	View main;
	// 品类按钮
	private List<ImageView> buttons = new ArrayList<ImageView>();

	private LinearLayout firstLine = null;
	private LinearLayout secondLine = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {

		main = getLayoutInflater().inflate(R.layout.activity_02_welcome, null);
		main.setSystemUiVisibility(View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
		// main.setOnClickListener(this);
		setContentView(main);
		super.onCreate(savedInstanceState);

		firstLine = (LinearLayout) this.findViewById(R.id.firstLine);
		secondLine = (LinearLayout) this.findViewById(R.id.secondLine);

		// 更新菜单按钮
		updateMenuCategory();

	}

	/**
	 * 更新菜单
	 */
	public void updateMenuCategory() {

		 final DBMenu newMU = (DBMenu) ColetApplication.getApp().getMdao() .getLastMenu();
		 
		 if( newMU.getGood_data_list().size() > 0 ){
			 loadLaestMenu(newMU);
		 }else{
			 loadDefaultMenu();
		 }

		
	}
	
	
	// 品类图片资源
	private static final int[] defaultMenuImageId = { R.drawable.mskf_off,
			R.drawable.kbjn_off, R.drawable.nt_off, R.drawable.nm_off,
			R.drawable.yskf_off, R.drawable.rmtj_off };
	private static final String[] defaultMenuNameArray = { "美式咖啡", "卡布奇诺", "拿铁",
			"热奶沫", "热牛奶" };

	// 自动替换的菜单id起始值
	public static final int AUTO_REPLACE_INDEX_START = 1000000;
	// 储值卡图片资源id
	private static final int THZQ = R.drawable.thzq_off;
	//储值卡图片组建id
	private static final int THZQ_ID = 10000010;
	
	String[] coffeeImageArray = null;
	String[] coffeeNameArray = null;
	String[] coffeePriceArray = null;
	int[]    coffeeTypeArray = null;
	
	private void loadSingleMenun(int i,String path,String version,DBGood good){

		// 添加图片
		coffeeImageArray[i] = path +File.separator +version+String.valueOf(good.getSequenceNo())+"."+SystemUtil.getSuffix(good.getImage());
		coffeeNameArray[i] = good.getName();
		coffeePriceArray[i] = String.format("%.2f", Double.valueOf(String.valueOf(good.getPromPrice()))/100);
		coffeeTypeArray[i] = getCoffeeType(coffeeNameArray[i]);
		
		LabelImageView laeblImageView = new LabelImageView(Activity_02_Welcome.this);
		laeblImageView.setId((AUTO_REPLACE_INDEX_START + i));
		if(ColetApplication.menuImage.containsKey(coffeeImageArray[i])){
			laeblImageView.setImageBitmap(ColetApplication.menuImage.get(coffeeImageArray[i]));
		}else{
			ColetApplication.getApp().logDebug("准备读取图片:"+coffeeImageArray[i]);
			Bitmap bitmap = SystemUtil.getLoacalBitmap(coffeeImageArray[i]);
			laeblImageView.setImageBitmap(bitmap);
			ColetApplication.menuImage.put(coffeeImageArray[i], bitmap);
		}
		LayoutParams lp = new LayoutParams(200, 200);
		if (i <= 5) {
			lp.setMargins(0, 0, 60, 0);
		}
		laeblImageView.setLayoutParams(lp);
		laeblImageView.setScaleType(ScaleType.FIT_CENTER);
		laeblImageView.setLabelBackgroundColor(Color.RED);
		laeblImageView.setLabelDistance(30);
		laeblImageView.setLabelHeight(40);
		laeblImageView.setLabelOrientation(LabelViewHelper.RIGHT_TOP);
		laeblImageView.setLabelText(coffeePriceArray[i]+"元");
		laeblImageView.setLabelTextSize(25);
		laeblImageView.setLabelTextColor(Color.WHITE);
		laeblImageView.setOnTouchListener(Activity_02_Welcome.this);
		laeblImageView.setOnClickListener(Activity_02_Welcome.this);
		if( i<=2 ){
			firstLine.addView(laeblImageView);
		}else{
			secondLine.addView(laeblImageView);
		}
		buttons.add(laeblImageView);
	}
	
	private void loadChuZhiKaMenu(LinearLayout second){
		//添加储值卡(原提货码位置)
		ImageView laeblImageView = new ImageView(
				Activity_02_Welcome.this);
		laeblImageView.setId(THZQ_ID);
		laeblImageView.setBackgroundResource(THZQ);
		LayoutParams lp = new LayoutParams(200, 200);
		lp.setMargins(0, 0, 60, 0);
		laeblImageView.setLayoutParams(lp);
		laeblImageView.setScaleType(ScaleType.CENTER_INSIDE);
		laeblImageView.setOnTouchListener(Activity_02_Welcome.this);
		laeblImageView.setOnClickListener(Activity_02_Welcome.this);
		second.addView(laeblImageView);
		buttons.add(laeblImageView);
	}
	
	/**
	 * 加载下载下来的菜单
	 */
	private void loadLaestMenu(final DBMenu menu){
		
		buttons.clear();
		firstLine.removeAllViews();
		secondLine.removeAllViews();
		
		int tmpSize = menu.getGood_list().size();
		
		coffeeImageArray = new String[tmpSize+1];
		coffeeNameArray = new String[tmpSize+1];
		coffeePriceArray = new String[tmpSize+1];
		coffeeTypeArray = new int[tmpSize+1];
		
		final String path = DownloadUtil.getInnerSDCrad() + File.separator + "menu" + File.separator + menu.getMenu_version();
		
		if( menu.getGood_list().size() <=2 ){
			firstLine.post((new Runnable() {
				public void run() {
					for (int i = 0; i < menu.getGood_list().size(); i++) {
						loadSingleMenun(i,path,menu.getMenu_version(),menu.getGood_list().get(i));
					}
					loadChuZhiKaMenu(firstLine);
				}
			}));
		}else if( menu.getGood_list().size() <=5 ){

			firstLine.post((new Runnable() {
				public void run() {
					for (int i = 0; i < 3; i++) {
						loadSingleMenun(i,path,menu.getMenu_version(),menu.getGood_list().get(i));
					}
				}
			}));
		
			secondLine.post((new Runnable() {
				public void run() {
					// 添加品类菜单按钮
					for (int i = 3; i < menu.getGood_list().size(); i++) {
						loadSingleMenun(i,path,menu.getMenu_version(),menu.getGood_list().get(i));
					}
					loadChuZhiKaMenu(secondLine);
				}
			}));
		}else{//size > 5
			firstLine.post((new Runnable() {
				public void run() {
					for (int i = 0; i < 3; i++) {
						loadSingleMenun(i,path,menu.getMenu_version(),menu.getGood_list().get(i));
					}
				}
			}));
		
			secondLine.post((new Runnable() {
				public void run() {
					// 添加品类菜单按钮
					for (int i = 3; i < 5; i++) {
						loadSingleMenun(i,path,menu.getMenu_version(),menu.getGood_list().get(i));
					}
					loadChuZhiKaMenu(secondLine);
				}
			}));
		}
	}
	
	
	/**
	 * 加载默认的菜单
	 */
	private void loadDefaultMenu(){
		buttons.clear();
		firstLine.removeAllViews();
		secondLine.removeAllViews();
		
		coffeeImageArray = new String[5];
		coffeeNameArray = new String[5];
		coffeePriceArray = new String[5];
		coffeeTypeArray = new int[5];
		
		firstLine.post((new Runnable() {
			public void run() {
				
				for (int i = 0; i < 3; i++) {
					// 添加图片
					LabelImageView laeblImageView = new LabelImageView(
							Activity_02_Welcome.this);
					laeblImageView.setId((AUTO_REPLACE_INDEX_START + i));
					laeblImageView.setBackgroundResource(defaultMenuImageId[i]);
					LayoutParams lp = new LayoutParams(200, 200);
					if (i != 2) {
						lp.setMargins(0, 0, 60, 0);
					}
					laeblImageView.setLayoutParams(lp);
					laeblImageView.setScaleType(ScaleType.CENTER_INSIDE);
					laeblImageView.setLabelBackgroundColor(Color.RED);
					laeblImageView.setLabelDistance(30);
					laeblImageView.setLabelHeight(40);
					laeblImageView
							.setLabelOrientation(LabelViewHelper.RIGHT_TOP);
					laeblImageView.setLabelText("0.01元");
					laeblImageView.setLabelTextSize(25);
					laeblImageView.setLabelTextColor(Color.WHITE);
					laeblImageView.setOnTouchListener(Activity_02_Welcome.this);
					laeblImageView.setOnClickListener(Activity_02_Welcome.this);
					firstLine.addView(laeblImageView);
					buttons.add(laeblImageView);
					
					//coffeeImageArray[i] = defaultMenuImageId[i];
					coffeeNameArray[i] = defaultMenuNameArray[i];
					coffeePriceArray[i] = "0.01";
					coffeeTypeArray[i] = getCoffeeType(coffeeNameArray[i]);
				}
			}
		}));

		secondLine.post((new Runnable() {
			public void run() {
				// 添加品类菜单按钮
				for (int i = 3; i < 5; i++) {
					// 添加图片
					LabelImageView laeblImageView = new LabelImageView(
							Activity_02_Welcome.this);
					laeblImageView.setId((AUTO_REPLACE_INDEX_START + i));
					laeblImageView.setBackgroundResource(defaultMenuImageId[i]);
					LayoutParams lp = new LayoutParams(200, 200);
					if (i != 5) {
						lp.setMargins(0, 0, 60, 0);
					}
					laeblImageView.setLayoutParams(lp);
					laeblImageView.setScaleType(ScaleType.CENTER_INSIDE);
					laeblImageView.setLabelBackgroundColor(Color.RED);
					laeblImageView.setLabelDistance(30);
					laeblImageView.setLabelHeight(40);
					laeblImageView
							.setLabelOrientation(LabelViewHelper.RIGHT_TOP);
					laeblImageView.setLabelText("0.01元");
					laeblImageView.setLabelTextSize(25);
					laeblImageView.setLabelTextColor(Color.WHITE);
					laeblImageView.setOnTouchListener(Activity_02_Welcome.this);
					laeblImageView.setOnClickListener(Activity_02_Welcome.this);
					secondLine.addView(laeblImageView);
					buttons.add(laeblImageView);
					
					coffeeNameArray[i] = defaultMenuNameArray[i];
					coffeePriceArray[i] = "0.01";
					coffeeTypeArray[i] = getCoffeeType(coffeeNameArray[i]);

				}
				//添加储值卡(原提货码位置)
				ImageView laeblImageView = new ImageView(
						Activity_02_Welcome.this);
				laeblImageView.setId(THZQ_ID);
				laeblImageView.setBackgroundResource(THZQ);
				LayoutParams lp = new LayoutParams(200, 200);
				lp.setMargins(0, 0, 60, 0);
				laeblImageView.setLayoutParams(lp);
				laeblImageView.setScaleType(ScaleType.CENTER_INSIDE);
				laeblImageView.setOnTouchListener(Activity_02_Welcome.this);
				laeblImageView.setOnClickListener(Activity_02_Welcome.this);
				secondLine.addView(laeblImageView);
				buttons.add(laeblImageView);

			}
		}));
	}

	/**
	 * 根据咖啡名称获取咖啡类型
	 * @param coffeeName
	 * @return
	 */
	private int getCoffeeType(String coffeeName) {

		int ret = CashlessConstants.COFFEE_TYPE_HOT_AMERACAN;
		if (coffeeName.contains("美式")) {
			if (coffeeName.contains("冰")) {
				ret = CashlessConstants.COFFEE_TYPE_ICE_AMERACAN;
			} else {
				ret = CashlessConstants.COFFEE_TYPE_HOT_AMERACAN;
			}
		} else if (coffeeName.contains("卡")) {
			ret = CashlessConstants.COFFEE_TYPE_CAPPUCCION;
		} else if (coffeeName.contains("拿铁")) {
			if (coffeeName.contains("冰")) {
				ret = CashlessConstants.COFFEE_TYPE_ICE_LATTE;
			} else {
				ret = CashlessConstants.COFFEE_TYPE_HOT_LATTE;
			}
		} else if (coffeeName.contains("意式")) {
			ret = CashlessConstants.COFFEE_TYPE_ITALY;
		}else if(coffeeName.contains("热牛奶")){
			ret = CashlessConstants.COFFEE_TYPE_MILK;
		}else if(coffeeName.contains("奶沫")){
			ret = CashlessConstants.COFFEE_TYPE_MILKRATE;
		}

		return ret;
	}

	@Override
	public void onClick(View arg0) {

		//判断是商品组件
		if (arg0 instanceof LabelImageView) {
			
			int id = arg0.getId()-AUTO_REPLACE_INDEX_START;
			LabelImageView tmp = (LabelImageView) arg0;
			Intent intent = new Intent();
			intent.setClass(Activity_02_Welcome.this, Activity_04_Pay.class);
			Bundle data = new Bundle();
			data.putString("subject", coffeeNameArray[id]);
			data.putString("price", coffeePriceArray[id]);
			data.putInt("coffeeType",coffeeTypeArray[id]);
			if( coffeeImageArray[id] == null || coffeeImageArray[id].length() == 0 ){
				data.putInt("imageId", defaultMenuImageId[id]);
			}else{
				data.putString("imagePath", coffeeImageArray[id]);
			}
			// data.putInt("coffeeSize", dialog.getCupSize());
			intent.putExtras(data);
			startActivity(intent);
		} else {
			switch (arg0.getId()) {
			//储值卡
			case THZQ_ID:
//				AlertDialog.Builder builder = new AlertDialog.Builder(
//						Activity_02_Welcome.this);
//				builder.setTitle("该功能将改成储值卡销售及充值").setPositiveButton("确定",
//						new DialogInterface.OnClickListener() {
//							public void onClick(DialogInterface dialog,
//									int which) {
//							}
//						});
//				builder.show();
				Intent intent = new Intent();
				intent.setClass(Activity_02_Welcome.this, Activity_10_vipcard2.class);
				startActivity(intent);
				break;
			default:
				break;
			}
		}

	}

	@Override
	protected void onStart() {
		super.onStart();
	}

	@Override
	protected void onStop() {
		super.onStop();
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		return UiEffectUtil.onTouch(v, event);
	}

}
