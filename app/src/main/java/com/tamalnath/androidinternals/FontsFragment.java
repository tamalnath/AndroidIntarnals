package com.tamalnath.androidinternals;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FontsFragment extends Fragment {

    private static final String TAG = "FontsFragment";
    private static final String SAMPLE = "The quick brown fox jumps over the lazy dog";

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        RecyclerView recyclerView = (RecyclerView) inflater.inflate(R.layout.recycler_view, container, false);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        Adapter adapter = new Adapter();
        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse("file:///system/etc/fonts.xml");
        } catch (IOException | SAXException | ParserConfigurationException e) {
            Log.e(TAG, e.getMessage(), e);
            recyclerView.setAdapter(adapter);
            return recyclerView;
        }
        NodeList familyList = doc.getElementsByTagName("family");
        for (int i = 0; i < familyList.getLength(); i++) {
            Element family = (Element) familyList.item(i);
            String familyName = family.getAttribute("name");
            if (familyName.isEmpty()) {
                continue;
            }
            String lang = family.getAttribute("lang");
            String variant = family.getAttribute("variant");
            if (!lang.isEmpty()) {
                familyName += " lang:" + lang;
            }
            if (!variant.isEmpty()) {
                familyName += " variant:" + variant;
            }
            adapter.addHeader(familyName);
            NodeList fontList = family.getElementsByTagName("font");
            for (int j = 0; j < fontList.getLength(); j++) {
                final Element font = (Element) fontList.item(j);
                adapter.addData(new Adapter.Data() {
                    @Override
                    public void decorate(RecyclerView.ViewHolder viewHolder) {
                        String fontFile = font.getTextContent().trim();
                        String weight = font.getAttribute("weight");
                        String style = font.getAttribute("style");
                        String fontName = fontFile;
                        if (!weight.isEmpty()) {
                            fontName += " weight:" + weight;
                        }
                        if (!style.isEmpty()) {
                            fontName += " style:" + style;
                        }
                        Typeface typeface = Typeface.createFromFile("/system/fonts/" + fontFile);
                        Adapter.KeyValueHolder holder = (Adapter.KeyValueHolder) viewHolder;
                        holder.keyView.setText(fontName);
                        holder.valueView.setText(SAMPLE);
                        holder.valueView.setTypeface(typeface);
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
