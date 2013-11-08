package com.android.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;


public class Search extends Activity {
	EditText city,zoom;
	Button ok;
	String cityStr="";
	String zoomStr="";

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.search_layout);
		city=(EditText)findViewById(R.id.city);
		zoom=(EditText)findViewById(R.id.zoom);
		ok=(Button) findViewById(R.id.ok);
		
		ok.setOnClickListener(new OnClickListener(){

			public void onClick(View v) {
				// TODO Auto-generated method stub
				cityStr=city.getText().toString().trim();
				zoomStr=zoom.getText().toString().trim();
				Bundle bundle=new Bundle();
				bundle.putString("CITY", cityStr);
				bundle.putString("ZOOM", zoomStr);
				Intent intent=new Intent();
				intent.putExtra("SEARCH", bundle);
				intent.setClass(Search.this, MyBaiduMapActivity.class);
				startActivity(intent);
				
			}
			
		});
		
		
	}
	
}
