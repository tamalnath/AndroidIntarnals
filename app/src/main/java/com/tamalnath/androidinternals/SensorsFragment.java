package com.tamalnath.androidinternals;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SensorsFragment extends Fragment implements SensorEventListener {

    private static final Map<String, Float> GRAVITY = Utils.findConstants(SensorManager.class, float.class, "GRAVITY_(.+)");
    private static final Map<String, Float> LIGHT = Utils.findConstants(SensorManager.class, float.class, "LIGHT_(.+)");

    SensorManager sensorManager;
    List<Sensor> sensors;
    RecyclerView recyclerView;

    private static String findNearest(Map<String, Float> map, float value) {
        float absValue = Math.abs(value);
        String name = null;
        float minDelta = Float.MAX_VALUE;
        for (Map.Entry<String, Float> entry : map.entrySet()) {
            float delta = Math.abs(entry.getValue() - absValue);
            if (delta < minDelta) {
                minDelta = delta;
                name = entry.getKey();
            }
        }
        if (name == null) {
            return null;
        }
        return name;
    }

    @Override
    public View onCreateView(final LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        sensorManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);
        sensors = new ArrayList<>(sensorManager.getSensorList(Sensor.TYPE_ALL));
        for (final Sensor sensor : sensors) {
            adapter.addData(new Adapter.Data() {

                @Override
                public void decorate(RecyclerView.ViewHolder viewHolder) {
                    Adapter.KeyValueHolder holder = (Adapter.KeyValueHolder) viewHolder;
                    holder.keyView.setText(sensor.getName());
                    holder.valueView.setText("");
                    holder.itemView.setOnClickListener(new SensorDetailsClickListener(sensor, inflater));
                }

                @Override
                @LayoutRes
                public int getLayout() {
                    return R.layout.card_key_value;
                }
            });
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

    @Override
    public void onResume() {
        super.onResume();
        for (Sensor sensor : sensors) {
            sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        for (Sensor sensor : sensors) {
            sensorManager.unregisterListener(this, sensor);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int index = sensors.indexOf(event.sensor);
        RecyclerView.ViewHolder holder = recyclerView.findViewHolderForAdapterPosition(index);
        if (holder == null) {
            return;
        }
        Sensor sensor = event.sensor;
        String unit = getUnit(sensor.getType());
        String value;
        float[] v = event.values;
        float magnitude;
        switch (sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
                magnitude = (float) Math.sqrt(v[0] * v[0] + v[1] * v[1] + v[2] * v[2]);
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                value += " (" + findNearest(GRAVITY, magnitude) + ")";
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                value = getString(R.string.sensor_values_magnetic_field_uncalibrated, v[0], v[1], v[2], v[3], v[4], v[5]);
                break;
            case Sensor.TYPE_GYROSCOPE:
                value = getString(R.string.sensor_values_xyz_unit, v[0], v[1], v[2], unit);
                break;
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                value = getString(R.string.sensor_values_gyroscope_uncalibrated, v[0], v[1], v[2], v[3], v[4], v[5]);
                break;
            case Sensor.TYPE_ORIENTATION:
                value = getString(R.string.sensor_values_xyz_unit, v[2], v[0], v[1], unit);
                break;
            case Sensor.TYPE_PROXIMITY:
                if (v[0] == 0) {
                    value = getString(R.string.sensor_value_near);
                } else if (v[0] == sensor.getMaximumRange()) {
                    value = getString(R.string.sensor_value_far);
                } else {
                    value = getString(R.string.sensor_value_unit, v[0], unit);
                }
                break;
            case Sensor.TYPE_LIGHT:
                value = getString(R.string.sensor_value_unit, v[0], unit);
                value += " (" + findNearest(LIGHT, v[0]) + ")";
                break;
            case Sensor.TYPE_SIGNIFICANT_MOTION:
            case Sensor.TYPE_STEP_DETECTOR:
                value = getString(R.string.sensor_values_no_value, System.currentTimeMillis());
                break;
            case Sensor.TYPE_STEP_COUNTER:
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
            case Sensor.TYPE_PRESSURE:
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                value = getString(R.string.sensor_value_unit, v[0], unit);
                break;
            default:
                StringBuilder sb = new StringBuilder();
                for (float f : v) {
                    sb.append(String.format(Locale.getDefault(), ", %1$ .2f", f));
                }
                value = sb.substring(2);
        }
        ((Adapter.KeyValueHolder) holder).valueView.setText(value);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    private String getUnit(int sensorType) {
        switch (sensorType) {
            case Sensor.TYPE_ACCELEROMETER:
            case Sensor.TYPE_LINEAR_ACCELERATION:
            case Sensor.TYPE_GRAVITY:
                return getString(R.string.sensor_unit_ms2);
            case Sensor.TYPE_MAGNETIC_FIELD:
            case Sensor.TYPE_MAGNETIC_FIELD_UNCALIBRATED:
                return getString(R.string.sensor_unit_ut);
            case Sensor.TYPE_GYROSCOPE:
            case Sensor.TYPE_GYROSCOPE_UNCALIBRATED:
                return getString(R.string.sensor_unit_rad);
            case Sensor.TYPE_ORIENTATION:
                return getString(R.string.sensor_unit_deg);
            case Sensor.TYPE_PROXIMITY:
                return getString(R.string.sensor_unit_cm);
            case Sensor.TYPE_LIGHT:
                return getString(R.string.sensor_unit_lx);
            case Sensor.TYPE_PRESSURE:
                return getString(R.string.sensor_unit_pascal);
            case Sensor.TYPE_RELATIVE_HUMIDITY:
                return getString(R.string.sensor_unit_percent);
            case Sensor.TYPE_STEP_COUNTER:
                return getString(R.string.sensor_unit_step);
            case Sensor.TYPE_TEMPERATURE:
            case Sensor.TYPE_AMBIENT_TEMPERATURE:
                return getString(R.string.sensor_unit_centigrade);
        }
        return "";
    }

    private class SensorDetailsClickListener implements View.OnClickListener {

        private Sensor sensor;
        private LayoutInflater inflater;

        SensorDetailsClickListener(Sensor sensor, LayoutInflater inflater) {
            this.sensor = sensor;
            this.inflater = inflater;
        }

        @Override
        public void onClick(View v) {
            View viewGroup = inflater.inflate(R.layout.sensor_details, null);

            String unit = getUnit(sensor.getType());
            TextView view;
            view = (TextView) viewGroup.findViewById(R.id.sensor_id);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(sensor.getId());
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_id_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_type);
            String sensorType;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT_WATCH) {
                 sensorType = sensor.getStringType();
            } else {
                sensorType = Utils.findConstant(Sensor.class, sensor.getType(), "TYPE_(.+)");
            }
            view.setText(sensorType);
            view = (TextView) viewGroup.findViewById(R.id.sensor_vendor);
            view.setText(sensor.getVendor());
            view = (TextView) viewGroup.findViewById(R.id.sensor_version);
            view.setText(String.valueOf(sensor.getVersion()));
            view = (TextView) viewGroup.findViewById(R.id.sensor_power);
            view.setText(getString(R.string.sensor_power_unit, sensor.getPower()));
            view = (TextView) viewGroup.findViewById(R.id.sensor_delay);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setText(getString(R.string.sensor_delay_unit, sensor.getMinDelay(), sensor.getMaxDelay()));
            } else {
                view.setText(getString(R.string.sensor_min_delay_unit, sensor.getMinDelay()));
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_resolution);
            view.setText(getString(R.string.sensor_resolution_unit, sensor.getResolution(), unit));
            view = (TextView) viewGroup.findViewById(R.id.sensor_max_range);
            view.setText(getString(R.string.sensor_value_unit, sensor.getMaximumRange(), unit));
            view = (TextView) viewGroup.findViewById(R.id.sensor_reserved_event);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setText(getString(R.string.sensor_event_unit, sensor.getFifoReservedEventCount()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_reserved_event_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_max_event);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                view.setText(getString(R.string.sensor_event_unit, sensor.getFifoMaxEventCount()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_max_event_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_dynamic);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(String.valueOf(sensor.isDynamicSensor()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_dynamic_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_wake_up);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                view.setText(String.valueOf(sensor.isWakeUpSensor()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_wake_up_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_additional_info);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                view.setText(String.valueOf(sensor.isAdditionalInfoSupported()));
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_additional_info_label).setVisibility(View.GONE);
            }
            view = (TextView) viewGroup.findViewById(R.id.sensor_reporting_mode);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                String mode = Utils.findConstant(Sensor.class, sensor.getReportingMode(), "REPORTING_MODE_(.+)");
                view.setText(mode);
            } else {
                view.setVisibility(View.GONE);
                viewGroup.findViewById(R.id.sensor_reporting_mode_label).setVisibility(View.GONE);
            }
            new AlertDialog.Builder(getContext())
                    .setTitle(sensor.getName())
                    .setView(viewGroup)
                    .setPositiveButton(R.string.close, null)
                    .show();
        }
    }
}
