package com.corpocloud.notes;

import android.app.DatePickerDialog;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Locale;

public class AddNoteActivity extends AppCompatActivity {

    EditText etTitle, etDescription;
    Button btnPickDate, btnSaveNote;
    TextView tvSelectedDate;

    private DatabaseHelper dbHelper;
    private Calendar calendar;
    private long noteId = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        calendar = Calendar.getInstance();

        etTitle = findViewById(R.id.etTitle);
        etDescription = findViewById(R.id.etDescription);
        btnPickDate = findViewById(R.id.btnPickDate);
        btnSaveNote = findViewById(R.id.btnSaveNote);
        tvSelectedDate = findViewById(R.id.tvSelectedDate);

        noteId = getIntent().getLongExtra("NOTE_ID", -1);

        if (noteId != -1) {
            setTitle("Editar Apunte");
            btnSaveNote.setText("Actualizar Apunte");
            loadNoteData();
        } else {
            setTitle("Añadir Apunte");
            updateDateText();
        }

        btnPickDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePicker();
            }
        });

        btnSaveNote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveNote();
            }
        });
    }

    private void showDatePicker() {
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                updateDateText();
            }
        };

        new DatePickerDialog(this, dateSetListener,
                calendar.get(Calendar.YEAR),
                calendar.get(Calendar.MONTH),
                calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void updateDateText() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
        tvSelectedDate.setText("Fecha seleccionada: " + sdf.format(calendar.getTime()));
    }

    private void loadNoteData() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query(DatabaseHelper.TABLE_NOTES,
                null,
                DatabaseHelper.COLUMN_ID + " = ?",
                new String[]{String.valueOf(noteId)},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            etTitle.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_TITLE)));
            etDescription.setText(cursor.getString(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DESCRIPTION)));
            long dateInMillis = cursor.getLong(cursor.getColumnIndexOrThrow(DatabaseHelper.COLUMN_DATE));
            calendar.setTimeInMillis(dateInMillis);
            updateDateText();
            cursor.close();
        }
        db.close();
    }

    private void saveNote() {
        String title = etTitle.getText().toString().trim();
        String description = etDescription.getText().toString().trim();
        long date = calendar.getTimeInMillis();

        if (title.isEmpty()) {
            Toast.makeText(this, "El título no puede estar vacío", Toast.LENGTH_SHORT).show();
            return;
        }

        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(DatabaseHelper.COLUMN_TITLE, title);
        values.put(DatabaseHelper.COLUMN_DESCRIPTION, description);
        values.put(DatabaseHelper.COLUMN_DATE, date);

        if (noteId == -1) {
            long newRowId = db.insert(DatabaseHelper.TABLE_NOTES, null, values);
            if (newRowId != -1) {
                Toast.makeText(this, "Apunte guardado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al guardar", Toast.LENGTH_SHORT).show();
            }
        } else {
            int rowsAffected = db.update(DatabaseHelper.TABLE_NOTES, values,
                    DatabaseHelper.COLUMN_ID + " = ?",
                    new String[]{String.valueOf(noteId)});

            if (rowsAffected > 0) {
                Toast.makeText(this, "Apunte actualizado", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "Error al actualizar", Toast.LENGTH_SHORT).show();
            }
        }

        db.close();
        finish();
    }
}