package com.example.mylibrary;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.OvershootInterpolator;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class FavoriteAdapter extends RecyclerView.Adapter<FavoriteAdapter.ViewHolder> {

    private List<Book> bookList;

    public FavoriteAdapter(List<Book> bookList) {
        this.bookList = bookList;
    }

    // ================= VIEW HOLDER =================

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView imgCover;
        ImageView btnFavorite;

        TextView tvTitle;
        TextView tvAuthor;
        TextView tvGenre;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            imgCover = itemView.findViewById(R.id.imgCover);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvGenre = itemView.findViewById(R.id.tvCategory);
        }
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(
            @NonNull ViewGroup parent,
            int viewType
    ) {

        View view = LayoutInflater.from(parent.getContext())
                .inflate(
                        R.layout.item_favorite_book,
                        parent,
                        false
                );

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(
            @NonNull ViewHolder holder,
            int position
    ) {

        Book book = bookList.get(position);

        // ================= DATA =================

        holder.tvTitle.setText(book.getTitle());

        holder.tvAuthor.setText(book.getAuthor());

        holder.tvGenre.setText(book.getGenre());

        // ================= COVER =================

        if (book.getCover() != null &&
                !book.getCover().isEmpty()) {

            int resId = holder.itemView
                    .getContext()
                    .getResources()
                    .getIdentifier(
                            book.getCover(),
                            "drawable",
                            holder.itemView
                                    .getContext()
                                    .getPackageName()
                    );

            if (resId != 0) {

                holder.imgCover.setImageResource(resId);

            } else {

                holder.imgCover.setImageResource(
                        R.drawable.book
                );
            }

        } else {

            holder.imgCover.setImageResource(
                    R.drawable.book
            );
        }

        // ================= FAVORITE ICON =================

        if (book.isFavorite()) {

            holder.btnFavorite.setImageResource(
                    R.drawable.ic_star_filled
            );

        } else {

            holder.btnFavorite.setImageResource(
                    R.drawable.ic_star_border
            );
        }

        // ================= ITEM ANIMATION =================

        holder.itemView.setAlpha(0f);

        holder.itemView.setTranslationY(50f);

        holder.itemView.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .setStartDelay(position * 80L)
                .start();

        // ================= FAVORITE TOGGLE =================

        holder.btnFavorite.setOnClickListener(v -> {

            boolean newState = !book.isFavorite();

            book.setFavorite(newState);

            // ================= ICON CHANGE =================

            if (newState) {

                holder.btnFavorite.setImageResource(
                        R.drawable.ic_star_filled
                );

            } else {

                holder.btnFavorite.setImageResource(
                        R.drawable.ic_star_border
                );
            }

            // ================= SMOOTH STAR ANIMATION =================

            PropertyValuesHolder scaleX =
                    PropertyValuesHolder.ofFloat(
                            View.SCALE_X,
                            1f,
                            1.4f,
                            1f
                    );

            PropertyValuesHolder scaleY =
                    PropertyValuesHolder.ofFloat(
                            View.SCALE_Y,
                            1f,
                            1.4f,
                            1f
                    );

            ObjectAnimator animator =
                    ObjectAnimator.ofPropertyValuesHolder(
                            holder.btnFavorite,
                            scaleX,
                            scaleY
                    );

            animator.setDuration(350);

            animator.setInterpolator(
                    new OvershootInterpolator()
            );

            animator.start();

            // ================= CARD CLICK EFFECT =================

            holder.itemView.animate()
                    .scaleX(0.97f)
                    .scaleY(0.97f)
                    .setDuration(80)
                    .withEndAction(() ->
                            holder.itemView.animate()
                                    .scaleX(1f)
                                    .scaleY(1f)
                                    .setDuration(80)
                    );
        });
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }
}