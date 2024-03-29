package com.example.saveus;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.maps.model.LatLng;
import com.google.maps.android.clustering.ClusterItem;
import com.google.maps.android.clustering.ClusterManager;

public class AedItem implements ClusterItem {
    private final LatLng mPosition;
    private final String mTitle;
    private final String mAddress;

    public AedItem(double lat, double lng, String title,String Address){
        mPosition = new LatLng(lat,lng);
        mTitle = title;
        mAddress = Address;
    }

    @NonNull
    @Override
    public LatLng getPosition() {
        return mPosition;
    }

    @Nullable
    @Override
    public String getTitle() {
        return mTitle;
    }


    @Nullable
    @Override
    public String getSnippet() {

        return mAddress;
    }
}