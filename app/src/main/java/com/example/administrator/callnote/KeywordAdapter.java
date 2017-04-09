package com.example.administrator.callnote;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

/**
 * Created by Administrator on 2016/9/10.
 */

class ViewHolder
{
    ImageView keywordImage;
    TextView keywordText;
}

public class KeywordAdapter extends ArrayAdapter<Keyword> {
    private int resourceId;
    public KeywordAdapter(Context context, int textViewResourceId, List<Keyword> objects)
    {
        super(context, textViewResourceId, objects);
        resourceId = textViewResourceId;
    }

    public View getView(int postion, View convertView, ViewGroup parent)
    {
        Keyword keyword = getItem(postion);
        View view;
        ViewHolder viewHolder;
        if (convertView == null) {
            view = LayoutInflater.from(getContext()).inflate(resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.keywordImage = (ImageView) view.findViewById(R.id.keyword_image);
            viewHolder.keywordText = (TextView) view.findViewById(R.id.keyword_text);
            view.setTag(viewHolder);
        }
        else{
            view = convertView;
            viewHolder = (ViewHolder) view.getTag();
        }


        viewHolder.keywordImage.setImageResource(keyword.getImageId());
        viewHolder.keywordText.setText(keyword.getName());
        return view;
    }
}
