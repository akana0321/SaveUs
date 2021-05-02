package com.example.saveus;

public class AedPoint {
    private String wgs84Lon; // 경도 값
    private String wgs84Lat; // 위도 값
    private String buildPlace; // 설치 위치
    private String buildAddress; // 설치기관 주소
    private String clerkTel; // 설치기관 전화번호

    public AedPoint(){
        super();
    }

    public AedPoint(String wgs84Lon, String wgs84Lat, String buildPlace, String buildAddress, String clerkTel){
        this.wgs84Lon = wgs84Lon;
        this.wgs84Lat = wgs84Lat;
        this.buildPlace = buildPlace;
        this.buildAddress = buildAddress;
        this.clerkTel = clerkTel;
    }

    public String getWgs84Lat() {  // 위도 값 가져오기
        return wgs84Lat;
    }

    public String getbuildPlace() { // 설치장소 가져오기.
        return buildPlace;
    }

    public String getBuildAddress() { // 설치기관 주소 가져오기
        return buildAddress;
    }

    public String getClerkTel() {  // 설치기관 전화번호 가져오기
        return clerkTel;
    }

    public String getWgs84Lon() { // 경도 값 가져오기
        return wgs84Lon;
    }

    public void setWgs84Lon(String wgs84Lon) { // 경도 값 설정하기
        this.wgs84Lon = wgs84Lon;
    }

    public void setWgs84Lat(String wgs84Lat) {  // 위도 값 설정하기
        this.wgs84Lat = wgs84Lat;
    }

    public void setBuilPlace(String buildPlace) { // 설치장소 가져오기.
        this.buildPlace= buildPlace;
    }

    public void setBuildAddress(String buildAddress) { // 설치기관 주소 설정하기
        this.buildAddress = buildAddress;
    }

    public void setClerkTel(String clerkTel) { // 설치기관 전화번호 가져오기
        this.clerkTel = clerkTel;
    }
}
