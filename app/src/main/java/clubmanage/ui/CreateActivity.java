package clubmanage.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.ContentUris;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

import clubmanage.httpInterface.ApplicationRequest;
import clubmanage.httpInterface.AreaRequest;
import clubmanage.message.HttpMessage;
import clubmanage.model.Area;
import clubmanage.model.Club;
import clubmanage.model.Create_activity;
import clubmanage.model.User;
import clubmanage.util.BaseException;
import clubmanage.util.ClubManageUtil;
import clubmanage.util.HttpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateActivity extends AppCompatActivity implements View.OnClickListener {

    private int clubid;
    private String[] place;
    private byte[] mbyteArray;
    private Calendar calendar= Calendar.getInstance(Locale.CHINA);
    private RelativeLayout create_name;
    private RelativeLayout create_cat;
    private RelativeLayout create_introduce;
    private RelativeLayout create_attention;
    private RelativeLayout create_public;
    private RelativeLayout create_place;
    private RelativeLayout create_start_time;
    private RelativeLayout create_finish_time;
    private RelativeLayout create_reason;
    private TextView t_create_name;
    private TextView t_create_cat;
    private TextView t_create_introduce;
    private TextView t_create_attention;
    private TextView t_create_public;
    private TextView t_create_place;
    private TextView t_create_start_time;
    private TextView t_create_finish_time;
    private TextView t_create_reason;

    private Handler handler2=new Handler(){
        public void handleMessage(Message msg){
            exception=(String) msg.obj;
            button.setEnabled(true);
            Toast.makeText(CreateActivity.this, exception, Toast.LENGTH_SHORT).show();
            return;
        }
    };
    private Handler handler3=new Handler(){
        public void handleMessage(Message msg){
            place=(String[])msg.obj;
        }
    };
    private String exception=null;
    private CircleImageView img;
    private Button button;

    public static final int CHOOSE_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create);
        Intent intentget=getIntent();
        clubid=(int)intentget.getSerializableExtra("clubid");
        verifyStoragePermissions(this);
        Toolbar toolbar = findViewById(R.id.create_activity_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        create_name=findViewById(R.id.create_name);
        create_cat=findViewById(R.id.create_cat);
        create_introduce=findViewById(R.id.create_introduce);
        create_attention=findViewById(R.id.create_attention);
        create_public=findViewById(R.id.create_public);
        create_place=findViewById(R.id.create_place);
        create_start_time=findViewById(R.id.create_start_time);
        create_finish_time=findViewById(R.id.create_finish_time);
        create_reason=findViewById(R.id.create_reason);
        create_name.setOnClickListener(this);
        create_cat.setOnClickListener(this);
        create_introduce.setOnClickListener(this);
        create_attention.setOnClickListener(this);
        create_public.setOnClickListener(this);
        create_place.setOnClickListener(this);
        create_start_time.setOnClickListener(this);
        create_finish_time.setOnClickListener(this);
        create_reason.setOnClickListener(this);
        t_create_name=findViewById(R.id.t_create_name);
        t_create_cat=findViewById(R.id.t_create_cat);
        t_create_introduce=findViewById(R.id.t_create_introduce);
        t_create_attention=findViewById(R.id.t_create_attention);
        t_create_public=findViewById(R.id.t_create_public);
        t_create_place=findViewById(R.id.t_create_place);
        t_create_start_time=findViewById(R.id.t_create_start_time);
        t_create_finish_time=findViewById(R.id.t_create_finish_time);
        t_create_reason=findViewById(R.id.t_create_reason);

        button=findViewById(R.id.button_create_activity);
        button.setOnClickListener(this);
        img=findViewById(R.id.create_activity_poster);
        img.setOnClickListener(this);
        getPlace();
    }

    public void getPlace(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        AreaRequest request = retrofit.create(AreaRequest.class);
        Call<HttpMessage<List<Area>>> call = request.listUsibleSpe();
        call.enqueue(new Callback<HttpMessage<List<Area>>>() {
            @Override
            public void onResponse(Call<HttpMessage<List<Area>>> call, Response<HttpMessage<List<Area>>> response) {
                HttpMessage<List<Area>> data=response.body();
                if (data.getCode()==200){
                    List<Area> area=(List<Area>) data.getData();
                    String[] places=new String[area.size()];
                    for(int i=0;i<area.size();i++){
                        places[i]=area.get(i).getArea_name();
                    }
                    Message message=new Message();
                    message.obj=places;
                    handler3.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<List<Area>>> call, Throwable t) {
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressLint("ResourceType")
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.create_activity_poster:
                if (ContextCompat.checkSelfPermission(CreateActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(CreateActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.button_create_activity:
                if(t_create_cat.getText().toString().equals("")||t_create_introduce.getText().toString().equals("") ||t_create_finish_time.getText().toString().equals("")||
                        t_create_start_time.getText().toString().equals("")||t_create_name.getText().toString().equals("")|| t_create_place.getText().toString().equals("")||
                        t_create_reason.getText().toString().equals("")||t_create_public.getText().toString().equals("")){
                        Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                        return;
                }
                button.setEnabled(false);
                createActivity();
                break;
            case R.id.create_name:
                final EditText edt1 = new EditText(this);
                edt1.setMinLines(1);
                edt1.setMaxLines(1);
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt1)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_name.setText(edt1.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.create_cat:
                final String[] items = {"????????????", "????????????", "????????????"};
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(items,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_create_cat.setText(items[which]);
                            }
                        }).show();
                break;
            case R.id.create_introduce:
                final EditText edt2 = new EditText(this);
                edt2.setMinLines(1);
                edt2.setMaxLines(5);
                new AlertDialog.Builder(this)
                        .setTitle("??????????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt2)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_introduce.setText(edt2.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.create_attention:
                final EditText edt4 = new EditText(this);
                edt4.setMinLines(1);
                edt4.setMaxLines(5);
                new AlertDialog.Builder(this)
                        .setTitle("????????????????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt4)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_attention.setText(edt4.getText().toString());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.create_public:
                final String[] items2 = {"???", "???"};
                new AlertDialog.Builder(this)
                        .setTitle("?????????????????????")
                        .setItems(items2,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_create_public.setText(items2[which]);
                            }
                        }).show();
                break;
            case R.id.create_place:
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(place,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_create_place.setText(place[which]);
                            }
                        }).show();
                break;
            case R.id.create_start_time:
                new DatePickerDialog(this, 4, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        t_create_start_time.setText(year+"-"+(monthOfYear + 1)+"-"+dayOfMonth);
                    }
                }
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.create_finish_time:
                new DatePickerDialog(this, 4, new DatePickerDialog.OnDateSetListener() {
                    // ???????????????(How the parent is notified that the date is set.)
                    @Override
                    public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                        t_create_finish_time.setText(year+"-"+(monthOfYear + 1)+"-"+dayOfMonth);
                    }
                }
                        , calendar.get(Calendar.YEAR)
                        , calendar.get(Calendar.MONTH)
                        , calendar.get(Calendar.DAY_OF_MONTH)).show();
                break;
            case R.id.create_reason:
                final EditText edt3 = new EditText(this);
                edt3.setMinLines(1);
                edt3.setMaxLines(5);
                new AlertDialog.Builder(this)
                        .setTitle("??????????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt3)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_reason.setText(edt3.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
        }
    }


    private void createActivity(){
        byte p=0;
        if (t_create_public.equals("???"))p=1;
        else if (t_create_public.equals("???")) p=0;
        Create_activity create_activity=new Create_activity();
        create_activity.setClub_id(clubid);
        if (mbyteArray!=null){
            create_activity.setPoster(Base64.encodeToString(mbyteArray,Base64.DEFAULT));
        }
        create_activity.setActivity_name(t_create_name.getText().toString());
        create_activity.setArea_name(t_create_place.getText().toString());
        create_activity.setActivity_owner_id(User.currentLoginUser.getUid());
        create_activity.setActivity_owner_name(User.currentLoginUser.getName());
        create_activity.setActivity_start_time(Timestamp.valueOf(t_create_start_time.getText().toString()+" 00:00:00"));
        create_activity.setActivity_end_time(Timestamp.valueOf(t_create_finish_time.getText().toString()+" 00:00:00"));
        create_activity.setActivity_details(t_create_introduce.getText().toString());
        create_activity.setActivity_attention(t_create_attention.getText().toString());
        create_activity.setActivity_category(t_create_cat.getText().toString());
        create_activity.setIf_public_activity(p);
        create_activity.setReason(t_create_reason.getText().toString());
        Gson gson=new GsonBuilder().setDateFormat("yyyy-MM-dd HH:mm:ss").create();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();
        ApplicationRequest request = retrofit.create(ApplicationRequest.class);
        Call<HttpMessage> call = request.addActivityAppli(create_activity);
        call.enqueue(new Callback<HttpMessage>() {
            @Override
            public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                HttpMessage data=response.body();
                if (data.getCode()==200){
                    Toast.makeText(CreateActivity.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    finish();
                }else if (data.getCode()==400){
                    Message message=new Message();
                    message.obj=data.getMsg();
                    handler2.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage> call, Throwable t) {
            }
        });

    }

    public static void verifyStoragePermissions(Activity activity) {
        try {
            //???????????????????????????
            int permission = ActivityCompat.checkSelfPermission(activity,
                    "android.permission.WRITE_EXTERNAL_STORAGE");
            if (permission != PackageManager.PERMISSION_GRANTED) {
                // ???????????????????????????????????????????????????????????????
                ActivityCompat.requestPermissions(activity, PERMISSIONS_STORAGE,REQUEST_EXTERNAL_STORAGE);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openAlbum() {
        Intent intent = new Intent("android.intent.action.GET_CONTENT");
        intent.setType("image/*");
        startActivityForResult(intent, CHOOSE_PHOTO); // ????????????
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 1:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    openAlbum();
                } else {
                    Toast.makeText(this, "You denied the permission", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case CHOOSE_PHOTO:
                if (resultCode == RESULT_OK) {
                    // ???????????????????????????
                    if (Build.VERSION.SDK_INT >= 19) {
                        // 4.4?????????????????????????????????????????????
                        handleImageOnKitKat(data);
                    } else {
                        // 4.4??????????????????????????????????????????
                        handleImageBeforeKitKat(data);
                    }
                }
                break;
            default:
                break;
        }
    }

    @TargetApi(19)
    private void handleImageOnKitKat(Intent data) {
        String imagePath = null;
        Uri uri = data.getData();
        Log.d("TAG", "handleImageOnKitKat: uri is " + uri);
        if (DocumentsContract.isDocumentUri(this, uri)) {
            // ?????????document?????????Uri????????????document id??????
            String docId = DocumentsContract.getDocumentId(uri);
            if("com.android.providers.media.documents".equals(uri.getAuthority())) {
                String id = docId.split(":")[1]; // ????????????????????????id
                String selection = MediaStore.Images.Media._ID + "=" + id;
                imagePath = getImagePath(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, selection);
            } else if ("com.android.providers.downloads.documents".equals(uri.getAuthority())) {
                Uri contentUri = ContentUris.withAppendedId(Uri.parse("content://downloads/public_downloads"), Long.valueOf(docId));
                imagePath = getImagePath(contentUri, null);
            }
        } else if ("content".equalsIgnoreCase(uri.getScheme())) {
            // ?????????content?????????Uri??????????????????????????????
            imagePath = getImagePath(uri, null);
        } else if ("file".equalsIgnoreCase(uri.getScheme())) {
            // ?????????file?????????Uri?????????????????????????????????
            imagePath = uri.getPath();
        }
        displayImage(imagePath); // ??????????????????????????????
    }

    private void handleImageBeforeKitKat(Intent data) {
        Uri uri = data.getData();
        String imagePath = getImagePath(uri, null);
        displayImage(imagePath);
    }

    private String getImagePath(Uri uri, String selection) {
        String path = null;
        // ??????Uri???selection??????????????????????????????
        Cursor cursor = getContentResolver().query(uri, null, selection, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                path = cursor.getString(cursor.getColumnIndex(MediaStore.Images.Media.DATA));
            }
            cursor.close();
        }
        return path;
    }

    private void displayImage(String imagePath) {
        if (imagePath != null) {
            Bitmap bitmap = BitmapFactory.decodeFile(imagePath);
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, stream);
            byte[] byteArray = stream.toByteArray();
            mbyteArray=byteArray;
            img.setImageBitmap(bitmap);
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }
}
