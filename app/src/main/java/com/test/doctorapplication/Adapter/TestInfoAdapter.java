package com.test.doctorapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.Model.SentTestInformation;
import com.test.doctorapplication.R;
import com.test.doctorapplication.TestInformationActivity;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class TestInfoAdapter extends RecyclerView.Adapter<TestInfoAdapter.MyViewHolder> {

    Context context;
    List<SentTestInformation> infoTestList;

    public TestInfoAdapter(Context context, List<SentTestInformation> infoTestList) {
        this.context = context;
        this.infoTestList = infoTestList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_received_test, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_data.setText(infoTestList.get(position).getTime());
        holder.txt_patient_email.setText(infoTestList.get(position).getPatientEmail());
        holder.txt_test_name.setText(infoTestList.get(position).getTestName());
        holder.txt_patient_name.setText(infoTestList.get(position).getPatientName());
        holder.btn_summary.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SentTestInformation testInformation = infoTestList.get(position);

                Intent intent = new Intent(context, TestInformationActivity.class);

                intent.putExtra("time", testInformation.getTime());
                intent.putExtra("patientName", testInformation.getPatientName());
                intent.putExtra("testName", testInformation.getTestName());
                intent.putExtra("photo", testInformation.getPhoto());
                intent.putExtra("patientEmail", testInformation.getPatientEmail());

                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return infoTestList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;

        @BindView(R.id.txt_data)
        TextView txt_data;
        @BindView(R.id.txt_test_name)
        TextView txt_test_name;
        @BindView(R.id.txt_patient_name)
        TextView txt_patient_name;
        @BindView(R.id.txt_patient_email)
        TextView txt_patient_email;
        @BindView(R.id.btn_summary)
        Button btn_summary;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }

}
