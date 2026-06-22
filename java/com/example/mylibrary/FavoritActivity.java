package com.example.mylibrary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class FavoritActivity extends AppCompatActivity {

    RecyclerView recyclerFavorit;

    ArrayList<Book> list;

    BookAdapter adapter;

    DatabaseHelper db;

    EditText etSearchFavorite;

    TextView tvTotalFavorite;

    String currentKeyword = "";

    // ================= BOTTOM NAV =================

    LinearLayout navHome, navFavorite, navCollection, navAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_favorit);

        initView();

        db = new DatabaseHelper(this);

        list = new ArrayList<>();

        adapter = new BookAdapter(this, list);

        recyclerFavorit.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerFavorit.setHasFixedSize(true);

        recyclerFavorit.setAdapter(adapter);

        setupBottomNav();

        setupSearch();

        loadFavoriteBooks();
    }

    // ================= INIT VIEW =================

    private void initView() {

        recyclerFavorit =
                findViewById(R.id.recyclerFavorit);

        etSearchFavorite =
                findViewById(R.id.etSearchFavorite);

        tvTotalFavorite =
                findViewById(R.id.tvTotalFavorite);

        navHome =
                findViewById(R.id.navHome);

        navFavorite =
                findViewById(R.id.navFavorite);

        navCollection =
                findViewById(R.id.navCollection);

        navAdd =
                findViewById(R.id.navAdd);
    }

    // ================= SEARCH =================

    private void setupSearch() {

        etSearchFavorite.addTextChangedListener(
                new TextWatcher() {

                    @Override
                    public void beforeTextChanged(
                            CharSequence s,
                            int start,
                            int count,
                            int after) {
                    }

                    @Override
                    public void onTextChanged(
                            CharSequence s,
                            int start,
                            int before,
                            int count) {

                        currentKeyword =
                                s.toString().trim();

                        loadFavoriteBooks();
                    }

                    @Override
                    public void afterTextChanged(
                            Editable s) {
                    }
                });
    }

    // ================= BOTTOM NAV =================

    private void setupBottomNav() {

        navHome.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            MainActivity.class
                    );

            startActivity(intent);

            finish();
        });

        navFavorite.setOnClickListener(v -> {

            // sedang di halaman favorit
        });

        navCollection.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            KoleksiActivity.class
                    );

            startActivity(intent);
        });

        navAdd.setOnClickListener(v -> {

            Intent intent =
                    new Intent(
                            this,
                            AddBookActivity.class
                    );

            startActivity(intent);
        });
    }

    // ================= LOAD FAVORITE =================

    public void loadFavoriteBooks() {

        list.clear();

        Cursor cursor = db.getFavoriteBooks();

        if (cursor != null && cursor.moveToFirst()) {

            do {

                int id =
                        cursor.getInt(0);

                String title =
                        cursor.getString(1);

                String author =
                        cursor.getString(2);

                String genre =
                        cursor.getString(3);

                String status =
                        cursor.getString(5);

                String cover =
                        cursor.getString(7);

                boolean isFavorite =
                        cursor.getInt(
                                cursor.getColumnIndexOrThrow(
                                        "is_favorite"
                                )
                        ) == 1;

                if (title.toLowerCase().contains(
                        currentKeyword.toLowerCase())
                        ||
                        author.toLowerCase().contains(
                                currentKeyword.toLowerCase()
                        )) {

                    list.add(
                            new Book(
                                    id,
                                    title,
                                    author,
                                    genre,
                                    status,
                                    cover,
                                    isFavorite
                            )
                    );
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        tvTotalFavorite.setText(
                list.size() + " Buku Favorit"
        );

        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadFavoriteBooks();
    }
}