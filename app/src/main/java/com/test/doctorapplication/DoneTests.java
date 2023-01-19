package com.test.doctorapplication;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.jaredrummler.materialspinner.MaterialSpinner;
import com.test.doctorapplication.Adapter.DoneTestsAdapter;
import com.test.doctorapplication.Adapter.TestInfoAdapter;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Interface.ITestTypeLoadListener;
import com.test.doctorapplication.Interface.RecyclerViewInterface;
import com.test.doctorapplication.Model.Event.DoctorReceivedTestLoadEvent;
import com.test.doctorapplication.Model.SentTestInformation;

import org.checkerframework.checker.units.qual.A;
import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoneTests extends AppCompatActivity implements ITestTypeLoadListener, RecyclerViewInterface {

    Unbinder unbinder;

    @BindView(R.id.spinner_testType)
    MaterialSpinner spinner_testType;
    @BindView(R.id.recyclerTest)
    RecyclerView recyclerTest;

    CollectionReference allTestRef;

    ITestTypeLoadListener iTestTypeLoadListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_done_tests);

        unbinder = ButterKnife.bind(this);

        allTestRef = FirebaseFirestore.getInstance().collection("TestResults");

        iTestTypeLoadListener = this;

        initView();
        loadTestType();

    }

    private void loadTestType() {
        allTestRef
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful())
                        {
                            List<String> list = new ArrayList<>();
                            list.add("Wybierz rodzaj badań");
                            for (QueryDocumentSnapshot documentSnapshot:task.getResult())
                                list.add(documentSnapshot.getId());
                            iTestTypeLoadListener.onTestTypeLoadSuccess(list);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                iTestTypeLoadListener.onTestTypeLoadFailed(e.getMessage());
            }
        });
    }

    private void loadDoctorReceivedTests(String testName) {
        Common.test = testName;

        CollectionReference receivedTests = FirebaseFirestore
                .getInstance()
                .collection("TestResults")
                .document(Common.test)
                .collection("Doctors")
                .document(Common.currentDoctor.getName())
                .collection("ReceivedTests");

        receivedTests.
                whereEqualTo("done", true)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        EventBus.getDefault().postSticky(new DoctorReceivedTestLoadEvent(false, e.getMessage()));
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    List<SentTestInformation> informationList = new ArrayList<>();
                    for (DocumentSnapshot receivedTestSnapShot:task.getResult())
                    {
                        SentTestInformation information = receivedTestSnapShot.toObject(SentTestInformation.class);
                        informationList.add(information);
                    }
                    EventBus.getDefault().postSticky(new DoctorReceivedTestLoadEvent(true, informationList));
                }
            }
        });
    }

    private void initView() {
        recyclerTest.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerTest.setLayoutManager(layoutManager);
        recyclerTest.addItemDecoration(new DividerItemDecoration(this, layoutManager.getOrientation()));
    }
    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void displayData(DoctorReceivedTestLoadEvent event)
    {
        if (event.isSuccess())
        {
            DoneTestsAdapter adapter = new DoneTestsAdapter(this, event.getsentTestInformationList(), this);
            recyclerTest.setAdapter(adapter);
        }
        else
        {
            Toast.makeText(this, ""+event.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onTestTypeLoadSuccess(List<String> areaNameList) {
        spinner_testType.setItems(areaNameList);
        spinner_testType.setOnItemSelectedListener(new MaterialSpinner.OnItemSelectedListener() {
            @Override
            public void onItemSelected(MaterialSpinner view, int position, long id, Object item) {
                if (position > 0)
                {
                    loadDoctorReceivedTests(item.toString());
                } else
                {
                    recyclerTest.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onTestTypeLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void onLongItemClick(int position) {

    }
}