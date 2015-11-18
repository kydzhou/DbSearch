package com.dbsearch.app.adpater;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;

import com.dbsearch.app.R;
import com.dbsearch.app.model.ClassModel;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by lgp on 2015/4/6.
 */
public class ClassAdapter extends BaseRecyclerViewAdapter<ClassModel> implements Filterable{
    private final List<ClassModel> originalList;
    private int upDownFactor = 1;
    private boolean isShowScaleAnimate = true;
    public ClassAdapter(List<ClassModel> list) {
        super(list);
        originalList = new ArrayList<>(list);
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        final View view = LayoutInflater.from(context).inflate(R.layout.class_item_layout, parent, false);
        return new ClassItemViewHolder(view);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        super.onBindViewHolder(viewHolder, position);
        ClassItemViewHolder holder = (ClassItemViewHolder) viewHolder;
        ClassModel book = list.get(position);
        if (book == null)
            return;
        holder.setTitleText(book.getTitle());
        holder.setAuthorText(book.getAuthor());
        holder.setPicImage(book.getPic());
        animate(viewHolder, position);
    }

    @Override
    public Filter getFilter() {
        return new NoteFilter(this, originalList);
    }

    @Override
    protected Animator[] getAnimators(View view) {
        if (view.getMeasuredHeight() <=0 || isShowScaleAnimate){
            ObjectAnimator scaleX = ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f);
            ObjectAnimator scaleY = ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f);
            return new ObjectAnimator[]{scaleX, scaleY};
        }
        return new Animator[]{
                ObjectAnimator.ofFloat(view, "scaleX", 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "scaleY", 1.1f, 1f),
                ObjectAnimator.ofFloat(view, "translationY", upDownFactor * 1.5f * view.getMeasuredHeight(), 0)
        };
    }

    @Override
    public void setList(List<ClassModel> list) {
        super.setList(list);
        this.originalList.clear();
        originalList.addAll(list);
        setUpFactor();
        isShowScaleAnimate = true;
    }

    public void setDownFactor(){
        upDownFactor = -1;
        isShowScaleAnimate = false;
    }

    public void setUpFactor(){
        upDownFactor = 1;
        isShowScaleAnimate = false;
    }

    private static class NoteFilter extends Filter{

        private final ClassAdapter adapter;

        private final List<ClassModel> originalList;

        private final List<ClassModel> filteredList;

        private NoteFilter(ClassAdapter adapter, List<ClassModel> originalList) {
            super();
            this.adapter = adapter;
            this.originalList = new LinkedList<>(originalList);
            this.filteredList = new ArrayList<>();
        }

        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            filteredList.clear();
            final FilterResults results = new FilterResults();
            if (constraint.length() == 0) {
                filteredList.addAll(originalList);
            } else {
                for ( ClassModel book : originalList) {
//                    if (book.getContent().contains(constraint) || book.getLabel().contains(constraint)) {
                        filteredList.add(book);
//                    }
                }
            }
            results.values = filteredList;
            results.count = filteredList.size();
            return results;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            adapter.list.clear();
            adapter.list.addAll((ArrayList<ClassModel>) results.values);
            adapter.notifyDataSetChanged();
        }
    }
}
