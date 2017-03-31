package com.tamalnath.androidinternals;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Map;

public class FontsFragment extends Fragment {

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        Map<String, Typeface> typefaces = Utils.findConstants(Typeface.class, Typeface.class, null);
        Map<String, Integer> styles = Utils.findConstants(Typeface.class, int.class, null);
        for (final Map.Entry<String, Typeface> typeface : typefaces.entrySet()) {
            for (final Map.Entry<String, Integer> style : styles.entrySet()) {
                adapter.addData(new Adapter.Data() {
                    @Override
                    public void decorate(RecyclerView.ViewHolder viewHolder) {
                        Adapter.KeyValueHolder holder = (Adapter.KeyValueHolder) viewHolder;
                        holder.keyView.setText(typeface.getKey() + " " + style.getKey());
                        holder.valueView.setText(getText(R.string.fonts_sample));
                        holder.valueView.setTypeface(typeface.getValue(), style.getValue());
                    }

                    @Override
                    public int getLayout() {
                        return R.layout.card_key_value;
                    }
                });
            }
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
