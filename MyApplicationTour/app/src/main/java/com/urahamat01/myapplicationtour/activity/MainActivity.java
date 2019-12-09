package com.urahamat01.myapplicationtour.activity;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.urahamat01.myapplicationtour.bottom_sheet.BottomSheet_AddTrip;
import com.urahamat01.myapplicationtour.fragment.DashBoardFragment;
import com.urahamat01.myapplicationtour.fragment.TicketFragment;
import com.urahamat01.myapplicationtour.map_action.MapsActivity;
import com.urahamat01.myapplicationtour.R;

import de.hdodenhof.circleimageview.CircleImageView;


public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private BottomSheet_AddTrip bottomSheet_addTrip;
    private TextView userNameTv;
    private FirebaseDatabase firebaseDatabase;
    private FirebaseAuth firebaseAuth;
    private String currentuser;
    private DatabaseReference database;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();

        loaddefaultfragment();


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        View header = navigationView.getHeaderView(0);
        final TextView userNameHeaderTv = (TextView) header.findViewById(R.id.userNameTvId);
        final TextView userEmailHeaderTv = (TextView) header.findViewById(R.id.userEmailTvId);
        final CircleImageView userPhotoIV = header.findViewById(R.id.imageView);

        database = FirebaseDatabase.getInstance().getReference().child("UserList");
        database.child(currentuser).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {

                    String fname = dataSnapshot.child("firstName").getValue().toString();
                    String lname = dataSnapshot.child("lastName").getValue().toString();
                    String email = dataSnapshot.child("email").getValue().toString();
                    String pfPhoto = dataSnapshot.child("profilePhoto").getValue().toString();
                    String name = fname + " " + lname;
                    Uri myUri = Uri.parse(pfPhoto);
                    Picasso.get().load(myUri).into(userPhotoIV);


                    userNameHeaderTv.setText(name);
                    userEmailHeaderTv.setText(email);

                } else {

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(MainActivity.this, "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });


    }

    private void loaddefaultfragment() {

        DashBoardFragment dashBoardFragment = new DashBoardFragment();
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.frame_layout_id, dashBoardFragment);
        fragmentTransaction.commit();

    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();



        if (id == R.id.action_settings) {
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_id, dashBoardFragment);
            fragmentTransaction.commit();

            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        int id = item.getItemId();


        if (id == R.id.nav_Weather) {

            Intent intent = new Intent(MainActivity.this, WeatherActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_Nearme) {

            Intent intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);


        } else if (id == R.id.nav_Logout) {


            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            LayoutInflater layoutInflater = LayoutInflater.from(this);
            View view = layoutInflater.inflate(R.layout.alart_signout, null);

            builder.setView(view);
            final Dialog dialog = builder.create();
            dialog.show();

            TextView signout = view.findViewById(R.id.signOutTvId);
            TextView cancel = view.findViewById(R.id.cancelTvID);

            signout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    FirebaseAuth.getInstance().signOut();
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    if (user != null) {


                    } else {

                        Intent intent = new Intent(MainActivity.this, LogInActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);


                    }


                    dialog.dismiss();
                }
            });

            cancel.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    dialog.dismiss();
                }
            });


        } else if (id == R.id.nav_Ticket) {

            TicketFragment ticketFragment = new TicketFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack("dashboard");
            fragmentTransaction.replace(R.id.frame_layout_id, ticketFragment);
            fragmentTransaction.commit();




        } else if (id == R.id.nav_Home) {
            DashBoardFragment dashBoardFragment = new DashBoardFragment();
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.frame_layout_id, dashBoardFragment);
            fragmentTransaction.commit();

        } else if (id == R.id.nav_share) {


        } else if (id == R.id.nav_send) {

        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
