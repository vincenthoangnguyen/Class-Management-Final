package com.vincent.hoangnguyen.classmanagement.ui.home;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
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
    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        checkInBtn = root.findViewById(R.id.checkInBtn);
        checkInBtn.setOnClickListener(view -> {
            checkInBtn = (Button) view;
            Map<String, Timestamp> map = new HashMap<>();
            Timestamp CheckInTime = Timestamp.now();
            map.put("CheckInTime",CheckInTime);
            FirebaseFirestore firestore = FirebaseFirestore.getInstance();
            firestore.collection("CheckInTime").add(map).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                @Override
                public void onComplete(@NonNull Task<DocumentReference> task) {
                    if(task.isSuccessful()){
                        Utility.showToast(getContext(),"Check in thành công!");
                        checkInBtn.setBackgroundColor(Color.GREEN);
                    }
                    else {
                        Utility.showToast(getContext(), Objects.requireNonNull(task.getException()).toString());
                    }
                }
            });

        });
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}