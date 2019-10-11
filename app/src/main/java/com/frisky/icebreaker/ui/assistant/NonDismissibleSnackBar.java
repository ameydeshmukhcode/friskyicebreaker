package com.frisky.icebreaker.ui.assistant;

import android.view.View;

import androidx.annotation.NonNull;

import com.google.android.material.snackbar.BaseTransientBottomBar;

public class NonDismissibleSnackBar extends BaseTransientBottomBar.Behavior {
    @Override
    public boolean canSwipeDismissView(@NonNull View view) {
        return false;
    }
}
