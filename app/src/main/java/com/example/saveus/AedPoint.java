package com.example.saveus;

public class AedPoint {
    private String rnum; // 순번
    private String wgs84Lon; // 경도 값
    private String wgs84Lat; // 위도 값
    private String buildPlace; // 설치 위치
    private String buildAddress; // 설치기관 주소
    private String clerkTel; // 설치기관 전화번호

    public AedPoint(){
        super();
    }

    public String getRnum(){ return  rnum; }
    public String getWgs84Lat() {  // 위도 값 가져오기
        return wgs84Lat;
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

    public String getWgs84Lon() { // 경도 값 가져오기
        return wgs84Lon;
    }

    public  void setRnum(String rnum){
        this.rnum = rnum;
    }

    public void setWgs84Lon(String wgs84Lon) { // 경도 값 설정하기
        this.wgs84Lon = wgs84Lon;
    }

    public void setWgs84Lat(String wgs84Lat) {  // 위도 값 설정하기
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

    public String findIndex(String buildPlace){
        if(this.buildPlace.equals(buildPlace)){
            return rnum;
        }
        else  return  null;
    }

}
