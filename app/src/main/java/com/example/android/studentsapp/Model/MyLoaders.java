package com.example.android.studentsapp.Model;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.ViewGroup;
import android.view.Window;

import com.example.android.studentsapp.HomeActivity;
import com.example.android.studentsapp.MainActivity;
import com.example.android.studentsapp.R;

public class MyLoaders {
    Dialog dialog;
    public void settingUpLoader(Context context){

        if(dialog != null && dialog.isShowing())
            return;

        dialog = new Dialog(context, R.style.LoaderStyle);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_loader);

        if(dialog.getWindow() == null)
            return;

        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.show();

    }

    public void hideLoadingDialog() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (dialog != null)
                    dialog.dismiss();
            }
        },5000);

    }
}
