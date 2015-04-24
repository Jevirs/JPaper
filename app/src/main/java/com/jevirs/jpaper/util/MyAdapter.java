package com.jevirs.jpaper.util;

import android.content.Context;

import com.jevirs.jpaper.R;
import com.jevirs.jpaper.ui.MyView;

import java.util.List;


public class MyAdapter extends CommonAdapter<MyView>{

    public MyAdapter(Context context, int resId, List<MyView> list) {
        super(context, resId, list);
    }

    @Override
    public void setView(ViewHolder holder, MyView myView) {
        holder.setText(R.id.ob_text,myView.getTitle());
        holder.setImage(R.id.ob_image,myView.getThumbUrl());
    }
}