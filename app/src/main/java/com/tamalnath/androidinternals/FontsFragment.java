package com.tamalnath.androidinternals;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.io.File;

public class FontsFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        for (final File fontFile : new File("/system/fonts").listFiles()) {
            final String fontName = fontFile.getName().split("\\.")[0];
            adapter.addData(new Adapter.Data() {

                @Override
                public void decorate(RecyclerView.ViewHolder holder) {
                    Typeface typeface = Typeface.createFromFile(fontFile);
                    TextView textView = (TextView) holder.itemView;
                    textView.setTextSize(16);
                    textView.setTypeface(typeface);
                    textView.setText(fontName);
                }

                @Override
                @LayoutRes
                public int getLayout() {
                    return R.layout.card_header;
                }
            });
        }
        recyclerView.setAdapter(adapter);
        return recyclerView;
    }

}
