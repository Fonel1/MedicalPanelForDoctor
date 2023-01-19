package com.test.doctorapplication;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.common.reflect.TypeToken;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.gson.Gson;
import com.test.doctorapplication.Adapter.StateAdapter;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Common.SpacesItemDecoration;
import com.test.doctorapplication.Interface.IOnAllStateLoadListener;
import com.test.doctorapplication.Model.City;
import com.test.doctorapplication.Model.Clinic;
import com.test.doctorapplication.Model.Doctor;
import com.test.doctorapplication.Common.Common;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.paperdb.Paper;

public class MainActivity extends AppCompatActivity implements IOnAllStateLoadListener {

    @BindView(R.id.recycler_state)
    RecyclerView recycler_state;

    CollectionReference allClinicsCollection;

    IOnAllStateLoadListener iOnAllStateLoadListener;

    StateAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        Common.updateToken(MainActivity.this, task.getResult());
                            String token = task.getResult();
                            Log.d("MPToken ", token);
                    }
                });

        Paper.init(this);
        String user = Paper.book().read(Common.LOG_KEY);
        if (TextUtils.isEmpty(user))
        {
            setContentView(R.layout.activity_main);

            ButterKnife.bind(this);

            initView();

            initz();

            loadAllStateFromFireStore();
        }
        else
        {
            Gson gson = new Gson();
            Common.city = Paper.book().read(Common.CITY_KEY);
            Common.currentClinic = gson.fromJson(Paper.book().read(Common.CLINIC_KEY, ""), new TypeToken<Clinic>(){}.getType());
            Common.currentDoctor = gson.fromJson(Paper.book().read(Common.DOCTOR_KEY, ""), new TypeToken<Doctor>(){}.getType());

            Intent intent = new Intent(this, DoctorHomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
            finish();
        }


    }


    private void loadAllStateFromFireStore() {
        allClinicsCollection
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        iOnAllStateLoadListener.onAllStateLoadFailed(e.getMessage());
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    List<City> cities = new ArrayList<>();
                    for (DocumentSnapshot citySnapShot: task.getResult())
                    {
                        City city = citySnapShot.toObject(City.class);
                        cities.add(city);
                    }
                    iOnAllStateLoadListener.onAllStateLoadSuccess(cities);
                }
            }
        });
    }

    private void initz() {
        allClinicsCollection = FirebaseFirestore.getInstance().collection("AllClinics");
        iOnAllStateLoadListener = this;
    }

    private void initView() {
        recycler_state.setHasFixedSize(true);
        recycler_state.setLayoutManager(new GridLayoutManager(this, 1));
        recycler_state.addItemDecoration(new SpacesItemDecoration(8));
    }

    @Override
    public void onAllStateLoadSuccess(List<City> cityList) {
        adapter = new StateAdapter(this, cityList);
        recycler_state.setAdapter(adapter);
    }

    @Override
    public void onAllStateLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}