package com.test.doctorapplication.Interface;

import android.content.DialogInterface;

public interface IDialogClickListener {
    void onClickLogin(DialogInterface dialogInterface, String userName, String password);
    void onClickCancel(DialogInterface dialogInterface);
}
