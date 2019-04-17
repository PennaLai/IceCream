package com.example.icecream;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import com.github.glomadrian.codeinputlib.CodeInput;
import com.mob.MobSDK;
import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener{

    private EditText etPhoneNumber;
    private Button sendVerificationCode;
    // private EditText etVerificationCode;
    private CodeInput pinCode;
    private Button nextStep;

    private String phoneNumber;
    private String verificationCode;

    boolean success = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);


        etPhoneNumber = (EditText) findViewById(R.id.phoneNumber);
//        etVerificationCode = (EditText) findViewById(R.id.verificationCode);
        pinCode = (CodeInput) findViewById(R.id.pinCode);
        sendVerificationCode = (Button) findViewById(R.id.btn_getVerificationCode);
        nextStep = (Button) findViewById(R.id.btn_checkVerificationCode);
        sendVerificationCode.setOnClickListener(this);
        nextStep.setOnClickListener(this);

        initSMSSDK();

    }


    public void initSMSSDK(){
        MobSDK.init(this);
        // the event handler for the SMSSDK
        EventHandler eh=new EventHandler(){

            @Override
            public void afterEvent(int event, int result, Object data) {
                // afterEvent会在子线程被调用，因此如果后续有UI相关操作，需要将数据发送到UI线程
                Message msg = new Message();
                msg.arg1 = event;
                msg.arg2 = result;
                msg.obj = data;
                new Handler(Looper.getMainLooper(), new Handler.Callback() {
                    @Override
                    public boolean handleMessage(Message msg) {
                        int event = msg.arg1;
                        int result = msg.arg2;
                        Object data = msg.obj;
                        if (event == SMSSDK.EVENT_GET_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理成功得到验证码的结果
                                Toast.makeText(RegisterActivity.this, "发送成功",  Toast.LENGTH_LONG).show();
                                // 请注意，此时只是完成了发送验证码的请求，验证码短信还需要几秒钟之后才送达
                            } else {
                                // TODO 处理错误的结果
                                ((Throwable) data).printStackTrace();
                            }
                        } else if (event == SMSSDK.EVENT_SUBMIT_VERIFICATION_CODE) {
                            if (result == SMSSDK.RESULT_COMPLETE) {
                                // TODO 处理验证码验证通过的结果
                                Toast.makeText(RegisterActivity.this, "验证成功",  Toast.LENGTH_LONG).show();
                            } else {
                                // TODO 处理错误的结果
                                Toast.makeText(RegisterActivity.this, "验证失败",  Toast.LENGTH_LONG).show();
                                ((Throwable) data).printStackTrace();
                            }
                        }
                        // TODO 其他接口的返回结果也类似，根据event判断当前数据属于哪个接口
                        return false;
                    }
                }).sendMessage(msg);

            }
        };
        SMSSDK.registerEventHandler(eh); //注册短信回调
    }



    /**
     * @author: Penna
     * @Description: onClick function, to check what component the user click
     * and do the corresponding operation.
     */
    @Override
    public void onClick(View v){
        switch (v.getId()){
            case R.id.btn_getVerificationCode:
                //  TODO: check the valid of the phone number before send message
                phoneNumber = etPhoneNumber.getText().toString();
                SMSSDK.getVerificationCode("86", phoneNumber);
                break;

//            case R.id.btn_checkVerificationCode:
//                verificationCode = etVerificationCode.getText().toString();
//                SMSSDK.submitVerificationCode("86", phoneNumber, verificationCode);
//                break;
        }
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        SMSSDK.unregisterAllEventHandler();  // 注销回调接口
    }

}
