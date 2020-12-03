package com.example.BaiTuanTong_Frontend.ui.register;

import android.app.Activity;

import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.StringRes;
import androidx.appcompat.app.AppCompatActivity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.BaiTuanTong_Frontend.R;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModel;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModelFactory;
import com.example.BaiTuanTong_Frontend.ui.register.RegistViewModel;

public class RegistActivity extends AppCompatActivity {

    private RegistViewModel registViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_regist);

        registViewModel = new ViewModelProvider(this, new RegistViewModelFactory())
                .get(RegistViewModel.class);

        final EditText usernameEditText = findViewById(R.id.registUserName);
        final EditText passwordEditText = findViewById(R.id.registPassword);
        final EditText passwordConfirmText = findViewById(R.id.registConfirmPassword);
        final EditText emailEditText = findViewById(R.id.registEmail);
        final Button registButton = findViewById(R.id.registButton);

        registViewModel.getRegistFormState().observe(this, new Observer<RegistFormState>() {
            @Override
            public void onChanged(@Nullable RegistFormState registFormState) {
                if (registFormState == null) {
                    return;
                }
                registButton.setEnabled(registFormState.isDataValid());
                if (registFormState.getUsernameError() != null) {
                    usernameEditText.setError(getString(registFormState.getUsernameError()));
                }
                if (registFormState.getEmailError() != null) {
                    emailEditText.setError(getString(registFormState.getEmailError()));
                }
                if (registFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(registFormState.getPasswordError()));
                }
                if (registFormState.getPasswordMisMatch() != null) {
                    passwordConfirmText.setError(getString(registFormState.getPasswordMisMatch()));
                }
            }
        });

        TextWatcher afterTextChangedListener = new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // ignore
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // ignore
            }

            @Override
            public void afterTextChanged(Editable s) {
                registViewModel.registDataChanged(usernameEditText.getText().toString(),
                        emailEditText.getText().toString(), passwordEditText.getText().toString(), passwordConfirmText.getText().toString());
            }
        };
        usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmText.addTextChangedListener(afterTextChangedListener);

        registButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }

}