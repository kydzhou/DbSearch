package com.dbsearch.app.adpater;

import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.dbsearch.app.R;

public class ClassItemViewHolder extends RecyclerView.ViewHolder{

    private final TextView mClassTitleView;
    private final TextView mClassAuthorView;
    private final ImageView mClassPic;
    public ClassItemViewHolder(View parent) {
        super(parent);
        mClassTitleView = (TextView) parent.findViewById(R.id.class_title_text);
        mClassAuthorView = (TextView) parent.findViewById(R.id.class_author_text);
        mClassPic = (ImageView) parent.findViewById(R.id.class_pic);
    }

    public void setTitleText(CharSequence text){
        setTextView(mClassTitleView, text);
    }

    public void setTitleText(int text){
        setTextView(mClassTitleView, text);
    }

    public void setAuthorText(CharSequence text){
        setTextView(mClassAuthorView, text);
    }

    public void setAuthorText(int text){
        setTextView(mClassAuthorView, text);
    }

    public void setPicImage(Bitmap bmp){
        setPicView(mClassPic, bmp);
    }

    private void setTextView(TextView view, CharSequence text){
        if (view == null || TextUtils.isEmpty(text))
            return;
        view.setText(text);
    }

    private void setTextView(TextView view, int text){
        if (view == null || text <= 0)
            return;
        view.setText(text);
    }
    private void setPicView(ImageView view, Bitmap bmp){
        if (view == null || bmp==null)
            return;
        view.setImageBitmap(bmp);
    }
}
