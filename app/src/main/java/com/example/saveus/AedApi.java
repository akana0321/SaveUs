package com.example.saveus;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.net.URL;
import java.util.ArrayList;

public class AedApi {
    private static String ServiceKey = "zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D"; //공공데이터 사이트를 통해 발급 받은 API키

    public AedApi() {
        try {
            apiParserSearch();
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    public ArrayList<AedPoint> apiParserSearch() throws Exception {

        URL url = new URL(getURLParam(null));

        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        XmlPullParser xpp = factory.newPullParser();
        factory.setNamespaceAware(true);
        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis, "utf-8");


        String tag = null;
        int event_type = xpp.getEventType();

        ArrayList<AedPoint> mapPoint = new ArrayList<AedPoint>();

        String facility_wgs84Lon = null, facility_wgs84Lat = null; // 위도, 경도 값 초기화; // double형이 아닌 문자열로 우선 받음.
        String facility_buildPlace = null, facility_buildAddress = null, facility_clerkTel = null; // 설치 장소, 주소, 전화번호 초기화

        boolean bfacility_wgs84Lon = false, bfacility_wgs84Lat = false, bfacility_buildPlace = false, bfacility_buildAddress = false, bfacility_clerkTel = false;

        while (event_type != XmlPullParser.END_DOCUMENT) {
            if (event_type == XmlPullParser.START_TAG) {
                tag = xpp.getName();
                if (tag.equals("wgs84Lon")) {
                    bfacility_wgs84Lon = true;
                }
                if (tag.equals("wgs84Lat")) {
                    bfacility_wgs84Lat = true;
                }
                if (tag.equals("buildPlace")) {
                    bfacility_buildPlace = true;
                }
                if (tag.equals("buildAddress")) {
                    bfacility_buildAddress = true;
                }
                if (tag.equals("clerkTel")) {
                    bfacility_clerkTel = true;
                }
            } else if (event_type == XmlPullParser.TEXT) {
                if (bfacility_wgs84Lon == true) {
                    facility_wgs84Lon = xpp.getText();
                    bfacility_wgs84Lon = false;
                } else if (bfacility_wgs84Lat == true) {
                    facility_wgs84Lat = xpp.getText();
                    bfacility_wgs84Lat = false;
                } else if (bfacility_buildPlace == true) {
                    facility_buildPlace = xpp.getText();
                    bfacility_buildPlace = false;
                } else if (bfacility_buildAddress == true) {
                    facility_buildAddress = xpp.getText();
                    bfacility_buildAddress = false;
                } else if (bfacility_clerkTel == true) {
                    facility_clerkTel = xpp.getText();
                    bfacility_clerkTel = false;
                }

            } else if (event_type == XmlPullParser.END_TAG) {
                tag = xpp.getName();
                if (tag.equals("row")) {
                    AedPoint entity = new AedPoint();
                    entity.setWgs84Lon(Double.valueOf(facility_wgs84Lon));
                    entity.setWgs84Lat(Double.valueOf(facility_wgs84Lat));
                    entity.setBuilPlace(facility_buildPlace);
                    entity.setBuildAddress(facility_buildAddress);
                    entity.setClerkTel(facility_clerkTel);
                    mapPoint.add(entity);
                    System.out.println(mapPoint.size());
                }
            }
            event_type = xpp.next();
        }
        System.out.println(mapPoint.size());

        return mapPoint;
    }


    private String getURLParam(String search){
        String url = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?WGS84_LON=127.8730596106769&WGS84_LAT=36.97442747612218&pageNo=1&numOfRows=30&ServiceKey="+ServiceKey;
        return url;
    }

    public static void main(String[] args) {
        new AedApi();
    }
}









/*
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;

public class AedApi {

    private  static String ServiceKey = "zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D";
    //공공데이터 사이트를 통해서 발급 받은 인코딩 API 값 넣기.

    public  AedApi(){
        try{
            apiParserSearch(); // AED API의 정보를 파싱하는 메서드 호출.
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    public ArrayList<AedPoint> apiParserSearch() throws XmlPullParserException, IOException {
        ArrayList<AedPoint> aedPoint = new ArrayList<AedPoint>();
        XmlPullParserFactory xmlPullParserFactory = null;
        XmlPullParser xmlPullParser = null;

        URL url = new URL(getURLParam(null)); // AED API_요청 URL 값을 불러오는 메소드.
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        factory.setNamespaceAware(true);
        XmlPullParser xpp = factory.newPullParser();



        BufferedInputStream bis = new BufferedInputStream(url.openStream());
        xpp.setInput(bis,"UTF-8");

        String tag = null;
        int event_type = xpp.getEventType();

        String facility_wgs84Lon = null, facility_wgs84Lat = null; // 위도, 경도 값 초기화; // double형이 아닌 문자열로 우선 받음.
        String facility_builPlace = null, facility_buildAddress = null , facility_clerkTel =null; // 설치 장소, 주소, 전화번호 초기화

        boolean bfacility_wgs84Lon  = false, bfacility_wgs84Lat = false, bfacility_builPlace =false,  bfacility_buildAddress =false, bfacility_clerkTel = false;
        // 불리언 값으로 지정하여 1차로 확인 하기위해 사용.

        while (event_type != XmlPullParser.END_DOCUMENT){
            if(event_type == XmlPullParser.START_TAG){ // 처음 xml 각 항목의 일치 여부 조건문
                tag = xpp.getName();
                if(tag.equals("wgs84Lon")){ // 태그 값이 경도 값이면 참으로 변경.
                    bfacility_wgs84Lon = true;
                }
                if(tag.equals("wgs84Lat")){ // 태그 값이 위도 값이면 참으로 변경.
                    bfacility_wgs84Lat = true;
                }
                if(tag.equals("builPlace")){ // 태그 값이 설치 장소 값이면 참으로 변경.
                    bfacility_builPlace = true;
                }
                if(tag.equals("buildAddress")){ // 태그 값이 경도 값이면 참으로 변경.
                    bfacility_buildAddress = true;
                }
                if(tag.equals("clerkTel")){ // 태그 값이 경도 값이면 참으로 변경.
                    bfacility_clerkTel = true;
                }

            }else if (event_type == XmlPullParser.TEXT){
                if(bfacility_wgs84Lon == true){
                    facility_wgs84Lon = xpp.getText();
                    bfacility_wgs84Lon =false;
                }
            }else if (event_type == XmlPullParser.TEXT){
                if(bfacility_wgs84Lat == true){
                    facility_wgs84Lat = xpp.getText();
                    bfacility_wgs84Lat =false;
                }
            }else if (event_type == XmlPullParser.TEXT) {
                if (bfacility_builPlace == true) {
                    facility_builPlace = xpp.getText();
                    bfacility_builPlace = false;
                }
            }else if (event_type == XmlPullParser.TEXT) {
                if (bfacility_buildAddress == true) {
                    facility_buildAddress = xpp.getText();
                    bfacility_buildAddress = false;
                }
            }else if (event_type == XmlPullParser.TEXT) {
                if (bfacility_clerkTel == true) {
                    facility_clerkTel= xpp.getText();
                    bfacility_clerkTel = false;
                }
            }else if (event_type == XmlPullParser.END_TAG){
                tag = xpp.getName();
                if(tag.equals("item")){  //AED API 데이터가 들어있는 태그명 작성.
                    AedPoint entity = new AedPoint();
                    entity.setWgs84Lat(Double.valueOf(facility_wgs84Lat));
                    entity.setWgs84Lon(Double.valueOf(facility_wgs84Lon));
                    entity.setBuilPlace(facility_builPlace);
                    entity.setBuildAddress(facility_buildAddress);
                    entity.setClerkTel(facility_clerkTel);
                    System.out.println(aedPoint.size());
                }
            }
            event_type =xpp.next();
        }
        System.out.println(aedPoint.size());
        return aedPoint;
    }


    private  String getURLParam(String search){    // AED API를 요청하는 URL를 호출 하는 메서드 그리고 페이지 1page 에서 numOfRows를 30개로 설정하여,
                                                  // 즉 한페이지 안에 근처 자동제세동기 정보를 30개정도 출력.
        String url ="http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?WGS84_LON=127.8730596106769&WGS84_LAT=36.97442747612218&pageNo=1"+
                "&numOfRows=30&ServiceKey=zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D";
        return url;

    }
    public  static  void main(String[] args){
        new AedApi();
    }
}
*/