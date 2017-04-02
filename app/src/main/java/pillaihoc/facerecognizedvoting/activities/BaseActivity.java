package pillaihoc.facerecognizedvoting.activities;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;

import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.fragments.AddVolunteersFragment;
import pillaihoc.facerecognizedvoting.fragments.AllUsersFragment;
import pillaihoc.facerecognizedvoting.fragments.AllVolunteersFragment;
import pillaihoc.facerecognizedvoting.fragments.EndVotingEventFragment;
import pillaihoc.facerecognizedvoting.fragments.MyProfileFragment;
import pillaihoc.facerecognizedvoting.fragments.ResultFragment;
import pillaihoc.facerecognizedvoting.fragments.VoteNowFragment;
import pillaihoc.facerecognizedvoting.fragments.VotingEventFragment;
import pillaihoc.facerecognizedvoting.helper.TinyDB;

/**
 * Created by deepakgavkar on 07/06/16.
 */
public class BaseActivity extends AppCompatActivity {

    TinyDB tinyDB;
    ArrayList<String> UserDetails = new ArrayList<String>();

    public void storeData(Context context, String email) {
        tinyDB = new TinyDB(context);

        tinyDB.putString("email", email);
        tinyDB.putInt("isLoggedIn", 1);
    }

    public void storeAllDetails(Context context, ArrayList<String> details) {
        tinyDB = new TinyDB(context);

        tinyDB.putListString("alldetails", details);
    }

    public String getEmail(Context context) {
        tinyDB = new TinyDB(context);

        return tinyDB.getString("email");
    }

    public ArrayList<String> getAllDetails(Context context) {
        tinyDB = new TinyDB(context);

        UserDetails = tinyDB.getListString("alldetails");

        return UserDetails;
    }

    public boolean LoggedIn(Context context) {
        tinyDB = new TinyDB(context);
        int flag = tinyDB.getInt("isLoggedIn");
        if (flag == 1) {
            return true;
        }
        return false;
    }

    public void logout(Context context) {
        tinyDB = new TinyDB(context);

        tinyDB.clear();
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public void showMyPofile() {
        MyProfileFragment frag = new MyProfileFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }
    public void showAddVolunteers() {
        AddVolunteersFragment frag = new AddVolunteersFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showAllUsers() {
        AllUsersFragment frag = new AllUsersFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showAllVolunteers() {
        AllVolunteersFragment frag = new AllVolunteersFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showVotingEvent() {
        VotingEventFragment frag = new VotingEventFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showResults() {
        ResultFragment frag = new ResultFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }


    public void showEndVoting() {
        EndVotingEventFragment frag = new EndVotingEventFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm =getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showVoteNow() {
        VoteNowFragment frag = new VoteNowFragment();
        FragmentTransaction trans = getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

}
