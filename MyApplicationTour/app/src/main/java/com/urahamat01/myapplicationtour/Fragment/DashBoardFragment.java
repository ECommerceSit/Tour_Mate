package com.urahamat01.myapplicationtour.Fragment;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;
import com.urahamat01.myapplicationtour.Adapter.ExpenseAdapter;
import com.urahamat01.myapplicationtour.Adapter.TripAdapter;
import com.urahamat01.myapplicationtour.BottomSheet.BottomSheet_AddExpense;
import com.urahamat01.myapplicationtour.BottomSheet_AddTrip;
import com.urahamat01.myapplicationtour.Classes.Expense;
import com.urahamat01.myapplicationtour.Classes.IndividualTrip;
import com.urahamat01.myapplicationtour.CurrentWeather.WeatherResponse;
import com.urahamat01.myapplicationtour.IOpenWeatherMap;
import com.urahamat01.myapplicationtour.MapAction.MapsActivity;
import com.urahamat01.myapplicationtour.R;
import com.urahamat01.myapplicationtour.RetrofitClass;
import com.urahamat01.myapplicationtour.WeatherActivity;
import com.urahamat01.myapplicationtour.Weither.WeatherResult;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * A simple {@link Fragment} subclass.
 */
public class DashBoardFragment extends Fragment {

    private TextView currentWeatherDiscription, currentWeathertemp, currentWeatherWind, currentWeatherLocatonTv, currentWeatherHumidity;
    private ImageView currentWeatherIcon;

    private RecyclerView recyclerView;

    private WeatherResult weatherResult;
    private WeatherResult currentWeatherResult;

    private LinearLayout balanceLayout;

    private double lat = 0;
    private double lon = 0;
    private String units = "metric";
    String url;

    FusedLocationProviderClient fusedLocationProviderClient;

    private BottomSheet_AddTrip bottomSheet_addTrip;
    private FloatingActionButton fab;
    private RecyclerView triprecyclerView;
    DatabaseReference database;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference myRef;
    private List<IndividualTrip> list;
    private List<IndividualTrip> filterList;
    private TripAdapter tripAdapter;

    private FirebaseAuth firebaseAuth;
    private String currentuser;

    private CardView cardView;

    private long fromdateMs;
    private long fromdateMs1;

    private long todateMs = Long.valueOf("2592000000");


    private TextView fromDateTv, toDateTv;
    private LinearLayout fromDatepicked, toDatepicked;
    private long selectedFromDateinMS;
    private long selectedToDateinMS;
    private TextView viewAllTripsTv;


    private FloatingActionButton floatingActionButton;
    private BottomSheet_AddExpense bottomSheet_addExpense;
    public String eventId;
    DatabaseReference database1;
    DatabaseReference dataB;
    FirebaseDatabase firebaseDatabase1;
    DatabaseReference myRef1;
    private List<Expense> expenseList;
    private ExpenseAdapter expenseAdapter;

    private FirebaseAuth firebaseAuth1;



    private CardView nearmeCv, weatherCV, ticketCv, allTripsCv;
    private TextView nearme_cv_Tv;


    private NumberFormat nf = new DecimalFormat("##.###");


    private TextView currentBalanceTvId, expensePersentageTv, budExTv;

    ProgressBar progressBar;

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    int expenditure;
    int reducedBudget = 0;
    int budget;
    int consumed;


    int cBudget;
    int cExpense;
    private ProgressDialog loadinbar;


    public DashBoardFragment() {

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View view = inflater.inflate(R.layout.fragment_dash_board, container, false);

        loadinbar = new ProgressDialog(getActivity());
        balanceLayout = view.findViewById(R.id.linlayID);

        nearme_cv_Tv = view.findViewById(R.id.nearme_cv_TvId);
        nearmeCv = view.findViewById(R.id.nearme_CardViewId);
        weatherCV = view.findViewById(R.id.weather_CardViewId);
        ticketCv = view.findViewById(R.id.ticket_CardViewId);
        allTripsCv = view.findViewById(R.id.allTours_CardViewId);
        fab = view.findViewById(R.id.fab);

        cardView = view.findViewById(R.id.weatherCardId);

        allTripsCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TripFragment tripFragment = new TripFragment();
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.frame_layout_id, tripFragment);
                fragmentTransaction.addToBackStack("dashboard");
                fragmentTransaction.commit();
            }
        });


        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                bottomSheet_addTrip = new BottomSheet_AddTrip();
                bottomSheet_addTrip.show(getFragmentManager(), "BootmSheet_addtrip");

            }
        });


        weatherCV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), WeatherActivity.class);
                startActivity(intent);
            }
        });
        nearmeCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        ticketCv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TicketFragment ticketFragment = new TicketFragment();
                FragmentManager fragmentManager = ((AppCompatActivity) getContext()).getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack("dashboard");
                fragmentTransaction.replace(R.id.frame_layout_id, ticketFragment);
                fragmentTransaction.commit();
            }
        });





        firebaseDatabase = FirebaseDatabase.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        currentuser = firebaseAuth.getCurrentUser().getUid();
        list = new ArrayList<>();
        filterList = new ArrayList<>();



        fromDateTv = view.findViewById(R.id.fromDateTV);
        toDateTv = view.findViewById(R.id.toDateTV);

        fromDatepicked = view.findViewById(R.id.fromDatePickDashboadLayoutId);
        toDatepicked = view.findViewById(R.id.toDatePickDashboardID);




        LinearLayoutManager layoutManager
                = new LinearLayoutManager(getContext(), LinearLayoutManager.HORIZONTAL, false);
        triprecyclerView = view.findViewById(R.id.trip_recycler_view);
        triprecyclerView.setLayoutManager(layoutManager);



        currentBalanceTvId = view.findViewById(R.id.currenBalanceDisplayTvId);

        expensePersentageTv = view.findViewById(R.id.expensePersentageTvId);
        budExTv = view.findViewById(R.id.budExTvId);





        progressBar = view.findViewById(R.id.progressBar);




        Calendar calendar = Calendar.getInstance();

        int year = calendar.get(calendar.YEAR);
        int month = calendar.get(calendar.MONTH);
        int day = calendar.get(calendar.DAY_OF_MONTH);
        month = month + 1;

        String selectedDate = year + "/" + month + "/" + day + " 00:00:00";

        SimpleDateFormat dateandTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        SimpleDateFormat dateSDF = new SimpleDateFormat("dd MMM yyyy");

        Date date = null;
        try {
            date = dateandTimeSDF.parse(selectedDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        fromdateMs = date.getTime();



        int year1 = calendar.get(calendar.YEAR);
        int month1 = calendar.get(calendar.MONTH);
        int day1 = calendar.get(calendar.DAY_OF_MONTH);
        month1 = month1 + 1;
        day1 = day1 + 30;
        String selectedtoDate = year1 + "/" + month1 + "/" + day1 + " 23:59:59";

        SimpleDateFormat todateandTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");


        Date date1 = new Date();

        date1 = null;
        try {
            date1 = todateandTimeSDF.parse(selectedtoDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }


        todateMs = date1.getTime();



        database = FirebaseDatabase.getInstance().getReference().child("UserList").child(currentuser);
        database.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    filterList.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {
                        String fromDatetrip = data.child("info").getValue(IndividualTrip.class).getTrip_fromDate();
                        String toDatetrip = data.child("info").getValue(IndividualTrip.class).getTrip_toDate();



                        Long flong = Long.valueOf(fromDatetrip);
                        Long tlong = Long.valueOf(toDatetrip);


                        Calendar calendar = Calendar.getInstance();

                        int year = calendar.get(calendar.YEAR);
                        int month = calendar.get(calendar.MONTH);
                        int day = calendar.get(calendar.DAY_OF_MONTH);
                        month = month + 1;
                        String selectedDate = year + "/" + month + "/" + day + " 23:59:59";

                        SimpleDateFormat dateandTimeSDF = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
                        SimpleDateFormat dateSDFF = new SimpleDateFormat("dd MMM yyyy");


                        Date date1 = null;
                        try {
                            date1 = dateandTimeSDF.parse(selectedDate);
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                        fromdateMs1 = date1.getTime();


                        if (flong <= fromdateMs1 && tlong >= fromdateMs1) {
                            IndividualTrip trip = data.child("info").getValue(IndividualTrip.class);
                            filterList.add(trip);


                        }


                    }


                    if (filterList.size() == 0) {
                        viewAllTrip();
                        balanceLayout.setVisibility(View.GONE);
                        triprecyclerView.setVisibility(view.GONE);
                        return;

                    } else {
                        filterList.get(0).getTrip_id();
                    }
                    setEventId(filterList.get(0).getTrip_id().toString());


                    firebaseDatabase = FirebaseDatabase.getInstance();
                    firebaseAuth = FirebaseAuth.getInstance();

                    expenseList = new ArrayList<>();

                    CreateProgressBar();


                    dataB = FirebaseDatabase.getInstance().getReference().child("UserList").child(currentuser);
                    dataB.child("Events").child(eventId).child("info").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()) {
                                budget = Integer.valueOf(dataSnapshot.getValue(IndividualTrip.class).getTrip_Budget());
                            }



                            cBudget = budget;
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {
                            Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });


                    database = FirebaseDatabase.getInstance().getReference().child("UserList").child(currentuser).child("Events").child(eventId);
                    database.child("Wallet").addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                            int total = 0;
                            for (DataSnapshot ds : dataSnapshot.getChildren()) {
                                int number = Integer.valueOf(ds.getValue(Expense.class).getExpenseAmount());
                                total = total + number;
                            }


                            expenditure = total;



                            ShowProgressBar();

                            checkBalance(total, budget);
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {

                        }
                    });


                    tripAdapter = new TripAdapter(filterList, getContext());
                    triprecyclerView.setAdapter(tripAdapter);
                    tripAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Empty database", Toast.LENGTH_SHORT).show();
                    balanceLayout.setVisibility(View.GONE);
                    triprecyclerView.setVisibility(view.GONE);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });








        currentWeatherDiscription = view.findViewById(R.id.cityNameCurrentTvId);
        currentWeatherIcon = view.findViewById(R.id.weatherCurrentIconIvId);
        currentWeathertemp = view.findViewById(R.id.tempCurrentWeitherTvId);
        currentWeatherWind = view.findViewById(R.id.windCurrentWeitherTvId);
        currentWeatherHumidity = view.findViewById(R.id.humidityCurrentWeitherTvId);
        currentWeatherLocatonTv = view.findViewById(R.id.cityStatusCurrentTvId);

        weatherResult = new WeatherResult();
        recyclerView = view.findViewById(R.id.weatherRecyclerViewId);

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getMyLocation();

        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }


        return view;
    }

    private void viewAllTrip() {


        database1 = FirebaseDatabase.getInstance().getReference().child("UserList").child(currentuser);
        database1.child("Events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    list.clear();
                    for (DataSnapshot data : dataSnapshot.getChildren()) {

                        IndividualTrip trip = data.child("info").getValue(IndividualTrip.class);

                        list.add(trip);

                    }
                    tripAdapter = new TripAdapter(list, getContext());
                    triprecyclerView.setAdapter(tripAdapter);
                    tripAdapter.notifyDataSetChanged();
                } else {
                    Toast.makeText(getActivity(), "Empty database", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                Toast.makeText(getContext(), "" + databaseError.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }


    private void getMyLocation() {

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(getActivity());

        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnCompleteListener(new OnCompleteListener<Location>() {
            @Override
            public void onComplete(@NonNull Task<Location> task) {
                if (task.isSuccessful()) {
                    Location location = task.getResult();
                    url = String.format("weather?lat=%f&lon=%f&units=%s&appid=%s", location.getLatitude(), location.getLongitude(), units, getResources().getString(R.string.appid1));

                    loadinbar.setTitle("Loading");
                    loadinbar.setMessage("Please wait");
                    loadinbar.show();
                    loadinbar.setCanceledOnTouchOutside(true);
                    getWeatherUpdate();
                }

            }
        });

    }

    private void getWeatherUpdate() {

        IOpenWeatherMap weatherService = RetrofitClass.getRetrofitInstance().create(IOpenWeatherMap.class);
        Call<WeatherResponse> weatherResponseCall = weatherService.getWeatherData1(url);
        weatherResponseCall.enqueue(new Callback<WeatherResponse>() {
            @Override
            public void onResponse(Call<WeatherResponse> call, Response<WeatherResponse> response) {

                if (response.code() == 200) {


                    WeatherResponse weatherResponse = response.body();
                    currentWeathertemp.setText(String.valueOf(weatherResponse.getMain().getTemp()) + "°C");
                    currentWeatherLocatonTv.setText(String.valueOf(weatherResponse.getName()));
                    currentWeatherDiscription.setText(String.valueOf(weatherResponse.getWeather().get(0).getDescription()));
                    currentWeatherHumidity.setText("Humidity: " + (String.valueOf(weatherResponse.getMain().getHumidity())) + "%");
                    currentWeatherWind.setText("Wind       : " + (String.valueOf(weatherResponse.getWind().getSpeed())) + "km/h");
                    Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                            .append(weatherResponse.getWeather().get(0).getIcon())
                            .append(".png").toString()).into(currentWeatherIcon);

                    cardView.setVisibility(View.VISIBLE);

                    loadinbar.dismiss();


                }


            }

            @Override
            public void onFailure(Call<WeatherResponse> call, Throwable t) {

                cardView.setVisibility(View.INVISIBLE);
                nearme_cv_Tv.setText("My Location");
                weatherCV.setEnabled(false);
                ticketCv.setEnabled(false);

                loadinbar.dismiss();


            }
        });
    }


    private void checkBalance(int total, int bud) {

        double consumed2 = (Double.valueOf(expenditure) * 100) / Double.valueOf(budget);
        expensePersentageTv.setText(String.valueOf(nf.format(consumed2)) + "%");
        final int cBalance = bud - total;
        currentBalanceTvId.setText(String.valueOf(cBalance) + " BDT");

        budExTv.setText(total + "/" + bud);





    }


    private void ShowProgressBar() {

        if (expenditure >= 0) {
            calculateProgress();
        } else
            Toast.makeText(getContext(), "Sorry! No Ammount is remainnig.", Toast.LENGTH_SHORT).show();
    }

    private void calculateProgress() {
        if (expenditure >= 0) {


            double consumed3 = (Double.valueOf(expenditure) * 100) / Double.valueOf(budget);
            progressBar.setProgress(Integer.valueOf((int) consumed3));

        } else Toast.makeText(getContext(), "please enter some ammount", Toast.LENGTH_SHORT).show();
    }

    private void CreateProgressBar() {
        progressBar.setIndeterminate(false);
        progressBar.setMax(100);
        progressBar.showContextMenu();
        progressBar.setScaleY(5f);
    }


}
