package com.test.doctorapplication.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.doctorapplication.ClinicListActivity;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Common.LoginDialog;
import com.test.doctorapplication.DoctorHomeActivity;
import com.test.doctorapplication.Interface.IDialogClickListener;
import com.test.doctorapplication.Interface.IGetDoctorListener;
import com.test.doctorapplication.Interface.IRecyclerItemSelectedListener;
import com.test.doctorapplication.Interface.IRememberUserListener;
import com.test.doctorapplication.Model.Clinic;
import com.test.doctorapplication.Model.Doctor;
import com.test.doctorapplication.R;

import java.util.ArrayList;
import java.util.List;

import dmax.dialog.SpotsDialog;

public class ClinicAdapter extends RecyclerView.Adapter<ClinicAdapter.MyViewHolder> implements IDialogClickListener {

    Context context;
    List<Clinic> clinicList;
    List<CardView> cardViewList;
    LocalBroadcastManager localBroadcastManager;

    IRememberUserListener iRememberUserListener;
    IGetDoctorListener iGetDoctorListener;

    public ClinicAdapter(Context context, List<Clinic> clinicList, IRememberUserListener iRememberUserListener, IGetDoctorListener iGetDoctorListener) {
        this.context = context;
        this.clinicList = clinicList;
        cardViewList = new ArrayList<>();
        localBroadcastManager= LocalBroadcastManager.getInstance(context); //by this we give information to app that we already selected clinic
        this.iGetDoctorListener = iGetDoctorListener;
        this.iRememberUserListener = iRememberUserListener;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_clinic, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        holder.txt_clinic_name.setText(clinicList.get(position).getName());
        holder.txt_clinic_address.setText(clinicList.get(position).getAddress());

        if (!cardViewList.contains(holder.card_clinic))
            cardViewList.add(holder.card_clinic);

        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
            @Override
            public void onItemSelectedListener(View view, int pos) {

                Common.currentClinic = clinicList.get(pos);
                loginDialog();

            }
        });
    }

    private void loginDialog() {
        LoginDialog.getInstance()
                .sLoginDialog("Login",
                        "LOGIN",
                        "ANULUJ",
                        context,
                        this);
    }


    @Override
    public int getItemCount() {
        return clinicList.size();
    }

    @Override
    public void onClickLogin(DialogInterface dialogInterface, String userName, String password) {
        AlertDialog alertDialog = new SpotsDialog.Builder().setCancelable(false)
                .setContext(context).build();

        alertDialog.show();

        FirebaseFirestore.getInstance()
                .collection("AllClinics")
                .document(Common.city)
                .collection("Clinics")
                .document(Common.currentClinic.getClinicId())
                .collection("Doctors")
                .whereEqualTo("username", userName)
                .whereEqualTo("password", password)
                .limit(1)
                .get()
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }).addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful())
                {
                    if (task.getResult().size() > 0)
                    {
                        dialogInterface.dismiss();
                        alertDialog.dismiss();

                        iRememberUserListener.onRememberUserSuccess(userName);

                        Doctor doctor = new Doctor();
                        for (DocumentSnapshot doctorSnapShot:task.getResult())
                        {
                            doctor = doctorSnapShot.toObject(Doctor.class);
                            doctor.setDoctorId(doctorSnapShot.getId());
                        }

                        iGetDoctorListener.onGetDoctorSuccess(doctor);

                        Intent doctorHome = new Intent(context, DoctorHomeActivity.class);
                        doctorHome.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        doctorHome.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                        context.startActivity(doctorHome);
                    }
                    else
                    {
                        alertDialog.dismiss();
                        Toast.makeText(context, "Złe hasło/nazwa użytkownika lub przychodnia", Toast.LENGTH_LONG).show();
                    }
                }
            }
        });
    }

    @Override
    public void onClickCancel(DialogInterface dialogInterface) {
        dialogInterface.dismiss();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_clinic_name, txt_clinic_address;
        CardView card_clinic;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            card_clinic = (CardView)itemView.findViewById(R.id.card_clinic);
            txt_clinic_address = (TextView) itemView.findViewById(R.id.txt_clinic_address);
            txt_clinic_name = (TextView) itemView.findViewById(R.id.txt_clinic_name);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}

