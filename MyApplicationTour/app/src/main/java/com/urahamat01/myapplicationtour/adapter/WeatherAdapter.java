package com.urahamat01.myapplicationtour.adapter;

import android.app.ProgressDialog;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;
import com.urahamat01.myapplicationtour.R;
import com.urahamat01.myapplicationtour.weather.WeatherResult;

import java.text.SimpleDateFormat;
import java.util.Date;

public class WeatherAdapter extends RecyclerView.Adapter<WeatherAdapter.ViewGroup> {

    Context context;
    WeatherResult weatherResult;
    private ProgressDialog loadinbar;

    public WeatherAdapter(Context context, WeatherResult weatherResult) {
        this.context = context;
        this.weatherResult = weatherResult;
    }

    @NonNull
    @Override
    public ViewGroup onCreateViewHolder(@NonNull android.view.ViewGroup viewGroup, int i) {

        View view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.weither_items, viewGroup, false);

        return new ViewGroup(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewGroup viewGroup, int i) {
        loadinbar = new ProgressDialog(context);

        Picasso.get().load(new StringBuilder("https://openweathermap.org/img/w/")
                .append(weatherResult.getList().get(i).getWeather().get(0).getIcon())
                .append(".png").toString()).into(viewGroup.weitherIcon);

        SimpleDateFormat dateandTimeSDF = new SimpleDateFormat("dd MMMM yyyy");

        Date date = new Date();
        date.setDate((weatherResult.getList().get(i).getDt()));
        viewGroup.weitherDate.setText("Date   : " + weatherResult.getList().get(i).getDt_txt());


        viewGroup.weitherDescription.setText("Status : " + weatherResult.getList().get(i).getWeather().get(0).getDescription());
        viewGroup.weitherTemp.setText(("Temp  : " + weatherResult.getList().get(i).getMain().getTemp() + " °C"));
        viewGroup.weitherWind.setText("Wind : " + weatherResult.getList().get(i).getWind().getSpeed() + " km/h");
        loadinbar.dismiss();

    }

    @Override
    public int getItemCount() {
        return weatherResult.getList().size();
    }

    public class ViewGroup extends RecyclerView.ViewHolder {
        private ImageView weitherIcon;
        private TextView weitherDate, weitherTemp, weitherWind, weitherHumidity, weitherDescription, weatherLoactionTv;

        public ViewGroup(@NonNull View itemView) {
            super(itemView);

            weitherIcon = itemView.findViewById(R.id.weatherItemIvId);
            weitherDate = itemView.findViewById(R.id.dateWeitherItemTvId);
            weitherTemp = itemView.findViewById(R.id.tempWeitherItemTvId);
            weitherWind = itemView.findViewById(R.id.windWeitherItemTvId);
            weitherDescription = itemView.findViewById(R.id.weitherDiscriptionTvId);


        }
    }
}

