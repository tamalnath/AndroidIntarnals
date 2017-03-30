package com.tamalnath.androidinternals;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.IOException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class FontsFragment extends Fragment {

    private static final String TAG = "FontsFragment";

    @Override
    public View onCreateView(final LayoutInflater inflater, final ViewGroup container, Bundle savedInstanceState) {
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
            final String familyName = family.getAttribute("name");
            if (familyName.isEmpty()) {
                continue;
            }
            final String lang = family.getAttribute("lang");
            final String variant = family.getAttribute("variant");
            adapter.addHeader(familyName);
            NodeList fontList = family.getElementsByTagName("font");
            for (int j = 0; j < fontList.getLength(); j++) {
                final Element font = (Element) fontList.item(j);
                adapter.addData(new Adapter.Data() {
                    @Override
                    public void decorate(RecyclerView.ViewHolder viewHolder) {
                        final String fontFile = font.getTextContent().trim();
                        final String weight = font.getAttribute("weight");
                        final String style = font.getAttribute("style");
                        final Typeface typeface = Typeface.createFromFile("/system/fonts/" + fontFile);
                        Adapter.KeyValueHolder holder = (Adapter.KeyValueHolder) viewHolder;
                        holder.keyView.setText(fontFile);
                        holder.valueView.setText(getText(R.string.fonts_sample));
                        holder.valueView.setTypeface(typeface);
                        holder.itemView.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                View viewGroup = inflater.inflate(R.layout.font_details, null);
                                TextView view;
                                view = (TextView) viewGroup.findViewById(R.id.font_family);
                                view.setText(familyName);
                                view = (TextView) viewGroup.findViewById(R.id.font_lang);
                                view.setText(lang);
                                view = (TextView) viewGroup.findViewById(R.id.font_variant);
                                view.setText(variant);
                                view = (TextView) viewGroup.findViewById(R.id.font_file);
                                view.setText(fontFile);
                                view = (TextView) viewGroup.findViewById(R.id.font_weight);
                                view.setText(weight);
                                view = (TextView) viewGroup.findViewById(R.id.font_style);
                                view.setText(style);
                                EditText editText = (EditText) viewGroup.findViewById(R.id.font_edit);
                                editText.setTypeface(typeface);
                                new AlertDialog.Builder(getContext())
                                        .setTitle(getString(R.string.font_details))
                                        .setView(viewGroup)
                                        .setPositiveButton(R.string.dismiss, null)
                                        .show();
                            }
                        });
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
