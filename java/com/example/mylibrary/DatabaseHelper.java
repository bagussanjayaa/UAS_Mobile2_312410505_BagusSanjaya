package com.example.mylibrary;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "library.db";
    private static final int DATABASE_VERSION = 3;

    public static final String TABLE_BOOK = "books";

    public static final String COL_ID = "id";
    public static final String COL_TITLE = "title";
    public static final String COL_AUTHOR = "author";
    public static final String COL_GENRE = "genre";
    public static final String COL_PAGES = "pages";
    public static final String COL_STATUS = "status";
    public static final String COL_NOTE = "note";
    public static final String COL_COVER = "cover";
    public static final String COL_EBOOK = "ebook";

    public static final String COL_FAVORITE = "is_favorite";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    // ================= CREATE =================
    @Override
    public void onCreate(SQLiteDatabase db) {

        String sql = "CREATE TABLE " + TABLE_BOOK + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT," +
                "title TEXT," +
                "author TEXT," +
                "genre TEXT," +
                "pages INTEGER," +
                "status TEXT," +
                "note TEXT," +
                "cover TEXT," +
                "ebook TEXT," +
                "is_favorite INTEGER DEFAULT 0" +
                ")";

        db.execSQL(sql);
    }

    // ================= UPGRADE (FIX AMAN) =================
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_BOOK);
        onCreate(db);
    }

    // ================= INSERT =================
    public boolean insertBook(
            String title,
            String author,
            String genre,
            int pages,
            String status,
            String note,
            String cover,
            String ebook
    ) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_TITLE, title);
        cv.put(COL_AUTHOR, author);
        cv.put(COL_GENRE, genre);
        cv.put(COL_PAGES, pages);
        cv.put(COL_STATUS, status);
        cv.put(COL_NOTE, note);
        cv.put(COL_COVER, cover);
        cv.put(COL_EBOOK, ebook);
        cv.put(COL_FAVORITE, 0); // default

        return db.insert(TABLE_BOOK, null, cv) > 0;
    }

    // ================= GET ALL =================
    public Cursor getAllBooks() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_BOOK + " ORDER BY id DESC",
                null
        );
    }

    // ================= GET FAVORITE (FIXED CORE) =================
    public Cursor getFavoriteBooks() {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_BOOK +
                        " WHERE is_favorite = 1 ORDER BY id DESC",
                null
        );
    }

    // ================= GET BY ID =================
    public Cursor getBookById(int id) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_BOOK + " WHERE id=?",
                new String[]{String.valueOf(id)}
        );
    }

    // ================= GET BY STATUS =================
    public Cursor getBooksByStatus(String status) {
        return getReadableDatabase().rawQuery(
                "SELECT * FROM " + TABLE_BOOK + " WHERE status=?",
                new String[]{status}
        );
    }

    // ================= UPDATE BOOK =================
    public boolean updateBook(
            int id,
            String title,
            String author,
            String genre,
            int pages,
            String status,
            String note,
            String cover,
            String ebook
    ) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_TITLE, title);
        cv.put(COL_AUTHOR, author);
        cv.put(COL_GENRE, genre);
        cv.put(COL_PAGES, pages);
        cv.put(COL_STATUS, status);
        cv.put(COL_NOTE, note);
        cv.put(COL_COVER, cover);
        cv.put(COL_EBOOK, ebook);

        return db.update(TABLE_BOOK, cv, "id=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // ================= TOGGLE FAVORITE (CORE FIX) =================
    public boolean updateFavorite(int id, boolean isFavorite) {

        SQLiteDatabase db = getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put(COL_FAVORITE, isFavorite ? 1 : 0);

        return db.update(TABLE_BOOK, cv, "id=?",
                new String[]{String.valueOf(id)}) > 0;
    }

    // ================= DELETE =================
    public boolean deleteBook(int id) {

        return getWritableDatabase().delete(
                TABLE_BOOK,
                "id=?",
                new String[]{String.valueOf(id)}
        ) > 0;
    }
}