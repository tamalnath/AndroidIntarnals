package com.tamalnath.androidinternals;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import java.util.List;
import java.util.Map;

import static android.content.pm.PackageManager.PERMISSION_DENIED;

public class LocationFragment extends Fragment implements LocationListener {

    private Adapter adapter;
    private RecyclerView recyclerView;
    private LocationManager locationManager;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        locationManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
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
        List<String> providers = locationManager.getAllProviders();
        for (final String provider : providers) {
            Location location = locationManager.getLastKnownLocation(provider);
            String locationDetails = getString(R.string.loading);
            if (location == null) {
                Toast.makeText(getContext(), getString(R.string.location_request, provider), Toast.LENGTH_SHORT).show();
                locationManager.requestSingleUpdate(provider, this, null);
            } else {
                locationDetails = getLocationDetails(location);
            }

            adapter.addKeyValue(provider, locationDetails, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getContext())
                            .setTitle(provider)
                            .setMessage("Message")
                            .setPositiveButton(R.string.close, null)
                            .show();
                }
            });
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

    @Override
    public void onLocationChanged(Location location) {
        int index = locationManager.getAllProviders().indexOf(location.getProvider());
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        ((Adapter.KeyValueHolder) holder).valueView.setText(getLocationDetails(location));
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        String statusMsg = Utils.findConstant(LocationProvider.class, status, null);
        statusMsg = getString(R.string.location_changed, provider, statusMsg);
        Toast.makeText(getContext(), statusMsg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderEnabled(String provider) {
        Toast.makeText(getContext(), getString(R.string.location_enabled, provider), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onProviderDisabled(String provider) {
        Toast.makeText(getContext(), getString(R.string.location_disabled, provider), Toast.LENGTH_SHORT).show();
        int index = locationManager.getAllProviders().indexOf(provider);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        ((Adapter.KeyValueHolder) holder).valueView.setText(getText(R.string.unknown));
    }

    private String getLocationDetails(Location location) {
        Map<String, Object> properties = Utils.findProperties(location);
        return Utils.toString(properties, "\n", "", "", ": ");
    }

}
