package com.tamalnath.androidinternals;

import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.HashMap;
import java.util.Map;

public class BatteryFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        Intent batteryStatus = getContext().registerReceiver(null, intentFilter);
        if (batteryStatus != null) {
            adapter.addMap(addBatteryDetails(batteryStatus));
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    private Map<String, Object> addBatteryDetails(Intent batteryStatus) {
        Map<String, Object> map = new HashMap<>();
        map.put(getString(R.string.battery_present), batteryStatus.getBooleanExtra(BatteryManager.EXTRA_PRESENT, false));

        int key = batteryStatus.getIntExtra(BatteryManager.EXTRA_STATUS, -1);
        String value = Utils.findConstant(BatteryManager.class, key, "BATTERY_STATUS_(.*)");
        map.put(getString(R.string.battery_status), value);

        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_HEALTH, -1);
        value = Utils.findConstant(BatteryManager.class, key, "BATTERY_HEALTH_(.*)");
        map.put(getString(R.string.battery_health), value);

        value = getString(R.string.unknown);
        key = batteryStatus.getIntExtra(BatteryManager.EXTRA_PLUGGED, -1);
        if (key > 0) {
            value = Utils.findConstant(BatteryManager.class, key, "BATTERY_PLUGGED_(.*)");
        } else if (key == 0) {
            value = getString(R.string.battery_plugged_unplugged);
        }
        map.put(getString(R.string.battery_plugged), value);

        int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
        int scale = batteryStatus.getIntExtra(BatteryManager.EXTRA_SCALE, -1);
        int percent = 100 * level / scale;
        map.put(getString(R.string.battery_charge), percent + "%");

        int voltage = batteryStatus.getIntExtra(BatteryManager.EXTRA_VOLTAGE, -1);
        map.put(getString(R.string.battery_voltage), (voltage / 1000f) + "V");

        float temperature = batteryStatus.getIntExtra(BatteryManager.EXTRA_TEMPERATURE, -1) / 10f;
        map.put(getString(R.string.battery_temperature), temperature + getString(R.string.sensor_unit_deg));

        String technology = batteryStatus.getStringExtra(BatteryManager.EXTRA_TECHNOLOGY);
        map.put(getString(R.string.battery_technology), technology);

        return map;
    }

}