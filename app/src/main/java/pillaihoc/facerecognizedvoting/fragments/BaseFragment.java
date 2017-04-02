package pillaihoc.facerecognizedvoting.fragments;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;

import java.util.ArrayList;

import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.helper.TinyDB;


/**
 * Created by deepakgavkar on 07/06/16.
 */
public class BaseFragment extends Fragment {

    TinyDB tinyDB;
    ArrayList<String> prodetails = new ArrayList<String>();

    public void makeLogin(Context context, String email) {
        tinyDB = new TinyDB(context);

        tinyDB.putString("email", email);
        tinyDB.putInt("isLoggedIn", 1);
    }

    public String getEmail(Context context) {
        tinyDB = new TinyDB(context);

        return tinyDB.getString("email");
    }

    public ArrayList<String> getAllDetails(Context context) {
        tinyDB = new TinyDB(context);

        prodetails = tinyDB.getListString("alldetails");

        return prodetails;
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


    public void storeAllDetails(Context context, ArrayList<String> details) {
        tinyDB = new TinyDB(context);

        tinyDB.putListString("alldetails", details);
    }


    public void clearAllDetails(Context context, String key) {
        tinyDB = new TinyDB(context);

        tinyDB.remove(key);
    }

    public void showMyPofile() {
        MyProfileFragment frag = new MyProfileFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showAddVolunteers() {
        AddVolunteersFragment frag = new AddVolunteersFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showAllVolunteers() {
        AllVolunteersFragment frag = new AllVolunteersFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showVotingEvent() {
        VotingEventFragment frag = new VotingEventFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showEndVoting() {
        EndVotingEventFragment frag = new EndVotingEventFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

    public void showVoteNow() {
        VoteNowFragment frag = new VoteNowFragment();
        FragmentTransaction trans = getActivity().getSupportFragmentManager().beginTransaction();
        trans.setCustomAnimations(0, 0, 0, 0);
        trans.replace(R.id.container, frag);
        trans.addToBackStack(null);
        FragmentManager fm = getActivity().getSupportFragmentManager();
        fm.popBackStack(null, FragmentManager.POP_BACK_STACK_INCLUSIVE);
        trans.commit();
    }

}