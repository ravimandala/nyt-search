package com.ravimandala.labs.nytimessearch.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticleArrayAdapter extends ArrayAdapter<Article> {

    private class ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
    }
    ViewHolder viewHolder;

    public ArticleArrayAdapter(Context context, List<Article> articles) {
        super(context, android.R.layout.simple_list_item_1, articles);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Article article = this.getItem(position);


        if (convertView == null) {
            LayoutInflater inflater = LayoutInflater.from(getContext());

            convertView = inflater.inflate(R.layout.item_article_result, parent, false);
            viewHolder = new ViewHolder();
            viewHolder.ivImage = (ImageView) convertView.findViewById(R.id.ivImage);
            viewHolder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }

        viewHolder.ivImage.setImageResource(0);
        viewHolder.tvTitle.setText(article.getHeadline());

        if (!TextUtils.isEmpty(article.getThumbnailURL())) {
            Picasso.with(getContext()).load(article.getThumbnailURL()).into(viewHolder.ivImage);
        }
        return convertView;
    }
}
