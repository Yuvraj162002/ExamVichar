package com.example.android.studentsapp.Adapters;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.studentsapp.Model.StatesName;
import com.example.android.studentsapp.StateActivities.StateExams;
import com.example.android.studentsapp.databinding.CustomStatesBinding;
import com.google.firebase.database.DatabaseReference;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class StatesAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    public List<StatesName> allStates;
    private boolean isGuest;
    private Drawable mColor;
    private DatabaseReference myRef;

    public StatesAdapter(Context context, ArrayList<StatesName> allStates,boolean isGuest) {
        this.context = context;
        this.allStates = new ArrayList<>(allStates);
        this.isGuest = isGuest;
    }

    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        CustomStatesBinding binding = CustomStatesBinding.inflate(LayoutInflater.from(context),parent,
                false);

        return new CustomBakchodiPhirse(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position) {
        final StatesName statesName = allStates.get(position);

        CustomStatesBinding b = ((CustomBakchodiPhirse)holder).binding;
       Glide.with(context)
                .load(statesName.url)
                .into(b.stateLogo);


        b.nameOfTheState.setText(""+statesName.stateName);

        b.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, StateExams.class);
                intent.putExtra("stateName",statesName.stateName);
                intent.putExtra("isGuest", isGuest);
                context.startActivity(intent);
            }
        });

    }

    @Override
    public int getItemCount() {
        return allStates.size();
    }


    private static class CustomBakchodiPhirse extends RecyclerView.ViewHolder {
        CustomStatesBinding binding;

        public CustomBakchodiPhirse(@NonNull CustomStatesBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
