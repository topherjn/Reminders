package org.fountainhook.reminders;

import android.app.Dialog;
import android.database.Cursor;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.ActionMode;
import android.view.MenuInflater;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.sql.SQLException;

import static android.R.attr.mode;
import static org.fountainhook.reminders.R.id.parent;

public class RemindersActivity extends AppCompatActivity {

    private ListView mListView;
    private RemindersDbAdapter mDbAdapter;
    private RemindersSimpleCursorAdapter mCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reminders);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mListView = (ListView) findViewById(R.id.reminders_list_view);
        mListView.setDivider(null);

        mDbAdapter = new RemindersDbAdapter(this);
        try {
            mDbAdapter.open();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if(savedInstanceState == null) {
            mDbAdapter.deleteAllReminders();
            mDbAdapter.createReminder("Buy Learn Android Studio", false);
            mDbAdapter.createReminder("Abduct child in my silver Nissan Rogue", true);


        }

        Cursor cursor = mDbAdapter.fetchAllReminders();

        String[] from = new String[]{RemindersDbAdapter.COL_CONTENT};

        int[] to = new int[]{R.id.row_text};

        mCursorAdapter = new RemindersSimpleCursorAdapter(
                RemindersActivity.this,
                R.layout.reminders_row,
                cursor,
                from,
                to,
                0);

        mListView.setAdapter(mCursorAdapter);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                AlertDialog.Builder builder = new AlertDialog.Builder(RemindersActivity.this);
                ListView modeListView = new ListView(RemindersActivity.this);
                String [] modes = new String[] {"Edit reminder", "Delete Reminder"};
                ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(RemindersActivity.this, android.R.layout.simple_list_item_1, android.R.id.text1, modes);
                modeListView.setAdapter(modeAdapter);
                builder.setView(modeListView);
                final Dialog dialog = builder.create();
                dialog.show();
                modeListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        if(position == 0) {
                            Toast.makeText(RemindersActivity.this,"edit ", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(RemindersActivity.this,"delete ", Toast.LENGTH_SHORT).show();
                        }
                        dialog.dismiss();
                    }
                });
            }
        });

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {

            mListView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
           mListView.setMultiChoiceModeListener(new AbsListView.MultiChoiceModeListener() {
               @Override
               public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                   MenuInflater inflater = mode.getMenuInflater();
                   inflater.inflate(R.menu.cam_menu, menu);
                   return true;
               }

               @Override
               public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                   return false;
               }

               @Override
               public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                   switch(item.getItemId()) {
                       case R.id.menu_item_delete_reminder:
                           for(int nC = mCursorAdapter.getCount() - 1; nC >= 0; nC--) {
                               if (mListView.isItemChecked(nC)) {
                                   mDbAdapter.deleteReminderById(getIdfromPosition(nC));
                               }
                           }
                           mode.finish();
                           mCursorAdapter.changeCursor(mDbAdapter.fetchAllReminders());
                           return true;
                   }
                   return false;
               }

               @Override
               public void onDestroyActionMode(ActionMode mode) {

               }

               @Override
               public void onItemCheckedStateChanged(ActionMode mode, int position, long id, boolean checked) {

               }
           });
        }
    }

    private int getIdfromPosition(int nC) {
        return (int) mCursorAdapter.getItemId(nC);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_reminders, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_new:
                Log.d(getLocalClassName(), "create new Reminder");
                return true;
            case R.id.action_exit:
                finish();
                return true;
            default:
                return false;
        }
    }
}
