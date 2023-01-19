package com.test.doctorapplication.Interface;

import android.content.DialogInterface;

public interface IDialogFinishListener {
    void onClickSend(DialogInterface dialogInterface, String finish);
    void onClickCancel(DialogInterface dialogInterface);
}
