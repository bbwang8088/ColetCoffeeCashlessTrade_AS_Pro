package tech.bbwang.www.activity;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;

public class SystemStarter extends BroadcastReceiver {

	public static final String TAG = "SystemStarter";  
    @Override  
    public void onReceive(Context context, Intent intent) {  
        //if ("android.intent.action.BOOT_COMPLETED".equals(intent.getAction())) {  
            ComponentName comp = new ComponentName(context.getPackageName(), Activity_01_Connect.class.getName());  
            context.startActivity(new Intent().setComponent(comp).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));  
        //} else {  
        //    Log.e(TAG, "Received unexpected intent " + intent.toString());  
        //}  
    }  


}
