package com.test.doctorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Common.SummaryReceivedResults;
import com.test.doctorapplication.Interface.IDialogFinishListener;
import com.test.doctorapplication.Model.SentTestInformation;
import com.test.doctorapplication.Model.SummaryOfTheVisit;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;

import butterknife.BindView;
import dmax.dialog.SpotsDialog;

public class TestInformationActivity extends AppCompatActivity implements IDialogFinishListener {

    TextView txtDate;
    TextView txtPName;
    TextView txtTName;
    ImageView imgReceived;
    Button btnFinish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_information);

        txtDate = findViewById(R.id.txt_time);
        txtPName = findViewById(R.id.txt_patient_name);
        txtTName = findViewById(R.id.txt_test_name);
        imgReceived = findViewById(R.id.receivedImage);
        btnFinish = findViewById(R.id.btn_finish);

        Intent intent = getIntent();
        String date = intent.getStringExtra("time");
        String pName = intent.getStringExtra("patientName");
        String tName = intent.getStringExtra("testName");
        String imageUrl = getIntent().getStringExtra("photo");

        Glide.with(this)
                .load(imageUrl)
                .into(imgReceived);

        txtDate.setText(date);
        txtPName.setText(pName);
        txtTName.setText(tName);

        FirebaseFirestore
                .getInstance()
                .collection("TestResults")
                .document(Common.test)
                .collection("Doctors")
                .document(Common.currentDoctor.getName())
                .collection("ReceivedTests")
                .whereEqualTo("photo", imageUrl)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    for (QueryDocumentSnapshot document:task.getResult())
                    {
                        String docId = document.getId();
                        Common.currentTest = docId;
                    }
                }
            }
        });

        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                summaryReceivedResults();
            }
        });

    }

    private void summaryReceivedResults()
    {
        SummaryReceivedResults.getInstance()
                .sSummaryReceivedResults("Opis",
                        "WYŚLIJ",
                        "ANULUJ",
                        this,
                        this);
    }

    @Override
    public void onClickSend(DialogInterface dialogInterface, String finish) {
        AlertDialog alertDialog = new SpotsDialog.Builder().setCancelable(false)
                .setContext(this).build();

        alertDialog.show();

        DocumentReference testSet = FirebaseFirestore.getInstance()
                .collection("TestResults")
                .document(Common.test)
                .collection("Doctors")
                .document(Common.currentDoctor.getName())
                .collection("ReceivedTests")
                .document(Common.currentTest);

        testSet.get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            Map<String, Object> dataUpdate = new HashMap<>();
                            dataUpdate.put("done", true);
                            dataUpdate.put("summaryTxt", finish);
                            testSet.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(TestInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            alertDialog.dismiss();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        updateInUserCollection(finish);
                                        Intent intent = new Intent(TestInformationActivity.this, DoctorHomeActivity.class);
                                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                        startActivity(intent);
                                        finish();
                                        dialogInterface.dismiss();
                                        alertDialog.dismiss();
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(TestInformationActivity.this, "Błąd", Toast.LENGTH_SHORT).show();
                                    dialogInterface.dismiss();
                                    alertDialog.dismiss();
                                }
                            });
                        }
                    }
                });
    }

    private void updateInUserCollection(String finish) {
        Intent intent = getIntent();
        String pEmail = intent.getStringExtra("patientEmail");
        String imageUrl = getIntent().getStringExtra("photo");

        CollectionReference testPatientRef = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(pEmail)
                .collection("SendTestResults");
        
        testPatientRef
                .whereEqualTo("photo", imageUrl)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            if (task.getResult().size() > 0)
                            {
                                DocumentReference userTestCurrentDocument = null;
                                for (DocumentSnapshot documentSnapshot: task.getResult())
                                {
                                    userTestCurrentDocument = testPatientRef.document(documentSnapshot.getId());
                                }
                                if (userTestCurrentDocument != null)
                                {
                                    Map<String, Object> dataUpdate = new HashMap<>();
                                    dataUpdate.put("done", true);
                                    dataUpdate.put("summaryTxt", finish);
                                    userTestCurrentDocument.update(dataUpdate)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Toast.makeText(TestInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                }
                            }
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(TestInformationActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

    }

    @Override
    public void onClickCancel(DialogInterface dialogInterface)
    {
        dialogInterface.dismiss();
    }
}