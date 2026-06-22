package com.example.mylibrary;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.widget.*;
import androidx.appcompat.app.AppCompatActivity;

public class DetailBookActivity extends AppCompatActivity {

    EditText etTitle, etAuthor, etGenre, etPages, etNote;
    ImageView imgCover, btnBack, btnSave;
    Button btnPickImage, btnPickPdf, btnOpenPdf;
    Button btnBelum, btnSedang, btnSelesai;

    DatabaseHelper db;

    int bookId = -1;

    String selectedStatus = "BELUM";
    String coverUri = "";
    String pdfUri = "";

    final int PICK_IMAGE = 101;
    final int PICK_PDF = 102;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_detail_book);

        // INIT VIEW
        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        etPages = findViewById(R.id.etPages);
        etNote = findViewById(R.id.etNote);

        imgCover = findViewById(R.id.imgCover);
        btnBack = findViewById(R.id.btnBack);
        btnSave = findViewById(R.id.btnSave);

        btnPickImage = findViewById(R.id.btnPickImage);
        btnPickPdf = findViewById(R.id.btnPickPdf);
        btnOpenPdf = findViewById(R.id.btnOpenPdf);

        btnBelum = findViewById(R.id.btnBelum);
        btnSedang = findViewById(R.id.btnSedang);
        btnSelesai = findViewById(R.id.btnSelesai);

        db = new DatabaseHelper(this);

        // AMBIL ID
        bookId = getIntent().getIntExtra("id", -1);

        if (bookId == -1) {
            Toast.makeText(this, "ID Buku tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadData();

        // EVENT
        btnBack.setOnClickListener(v -> finish());

        btnSave.setOnClickListener(v -> updateBook());

        btnPickImage.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("image/*");
            startActivityForResult(i, PICK_IMAGE);
        });

        btnPickPdf.setOnClickListener(v -> {
            Intent i = new Intent(Intent.ACTION_OPEN_DOCUMENT);
            i.setType("application/pdf");
            startActivityForResult(i, PICK_PDF);
        });

        btnOpenPdf.setOnClickListener(v -> openPdf());

        btnBelum.setOnClickListener(v -> {
            selectedStatus = "BELUM";
            setStatusActive();
        });

        btnSedang.setOnClickListener(v -> {
            selectedStatus = "SEDANG";
            setStatusActive();
        });

        btnSelesai.setOnClickListener(v -> {
            selectedStatus = "SELESAI";
            setStatusActive();
        });
    }

    // =========================
    // LOAD DATA (FIX TOTAL)
    // =========================
    private void loadData() {

        Cursor c = db.getBookById(bookId);

        if (c != null && c.moveToFirst()) {

            etTitle.setText(safeString(c, 1));
            etAuthor.setText(safeString(c, 2));
            etGenre.setText(safeString(c, 3));

            int pages = safeInt(c, 4);
            etPages.setText(pages == 0 ? "" : String.valueOf(pages));

            selectedStatus = safeString(c, 5);
            etNote.setText(safeString(c, 6));

            coverUri = safeString(c, 7);
            pdfUri = safeString(c, 8);

            if (!coverUri.isEmpty()) {
                try {
                    imgCover.setImageURI(Uri.parse(coverUri));
                } catch (Exception e) {
                    imgCover.setImageResource(R.drawable.book);
                }
            } else {
                imgCover.setImageResource(R.drawable.book);
            }

            setStatusActive();
            c.close();

        } else {
            Toast.makeText(this, "Data tidak ditemukan", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    // =========================
    // UPDATE
    // =========================
    private void updateBook() {

        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        int pages = 0;

        try {
            String p = etPages.getText().toString().trim();
            if (!p.isEmpty()) {
                pages = Integer.parseInt(p);
            }
        } catch (Exception e) {
            pages = 0;
        }

        boolean result = db.updateBook(
                bookId,
                title,
                author,
                genre,
                pages,
                selectedStatus,
                note,
                coverUri,
                pdfUri
        );

        if (result) {
            Toast.makeText(this, "Buku berhasil diperbarui", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Gagal update", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================
    // OPEN PDF (SAFE)
    // =========================
    private void openPdf() {

        if (pdfUri == null || pdfUri.isEmpty()) {
            Toast.makeText(this, "File e-book belum ada", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            Intent i = new Intent(Intent.ACTION_VIEW);
            i.setDataAndType(Uri.parse(pdfUri), "application/pdf");
            i.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
            startActivity(i);
        } catch (Exception e) {
            Toast.makeText(this, "Tidak ada aplikasi PDF", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================
    // STATUS UI
    // =========================
    private void setStatusActive() {

        btnBelum.setAlpha(0.4f);
        btnSedang.setAlpha(0.4f);
        btnSelesai.setAlpha(0.4f);

        if (selectedStatus.equals("BELUM")) {
            btnBelum.setAlpha(1f);
        } else if (selectedStatus.equals("SEDANG")) {
            btnSedang.setAlpha(1f);
        } else {
            btnSelesai.setAlpha(1f);
        }
    }

    // =========================
    // RESULT
    // =========================
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (resultCode == RESULT_OK && data != null) {

            Uri uri = data.getData();
            if (uri == null) return;

            if (requestCode == PICK_IMAGE) {
                coverUri = uri.toString();
                imgCover.setImageURI(uri);
            }

            if (requestCode == PICK_PDF) {
                pdfUri = uri.toString();
                Toast.makeText(this, "PDF dipilih", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // =========================
    // SAFE HELPER
    // =========================
    private String safeString(Cursor c, int index) {
        return c.isNull(index) ? "" : c.getString(index);
    }

    private int safeInt(Cursor c, int index) {
        return c.isNull(index) ? 0 : c.getInt(index);
    }
}