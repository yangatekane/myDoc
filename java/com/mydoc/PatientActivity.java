package com.mydoc;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.mydoc.dto.Patient;
import com.mydoc.notes.NotePad;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by yanga on 2013/11/13.
 */
public class PatientActivity extends MyDocActivity {
    private static final String TAG = PatientActivity.class.getName();
    private List<Patient> patients = new LinkedList<Patient>();
    private PatientAdapter patientAdapter;
    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;
    private Button btnView;
    private Button btnPrescribe;
    private static final String[] PROJECTION = new String[] {
            NotePad.Notes._ID, // 0
            NotePad.Notes.COLUMN_NAME_TITLE, // 1
    };

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.existing_patient_activity);
        ((Button)findViewById(R.id.btn_new_patiant)).setBackgroundResource(R.drawable.button_state_gray);
        ((Button)findViewById(R.id.btn_emergency_patient)).setBackgroundResource(R.drawable.button_state_gray);
        ((Button)findViewById(R.id.btn_existing_patiant)).setBackgroundResource(R.drawable.botton_selected);
        loadPatients();
        setupPatientTypes();
        patientAdapter = new PatientAdapter();
        ((ListView)findViewById(R.id.patient_list)).setAdapter(patientAdapter);
        setupDrawer();

        Intent intent = getIntent();

        if (intent.getData() == null) {
            intent.setData(NotePad.Notes.CONTENT_URI);
        }

        Cursor cursor = managedQuery(
                getIntent().getData(),
                PROJECTION,
                null,
                null,
                NotePad.Notes.DEFAULT_SORT_ORDER
        );

        String[] dataColumns = { NotePad.Notes.COLUMN_NAME_TITLE };

        int[] viewIDs = { R.id.repair_model };

        SimpleCursorAdapter adapter
                = new SimpleCursorAdapter(
                this,
                R.layout.available_jobs_view,
                cursor,
                dataColumns,
                viewIDs
        );

        ((GridView)findViewById(R.id.prescriptions_available)).setAdapter(adapter);

        ((GridView)findViewById(R.id.prescriptions_available)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Uri uri = ContentUris.withAppendedId(getIntent().getData(), id);

                String action = getIntent().getAction();

                if (Intent.ACTION_PICK.equals(action) || Intent.ACTION_GET_CONTENT.equals(action)) {
                    setResult(RESULT_OK, new Intent().setData(uri));
                } else {
                    startActivity(new Intent(Intent.ACTION_EDIT, uri));
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupPatientTypes() {
        ((Button)findViewById(R.id.btn_new_patiant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PatientActivity.this,HomeActivity.class));
            }
        });
    }

    private void setupDrawer(){
        drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.START);
        ((ListView)findViewById(R.id.left_drawer_services)).addHeaderView(getLayoutInflater().inflate(R.layout.header_left_menu_view, null));
        ((ListView)findViewById(R.id.left_drawer_services)).setAdapter(new LeftMenuAdapter());
        getActionBar().setDisplayHomeAsUpEnabled(true);
        getActionBar().setHomeButtonEnabled(true);
        drawerToggle = new ActionBarDrawerToggle(
                this,
                drawerLayout,
                R.drawable.ic_drawer,
                R.string.drawer_open,
                R.string.drawer_close
        ){
            public void onDrawerClosed(View view) {
                invalidateOptionsMenu();
            }

            public void onDrawerOpened(View drawerView) {
                invalidateOptionsMenu();
            }
        };
        drawerLayout.setDrawerListener(drawerToggle);
    }

    private void loadPatients(){
        new AsyncTask<Void, List<Patient>, List<Patient>>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected List<Patient> doInBackground(Void... voids) {
                List<Patient> cs = getPatientInfoManager().getPatients();
                if (cs!=null){
                    publishProgress(cs);
                }
                return cs;
            }

            @Override
            protected void onProgressUpdate(List<Patient>... values) {
                patients = values[0];
                patientAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(List<Patient> cs) {
                if (cs!=null){
                    patients = cs;
                    patientAdapter.notifyDataSetChanged();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }

    private class PatientAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return patients.size();
        }

        @Override
        public Patient getItem(int i) {
            return patients.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.patient_view, null);
            }
            btnView = (Button)view.findViewById(R.id.btnView);
            btnPrescribe = (Button)view.findViewById(R.id.btnPrescribe);

            btnView.setTag(getItem(i));
            btnPrescribe.setTag(getItem(i));

            ((TextView)view.findViewById(R.id.txtTitle)).setText(getItem(i).getName() + " " + getItem(i).getSurname());
            ((TextView)view.findViewById(R.id.txtDescription)).setText(getItem(i).getPcase());
            try{
                ((ImageView)view.findViewById(R.id.imgAvatar)).setImageBitmap(BitmapFactory.decodeFile(getItem(i).getPatientContent().get(getItem(i).getName()).get(0).file));
            }catch (IndexOutOfBoundsException e){
                Log.e(TAG,e.getMessage(),e);
                ((ImageView) view.findViewById(R.id.imgAvatar)).setImageResource(R.drawable.avatar_blank);
            }
            ((Button)view.findViewById(R.id.btnPrescribe)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(PatientActivity.this, NotesList.class));
                }
            });
            return view;
        }
    }

    private class LeftMenuAdapter extends BaseAdapter{

        @Override
        public int getCount() {
            return getResources().getStringArray(R.array.left_menu).length;
        }

        @Override
        public String getItem(int i) {
            return getResources().getStringArray(R.array.left_menu)[i];
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            if (view == null){
                view = getLayoutInflater().inflate(R.layout.left_menu_view,null);
            }
            switch (i){
                case 0:
                    ((ImageView)view.findViewById(R.id.left_menu_icon)).setImageResource(R.drawable.asociates);
                    ((TextView)view.findViewById(R.id.menu_left_description)).setText(getItem(i));
                    break;
                case 1:
                    ((ImageView)view.findViewById(R.id.left_menu_icon)).setImageResource(R.drawable.events);
                    ((TextView)view.findViewById(R.id.menu_left_description)).setText(getItem(i));
                    break;
                case 2:
                    ((ImageView)view.findViewById(R.id.left_menu_icon)).setImageResource(R.drawable.locations);
                    ((TextView)view.findViewById(R.id.menu_left_description)).setText(getItem(i));
                    break;
                case 3:
                    ((ImageView)view.findViewById(R.id.left_menu_icon)).setImageResource(R.drawable.community);
                    ((TextView)view.findViewById(R.id.menu_left_description)).setText(getItem(i));
                    break;
            }
            return view;
        }
    }
}