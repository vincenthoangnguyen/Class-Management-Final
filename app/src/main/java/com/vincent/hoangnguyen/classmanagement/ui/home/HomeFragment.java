package com.vincent.hoangnguyen.classmanagement.ui.home;

import static android.content.Context.MODE_PRIVATE;

import static androidx.fragment.app.FragmentManagerKt.commit;

import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.vincent.hoangnguyen.classmanagement.R;
import com.vincent.hoangnguyen.classmanagement.databinding.FragmentHomeBinding;
import com.vincent.hoangnguyen.classmanagement.model.Utility;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HomeFragment extends Fragment {
    Button checkInBtn;
    TextView timeCheckInTitle,timeCheckInTv;
    TextView weekTextView;

    private FragmentHomeBinding binding;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        //mapping
        timeCheckInTitle = root.findViewById(R.id.timeCheckInTitle);
        timeCheckInTv = root.findViewById(R.id.timeCheckInTv);
        checkInBtn = root.findViewById(R.id.checkInBtn);
        weekTextView = root.findViewById(R.id.week_number_tv);
        // get SharedPreferences để có thể lấy số tuần
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("mypref", MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        String WeekNumber = sharedPref.getString("WeekNumber", "");
        Log.d("Weeknumber",WeekNumber);
        weekTextView.setText(WeekNumber);
        //set click cho checkIn btn
        checkInBtn.setOnClickListener(view -> {
            checkInBtn = (Button) view;
            Map<String, Timestamp> map = new HashMap<>();
            Timestamp CheckInTime = Timestamp.now();
            map.put("CheckInTime",CheckInTime);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
            assert firebaseUser != null;
            firestore.collection("CheckInTime").document(firebaseUser.getUid()).set(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Utility.showToast(getContext(),"Check in thành công!");
                    checkInBtn.setBackgroundColor(Color.GREEN);
                    timeCheckInTv.setText(Utility.timeStampToStringHHDD(CheckInTime));
                    editor.putString(WeekNumber,Utility.timeStampToStringHHDD(CheckInTime));
                    editor.apply();
                    // Khởi động lại Fragment
                    FragmentTransaction fragmentTransaction = requireActivity().getSupportFragmentManager().beginTransaction();
                    fragmentTransaction.replace(R.id.nav_host_fragment_content_class_et4710_test, new HomeFragment());
                    fragmentTransaction.commit();
                }
            });

        });



        String timeCheckIn = sharedPref.getString(WeekNumber,"");
        if(timeCheckIn.equals("")){
            timeCheckInTitle.setVisibility(View.GONE);
            timeCheckInTv.setVisibility(View.GONE);
        }
        timeCheckInTv.setText(timeCheckIn);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}