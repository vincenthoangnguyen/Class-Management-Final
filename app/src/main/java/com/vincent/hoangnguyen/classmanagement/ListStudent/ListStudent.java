package com.vincent.hoangnguyen.classmanagement.ListStudent;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.Query;
import com.vincent.hoangnguyen.classmanagement.model.InformationAdapter;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.model.Student;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

public class ListStudent extends AppCompatActivity {
    RecyclerView recyclerView;
    InformationAdapter informationAdapter;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_student_activity);
        recyclerView = findViewById(R.id.recycle_view);
        setUpRecycleView();
    }



    void setUpRecycleView(){
        Query query = Utility.getCollectionReferenceForStudent();
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
}