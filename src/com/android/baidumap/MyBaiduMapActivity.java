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
        
        //������һ��Activity�����Bundle����
        search_bundle=getIntent().getBundleExtra("SEARCH");
        route_bundle=getIntent().getBundleExtra("ROUTE");
        
        maker=getResources().getDrawable(R.drawable.iconmarka);
        mPopView=super.getLayoutInflater().inflate(R.layout.popview, null);
        //����maker����Ĵ�С
        maker.setBounds(0, 0, maker.getIntrinsicWidth(), maker.getIntrinsicHeight());
        //������ͼ������
        mapManager=new BMapManager(this);
        //��֤��Կ�����������쳣����ʼ��
        mapManager.init(mapkey, new MyGeneralListener());
        mapManager.start();
        super.initMapActivity(mapManager);
        
        //����mapView�зŴ���Сͼ�꣬�Ŵ���Сʱ��ǿɼ���Ĭ�ϲ��ɼ���
        mMapView=(MapView)findViewById(R.id.map_View);
        mMapView.setBuiltInZoomControls(true);
        mMapView.setDrawOverlayWhenZooming(true);
        //�ڵ�ͼͼ��������mLocationOverlay������ͼ����뵽MapView��
        mLocationOverlay=new MyLocationOverlay(this, mMapView);
        mMapView.getOverlays().add(mLocationOverlay);
        LocationManager locationManager=(LocationManager)this.getSystemService(Context.LOCATION_SERVICE); 

//        Location location=locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);   
//        System.out.println("ʱ�䣺"+location.getTime());   
//        System.out.println("���ȣ�"+location.getLongitude());  

        //��λ��ǰλ�ü����ӿڵ�ʵ��
        locationListener=new LocationListener(){

			
			public void onLocationChanged(Location location) {
				// TODO Auto-generated method stub
				Log.i("latitude--->","��λ��λ");

				if(location!=null){
					Log.i("latitude--->",location.getLatitude()+"");
					GeoPoint point=new GeoPoint((int)(location.getLatitude()*1e6),(int)(location.getLongitude()*1e6));
					mMapView.getController().animateTo(point);
				}
			}
        };
        //�ص������ӿڵ�ʵ��
         final MKSearchListener mkSearchListener=new MKSearchListener(){


        	public void onGetPoiResult(MKPoiResult res, int type,int error) {
        		// TODO Auto-generated method stub
        		if(error!=0||res==null){
        			Toast.makeText(MyBaiduMapActivity.this, "�Բ���δ�ҵ����λ��", Toast.LENGTH_LONG).show();
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
					String s=String.format("������ţ�%d", error);
					Toast.makeText(MyBaiduMapActivity.this, s, Toast.LENGTH_LONG).show();
					return;
				}
				mMapView.getController().animateTo(res.geoPt);
				String info=String.format("γ�ȣ�%f ���ȣ�%f \r\n", res.geoPt.getLatitudeE6()/1E6,res.geoPt.getLongitudeE6()/1E6);
				Toast.makeText(MyBaiduMapActivity.this, info, Toast.LENGTH_LONG).show();
				mMapView.getOverlays().clear();
				mMapView.getOverlays().add(new OverItem(maker, MyBaiduMapActivity.this));
				mMapView.invalidate();
			}

			
			public void onGetBusDetailResult(MKBusLineResult res, int error) {
				// TODO Auto-generated method stub
				if(error!=0||res==null){
					Toast.makeText(MyBaiduMapActivity.this, "�Բ���δ�ҵ������Ϣ", Toast.LENGTH_LONG).show();
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
					Toast.makeText(MyBaiduMapActivity.this, "��Ǹ��δ�ҵ����",Toast.LENGTH_LONG).show();
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
					Toast.makeText(MyBaiduMapActivity.this, "��Ǹ��δ�ҵ����",Toast.LENGTH_LONG).show();
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
					Toast.makeText(MyBaiduMapActivity.this, "��Ǹ��δ�ҵ����",Toast.LENGTH_LONG).show();
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
      //��ʼ������ģ��
        mSearch=new MKSearch();
    	mSearch.init(mapManager, mkSearchListener);
    	//��ͼ�����¼�����ʾ����ľ�γ��
        mMapView.setOnClickListener(new OnClickListener() {

		
			public void onClick(View v) {
				// TODO Auto-generated method stub
				GeoPoint ptCenter=mMapView.getMapCenter();
				mSearch.reverseGeocode(ptCenter);
				
			}
	
		});
        
        //���ݴ����bundle�ж���Դbundle��Ȼ����������Ӧ�Ĵ�����������bundleЯ��������������λ
        if(search_bundle!=null){
       
        	mSearch.poiSearchInCity(search_bundle.getString("CITY"), search_bundle.getString("ZOOM"));
        	search_bundle=null;
        }
        //���ݴ���bundle���������·�߲�ѯ�Ĵ�����
        if(route_bundle!=null){
        	
        	MKPlanNode staNode=new MKPlanNode();
        	staNode.name=route_bundle.getString("FROM");
        	MKPlanNode endNode=new MKPlanNode();
        	endNode.name=route_bundle.getString("TO");
        	switch(route_bundle.getInt("TYPE")){
        	case 1:mSearch.walkingSearch("�ɶ�", staNode, "�ɶ�", endNode);
        		break;
        	case 2:mSearch.drivingSearch("�ɶ�", staNode, "�ɶ�", endNode);
        		break;
        	case 3:mSearch.transitSearch("�ɶ�", staNode, endNode);
        		break;
        	}
        	route_bundle=null;
        }
        //��Ӳ�ѯ����·���¼�!!!��ģ����ʱ�����á�������N���޹�
        route_ok.setOnClickListener(new OnClickListener() {
			
			
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
	        	mSearch.busLineSearch("�人",route_name.getText().toString().trim());
	        	Log.i("SHOW-->",route_name.getText().toString().trim());
			}
		});
        //����ҳ������ӷ���Ŀ����Ｐ��Ӧ��������ʾͼ��
        mMapView.getOverlays().add(new OverItem(maker, this));
        mMapView.addView(mPopView, new MapView.LayoutParams(LayoutParams.WRAP_CONTENT,
        		LayoutParams.WRAP_CONTENT,null,MapView.LayoutParams.TOP_LEFT));
        //����PopView���ɼ�������ǲſɼ�
        mPopView.setVisibility(View.GONE);
        
    }
    //����������ߵ�ͼKey�쳣��
    class MyGeneralListener implements MKGeneralListener{

		
		public void onGetNetworkState(int iError) {
			// TODO Auto-generated method stub
			if(iError==MKEvent.ERROR_NETWORK_CONNECT)
			Toast.makeText(MyBaiduMapActivity.this,"��ǰ�����쳣����ʱ�޷��ṩ����", Toast.LENGTH_LONG).show();
		}

		
		public void onGetPermissionState(int iError) {
			// TODO Auto-generated method stub
			if(iError==MKEvent.ERROR_PERMISSION_DENIED){
				Toast.makeText(MyBaiduMapActivity.this, "�ٶȵ�ͼ��Ȩ��Key�������������ã�", Toast.LENGTH_LONG).show();
			}
			map_key=false;
		}
    	
    }
    //��ͼ��·�������
   
	protected boolean isRouteDisplayed() {
		// TODO Auto-generated method stub
		return false;
	}
    //��������ʱ����MapManager
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		mapManager.destroy();
		super.onDestroy();
	}
	//����������ʱ��ʱͣ��MapManager����ǰλ�������ʾ
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		mapManager.getLocationManager().removeUpdates(locationListener);
		mLocationOverlay.disableMyLocation();
		mLocationOverlay.disableCompass();
		mapManager.stop();
		super.onPause();
	}
	//����Ӻ�̨�ָ�ʱ��ʹ��ͼ����������ǰλ����ʾ�ȿ���
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		mapManager.getLocationManager().requestLocationUpdates(locationListener);
		mLocationOverlay.enableCompass();
		mLocationOverlay.enableMyLocation();
		super.onResume();
	}
	//����Ŀ�����������ͼƬ�ȱ�ע�ض�λ�õķ����������л��漰���������ݿ�
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
		//�Զ���һЩ�ֲ�������������drawable��context����
		public OverItem(Drawable maker,Context context) {
			super(boundCenterBottom(maker));
			this.maker=maker;
			this.context=context;
			
//			GeoPoint p1=new GeoPoint((int)(mlat1*1E6),(int)(mlong1*1E6));
//			GeoPoint p2=new GeoPoint((int)(mlat2*1E6),(int)(mlong2*1E6));
//			GeoPoint p3=new GeoPoint((int)(mlat3*1E6),(int)(mlong3*1E6));
//			
//			mGeoList.add(new OverlayItem(p1,"��һ��","����˭��"));
//			mGeoList.add(new OverlayItem(p2,"�ڶ���","���������"));
//			mGeoList.add(new OverlayItem(p3,"������","�ҽ�Ҫ��ʲô��"));
			
			populate();
		}

		@Override
		protected OverlayItem createItem(int arg0) {
			// TODO Auto-generated method stub
			return mGeoList.get(arg0);
		}
		//���ر����Ŀ������
		@Override
		public int size() {
			// TODO Auto-generated method stub
			return mGeoList.size();
		}
		//�������λ��������ʧ
		@Override
		public boolean onTap(GeoPoint arg0, MapView arg1) {
			// ��ȥ����������
			mPopView.setVisibility(View.GONE);
			
			return super.onTap(arg0, arg1);
		}
		//��������ʱ��������
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
		//����ͼ�����ֵȣ����ݵ�����
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

	
	//����menu�˵������ڵ���
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.menu_activity, menu);
		return true;
		
	}
	//����˵�����ʱ���¼�
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