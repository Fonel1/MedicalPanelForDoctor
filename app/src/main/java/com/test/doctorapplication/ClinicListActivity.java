package com.test.doctorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.gson.Gson;
import com.test.doctorapplication.Adapter.ClinicAdapter;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Common.SpacesItemDecoration;
import com.test.doctorapplication.Interface.IClinicLoadListener;
import com.test.doctorapplication.Interface.IGetDoctorListener;
import com.test.doctorapplication.Interface.IRememberUserListener;
import com.test.doctorapplication.Model.Clinic;
import com.test.doctorapplication.Model.Doctor;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class ClinicListActivity extends AppCompatActivity implements IClinicLoadListener, IRememberUserListener, IGetDoctorListener {

    @BindView(R.id.recycler_clinic)
    RecyclerView recycler_clinic;

    IClinicLoadListener iClinicLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_clinic_list);

        ButterKnife.bind(this);

        initView();

        initz();

        loadClinicFromCity(Common.city);
    }

    private void loadClinicFromCity(String name) {

        FirebaseFirestore.getInstance().collection("AllClinics")
                .document(name)
                .collection("Clinics")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<Clinic> clinics = new ArrayList<>();
                            for (DocumentSnapshot clinicSnapShot: task.getResult())
                            {
                                Clinic clinic = clinicSnapShot.toObject(Clinic.class);
                                clinic.setClinicId(clinicSnapShot.getId());
                                clinics.add(clinic);
                            }
                            iClinicLoadListener.onClinicLoadSuccess(clinics);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iClinicLoadListener.onClinicLoadFailed(e.getMessage());
            }
        });

    }

    private void initz() {
        iClinicLoadListener = this;
    }

    private void initView() {
        recycler_clinic.setHasFixedSize(true);
        recycler_clinic.setLayoutManager(new GridLayoutManager(this, 1));
        recycler_clinic.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onClinicLoadSuccess(List<Clinic> clinicList) {
        ClinicAdapter clinicAdapter = new ClinicAdapter(this, clinicList, this, this);
        recycler_clinic.setAdapter(clinicAdapter);

    }

    @Override
    public void onClinicLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRememberUserSuccess(String user) {
        Paper.init(this);
        Paper.book().write(Common.LOG_KEY, user);
        Paper.book().write(Common.CITY_KEY, Common.city);
        Paper.book().write(Common.CLINIC_KEY, new Gson().toJson(Common.currentClinic));

    }

    @Override
    public void onGetDoctorSuccess(Doctor doctor) {
        Common.currentDoctor = doctor;
        Paper.book().write(Common.DOCTOR_KEY, new Gson().toJson(doctor));
    }
}