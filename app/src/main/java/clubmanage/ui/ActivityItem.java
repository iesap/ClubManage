package clubmanage.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Base64;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.sql.Timestamp;

import clubmanage.httpInterface.ActivityRequest;
import clubmanage.httpInterface.ApplicationRequest;
import clubmanage.message.HttpMessage;
import clubmanage.model.Activity;
import clubmanage.model.User;
import clubmanage.util.ClubManageUtil;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ActivityItem extends AppCompatActivity implements View.OnClickListener {
    private int activityid;
    private String clubname;
    private String ownname;
    private Activity activity;
    private boolean isSignUp=false;
    private Button join;

    private ImageView poster;
    private TextView name;
    private TextView address;
    private TextView time;
    private TextView time2;
    private TextView intruduction;
    private TextView notice;
    private TextView act_person;
    private TextView act_club;

    private Handler handler=new Handler(){
        public void handleMessage(Message msg){
            isSignUp=(boolean)msg.obj;
            if (isSignUp==true){
                join.setEnabled(false);
                join.setText("已报名");
                join.setBackgroundResource(R.drawable.button_shape_gray);
            }else {
                join.setEnabled(true);
                join.setText("我要报名");
                join.setBackgroundResource(R.drawable.button_shape);
            }
        }
    };
    private Handler handler2=new Handler() {
        public void handleMessage(Message msg) {
            activity=(Activity)msg.obj;
            if (activity.getPoster()!=null){
                byte[] bt= Base64.decode(activity.getPoster(),Base64.DEFAULT);
                poster.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.length));
            }
            Timestamp now = new Timestamp(System.currentTimeMillis());
            if(activity.getActivity_end_time().before(now)){
                join.setEnabled(false);
                join.setText("活动已结束");
                join.setBackgroundResource(R.drawable.button_shape_gray);
            }
            else {
                join.setEnabled(true);
                join.setText("我要报名");
                join.setBackgroundResource(R.drawable.button_shape);
            }
            name.setText(name.getText().toString()+" "+activity.getActivity_name());
            address.setText(address.getText().toString()+" "+activity.getActivity_place());
            time.setText(time.getText().toString()+" "+activity.getActivity_start_time().toString());
            time2.setText(time2.getText().toString()+""+activity.getActivity_end_time().toString());
            intruduction.setText(intruduction.getText().toString()+" "+activity.getActivity_introduce());
            notice.setText(notice.getText().toString()+" "+activity.getActivity_attention());
        }
    };
    private Handler handler3=new Handler(){
        public void handleMessage(Message msg){
            clubname=(String) msg.obj;
            act_club.setText(act_club.getText().toString()+" "+clubname);
        }
    };
    private Handler handler4=new Handler(){
        public void handleMessage(Message msg){
            ownname=(String) msg.obj;
            act_person.setText(act_person.getText().toString()+" "+ownname);
        }
    };
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_item);
        Intent intentget=getIntent();
        activityid=(int) intentget.getSerializableExtra("activityid");
        poster=(ImageView)findViewById(R.id.activity_poster);
        join=findViewById(R.id.activity_sign_up);
        join.setEnabled(false);
        join.setOnClickListener(this);
        name=findViewById(R.id.manage_item_text1);
        address=findViewById(R.id.act_address);
        time=findViewById(R.id.act_time);
        time2=findViewById(R.id.act_time2);
        intruduction=findViewById(R.id.act_intruduction_context);
        notice=findViewById(R.id.act_intruduction);
        act_person=findViewById(R.id.act_person);
        act_club=findViewById(R.id.act_club);
        ImageButton back=findViewById(R.id.act_head_img1);
        back.setOnClickListener(this);
        ifSignUp();
        getActivity();
        getClubName();
        getOwnname();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.act_head_img1:
                finish();
                break;
            case R.id.activity_sign_up:
                join.setEnabled(false);
                signUpAvtivity(User.currentLoginUser.getUid());
                Toast.makeText(ActivityItem.this,"报名成功",Toast.LENGTH_SHORT).show();
                join.setText("已报名");
                join.setBackgroundResource(R.drawable.button_shape_gray);
                break;
        }
    }

    private void ifSignUp(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.36.153.113:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ActivityRequest request = retrofit.create(ActivityRequest.class);
        Call<HttpMessage<Boolean>> call = request.ifparticipate(User.currentLoginUser.getUid(),activityid);
        call.enqueue(new Callback<HttpMessage<Boolean>>() {
            @Override
            public void onResponse(Call<HttpMessage<Boolean>> call, Response<HttpMessage<Boolean>> response) {
                HttpMessage<Boolean> data=response.body();
                if (data.getCode()==0){
                    Boolean isSignUp = (Boolean)data.getData();
                    Message message=new Message();
                    message.obj=isSignUp;
                    handler.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<Boolean>> call, Throwable t) {
            }
        });
    }

    private void signUpAvtivity(String uid){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.36.153.113:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApplicationRequest request = retrofit.create(ApplicationRequest.class);
        Call<HttpMessage> call = request.signupActivity(User.currentLoginUser.getUid(),activityid);
        call.enqueue(new Callback<HttpMessage>() {
            @Override
            public void onResponse(Call<HttpMessage> call, Response<HttpMessage> response) {
                HttpMessage<Boolean> data=response.body();
                if (data.getCode()==0){
                }
            }
            @Override
            public void onFailure(Call<HttpMessage> call, Throwable t) {
            }
        });
    }

    private void getActivity(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.36.153.113:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ActivityRequest request = retrofit.create(ActivityRequest.class);
        Call<HttpMessage<Activity>> call = request.searchActivityById(activityid);
        call.enqueue(new Callback<HttpMessage<Activity>>() {
            @Override
            public void onResponse(Call<HttpMessage<Activity>> call, Response<HttpMessage<Activity>> response) {
                HttpMessage<Activity> data=response.body();
                if (data.getCode()==0){
                    Activity act = (Activity)data.getData();
                    Message message=new Message();
                    message.obj=act;
                    handler2.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<Activity>> call, Throwable t) {
            }
        });
    }

    private void getClubName(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.36.153.113:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ActivityRequest request = retrofit.create(ActivityRequest.class);
        Call<HttpMessage<String>> call = request.findClubNameByActivityId(activityid);
        call.enqueue(new Callback<HttpMessage<String>>() {
            @Override
            public void onResponse(Call<HttpMessage<String>> call, Response<HttpMessage<String>> response) {
                HttpMessage<String> data=response.body();
                if (data.getCode()==0){
                    String name = (String)data.getData();
                    Message message=new Message();
                    message.obj=name;
                    handler3.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<String>> call, Throwable t) {
            }
        });
    }

    private void getOwnname(){
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://121.36.153.113:8000")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ActivityRequest request = retrofit.create(ActivityRequest.class);
        Call<HttpMessage<String>> call = request.findProprieterNameByActivityId(activityid);
        call.enqueue(new Callback<HttpMessage<String>>() {
            @Override
            public void onResponse(Call<HttpMessage<String>> call, Response<HttpMessage<String>> response) {
                HttpMessage<String> data=response.body();
                if (data.getCode()==0){
                    String name = (String)data.getData();
                    Message message=new Message();
                    message.obj=name;
                    handler4.sendMessage(message);
                }
            }
            @Override
            public void onFailure(Call<HttpMessage<String>> call, Throwable t) {
            }
        });
    }
}
