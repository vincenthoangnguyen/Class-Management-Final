package com.vincent.hoangnguyen.classmanagement.ui.Information;

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
import com.vincent.hoangnguyen.classmanagement.databinding.FragmentInformationBinding;
public class InformationFragment extends Fragment {

    private FragmentInformationBinding binding;
    private TextView nameTv,idTv,phoneNumberTv,emailTv;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        InformationViewModel galleryViewModel =
                new ViewModelProvider(this).get(InformationViewModel.class);
        binding = FragmentInformationBinding.inflate(inflater, container, false);
        View root = binding.getRoot();
        nameTv = root.findViewById(R.id.fragment_information_name);
        idTv = root.findViewById(R.id.fragment_information_mssv);
        phoneNumberTv = root.findViewById(R.id.fragment_information_sdt);
        emailTv = root.findViewById(R.id.fragment_information_email);
        // lấy object user để lấy lại các thông tin người dùng và fill vào fragment
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if(user != null){
            emailTv.setText(user.getEmail());
            nameTv.setText(user.getUid());
        }


        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}