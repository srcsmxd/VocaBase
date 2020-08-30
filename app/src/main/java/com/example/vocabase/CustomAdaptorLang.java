package com.example.vocabase;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class CustomAdaptorLang extends BaseAdapter {
    private ArrayList<LangList> langList;
    private Context context;
    public CustomAdaptorLang(ArrayList<LangList> list, Context cont){
        this.langList = list;
        this.context = cont;
    }

    @Override
    public int getCount() {
        return this.langList.size();
    }

    @Override
    public Object getItem(int position) {
        return this.langList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // inflate the layout for each list row
        if (convertView == null) {
            convertView = LayoutInflater.from(context).
                    inflate(R.layout.textcenter1, parent, false);
        }

        // get current item to be displayed
        LangList currentItem = (LangList) getItem(position);

        // get the TextView for item name and item description
        TextView textViewItemName = (TextView)
                convertView.findViewById(R.id.textview);
//        TextView tvSentence = convertView.findViewById(R.id.textview1);
        //sets the text for item name and item description from the current item object
        textViewItemName.setText(currentItem.getLanguage().toUpperCase());
//        tvSentence.setText(currentItem.getSentence());
        // returns the view for the current row
        return convertView;
    }
}
