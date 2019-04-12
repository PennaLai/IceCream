package com.example.icecream;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

public class PersonalDetailActivity extends AppCompatActivity {

    private String userName;
    TextView userNameTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_personal_detail);
        userNameTextView = (TextView) findViewById(R.id.username);
        getUserInformation();
    }

    /**
     * @author: Penna
     * @Description:
     */
    public void getUserInformation(){
        Intent intentFromLogin =getIntent();
        if(intentFromLogin.hasExtra(Intent.EXTRA_TEXT)){
            userName = intentFromLogin.getStringExtra(Intent.EXTRA_TEXT);
            updateUserInformation();
        }
    }

    /**
     * @author: Penna
     * @Description: update the user information on the screen
     */
    public void updateUserInformation(){
        userNameTextView.setText(userName);
    }


}
