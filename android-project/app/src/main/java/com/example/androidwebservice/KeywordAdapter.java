package com.example.androidwebservice;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

public class KeywordAdapter extends ArrayAdapter<Keyword> {
    private List<Keyword> keywords;
    private Context context;

    public KeywordAdapter(@NonNull Context context, int resource, @NonNull List<Keyword> keywords) {
        super(context, resource, keywords);
        this.keywords = keywords;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listView = layoutInflater.inflate(R.layout.keywords, null, true);
        TextView textViewKeyword = listView.findViewById(R.id.textViewKeyword);
        TextView textViewValue = listView.findViewById(R.id.textViewValue);

        Keyword keyword = keywords.get(position);

        textViewKeyword.setText(keyword.getKeyword());
        textViewValue.setText(keyword.getValue());

        final int iconId = keyword.getSynced()
                ? R.drawable.success
                : R.drawable.stopwatch;
        textViewValue.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0);

        return listView;
    }
}
