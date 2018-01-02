package tech.bbwang.www.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.util.Log;
import android_serialport_api.SerialPort;

/**
 * 咖啡机串口通讯类
 * 
 * @author wang-bingbing
 * 
 */
public class ColetCoffeeUtil extends Thread {

	private SerialPort sp = null;
	private FileOutputStream outputStream = null;
	private FileInputStream inputStream = null;
	private static ColetCoffeeUtil coffeeUtil = null;
	private boolean stop = false;
	private String receiveCallBackMethodName = null;
	private Object receiver = null;

	public ColetCoffeeUtil(Object receiver, String callbackMethod) {
		super();
		setOnReceiveCallBack(receiver, callbackMethod);
		try {
			openUART();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void run() {
		super.run();
		while (stop == false) {
			receive();
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

	}

	/**
	 * 启动收发
	 */
	public void startRun() {
		this.stop = false;
		this.start();
	}

	/**
	 * 关闭收发
	 */
	public void stopRun() {
		this.stop = true;
	}

	/**
	 * 销毁收发线程
	 */
	public void destoryThread() {
		try {
			stopRun();
			if (inputStream != null) {
				inputStream.close();
			}
			if (outputStream != null) {
				outputStream.close();
			}
			if (sp != null) {
				sp.close();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static final int ONE_PACKET_SIZE = 10;
	byte[] buffer = new byte[ONE_PACKET_SIZE];
	int size = 0;

	/**
	 * 接受数据，线程处理
	 */
	public void receive() {
		size = 0;
		try {
			if (inputStream == null) {
				return;
			}
			while (inputStream.available() > 0 && size < 10) {
				size += inputStream.read(buffer, size, 1);
			}

			if (size == 10 && buffer[0] == 0x72 && buffer[9] == 0x73) {
				// if( size == 10 ){
				Method callback = receiver.getClass().getDeclaredMethod(this.receiveCallBackMethodName, byte[].class);
				callback.invoke(receiver, buffer);
				size = 0;
			}
			// }

		} catch (IOException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		} catch (NoSuchMethodException e) {
			e.printStackTrace();
		}

	}

	/**
	 * 发送数据
	 * 
	 * @param data
	 */
	public void send(byte[] data) {
		try {
			Log.d("ColetCoffeeUtil", "send " + ByteUtil.byte2HexStr(data, data.length));
			outputStream.write(data);
			outputStream.write('\n');
			// outputStream.flush();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void setOnReceiveCallBack(Object obj, String callback) {
		this.receiver = obj;
		this.receiveCallBackMethodName = callback;
	}

	private ColetCoffeeUtil() {
		try {
			openUART();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static ColetCoffeeUtil getInstance() {
		if (coffeeUtil == null) {
			coffeeUtil = new ColetCoffeeUtil();
		}
		return coffeeUtil;
	}

	public void openUART() throws SecurityException, IOException {

		SerialPort sp = null;
		/*   展讯平台 -> /dev/ttyS3
		 *	 MTK平台  -> /dev/ttyMT  
		 */
		sp = new SerialPort(new File("/dev/ttyMT1"), 9600, 0); 
		outputStream = (FileOutputStream) sp.getOutputStream();
		inputStream = (FileInputStream) sp.getInputStream();
	}

	/**
	 * 制作一杯测试用的意式咖啡，内部参数只是随便调的
	 * 
	 * @return
	 */
	public void makeTestItalyCoffee() {
		byte[] data1_7 = { 0x51, 0x03, 0x64, 0x1e, 0x1e, 0x64, 0x73 };
		byte checksum = calcCheckSum(data1_7);
		byte[] data = { 0x72, 0x51, 0x03, 0x64, 0x1e, 0x1e, 0x64, 0x73, checksum, 0x73 };
		this.send(data);
	}

	/**
	 * 制作一杯测试用的冰美式咖啡，内部参数只是随便调的
	 * 
	 * @return
	 */
	public void makeTestColdAmricanCoffee() {
		byte[] data1_7 = { 0x51, 0x05, 0x64, 0x1e, 0x1e, 0x64, 0x73 };
		byte checksum = calcCheckSum(data1_7);
		byte[] data = { 0x72, 0x51, 0x05, 0x64, 0x1e, 0x1e, 0x64, 0x73, checksum, 0x73 };
		this.send(data);
	}

	/**
	 * 制作一杯测试用的熱拿鐵咖啡，内部参数只是随便调的
	 * 
	 * @return
	 */
	public void makeTestHotNatieCoffee() {
		byte[] data1_7 = { 0x51, 0x08, 0x64, 0x1e, 0x1e, 0x64, 0x73 };
		byte checksum = calcCheckSum(data1_7);
		byte[] data = { 0x72, 0x51, 0x08, 0x64, 0x1e, 0x1e, 0x64, 0x73, checksum, 0x73 };
		this.send(data);
	}

	/**
	 * 计算校验码
	 * 
	 * @param data
	 *            需要计算的byte数组
	 * @return byte 计算得到的校验码，如果传入的数组为null或者长度为0，则返回0xff.
	 */
	public byte calcCheckSum(final byte[] data) {
		byte checksum = (byte) 0x00;
		if (data == null || data.length == 0) {
			return checksum;
		}
		for (byte b : data) {
			checksum += b;
		}
		return checksum;
	}

	/**
	 * 核对校验码
	 * 
	 * @param data
	 *            数据帧数组
	 * @return boolean true：正确的数据帧 false:错误的数据帧
	 */
	public boolean confirmCheckSum(final byte[] data) {

		boolean falg = false;
		if (data == null || data.length != 10) {
			return falg;
		}
		byte[] tmp = new byte[7];
		System.arraycopy(data, 1, tmp, 0, 7);
		byte checkSum1 = calcCheckSum(tmp);
		if (checkSum1 == data[8]) {
			falg = true;
		}
		return falg;
	} 
    //                                               命令码                  咖啡量                        热牛奶时间      拿铁比例                               温度符号          温度                    校验码
	//                                   d[0]   d[1]  d[2]          d[3]            d[4]      d[5]             d[6]        d[7]          d[8]         d[9]
	private static byte [] meishi    ={  0x72,  0x51, 0x06,  (byte) 0xAA/*170*/,    0x00,     0x00,            0x01,      0x02/*中温*/,   0x00,       0x73 };
	private static byte [] kabujinuo ={  0x72,  0x51, 0x07,         0x6E/*110*/,    0x1E,     0x00,            0x01,      0x02/*中温*/,   0x00,       0x73 };
	private static byte [] natie     ={  0x72,  0x51, 0x08,         0x6E/*110*/,    0x1E,     0x32/*50%*/,     0x01,      0x02/*中温*/,   0x00,       0x73 };
	private static byte [] hotMilk   ={  0x72,  0x51, 0x09,         0x00,     (byte)0xFA,     0x00,            0x00,      0x00/*低温*/,  (byte) 0x8C, 0x73 };
	private static byte [] hotMlikPao={  0x72,  0x51, 0x10,         0x00,     (byte)0xFA,     0x00,            0x00,      0x02/*中温*/,  (byte) 0x93, 0x73};
	
	public void sendCoffeeCommand(int coffeeType, int coffeeSize, int needSuger) {
		
//		public final static int COFFEE_TYPE_CAPPUCCION = 0;//卡布基诺
//		public final static int COFFEE_TYPE_HOT_AMERACAN = 1;//美式咖啡（热
//		public final static int COFFEE_TYPE_ICE_AMERACAN = 2;//美式咖啡（冰）
//		public final static int COFFEE_TYPE_ICE_LATTE = 3;//拿铁（冰）
//		public final static int COFFEE_TYPE_HOT_LATTE = 4;//拿铁（热）
//		public final static int COFFEE_TYPE_ITALY = 5;//意式咖啡
//		
//		
//		public final static int COFFEE_TYPE_MILK = 6;//热牛奶
//		public final static int COFFEE_TYPE_ZQX = 7;//自清洗
//		public final static int COFFEE_TYPE_CG = 8;//除垢
//		public final static int COFFEE_TYPE_TOTAL = 9;//总数
//		public final static int COFFEE_TYPE_MILKRATE = 10;//奶沫
//		public final static int COFFEE_TYPE_CANLE= 11;//取消
		byte [] data = null;
		
		switch( coffeeType ){
		case CashlessConstants.COFFEE_TYPE_HOT_AMERACAN:
			data = meishi;
			break;
		case CashlessConstants.COFFEE_TYPE_CAPPUCCION:
			data = kabujinuo;
			break;
		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
			data = natie;
			break;
		case CashlessConstants.COFFEE_TYPE_MILK:
			data = hotMilk;
			break;
		case CashlessConstants.COFFEE_TYPE_MILKRATE:
			data = hotMlikPao;
			break;
		default:
			break;
		}
		
		if( data.length == 10 ){
			byte[] data_tmp = new byte[7];
			System.arraycopy(data, 1, data_tmp, 0, 7);
			byte checkSum = this.calcCheckSum(data_tmp);
			data[8] = checkSum;
		}
		
		this.send(data);
	}
//	public void sendCoffeeCommand(int coffeeType, int coffeeSize, int needSuger) {
//
//		byte para0 = 0;
//		byte para2 = 0;
//		byte para3 = 0;
//		byte para1 = 0;
//		byte para4 = 0;
////		byte milkBubble = 0;
//		byte  para5 = 0;
//		byte checksum = 0;
//		para0 = getCoffeeType(coffeeType);
//		switch (para0){
//
//			case 0x07:            // 卡布
//
////				try {
//					byte [] data={0x72,0x51, 0x07,0x6e,0x28, 0x00, 0x01, 0x02, checksum, 0x73};
//					//coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "卡布奇诺").findFirst();
////					if (coffeeInfo!=null){
////						int coffee = coffeeInfo.getCoffeeSize();
////						int milkSize = coffeeInfo.getMilkSize();
////						data= new byte[]{0x72, 0x51, 0x07, (byte) coffee, (byte) milkSize, 0x00, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//                break;
//			case 0x08:  //拿铁
//
////				try {
//					byte[] data2={0x72,0x51, 0x08,0x6e,0x28,0x1E, 0x01, 0x02, checksum, 0x73};
////					coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "拿铁").findFirst();
////					if (coffeeInfo!=null){
////						int coffee = coffeeInfo.getCoffeeSize();
////						int milkSize = coffeeInfo.getMilkSize();
////						int milkRate = coffeeInfo.getMilkRate();
////						data= new byte[]{0x72, 0x51, 0x08, (byte) coffee, (byte) milkSize, (byte) milkRate, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data2);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//				break;
//			case 0x06:		//美式
//
////				try {
//					byte[] data3={0x72,0x51, 0x06, (byte) 0xAA, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "美式咖啡").findFirst();
////					if (coffeeInfo!=null){
////						int coffee = coffeeInfo.getCoffeeSize();
////						data= new byte[]{0x72, 0x51, 0x06, (byte) coffee, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data3);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//				break;
//			case 0x05:  //意式
//
////				try {
//					byte[] data4={0x72,0x51, 0x05,  0x64, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "意式咖啡").findFirst();
////					if (coffeeInfo!=null){
////						int coffee = coffeeInfo.getCoffeeSize();
////						data= new byte[]{0x72, 0x51, 0x05, (byte) coffee, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data4);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//				break;
//			case 0x09:   //热牛奶
//
////				try {
//					byte[] data5={0x72,0x51, 0x09, (byte) 0xc8, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "牛奶").findFirst();
////					if (coffeeInfo!=null){
////						int milkSize = coffeeInfo.getMilkSize();
////						data= new byte[]{0x72, 0x51, 0x09, (byte) milkSize,0x00 , 0x00, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data5);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//                break;
//			case 0x10:   //热奶沫
//
////				try {
//					byte[] data6={0x72,0x51, 0x10, 0x64, 0x00, 0x00, 0x01, 0x02, checksum, 0x73};
////					coffeeInfo = dbManager.selector(CoffeeInfo.class).where("Type", "=", "奶沫").findFirst();
////					if (coffeeInfo!=null){
////						int milkSize = coffeeInfo.getMilkSize();
////						data= new byte[]{0x72, 0x51, 0x10, (byte) milkSize,0x00 , 0x00, 0x01, 0x02, checksum, 0x73};
////					}
////					byte[] data_tmp = new byte[7];
////					System.arraycopy(data, 1, data_tmp, 0, 7);
////					checksum = calcCheckSum(data_tmp);
////					data[8] = checksum;
//					this.send(data6);
////				} catch (DbException e) {
////					e.printStackTrace();
////				}
//				break;
//			case 0x13:
//				byte[] data7={0x72,0x51, 0x13, 0x00, 0x00, 0x00, 0x00, 0x00, checksum, 0x73};
//				byte[] data_tmp6 = new byte[7];
//				System.arraycopy(data7, 1, data_tmp6, 0, 7);
//				checksum = calcCheckSum(data_tmp6);
//				data7[8] = checksum;
//				this.send(data7);
//				break;
//			case 0x12:
//				byte[] data8={0x72,0x51, 0x12, 0x00, 0x00, 0x00, 0x00, 0x00, checksum, 0x73};
//				byte[] data_tmp7 = new byte[7];
//				System.arraycopy(data8, 1, data_tmp7, 0, 7);
//				checksum = calcCheckSum(data_tmp7);
//				data8[8] = checksum;
//				this.send(data8);
//				break;
//			case (byte) 0xff:
//				byte[] data9={0x72,0x51,(byte)0xff, 0x00, 0x00, 0x00, 0x00, 0x00, checksum, 0x73};
//				byte[] data_tmp8 = new byte[7];
//				System.arraycopy(data9, 1, data_tmp8, 0, 7);
//				checksum = calcCheckSum(data_tmp8);
//				data9[8] = checksum;
//				this.send(data9);
//				break;
//			default:
//				break;
//		}
//	}
		
//	public void sendCoffeeCommand(int coffeeType, int coffeeSize, int needSuger) {
//
//		byte para0 = 0;
//		byte para2 = 0;
//		byte para3 = 0;
//		byte para1 = 0;
//		byte para4 = 0;
////		byte milkBubble = 0;
//		byte  para5 = 0;
//		byte checksum = 0;
//		
//		para0 = getCoffeeType(coffeeType);
//		para1 = getCoffeeSize(coffeeType, coffeeSize);
//		para2 = getHotColdWater(coffeeType, coffeeSize);
//		para3 = getMilkRate(coffeeType, coffeeSize);
//		para4 = getMilkSize(coffeeType, coffeeSize);
////		milkBubble = getMilkBubble(coffeeType, coffeeSize);
//		para5 = getPowderSuger(coffeeType, needSuger);
//		
//		byte[] data = { 0x72,0x51, para0, para1, para2, para3, para4, para5,checksum, 0x73};
//		byte[] data_tmp = new byte[7];
//		System.arraycopy(data, 1, data_tmp, 0, 7);
//		checksum = calcCheckSum(data_tmp);
//		data[8] = checksum;
//		
//		this.send(data);
//	}

//	/**
//	 * 咖啡命令
//	 * @param coffeeType
//	 * @return
//	 */
//	private byte getCoffeeType(int coffeeType) {
//		byte type = (byte) 0x03;
//
//		switch (coffeeType) {
//		case CashlessConstants.COFFEE_TYPE_ITALY:
//			type = (byte) 0x03;
//			break;
//		case CashlessConstants.COFFEE_TYPE_HOT_AMERACAN:
//			type = (byte) 0x04;
//			break;
//		case CashlessConstants.COFFEE_TYPE_ICE_AMERACAN:
//			type = (byte) 0x05;
//			break;
//		case CashlessConstants.COFFEE_TYPE_CAPPUCCION:
//			type = (byte) 0x06;
//			break;
//		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
//			type = (byte) 0x07;
//			break;
//		case CashlessConstants.COFFEE_TYPE_ICE_LATTE:
//			type = (byte) 0x08;
//			break;
//		default:
//			break;
//		}
//
//		return type;
//	}
	
//	/**
//	 * 咖啡命令
//	 * @param coffeeType
//	 * @return
//	 */
//	private byte getCoffeeType(int coffeeType) {
//		byte type = (byte) 0x03;
//
//		switch (coffeeType) {
//		
//		case CashlessConstants.COFFEE_TYPE_ITALY:
//			type = (byte) 0x05;
//			break;
//		case CashlessConstants.COFFEE_TYPE_HOT_AMERACAN:
//			type = (byte) 0x06;
//			break;
//		case CashlessConstants.COFFEE_TYPE_MILK:
//			type = (byte) 0x09;
//			break;
//		case CashlessConstants.COFFEE_TYPE_CAPPUCCION:
////			type = (byte) 0x06;
//			type = (byte) 0x07;
//			break;
//		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
////			type = (byte) 0x07;
//			type = (byte) 0x08;//新咖啡机
//			break;
//		case CashlessConstants.COFFEE_TYPE_ZQX:
//			type = (byte) 0x13;
//				break;
//		case CashlessConstants.COFFEE_TYPE_CG:
//				type = (byte) 0x12;
//				break;
//		case CashlessConstants.COFFEE_TYPE_MILKRATE:
//				type = (byte) 0x10;
//			break;
//		case CashlessConstants.COFFEE_TYPE_CANLE:
//				type = (byte) 0xFF;
//			break;
//		default:
//			break;
//		}
//
//		return type;
//	}

	/**
	 * 咖啡流量
	 * @param coffeeType
	 * @param coffeeSize
	 * @return
	 */
	private byte getCoffeeSize(int coffeeType, int coffeeSize) {

		byte size = (byte) 0xff;// 60ml

		switch (coffeeSize) {
		case CashlessConstants.CUP_SIZE_SMALL:
			if (coffeeType == CashlessConstants.COFFEE_TYPE_ICE_LATTE) {
				size = (byte) 0x28;// 40ml
			} else if (coffeeType == CashlessConstants.COFFEE_TYPE_ITALY) {
				size = (byte) 0x50;// 80ml
			} else {
				size = (byte) 0x3c;// 60ml
			}
			break;
		case CashlessConstants.CUP_SIZE_NORMAL:
			size = (byte) 0x50;// 80ml
			break;
		case CashlessConstants.CUP_SIZE_BIG:
			if (coffeeType == CashlessConstants.COFFEE_TYPE_ICE_LATTE
					|| coffeeType == CashlessConstants.COFFEE_TYPE_ITALY) {
				size = (byte) 0x50;// 40ml
			} else {
				size = (byte) 0x64;// 100ml
			}
			break;
		default:
			break;
		}

		return size;
	}

	/**
	 * 热水/冷水量
	 * @param coffeeType
	 * @param coffeeSize
	 * @return
	 */
	private byte getHotColdWater(int coffeeType, int coffeeSize) {
		byte ret = (byte) 0xff;

		switch (coffeeType) {
		case CashlessConstants.COFFEE_TYPE_HOT_AMERACAN:
			if (coffeeSize == CashlessConstants.CUP_SIZE_SMALL) {
				ret = (byte) 0x5a;// 90ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_NORMAL) {
				ret = (byte) 0x8c;// 140ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_BIG) {
				ret = (byte) 0xc8;// 200ml
			}
			break;
		case CashlessConstants.COFFEE_TYPE_ICE_AMERACAN:
			if (coffeeSize == CashlessConstants.CUP_SIZE_SMALL) {
				ret = (byte) 0x5a;// 90ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_NORMAL) {
				ret = (byte) 0x78;// 120ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_BIG) {
				ret = (byte) 0x96;// 150ml
			}
			break;
		default:
			break;
		}

		return ret;
	}

	/**
	 * 奶沫比例
	 * @param coffeeType
	 * @param coffeeSize
	 * @return
	 */
	private byte getMilkRate(int coffeeType, int coffeeSize) {
		byte ret = (byte) 0xff;

		switch (coffeeType) {
		case CashlessConstants.COFFEE_TYPE_ICE_LATTE:
			ret = (byte) 0x1e;// 30%
			break;
		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
			ret = (byte) 0x32;// 50%
			break;
		default:
			break;
		}

		return ret;
	}

	/**
	 * 奶沫量/牛奶量
	 * @param coffeeType
	 * @param coffeeSize
	 * @return
	 */
	private byte getMilkSize(int coffeeType, int coffeeSize) {
		byte ret = (byte) 0xff;

		switch (coffeeType) {
		case CashlessConstants.COFFEE_TYPE_ICE_LATTE:
			if (coffeeSize == CashlessConstants.CUP_SIZE_SMALL) {
				ret = (byte) 0x14;// 20ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_NORMAL) {
				ret = (byte) 0x1e;// 30ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_BIG) {
				ret = (byte) 0x28;// 40ml
			}
			break;
		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
			if (coffeeSize == CashlessConstants.CUP_SIZE_SMALL) {
				ret = (byte) 0x0f;// 15ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_NORMAL) {
				ret = (byte) 0x14;// 20ml
			} else if (coffeeSize == CashlessConstants.CUP_SIZE_BIG) {
				ret = (byte) 0x19;// 25ml
			}
			break;
		default:
			break;
		}

		return ret;
	}

//	/**
//	 * 奶泡
//	 * @param coffeeType
//	 * @param coffeeSize
//	 * @return
//	 */
//	private byte getMilkBubble(int coffeeType, int coffeeSize) {
//		byte ret = (byte) 0xff;
//
//		switch (coffeeType) {
//		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
//			if (coffeeSize == CashlessConstants.CUP_SIZE_SMALL) {
//				ret = (byte) 0x0f;// 15ml
//			} else if (coffeeSize == CashlessConstants.CUP_SIZE_NORMAL) {
//				ret = (byte) 0x14;// 20ml
//			} else if (coffeeSize == CashlessConstants.CUP_SIZE_BIG) {
//				ret = (byte) 0x19;// 25ml
//			}
//			break;
//		default:
//			break;
//		}
//
//		return ret;
//	}
	
	/**
	 * 粉，温度范围，糖浆
	 * @param coffeeType
	 * @param needSuger
	 * @return
	 */
	private byte getPowderSuger(int coffeeType, int needSuger) {
		byte ret = (byte) 0xff;

		switch (coffeeType) {
		case CashlessConstants.COFFEE_TYPE_CAPPUCCION:
		case CashlessConstants.COFFEE_TYPE_HOT_AMERACAN:
		case CashlessConstants.COFFEE_TYPE_HOT_LATTE:
		case CashlessConstants.COFFEE_TYPE_ITALY:
			if( needSuger == CashlessConstants.WANT_SUGER_YES){
				ret = (byte) 0xbc;//101 11 100
			}else if( needSuger == CashlessConstants.WANT_SUGER_NO){
				ret = (byte) 0xb8;//101 11 000
			}
			break;
		case CashlessConstants.COFFEE_TYPE_ICE_AMERACAN:
		case CashlessConstants.COFFEE_TYPE_ICE_LATTE:
			if( needSuger == CashlessConstants.WANT_SUGER_YES){
				ret = (byte) 0xb4;//101 10 100
			}else if( needSuger == CashlessConstants.WANT_SUGER_NO){
				ret = (byte) 0xb0;//101 10 000
			}
			break;
		default:
			break;
		}

		return ret;
	}
	
}
