package pillaihoc.facerecognizedvoting.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;

import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IDrawerItem;
import com.mikepenz.materialdrawer.util.AbstractDrawerImageLoader;
import com.mikepenz.materialdrawer.util.DrawerImageLoader;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import butterknife.Bind;
import butterknife.ButterKnife;
import pillaihoc.facerecognizedvoting.R;
import pillaihoc.facerecognizedvoting.enums.SerialNumber;
import pillaihoc.facerecognizedvoting.helper.Utils;

/**
 * Created by deepakgavkar on 07/09/16.
 */
public class HomeActivity extends BaseActivity {

    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;

    PrimaryDrawerItem myProfile, vote,results, logout;
    Drawer result;
    AccountHeader headerResult;
    String profileName = "", email = "", url = "";
    ArrayList<String> userData = new ArrayList<String>();
    Bundle extras;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);
        extras = getIntent().getExtras();

        try {
            if (extras != null) {
                String profile = extras.getString("profile");
                if (profile != null && profile.equals("1")) {
                    showMyPofile();
                }
            }
            userData = getAllDetails(getApplicationContext());

            profileName = userData.get(SerialNumber.LISTNAME);
            email = userData.get(SerialNumber.LISTEMAIL);
            url = userData.get(SerialNumber.LISTPROFILEPHOTO);

            myProfile = new PrimaryDrawerItem().withName("My Profile").withIcon(getResources().getDrawable(R.drawable.myprofile));
            vote = new PrimaryDrawerItem().withName("Vote now").withIcon(getResources().getDrawable(R.drawable.logout));
            logout = new PrimaryDrawerItem().withName("Logout").withIcon(getResources().getDrawable(R.drawable.logout));
            results  = new PrimaryDrawerItem().withName("Results").withIcon(getResources().getDrawable(R.drawable.myprofile));
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.background)
                    .withSelectionListEnabledForSingleProfile(false)
                    .addProfiles(
                            new ProfileDrawerItem().withName(profileName).withEmail(email).withIcon(url)
                    ).build();

            DrawerImageLoader.init(new AbstractDrawerImageLoader() {
                @Override
                public void set(ImageView imageView, Uri uri, Drawable placeholder) {
                    Picasso.with(imageView.getContext()).load(uri).placeholder(placeholder).into(imageView);
                }

                @Override
                public void cancel(ImageView imageView) {
                    Picasso.with(imageView.getContext()).cancelRequest(imageView);
                }
            });

            result = new DrawerBuilder()
                    .withActivity(this)
                    .withToolbar(toolbar_actionbar)
                    .withAccountHeader(headerResult)
                    .addDrawerItems(
                            myProfile,
                            vote,
                            results,
                            logout
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (position == 1) {
                                showMyPofile();
                            } else if (position == 2) {
                                showVoteNow();
                            }else if (position == 3) {
                                showResults();
                            } else if (position == 4) {
                                logout(getApplicationContext());
                                Intent i = new Intent(HomeActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            return false;
                        }
                    }).build();

            result.setSelection(myProfile);
        } catch (Exception e) {
            Utils.PrintErrorLog(HomeActivity.class.getSimpleName(), "" + e);
        }
    }
}
