package com.test.doctorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Model.FCMResponse;
import com.test.doctorapplication.Model.FCMSendData;
import com.test.doctorapplication.Model.MyToken;
import com.test.doctorapplication.Model.SummaryOfTheVisit;
import com.test.doctorapplication.Retrofit.IFCMService;
import com.test.doctorapplication.Retrofit.RetrofitClient;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

public class DoneAppointmentActivity extends AppCompatActivity {

    IFCMService ifcmService;

    @BindView(R.id.txt_patient_name)
    TextView txt_patient_name;
    @BindView(R.id.txt_patient_phone)
    TextView txt_patient_phone;

    @BindView(R.id.input_summary)
    TextInputEditText input_summary;
    @BindView(R.id.btn_finish)
    Button btn_finish;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_appointment);

        ButterKnife.bind(this);

        setPatientInformation();

        ifcmService = RetrofitClient.getInstance().create(IFCMService.class);

        btn_finish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uploadSummaryAppointment();
            }
        });

    }

    private void uploadSummaryAppointment() {


        //Update appointment status false => true
        DocumentReference appointmentSet = FirebaseFirestore.getInstance()
                .collection("AllClinics")
                .document(Common.city)
                .collection("Clinics")
                .document(Common.currentClinic.getClinicId())
                .collection("Doctors")
                .document(Common.currentDoctor.getDoctorId())
                .collection(Common.simpleDateFormat.format(Common.currentData.getTime()))
                .document(Common.currentAppointmentInfo.getApptId());

        appointmentSet
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                        if (task.isSuccessful())
                        {
                            //Update
                            Map<String, Object> dataUpdate = new HashMap<>();
                            dataUpdate.put("done", true);
                            appointmentSet.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoneAppointmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    }).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful())
                                    {
                                        updateStatusinUser();
                                        createSummary();
                                        Toast.makeText(DoneAppointmentActivity.this, "Wizyta zakończona", Toast.LENGTH_SHORT).show();
                                        Intent intent = new Intent(DoneAppointmentActivity.this, DoctorHomeActivity.class);
                                        startActivity(intent);
                                    }
                                }
                            });
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(DoneAppointmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void updateStatusinUser() {
        CollectionReference userAppointment;

        String summaryText = input_summary.getText().toString();

        userAppointment = FirebaseFirestore.getInstance()
                .collection("Users")
                .document(Common.currentAppointmentInfo.getPatientEmail())
                .collection("Appointment");

        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, 0);
        calendar.add(Calendar.HOUR_OF_DAY,0);
        calendar.add(Calendar.MINUTE, 0);

        Timestamp timestamp = new Timestamp(calendar.getTimeInMillis());

        userAppointment
                .whereGreaterThanOrEqualTo("timestamp", timestamp)
                .whereEqualTo("done", false)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoneAppointmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size() > 0)
                    {
                        //Update
                        DocumentReference userAppointmentCurrentDocument = null;
                        for (DocumentSnapshot documentSnapshot: task.getResult())
                        {
                            userAppointmentCurrentDocument = userAppointment.document(documentSnapshot.getId());

                        }
                        if (userAppointmentCurrentDocument != null)
                        {
                            Map<String, Object> dataUpdate = new HashMap<>();
                            dataUpdate.put("done", true);
                            dataUpdate.put("summaryText", summaryText);
                            userAppointmentCurrentDocument.update(dataUpdate)
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            Toast.makeText(DoneAppointmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                        }
                                    });
                        }
                    }
                }
            }
        });
    }

    private void createSummary() {

        String summaryText = input_summary.getText().toString();

        CollectionReference summaryRef = FirebaseFirestore.getInstance()
                .collection("AllClinics")
                .document(Common.city)
                .collection("Clinics")
                .document(Common.currentClinic.getClinicId())
                .collection("AppointmentSummary");

        SummaryOfTheVisit summaryOfTheVisit = new SummaryOfTheVisit();

        summaryOfTheVisit.setDoctorId(Common.currentDoctor.getDoctorId());
        summaryOfTheVisit.setDoctorName(Common.currentDoctor.getName());

        summaryOfTheVisit.setClinicId(Common.currentClinic.getClinicId());
        summaryOfTheVisit.setClinicName(Common.currentClinic.getName());
        summaryOfTheVisit.setClinicAddress(Common.currentClinic.getAddress());

        summaryOfTheVisit.setPatientName(Common.currentAppointmentInfo.getPatientName());
        summaryOfTheVisit.setPatientEmail(Common.currentAppointmentInfo.getPatientEmail());

        summaryOfTheVisit.setSummaryText(summaryText);

        summaryRef.document()
                .set(summaryOfTheVisit)
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoneAppointmentActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful())
                {
                    sendNotificationUpdateToUser(Common.currentAppointmentInfo.getPatientName());
                }
            }
        });
    }

    private void sendNotificationUpdateToUser(String patientEmail) {
        FirebaseFirestore.getInstance()
                .collection("Tokens")
                .whereEqualTo("userEmail", patientEmail)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult().size() > 0)
                        {
                            MyToken myToken = new MyToken();
                            for (DocumentSnapshot tokenSnapShot: task.getResult())
                                myToken = tokenSnapShot.toObject(MyToken.class);

                            FCMSendData fcmSendData = new FCMSendData();
                            Map<String, String> dataSend = new HashMap<>();
                            dataSend.put("update_done", "true");

                            fcmSendData.setTo(myToken.getToken());
                            fcmSendData.setData(dataSend);

                            ifcmService.sendNotification(fcmSendData)
                                    .subscribeOn(Schedulers.io())
                                    .observeOn(Schedulers.newThread())
                                    .subscribe(new Consumer<FCMResponse>() {
                                        @Override
                                        public void accept(FCMResponse fcmResponse) throws Exception {

                                        }
                                    }, new Consumer<Throwable>() {
                                        @Override
                                        public void accept(Throwable throwable) throws Exception {
                                            Toast.makeText(DoneAppointmentActivity.this, throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        }
                                    });
                        }
                    }
                });
    }

    private void setPatientInformation() {
        txt_patient_name.setText(Common.currentAppointmentInfo.getPatientName());
        txt_patient_phone.setText(Common.currentAppointmentInfo.getPatientEmail());
    }
}