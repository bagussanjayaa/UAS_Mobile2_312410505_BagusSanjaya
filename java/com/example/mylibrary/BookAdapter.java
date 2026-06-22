package com.example.mylibrary;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class BookAdapter extends RecyclerView.Adapter<BookAdapter.ViewHolder> {

    Context context;
    ArrayList<Book> list;
    DatabaseHelper db;

    public BookAdapter(Context context, ArrayList<Book> list) {
        this.context = context;
        this.list = list;
        this.db = new DatabaseHelper(context);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView tvTitle, tvAuthor, tvStatus, tvGenre;
        ImageView imgBook;
        ImageView btnFavorite;

        public ViewHolder(View itemView) {
            super(itemView);

            tvTitle = itemView.findViewById(R.id.tvTitle);
            tvAuthor = itemView.findViewById(R.id.tvAuthor);
            tvStatus = itemView.findViewById(R.id.tvStatus);
            tvGenre = itemView.findViewById(R.id.tvGenre);
            imgBook = itemView.findViewById(R.id.imgBook);
            btnFavorite = itemView.findViewById(R.id.btnFavorite);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context)
                .inflate(R.layout.item_book, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Book book = list.get(position);

        // ================= DATA =================
        holder.tvTitle.setText(book.getTitle());
        holder.tvAuthor.setText(book.getAuthor());
        holder.tvGenre.setText(book.getGenre());
        holder.tvStatus.setText(getStatusText(book.getStatus()));

        // ================= COVER =================
        if (book.getCover() != null && !book.getCover().isEmpty()) {
            try {
                holder.imgBook.setImageURI(Uri.parse(book.getCover()));
            } catch (Exception e) {
                holder.imgBook.setImageResource(R.drawable.book);
            }
        } else {
            holder.imgBook.setImageResource(R.drawable.book);
        }

        // ================= STATUS COLOR =================
        switch (book.getStatus()) {
            case "BELUM":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_belum);
                break;
            case "SEDANG":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_sedang);
                break;
            case "SELESAI":
                holder.tvStatus.setBackgroundResource(R.drawable.bg_status_sudah);
                break;
        }

        // ================= FAVORITE ICON =================
        holder.btnFavorite.setImageResource(
                book.isFavorite()
                        ? R.drawable.ic_star_filled
                        : R.drawable.ic_star_border
        );

        // ================= FAVORITE CLICK =================
        holder.btnFavorite.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Book selectedBook = list.get(pos);

            boolean newState = !selectedBook.isFavorite();
            selectedBook.setFavorite(newState);

            db.updateFavorite(selectedBook.getId(), newState);

            notifyItemChanged(pos);

            if (context instanceof MainActivity) {
                ((MainActivity) context).loadFavoriteBooks();
            }

            Toast.makeText(context,
                    newState ? "Ditambahkan ke favorit ⭐" : "Dihapus dari favorit",
                    Toast.LENGTH_SHORT).show();
        });

        // ================= CLICK DETAIL =================
        holder.itemView.setOnClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return;

            Book selectedBook = list.get(pos);

            Intent intent = new Intent(context, DetailBookActivity.class);
            intent.putExtra("id", selectedBook.getId());
            context.startActivity(intent);
        });

        // ================= DELETE =================
        holder.itemView.setOnLongClickListener(v -> {

            int pos = holder.getAdapterPosition();
            if (pos == RecyclerView.NO_POSITION) return true;

            Book selectedBook = list.get(pos);

            new android.app.AlertDialog.Builder(context)
                    .setTitle("Hapus Buku")
                    .setMessage("Apakah kamu yakin ingin menghapus buku ini?")
                    .setPositiveButton("Ya", (dialog, which) -> {

                        db.deleteBook(selectedBook.getId());

                        list.remove(pos);
                        notifyItemRemoved(pos);

                        notifyItemRangeChanged(pos, list.size());

                        if (context instanceof MainActivity) {
                            ((MainActivity) context).loadFavoriteBooks();
                        }

                        Toast.makeText(context, "Buku dihapus", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Tidak", null)
                    .show();

            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    private String getStatusText(String status) {
        switch (status) {
            case "BELUM":
                return "Belum Dibaca";
            case "SEDANG":
                return "Sedang Dibaca";
            case "SELESAI":
                return "Sudah Dibaca";
        }
        return status;
    }
}