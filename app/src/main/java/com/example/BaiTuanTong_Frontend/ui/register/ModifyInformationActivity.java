package com.example.BaiTuanTong_Frontend.ui.register;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.BaiTuanTong_Frontend.R;

public class ModifyInformationActivity extends AppCompatActivity {

    private RegistViewModel modifyInfViewModel;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_information);

        modifyInfViewModel = new ViewModelProvider(this, new RegistViewModelFactory())
                .get(RegistViewModel.class);

        final EditText usernameEditText = findViewById(R.id.modifyInfUserName);
        final EditText passwordEditText = findViewById(R.id.modifyInfPassword);
        final EditText passwordConfirmText = findViewById(R.id.modifyInfConfirmPassword);
        final EditText emailEditText = findViewById(R.id.modifyInfEmail);
        final Button modifyInfButton = findViewById(R.id.modifyInfButton);

        String username = "zhp";
        String email = "1800012xxx@pku.edu.cn";
        String password = "123456";
        String confirmPassword = password;

        usernameEditText.setText(username);
        emailEditText.setText(email);
        passwordEditText.setText(password);
        passwordConfirmText.setText(confirmPassword);

        modifyInfViewModel.getRegistFormState().observe(this, new Observer<RegistFormState>() {
            @Override
            public void onChanged(@Nullable RegistFormState modifyInfFormState) {
                if (modifyInfFormState == null) {
                    return;
                }
                modifyInfButton.setEnabled(modifyInfFormState.isDataValid());
                if (modifyInfFormState.getUsernameError() != null) {
                    //usernameEditText.setError(getString(modifyInfFormState.getUsernameError()));
                }
                if (modifyInfFormState.getEmailError() != null) {
                    //emailEditText.setError(getString(modifyInfFormState.getEmailError()));
                }
                if (modifyInfFormState.getPasswordError() != null) {
                    passwordEditText.setError(getString(modifyInfFormState.getPasswordError()));
                }
                if (modifyInfFormState.getPasswordMisMatch() != null) {
                    passwordConfirmText.setError(getString(modifyInfFormState.getPasswordMisMatch()));
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
                modifyInfViewModel.registDataChanged("",
                        "", passwordEditText.getText().toString(), passwordConfirmText.getText().toString());
            }
        };
        //usernameEditText.addTextChangedListener(afterTextChangedListener);
        passwordEditText.addTextChangedListener(afterTextChangedListener);
        //emailEditText.addTextChangedListener(afterTextChangedListener);
        passwordConfirmText.addTextChangedListener(afterTextChangedListener);

        modifyInfButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                return;
            }
        });
    }

}