package com.example.icecream;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import com.rengwuxian.materialedittext.MaterialEditText;
import mehdi.sakout.fancybuttons.FancyButton;

public class LoginActivity extends AppCompatActivity {
    MaterialEditText usernameEdit;
    MaterialEditText passwordEdit;
    FancyButton login;
    FancyButton signUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        usernameEdit = (MaterialEditText) findViewById(R.id.username);
        passwordEdit = (MaterialEditText) findViewById(R.id.password);
        login = (FancyButton) findViewById(R.id.login);
        signUp = (FancyButton) findViewById(R.id.signup);
    }


    /**
     * @author: Penna
     * @Description: click login event
     */
    public void onLogin(View view){
        checkLoginValid();
    }

    /**
     * @author: Penna
     * @Description: check the usernameEdit and passwordEdit valid
     */
    public void checkLoginValid(){
        String username = usernameEdit.getText().toString();
        String password = passwordEdit.getText().toString();
        if (username.equals("") || password.equals("")){
            Toast.makeText(LoginActivity.this, "username or password should not be empty",  Toast.LENGTH_LONG).show();
        }else if(true){
            // TODO check more restriction that username and password should satisfy
        } else {
            Toast.makeText(LoginActivity.this, "login successful",  Toast.LENGTH_LONG).show();
            // TODO try to connect server to login
        }
    }

    public void onSignUp(View view){
        Toast.makeText(LoginActivity.this, "sing up successfully",  Toast.LENGTH_LONG).show();
    }

}
