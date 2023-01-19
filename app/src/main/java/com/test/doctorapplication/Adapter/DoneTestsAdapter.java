package com.test.doctorapplication.Adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.test.doctorapplication.Interface.RecyclerViewInterface;
import com.test.doctorapplication.Model.SentTestInformation;
import com.test.doctorapplication.R;
import com.test.doctorapplication.ShowTestSummary;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public class DoneTestsAdapter extends RecyclerView.Adapter<DoneTestsAdapter.MyViewHolder> {
    private final RecyclerViewInterface recyclerViewInterface;

    Context context;
    List<SentTestInformation> infoTestList;

    public DoneTestsAdapter(Context context, List<SentTestInformation> infoTestList,
                            RecyclerViewInterface recyclerViewInterface) {
        this.context = context;
        this.infoTestList = infoTestList;
        this.recyclerViewInterface = recyclerViewInterface;
    }

    @NonNull
    @Override
    public DoneTestsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_done_test, parent, false);
        return new MyViewHolder(itemView, recyclerViewInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull DoneTestsAdapter.MyViewHolder holder, @SuppressLint("RecyclerView") int position) {
        holder.txt_data.setText(infoTestList.get(position).getTime());
        holder.txt_patient_email.setText(infoTestList.get(position).getPatientEmail());
        holder.txt_patient_name.setText(infoTestList.get(position).getPatientName());
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SentTestInformation sentTestInformation = infoTestList.get(position);
                Intent intent = new Intent(context, ShowTestSummary.class);
                intent.putExtra("summaryTxt", sentTestInformation.getSummaryTxt());
                intent.putExtra("photo", sentTestInformation.getPhoto());
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
        @BindView(R.id.txt_patient_name)
        TextView txt_patient_name;
        @BindView(R.id.txt_patient_email)
        TextView txt_patient_email;

        public MyViewHolder(@NonNull View itemView, RecyclerViewInterface recyclerViewInterface) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
