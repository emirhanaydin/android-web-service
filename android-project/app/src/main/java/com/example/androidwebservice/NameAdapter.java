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

public class NameAdapter extends ArrayAdapter<Name> {
    private List<Name> names;
    private Context context;

    public NameAdapter(@NonNull Context context, int resource, @NonNull List<Name> names) {
        super(context, resource, names);
        this.names = names;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        LayoutInflater layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View listView = layoutInflater.inflate(R.layout.names, null, true);
        TextView textViewName = listView.findViewById(R.id.textViewName);

        Name name = names.get(position);

        textViewName.setText(name.getName());

        final int iconId = name.getStatus() == MainActivity.NAME_SYNCED_WITH_SERVER
                ? R.drawable.success
                : R.drawable.stopwatch;
        textViewName.setCompoundDrawablesWithIntrinsicBounds(0, 0, iconId, 0);

        return listView;
    }
}
