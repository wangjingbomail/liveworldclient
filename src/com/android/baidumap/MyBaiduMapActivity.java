package com.android.baidumap;

import java.util.ArrayList;
import java.util.List;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.GeoPoint;
import com.baidu.mapapi.ItemizedOverlay;
import com.baidu.mapapi.LocationListener;
import com.baidu.mapapi.MKAddrInfo;
import com.baidu.mapapi.MKBusLineResult;
import com.baidu.mapapi.MKDrivingRouteResult;
import com.baidu.mapapi.MKEvent;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.MKPlanNode;
import com.baidu.mapapi.MKPoiResult;
import com.baidu.mapapi.MKSearch;
import com.baidu.mapapi.MKSearchListener;
import com.baidu.mapapi.MKSuggestionResult;
import com.baidu.mapapi.MKTransitRouteResult;
import com.baidu.mapapi.MKWalkingRouteResult;
import com.baidu.mapapi.MapActivity;
import com.baidu.mapapi.MapView;
import com.baidu.mapapi.MyLocationOverlay;
import com.baidu.mapapi.OverlayItem;
import com.baidu.mapapi.PoiOverlay;
import com.baidu.mapapi.Projection;
import com.baidu.mapapi.RouteOverlay;
import com.baidu.mapapi.TransitOverlay;

import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

public class MyBaiduMapActivity extends MapActivity {
    String mapkey="42641717856F974F67EA8A123F56F62C389F1A75";
    boolean map_key=true;
    BMapManager mapManager=null;
    MapView mMapView=null;
    LocationListener locationListener=null;   
    MyLocationOverlay mLocationOverlay=null;
    TextView title,info;
    Drawable maker;
    EditText route_name;
    ImageButton route_ok;
    View mPopView=null;
    Bundle search_bundle=null;
    Bundle route_bundle=null;
    MKSearch mSearch=null;   
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_main);
        
        title=(TextView)findViewById(R.id.title);
        info=(TextView)findViewById(R.id.info);
        route_name=(EditText)findViewById(R.id.route_name);
        route_ok=(ImageButton)findViewById(R.id.route_ok);
        
        //接受上一个Activity传入的Bundle数据
        search_bundle=getIntent().getBundleExtra("SEARCH");
        route_bundle=getIntent().getBundleExtra("ROUTE");
        
        maker=getResources().getDrawable(R.drawable.iconmarka);
        mPopView=super.getLayoutInflater().inflate(R.layout.popview, null);
        //定义maker对象的大小
        maker.setBounds(0, 0, maker.getIntrinsicWidth(), maker.getIntrinsicHeight());
        //声明地图管理类
        mapManager=new BMapManager(this);
        //验证密钥，监听常见异常，初始化
        mapManager.init(mapkey, new MyGeneralListener());
        mapManager.start();
        super.initMapActivity(mapManager);
        
        //设置mapView有放大缩小图标，放大缩小时标记可见（默认不可见）
        mMapView=(MapView)findViewById(R.id.map_View);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setDrawOverlayWhenZooming(true);
        //在地图图层上声明mLocationOverlay并将此图层加入到MapView中
        mLocationOverlay=new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mLocationOverlay);
        LocationManager locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE); 

//        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   
//        System.out.println("时间："+location.getTime());   
//        System.out.println("经度："+location.getLongitude());  

        //定位当前位置监听接口的实现
        locationListener=new LocationListener(){

			
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Log.i("latitude--->","定位定位");

				if(location!=null){
					Log.i("latitude--->",location.getLatitude()+"");
					GeoPoint point=new GeoPoint((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6));
					mMapView.getController().animateTo(point);
				}
			}
        };
        //地点搜索接口的实现
         final MKSearchListener mkSearchListener=new MKSearchListener(){


        	public void onGetPoiResult(MKPoiResult res, int type,int error) {
        		// TODO Auto-generated method stub
        		if(error!=0||res==null){
        			Toast.makeText(MyBaiduMapActivity.this, "对不起，未找到相关位置", Toast.LENGTH_LONG).show();
        			return ;
        		}
        		if(res.getCurrentNumPois()>0){
        			PoiOverlay overlay=new PoiOverlay(MyBaiduMapActivity.this,mMapView);
        			overlay.setData(res.getAllPoi());
        			mMapView.getOverlays().clear();
        			mMapView.getOverlays().add(overlay);
        			mMapView.invalidate();
        			mMapView.getController().animateTo(res.getPoi(0).pt);
        		}
        	}
			
			public void onGetAddrResult(MKAddrInfo res , int error) {
				// TODO Auto-generated method stub
				if(error!=0){
					String s=String.format("错误代号：%d", error);
					Toast.makeText(MyBaiduMapActivity.this, s, Toast.LENGTH_LONG).show();
					return;
				}
				mMapView.getController().animateTo(res.geoPt);
				String info=String.format("纬度：%f 经度：%f \r\n", res.geoPt.getLatitudeE6()/1E6,res.geoPt.getLongitudeE6()/1E6);
				Toast.makeText(MyBaiduMapActivity.this, info, Toast.LENGTH_LONG).show();
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(new OverItem(maker, MyBaiduMapActivity.this));
				mMapView.invalidate();
			}

			
			public void onGetBusDetailResult(MKBusLineResult res, int error) {
				// TODO Auto-generated method stub
				if(error!=0||res==null){
					Toast.makeText(MyBaiduMapActivity.this, "对不起，未找到相关信息", Toast.LENGTH_LONG).show();
					return;
				}
				RouteOverlay overlay=new RouteOverlay(MyBaiduMapActivity.this,mMapView);
				overlay.setData(res.getBusRoute());
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(overlay);
				mMapView.invalidate();
				
				mMapView.getController().animateTo(res.getBusRoute().getStart());
			}

		
			public void onGetDrivingRouteResult(MKDrivingRouteResult res,
					int error) {
				// TODO Auto-generated method stub
				if(error!=0||res==null){
					Toast.makeText(MyBaiduMapActivity.this, "抱歉，未找到结果",Toast.LENGTH_LONG).show();
					return;
				}
				RouteOverlay overlay=new RouteOverlay(MyBaiduMapActivity.this, mMapView);
				overlay.setData(res.getPlan(0).getRoute(1));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(overlay);
				mMapView.invalidate();
				
				mMapView.getController().animateTo(res.getStart().pt);
			}


			
			public void onGetTransitRouteResult(MKTransitRouteResult res,
					int error) {
				// TODO Auto-generated method stub
				if(error!=0||res==null){
					Toast.makeText(MyBaiduMapActivity.this, "抱歉，未找到结果",Toast.LENGTH_LONG).show();
					return;
				}
				TransitOverlay overlay=new TransitOverlay(MyBaiduMapActivity.this, mMapView);
				overlay.setData(res.getPlan(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(overlay);
				mMapView.invalidate();
				
				mMapView.getController().animateTo(res.getStart().pt);
			}

			
			public void onGetWalkingRouteResult(MKWalkingRouteResult res,
					int error) {
				// TODO Auto-generated method stub
				if(error!=0||res==null){
					Toast.makeText(MyBaiduMapActivity.this, "抱歉，未找到结果",Toast.LENGTH_LONG).show();
					return;
				}
				RouteOverlay overlay=new RouteOverlay(MyBaiduMapActivity.this, mMapView);
				overlay.setData(res.getPlan(0).getRoute(0));
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(overlay);
				mMapView.invalidate();
				
				mMapView.getController().animateTo(res.getStart().pt);
			}

			public void onGetRGCShareUrlResult(String arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}

			public void onGetSuggestionResult(MKSuggestionResult arg0, int arg1) {
				// TODO Auto-generated method stub
				
			}
        	
        };
      //初始化搜索模块
        mSearch=new MKSearch();
    	mSearch.init(mapManager, mkSearchListener);
    	//地图长击事件，显示击点的经纬度
        mMapView.setOnClickListener(new OnClickListener() {

		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GeoPoint ptCenter=mMapView.getMapCenter();
				mSearch.reverseGeocode(ptCenter);
				
			}
	
		});
        
        //根据传入的bundle判断其源bundle，然后对其添加相应的处理方法。根据bundle携带的数据搜索定位
        if(search_bundle!=null){
       
        	mSearch.poiSearchInCity(search_bundle.getString("CITY"), search_bundle.getString("ZOOM"));
        	search_bundle=null;
        }
        //根据传入bundle的数据添加路线查询的处理方法
        if(route_bundle!=null){
        	
        	MKPlanNode staNode=new MKPlanNode();
        	staNode.name=route_bundle.getString("FROM");
        	MKPlanNode endNode=new MKPlanNode();
        	endNode.name=route_bundle.getString("TO");
        	switch(route_bundle.getInt("TYPE")){
        	case 1:mSearch.walkingSearch("成都", staNode, "成都", endNode);
        		break;
        	case 2:mSearch.drivingSearch("成都", staNode, "成都", endNode);
        		break;
        	case 3:mSearch.transitSearch("成都", staNode, endNode);
        		break;
        	}
        	route_bundle=null;
        }
        //添加查询公交路线事件!!!此模块暂时不可用。调试了N次无果
        route_ok.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
	        	mSearch.busLineSearch("武汉",route_name.getText().toString().trim());
	        	Log.i("SHOW-->",route_name.getText().toString().trim());
			}
		});
        //想首页面中添加分条目标记物及相应的气泡显示图层
        mMapView.getOverlays().add(new OverItem(maker, this));
        mMapView.addView(mPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
        		LayoutParams.WRAP_CONTENT,null,MapView.LayoutParams.TOP_LEFT));
        //设置PopView不可见，点击是才可见
        mPopView.setVisibility(View.GONE);
        
    }
    //监听网络或者地图Key异常类
    class MyGeneralListener implements MKGeneralListener{

		
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			if(iError==MKEvent.ERROR_NETWORK_CONNECT)
			Toast.makeText(MyBaiduMapActivity.this,"当前网络异常，暂时无法提供服务", Toast.LENGTH_LONG).show();
		}

		
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if(iError==MKEvent.ERROR_PERMISSION_DENIED){
				Toast.makeText(MyBaiduMapActivity.this, "百度地图授权的Key错误，请重新设置！", Toast.LENGTH_LONG).show();
			}
			map_key=false;
		}
    	
    }
    //地图线路的相关类
   
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    //程序销毁时销毁MapManager
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mapManager.destroy();
		super.onDestroy();
	}
	//程序进入后天时暂时停用MapManager及当前位置相关显示
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mapManager.getLocationManager().removeUpdates(locationListener);
		mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass();
		mapManager.stop();
		super.onPause();
	}
	//程序从后台恢复时，使地图管理器，当前位置显示等可用
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mapManager.getLocationManager().requestLocationUpdates(locationListener);
		mLocationOverlay.enableCompass();
		mLocationOverlay.enableMyLocation();
		super.onResume();
	}
	//分条目覆盖物，用文字图片等标注特定位置的方法，本例中还涉及到弹出气泡框
	class OverItem extends ItemizedOverlay<OverlayItem>{
		private List<OverlayItem> mGeoList=new ArrayList<OverlayItem>();
		
		private double mlat1=30.540554;
		private double mlong1=114.436211;
		private double mlat2=30.540554;
		private double mlong2=114.486211;
		private double mlat3=30.540554;
		private double mlong3=114.536211;
		
		private Drawable maker;
		private Context context;
		//自定义一些局部变量，并传入drawable和context参数
		public OverItem(Drawable maker,Context context) {
			super(boundCenterBottom(maker));
			this.maker=maker;
			this.context=context;
			
//			GeoPoint p1=new GeoPoint((int)(mlat1*1E6),(int)(mlong1*1E6));
//			GeoPoint p2=new GeoPoint((int)(mlat2*1E6),(int)(mlong2*1E6));
//			GeoPoint p3=new GeoPoint((int)(mlat3*1E6),(int)(mlong3*1E6));
//			
//			mGeoList.add(new OverlayItem(p1,"第一点","我是谁？"));
//			mGeoList.add(new OverlayItem(p2,"第二点","我来自哪里？"));
//			mGeoList.add(new OverlayItem(p3,"第三点","我将要干什么？"));
			
			populate();
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return mGeoList.get(arg0);
		}
		//返回标记条目的数量
		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}
		//点击其他位置气泡消失
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// 消去弹出的气泡
			mPopView.setVisibility(View.GONE);
			
			return super.onTap(arg0, arg1);
		}
		//点击标记物时弹出气泡
		@Override
		protected boolean onTap(int i) {
			// TODO Auto-generated method stub
			setFocus(mGeoList.get(i));
			title=(TextView)mPopView.findViewById(R.id.title);
			info=(TextView)mPopView.findViewById(R.id.info);
			title.setText(mGeoList.get(i).getTitle());
			info.setText(mGeoList.get(i).getSnippet());
			GeoPoint pt=mGeoList.get(i).getPoint();
			mMapView.updateViewLayout(mPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,LayoutParams.WRAP_CONTENT,
					pt,MapView.LayoutParams.BOTTOM_CENTER));
			
			mPopView.setVisibility(View.VISIBLE);
			
			return true;
		}
		//产生图形文字等，气泡的内容
		@Override
		public void draw(Canvas canvas, MapView mapView, boolean shadow) {
			// TODO Auto-generated method stub
			Projection projection=mapView.getProjection();
			for(int index=size()-1;index>=0;index--){
				OverlayItem overlayItem=getItem(index);
				Point point=projection.toPixels(overlayItem.getPoint(), null);
				
				String title=overlayItem.getTitle();
				Paint paint=new Paint();
				paint.setColor(Color.BLUE);
				paint.setTextSize(13);
				canvas.drawText(title, point.x-30, point.y, paint);
			}
			super.draw(canvas,mapView,shadow);
		}
		
	}

	
	//创建menu菜单，用于导航
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_activity, menu);
		return true;
		
	}
	//定义菜单单击时的事件
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		switch (item.getItemId()) {
		case R.id.menu_about:
        setContentView(R.layout.about);
			break;
		case R.id.menu_converage_hospital:
             
			break;
		case R.id.menu_converage_hot:
             
			break;
		case R.id.menu_converage_satellite:
           
			mMapView=(MapView)findViewById(R.id.map_View);
			mMapView.setSatellite(true);
				
	
			break;
		case R.id.menu_converage_hotel:
            
			break;
		case R.id.menu_local_map:
      
			break;
		case R.id.menu_quit:
			System.exit(0);
			break;
		case R.id.menu_seek:
			Intent intent=new Intent();
			intent.setClass( MyBaiduMapActivity.this, Search.class);
			startActivity(intent);
			break;
		case R.id.menu_setting:

			break;
		case R.id.menu_way:
			
			Intent intent1=new Intent();
			intent1.setClass(MyBaiduMapActivity.this,RouteSelect.class);
			startActivity(intent1);
			break;

		default:
			break;
		}
		return super.onOptionsItemSelected(item);
		
		
	
		
	}
	public  void toback() {
		// TODO Auto-generated method stub
       setContentView(R.layout.map_main);
	}
}