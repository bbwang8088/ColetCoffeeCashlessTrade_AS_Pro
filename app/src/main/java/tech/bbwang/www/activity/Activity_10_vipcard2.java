package tech.bbwang.www.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import tech.bbwang.www.R;
import tech.bbwang.www.sqlite.PrePayCard;
import tech.bbwang.www.sqlite.PrePayCardDAO;
import tech.bbwang.www.sqlite.PrePayCardList;
import tech.bbwang.www.util.CashlessConstants;
import tech.bbwang.www.util.SystemUtil;
import tech.bbwang.www.util.UiEffectUtil;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class Activity_10_vipcard2 extends SuperActivity implements
		OnItemClickListener, OnTouchListener,OnClickListener {

	private static final int CARD_NUM = 6;

	private String[] defaultTitle = { "储值卡", "储值卡", "储值卡", "储值卡", "储值卡", "储值卡" };
	private String[] defaultListPrice = { "2", "3", "4", "5", "6", "8" };
	private String[] defaultFreshrice = { "1", "1", "1", "2", "2", "3" };
	private String[] defaultVipPrice = { "1", "2", "2", "3", "4", "4" };

	private int[] card_title_array = new int[CARD_NUM];
	private int[] freshprice_array = new int[CARD_NUM];
	private int[] vipprice_array = new int[CARD_NUM];
	private int[] listprice_array = new int[CARD_NUM];
	private int[] card_array = new int[CARD_NUM];

	private static long VIP_COUNT_TIME = 30;
//	 静止状态时倒计时
	private long countTime = VIP_COUNT_TIME;
//	 是否关闭倒计时
	private boolean stopCountTimer = false;
	private boolean isShowDefaultCards = true;
	
	private Handler handler = new Handler(Looper.getMainLooper()) {
		public void handleMessage(android.os.Message msg) {
			Intent intent = new Intent();
			switch (msg.what) {
			case 0:
				if( stopCountTimer == true ){
					break;
				}
				// 移除所有的msg.what为0等消息，保证只有一个循环消息队列再跑
				handler.removeMessages(0);
				// 倒计时结束
				if (countTime == 0) {
					stopCountTimer();
					if (CashlessConstants.debug) {
						ColetApplication.getApp().logDebug("Activity_10_vipcard,倒计时结束，画面跳回Activity_02_Welcome");
					}
					// 跳转回广告欢迎页面
					intent.setClass(Activity_10_vipcard2.this, Activity_02_Welcome.class);
					Activity_10_vipcard2.this.startActivity(intent);
				} else {
					if (CashlessConstants.debug) {
						ColetApplication.getApp().logDebug( "Activity_10_vipcard,倒计时=" + countTime);
					}
					countTime = countTime - 1;
					handler.sendEmptyMessageDelayed(0, 1000);
				}
				break;
			default:
				break;
			}
		}
	};
/**
	 * 重置倒计时
	 */
	private void resetCountTimer() {
		if (CashlessConstants.debug) {
			ColetApplication.getApp().logDebug("Activity_10_vipcard:倒计时被重置");
		}
		countTime = VIP_COUNT_TIME;
		stopCountTimer = false;
	}

	/**
	 * 关闭倒计时
	 */
	private void stopCountTimer() {
		if (CashlessConstants.debug) {
			ColetApplication.getApp().logDebug("Activity_10_vipcard:倒计时被关闭");
		}
		countTime = -1;
		stopCountTimer = true;
	}

	/**
	 * 开启倒计时
	 */
	private void restartCountTimer() {
		if (CashlessConstants.debug) {
			ColetApplication.getApp().logDebug("Activity_10_vipcard:倒计时被重启");
		}
		countTime = VIP_COUNT_TIME;
		stopCountTimer = false;
		handler.sendEmptyMessageDelayed(0, 1000);
	}
	private GridView gv = null;

	private int realCardsSize = 0;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		setContentView(R.layout.activity_10_vipcard2);
		gv = (GridView) findViewById(R.id.gridview);
		((ImageView)this.findViewById(R.id.button_cancel_trade_for_card)).setOnClickListener(this);
		initComponents();
		super.onCreate(savedInstanceState);
		handler.sendEmptyMessageDelayed(0, 1000);
	}
	
	private void initComponents() {

		((ImageView)this.findViewById(R.id.button_cancel_trade_for_card)).setOnClickListener(this);
		((LinearLayout)this.findViewById(R.id.layout_cancel_trade_for_card)).setOnClickListener(this);
		if(loadDBCards() == false){
			loadDefaultCards(defaultTitle.length);
		}else{
			loadDefaultCards(realCardsSize);
		}
	}
	
	private boolean loadDBCards(){
		boolean isHaveCards = false;
		Map<String, String> params = new HashMap<String,String>();
		params.put(PrePayCardDAO.ELEM_VERSION_CODE, ColetApplication.getApp().getConfigFile().getVipCardListCode());
		PrePayCardList cards = (PrePayCardList) ColetApplication.getApp().getprePaycardDao().get(params);
		if( cards.getPrePayCardList().size() == 0 ){
			return isHaveCards;
		}
		realCardsSize = 0;
		for(PrePayCard card : cards.getPrePayCardList()){
			defaultFreshrice[realCardsSize] = card.getFresh_price();
			defaultListPrice[realCardsSize] = card.getList_price();
			defaultVipPrice[realCardsSize]  = card.getVip_price();
			
			String ListP= SystemUtil.txfloat(Float.valueOf(defaultListPrice[realCardsSize]) / 100.00f);
			String freshP = SystemUtil.txfloat(Float.valueOf(Float.valueOf(defaultListPrice[realCardsSize])-Float.valueOf(defaultFreshrice[realCardsSize])) / 100.00f);
			String vipP = SystemUtil.txfloat(Float.valueOf(defaultVipPrice[realCardsSize]) / 100.00f);
			
//			initCard(i, defaultTitle[i], "新手价:¥"+freshP, "会员价:¥"+vipP, "¥"+ListP, CARD_TYPE.BLUE);
			realCardsSize++;
		}
		isHaveCards = true;
//		isShowDefaultCards = true;
		return isHaveCards;
	}

	private void loadDefaultCards(int size) {
		List<HashMap<String, Object>> gridItemList = new ArrayList<HashMap<String, Object>>();
		for (int i = 0; i < size; i++) {
			HashMap<String, Object> gridItem = new HashMap<String, Object>();
			
			String ListP= SystemUtil.txfloat(Float.valueOf(defaultListPrice[i]) / 100.00f);
			String freshP = SystemUtil.txfloat((Float.valueOf(defaultListPrice[i])-Float.valueOf(defaultFreshrice[i])) / 100.00f);
			String vipP = SystemUtil.txfloat(Float.valueOf(defaultVipPrice[i]) / 100.00f);
			
			gridItem.put("itemImage", R.drawable.logo);
			gridItem.put("card_title", defaultTitle[i]);
			gridItem.put("listPrice", ListP);
			gridItem.put("freshPrice",freshP);
			gridItem.put("vipPrice", vipP);
			gridItemList.add(gridItem);
		}

		SimpleAdapter simpleAdapter = new SimpleAdapter(this, gridItemList,
				R.layout.gridview_item, new String[] { "itemImage",
						"card_title", "listPrice", "freshPrice", "vipPrice" },
				new int[] { R.id.logo,  R.id.card_title,R.id.listPrice,
						R.id.freshPrice, R.id.vipPrice });

		gv.setAdapter(simpleAdapter);
		gv.setNumColumns(3);
		gv.setOnItemClickListener(this);
	}

	// // private void createGridMenu() {
	// // List<HashMap<String, Object>> gridItemList = new
	// ArrayList<HashMap<String, Object>>();
	// // for (int i = 0; i < gridMenu.length; i++) {
	// // HashMap<String, Object> gridItem = new HashMap<String, Object>();
	// // gridItem.put("itemImage", gridMenu[i][1]);
	// // gridItem.put("itemText", getString(gridMenu[i][0]));
	// // gridItemList.add(gridItem);
	// // }
	// //
	// // SimpleAdapter simpleAdapter = new SimpleAdapter(this,
	// // gridItemList,
	// // R.layout.gridview_item,
	// // new String[] { "itemImage", "itemText" },
	// // new int[] { R.id.imageview, R.id.textvew1 });
	// //
	// // gv.setAdapter(simpleAdapter);
	// // gv.setNumColumns(4);
	// // gv.setOnItemClickListener(this);
	// // }
	//
	//
	// private void initComponents() {
	//
	// ((ImageView)this.findViewById(R.id.button_cancel_trade_for_card)).setOnClickListener(this);
	// ((LinearLayout)this.findViewById(R.id.layout_cancel_trade_for_card)).setOnClickListener(this);
	// if(loadDBCards() == false){
	// loadDefaultCards();
	// }
	// }
	//
	// private boolean loadDBCards(){
	// boolean isHaveCards = false;
	// Map<String, String> params = new HashMap<String,String>();
	// params.put(PrePayCardDAO.ELEM_VERSION_CODE,
	// ColetApplication.getApp().getConfigFile().getVipCardListCode());
	// PrePayCardList cards = (PrePayCardList)
	// ColetApplication.getApp().getprePaycardDao().get(params);
	// if( cards.getPrePayCardList().size() == 0 ){
	// return isHaveCards;
	// }
	// int i = 0;
	// for(PrePayCard card : cards.getPrePayCardList()){
	// defaultFreshrice[i] = card.getFresh_price();
	// defaultListPrice[i] = card.getList_price();
	// defaultVipPrice[i]=card.getVip_price();
	//
	// String ListP= SystemUtil.txfloat(Float.valueOf(defaultListPrice[i]) /
	// 100.00f);
	// String freshP = SystemUtil.txfloat(Float.valueOf(defaultFreshrice[i]) /
	// 100.00f);
	// String vipP = SystemUtil.txfloat(Float.valueOf(defaultVipPrice[i]) /
	// 100.00f);
	//
	// initCard(i, defaultTitle[i], "新手价:¥"+freshP, "会员价:¥"+vipP, "¥"+ListP,
	// CARD_TYPE.BLUE);
	// i++;
	// }
	//
	// isHaveCards = true;
	// isShowDefaultCards = true;
	// return isHaveCards;
	// }
	//
	// private void loadDefaultCards(){
	// for (int i = 0; i < CARD_NUM; i++) {
	// String ListP= SystemUtil.txfloat(Float.valueOf(defaultListPrice[i]) /
	// 100.00f);
	// String freshP = SystemUtil.txfloat(Float.valueOf(defaultFreshrice[i]) /
	// 100.00f);
	// String vipP = SystemUtil.txfloat(Float.valueOf(defaultVipPrice[i]) /
	// 100.00f);
	//
	// initCard(i, defaultTitle[i], "新手价:¥"+freshP, "会员价:¥"+vipP, "¥"+ListP,
	// CARD_TYPE.BLUE);
	// // if( i<2 ){
	// // }else if( i < 4){
	// // initCard(i, title, "", "会员价:¥"+ListP, "¥5.00", CARD_TYPE.RED);
	// // }else{
	// // initCard(i, title", "", "会员价:¥7.50", "¥10.00", CARD_TYPE.PURPLE);
	// // }
	// }
	// }

	enum CARD_TYPE {
		BLUE, YELLOW, RED, PURPLE
	}

	/**
	 * 初始化卡
	 * 
	 * @param index
	 */
	private void initCard(int index, String title, String freshPrice,
			String vipPrice, String listPrice, CARD_TYPE type) {

		int cardImage = -1;
		int cardTitle = -1;
		int cardFreshPrice = -1;
		int cardVipPrice = -1;
		int cardListPrice = -1;

		switch (index) {
		case 0:
			cardImage = R.id.card1_image;
			cardTitle = R.id.card1_title;
			cardFreshPrice = R.id.card1_freshprice;
			cardVipPrice = R.id.card1_vipprice;
			cardListPrice = R.id.card1_listprice;
			break;
		case 1:
			cardImage = R.id.card2_image;
			cardTitle = R.id.card2_title;
			cardFreshPrice = R.id.card2_freshprice;
			cardVipPrice = R.id.card2_vipprice;
			cardListPrice = R.id.card2_listprice;
			break;
		case 2:
			cardImage = R.id.card3_image;
			cardTitle = R.id.card3_title;
			cardFreshPrice = R.id.card3_freshprice;
			cardVipPrice = R.id.card3_vipprice;
			cardListPrice = R.id.card3_listprice;
			break;
		case 3:
			cardImage = R.id.card4_image;
			cardTitle = R.id.card4_title;
			cardFreshPrice = R.id.card4_freshprice;
			cardVipPrice = R.id.card4_vipprice;
			cardListPrice = R.id.card4_listprice;
			break;
		case 4:
			cardImage = R.id.card5_image;
			cardTitle = R.id.card5_title;
			cardFreshPrice = R.id.card5_freshprice;
			cardVipPrice = R.id.card5_vipprice;
			cardListPrice = R.id.card5_listprice;
			break;
		case 5:
			cardImage = R.id.card6_image;
			cardTitle = R.id.card6_title;
			cardFreshPrice = R.id.card6_freshprice;
			cardVipPrice = R.id.card6_vipprice;
			cardListPrice = R.id.card6_listprice;
			break;
		default:
			break;
		}
		if (cardTitle != -1) {
			card_title_array[index] = cardTitle;
			((TextView) this.findViewById(card_title_array[index]))
					.setVisibility(View.VISIBLE);
			((TextView) this.findViewById(card_title_array[index]))
					.setText(title);

		}

		if (cardFreshPrice != -1) {
			freshprice_array[index] = cardFreshPrice;
			((TextView) this.findViewById(freshprice_array[index]))
					.setVisibility(View.VISIBLE);
			((TextView) this.findViewById(freshprice_array[index]))
					.setText(freshPrice);
		}

		if (cardVipPrice != -1) {
			vipprice_array[index] = cardVipPrice;
			((TextView) this.findViewById(vipprice_array[index]))
					.setVisibility(View.VISIBLE);
			((TextView) this.findViewById(vipprice_array[index]))
					.setText(vipPrice);
		}

		if (cardListPrice != -1) {
			listprice_array[index] = cardListPrice;
			((TextView) this.findViewById(listprice_array[index]))
					.setVisibility(View.VISIBLE);
			((TextView) this.findViewById(listprice_array[index]))
					.setText(listPrice);
			// DiscountLine(listprice_array[index]);//删除线
		}

		if (cardImage != -1) {
			card_array[index] = cardImage;

			ImageView image = ((ImageView) this.findViewById(card_array[index]));
			image.setVisibility(View.VISIBLE);
			image.setOnTouchListener(this);
			image.setOnClickListener(this);
			// switch (type) {
			// case YELLOW:
			// image.setImageResource(R.drawable.vip1);
			// break;
			// case BLUE:
			// image.setImageResource(R.drawable.vip2);
			// break;
			// case RED:
			// image.setImageResource(R.drawable.vip3);
			// break;
			// case PURPLE:i
			// image.setImageResource(R.drawable.vip4);
			// break;
			// }
		}

	}

	/**
	 * 给TextView的文字增加删除线
	 * 
	 * @param textview
	 */
	// private void DiscountLine(TextView textview) {
	// textview.getPaint().setFlags(Paint.STRIKE_THRU_TEXT_FLAG);// 中间横线
	// textview.getPaint().setAntiAlias(true);// 抗锯齿
	// }

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		stopCountTimer();
		Activity_vipcard_confirm dialog = new Activity_vipcard_confirm.Builder(
				Activity_10_vipcard2.this).create();
		dialog.setOnCancelListener(new OnCancelListener(){
			@Override
			public void onCancel(DialogInterface arg0) {
				restartCountTimer();
			}
		});
		dialog.setOnDismissListener(new OnDismissListener(){
			@Override
			public void onDismiss(DialogInterface arg0) {
				restartCountTimer();
			}
		});
		dialog.setListPrice(defaultListPrice[position]);
		dialog.setFreshPrice(defaultFreshrice[position]);
		dialog.setVipPrice(defaultVipPrice[position]);
		dialog.show();
	}

	@Override
	public void onClick(View arg0) {

		if (arg0.getId() == R.id.button_cancel_trade_for_card
				|| arg0.getId() == R.id.layout_cancel_trade_for_card) {
			
			stopCountTimer();
			
			((LinearLayout) this
					.findViewById(R.id.layout_cancel_trade_for_card))
					.setBackgroundColor(Color.GRAY);
			Intent intent = new Intent();
			intent.setClass(Activity_10_vipcard2.this,
					Activity_02_Welcome.class);
			Activity_10_vipcard2.this.startActivity(intent);
			this.finish();
		}
	}

	@Override
	public boolean onTouch(View v, MotionEvent event) {
		UiEffectUtil.onTouch(v, event);
		resetCountTimer();
		return false;
	}

}
