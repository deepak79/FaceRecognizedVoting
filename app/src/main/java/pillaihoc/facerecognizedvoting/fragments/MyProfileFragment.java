package pillaihoc.facerecognizedvoting.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Calendar;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.activities.ChangePasswordActivity;
import pillaihoc.facerecognizedvoting.enums.SerialNumber;

/**
 * Created by deepakgavkar on 08/09/16.
 */
public class MyProfileFragment extends BaseFragment {

    @Bind(R.id.imgProfile)
    ImageView imgProfile;

    @Bind(R.id.btnChangePassword)
    Button btnChangePassword;

    @Bind(R.id.tvUserName)
    TextView tvUserName;

    @Bind(R.id.tvUserEmail)
    TextView tvUserEmail;

    @Bind(R.id.tvUserWard)
    TextView tvUserWard;

    @Bind(R.id.tvUserGenderAge)
    TextView tvUserGenderAge;

    ArrayList<String> userData = new ArrayList<String>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_my_profile, container, false);
        ButterKnife.bind(this, view);
        userData = getAllDetails(getActivity());

        try {
            ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("My Profile");
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        setFields();

        return view;
    }

    @OnClick(R.id.btnChangePassword)
    public void changePass() {
        Intent i = new Intent(getActivity(), ChangePasswordActivity.class);
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(i);
        getActivity().finish();
    }

    public void setFields() {
        if (userData != null && userData.size() > 0) {
            try {
                Calendar now = Calendar.getInstance();
                int currentYear = now.get(Calendar.YEAR), currentAge = -1;
                tvUserName.setText(userData.get(SerialNumber.LISTNAME));
                tvUserEmail.setText(userData.get(SerialNumber.LISTEMAIL));
                tvUserWard.setText(userData.get(SerialNumber.LISTWARD));
                String[] gender = userData.get(SerialNumber.LISTGENDER).split("a");
                String age = userData.get(SerialNumber.LISTDOB);
                String[] scatteredDOB = age.split("/");
                Picasso.with(getActivity()).load(userData.get(SerialNumber.LISTPROFILEPHOTO)).into(imgProfile);
                currentAge = (currentYear - Integer.parseInt(scatteredDOB[2]));
                tvUserGenderAge.setText(currentAge + " / " + gender[0]);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        userData = getAllDetails(getActivity());
    }
}
