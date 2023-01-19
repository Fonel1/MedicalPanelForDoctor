package com.test.doctorapplication.Common;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.test.doctorapplication.Interface.IDialogClickListener;
import com.test.doctorapplication.Interface.IDialogFinishListener;
import com.test.doctorapplication.R;

import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;

public class SummaryReceivedResults {

    @BindView(R.id.edt_finish)
    EditText edt_finish;
    @BindView(R.id.button_send)
    Button button_send;
    @BindView(R.id.button_cancel)
    Button button_cancel;
    @BindView(R.id.txt_title)
    TextView txt_title;


    public static SummaryReceivedResults cDialog;
    public IDialogFinishListener iDialogFinishListener;

    public static SummaryReceivedResults getInstance() {
        if (cDialog == null)
            cDialog = new SummaryReceivedResults();
        return cDialog;
    }

    public void sSummaryReceivedResults(String title,
                                        String positive,
                                        String negative,
                                        Context context,
                                        IDialogFinishListener iDialogFinishListener)
    {
        this.iDialogFinishListener = iDialogFinishListener;

        Dialog dialog = new Dialog(context);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.layout_finish);

        ButterKnife.bind(this, dialog);

        if (!TextUtils.isEmpty(title))
        {
            txt_title.setText(title);
            txt_title.setVisibility(View.VISIBLE);
        }
        button_send.setText(positive);
        button_cancel.setText(negative);

        dialog.setCancelable(true);
        dialog.show();

        Window window = dialog.getWindow();
        window.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);

        button_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (edt_finish.getText().toString().isEmpty()) {
                    Toast.makeText(context, "Podaj opis zgłoszenia!", Toast.LENGTH_SHORT).show();
                } else {
                    iDialogFinishListener.onClickSend(dialog, edt_finish.getText().toString());
                }
            }
        });

        button_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iDialogFinishListener.onClickCancel(dialog);
            }
        });
    }
}