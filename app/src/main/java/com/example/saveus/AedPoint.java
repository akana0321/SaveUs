package com.example.saveus;

public class AedPoint {
    private String rnum; // 순번
    private String org; // 설치장소명
    private double wgs84Lon; // 경도 값
    private double wgs84Lat; // 위도 값
    private String buildPlace; // 설치 대략적 위치
    private String buildAddress; // 설치 전체 주소
    private String clerkTel; // 설치기관 전화번호

    public AedPoint(){
        super();
    }
    public String getRnum(){ return  rnum; }
    public String getOrg() {return org;}
    public double getWgs84Lat() {  // 위도 값 가져오기
        return wgs84Lat;
    }
    public double getWgs84Lon() { // 경도 값 가져오기
        return wgs84Lon;
    }
    public String getBuildPlace() { // 설치장소 가져오기.
        return buildPlace;
    }
    public String getBuildAddress() { // 설치기관 주소 가져오기
        return buildAddress;
    }
    public String getClerkTel() {  // 설치기관 전화번호 가져오기
        return clerkTel;
    }



    public  void setRnum(String rnum){
        this.rnum = rnum;
    }

    public  void setOrg(String org){this.org = org;}
    public void setWgs84Lon(double wgs84Lon) { // 경도 값 설정하기
        this.wgs84Lon = wgs84Lon;
    }

    public void setWgs84Lat(double wgs84Lat) {  // 위도 값 설정하기
        this.wgs84Lat = wgs84Lat;
    }

    public void setBuildPlace(String buildPlace) { // 설치장소 가져오기.
        this.buildPlace= buildPlace;
    }

    public void setBuildAddress(String buildAddress) { // 설치기관 주소 설정하기
        this.buildAddress = buildAddress;
    }

    public void setClerkTel(String clerkTel) { // 설치기관 전화번호 가져오기
        this.clerkTel = clerkTel;
    }

}
