package clubmanage.ui;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
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
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import clubmanage.httpInterface.ClubRequest;
import clubmanage.message.HttpMessage;
import clubmanage.model.Club;
import clubmanage.model.User;
import clubmanage.util.ClubManageUtil;
import clubmanage.util.HttpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageClub extends AppCompatActivity implements View.OnClickListener{
    private Club club;
    private String notice;
    private int clubid;
    private int f=0;
    private RelativeLayout club_logo;
    private CircleImageView c_club_logo;
    private RelativeLayout club_poster;
    private TextView t_club_poster;
    private RelativeLayout club_name;
    private TextView t_club_name;
    private RelativeLayout club_cat;
    private TextView t_club_cat;
    private RelativeLayout club_slogan;
    private TextView t_club_slogan;
    private RelativeLayout club_introduce;
    private TextView t_club_introduce;
    private RelativeLayout club_notice;
    private TextView t_club_notice;
    private RelativeLayout club_place;
    private TextView t_club_place;

    public static final int CHOOSE_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            club=(Club) msg.obj;
            byte[] bt =null;
            if (club.getClub_icon()==null) {
                c_club_logo.setImageResource(R.drawable.enrollment);
            }else {
                bt= Base64.decode(club.getClub_icon(),Base64.DEFAULT);
                c_club_logo.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.length));
            }
            t_club_poster.setText("");
            t_club_name.setText(club.getClub_name());
            t_club_cat.setText(club.getCategory_name());
            t_club_slogan.setText(club.getSlogan());
            t_club_introduce.setText(club.getClub_introduce());
            t_club_place.setText(club.getClub_place());
        }
    };
    private Handler handler2=new Handler(){
        public void handleMessage(Message msg){
            notice=(String)msg.obj;
            t_club_notice.setText(notice);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.manage_club);
        Intent intentget=getIntent();
        clubid=(int)intentget.getSerializableExtra("clubid");
        Toolbar toolbar = findViewById(R.id.change_club_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        verifyStoragePermissions(this);
        club_logo=findViewById(R.id.club_logo);
        c_club_logo=findViewById(R.id.c_club_logo);
        club_poster=findViewById(R.id.club_poster);
        t_club_poster=findViewById(R.id.t_club_poster);
        club_name=findViewById(R.id.club_name);
        t_club_name=findViewById(R.id.t_club_name);
        club_cat=findViewById(R.id.club_cat);
        t_club_cat=findViewById(R.id.t_club_cat);
        club_slogan=findViewById(R.id.club_slogan);
        t_club_slogan=findViewById(R.id.t_club_slogan);
        club_introduce=findViewById(R.id.club_introduce);
        t_club_introduce=findViewById(R.id.t_club_introduce);
        club_notice=findViewById(R.id.club_notice);
        t_club_notice=findViewById(R.id.t_club_notice);
        club_place=findViewById(R.id.club_place);
        t_club_place=findViewById(R.id.t_club_place);
        club_logo.setOnClickListener(this);
        club_poster.setOnClickListener(this);
        club_cat.setOnClickListener(this);
        club_slogan.setOnClickListener(this);
        club_introduce.setOnClickListener(this);
        club_notice.setOnClickListener(this);
        getClubMsg();
        getClubNotice();
    }

    public void getClubMsg(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ClubRequest request = retrofit.create(ClubRequest.class);
        Call<HttpMessage<Club>> call = request.searchClubByProprieter(User.currentLoginUser.getUid());
        call.enqueue(new Callback<HttpMessage<Club>>() {
            @Override
            public void onResponse(Call<HttpMessage<Club>> call, Response<HttpMessage<Club>> response) {
                HttpMessage<Club> data=response.body();
                if (data.getCode()==200){
                    Club club = (Club)data.getData();
                    Message message=new Message();
                    message.obj=club;
                    handler.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<Club>> call, Throwable t) {
            }
        });
    }

    public void getClubNotice(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ClubRequest request = retrofit.create(ClubRequest.class);
        Call<HttpMessage<String>> call = request.searchNotice(clubid);
        call.enqueue(new Callback<HttpMessage<String>>() {
            @Override
            public void onResponse(Call<HttpMessage<String>> call, Response<HttpMessage<String>> response) {
                HttpMessage<String> data=response.body();
                if (data.getCode()==200){
                    String notice = (String)data.getData();
                    Message message=new Message();
                    message.obj=notice;
                    handler2.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<String>> call, Throwable t) {
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.club_logo:
                if (ContextCompat.checkSelfPermission(ManageClub.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManageClub.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    this.f=1;
                    openAlbum();
                }
                break;
            case R.id.club_poster:
                if (ContextCompat.checkSelfPermission(ManageClub.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(ManageClub.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    this.f=2;
                    openAlbum();
                }
                break;
            case R.id.club_cat:
                final String[] items = {"????????????","????????????", "????????????"};
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(items,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, final int which) {
                                t_club_cat.setText(items[which]);
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ClubRequest request = retrofit.create(ClubRequest.class);
                                Call<HttpMessage> call = request.editCategory(clubid,items[which]);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage<String> data=response.body();
                                        if (data.getCode()==200){
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                Toast.makeText(ManageClub.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
            case R.id.club_slogan:
                final EditText edt1 = new EditText(this);
                edt1.setMinLines(1);
                edt1.setMaxLines(3);
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt1)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_club_slogan.setText(edt1.getText().toString());
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ClubRequest request = retrofit.create(ClubRequest.class);
                                Call<HttpMessage> call = request.editSlogan(clubid,edt1.getText().toString());
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage<String> data=response.body();
                                        if (data.getCode()==200){
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                Toast.makeText(ManageClub.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.club_introduce:
                final EditText edt2 = new EditText(this);
                edt2.setMinLines(1);
                edt2.setMaxLines(15);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt2)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_club_introduce.setText(edt2.getText().toString());
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ClubRequest request = retrofit.create(ClubRequest.class);
                                Call<HttpMessage> call = request.editIntroduction(clubid,edt2.getText().toString());
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage<String> data=response.body();
                                        if (data.getCode()==200){
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                Toast.makeText(ManageClub.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.club_notice:
                final EditText edt3 = new EditText(this);
                edt3.setMinLines(1);
                edt3.setMaxLines(10);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt3)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_club_notice.setText(edt3.getText().toString());
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                ClubRequest request = retrofit.create(ClubRequest.class);
                                Call<HttpMessage> call = request.editNotice(clubid,edt3.getText().toString());
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage<String> data=response.body();
                                        if (data.getCode()==200){
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                Toast.makeText(ManageClub.this, "??????????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
        }
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
            final byte[] byteArray = stream.toByteArray();
            if(this.f==1){
                c_club_logo.setImageBitmap(bitmap);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(HttpUtil.httpUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ClubRequest request = retrofit.create(ClubRequest.class);
                Call<HttpMessage> call = request.editLogo(clubid,Base64.encodeToString(byteArray,Base64.DEFAULT));
                call.enqueue(new Callback<HttpMessage>() {
                    @Override
                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                        HttpMessage<String> data=response.body();
                        if (data.getCode()==200){
                        }
                    }
                    @Override
                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                    }
                });
                Toast.makeText(this, "logo????????????", Toast.LENGTH_SHORT).show();
            }else if (this.f==2){
                t_club_poster.setText(imagePath);
                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(HttpUtil.httpUrl)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ClubRequest request = retrofit.create(ClubRequest.class);
                Call<HttpMessage> call = request.editCover(clubid,Base64.encodeToString(byteArray,Base64.DEFAULT));
                call.enqueue(new Callback<HttpMessage>() {
                    @Override
                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                        HttpMessage<String> data=response.body();
                        if (data.getCode()==200){
                        }
                    }
                    @Override
                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                    }
                });
                Toast.makeText(this, "??????????????????", Toast.LENGTH_SHORT).show();
            }
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