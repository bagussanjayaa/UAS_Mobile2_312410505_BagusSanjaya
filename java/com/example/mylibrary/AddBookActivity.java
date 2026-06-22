package com.example.mylibrary;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.widget.*;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;

public class AddBookActivity extends AppCompatActivity {

    EditText etTitle, etAuthor, etGenre, etPages, etNote;
    Button btnBelum, btnSedang, btnSelesai, btnPickEbook, btnPickImage;
    ImageView btnBack, headerSave, imgCover;

    String selectedStatus = "BELUM";
    String coverPath = "", ebookPath = "";

    DatabaseHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 🔥 STATUS BAR BIRU
        getWindow().setStatusBarColor(
                Color.parseColor("#1976D2")
        );

        setContentView(R.layout.activity_add_book);

        etTitle = findViewById(R.id.etTitle);
        etAuthor = findViewById(R.id.etAuthor);
        etGenre = findViewById(R.id.etGenre);
        etPages = findViewById(R.id.etPages);
        etNote = findViewById(R.id.etNote);

        btnBelum = findViewById(R.id.btnBelum);
        btnSedang = findViewById(R.id.btnSedang);
        btnSelesai = findViewById(R.id.btnSelesai);

        btnBack = findViewById(R.id.btnBack);
        headerSave = findViewById(R.id.headerSave);

        imgCover = findViewById(R.id.imgCover);
        btnPickImage = findViewById(R.id.btnPickImage);
        btnPickEbook = findViewById(R.id.btnPickPdf);

        db = new DatabaseHelper(this);

        btnBack.setOnClickListener(v -> finish());
        headerSave.setOnClickListener(v -> saveBook());

        imgCover.setOnClickListener(v -> openGallery());
        btnPickImage.setOnClickListener(v -> openGallery());

        btnPickEbook.setOnClickListener(v -> pickPdf.launch("application/pdf"));

        btnBelum.setOnClickListener(v -> {
            selectedStatus = "BELUM";
            updateStatusUI();
        });

        btnSedang.setOnClickListener(v -> {
            selectedStatus = "SEDANG";
            updateStatusUI();
        });

        btnSelesai.setOnClickListener(v -> {
            selectedStatus = "SELESAI";
            updateStatusUI();
        });

        updateStatusUI();
    }

    // ========================
    // STATUS UI
    // ========================
    private void updateStatusUI() {

        btnBelum.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#FFCDD2")));

        btnSedang.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#FFE0B2")));

        btnSelesai.setBackgroundTintList(
                android.content.res.ColorStateList.valueOf(
                        android.graphics.Color.parseColor("#C8E6C9")));

        switch (selectedStatus) {
            case "BELUM":
                btnBelum.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#EF5350")));
                break;

            case "SEDANG":
                btnSedang.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#FB8C00")));
                break;

            case "SELESAI":
                btnSelesai.setBackgroundTintList(
                        android.content.res.ColorStateList.valueOf(
                                android.graphics.Color.parseColor("#43A047")));
                break;
        }
    }

    // ========================
    // OPEN GALLERY (BALIK KE GALERI)
    // ========================
    private void openGallery() {
        Intent intent = new Intent(
                Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        );
        pickImageLauncher.launch(intent);
    }

    // ========================
    // COPY IMAGE KE INTERNAL STORAGE
    // ========================
    private String saveImageToInternal(Uri uri) {
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);

            String fileName = "cover_" + System.currentTimeMillis() + ".jpg";
            File file = new File(getFilesDir(), fileName);

            FileOutputStream outputStream = new FileOutputStream(file);

            byte[] buffer = new byte[1024];
            int length;

            while ((length = inputStream.read(buffer)) > 0) {
                outputStream.write(buffer, 0, length);
            }

            outputStream.close();
            inputStream.close();

            return file.getAbsolutePath();

        } catch (Exception e) {
            e.printStackTrace();
            return "";
        }
    }

    // ========================
    // IMAGE PICK RESULT
    // ========================
    ActivityResultLauncher<Intent> pickImageLauncher =
            registerForActivityResult(
                    new ActivityResultContracts.StartActivityForResult(),
                    result -> {
                        if (result.getResultCode() == RESULT_OK &&
                                result.getData() != null) {

                            Uri uri = result.getData().getData();

                            if (uri != null) {

                                // 🔥 simpan ke internal (fix hilang)
                                coverPath = saveImageToInternal(uri);

                                // tampilkan preview
                                imgCover.setImageURI(uri);
                            }
                        }
                    });

    // ========================
    // PDF PICKER
    // ========================
    ActivityResultLauncher<String> pickPdf =
            registerForActivityResult(
                    new ActivityResultContracts.GetContent(),
                    uri -> {
                        if (uri != null) {
                            ebookPath = uri.toString();
                            Toast.makeText(this,
                                    "Ebook dipilih",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });

    // ========================
    // SAVE BOOK
    // ========================
    private void saveBook() {

        String title = etTitle.getText().toString().trim();
        String author = etAuthor.getText().toString().trim();
        String genre = etGenre.getText().toString().trim();
        String note = etNote.getText().toString().trim();

        int pages = 0;

        if (TextUtils.isEmpty(title)) {
            etTitle.setError("Judul wajib diisi");
            return;
        }

        if (TextUtils.isEmpty(author)) {
            etAuthor.setError("Penulis wajib diisi");
            return;
        }

        try {
            if (!etPages.getText().toString().trim().isEmpty()) {
                pages = Integer.parseInt(etPages.getText().toString().trim());
            }
        } catch (Exception e) {
            pages = 0;
        }

        boolean result = db.insertBook(
                title,
                author,
                genre,
                pages,
                selectedStatus,
                note,
                coverPath,
                ebookPath
        );

        if (result) {
            Toast.makeText(this,
                    "Buku berhasil ditambah",
                    Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this,
                    "Gagal menambah buku",
                    Toast.LENGTH_SHORT).show();
        }
    }
}