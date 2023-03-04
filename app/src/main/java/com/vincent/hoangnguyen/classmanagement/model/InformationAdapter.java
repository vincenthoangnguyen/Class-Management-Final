package com.vincent.hoangnguyen.classmanagement.model;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.controller.UI.ET4710.Class_ET4710_Admin;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalTime;
import java.util.Date;

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


            // khi nhấn vào 1 recycleview thì chuyển đến màn detailInformation và put data sang
            holder.itemView.setOnClickListener(view -> {
                Intent intent = new Intent(context, DetailInformationActivity.class);
                // put dữ liệu sang màn detailInformation
                intent.putExtra("Name",student.getName());
                intent.putExtra("Id",student.getId());
                intent.putExtra("PhoneNumber",student.getPhoneNumber());
                intent.putExtra("Email",student.getEmail());
                intent.putExtra("midtermScore",student.getMidtermScore());
                intent.putExtra("finalScore",student.getFinalScore());
                String docID = this.getSnapshots().getSnapshot(position).getId();
                intent.putExtra("docID", docID);
                context.startActivity(intent);
            });

         String[] timeStudentArrive = {Utility.timeStampToString(student.getTimestamp())};
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        DocumentReference docRef = db.collection("CheckInTime").document(this.getSnapshots().getSnapshot(position).getId());
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            // get time checkIn
                            Timestamp timeCheckIn = documentSnapshot.getTimestamp("CheckInTime");
                            assert timeCheckIn != null;
                            timeStudentArrive[0] = Utility.timeStampToString(timeCheckIn);
                            holder.timeStamp.setText("CheckIn lúc:  " + timeStudentArrive[0]);
                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                                if (LocalTime.parse(timeStudentArrive[0]).isAfter(LocalTime.parse(Class_ET4710_Admin.ClosingTime))) { // nếu học sinh đi muộn
                                    holder.layout.setBackgroundColor(Color.RED);
                                }
                            }
                        } else {
                            // The document does not exist
                            Log.d("TAG", "The document does not exist");
                        }
                    }
                });

        // so sánh thời gian học sinh đến với thời gian thầy cài đặt đóng cửa nếu mà đến muộn thì background thành màu đỏ
        // Thời gian đóng cửa là biến static khai báo bên ClassET_4710_admin


    }
    @NonNull
    @Override
    public InformationHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.layout_recycle_view_student_information,parent,false);
        return new InformationHolder(view);
    }

    static class InformationHolder extends RecyclerView.ViewHolder{
        TextView nameTextview,mssvTextview,timeStamp;
        RelativeLayout layout;
        public InformationHolder(@NonNull View itemView) {
            super(itemView);
            nameTextview = itemView.findViewById(R.id.recycle_name);
            mssvTextview = itemView.findViewById(R.id.recycle_mssv);
            timeStamp = itemView.findViewById(R.id.timeStampLayout);
            layout = itemView.findViewById(R.id.fullLayout);
        }
    }
}