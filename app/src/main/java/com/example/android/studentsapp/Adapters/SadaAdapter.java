package com.example.android.studentsapp.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.android.studentsapp.BranchActivity;
import com.example.android.studentsapp.HomeActivity;
import com.example.android.studentsapp.IndianActivities.IndiaPrevYearPapers;
import com.example.android.studentsapp.MainActivity;
import com.example.android.studentsapp.MeritPapers;
import com.example.android.studentsapp.Model.ExamDates;
import com.example.android.studentsapp.IndianActivities.NotesActivity;
import com.example.android.studentsapp.R;
import com.example.android.studentsapp.IndianActivities.SyllabusActivity;
import com.example.android.studentsapp.databinding.ChooseExamDialogBinding;
import com.example.android.studentsapp.databinding.ChooseGuestDialogBinding;
import com.example.android.studentsapp.databinding.ChooseThemeDialogBinding;
import com.example.android.studentsapp.databinding.CustomViewBinding;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SadaAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>{

    private Context context;
    public List<ExamDates> allDates;
    private Drawable mColor;
    private DatabaseReference myRef;
    RecyclerView recyclerView;
    ArrayList<String> options = new ArrayList<>();
    String getYear;
    boolean isGuest;
    boolean connected = false;

    public SadaAdapter(Context context, ArrayList<ExamDates> allDates, boolean guest) {
        this.context = context;
        this.allDates = new ArrayList<>(allDates);
        this.isGuest = guest;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        CustomViewBinding binding = CustomViewBinding.inflate(LayoutInflater.from(context),parent,
                false);

        return new CustomBakchodi(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        final ExamDates examDates = allDates.get(position);

        CustomViewBinding b = ((CustomBakchodi)holder).binding;
        Glide.with(context)
                .load(examDates.url)
                .into(b.logoImage);
        b.allInExams.setText(""+examDates.examName+"\n\nNext Exam Date - \n\n"+examDates.examDate);

        b.cardView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                options.removeAll(options);
                    mColor = ContextCompat.getDrawable(context,
                            R.drawable.round_first);

                    b.cardView.setBackground(mColor);
                ChooseExamDialogBinding binding = ChooseExamDialogBinding.inflate(LayoutInflater.from(context));
                binding.chooseExam.setText(""+examDates.examName);
               // Toast.makeText(context, binding.chooseExam.getText().toString(), Toast.LENGTH_SHORT).show();
                new MaterialAlertDialogBuilder(context,R.style.CustomDialogTheme)
                        .setView(binding.getRoot())
                        .setOnCancelListener(new DialogInterface.OnCancelListener() {
                            @Override
                            public void onCancel(DialogInterface dialog) {
                                Drawable mColorW = ContextCompat.getDrawable(context,
                                        R.drawable.round_second);
                                b.cardView.setBackground(mColorW);
                            }
                        })
                        .show();
                binding.syllabus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        checkingConnectivity();
                        if(connected) {
                            Intent intent = new Intent(context, SyllabusActivity.class);
                            intent.putExtra("examName", examDates.examName);
                            context.startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                binding.notes.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isGuest == false) {
                            if (examDates.examName.equals("SBI PO")) {
                                binding.group.setVisibility(View.GONE);
                                binding.group4.setVisibility(View.VISIBLE);
                                binding.prelims.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {

                                        Intent intent = new Intent(context, NotesActivity.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("subExamName", "prelims");
                                        context.startActivity(intent);
                                    }
                                });
                                binding.mainsPolitics.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        Intent intent = new Intent(context, NotesActivity.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("subExamName", "mains");
                                        context.startActivity(intent);
                                    }
                                });
                            } else {
                                Intent intent = new Intent(context, NotesActivity.class);
                                intent.putExtra("examName", examDates.examName);
                                intent.putExtra("subExamName", "prelims");
                                context.startActivity(intent);
                            }
                        }
                        else{
                            DialogForGuest();
                        }

                    }
                });

                binding.allInPrevPapers.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(isGuest == false) {
                            SelectingBigBranches(examDates, binding);
                        }
                        else{
                            DialogForGuest();
                        }
                    }
                });
            }
        });
    }

    private void SelectingBigBranches(ExamDates examDates, ChooseExamDialogBinding binding) {
        if (examDates.examName.equals("SBI PO")) {
            binding.group.setVisibility(View.GONE);
            binding.group4.setVisibility(View.VISIBLE);
            binding.prelims.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.group4.setVisibility(View.GONE);
                    binding.group2.setVisibility(View.VISIBLE);
                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                    myRef.child(examDates.examName).child("prelims").child("prevPaper")
                            .addValueEventListener
                                    (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            contentOnData(snapshot);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                    ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            getYear = (String) parent.getItemAtPosition(position);
                            binding.autoCompleteYear.setText(getYear, false);

                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    options.removeAll(options);
                                    checkingConnectivity();
                                    if(connected) {
                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("year", getYear);
                                        intent.putExtra("subExamName", "prelims");
                                        context.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });

            binding.mainsPolitics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.group4.setVisibility(View.GONE);
                    binding.group2.setVisibility(View.VISIBLE);
                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                    myRef.child(examDates.examName).child("mains").child("prevPaper")
                            .addValueEventListener
                                    (new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            contentOnData(snapshot);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                    ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            getYear = (String) parent.getItemAtPosition(position);
                            binding.autoCompleteYear.setText(getYear, false);

                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    options.removeAll(options);
                                    checkingConnectivity();
                                    if(connected) {
                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("year", getYear);
                                        intent.putExtra("subExamName", "mains");
                                        context.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }


    else if (examDates.examName.equals("JEE")) {
        binding.group.setVisibility(View.GONE);
        binding.group3.setVisibility(View.VISIBLE);
        mainsBranch(examDates, binding.mains, "mains", binding);
        mainsBranch(examDates, binding.advance, "advance", (ChooseExamDialogBinding) binding);
    }

    else if(examDates.examName.equals("UPSC")){
            binding.group.setVisibility(View.GONE);
            binding.group4.setVisibility(View.VISIBLE);

            binding.prelims.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.group4.setVisibility(View.GONE);
                    binding.group2.setVisibility(View.VISIBLE);

                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                    myRef.child(examDates.examName).child("prelims").child("prevPaper").
                            addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                    contentOnData(snapshot);
                                }

                                @Override
                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                }
                            });
                    ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                            getYear = (String) parent.getItemAtPosition(position);
                            binding.autoCompleteYear.setText(getYear, false);

                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    options.removeAll(options);
                                    checkingConnectivity();
                                    if (connected) {
                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("year", getYear);
                                        intent.putExtra("subExamName", "prelims");
                                        context.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });

            binding.mainsPolitics.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    binding.group4.setVisibility(View.GONE);
                    binding.group5.setVisibility(View.VISIBLE);
                    binding.qualifyingPapers.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.group5.setVisibility(View.GONE);
                            binding.group2.setVisibility(View.VISIBLE);

                            myRef = FirebaseDatabase.getInstance().getReference("PDF");
                            myRef.child(examDates.examName).child("mains").child("qualifyingPapers").child("prevPaper").
                                    addValueEventListener(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                            contentOnData(snapshot);
                                        }

                                        @Override
                                        public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                        }
                                    });
                            ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                            binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                    getYear = (String) parent.getItemAtPosition(position);
                                    binding.autoCompleteYear.setText(getYear, false);

                                    binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View v) {
                                            options.removeAll(options);
                                            checkingConnectivity();
                                            if(connected) {
                                                Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                intent.putExtra("examName", examDates.examName);
                                                intent.putExtra("year", getYear);
                                                intent.putExtra("subExamName", "mains");
                                                intent.putExtra("subsubExamName", "qualifyingPapers");
                                                context.startActivity(intent);
                                            }
                                            else{
                                                Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                                }
                            });
                        }
                    });

                    binding.papersMerit.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            binding.group5.setVisibility(View.GONE);
                            binding.group6.setVisibility(View.VISIBLE);

                            binding.essay.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.group6.setVisibility(View.GONE);
                                    binding.group2.setVisibility(View.VISIBLE);

                                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                                    myRef.child(examDates.examName).child("mains").child("passMerit").child("essay").child("prevPaper").
                                            addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    contentOnData(snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                    ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                            getYear = (String) parent.getItemAtPosition(position);
                                            binding.autoCompleteYear.setText(getYear, false);

                                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    options.removeAll(options);
                                                    checkingConnectivity();
                                                    if(connected) {
                                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                        intent.putExtra("examName", examDates.examName);
                                                        intent.putExtra("year", getYear);
                                                        intent.putExtra("subExamName", "mains");
                                                        intent.putExtra("subsubExamName", "passMerit");
                                                        intent.putExtra("subsubsubExam", "essay");
                                                        context.startActivity(intent);
                                                    }
                                                    else{
                                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });

                            binding.generalStudiesI.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.group6.setVisibility(View.GONE);
                                    binding.group2.setVisibility(View.VISIBLE);

                                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                                    myRef.child(examDates.examName).child("mains").child("passMerit").child("generalStudiesI").child("prevPaper")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    contentOnData(snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                    ultimateArrayAdapter(binding);

                                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                            getYear = (String) parent.getItemAtPosition(position);
                                            binding.autoCompleteYear.setText(getYear, false);

                                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    options.removeAll(options);
                                                    checkingConnectivity();
                                                    if(connected) {
                                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                        intent.putExtra("examName", examDates.examName);
                                                        intent.putExtra("year", getYear);
                                                        intent.putExtra("subExamName", "mains");
                                                        intent.putExtra("subsubExamName", "passMerit");
                                                        intent.putExtra("subsubsubExam", "generalStudiesI");
                                                        context.startActivity(intent);
                                                    }
                                                    else{
                                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });
                                }
                            });
                            binding.generalStudiesIi.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.group6.setVisibility(View.GONE);
                                    binding.group2.setVisibility(View.VISIBLE);

                                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                                    myRef.child(examDates.examName).child("mains").child("passMerit").
                                            child("generalStudiesII").child("prevPaper")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    contentOnData(snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                    ultimateArrayAdapter(binding);
                                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                            getYear = (String) parent.getItemAtPosition(position);
                                            binding.autoCompleteYear.setText(getYear, false);

                                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    options.removeAll(options);
                                                    checkingConnectivity();
                                                    if(connected) {
                                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                        intent.putExtra("examName", examDates.examName);
                                                        intent.putExtra("year", getYear);
                                                        intent.putExtra("subExamName", "mains");
                                                        intent.putExtra("subsubExamName", "passMerit");
                                                        intent.putExtra("subsubsubExam", "generalStudiesII");
                                                        context.startActivity(intent);
                                                    }
                                                    else {
                                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                            binding.generalStudiesIii.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.group6.setVisibility(View.GONE);
                                    binding.group2.setVisibility(View.VISIBLE);

                                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                                    myRef.child(examDates.examName).child("mains").child("passMerit").
                                            child("generalStudiesIII").child("prevPaper")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    contentOnData(snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                    ultimateArrayAdapter(binding);
                                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                            getYear = (String) parent.getItemAtPosition(position);
                                            binding.autoCompleteYear.setText(getYear, false);

                                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    options.removeAll(options);
                                                    checkingConnectivity();
                                                    if(connected) {
                                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                        intent.putExtra("examName", examDates.examName);
                                                        intent.putExtra("year", getYear);
                                                        intent.putExtra("subExamName", "mains");
                                                        intent.putExtra("subsubExamName", "passMerit");
                                                        intent.putExtra("subsubsubExam", "generalStudiesIII");
                                                        context.startActivity(intent);
                                                    }
                                                    else{
                                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                            binding.generalStudiesIv.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    binding.group6.setVisibility(View.GONE);
                                    binding.group2.setVisibility(View.VISIBLE);

                                    myRef = FirebaseDatabase.getInstance().getReference("PDF");
                                    myRef.child(examDates.examName).child("mains").child("passMerit").
                                            child("generalStudiesIV").child("prevPaper")
                                            .addValueEventListener(new ValueEventListener() {
                                                @Override
                                                public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                                    contentOnData(snapshot);
                                                }

                                                @Override
                                                public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                                }
                                            });
                                    ultimateArrayAdapter(binding);
                                    binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                                        public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                                            getYear = (String) parent.getItemAtPosition(position);
                                            binding.autoCompleteYear.setText(getYear, false);

                                            binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                                                @Override
                                                public void onClick(View v) {
                                                    options.removeAll(options);
                                                    checkingConnectivity();
                                                    if(connected) {
                                                        Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                                        intent.putExtra("examName", examDates.examName);
                                                        intent.putExtra("year", getYear);
                                                        intent.putExtra("subExamName", "mains");
                                                        intent.putExtra("subsubExamName", "passMerit");
                                                        intent.putExtra("subsubsubExam", "generalStudiesIV");
                                                        context.startActivity(intent);
                                                    }
                                                    else{
                                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                                    }
                                                }
                                            });
                                        }
                                    });

                                }
                            });
                            binding.optional.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    options.removeAll(options);
                                    checkingConnectivity();
                                    if(connected) {
                                        Intent intent = new Intent(context, MeritPapers.class);
                                        intent.putExtra("examName", examDates.examName);
                                        intent.putExtra("year", getYear);
                                        intent.putExtra("subExamName", "mains");
                                        intent.putExtra("subsubExamName", "passMerit");
                                        intent.putExtra("subsubsubExam", "optional");
                                        context.startActivity(intent);
                                    }
                                    else{
                                        Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                    }
                                }
                            });
                        }
                    });
                }
            });
        }

    else {
        binding.group.setVisibility(View.GONE);
        binding.group2.setVisibility(View.VISIBLE);

        settingUpPreviousYearShit(examDates, binding);

        settingUpAutocompleteShit(binding, examDates);

    }
    }

    private void contentOnData(@NonNull @NotNull DataSnapshot snapshot) {
        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
            String value = dataSnapshot.getKey().toString();
            options.add(value);
        }
    }

    private void ultimateArrayAdapter(ChooseExamDialogBinding binding) {
        ArrayAdapter arrayAdapter = new ArrayAdapter(context, R.layout.drop_down_view, options);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        binding.autoCompleteYear.setAdapter(arrayAdapter);
    }

    private void DialogForGuest() {
        ChooseGuestDialogBinding binding = ChooseGuestDialogBinding.inflate(LayoutInflater.from(context));

        new MaterialAlertDialogBuilder(context,R.style.CustomDialogTheme2)
                .setView(binding.getRoot())
                .setCancelable(false).setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).show();
    }

    private void mainsBranch(ExamDates examDates, TextView p, String mains, ChooseExamDialogBinding binding) {
        p.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                binding.group3.setVisibility(View.GONE);
                binding.group2.setVisibility(View.VISIBLE);
                myRef = FirebaseDatabase.getInstance().getReference("PDF");
                myRef.child(examDates.examName).child(mains).child("prevPaper")
                        .addValueEventListener
                                (new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                                        contentOnData(snapshot);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                                    }
                                });
                ultimateArrayAdapter((ChooseExamDialogBinding) binding);

                binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                        getYear = (String) parent.getItemAtPosition(position);
                        binding.autoCompleteYear.setText(getYear, false);

                        binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                options.removeAll(options);
                                checkingConnectivity();
                                if(connected) {
                                    Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                                    intent.putExtra("examName", examDates.examName);
                                    intent.putExtra("year", getYear);
                                    intent.putExtra("subExamName", mains);
                                    context.startActivity(intent);
                                }
                                else{
                                    Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
                });
            }
        });
    }

    private void settingUpAutocompleteShit(ChooseExamDialogBinding binding, ExamDates examDates) {
        binding.autoCompleteYear.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view, int position, long rowId) {
                getYear = (String) parent.getItemAtPosition(position);
                binding.autoCompleteYear.setText(getYear, false);

                binding.proceedToPrev.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        options.removeAll(options);
                        checkingConnectivity();
                        if(connected) {
                            Intent intent = new Intent(context, IndiaPrevYearPapers.class);
                            intent.putExtra("examName", examDates.examName);
                            intent.putExtra("year", getYear);
                            context.startActivity(intent);
                        }
                        else{
                            Toast.makeText(context, "NO CONNECTION !", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void settingUpPreviousYearShit(ExamDates examDates, ChooseExamDialogBinding binding) {
        myRef = FirebaseDatabase.getInstance().getReference("PDF");
        myRef.child(examDates.examName).child("prevPaper").addValueEventListener
                (new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {
                        contentOnData(snapshot);
                    }

                    @Override
                    public void onCancelled(@NonNull @NotNull DatabaseError error) {

                    }
                });
        ultimateArrayAdapter((ChooseExamDialogBinding) binding);
    }

    @Override
    public int getItemCount() {
        return allDates.size();
    }


    private static class CustomBakchodi extends RecyclerView.ViewHolder {
        CustomViewBinding binding;
        public CustomBakchodi(@NonNull CustomViewBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
    private void checkingConnectivity() {
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        }
        else {
            connected = false;
        }
    }
}
