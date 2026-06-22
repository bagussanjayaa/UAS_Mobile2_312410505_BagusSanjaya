package com.example.mylibrary;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;

    ArrayList<Book> list;
    BookAdapter adapter;
    DatabaseHelper db;

    EditText etSearch;
    ImageView btnMenu;

    TextView tvGreeting, tvSubGreeting;
    TextView tvQuote, tvAuthorQuote;
    TextView tvStreak;

    LinearLayout btnReadingToday;
    LinearLayout layoutFavorite;

    LinearLayout navHome, navFavorite, navCollection, navAdd;

    TextView btnSeeAllFavorite, btnSeeAllCollection;

    String currentFilter = "SEMUA";
    String currentKeyword = "";

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().setStatusBarColor(Color.parseColor("#1976D2"));

        setContentView(R.layout.activity_main);

        initView();

        db = new DatabaseHelper(this);

        pref = getSharedPreferences("MY_LIBRARY", MODE_PRIVATE);

        list = new ArrayList<>();

        adapter = new BookAdapter(this, list);

        recyclerView.setLayoutManager(
                new LinearLayoutManager(
                        this,
                        LinearLayoutManager.HORIZONTAL,
                        false
                )
        );

        recyclerView.setHasFixedSize(true);

        recyclerView.setAdapter(adapter);

        setupUI();

        loadData();

        loadFavoriteBooks();
    }

    // ================= INIT VIEW =================

    private void initView() {

        recyclerView = findViewById(R.id.recyclerView);


        etSearch = findViewById(R.id.etSearch);

        btnMenu = findViewById(R.id.btnMenu);

        tvGreeting = findViewById(R.id.tvGreeting);
        tvSubGreeting = findViewById(R.id.tvSubGreeting);

        tvQuote = findViewById(R.id.tvQuote);
        tvAuthorQuote = findViewById(R.id.tvAuthorQuote);

        tvStreak = findViewById(R.id.tvStreak);

        btnReadingToday = findViewById(R.id.btnReadingToday);

        layoutFavorite = findViewById(R.id.layoutFavorite);

        btnSeeAllFavorite = findViewById(R.id.btnSeeAllFavorite);
        btnSeeAllCollection = findViewById(R.id.btnSeeAllCollection);

        navHome = findViewById(R.id.navHome);
        navFavorite = findViewById(R.id.navFavorite);
        navCollection = findViewById(R.id.navCollection);
        navAdd = findViewById(R.id.navAdd);
    }

    // ================= UI =================

    private void setupUI() {

        setupGreeting();
        setupQuote();
        setupStreak();

        // ================= SEARCH =================

        etSearch.setOnEditorActionListener((v, actionId, event) -> {

            boolean isEnter =
                    event != null &&
                            event.getKeyCode() == KeyEvent.KEYCODE_ENTER &&
                            event.getAction() == KeyEvent.ACTION_DOWN;

            if (actionId == EditorInfo.IME_ACTION_SEARCH || isEnter) {

                currentKeyword = etSearch.getText().toString().trim();

                loadData();

                return true;
            }

            return false;
        });

        // ================= MENU =================

        btnMenu.setOnClickListener(this::showPopupMenu);

        // ================= TAB =================


        // ================= STREAK =================

        btnReadingToday.setOnClickListener(v -> {

            String today = new SimpleDateFormat(
                    "yyyyMMdd",
                    Locale.getDefault()
            ).format(new Date());

            String lastDate = pref.getString("LAST_READ_DATE", "");

            int streak = pref.getInt("STREAK", 0);

            if (lastDate.equals(today)) {

                Toast.makeText(
                        this,
                        "Sudah baca hari ini 🔥",
                        Toast.LENGTH_SHORT
                ).show();

                return;
            }

            streak++;

            pref.edit()
                    .putInt("STREAK", streak)
                    .putString("LAST_READ_DATE", today)
                    .apply();

            tvStreak.setText(String.valueOf(streak));
        });

        // ================= NAVIGATION =================

        navHome.setOnClickListener(v ->
                Toast.makeText(this, "Beranda", Toast.LENGTH_SHORT).show()
        );

        navFavorite.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritActivity.class))
        );

        navCollection.setOnClickListener(v ->
                startActivity(new Intent(this, KoleksiActivity.class))
        );

        navAdd.setOnClickListener(v ->
                startActivity(new Intent(this, AddBookActivity.class))
        );

        // ================= SEE ALL =================

        btnSeeAllFavorite.setOnClickListener(v ->
                startActivity(new Intent(this, FavoritActivity.class))
        );

        btnSeeAllCollection.setOnClickListener(v ->
                startActivity(new Intent(this, KoleksiActivity.class))
        );
    }

    // ================= FAVORITE =================

    // ================= FAVORITE =================

    void loadFavoriteBooks() {

        if (layoutFavorite == null) return;

        layoutFavorite.removeAllViews();

        Cursor cursor = db.getFavoriteBooks();

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String title = cursor.getString(1);

                String author = cursor.getString(2);

                String cover = cursor.getString(7);

                // ================= CARD =================

                LinearLayout card = new LinearLayout(this);

                card.setOrientation(LinearLayout.HORIZONTAL);

                card.setPadding(20, 20, 20, 20);

                card.setBackgroundResource(R.drawable.bg_card);

                card.setElevation(6f);

                LinearLayout.LayoutParams cardParams =
                        new LinearLayout.LayoutParams(
                                520,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        );

                cardParams.setMargins(0, 0, 24, 0);

                card.setClickable(true);
                card.setFocusable(true);

                card.setLayoutParams(cardParams);

                // ================= IMAGE =================

                ImageView img = new ImageView(this);

                LinearLayout.LayoutParams imgParams =
                        new LinearLayout.LayoutParams(180, 250);

                img.setLayoutParams(imgParams);

                img.setScaleType(ImageView.ScaleType.CENTER_CROP);

                if (cover != null && !cover.isEmpty()) {

                    try {

                        img.setImageURI(Uri.parse(cover));

                    } catch (Exception e) {

                        img.setImageResource(R.drawable.book);
                    }

                } else {

                    img.setImageResource(R.drawable.book);
                }

                // ================= RIGHT CONTENT =================

                LinearLayout rightLayout = new LinearLayout(this);

                rightLayout.setOrientation(LinearLayout.VERTICAL);

                rightLayout.setPadding(24, 0, 0, 0);

                rightLayout.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                0,
                                LinearLayout.LayoutParams.WRAP_CONTENT,
                                1
                        )
                );

                // ================= FAVORITE LABEL =================

                TextView tvFav = new TextView(this);

                tvFav.setText("⭐ Favorite");

                tvFav.setTextColor(Color.parseColor("#1976D2"));

                tvFav.setTextSize(13);

                // ================= TITLE =================

                TextView tvTitle = new TextView(this);

                tvTitle.setText(title);
                tvTitle.setTextSize(15);
                tvTitle.setTypeface(null, android.graphics.Typeface.BOLD);

                tvTitle.setMaxLines(2); // maksimal 2 baris
                tvTitle.setEllipsize(android.text.TextUtils.TruncateAt.END);

                tvTitle.setLayoutParams(
                        new LinearLayout.LayoutParams(
                                LinearLayout.LayoutParams.MATCH_PARENT,
                                LinearLayout.LayoutParams.WRAP_CONTENT
                        )
                );
                // ================= AUTHOR =================

                TextView tvAuthor = new TextView(this);

                tvAuthor.setText(author);

                tvAuthor.setTextColor(Color.DKGRAY);

                tvAuthor.setTextSize(15);

                tvAuthor.setPadding(0, 10, 0, 0);

                // ================= ADD VIEW =================

                rightLayout.addView(tvFav);

                rightLayout.addView(tvTitle);

                rightLayout.addView(tvAuthor);

                card.addView(img);

                card.addView(rightLayout);

                final int bookId = cursor.getInt(0);

                card.setOnClickListener(v -> {

                    Intent intent =
                            new Intent(MainActivity.this,
                                    DetailBookActivity.class);

                    intent.putExtra("id", bookId);

                    startActivity(intent);
                });

                layoutFavorite.addView(card);

            } while (cursor.moveToNext());
        }

        if (cursor != null) {

            cursor.close();
        }
    }

    // ================= LOAD DATA =================

    private void loadData() {

        list.clear();

        Cursor cursor = db.getAllBooks();

        if (cursor != null && cursor.moveToFirst()) {

            do {

                String title = cursor.getString(1);
                String author = cursor.getString(2);

                if (title.toLowerCase().contains(currentKeyword.toLowerCase())
                        || author.toLowerCase().contains(currentKeyword.toLowerCase())) {

                    list.add(
                            new Book(
                                    cursor.getInt(0),
                                    title,
                                    author,
                                    cursor.getString(3),
                                    cursor.getString(5),
                                    cursor.getString(7),
                                    cursor.getInt(9) == 1
                            )
                    );
                }

            } while (cursor.moveToNext());
        }

        if (cursor != null) {
            cursor.close();
        }

        adapter.notifyDataSetChanged();
    }

    // ================= GREETING =================

    private void setupGreeting() {

        int hour = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);

        if (hour < 12) {

            tvGreeting.setText("Good Morning ☀️");

        } else if (hour < 16) {

            tvGreeting.setText("Good Afternoon 🌤️");

        } else if (hour < 19) {

            tvGreeting.setText("Good Evening 🌇");

        } else {

            tvGreeting.setText("Good Night 🌙");
        }
    }

    // ================= QUOTE =================

    private void setupQuote() {

        String[] quotes = {
                "Books are magic.",
                "Read more, know more.",
                "Today reader, tomorrow leader.",
                "Reading is dreaming."
        };

        int index =
                Calendar.getInstance().get(Calendar.DAY_OF_YEAR)
                        % quotes.length;

        tvQuote.setText(quotes[index]);

        tvAuthorQuote.setText("- My Library");
    }

    // ================= STREAK =================

    private void setupStreak() {

        tvStreak.setText(
                String.valueOf(
                        pref.getInt("STREAK", 0)
                )
        );
    }



    // ================= POPUP MENU =================

    private void showPopupMenu(View view) {

        PopupMenu popupMenu = new PopupMenu(this, view);

        popupMenu.getMenu().add("Chat AI Buku");

        popupMenu.getMenu().add("Statistik Buku");

        popupMenu.setOnMenuItemClickListener(item -> {

            if (item.getTitle().equals("Chat AI Buku")) {

                startActivity(
                        new Intent(this, ChatActivity.class)
                );

            } else {

                startActivity(
                        new Intent(this, StatistikActivity.class)
                );
            }

            return true;
        });

        popupMenu.show();
    }

    @Override
    protected void onResume() {
        super.onResume();

        loadData();

        loadFavoriteBooks();
    }
}