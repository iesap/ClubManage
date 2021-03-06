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
import android.os.Handler;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;

import clubmanage.httpInterface.ApplicationRequest;
import clubmanage.httpInterface.AreaRequest;
import clubmanage.message.HttpMessage;
import clubmanage.model.Area;
import clubmanage.model.Create_club;
import clubmanage.model.User;
import clubmanage.util.BaseException;
import clubmanage.util.ClubManageUtil;
import clubmanage.util.HttpUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class CreateClub extends AppCompatActivity implements View.OnClickListener {
    private String exception=null;
    private String[] place;
    private Button button;
    private RelativeLayout create_club_name;
    private RelativeLayout create_club_cat;
    private RelativeLayout create_club_place;
    private RelativeLayout create_club_introduce;
    private RelativeLayout create_club_reason;
    private TextView t_create_club_name;
    private TextView t_create_club_cat;
    private TextView t_create_club_place;
    private TextView t_create_club_introduce;
    private TextView t_create_club_reason;

    public static final int CHOOSE_PHOTO = 2;
    private static final int REQUEST_EXTERNAL_STORAGE = 1;
    private static String[] PERMISSIONS_STORAGE = {
            "android.permission.READ_EXTERNAL_STORAGE",
            "android.permission.WRITE_EXTERNAL_STORAGE" };

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            exception=(String) msg.obj;
            button.setEnabled(true);
            Toast.makeText(CreateClub.this, exception, Toast.LENGTH_SHORT).show();
            return;
        }
    };
    private Handler handler2=new Handler(){
        public void handleMessage(Message msg){
            place=(String[])msg.obj;
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_club);

        Toolbar toolbar = findViewById(R.id.create_club_toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        create_club_name=findViewById(R.id.create_club_name);
        create_club_cat=findViewById(R.id.create_club_cat);
        create_club_place=findViewById(R.id.create_club_place);
        create_club_introduce=findViewById(R.id.create_club_introduce);
        create_club_reason=findViewById(R.id.create_club_reason);
        create_club_name.setOnClickListener(this);
        create_club_cat.setOnClickListener(this);
        create_club_place.setOnClickListener(this);
        create_club_introduce.setOnClickListener(this);
        create_club_reason.setOnClickListener(this);
        t_create_club_name=findViewById(R.id.t_create_club_name);
        t_create_club_cat=findViewById(R.id.t_create_club_cat);
        t_create_club_place=findViewById(R.id.t_create_club_place);
        t_create_club_introduce=findViewById(R.id.t_create_club_introduce);
        t_create_club_reason=findViewById(R.id.t_create_club_reason);

        button=findViewById(R.id.button_create_club);
        button.setOnClickListener(this);
        getPlace();
    }

    public void getPlace(){
//        new Thread(){
//            @Override
//            public void run() {
//                List<Area> area=null;
//                try {
//                    area= ClubManageUtil.areaManage.listUsibleSpe();
//                } catch (BaseException e) {
//                    e.printStackTrace();
//                }
//                String[] places=new String[area.size()];
//                for(int i=0;i<area.size();i++){
//                    places[i]=area.get(i).getArea_name();
//                }
//                Message message=new Message();
//                message.obj=places;
//                handler2.sendMessage(message);
//            }
//        }.start();
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
                    handler2.sendMessage(message);
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

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.button_create_club:
                if (t_create_club_name.getText().toString().equals("")||t_create_club_cat.getText().toString().equals("")||
                        t_create_club_place.getText().toString().equals("")||t_create_club_introduce.getText().toString().equals("")||
                        t_create_club_reason.getText().toString().equals("")){
                    Toast.makeText(this, "?????????????????????", Toast.LENGTH_SHORT).show();
                    return;
                }
                button.setEnabled(false);
                createClub();
                break;
            case R.id.create_club_name:
                final EditText edt1 = new EditText(this);
                edt1.setMinLines(1);
                edt1.setMaxLines(1);
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt1)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_club_name.setText(edt1.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.create_club_cat:
                final String[] items = {"????????????","????????????", "????????????"};
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(items,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_create_club_cat.setText(items[which]);
                            }
                        }).show();
                break;
            case R.id.create_club_place:
                new AlertDialog.Builder(this)
                        .setTitle("???????????????")
                        .setItems(place,new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                t_create_club_place.setText(place[which]);
                            }
                        }).show();
                break;
            case R.id.create_club_introduce:
                final EditText edt3 = new EditText(this);
                edt3.setMinLines(1);
                edt3.setMaxLines(10);
                new AlertDialog.Builder(this)
                        .setTitle("??????????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt3)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_club_introduce.setText(edt3.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
            case R.id.create_club_reason:
                final EditText edt4 = new EditText(this);
                edt4.setMinLines(1);
                edt4.setMaxLines(10);
                new AlertDialog.Builder(this)
                        .setTitle("??????????????????")
                        .setIcon(android.R.drawable.ic_menu_edit)
                        .setView(edt4)
                        .setPositiveButton("??????", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) {
                                t_create_club_reason.setText(edt4.getText());
                            }
                        })
                        .setNegativeButton("??????", null)
                        .show();
                break;
        }
    }

    private void createClub() {
//        new Thread() {
//            @Override
//            public void run() {
//                try {
//                    ClubManageUtil.applicationManage.addClubAppli(t_create_club_place.getText().toString(), User.currentLoginUser.getUid(),
//                            User.currentLoginUser.getName(), t_create_club_name.getText().toString(), t_create_club_cat.getText().toString(),
//                            t_create_club_introduce.getText().toString(), t_create_club_reason.getText().toString());
//                    Message message = new Message();
//                    message.obj = null;
//                    handler.sendMessage(message);
//                } catch (BaseException e) {
//                    Message message = new Message();
//                    message.obj = e.getMessage();
//                    handler.sendMessage(message);
//                }
//            }
//        }.start();
        Create_club create_club=new Create_club();
        create_club.setArea_name(t_create_club_place.getText().toString());
        create_club.setUid(User.currentLoginUser.getUid());
        create_club.setApplican_name(User.currentLoginUser.getName());
        create_club.setClub_name(t_create_club_name.getText().toString());
        create_club.setClub_category(t_create_club_cat.getText().toString());
        create_club.setIntroduce(t_create_club_introduce.getText().toString());
        create_club.setReason(t_create_club_reason.getText().toString());
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(HttpUtil.httpUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApplicationRequest request = retrofit.create(ApplicationRequest.class);
        Call<HttpMessage> call = request.addClubAppli(create_club);
        call.enqueue(new Callback<HttpMessage>() {
            @Override
            public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                HttpMessage data=response.body();
                if (data.getCode()==200){
                    Toast.makeText(CreateClub.this, "??????????????????????????????", Toast.LENGTH_SHORT).show();
                    Intent intent=new Intent();
                    intent.putExtra("data","OK");
                    setResult(RESULT_OK,intent);
                    finish();
                }else if (data.getCode()==400){
                    Message message=new Message();
                    message.obj=data.getMsg();
                    handler.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage> call, Throwable t) {
            }
        });
    }
}