package clubmanage.ui.my;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import clubmanage.model.User;
import clubmanage.ui.CircleImageView;
import clubmanage.ui.LoginActivity;
import clubmanage.ui.My_attention;
import clubmanage.ui.PersonalCenterActivity;
import clubmanage.ui.R;
import clubmanage.ui.Register;
import clubmanage.ui.Setting;
import clubmanage.util.ClubManageUtil;

import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;

public class My_Fragement extends Fragment implements View.OnClickListener {
    private TextView clubNm;
    private TextView activityNm;
    private TextView attNm;
    private CircleImageView img;
    private TextView text;
    private Handler handler2=new Handler(){
        public void handleMessage(Message msg){
            int num =(int) msg.obj;
            clubNm.setText(Integer.toString(num));
        }
    };
    private Handler handler3=new Handler(){
        public void handleMessage(Message msg){
            int num =(int) msg.obj;
            activityNm.setText(Integer.toString(num));
        }
    };
    private Handler handler4=new Handler(){
        public void handleMessage(Message msg){
            int num =(int) msg.obj;
            attNm.setText(Integer.toString(num));
        }
    };
    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.my,container,false);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Button changeUser=(Button)getActivity().findViewById(R.id.my_btn1);
        changeUser.setOnClickListener(this);
        Button exit=(Button)getActivity().findViewById(R.id.my_btn2);
        exit.setOnClickListener(this);
        ImageButton reg=(ImageButton)getActivity().findViewById(R.id.register);
        reg.setOnClickListener(this);
        ImageButton setting=(ImageButton)getActivity().findViewById(R.id.setting);
        setting.setOnClickListener(this);

        RelativeLayout club_num=(RelativeLayout)getActivity().findViewById(R.id.club_num);
        club_num.setOnClickListener(this);
        RelativeLayout activity_num=(RelativeLayout)getActivity().findViewById(R.id.activity_num);
        activity_num.setOnClickListener(this);
        RelativeLayout focus_num=(RelativeLayout)getActivity().findViewById(R.id.focus_num);
        focus_num.setOnClickListener(this);

        RelativeLayout my_club=(RelativeLayout)getActivity().findViewById(R.id.my_club);
        my_club.setOnClickListener(this);
        RelativeLayout my_activity=(RelativeLayout)getActivity().findViewById(R.id.my_activity);
        my_activity.setOnClickListener(this);
        RelativeLayout my_focus=(RelativeLayout)getActivity().findViewById(R.id.my_focus);
        my_focus.setOnClickListener(this);
        RelativeLayout personal_center=(RelativeLayout)getActivity().findViewById(R.id.personal_center);
        personal_center.setOnClickListener(this);

        img=(CircleImageView)getActivity().findViewById(R.id.my_head_img1);
        initHead();
        text=(TextView)getActivity().findViewById(R.id.my_head_btn3);
        initName();
        clubNm=(TextView)getActivity().findViewById(R.id.my_neck_text1);
        activityNm=(TextView)getActivity().findViewById(R.id.my_neck_text5);
        attNm=(TextView)getActivity().findViewById(R.id.my_neck_text3);
        initClubNumber();
        initActivityMumber();
        initAttNumber();
    }
    private void initClubNumber(){
        new Thread(){
            @Override
            public void run() {
                int clubnum= ClubManageUtil.clubManage.searchMyClubCount(User.currentLoginUser.getUid());
                Message message=new Message();
                message.obj=clubnum;
                handler2.sendMessage(message);
            }
        }.start();
    }

    private void initActivityMumber(){
        new Thread(){
            @Override
            public void run() {
                int num= ClubManageUtil.activityManage.searchMyActivityCount(User.currentLoginUser.getUid());
                Message message=new Message();
                message.obj=num;
                handler3.sendMessage(message);
            }
        }.start();
    }

    private void initAttNumber(){
        new Thread(){
            @Override
            public void run() {
                int num= ClubManageUtil.attentionManage.searchAttenCount(User.currentLoginUser.getUid());
                Message message=new Message();
                message.obj=num;
                handler4.sendMessage(message);
            }
        }.start();
    }

    private void initHead(){
        byte[] bt= User.currentLoginUser.getImage();
        if(bt!=null){
            img.setImageBitmap(BitmapFactory.decodeByteArray(bt, 0, bt.length));
        }else img.setImageResource(R.drawable.photo1);
    }

    private void initName(){
        text.setText(User.currentLoginUser.getName());
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.my_btn1:
                Intent intent1=new Intent(getActivity(), LoginActivity.class);
                getActivity().startActivity(intent1);
                break;
            case R.id.my_btn2:
                new AlertDialog.Builder(getActivity())
                        .setTitle("确定退出吗？")
                        .setIcon(android.R.drawable.ic_dialog_info)
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface arg0, int arg1) { getActivity().finish();
                            }
                        })
                        .setNegativeButton("取消", null)
                        .show();
                break;
            case R.id.register:
                Intent intent2=new Intent(getActivity(), Register.class);
                startActivityForResult(intent2,1);
                getActivity().overridePendingTransition(R.anim.abc_fade_in,R.anim.abc_fade_out);
                break;
            case R.id.setting:
                Intent intent3=new Intent(getActivity(), Setting.class);
                startActivity(intent3);
                break;
            case R.id.club_num:
                break;
            case R.id.activity_num:
                break;
            case R.id.focus_num:
                Intent intent4 = new Intent(getActivity(), My_attention.class);
                getActivity().startActivity(intent4);
                break;
            case R.id.my_club:
                break;
            case R.id.my_activity:
                break;
            case R.id.my_focus:
                break;
            case R.id.personal_center:
                Intent intent8 = new Intent(getActivity(), PersonalCenterActivity.class);
                getActivity().startActivity(intent8);
                break;
        }
    }
    @Override
    public void onActivityResult(int requestCode,int resultCode,Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode){
            case 1:
                if(resultCode==RESULT_OK){
                    getActivity().finish();
                }
                if(requestCode==RESULT_CANCELED){
                    return;
                }
                break;
        }
    }

    @Override
     public void onHiddenChanged(boolean hidden){
        super.onHiddenChanged(hidden);
        if (hidden) {
            return;
        }else{
            initHead();
            initName();
            initClubNumber();
            initActivityMumber();
            initAttNumber();
        }
    }
}