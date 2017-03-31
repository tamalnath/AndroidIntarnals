package com.tamalnath.androidinternals;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LocationFragment extends Fragment {

    private Adapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        adapter = new Adapter();

        /*
        if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION)
                || ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
            Toast.makeText(getContext(), R.string.location_permission, Toast.LENGTH_SHORT).show();
        }
        */

        addLocationDetails();
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    void addLocationDetails() {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            final String[] perms = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(getActivity(), perms, (short) hashCode());
            return;
        }
        LocationManager locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        List<String> names = locationManager.getAllProviders();
        for (String name : names) {
            Location location = locationManager.getLastKnownLocation(name);
            String latitude = Location.convert(location.getLatitude(), Location.FORMAT_SECONDS);
            String longitude = Location.convert(location.getLongitude(), Location.FORMAT_SECONDS);
            CharSequence time = DateUtils.getRelativeDateTimeString(getContext(), location.getTime(), DateUtils.SECOND_IN_MILLIS, DateUtils.WEEK_IN_MILLIS, DateUtils.FORMAT_ABBREV_ALL);
            adapter.addKeyValue(name, getString(R.string.location_lat_lon, latitude, longitude, time));
        }
        adapter.notifyDataSetChanged();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        for (int i = 0; i < permissions.length; i++) {
            if ((Manifest.permission.ACCESS_COARSE_LOCATION.equals(permissions[i])
                    || Manifest.permission.ACCESS_FINE_LOCATION.equals(permissions[i]))
                    && PERMISSION_DENIED == grantResults[i]) {
                Toast.makeText(getContext(), R.string.location_permission, Toast.LENGTH_SHORT).show();
                return;
            }
        }
        addLocationDetails();
    }
}