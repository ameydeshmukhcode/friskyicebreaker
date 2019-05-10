package com.frisky.icebreaker.ui.components.dialogs;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;

import com.frisky.icebreaker.R;

public class FiltersDialog extends Dialog {

    Button mApplyButton;
    Button mCancelButton;

    public FiltersDialog(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.dialog_filters);

        mApplyButton = findViewById(R.id.button_apply);
        mCancelButton = findViewById(R.id.button_cancel);

        mCancelButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
