package com.vincent.hoangnguyen.classmanagement.ui.Information;

import static android.content.Context.MODE_PRIVATE;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.vincent.hoangnguyen.classmanagement.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.vincent.hoangnguyen.classmanagement.controller.UI.loginAndCreate.CreateAccountActivity;
import com.vincent.hoangnguyen.classmanagement.databinding.FragmentInformationBinding;
import com.vincent.hoangnguyen.classmanagement.model.DetailInformationActivity;

import java.util.Objects;

public class InformationFragment extends Fragment {

    private FragmentInformationBinding binding;
    private TextView nameTv,idTv,phoneNumberTv,emailTv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InformationViewModel galleryViewModel =
                new ViewModelProvider(this).get(InformationViewModel.class);
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        //mapping
        nameTv = root.findViewById(R.id.fragment_information_name);
        idTv = root.findViewById(R.id.fragment_information_mssv);
        phoneNumberTv = root.findViewById(R.id.fragment_information_sdt);
        emailTv = root.findViewById(R.id.fragment_information_email);
        // lấy object user để lấy lại các thông tin người dùng và fill vào fragment
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            emailTv.setText(user.getEmail());
        }
        // lấy thông tin từ bộ nhớ ứng dụng để điền vào fragment thông tin sinh viên
        SharedPreferences sharedPref = requireActivity().getSharedPreferences("mypref", MODE_PRIVATE);
        String username = sharedPref.getString("userName", "");
        String userEmail = sharedPref.getString("userEmail", "");
        String userID = sharedPref.getString("userId", "");
        String userPhoneNumber = sharedPref.getString("userPhoneNumber", "");
        // set text cho các text view
        nameTv.setText(username);
        emailTv.setText(userEmail);
        idTv.setText(userID);
        phoneNumberTv.setText(userPhoneNumber);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}