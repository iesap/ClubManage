package clubmanage.ui;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

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

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import clubmanage.httpInterface.PersonalRequest;
import clubmanage.message.HttpMessage;
import clubmanage.model.User;
import clubmanage.util.BaseException;
import clubmanage.util.ClubManageUtil;
import clubmanage.util.HttpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PersonalCenterActivity extends AppCompatActivity implements View.OnClickListener {
    private CircleImageView img;
    private RelativeLayout person_uid;
    private RelativeLayout person_name;
    private RelativeLayout person_gender;
    private RelativeLayout person_phone;
    private RelativeLayout person_mail;
    private RelativeLayout person_major;
    private TextView t_person_uid;
    private TextView t_person_name;
    private TextView t_person_gender;
    private TextView t_person_phone;
    private TextView t_person_mail;
    private TextView t_person_major;

    public static final int CHOOSE_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    public PersonalCenterActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_center);
        verifyStoragePermissions(this);
        Toolbar toolbar = findViewById(R.id.person_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        person_uid=findViewById(R.id.person_uid);
        person_name=findViewById(R.id.person_name);
        person_gender=findViewById(R.id.person_gender);
        person_phone=findViewById(R.id.person_phone);
        person_mail=findViewById(R.id.person_mail);
        person_major=findViewById(R.id.person_major);
        person_uid.setOnClickListener(this);
        person_name.setOnClickListener(this);
        person_gender.setOnClickListener(this);
        person_phone.setOnClickListener(this);
        person_mail.setOnClickListener(this);
        person_major.setOnClickListener(this);
        t_person_uid=findViewById(R.id.t_person_uid);
        t_person_name=findViewById(R.id.t_person_name);
        t_person_gender=findViewById(R.id.t_person_gender);
        t_person_phone=findViewById(R.id.t_person_phone);
        t_person_mail=findViewById(R.id.t_person_mail);
        t_person_major=findViewById(R.id.t_person_major);
        t_person_uid.setText(User.currentLoginUser.getUid());
        t_person_name.setText(User.currentLoginUser.getName());
        t_person_gender.setText(User.currentLoginUser.getGender());
        t_person_phone.setText(User.currentLoginUser.getPhone_number());
        t_person_mail.setText(User.currentLoginUser.getMail());
        t_person_major.setText(User.currentLoginUser.getMajor());
        img=findViewById(R.id.per_cen_head);
        img.setOnClickListener(this);
        byte[] bt=null;
        if (User.currentLoginUser.getImage()!=null) {
            bt=Base64.decode(User.currentLoginUser.getImage(),Base64.DEFAULT);
            img.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.length));
        }
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
            case R.id.per_cen_head:
                if (ContextCompat.checkSelfPermission(PersonalCenterActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(PersonalCenterActivity.this, new String[]{ Manifest.permission. WRITE_EXTERNAL_STORAGE }, 1);
                } else {
                    openAlbum();
                }
                break;
            case R.id.person_name:
                final EditText edt1 = new EditText(this);
                edt1.setMinLines(1);
                edt1.setMaxLines(1);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt1)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                final String meg=edt1.getText().toString();
                                if(meg==null||meg.equals("")) {
                                    Toast.makeText(PersonalCenterActivity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                PersonalRequest request = retrofit.create(PersonalRequest.class);
                                Call<HttpMessage> call = request.changeName(User.currentLoginUser.getUid(), meg);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage data=response.body();
                                        if (data.getCode()==200){
                                        }else {
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                t_person_name.setText(meg);
                                User.currentLoginUser.setName(meg);
                                Toast.makeText(PersonalCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.person_gender:
                final String[] items = {"???", "???"};
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(items,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                final String msg=items[which];
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                PersonalRequest request = retrofit.create(PersonalRequest.class);
                                Call<HttpMessage> call = request.changeGender(User.currentLoginUser.getUid(), msg);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage data=response.body();
                                        if (data.getCode()==200){
                                        }else {
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                t_person_gender.setText(msg);
                                User.currentLoginUser.setGender(msg);
                                Toast.makeText(PersonalCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        }).show();
                break;
            case R.id.person_phone:
                final EditText edt2 = new EditText(this);
                edt2.setMinLines(1);
                edt2.setMaxLines(1);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt2)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                final String meg=edt2.getText().toString();
                                if(meg==null||meg.equals("")) {
                                    Toast.makeText(PersonalCenterActivity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                PersonalRequest request = retrofit.create(PersonalRequest.class);
                                Call<HttpMessage> call = request.changePhone_number(User.currentLoginUser.getUid(), meg);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage data=response.body();
                                        if (data.getCode()==200){
                                        }else {
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                t_person_phone.setText(meg);
                                User.currentLoginUser.setPhone_number(meg);
                                Toast.makeText(PersonalCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.person_mail:
                final EditText edt3 = new EditText(this);
                edt3.setMinLines(1);
                edt3.setMaxLines(2);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt3)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                final String meg=edt3.getText().toString();
                                if(meg==null||meg.equals("")) {
                                    Toast.makeText(PersonalCenterActivity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                PersonalRequest request = retrofit.create(PersonalRequest.class);
                                Call<HttpMessage> call = request.changeMail(User.currentLoginUser.getUid(), meg);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage data=response.body();
                                        if (data.getCode()==200){
                                        }else {
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                t_person_mail.setText(meg);
                                User.currentLoginUser.setMail(meg);
                                Toast.makeText(PersonalCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.person_major:
                final EditText edt4 = new EditText(this);
                edt4.setMinLines(1);
                edt4.setMaxLines(1);
                new AlertDialog.Builder(this)
                        .setTitle("????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt4)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                final String meg=edt4.getText().toString();
                                if(meg==null||meg.equals("")) {
                                    Toast.makeText(PersonalCenterActivity.this, "??????????????????" , Toast.LENGTH_SHORT).show();
                                    return;
                                }
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl(HttpUtil.httpUrl)
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();
                                PersonalRequest request = retrofit.create(PersonalRequest.class);
                                Call<HttpMessage> call = request.changeMajor(User.currentLoginUser.getUid(), meg);
                                call.enqueue(new Callback<HttpMessage>() {
                                    @Override
                                    public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                                        HttpMessage data=response.body();
                                        if (data.getCode()==200){
                                        }else {
                                        }
                                    }
                                    @Override
                                    public void onFailure(Call<HttpMessage> call, Throwable t) {
                                    }
                                });
                                t_person_major.setText(meg);
                                User.currentLoginUser.setMajor(meg);
                                Toast.makeText(PersonalCenterActivity.this, "????????????", Toast.LENGTH_SHORT).show();
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
            byte[] byteArray = stream.toByteArray();
            updateImg(byteArray);
            User.currentLoginUser.setImage(Base64.encodeToString(byteArray,Base64.DEFAULT));
            img.setImageBitmap(bitmap);
            Toast.makeText(this, "????????????", Toast.LENGTH_SHORT).show();
            try {
                stream.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            Toast.makeText(this, "failed to get image", Toast.LENGTH_SHORT).show();
        }
    }

    private void updateImg(final byte[] img){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        PersonalRequest request = retrofit.create(PersonalRequest.class);
        Call<HttpMessage> call = request.changeImage(User.currentLoginUser.getUid(), Base64.encodeToString(img,Base64.DEFAULT));
        call.enqueue(new Callback<HttpMessage>() {
            @Override
            public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                HttpMessage data=response.body();
                if (data.getCode()==200){
                }else {
                }
            }
            @Override
            public void onFailure(Call<HttpMessage> call, Throwable t) {
            }
        });
    }
}
