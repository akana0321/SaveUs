package com.example.saveus;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.util.ArrayList;
import java.util.List;

public class PermissionSupport {

    private Context context;
    private Activity activity;

    // 요청할 권한
    private String[] permissions = {
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.CAMERA,
            Manifest.permission.SEND_SMS
    };

    private String[] permissions_gps = {
            Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_COARSE_LOCATION
    };
    private String[] permissions_camera = {
            Manifest.permission.CAMERA,
    };
    private List permissionList;

    // 권한 요청을 할 떄 발생하는 창에 대한 결과값을 받기 위해 지정해주는 int 형
    private final int MULTIPLE_PERMISSIONS = 1055; // 임의의 사용자 지정값

    // 생성자에서 Activity와 Context를 파라미터로 받음
    public PermissionSupport(Activity _activity, Context _context) {
        this.activity = _activity;
        this.context = _context;
    }

    // 허용 받아야 할 권한이 남아있는지 체크
    public boolean checkPermission() {
        int result;
        permissionList = new ArrayList();

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for (String pm : permissions) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty())
            return false;
        return true;
    }

    // 각각의 권한 체크
    public boolean checkPermission_gps() {
        int result;
        permissionList = new ArrayList();

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for(String pm : permissions_gps) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty())
            return false;
        return true;
    }
    public boolean checkPermission_camera() {
        int result;
        permissionList = new ArrayList();

        // 위에서 배열로 선언한 권한 중 허용되지 않은 권한이 있는지 체크
        for(String pm : permissions_camera) {
            result = ContextCompat.checkSelfPermission(context, pm);
            if (result != PackageManager.PERMISSION_GRANTED) {
                permissionList.add(pm);
            }
        }
        if (!permissionList.isEmpty())
            return false;
        return true;
    }

    // 권한 허용 요청
    public void requestPermission() {
        ActivityCompat.requestPermissions(activity, (String[]) permissionList.toArray(new String[permissionList.size()]), MULTIPLE_PERMISSIONS);
    }

    public boolean permissionCheck_gps(Intent intent) {
        // SDK 23버전 이하에서는 Permission이 필요하지 않음
        if (Build.VERSION.SDK_INT >= 23) {
            // 권한 체크한 후에 리턴이 false로 들어온다면 권한요청
            if (!checkPermission_gps()) {
                requestPermission();
                Toast.makeText(activity, "위치 권한 승인 후 다시 메뉴를 눌러주세요.", Toast.LENGTH_LONG).show();
            } else {
                activity.startActivity(intent);
                return true;
            }
        }
        return true;
    }

    // 권한 요청에 대한 결과 처리
    public boolean permissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        // 우선 requestCode가 아까 위에 final로 선언하였던 숫자와 맞는지
        // 결과값의 길이가 0보다 큰지 먼저 체크
        if (requestCode == MULTIPLE_PERMISSIONS && (grantResults.length >0)) {
            for (int i = 0; i < grantResults.length; i++) {
                // grantResults가 0이면 사용자가 허용한 것이고 -1이면 거부한 것
                // -1이 있는지 체크하여 하나라도 -1이 나온다면 false를 리턴
                if (grantResults[i] == -1)
                    return false;
            }
        }
        return true;
    }
}
