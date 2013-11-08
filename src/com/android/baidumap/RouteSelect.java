package com.android.baidumap;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

public class RouteSelect extends Activity{
	RadioGroup button_group;
	Button click;
	EditText fromwhere,towhere;
	RadioButton walk,car,bus;
	int TYPE;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.bus_layout);
		button_group=(RadioGroup)findViewById(R.id.button_group);
		fromwhere=(EditText)findViewById(R.id.fromwhere);
		towhere=(EditText)findViewById(R.id.towhere);
		click=(Button)findViewById(R.id.click);
		
		walk=(RadioButton)findViewById(R.id.walk);
		car=(RadioButton)findViewById(R.id.car);
		bus=(RadioButton)findViewById(R.id.bus);
		
		button_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			
		
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				// TODO Auto-generated method stub
				if(checkedId==walk.getId()) TYPE=1;
				else if(checkedId==car.getId()) TYPE=2;
				else if(checkedId==bus.getId()) TYPE=3;
				else TYPE=0;
			}
		});
		
		
		click.setOnClickListener(new OnClickListener(){

			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				Bundle bundle=new Bundle();
				bundle.putString("FROM",fromwhere.getText().toString().trim());
				bundle.putString("TO",towhere.getText().toString().trim());
				bundle.putInt("TYPE", TYPE);
				Intent intent=new Intent();
				intent.putExtra("ROUTE",bundle);
				intent.setClass(RouteSelect.this,MyBaiduMapActivity.class);
				startActivity(intent);
			}});
		
		
	}
	

}
