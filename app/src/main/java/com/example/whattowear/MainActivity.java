package com.example.whattowear;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private GpsTracker gpsTracker;
    String key = "";

    ArrayList<WeatherInfoData> widArray = new ArrayList<WeatherInfoData>();

    private static final int GPS_ENABLE_REQUEST_CODE = 2001;
    private static final int PERMISSIONS_REQUEST_CODE = 100;
    String[] REQUIRED_PERMISSIONS = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ImageButton searchButton = (ImageButton)findViewById(R.id.searchButton);
        ImageButton settingButton = (ImageButton)findViewById(R.id.settingButton);
        TextView resultView = (TextView)findViewById(R.id.resultview);

        //검색 아이콘 클릭
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        //설정 아이콘
        settingButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });

        if(!checkLocationServicesStatus()) {
            showDialogForLocationServicesSetting();
        } else {
            checkRunTimePermission();
        }
        Button locationTestButton = (Button) findViewById(R.id.locationTestButton);
        locationTestButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                gpsTracker = new GpsTracker(MainActivity.this);

                int latitude = (int) gpsTracker.getLatitude();
                int longitude = (int) gpsTracker.getLongitude();

                String address = getCurrentAddress(latitude, longitude);
                Toast.makeText(MainActivity.this, "현재위치 \n위도" + latitude + "경도 " + longitude, Toast.LENGTH_LONG).show();
            }
        });

        showWeather();
    }

    void showWeather() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                getXmlData();


                for(int i = 0; i<widArray.size(); i++){
                    Log.d("여기는 런", "i = " + i);
                    Log.d("여기는 런"," 강수확률? " + widArray.get(i).getRainP() + " 입니다." );
                    Log.d("여기는 런"," 습도? " + widArray.get(i).getHumidity() + " 입니다." );
                    Log.d("여기는 런"," 구름? " + widArray.get(i).getSky() + " 입니다." );
                    Log.d("여기는 런"," 온도? " + widArray.get(i).getTempature() + " 입니다." );
                    Log.d("여기는 런"," 풍속? " + widArray.get(i).getWindSpeed() + " 입니다." );
                    Log.d("여기는 런"," 날씨? " + widArray.get(i).getRain() + " 입니다." );
                }

            }
        }).start();
    }
    //파싱
    void getXmlData() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        Log.d("시간", "현재 시간 : " +  date.getTime());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
        String getDate = sdf.format(date);

        String queryUrl = "http://apis.data.go.kr/1360000/VilageFcstInfoService/getVilageFcst?serviceKey=" + key + "&numOfRows=100&pageNo=1&base_date=" + getDate + "&base_time=0200&nx=51&ny=38";
        Log.v("태그", "url" + queryUrl);

        //파싱할때만 쓰이는 임시 items
        //ArrayList<Item> tmpItmes = new ArrayList<Item>();
        try {
            URL url = null;

            url = new URL(queryUrl);
            InputStream is = url.openStream();

            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new InputStreamReader(is, "UTF-8"));


            Log.v("태그", "api 전송후");

            xpp.next();
            int eventType = xpp.getEventType();
            int count=0;
            String category="";
            int rainP = 0, humidity = 0, sky = 0, tempature = 0, rain = 0;
            double windSpeed = 0;


            while (eventType != XmlPullParser.END_DOCUMENT) {
                String tag;
                switch (eventType) {
                    case XmlPullParser.START_DOCUMENT:
                        break;
                    case XmlPullParser.START_TAG:

                        tag = xpp.getName();

                        if (tag.equals("category")) {
                            // 카테고리다 팝이면 새거 아니면 값을 가져오쟈
                            xpp.next();
                            category = xpp.getText();
                            Log.d("api테스트", "카테고리 : " + category);
                            if(category.equals("POP")){
                                Log.d("------","팝나와따" + count);
                                count = count + 1;
                                if(count > 1){
                                    Log.d("여기는 배열에 추가하기 전","비확률" + rainP);
                                    Log.d("여기는 배열에 추가하기 전","습도" + humidity);
                                    Log.d("여기는 배열에 추가하기 전","날씨" + sky);
                                    Log.d("여기는 배열에 추가하기 전","기온" + tempature);
                                    Log.d("여기는 배열에 추가하기 전","풍속" + windSpeed);
                                    Log.d("여기는 배열에 추가하기 전","풍속" + rain);

                                    widArray.add(new WeatherInfoData(sky,humidity,tempature,rainP,windSpeed,rain));
                                }
                                rainP = 0;
                                humidity = 0;
                                sky = 0;
                                tempature = 0;
                                rain = 0;
                                windSpeed = 0;

                            }
                        } else if (tag.equals("fcstValue")) {
                            // 값이다 어딘가 저장하자
                            xpp.next();
                            String stringValue = xpp.getText();

                            Log.d("값 출력해봄 ","" + category + " : " + stringValue);
                            if(category.equals("POP")){
                                rainP = Integer.parseInt(stringValue);
                            }else if(category.equals("REH")){
                                humidity = Integer.parseInt(stringValue);
                            }else if(category.equals("SKY")){
                                sky =  Integer.parseInt(stringValue);
                            }else if(category.equals("T3H")){
                                tempature =  Integer.parseInt(stringValue);
                            }else if(category.equals("PTY")){
                                rain =  Integer.parseInt(stringValue);
                            }else if(category.equals("WSD")){
                                double doubleValue = Double.parseDouble(stringValue);
                                windSpeed = doubleValue;
                            }
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        tag = xpp.getName();
                        if (tag.equals("item")){
                            // 아이템 하나 끝
                            //tmpItmes.add(new Item(hospitalName, hospitalAddr)); //api로 받아온 정보 객체로 저장. 이거를 itemArrayList에 저장.
                        }
                        break;
                }
                eventType = xpp.next();
            }

        } catch(Exception e){
            Log.v("태그", "e = " + e);
        }
        //임시 아이템을 items에게 넘겨줌
        //itemArrayList = tmpItmes;
    }

    //날씨 배경 바꾸는 함수
    void chageBackground() {
        long now = System.currentTimeMillis();
        Date date = new Date(now);
        SimpleDateFormat sdf = new SimpleDateFormat("hh");
        String getDate = sdf.format(date);

        int nowHour = Integer.parseInt(getDate);
        int result = nowHour / 3 -1;
        if(result < 0){
            result = 0;
        }
        WeatherInfoData nowData = widArray.get(result);
        //비 오는지 안오는지 체크
        if(nowData.getRain() == 0){ //비가 안올때
            switch (nowData.getSky()){
                case 1:
                    //날씨 맑음 하늘색
                    break;
                case 3:
                    //구름 많음 흐림보다는 밝은색
                    break;
                case 4:
                    //흐림 회색
                    break;
            }
        } else {
            //비가 옴 회색
        }
    }

    /*
     * ActivityCompat.requestPermissions를 사용한 퍼미션 요청의 결과를 리턴받는 메소드입니다.
     */
    @Override
    public void onRequestPermissionsResult(int permsRequestCode,
                                           @NonNull String[] permissions,
                                           @NonNull int[] grandResults) {

        if ( permsRequestCode == PERMISSIONS_REQUEST_CODE && grandResults.length == REQUIRED_PERMISSIONS.length) {
            // 요청 코드가 PERMISSIONS_REQUEST_CODE 이고, 요청한 퍼미션 개수만큼 수신되었다면
            boolean check_result = true;

            // 모든 퍼미션을 허용했는지 체크합니다.
            for (int result : grandResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    check_result = false;
                    break;
                }
            }

            if ( check_result ) {
                //위치 값을 가져올 수 있음
                ;
            }
            else {
                // 거부한 퍼미션이 있다면 앱을 사용할 수 없는 이유를 설명해주고 앱을 종료합니다.2 가지 경우가 있습니다.
                if (ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[0])
                        || ActivityCompat.shouldShowRequestPermissionRationale(this, REQUIRED_PERMISSIONS[1])) {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 앱을 다시 실행하여 퍼미션을 허용해주세요.", Toast.LENGTH_LONG).show();
                    finish();
                }else {
                    Toast.makeText(MainActivity.this, "퍼미션이 거부되었습니다. 설정(앱 정보)에서 퍼미션을 허용해야 합니다. ", Toast.LENGTH_LONG).show();
                }
            }

        }
    }

    void checkRunTimePermission(){
        //런타임 퍼미션 처리
        // 1. 위치 퍼미션을 가지고 있는지 체크합니다.
        int hasFineLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_FINE_LOCATION);
        int hasCoarseLocationPermission = ContextCompat.checkSelfPermission(MainActivity.this,
                Manifest.permission.ACCESS_COARSE_LOCATION);


        if (hasFineLocationPermission == PackageManager.PERMISSION_GRANTED &&
                hasCoarseLocationPermission == PackageManager.PERMISSION_GRANTED) {

            // 2. 이미 퍼미션을 가지고 있다면
            // ( 안드로이드 6.0 이하 버전은 런타임 퍼미션이 필요없기 때문에 이미 허용된 걸로 인식합니다.)

            // 3.  위치 값을 가져올 수 있음

        } else {  //2. 퍼미션 요청을 허용한 적이 없다면 퍼미션 요청이 필요합니다. 2가지 경우(3-1, 4-1)가 있습니다.

            // 3-1. 사용자가 퍼미션 거부를 한 적이 있는 경우에는
            if (ActivityCompat.shouldShowRequestPermissionRationale(MainActivity.this, REQUIRED_PERMISSIONS[0])) {

                // 3-2. 요청을 진행하기 전에 사용자가에게 퍼미션이 필요한 이유를 설명해줄 필요가 있습니다.
                Toast.makeText(MainActivity.this, "이 앱을 실행하려면 위치 접근 권한이 필요합니다.", Toast.LENGTH_LONG).show();
                // 3-3. 사용자게에 퍼미션 요청을 합니다. 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            } else {
                // 4-1. 사용자가 퍼미션 거부를 한 적이 없는 경우에는 퍼미션 요청을 바로 합니다.
                // 요청 결과는 onRequestPermissionResult에서 수신됩니다.
                ActivityCompat.requestPermissions(MainActivity.this, REQUIRED_PERMISSIONS,
                        PERMISSIONS_REQUEST_CODE);
            }

        }

    }

    public String getCurrentAddress( int latitude, int longitude) {

        //지오코더... GPS를 주소로 변환
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());

        List<Address> addresses;

        try {
            addresses = geocoder.getFromLocation(
                    latitude,
                    longitude,
                    7);
        } catch (IOException ioException) {
            //네트워크 문제
            Toast.makeText(this, "지오코더 서비스 사용불가", Toast.LENGTH_LONG).show();
            return "지오코더 서비스 사용불가";
        } catch (IllegalArgumentException illegalArgumentException) {
            Toast.makeText(this, "잘못된 GPS 좌표", Toast.LENGTH_LONG).show();
            return "잘못된 GPS 좌표";
        }

        if (addresses == null || addresses.size() == 0) {
            Toast.makeText(this, "주소 미발견", Toast.LENGTH_LONG).show();
            return "주소 미발견";
        }
        Address address = addresses.get(0);
        return address.getAddressLine(0).toString()+"\n";
    }

    //GPS활성화를 위한 메소드들
    private void showDialogForLocationServicesSetting() {
        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("위치 서비스 비활성화");
        builder.setMessage("앱을 사용하기 위해서는 위치 서비스가 필요합니다.\n"+"위치 설정을 수정하시겠습니까?");
        builder.setCancelable(true);
        builder.setPositiveButton("설정", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                    Intent callGPSSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivityForResult(callGPSSettingIntent, GPS_ENABLE_REQUEST_CODE);
                }
        });
        builder.setNegativeButton("취소", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.create().show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case GPS_ENABLE_REQUEST_CODE:
                //사용자가 GPS 활성 시켰는지 검사
                if (checkLocationServicesStatus()) {
                    if (checkLocationServicesStatus()) {
                        Log.d("@@@", "onActivityResult : GPS 활성화 되있음");
                        checkRunTimePermission();
                        return;
                    }
                }
                break;
        }
    }

    public boolean checkLocationServicesStatus() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
                || locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
    }
}