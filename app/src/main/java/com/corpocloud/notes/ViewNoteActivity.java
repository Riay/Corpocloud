package com.corpocloud.notes;

import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ViewNoteActivity extends AppCompatActivity {

    TextView tvTitle, tvDate, tvDescription;
    Button btnEdit, btnDelete;

    private DatabaseHelper dbHelper;
    private long noteId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_view_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        tvTitle = findViewById(R.id.tvViewTitle);
        tvDate = findViewById(R.id.tvViewDate);
        tvDescription = findViewById(R.id.tvViewDescription);
        btnEdit = findViewById(R.id.btnEditNote);
        btnDelete = findViewById(R.id.btnDeleteNote);

        noteId = getIntent().getLongExtra("NOTE_ID", -1);

        if (noteId == -1) {
            Toast.makeText(this, "Error: No se encontró la nota", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        loadNoteDetails();

        btnEdit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ViewNoteActivity.this, AddNoteActivity.class);
                intent.putExtra("NOTE_ID", noteId);
                startActivity(intent);
                finish();
            }
        });

        btnDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });
    }

    private void loadNoteDetails() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(noteId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {

            String title = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE));

            String description = cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION));
            long dateInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));

            SimpleDateFormat sdf = new SimpleDateFormat("dd MMMM, yyyy", Locale.getDefault());
            String dateString = sdf.format(new Date(dateInMillis));

            tvTitle.setText(title);
            tvDescription.setText(description);
            tvDate.setText(dateString);

            cursor.close();
        }
        db.close();
    }

    private void showDeleteConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Confirmar Eliminación")
                .setMessage("¿Estás seguro de que deseas eliminar este apunte?")
                .setPositiveButton("Eliminar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        deleteNote();
                    }
                })
                .setNegativeButton("Cancelar", null)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .show();
    }

    private void deleteNote() {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        int rowsAffected = db.delete(DatabaseHelper.TABLE_NOTES,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(noteId)});
        db.close();

        if (rowsAffected > 0) {
            Toast.makeText(this, "Apunte eliminado", Toast.LENGTH_SHORT).show();
            finish();
        } else {
            Toast.makeText(this, "Error al eliminar el apunte", Toast.LENGTH_SHORT).show();
        }
    }

}