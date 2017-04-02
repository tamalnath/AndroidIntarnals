package com.tamalnath.androidinternals;

import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private List<Data> dataList = new ArrayList<>();

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, @LayoutRes int layout) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(layout, parent, false);
        if (layout == R.layout.card_key_value) {
            return new KeyValueHolder(view);
        } else {
            return new Holder(view);
        }
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        dataList.get(position).decorate(holder);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return dataList.get(position).getLayout();
    }

    void addData(Data data) {
        dataList.add(data);
    }

    void addHeader(@NonNull final String header) {
        addHeader(header, null);
    }

    void addHeader(@NonNull final String header, @Nullable final View.OnClickListener listener) {
        dataList.add(new Data() {

            public int getLayout() {
                return R.layout.card_header;
            }

            @Override
            public void decorate(RecyclerView.ViewHolder holder) {
                ((TextView) holder.itemView).setText(header);
                holder.itemView.setOnClickListener(listener);
            }
        });
    }

    void addKeyValue(@NonNull final String key, @NonNull final String value) {
        addKeyValue(key, value, null);
    }

    void addKeyValue(@NonNull final String key, @NonNull final String value, @Nullable final View.OnClickListener listener) {
        dataList.add(new Data() {

            @Override
            public int getLayout() {
                return R.layout.card_key_value;
            }

            @Override
            public void decorate(RecyclerView.ViewHolder viewHolder) {
                KeyValueHolder holder = (KeyValueHolder) viewHolder;
                holder.keyView.setText(key);
                holder.valueView.setText(value);
                viewHolder.itemView.setOnClickListener(listener);
            }
        });
    }

    void addMap(@NonNull Map<?, ?> map) {
        addMap(map, null);
    }

    void addMap(@NonNull Map<?, ?> map, @Nullable View.OnClickListener listener) {
        for (final Map.Entry<?, ?> entry : map.entrySet()) {
            String key = Utils.toString(entry.getKey());
            String value = Utils.toString(entry.getValue(), "\n", "", "", null);
            addKeyValue(key, value, listener);
        }
    }

    interface Data {

        void decorate(RecyclerView.ViewHolder viewHolder);

        @LayoutRes
        int getLayout();
    }

    private static class Holder extends RecyclerView.ViewHolder {

        Holder(View itemView) {
            super(itemView);
        }
    }

    static class KeyValueHolder extends RecyclerView.ViewHolder {

        TextView keyView;
        TextView valueView;

        KeyValueHolder(View itemView) {
            super(itemView);
            keyView = (TextView) itemView.findViewById(R.id.key);
            valueView = (TextView) itemView.findViewById(R.id.value);
        }
    }

}
