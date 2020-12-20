package com.example.BaiTuanTong_Frontend.ui.register;

import androidx.annotation.Nullable;

/**
 * Data validation state of the login form.
 */
class RegistFormState {
    @Nullable
    private Integer usernameError;
    @Nullable
    private Integer emailError;
    @Nullable
    private Integer passwordError;
    @Nullable
    private Integer passwordMisMatch;

    private boolean isDataValid;

    RegistFormState(@Nullable Integer usernameError, @Nullable Integer emailError, @Nullable Integer passwordError, @Nullable Integer passwordMismatch) {
        this.emailError = emailError;
        this.usernameError = usernameError;
        this.passwordError = passwordError;
        this.passwordMisMatch = passwordMismatch;
        this.isDataValid = false;
    }

    RegistFormState(boolean isDataValid) {
        this.emailError = null;
        this.usernameError = null;
        this.passwordError = null;
        this.passwordMisMatch = null;
        this.isDataValid = isDataValid;
    }

    @Nullable
    Integer getUsernameError() { return usernameError; }

    @Nullable
    Integer getPasswordError() {
        return passwordError;
    }

    @Nullable
    Integer getPasswordMisMatch() {
        return passwordMisMatch;
    }

    @Nullable
    Integer getEmailError() {
        return emailError;
    }

    boolean isDataValid() {
        return isDataValid;
    }
}