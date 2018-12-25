package honbab.voltage.com.tete;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import net.daum.mf.map.api.CalloutBalloonAdapter;
import net.daum.mf.map.api.MapPOIItem;
import net.daum.mf.map.api.MapPoint;
import net.daum.mf.map.api.MapView;
import net.daum.mf.map.api.MapView.MapViewEventListener;

import honbab.voltage.com.data.MapApiConst;

public class DaumMapActivity extends FragmentActivity implements MapViewEventListener {
    //implements MapViewEventListener
    //MapView.CurrentLocationEventListener

    private static final String LOG_TAG = "abc";

    private MapView mMapView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_daummap);

        mMapView = (MapView) findViewById(R.id.map_view);
//        mMapView.setDaumMapApiKey(MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY);
        mMapView.setMapViewEventListener(this);

//        mMapView.setCurrentLocationEventListener(this);

        Log.e("abc", "다음지도1 xxxxxxxxxxxxxxxxxx");

        TextView btn_search = (TextView) findViewById(R.id.btn_search);
        btn_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String query = mEditTextQuery.getText().toString();
//                if (query == null || query.length() == 0) {
//                    showToast("검색어를 입력하세요.");
//                    return;
//                }
//                hideSoftKeyboard(); // 키보드 숨김
                MapPoint.GeoCoordinate geoCoordinate = mMapView.getMapCenterPoint().getMapPointGeoCoord();
                double latitude = geoCoordinate.latitude; // 위도
                double longitude = geoCoordinate.longitude; // 경도
                int radius = 10000; // 중심 좌표부터의 반경거리. 특정 지역을 중심으로 검색하려고 할 경우 사용. meter 단위 (0 ~ 10000)
                int page = 1; // 페이지 번호 (1 ~ 3). 한페이지에 15개
                String apikey = MapApiConst.DAUM_MAPS_ANDROID_APP_API_KEY;

//                Searcher searcher = new Searcher(); // net.daum.android.map.openapi.search.Searcher
//                searcher.searchKeyword(getApplicationContext(), query, latitude, longitude, radius, page, apikey, new OnFinishSearchListener() {
//                    @Override
//                    public void onSuccess(List<ClipData.Item> itemList) {
//                        mMapView.removeAllPOIItems(); // 기존 검색 결과 삭제
//                        showResult(itemList); // 검색 결과 보여줌
//                    }
//
//                    @Override public void onFail() {
//                       showToast("API_KEY의 제한 트래픽이 초과되었습니다.");
//                    }
//                });
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
//        mMapView.setCurrentLocationTrackingMode(MapView.CurrentLocationTrackingMode.TrackingModeOff);
//        mMapView.setShowCurrentLocationMarker(false);
    }


    @Override
    public void onMapViewInitialized(MapView mapView) {
        Log.e("abc", "다음지도2 xxxxxxxxxxxxxxxxxx");



        MapPoint myPoint = MapPoint.mapPointWithGeoCoord(37.537094, 127.005470);
        mapView.setMapCenterPoint(myPoint, true);

        MapPOIItem customMarker = new MapPOIItem();
        customMarker.setItemName("Custom Marker");
        customMarker.setTag(1);
        customMarker.setMapPoint(myPoint);
        customMarker.setMarkerType(MapPOIItem.MarkerType.CustomImage); // 마커타입을 커스텀 마커로 지정.
        customMarker.setCustomImageResourceId(R.drawable.custom_arrow_map_present_tracking); // 마커 이미지.
        customMarker.setCustomImageAutoscale(false); // hdpi, xhdpi 등 안드로이드 플랫폼의 스케일을 사용할 경우 지도 라이브러리의 스케일 기능을 꺼줌.
        customMarker.setCustomImageAnchor(0.5f, 1.0f); // 마커 이미지중 기준이 되는 위치(앵커포인트) 지정 - 마커 이미지 좌측 상단 기준 x(0.0f ~ 1.0f), y(0.0f ~ 1.0f) 값.

        mMapView.addPOIItem(customMarker);



        mMapView.setCalloutBalloonAdapter(new CustomCalloutBalloonAdapter());

//        mMapView.setShowCurrentLocationMarker(true);
    }

    @Override
    public void onMapViewCenterPointMoved(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewZoomLevelChanged(MapView mapView, int i) {

    }

    @Override
    public void onMapViewSingleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDoubleTapped(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewLongPressed(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragStarted(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewDragEnded(MapView mapView, MapPoint mapPoint) {

    }

    @Override
    public void onMapViewMoveFinished(MapView mapView, MapPoint mapPoint) {

    }










    // CalloutBalloonAdapter 인터페이스 구현
    class CustomCalloutBalloonAdapter implements CalloutBalloonAdapter {
        private final View mCalloutBalloon;

        public CustomCalloutBalloonAdapter() {
            mCalloutBalloon = getLayoutInflater().inflate(R.layout.balloon_marker, null);
        }

        @Override
        public View getCalloutBalloon(MapPOIItem poiItem) {
            ((ImageView) mCalloutBalloon.findViewById(R.id.badge)).setImageResource(R.drawable.icon_noprofile_circle);
            ((TextView) mCalloutBalloon.findViewById(R.id.title)).setText(poiItem.getItemName());
            ((TextView) mCalloutBalloon.findViewById(R.id.desc)).setText("Custom CalloutBalloon");
            return mCalloutBalloon;
        }

        @Override
        public View getPressedCalloutBalloon(MapPOIItem poiItem) {
            return null;
        }
    }

}
