package com.tamalnath.androidinternals;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class BuildFragment extends Fragment {

    private static final Map<String, Object> BUILD = Utils.findConstants(Build.class, null, null);

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        adapter.addHeader(getString(R.string.build_build));
        adapter.addMap(BUILD);
        adapter.addHeader(getString(R.string.build_environment));
        adapter.addMap(System.getenv());
        adapter.addHeader(getString(R.string.build_properties));
        adapter.addMap(System.getProperties());
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }
}
