package com.corpocloud.notes;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class ListNoteActivity extends AppCompatActivity {

    ListView listViewNotes;
    DatabaseHelper dbHelper;
    SimpleCursorAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_note);

        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        dbHelper = new DatabaseHelper(this);
        listViewNotes = findViewById(R.id.listViewNotes);

        String[] fromColumns = {DatabaseHelper.COLUMN_TITLE, DatabaseHelper.COLUMN_DATE};
        int[] toViews = {R.id.tvNoteTitle, R.id.tvNoteDate};

        adapter = new SimpleCursorAdapter(
                this,
                R.layout.list_item_note,
                null,
                fromColumns,
                toViews,
                0
        );

        adapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {
            @Override
            public boolean setViewValue(View view, Cursor cursor, int columnIndex) {
                if (view.getId() == R.id.tvNoteDate) {
                    long dateInMillis = cursor.getLong(columnIndex);
                    SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                    ((android.widget.TextView) view).setText(sdf.format(new Date(dateInMillis)));
                    return true;
                }
                return false;
            }
        });

        listViewNotes.setAdapter(adapter);

        listViewNotes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(ListNoteActivity.this, ViewNoteActivity.class);
                intent.putExtra("NOTE_ID", id);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadNotes();
    }

    private void loadNotes() {
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                DatabaseHelper.COLUMN_ID + " as _id",
                DatabaseHelper.COLUMN_TITLE,
                DatabaseHelper.COLUMN_DATE
        };

        Cursor cursor = db.query(
                DatabaseHelper.TABLE_NOTES,
                projection,
                null,
                null,
                null,
                null,
                DatabaseHelper.COLUMN_DATE + " DESC"
        );

        adapter.changeCursor(cursor);
    }
}