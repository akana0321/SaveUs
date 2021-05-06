package com.example.saveus;

import android.os.AsyncTask;
import android.util.Log;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import java.io.IOException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class Test_AED_Multiple_Passing_ {

    // tag값의 정보를 가져오는 메소드
    private static String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if(nValue == null)
            return null;
        return nValue.getNodeValue();
    }

    public static void main(String[] args) {
        int page = 1;	// 페이지 초기값
        try{
            while(true){

                //# 1. 파싱할 URL 준비
                // parsing 할 url 지정(API 키 포함해서)
                String url ="http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?WGS84_LON=127.8730596106769&WGS84_LAT=36.97442747612218&pageNo="+page +
                        "&numOfRows=1&ServiceKey=zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D";

                //# 2. 페이지에 접근해줄 Document객체 생성
                DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
                DocumentBuilder dBuilder = dbFactoty.newDocumentBuilder();
                Document doc = dBuilder.parse(url);

                //# 3. 파싱할 정보가 있는 tag에 접근
                // root tag 최상위 계층 요소 출력,
                doc.getDocumentElement().normalize();
                System.out.println("Root element :" + doc.getDocumentElement().getNodeName());


                //# 4. list에 담긴 데이터 출력하기
                // 파싱할 tag
                NodeList nList = doc.getElementsByTagName("item");
                //System.out.println("파싱할 리스트 수 : "+ nList.getLength());

                for(int temp = 0; temp < nList.getLength(); temp++){
                    Node nNode = nList.item(temp);
                    if(nNode.getNodeType() == Node.ELEMENT_NODE){

                        Element eElement = (Element) nNode;
                        System.out.println("######################");
                        //System.out.println(eElement.getTextContent());
                        System.out.println("경도 : " + getTagValue("wgs84Lon", eElement));
                        System.out.println("위도 : " + getTagValue("wgs84Lat", eElement));
                        System.out.println("설치위치 : " + getTagValue("buildPlace", eElement));
                        System.out.println("설치기관 주소: " + getTagValue("buildAddress", eElement));
                        System.out.println("설치기관 전화번호: " + getTagValue("clerkTel", eElement));

                    }	// for end
                }	// if end

                page += 1;
                System.out.println("page number : "+page);
                if(page > 12){ // 총 13건의 목록만 출력하기.
                    break;
                }
            }	// while end

        } catch (Exception e){
            e.printStackTrace();
        }	// try~catch end
    }	// main end
}	// class end



/*
public class Test_AED_Multiple_Passing_ extends AsyncTask<Void,Void,String> {

    private String url;

    public Test_AED_Multiple_Passing_(String url) {
        this.url = url;
    }

    @Override
    protected String doInBackground(Void... voids) {

        String url = "http://apis.data.go.kr/B552657/AEDInfoInqireService/getAedLcinfoInqire?WGS84_LON=127.8730596106769&WGS84_LAT=36.97442747612218&pageNo=1&numOfRows=1&" +
                "ServiceKey=zsXZlLKntZ%2FCbsosKL7YaKFv15A2gTyu4Mf81dNY2rXE9H5GUzInG3VXV8c8EIW8g3qI3DJrhw88FzXegbFcGA%3D%3D";

        Test_AED_Multiple_Passing_ dust = new Test_AED_Multiple_Passing_(url);
        dust.execute();

        DocumentBuilderFactory dbFactoty = DocumentBuilderFactory.newInstance();
        DocumentBuilder dBuilder = null;

        try {
            dBuilder = dbFactoty.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            e.printStackTrace();
        }
        Document doc = null;

        try {
            doc = dBuilder.parse(url);
        } catch (IOException | SAXException e) {
            e.printStackTrace();
        }

        doc.getDocumentElement().normalize();
        System.out.println("Root element: " + doc.getDocumentElement().getNodeName()); // Root element: result

        // 파싱할 tag
        NodeList nList = doc.getElementsByTagName("item");

        for (int temp = 0; temp < nList.getLength(); temp++) {
            Node nNode = nList.item(temp);
            if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                Element eElement = (Element) nNode;
                Log.d("OPEN_AED", "경도 : " + getTagValue("wgs84Lon", eElement));
                Log.d("OPEN_AED", "위도 : " + getTagValue("wgs84Lat", eElement));
                Log.d("OPEN_AED", "설치위치 : " + getTagValue("buildPlace", eElement));
                Log.d("OPEN_AED", "설치기관 주소: " + getTagValue("buildAddress", eElement));
                Log.d("OPEN_AED","설치기관 전화번호: " + getTagValue("clerkTel", eElement));

            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(String str) {
        super.onPostExecute(str);
    }


    private String getTagValue(String tag, Element eElement) {
        NodeList nlList = eElement.getElementsByTagName(tag).item(0).getChildNodes();
        Node nValue = (Node) nlList.item(0);
        if (nValue == null)
            return null;
        return nValue.getNodeValue();
    }

}
*/
