package com.ravimandala.labs.nytimessearch.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.ravimandala.labs.nytimessearch.R;
import com.ravimandala.labs.nytimessearch.model.Article;
import com.squareup.picasso.Picasso;

import java.util.List;

public class ArticlesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Article> articles;
    private static OnItemClickListener listener;

    private static final int VIEW_WITH_IMAGE = 0;
    private static final int VIEW_WITHOUT_IMAGE = 1;

    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }

    // Define the method that allows the parent activity or fragment to define the listener
    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public ArticlesAdapter(List<Article> articles) {
        this.articles = articles;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());

        RecyclerView.ViewHolder viewHolder;

        switch(viewType) {
            case VIEW_WITH_IMAGE:
                viewHolder = new ViewHolderImage(parent.getContext(),
                        inflater.inflate(R.layout.item_article_image_result, parent, false));
                break;
            default:
                viewHolder = new ViewHolderNoImage(parent.getContext(),
                        inflater.inflate(R.layout.item_article_result, parent, false));
                break;
//            default: // Assuming view without image
//                viewHolder = new RecyclerViewSimpleTextViewHolder (
//                        inflater.inflate(android.R.layout.simple_list_item_1, parent, false));
        }

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        Article article = articles.get(position);
        switch (holder.getItemViewType()) {
            case VIEW_WITH_IMAGE:
                ViewHolderImage viewHolderImage = (ViewHolderImage) holder;
                viewHolderImage.ivImage.setImageResource(0);
                viewHolderImage.tvTitle.setText(article.getHeadline());
                if (!TextUtils.isEmpty(article.getThumbnailURL())) {
                    Picasso.with(viewHolderImage.ivImage.getContext()).load(article.getThumbnailURL()).into(viewHolderImage.ivImage);
                }
                break;
            default:
                ViewHolderNoImage viewHolderNoImage = (ViewHolderNoImage) holder;
                viewHolderNoImage.tvTitle.setText(article.getHeadline());
                break;
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public static class ViewHolderImage extends RecyclerView.ViewHolder {
        ImageView ivImage;
        TextView tvTitle;
        Context context;

        public ViewHolderImage(Context context, View itemView) {
            super(itemView);

            this.context = context;
            ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(v, getLayoutPosition());
                }
            });
        }
    }

    public static class ViewHolderNoImage extends RecyclerView.ViewHolder {
        TextView tvTitle;
        Context context;

        public ViewHolderNoImage(Context context, View itemView) {
            super(itemView);

            this.context = context;
            tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);

            itemView.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    if (listener != null)
                        listener.onItemClick(v, getLayoutPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (articles.get(position).getThumbnailURL().isEmpty()) {
            return VIEW_WITHOUT_IMAGE;
        } else {
            return VIEW_WITH_IMAGE;
        }
    }

    public void clear() {
        articles.clear();
        notifyDataSetChanged();
    }

    public void addAll(List<Article> moreArticles) {
        articles.addAll(moreArticles);
        notifyDataSetChanged();
    }
}
