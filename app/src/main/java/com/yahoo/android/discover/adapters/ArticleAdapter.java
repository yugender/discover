package com.yahoo.android.discover.adapters;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.yahoo.android.discover.R;
import com.yahoo.android.discover.models.Article;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by yboini on 11/16/16.
 */

public class ArticleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Article> articles;
    // Store the context for easy access
    private Context mContext;
    // Define listener member variable
    private OnItemClickListener listener;
    private final int TEXT = 0, IMAGE = 1;

    // Pass in the contact array into the constructor
    public ArticleAdapter(Context context, List<Article> articles) {
        this.articles = articles;
        mContext = context;
    }

    // Easy access to the context object in the recyclerview
    private Context getContext() {
        return mContext;
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public int getItemViewType(int position) {
        Article article = articles.get(position);
        if (article.getImageUrl() != null) {
            return IMAGE;
        } else {
            return TEXT;
        }
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        RecyclerView.ViewHolder viewHolder;
        switch (viewType) {
            case IMAGE:
                View v1 = inflater.inflate(R.layout.item_article_tile, parent, false);
                viewHolder = new ViewHolder(v1);
                break;
            case TEXT:
            default:
                View v2 = inflater.inflate(R.layout.item_article_text, parent, false);
                viewHolder = new TextViewHolder(v2);
                break;
        }
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder viewHolder, int position) {
        Article article = articles.get(position);

        // Set item views based on your views and data model

        switch (viewHolder.getItemViewType()) {
            case IMAGE:
                ViewHolder imageViewHolder = (ViewHolder) viewHolder;
                configureImageViewHolder(imageViewHolder, article);
                break;
            case TEXT:
            default:
                TextViewHolder textViewHolder = (TextViewHolder) viewHolder;
                configureTextViewHolder(textViewHolder, article);
                break;
        }
    }

    private void configureTextViewHolder(TextViewHolder viewHolder, Article article) {
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getTitle());
    }

    private void configureImageViewHolder(ViewHolder viewHolder, Article article) {
        TextView tvTitle = viewHolder.tvTitle;
        tvTitle.setText(article.getTitle());
        if (viewHolder.ivImage != null && article.getImageUrl() != null) {
            Glide.with(getContext())
                    .load(article.getImageUrl())
                    .override(500,500)
                    .into(viewHolder.ivImage);
        }
    }

    @Override
    public int getItemCount() {
        return articles.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        // Your holder should contain a member variable
        // for any view that will be set as you render a row
        //public TextView tvTitle;
        //public ImageView ivImage;
        @BindView(R.id.tvTitle) TextView tvTitle;
        @BindView(R.id.ivImage) ImageView ivImage;

        // We also create a constructor that accepts the entire item row
        // and does the view lookups to find each subview
        public ViewHolder(final View itemView) {
            // Stores the itemView in a public final member variable that can be used
            // to access the context from any ViewHolder instance.
            super(itemView);
            ButterKnife.bind(this, itemView);
            //tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
            //ivImage = (ImageView) itemView.findViewById(R.id.ivImage);
            itemView.setOnClickListener(v -> {
                // Triggers click upwards to the adapter on click
                if (listener != null) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        listener.onItemClick(itemView, position);
                    }
                }
            });
        }
    }

    public class TextViewHolder extends RecyclerView.ViewHolder {
        //public TextView tvTitle;
        @BindView(R.id.tvTitle) TextView tvTitle;
        public TextViewHolder(View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            //tvTitle = (TextView) itemView.findViewById(R.id.tvTitle);
        }
    }

    // Define the listener interface
    public interface OnItemClickListener {
        void onItemClick(View itemView, int position);
    }
}
