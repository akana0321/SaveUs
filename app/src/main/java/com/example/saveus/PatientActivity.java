package com.example.saveus;

import android.content.Intent;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.android.material.bottomnavigation.BottomNavigationView;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.net.URL;

public class PatientActivity extends MainActivity {
    TextView status1;
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient);
        actList.add(this);  // 메인의 Activity List에 추가
        setTitle("환자 상태 파악");

        //StrictMode.enableDefaults();
        status1 = (TextView) findViewById(R.id.result);

/*
        boolean initem = false, inRnum = false, inwgs84Lon = false, inWgs84Lat = false, inOrg = false, inMfg = false;
        boolean inbuildPlace = false, inclerkTel = false, inbuildAddress = false, inManager = false, inmanagerTel = false;
        boolean inModel = false, inZipcode1 = false, inZipcode2 = false, inCnt = false, inDistance = false;

        String rnum = null;
        String wgs84Lon = null, wgs84Lat = null, org = null, mfg = null, buildPlace = null, clerkTel = null, buildAddress = null, manager = null;
        String managerTel = null, model = null, zipcode1 = null, zipcode2 = null, cnt = null, distance = null;


        try{
            URL url = new URL("http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?WGS84_LON=127.8730596106769&WGS84_LAT=36.97442747612218&pageNo=1&numOfRows=1&" +
                    "ServiceKey=zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D"
            ); //검색 URL부분

            XmlPullParserFactory parserCreator = XmlPullParserFactory.newInstance();
            XmlPullParser parser = parserCreator.newPullParser();

            parser.setInput(url.openStream(), null);

            int parserEvent = parser.getEventType();
            System.out.println("파싱시작합니다.");

            while (parserEvent != XmlPullParser.END_DOCUMENT){
                switch(parserEvent){
                    case XmlPullParser.START_TAG: //parser가 시작 태그를 만나면 실행
                        if(parser.getName().equals("rnum")){
                            inRnum =true;
                        }
                        if(parser.getName().equals("wgs84Lon")){ //wgs84Lon 만나면 내용을 받을수 있게 하자
                            inwgs84Lon = true;
                        }
                        if(parser.getName().equals("wgs84Lat")){ //wgs84Lat 만나면 내용을 받을수 있게 하자
                            inWgs84Lat = true;
                        }
                        if(parser.getName().equals("org")){ //org 만나면 내용을 받을수 있게 하자
                            inOrg = true;
                        }
                        if(parser.getName().equals("mfg")){ //mfg 만나면 내용을 받을수 있게 하자
                            inMfg = true;
                        }
                        if(parser.getName().equals("buildPlace")){ //buildPlace 만나면 내용을 받을수 있게 하자
                            inbuildPlace = true;
                        }
                        if(parser.getName().equals("clerkTel")){ //clerkTel 만나면 내용을 받을수 있게 하자
                            inclerkTel = true;
                        }
                        if(parser.getName().equals("buildAddress")){ //buildAddress 만나면 내용을 받을수 있게 하자
                            inbuildAddress= true;
                        }
                        if(parser.getName().equals("manager")){ //manager 만나면 내용을 받을수 있게 하자
                            inManager = true;
                        }
                        if(parser.getName().equals("managerTel")){ //managerTel 만나면 내용을 받을수 있게 하자
                            inmanagerTel = true;
                        }
                        if(parser.getName().equals("model")){ //model 만나면 내용을 받을수 있게 하자
                            inModel = true;
                        }
                        if(parser.getName().equals("zipcode1")){ //zipcode1 만나면 내용을 받을수 있게 하자
                            inZipcode1= true;
                        }
                        if(parser.getName().equals("zipcode2")){ //zipcode2  만나면 내용을 받을수 있게 하자
                            inZipcode2 = true;
                        }
                        if(parser.getName().equals("cnt")){ //cnt 만나면 내용을 받을수 있게 하자
                            inCnt = true;
                        }
                        if(parser.getName().equals("distance")){ //distance 만나면 내용을 받을수 있게 하자
                            inDistance = true;
                        }
                        if(parser.getName().equals("message")){ //message 태그를 만나면 에러 출력
                            status1.setText(status1.getText()+"에러");
                            //여기에 에러코드에 따라 다른 메세지를 출력하도록 할 수 있다.
                        }
                        break;

                    case XmlPullParser.TEXT://parser가 내용에 접근했을때
                        if(inRnum){ //inRnum이 true일 때 태그의 내용을 저장.
                            rnum = parser.getText();
                            inRnum = false;
                        }
                        if(inWgs84Lat){ //inWgs84Lat가 true일 때 태그의 내용을 저장.
                            wgs84Lat = parser.getText();
                            inWgs84Lat = false;
                        }
                        if(inwgs84Lon){ //inwgs84Lon이 true일 때 태그의 내용을 저장.
                            wgs84Lon= parser.getText();
                            inwgs84Lon = false;
                        }
                        if(inOrg){ //inOrg이 true일 때 태그의 내용을 저장.
                            org = parser.getText();
                            inOrg = false;
                        }
                        if(inMfg){ //inMfg이 true일 때 태그의 내용을 저장.
                            mfg = parser.getText();
                            inMfg = false;
                        }
                        if(inbuildPlace){ //inbuildPlace이 true일 때 태그의 내용을 저장.
                            buildPlace = parser.getText();
                            inbuildPlace = false;
                        }
                        if(inclerkTel){ //inclerkTel 이 true일 때 태그의 내용을 저장.
                            clerkTel = parser.getText();
                            inclerkTel= false;
                        }
                        if(inbuildAddress){ //inbuildAddress이 true일 때 태그의 내용을 저장.
                            buildAddress = parser.getText();
                            inbuildAddress= false;
                        }
                        if(inManager){ //inManager이 true일 때 태그의 내용을 저장.
                            manager = parser.getText();
                            inManager = false;
                        }
                        if(inmanagerTel){ //inmanagerTel이 true일 때 태그의 내용을 저장.
                            managerTel = parser.getText();
                            inmanagerTel = false;
                        }
                        if(inModel){ //inModel 이 true일 때 태그의 내용을 저장.
                            model = parser.getText();
                            inModel = false;
                        }
                        if(inZipcode1){ //inZipcode1이 true일 때 태그의 내용을 저장.
                            zipcode1 = parser.getText();
                            inZipcode1 = false;
                        }
                        if(inZipcode2){ //inZipcode2 이 true일 때 태그의 내용을 저장.
                            zipcode2 = parser.getText();
                            inZipcode2 = false;
                        }
                        if(inCnt){ //inCnt이 true일 때 태그의 내용을 저장.
                            cnt = parser.getText();
                            inCnt= false;
                        }
                        if(inDistance){ //inDistance이 true일 때 태그의 내용을 저장.
                            distance = parser.getText();
                            inDistance= false;
                        }
                        break;
                    case XmlPullParser.END_TAG:
                        if(parser.getName().equals("item")){
                            status1.setText(status1.getText()+"제조번호 : "+ rnum +"\n 경도: "+ wgs84Lon +"\n 위도 : " + wgs84Lat
                                    +"\n 설치기관 명 : " + org +  "\n  제조사: " + mfg + "\n 설치 위치: " + buildPlace
                                    +"\n 설치기관 전화번호 : " + clerkTel + "\n 설치기관 주소 : " + buildAddress + "\n 관리 책임자명 " +manager
                                    +"\n 관리자 연락처: " + managerTel +"\n AED 모델명 : " +model+ "\n 우편번호(앞자리) :" +zipcode1+ "\n 우편번호(뒷자리) :" +zipcode2
                                    + "\n 건수 :" + cnt + "\n 거리 " +distance);
                            initem = false;
                        }
                        break;
                }
                parserEvent = parser.next();
            }
        } catch(Exception e){
            status1.setText("에러가..났습니다...");
        }
    }

*/
        /****************************************************
         ****************** 변수 선언부 *********************
         ****************************************************/

        /****************************************************
         *************** 인텐트 변환 메서드 ******************
         ****************************************************/
        /* 20210412 - 아직 구현이 되지 않아서 바텀 네비게이션 살려두면 어플이 사망함
        // 바텀 네이게이션 각 버튼 클릭시 실행.
        BottomNavigationView frBottom = (BottomNavigationView) findViewById(R.id.frBottom);
        frBottom.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.frBack :
                        finish();
                        return true;
                    case R.id.frMain :
                        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                        // 활성화 되어 있는 모든 인텐트 삭제
                        for (int i = 0; i < actList.size(); i++)
                            actList.get(i).finish();
                        startActivity(intent);
                        finish();
                        return true;
                    case R.id.frExit :
                        moveTaskToBack(true); // 태스크를 백그라운드로 이동
                        finishAndRemoveTask(); // 액티비티 종료 + 태스크 리스트에서 지우기
                        System.exit(0);
                        return true;
                }
                return false;
            }
        });
         */
        /*
        @Override
        public boolean onOptionsItemSelected(MenuItem item) {
            // Handle presses on the action bar items
            switch (item.getItemId()) {
                case R.id.tbEmer :
                    Intent intent = new Intent(getApplicationContext(), EmergencyActivity.class);
                    startActivity(intent); // 응급처치 기능 클릭시 페이지 전환
                    return true;
                case R.id.tbAed:
                    intent = new Intent(getApplicationContext(), AedActivity.class);
                    startActivity(intent); //자동제세동기 기능 클릭시 페이지 전환
                    return true;
                case R.id.tbMoun:
                    intent = new Intent(getApplicationContext(), MountainActivity.class);
                    startActivity(intent); //등산중 사고 신고 클릭시 페이지 전환
                    return true;
                case R.id.tbPati:
                    intent = new Intent(getApplicationContext(), PatientActivity.class);
                    startActivity(intent); //환자 상태파악 클릭시 페이지 전환
                    return true;
                case R.id.tbCont:
                    intent = new Intent(getApplicationContext(), ContactActivity.class);
                    startActivity(intent); //문의하기 기능클릭시 페이지 전환
                    return true;
                default:
                    return super.onOptionsItemSelected(item);
            }*/
        }


    }
