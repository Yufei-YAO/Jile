package com.example.jile.ui.login;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.jile.Bean.User;
import com.example.jile.LogoActivity;
import com.example.jile.R;
import com.example.jile.Setting.ThemeSettingActivity;

import java.util.List;

public class ChangePasswordActivity extends AppCompatActivity {
    private String newPassword,pwd1,pwd2;
    private boolean b1,b2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ThemeSettingActivity.setActivityTheme(this);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_password);
        final Bundle bundle = getIntent().getExtras();
        final ImageButton btnBack = findViewById(R.id.btnBack);
        final Button btnComplete = findViewById(R.id.btnComplete);
        final EditText etPassword = findViewById(R.id.etPassword);
        final EditText etRepeatPassword = findViewById(R.id.etRepeatPassword);
        btnComplete.setEnabled(false);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnComplete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(pwd1.equals(pwd2)){
                    resetPassword(bundle.getString("username"),pwd1);
                    startActivity(new Intent(ChangePasswordActivity.this,LoginActivity.class));
                }else{
                    Toast.makeText(ChangePasswordActivity.this,"两次密码不一致",Toast.LENGTH_SHORT).show();
                }
            }
        });
        etPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwd1 = s.toString();
                b1 = !s.toString().equals("");
                if(b1&&b2){
                    btnComplete.setEnabled(true);
                }else{
                    btnComplete.setEnabled(false);
                }
            }
        });
        etRepeatPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                pwd2 = s.toString();
                b2 = !s.toString().equals("");
                if(b1&&b2){
                    btnComplete.setEnabled(true);
                }else{
                    btnComplete.setEnabled(false);
                }
                if(!pwd1.equals(pwd2)&&b2){
                    etRepeatPassword.setError("两次密码不一致");
                }
            }
        });
    }

    //
    private void resetPassword(String username,String newPassword){
        List<User> userList = LogoActivity.userDao.query();
        User user = null;
        for(User u:userList){
            if(u.getName().equals(username)){
                user = u;
            }
        }
        if(user==null){
            Toast.makeText(ChangePasswordActivity.this,"error in ChangePasswordActivity",Toast.LENGTH_SHORT).show();
        }else{
            user.setPassword(newPassword);
            LogoActivity.userDao.insert(user);
        }
        Toast.makeText(ChangePasswordActivity.this,username+"修改密码为"+pwd1,Toast.LENGTH_SHORT).show();
    }
}