package com.vincent.hoangnguyen.classmanagement.model;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.vincent.hoangnguyen.classmanagement.R;

public class InformationAdapter extends FirestoreRecyclerAdapter<Student,InformationAdapter.InformationHolder> {
    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options
     */
    Context context;
    public InformationAdapter(@NonNull FirestoreRecyclerOptions<Student> options, Context context) {
        super(options);
        this.context =context;
    }

    @Override
    protected void onBindViewHolder(@NonNull InformationHolder holder, int position, @NonNull Student student) {
    holder.nameTextview.setText(student.getName());
    holder.mssvTextview.setText(student.getId());

    holder.itemView.setOnClickListener(view -> {
        Intent intent = new Intent(context, DetailInformationActivity.class);
        intent.putExtra("Name",student.getName());
        intent.putExtra("Id",student.getId());
        intent.putExtra("PhoneNumber",student.getPhoneNumber());
        String docID = this.getSnapshots().getSnapshot(position).getId();
        intent.putExtra("docID", docID);
        context.startActivity(intent);
    });
    }

    @NonNull
    @Override
    public InformationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycle_view_student_information,parent,false);
        return new InformationHolder(view);
    }

    static class InformationHolder extends RecyclerView.ViewHolder{
        TextView nameTextview,mssvTextview;
        public InformationHolder(@NonNull View itemView) {
            super(itemView);
            nameTextview = itemView.findViewById(R.id.recycle_name);
            mssvTextview = itemView.findViewById(R.id.recycle_mssv);

        }
    }
}
