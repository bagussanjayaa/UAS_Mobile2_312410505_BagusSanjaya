package com.example.mylibrary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class KoleksiActivity extends AppCompatActivity {

    RecyclerView recyclerKoleksi;

    ArrayList<Book> list;

    BookAdapter adapter;

    DatabaseHelper db;

    EditText etSearchCollection;

    TextView tabSemua, tabBelum, tabSedang, tabSelesai;

    TextView tvTotalBook;

    LinearLayout navHome, navFavorite, navCollection, navAdd;

    String currentFilter = "SEMUA";

    String currentKeyword = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_koleksi);

        initView();

        db = new DatabaseHelper(this);

        list = new ArrayList<>();

        adapter = new BookAdapter(this, list);

        recyclerKoleksi.setLayoutManager(
                new LinearLayoutManager(this)
        );

        recyclerKoleksi.setNestedScrollingEnabled(true);

        recyclerKoleksi.setAdapter(adapter);

        setupUI();

        loadBooks();
    }

    // ================= INIT VIEW =================

    private void initView() {

        recyclerKoleksi =
                findViewById(R.id.recyclerView);

        etSearchCollection =
                findViewById(R.id.etSearchCollection);

        tabSemua =
                findViewById(R.id.tabSemua);

        tabBelum =
                findViewById(R.id.tabBelum);

        tabSedang =
                findViewById(R.id.tabSedang);

        tabSelesai =
                findViewById(R.id.tabSelesai);

        tvTotalBook =
                findViewById(R.id.tvTotalBook);

        navHome =
                findViewById(R.id.navHome);

        navFavorite =
                findViewById(R.id.navFavorite);

        navCollection =
                findViewById(R.id.navCollection);

        navAdd =
                findViewById(R.id.navAdd);
    }

    // ================= UI =================

    private void setupUI() {

        setActiveTab(tabSemua);

        // ================= SEARCH =================

        etSearchCollection.setOnEditorActionListener(
                (v, actionId, event) -> {

                    boolean isEnter =
                            event != null &&
                                    event.getKeyCode() ==
                                            KeyEvent.KEYCODE_ENTER &&
                                    event.getAction() ==
                                            KeyEvent.ACTION_DOWN;

                    if (actionId ==
                            EditorInfo.IME_ACTION_SEARCH
                            || isEnter) {

                        currentKeyword =
                                etSearchCollection
                                        .getText()
                                        .toString()
                                        .trim();

                        loadBooks();

                        return true;
                    }

                    return false;
                });

        // ================= FILTER =================

        tabSemua.setOnClickListener(v -> {

            currentFilter = "SEMUA";

            setActiveTab(tabSemua);

            loadBooks();
        });

        tabBelum.setOnClickListener(v -> {

            currentFilter = "BELUM";

            setActiveTab(tabBelum);

            loadBooks();
        });

        tabSedang.setOnClickListener(v -> {

            currentFilter = "SEDANG";

            setActiveTab(tabSedang);

            loadBooks();
        });

        tabSelesai.setOnClickListener(v -> {

            currentFilter = "SELESAI";

            setActiveTab(tabSelesai);

            loadBooks();
        });

        // ================= BOTTOM NAV =================

        navHome.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            MainActivity.class
                    )
            );

            overridePendingTransition(
                    R.anim.slide_in_left,
                    R.anim.slide_out_right
            );

            finish();
        });

        navFavorite.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            FavoritActivity.class
                    )
            );

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        });

        navAdd.setOnClickListener(v -> {

            startActivity(
                    new Intent(
                            this,
                            AddBookActivity.class
                    )
            );

            overridePendingTransition(
                    R.anim.slide_in_right,
                    R.anim.slide_out_left
            );
        });
    }

    // ================= LOAD BOOK =================

    private void loadBooks() {

        list.clear();

        Cursor cursor;

        if (currentFilter.equals("SEMUA")) {

            cursor = db.getAllBooks();

        } else {

            cursor = db.getBooksByStatus(currentFilter);
        }

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

                // ================= SEARCH FILTER =================

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

        tvTotalBook.setText(
                list.size() + " Buku"
        );

        adapter.notifyDataSetChanged();

        // ================= RECYCLER ANIMATION =================

        recyclerKoleksi.setAlpha(0f);

        recyclerKoleksi.setTranslationY(60f);

        recyclerKoleksi.animate()
                .alpha(1f)
                .translationY(0f)
                .setDuration(400)
                .start();
    }

    // ================= ACTIVE TAB =================

    private void setActiveTab(TextView active) {

        TextView[] tabs = {

                tabSemua,

                tabBelum,

                tabSedang,

                tabSelesai
        };

        for (TextView tab : tabs) {

            tab.setBackgroundResource(
                    R.drawable.bg_tab
            );

            tab.setTextColor(
                    Color.parseColor("#666666")
            );
        }

        active.setBackgroundResource(
                R.drawable.bg_tab_active
        );

        active.setTextColor(
                Color.WHITE
        );
    }

    @Override
    protected void onResume() {

        super.onResume();

        loadBooks();
    }

    @Override
    public void finish() {

        super.finish();

        overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
        );
    }
}