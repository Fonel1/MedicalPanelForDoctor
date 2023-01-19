package com.test.doctorapplication.Common;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.material.textfield.TextInputEditText;
import com.test.doctorapplication.Interface.IDialogClickListener;
import com.test.doctorapplication.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class LoginDialog {

    @BindView(R.id.txt_title)
    TextView txt_title;
    @BindView(R.id.edt_user)
    TextInputEditText edt_user;
    @BindView(R.id.edt_password)
    TextInputEditText edt_password;
    @BindView(R.id.button_login)
    Button button_login;
    @BindView(R.id.button_cancel)
    Button button_cancel;

    public static LoginDialog cDialog;
    public IDialogClickListener iDialogClickListener;

    public static LoginDialog getInstance() {
        if (cDialog == null)
            cDialog = new LoginDialog();
        return cDialog;
    }

    public void sLoginDialog(String title,
                             String positive,
                             String negative,
                             Context context,
                             IDialogClickListener iDialogClickListener) {
        this.iDialogClickListener = iDialogClickListener;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_login);

        ButterKnife.bind(this, dialog);

        //title
        if (!TextUtils.isEmpty(title))
        {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);
        }
        button_login.setText(positive);
        button_cancel.setText(negative);

        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        button_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickLogin(dialog, edt_user.getText().toString(),
                        edt_password.getText().toString());
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogClickListener.onClickCancel(dialog);
            }
        });
    }

}