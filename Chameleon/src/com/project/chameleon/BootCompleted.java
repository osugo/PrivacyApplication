package com.project.chameleon;

import android.content.Context;
import android.content.BroadcastReceiver;
import android.content.Intent;
 

public class BootCompleted extends BroadcastReceiver{
     
	//here is the OnRecieve method which will be called when boot completed
	@Override
     public void onReceive(Context context, Intent intent) {
 
		//we double check here for only boot complete event
		if(intent.getAction().equalsIgnoreCase(Intent.ACTION_BOOT_COMPLETED))
   {
     //here we start the service             
     Intent serviceIntent = new Intent(context, MyService.class);
     context.startService(serviceIntent);
   }
 }
}