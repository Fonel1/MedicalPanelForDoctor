package com.test.doctorapplication;

import static com.test.doctorapplication.Common.Common.simpleDateFormat;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.doctorapplication.Adapter.DataSlotAdapter;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Common.SpacesItemDecoration;
import com.test.doctorapplication.Interface.IDataSlotLoadListener;
import com.test.doctorapplication.Interface.INotificationListener;
import com.test.doctorapplication.Model.AppointmentInformation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import devs.mulham.horizontalcalendar.HorizontalCalendar;
import devs.mulham.horizontalcalendar.HorizontalCalendarView;
import devs.mulham.horizontalcalendar.utils.HorizontalCalendarListener;
import io.paperdb.Paper;

public class DoctorHomeActivity extends AppCompatActivity implements IDataSlotLoadListener, INotificationListener {

    @BindView(R.id.activity_main)
    DrawerLayout drawerLayout;
    @BindView(R.id.navigation_view)
    NavigationView navigationView;

    ActionBarDrawerToggle actionBarDrawerToggle;

    IDataSlotLoadListener iDataSlotLoadListener;
    DocumentReference doctorDoc;

    @BindView(R.id.recycler_data_slot)
    RecyclerView recycler_data_slot;
    @BindView(R.id.calendarView)
    HorizontalCalendarView calendarView;

    TextView txt_notification_badge;
    CollectionReference notificationCollection;
    CollectionReference currentAppointmentDateCollection;

    EventListener<QuerySnapshot> notificationEvent;
    EventListener<QuerySnapshot> appointmentEvent;

    ListenerRegistration notificationListener;
    ListenerRegistration appointmentRealtimeListener;

    INotificationListener iNotificationListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_home);
        ButterKnife.bind(this);

        initz();
        initView();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (actionBarDrawerToggle.onOptionsItemSelected(item))
            return true;

        if (item.getItemId() == R.id.action_new_notification)
        {
            startActivity(new Intent(DoctorHomeActivity.this, NotificationActivity.class));
            txt_notification_badge.setText("");
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void initView() {
        actionBarDrawerToggle = new ActionBarDrawerToggle(this, drawerLayout,
                R.string.close,
                R.string.open);

        drawerLayout.addDrawerListener(actionBarDrawerToggle);
        actionBarDrawerToggle.syncState();
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                if (item.getItemId() == R.id.menu_exit)
                    logOut();
                if (item.getItemId() == R.id.received_tests)
                    startActivity(new Intent(DoctorHomeActivity.this, ReceivedTestsResults.class));
                if (item.getItemId() == R.id.done_tests)
                    startActivity(new Intent(DoctorHomeActivity.this, DoneTests.class));
                return false;
            }
        });

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 0); //Add current date
        loadAvailableDataSlotOfDoctor(Common.currentDoctor.getDoctorId(), simpleDateFormat.format(date.getTime()));

        recycler_data_slot.setHasFixedSize(true);
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 3);
        recycler_data_slot.setLayoutManager(gridLayoutManager);
        recycler_data_slot.addItemDecoration(new SpacesItemDecoration(8));

        Calendar startDate = Calendar.getInstance();
        startDate.add(Calendar.DATE, 0);
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.DATE, 2);

        HorizontalCalendar horizontalCalendar = new HorizontalCalendar.Builder(this, R.id.calendarView)
                .range(startDate, endDate)
                .datesNumberOnScreen(1)
                .mode(HorizontalCalendar.Mode.DAYS)
                .defaultSelectedDate(startDate)
                .build();

        horizontalCalendar.setCalendarListener(new HorizontalCalendarListener() {
            @Override
            public void onDateSelected(Calendar date, int position) {
                if (Common.currentData.getTimeInMillis() != date.getTimeInMillis())
                {
                    Common.currentData = date; //won't load again if you select same day
                    loadAvailableDataSlotOfDoctor(Common.currentDoctor.getDoctorId(),simpleDateFormat.format(date.getTime()));
                }
            }
        });
    }

    private void logOut() {
        Paper.init(this);
        Paper.book().delete(Common.CLINIC_KEY);
        Paper.book().delete(Common.DOCTOR_KEY);
        Paper.book().delete(Common.CITY_KEY);
        Paper.book().delete(Common.LOG_KEY);

        new AlertDialog.Builder(this)
                .setMessage("Na pewno chcesz się wylogować?")
                .setCancelable(false)
                .setPositiveButton("Tak", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Intent intent = new Intent(DoctorHomeActivity.this, MainActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                        finish();

                    }
                }).setNegativeButton("Nie", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }).show();

    }

    private void loadAvailableDataSlotOfDoctor(String doctorId, String apptData) {
            //getting inf of doctor
            doctorDoc.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot documentSnapshot = task.getResult();
                        if (documentSnapshot.exists()) //if doctor is available
                        {
                            //getting inf about appointment
                            //if not created, return empty
                            CollectionReference date = FirebaseFirestore.getInstance()
                                    .collection("AllClinics")
                                    .document(Common.city)
                                    .collection("Clinics")
                                    .document(Common.currentClinic.getClinicId())
                                    .collection("Doctors")
                                    .document(Common.currentDoctor.getDoctorId())
                                    .collection(apptData);

                            date.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        QuerySnapshot querySnapshot = task.getResult();
                                        if (querySnapshot.isEmpty()) //if dont have any appointment
                                            iDataSlotLoadListener.onDataSlotLoadEmpty();
                                        else {

                                            List<AppointmentInformation> dataSlots = new ArrayList<>();

                                            for (QueryDocumentSnapshot document : task.getResult()) {
                                                dataSlots.add(document.toObject(AppointmentInformation.class));
                                            }
                                            iDataSlotLoadListener.onDataSlotLoadSuccess(dataSlots);
                                        }
                                    }
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    iDataSlotLoadListener.onDataSlotLoadFailed(e.getMessage());
                                }
                            });
                        }
                    }
                }
            });
    }

    private void initz() {
        iDataSlotLoadListener = this;
        iNotificationListener = this;
        initNotificationUpdate();
        initAppointmentRealtimeUpdate();
    }

    private void initAppointmentRealtimeUpdate() {
        doctorDoc = FirebaseFirestore.getInstance()
                .collection("AllClinics")
                .document(Common.city)
                .collection("Clinics")
                .document(Common.currentClinic.getClinicId())
                .collection("Doctors")
                .document(Common.currentDoctor.getDoctorId());

        Calendar date = Calendar.getInstance();
        date.add(Calendar.DATE, 0);
        appointmentEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                loadAvailableDataSlotOfDoctor(Common.currentDoctor.getDoctorId(),
                        Common.simpleDateFormat.format(date.getTime()));
            }
        };

        currentAppointmentDateCollection = doctorDoc.collection(simpleDateFormat.format(date.getTime()));

        appointmentRealtimeListener = currentAppointmentDateCollection.addSnapshotListener(appointmentEvent);

    }

    private void initNotificationUpdate() {
        notificationCollection = FirebaseFirestore.getInstance()
                .collection("AllClinics")
                .document(Common.city)
                .collection("Clinics")
                .document(Common.currentClinic.getClinicId())
                .collection("Doctors")
                .document(Common.currentDoctor.getDoctorId())//getDoctorId
                .collection("Notifications");

        notificationEvent = new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (value.size() > 0)
                    loadNotification();
            }
        };

        notificationListener = notificationCollection.whereEqualTo("read", false)
                .addSnapshotListener(notificationEvent);

    }

    @Override
    public void onDataSlotLoadSuccess(List<AppointmentInformation> dataSlot) {
        DataSlotAdapter adapter = new DataSlotAdapter(this, dataSlot);
        recycler_data_slot.setAdapter(adapter);
    }

    @Override
    public void onDataSlotLoadFailed(String message) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDataSlotLoadEmpty() {
        DataSlotAdapter adapter = new DataSlotAdapter(this);
        recycler_data_slot.setAdapter(adapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.doctor_home_menu, menu);
        MenuItem menuItem = menu.findItem(R.id.action_new_notification);

        txt_notification_badge = (TextView) menuItem.getActionView()
                .findViewById(R.id.notification_badge);

        loadNotification();

        menuItem.getActionView().setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onOptionsItemSelected(menuItem);
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    private void loadNotification() {
        notificationCollection.whereEqualTo("read", false)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(DoctorHomeActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    iNotificationListener.onNotificationSuccess(task.getResult().size());
                }
            }
        });
    }

    @Override
    public void onNotificationSuccess(int count) {
        if (count == 0)
            txt_notification_badge.setVisibility(View.INVISIBLE);
        else
        {
            txt_notification_badge.setVisibility(View.VISIBLE);
            if (count <= 9)
                txt_notification_badge.setText(String.valueOf(count));
            else
                txt_notification_badge.setText("9+");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        initAppointmentRealtimeUpdate();
        initNotificationUpdate();
    }

    @Override
    protected void onStart() {
        if (notificationListener != null)
            notificationListener.remove();
        if (appointmentRealtimeListener != null)
            appointmentRealtimeListener.remove();
        super.onStart();
    }

    @Override
    protected void onDestroy() {
        if (notificationListener != null)
            notificationListener.remove();
        if (appointmentRealtimeListener != null)
            appointmentRealtimeListener.remove();
        super.onDestroy();
    }
}