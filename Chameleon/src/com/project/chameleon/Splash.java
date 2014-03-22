package com.project.chameleon;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

public class Splash extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash);

		Thread timer = new Thread(){
			public void run(){
				try {
					sleep(3000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} finally{
					Intent startingPoint = new Intent(Splash.this, LoginPage.class );
					startActivity(startingPoint);
				}
			}
		};
		timer.start();
	}

}
