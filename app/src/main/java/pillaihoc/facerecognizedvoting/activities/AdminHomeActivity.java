package pillaihoc.facerecognizedvoting.activities;

import android.content.Intent;
import android.content.res.Resources;
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

import butterknife.Bind;
import butterknife.ButterKnife;
import pillaihoc.facerecognizedvoting.R;

/**
 * Created by deepakgavkar on 09/09/16.
 */
public class AdminHomeActivity extends BaseActivity {
    public static PrimaryDrawerItem allVolunteers, addVolunteers, allUsers, results, setVoteEvent, logout;
    public static Drawer result;
    @Bind(R.id.toolbar_actionbar)
    Toolbar toolbar_actionbar;
    AccountHeader headerResult;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_home);
        ButterKnife.bind(this);
        setSupportActionBar(toolbar_actionbar);
        try {

            addVolunteers = new PrimaryDrawerItem().withName("Add volunteers").withIcon(getResources().getDrawable(R.drawable.myprofile));
            allUsers = new PrimaryDrawerItem().withName("All Users").withIcon(getResources().getDrawable(R.drawable.myprofile));
            setVoteEvent = new PrimaryDrawerItem().withName("Handle vote event").withIcon(getResources().getDrawable(R.drawable.myprofile));
            logout = new PrimaryDrawerItem().withName("Logout").withIcon(getResources().getDrawable(R.drawable.logout));
            allVolunteers = new PrimaryDrawerItem().withName("All Volunteers").withIcon(getResources().getDrawable(R.drawable.logout));
            results = new PrimaryDrawerItem().withName("Voting Results").withIcon(getResources().getDrawable(R.drawable.myprofile));
            headerResult = new AccountHeaderBuilder()
                    .withActivity(this)
                    .withHeaderBackground(R.drawable.background)
                    .withSelectionListEnabledForSingleProfile(false)
                    .addProfiles(
                            new ProfileDrawerItem().withName("Admin").withIcon(getResources().getDrawable(R.drawable.personplaceholder))
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
                            allVolunteers, allUsers,
                            addVolunteers,
                            setVoteEvent,
                            results,
                            logout
                    )
                    .withOnDrawerItemClickListener(new Drawer.OnDrawerItemClickListener() {
                        @Override
                        public boolean onItemClick(View view, int position, IDrawerItem drawerItem) {
                            if (position == 1) {
                                showAllVolunteers();
                            } else if (position == 2) {
                                showAllUsers();
                            } else if (position == 3) {
                                showAddVolunteers();
                            } else if (position == 4) {
                                showVotingEvent();
                            } else if (position == 5) {
                                showResults();
                            } else if (position == 6) {
                                logout(getApplicationContext());
                                Intent i = new Intent(AdminHomeActivity.this, LoginActivity.class);
                                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                                startActivity(i);
                                finish();
                            }
                            return false;
                        }
                    }).build();

            result.setSelection(allVolunteers);
        } catch (Resources.NotFoundException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
