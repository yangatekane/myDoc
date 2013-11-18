package com.mydoc;

import android.app.ActivityOptions;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.mydoc.dto.Content;
import com.mydoc.dto.Patient;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.SynchronousQueue;

import retrofit.mime.TypedFile;

public class HomeActivity extends MyDocActivity{
    private static final String TAG = HomeActivity.class.getName();
    private List<Content> contents = new LinkedList<Content>();
    private final int TAKE_PATIENT_PICTURE_REQUEST_CODE = 100;
    private final int BROWSE_PATIENT_PICTURE_REQUEST_CODE = 101;
    private String filePath;
    private Uri fileUri;
    private FileUplaodAdapter fileUplaodAdapter;
    private String typeOfProfession;

    private Patient patient;
    private String pName;
    private String pSurname;
    private String pCase;
    private String pPaymentType;
    private String pAdmittionDate;
    private boolean hasSigned;

    private EditText pname;
    private EditText psurname;
    private EditText pcase;
    private Spinner day;
    private Spinner month;
    private Spinner year;
    private Spinner ppayment;

    private DrawerLayout drawerLayout;
    private ActionBarDrawerToggle drawerToggle;

    private TextToSpeech tts;

    private static final String BASIC_INFO_INSTRUCTION="Please fill in information on the left panel";

    private static final String ORAL_EXAMINATION = "Your oral examination is about to start";

    private static final String MOUTH_STRAIGHT_OPEN = "Please open your mouth wide and face to towards the camera, and take a picture";
    private static final String MOUTH_LEFT_OPEN = "Please open your mouth wide and turn your head to the left, and take a picture";
    private static final String MOUTH_RIGHT_OPEN = "Please open your mouth wide and turn your head to the right, and take a picture";
    private static final String MOUTH_UP_OPEN = "Please open your mouth wide and tilt your head upwards, and take a picture";
    private static final String MOUTH_UP_DOWN = "Please open your mouth wide and tilt your head downwards, and take a picture";

    private static final String ORAL_EXAMINATION_COMPLETE = "Your oral examination is complete";

    private static final String GENERAL_MED_CASE = "Please take a picture of the medical condition you have," +
            " if it visible";

    private static final String OTHO_CASE = "Please take a picture of the X-Ray scan";

    private static final String THANK_YOU_GESTURE = "Thank you";

    private Handler handler = new Handler();
    private BlockingQueue<String> cavityExaminationQueue = new LinkedBlockingQueue<String>();
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.home_activity);
        typeOfProfession = getString(R.string.medical);
        tts = new TextToSpeech(HomeActivity.this,new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int i) {

            }
        });
        tts.setLanguage(Locale.ENGLISH);
        ((Button)findViewById(R.id.btn_new_patiant)).setBackgroundResource(R.drawable.botton_selected);
        ((Button)findViewById(R.id.btn_emergency_patient)).setBackgroundResource(R.drawable.button_state_gray);
        ((Button)findViewById(R.id.btn_existing_patiant)).setBackgroundResource(R.drawable.button_state_gray);
        loadContents();
        setupPatientTypes();
        setupDiagnosticOptions();
        setupAdapters();
        pname = ((EditText) findViewById(R.id.edit_patient_name));
        psurname = ((EditText) findViewById(R.id.edit_patient_surname));
        pcase = ((EditText) findViewById(R.id.edit_patient_case));
        ppayment = ((Spinner) findViewById(R.id.spin_payment_type));

        day = ((Spinner) findViewById(R.id.spin_warranty_day));
        month = ((Spinner) findViewById(R.id.spin_warranty_month));
        year = ((Spinner) findViewById(R.id.spin_warranty_year));


        ((Button)findViewById(R.id.btn_next)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pName = pname.getText().toString();
                pSurname = psurname.getText().toString();
                pCase = pcase.getText().toString();
                pAdmittionDate = day.getSelectedItem()+" "
                        +month.getSelectedItem()+ " "
                        +year.getSelectedItem();
                pPaymentType = (String)ppayment.getSelectedItem();
                hasSigned = ((CheckBox)findViewById(R.id.addmit_checkbox)).isChecked();
                if (isNullSafe()){
                    patient = new Patient(pName,pSurname,pCase,pPaymentType,pAdmittionDate,hasSigned);
                    patient.patientContent.put(pName,contents);
                    getPatientInfoManager().addPatient(patient);
                    startActivity(new Intent(HomeActivity.this,PatientActivity.class));
                }else {
                    showToast(HomeActivity.this,"Please fill in all fields");
                    tts.speak("Please fill in all fields before you save", TextToSpeech.QUEUE_ADD,null);
                }
            }
        });
    }

    @Override
    protected void onDestroy() {
        if (tts!=null){
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }

    private boolean isNullSafe(){
        if (!pName.isEmpty()&&
            !pSurname.isEmpty()&&
            !pCase.isEmpty()&&
            !pAdmittionDate.isEmpty()&&
            !pPaymentType.isEmpty())
            return true;
        else
            return false;
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

    private void setupPatientTypes() {
        ((Button)findViewById(R.id.btn_existing_patiant)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(HomeActivity.this,PatientActivity.class));
            }
        });
    }

    private void setupAdapters() {
        fileUplaodAdapter = new FileUplaodAdapter();
        ((ListView)findViewById(R.id.requisition_list)).setAdapter(fileUplaodAdapter);
        ((ListView)findViewById(R.id.requisition_list)).setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                String file = (String) view.getTag();
                ((ImageView)findViewById(R.id.camera_scan)).setImageBitmap(BitmapFactory.decodeFile(file));
                ((ImageView)findViewById(R.id.camera_scan)).setTag(file);
            }
        });
        setupDrawer();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void setupDiagnosticOptions() {
        ((Button)findViewById(R.id.btn_take_picture)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = getOutputMediaFile();
                filePath = file.getAbsolutePath();
                fileUri = Uri.fromFile(file);
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
                startActivityForResult(intent, TAKE_PATIENT_PICTURE_REQUEST_CODE);
            }
        });
        ((Button)findViewById(R.id.btn_x_ray)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfProfession = getString(R.string.riadio);
                getPreferenceManager().saveOperationType(typeOfProfession);
                tts.speak(OTHO_CASE,TextToSpeech.QUEUE_ADD,null);
            }
        });
        ((Button)findViewById(R.id.btn_teeth)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfProfession = getString(R.string.dental);
                getPreferenceManager().saveOperationType(typeOfProfession);
                try {
                    if (cavityExaminationQueue.size()==7){
                        tts.speak(cavityExaminationQueue.take(), TextToSpeech.QUEUE_ADD,null);
                        examineCavity(cavityExaminationQueue.take());
                    }else {
                        examineCavity(cavityExaminationQueue.take());
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

            }
        });
        ((Button) findViewById(R.id.btn_wound)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                typeOfProfession = getString(R.string.medical);
                getPreferenceManager().saveOperationType(typeOfProfession);
            }
        });
        ((ImageView)findViewById(R.id.camera_scan)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String file = (String)((ImageView)findViewById(R.id.camera_scan)).getTag();
                Intent i = new Intent(HomeActivity.this, FullImageActivity.class);
                i.putExtra("File", file);
                Bundle b =null;
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN){
                    Bitmap bitmap = Bitmap.createBitmap(v.getWidth(),v.getHeight(), Bitmap.Config.RGB_565);
                    b = ActivityOptions.makeThumbnailScaleUpAnimation(v, bitmap, 0, 0).toBundle();
                }
                startActivity(i,b);
            }
        });
    }

    private void examineCavity(final String area) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                tts.speak(area, TextToSpeech.QUEUE_ADD,null);
            }
        },5000);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.home, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (drawerToggle.onOptionsItemSelected(item)) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
    private void loadContents(){
        new AsyncTask<Void, List<Content>, List<Content>>() {
            @Override
            protected void onPreExecute() {
            }

            @Override
            protected List<Content> doInBackground(Void... voids) {
                List<Content> cs = getContentManager().getContentInfo(typeOfProfession);
                if (cs!=null){
                    publishProgress(cs);
                }
                return cs;
            }

            @Override
            protected void onProgressUpdate(List<Content>... values) {
                contents = values[0];
                fileUplaodAdapter.notifyDataSetChanged();
            }

            @Override
            protected void onPostExecute(List<Content> cs) {
                if (cs!=null){
                    contents = cs;
                    fileUplaodAdapter.notifyDataSetChanged();
                }
            }
        }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode){
            case TAKE_PATIENT_PICTURE_REQUEST_CODE:

                String mimeType = filePath.split("\\.")[1];
                mimeType = getFileMimeType(mimeType);
                File f = new File(filePath);
                TypedFile file = new TypedFile(mimeType,f);
                String sizeFormatted = "(" + String.valueOf(f.length()/1024) + "KB)";
                contents.add(new Content(f.getName(), sizeFormatted,f.getAbsolutePath()));
                ((ImageView)findViewById(R.id.camera_scan)).setImageBitmap(BitmapFactory.decodeFile(filePath));
                ((ImageView)findViewById(R.id.camera_scan)).setTag(filePath);
                fileUplaodAdapter.notifyDataSetChanged();
//                new AsyncTask<Void, Void, Void>() {
//                    @Override
//                    protected Void doInBackground(Void... voids) {
//                        getContentManager().saveConteInfo(typeOfProfession,contents);
//                        return null;
//                    }
//                }.executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
                if (cavityExaminationQueue.size()>0){
                    ((Button)findViewById(R.id.btn_teeth)).performClick();
                }
                break;
            case BROWSE_PATIENT_PICTURE_REQUEST_CODE:
                if (data != null) {
                    Uri selectedImage = data.getData();
                    String[] filePathColumn = { MediaStore.Images.Media.DATA };

                    Cursor cursor = getContentResolver().query(selectedImage,
                            filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String picturePath = cursor.getString(columnIndex);
                    String mType = picturePath.split("\\.")[1];
                    if (picturePath.split("\\.")[1].equalsIgnoreCase("pdf")){
                        mType = "application/pdf";
                    }else if (mType.equalsIgnoreCase("jpg")){
                        mType = "image/jpeg";
                    }else if (picturePath.split("\\.")[1].equalsIgnoreCase("png")){
                        mType = "image/png";
                    }
                    File mf = new File(picturePath);
                    TypedFile tfile = new TypedFile(mType,mf);
                    String size = "(" + String.valueOf(mf.length()/1024) + "KB)";
                    contents.add(new Content(mf.getName(), size,mf.getAbsolutePath()));
                    cursor.close();
                    ((ImageView)findViewById(R.id.camera_scan)).setImageBitmap(BitmapFactory.decodeFile(picturePath));
                }
            break;
        }
    }

    private static File getOutputMediaFile(){

        File mediaStorageDir = new File(Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_PICTURES), "myDoc");
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                Log.d("myDoc", "failed to create directory");
                return null;
            }
        }

        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        File mediaFile = new File(mediaStorageDir.getPath() + File.separator +
                "IMG_"+ timeStamp + ".jpg");

        return mediaFile;
    }

    private String getFileMimeType(String mimeType) {
        if (filePath.split("\\.")[1].equalsIgnoreCase("pdf")){
            mimeType = "application/pdf";
        }else if (mimeType.equalsIgnoreCase("jpg")){
            mimeType = "image/jpeg";
        }else if (filePath.split("\\.")[1].equalsIgnoreCase("png")){
            mimeType = "image/png";
        }
        return mimeType;
    }

    private class FileUplaodAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return contents.size();
        }

        @Override
        public Content getItem(int i) {
            return contents.get(i);
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        @Override
        public View getView(final int i, View view, ViewGroup viewGroup) {
            if (view == null) {
                view = getLayoutInflater().inflate(R.layout.file_upload_view, null);
            }
            view.setTag(getItem(i).file);
            ((ImageView) view.findViewById(R.id.file_name)).setImageBitmap(BitmapFactory.decodeFile(getItem(i).file));
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

    public Bitmap getImage(String file){
        return BitmapFactory.decodeFile(file);
    }
}
