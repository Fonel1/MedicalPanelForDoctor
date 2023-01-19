package com.test.doctorapplication.Adapter;

import android.content.Context;
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
import com.test.doctorapplication.Common.Common;
import com.test.doctorapplication.DoneAppointmentActivity;
import com.test.doctorapplication.Interface.IRecyclerItemSelectedListener;
import com.test.doctorapplication.Model.AppointmentInformation;
import com.test.doctorapplication.R;

import java.util.ArrayList;
import java.util.List;

public class DataSlotAdapter extends RecyclerView.Adapter<DataSlotAdapter.MyViewHolder> {
    Context context;
    List<AppointmentInformation> dataSlotList;
    List<CardView> cardViewList;


    public DataSlotAdapter(Context context) {
        this.context = context;
        this.dataSlotList = new ArrayList<>();
        cardViewList = new ArrayList<>();
    }

    public DataSlotAdapter(Context context, List<AppointmentInformation> dataSlotList) {
        this.context = context;
        this.dataSlotList = dataSlotList;
        cardViewList = new ArrayList<>();
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(context)
                .inflate(R.layout.layout_data_slot, parent, false);
        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, final int i) {
        holder.txt_data_slot.setText(new StringBuilder(Common.convertDataSlotToString(i)).toString());
        if (dataSlotList.size() == 0) //if all positions is available - show list
        {
            holder.card_data_slot.setCardBackgroundColor(context.getColor(android.R.color.white));
            holder.txt_data_slot_description.setText(new StringBuilder("Wolny\ntermin"));
            holder.txt_data_slot_description.setTextColor(context.getColor(android.R.color.black));
            holder.txt_data_slot.setTextColor(context.getColor(android.R.color.black));



                holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                    @Override
                    public void onItemSelectedListener(View view, int pos) {

                }
                });

            } else //if position is already full
                {
                for (AppointmentInformation slotValue : dataSlotList) {
                    //Loop all dates from server and set different color
                    int slot = Integer.parseInt(slotValue.getSlot().toString());
                    if (slot == i) //If slot == position
                    {
                        if (!slotValue.isDone()) {

                        holder.card_data_slot.setTag(Common.DISABLE_TAG);
                        holder.card_data_slot.setCardBackgroundColor(context.getColor(android.R.color.darker_gray));
                        holder.txt_data_slot_description.setText("Zajęte");
                        holder.txt_data_slot_description.setTextColor(context.getColor(android.R.color.white));
                        holder.txt_data_slot.setTextColor(context.getColor(android.R.color.white));

                        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelectedListener(View view, int pos) {
                                FirebaseFirestore.getInstance()
                                        .collection("AllClinics")
                                        .document(Common.city)
                                        .collection("Clinics")
                                        .document(Common.currentClinic.getClinicId())
                                        .collection("Doctors")
                                        .document(Common.currentDoctor.getDoctorId())
                                        .collection(Common.simpleDateFormat.format(Common.currentData.getTime()))
                                        .document(slotValue.getSlot().toString())
                                        .get()
                                        .addOnFailureListener(new OnFailureListener() {
                                            @Override
                                            public void onFailure(@NonNull Exception e) {
                                                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                        if (task.isSuccessful())
                                        {
                                            if (task.getResult().exists())
                                            {
                                                Common.currentAppointmentInfo = task.getResult().toObject(AppointmentInformation.class);
                                                Common.currentAppointmentInfo.setApptId(task.getResult().getId());
                                                context.startActivity(new Intent(context, DoneAppointmentActivity.class));
                                            }
                                        }
                                    }
                                });

                            }
                        });
                    }
                    else
                    {
                        holder.card_data_slot.setTag(Common.DISABLE_TAG);
                        holder.card_data_slot.setCardBackgroundColor(context.getColor(android.R.color.holo_orange_dark));

                        holder.txt_data_slot_description.setText("Gotowe");
                        holder.txt_data_slot_description.setTextColor(context.getColor(android.R.color.white));
                        holder.txt_data_slot.setTextColor(context.getColor(android.R.color.white));

                        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelectedListener(View view, int pos) {

                            }
                        });
                    }
                }
                else
                {
                    if (holder.getiRecyclerItemSelectedListener() == null)
                    {
                        holder.setiRecyclerItemSelectedListener(new IRecyclerItemSelectedListener() {
                            @Override
                            public void onItemSelectedListener(View view, int pos) {

                            }
                        });
                    }
                }
            }
        }

        //add all card to list
        if (!cardViewList.contains(holder.card_data_slot)) {
            cardViewList.add(holder.card_data_slot);
        }
    }


    @Override
    public int getItemCount() {
        return Common.DATA_SLOT_TOTAL;
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        TextView txt_data_slot, txt_data_slot_description;
        CardView card_data_slot;

        IRecyclerItemSelectedListener iRecyclerItemSelectedListener;

        public IRecyclerItemSelectedListener getiRecyclerItemSelectedListener() {
            return iRecyclerItemSelectedListener;
        }

        public void setiRecyclerItemSelectedListener(IRecyclerItemSelectedListener iRecyclerItemSelectedListener) {
            this.iRecyclerItemSelectedListener = iRecyclerItemSelectedListener;
        }

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            card_data_slot = (CardView) itemView.findViewById(R.id.card_data_slot);
            txt_data_slot = (TextView) itemView.findViewById(R.id.txt_data_slot);
            txt_data_slot_description = (TextView) itemView.findViewById(R.id.txt_data_slot_description);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            iRecyclerItemSelectedListener.onItemSelectedListener(view, getAdapterPosition());
        }
    }
}
