package com.example.mygpslogger;

import android.location.LocationListener;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;

public class MyLocListener implements LocationListener {
    private LOCListenerInterface locaListenerInterface;

    @Override
    public void onLocationChanged(@NonNull Location location) {
        locaListenerInterface.onLocationChanged(location);
    }

    public void setLocaListenerInterface(LOCListenerInterface locaListenerInterface) {
        this.locaListenerInterface = locaListenerInterface;
    }
}
