package com.example.icecream;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText username;
    MaterialEditText password;
    FancyButton login;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (MaterialEditText) findViewById(R.id.username);
        password = (MaterialEditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.login);
    }


    /**
     * @author: Penna
     * @Description: click login event
     */
    public void onLogin(View view){
        System.out.println(username.getText());
        System.out.println(password.getText());
        Toast.makeText(LoginActivity.this, username.getText(),  Toast.LENGTH_LONG).show();
    }
}
