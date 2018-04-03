package com.cqebd.student.test;

import android.Manifest;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.cqebd.student.R;
import com.cqebd.student.netease.NetEaseCache;
import com.cqebd.student.netease.ui.ChatRoomActivity;
import com.netease.nimlib.sdk.AbortableFuture;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.orhanobut.logger.Logger;
import com.tbruyelle.rxpermissions2.RxPermissions;

public class TestNetEaseActivity extends AppCompatActivity implements View.OnClickListener {

    private Button btn_login,btn_logOut,btn_join;
    private TextView tv_status,tv_number;
    private EditText et_number;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_net_ease);

        btn_login = findViewById(R.id.test_btn_login);
        btn_logOut = findViewById(R.id.test_btn_logout);
        btn_join = findViewById(R.id.test_btn_join);
        tv_status = findViewById(R.id.test_tv_status);
        tv_number = findViewById(R.id.test_tv_number);
        et_number = findViewById(R.id.test_et_number);


        btn_login.setOnClickListener(this);
        btn_logOut.setOnClickListener(this);
        btn_join.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.test_btn_login:
                login();
                break;
            case R.id.test_btn_logout:
                break;
            case R.id.test_btn_join:
//                TestChatRoomActivity.start(this,et_number.getText().toString(),false);

                RxPermissions rxPermissions = new RxPermissions(this);
                rxPermissions
                        .request(
                                Manifest.permission.CAMERA,
                                Manifest.permission.RECORD_AUDIO,
                                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                Manifest.permission.READ_EXTERNAL_STORAGE)
                        .subscribe(granted -> {
                            if (granted) {
                                ChatRoomActivity.start(this,et_number.getText().toString(),false);
                                // All requested permissions are granted
                            } else {
                                Toast.makeText(this,"您拒绝了权限，无法开启直播，下次请允许权限",Toast.LENGTH_SHORT).show();
                                // At least one permission is denied
                            }
                        });


                break;
        }
    }


    private AbortableFuture<LoginInfo> loginRequest;
    private void login() {
        loginRequest = NIMClient.getService(AuthService.class).login(new LoginInfo("jiangjunjuel", "b7cd803fa8cba6e9ec5b3f2b0683a914"));
        loginRequest.setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo param) {
                Logger.d(param.getAccount());
                loginRequest = null;
                tv_status.setText("登录状态：已登录，登录账号jiangjunjuel");

                NetEaseCache.setAccount("jiangjunjuel");

            }

            @Override
            public void onFailed(int code) {
                if (code == 302 || code == 404) {
                    Toast.makeText(TestNetEaseActivity.this, "账号或密码错误", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(TestNetEaseActivity.this, "登录失败: " + code, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onException(Throwable exception) {
                Logger.d(exception);
            }
        });
    }




}
