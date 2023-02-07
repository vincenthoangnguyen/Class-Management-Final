package com.vincent.hoangnguyen.classmanagement.ListStudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710.Class_ET4710_Admin;
import com.vincent.hoangnguyen.classmanagement.controller.UI.MainActivity;
import com.vincent.hoangnguyen.classmanagement.model.InformationAdapter;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.model.Student;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class ListStudent extends AppCompatActivity {
    RecyclerView recyclerView;
    InformationAdapter informationAdapter;
    TextView countStudent_tv,countStudent_Late;
    FirebaseFirestore db;
    Timer timerRefresh;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_student_activity);
        recyclerView = findViewById(R.id.recycle_view);
        countStudent_tv = findViewById(R.id.count_student);
        countStudent_Late = findViewById(R.id.count_student_late);
         db = FirebaseFirestore.getInstance();
        timerRefresh = new Timer();
        timerRefresh.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                getNumberOfStudent();
                try {
                    getNumberStudentLate();
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        },0,500);

        setUpRecycleView();
    }

    void setUpRecycleView(){
        Query query = Utility.getCollectionReferenceForStudent().orderBy("timestamp", Query.Direction.ASCENDING);
        FirestoreRecyclerOptions<Student> options = new FirestoreRecyclerOptions.Builder<Student>()
                .setQuery(query,Student.class).build();
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        informationAdapter = new InformationAdapter(options, this);
        recyclerView.setAdapter(informationAdapter);


    }
    @Override
    protected void onStart() {
        super.onStart();
        informationAdapter.startListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        informationAdapter.notifyDataSetChanged();
    }

    @Override
    protected void onStop() {
        super.onStop();
        informationAdapter.stopListening();
    }
    private void getNumberOfStudent(){
        db.collection("ET4710_information").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int count = task.getResult().size();
                    countStudent_tv.setText(String.valueOf(count));
                } else {
                    Utility.showToast(ListStudent.this,task.getException().toString());
                }
            }

        });
    }

    private void getNumberStudentLate() throws ParseException {
        String closingTimeStr = Class_ET4710_Admin.ClosingTime;
        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm");
        Date closingTime = sdf.parse(closingTimeStr);
        long closingTimeLong = closingTime.getTime();
        Date closingTimeDate = new Date(closingTimeLong);
        Timestamp closingTimestamp = new Timestamp(closingTimeDate);
        db.collection("ET4710_information").whereGreaterThan("timestamp",closingTimestamp)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            int countLate = task.getResult().size();
                            Log.d("ABC",String.valueOf(countLate));
                            countStudent_Late.setText(String.valueOf(countLate));
                        }
                        else {
                            Utility.showToast(ListStudent.this,task.getException().toString());
                        }
                    }
                });
    }
}