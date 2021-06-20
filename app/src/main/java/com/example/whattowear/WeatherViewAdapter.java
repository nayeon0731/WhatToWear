package com.example.whattowear;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class WeatherViewAdapter extends RecyclerView.Adapter<WeatherViewAdapter.ViewHolder> {

    ArrayList<WeatherInfoData> itemViewArrayList = null; //객체배열

//    public WeatherViewAdapter(Context context, ArrayList<WeatherInfoData> itemViewArrayList) {
//        this.itemViewArrayList = itemViewArrayList;
//        this.mContext = context;
//    }

    //아이템 뷰를 저장하는 뷰홀더 클래스
    public class ViewHolder extends RecyclerView.ViewHolder {
        //weather_recyclerview에 만들어진 뷰 넣기
        TextView tempItemView, rainItemView, timeItemView;

        ViewHolder(View itemView) {
            super(itemView);

            //뷰 객체에 대한 참조

            tempItemView = itemView.findViewById(R.id.tempItemView);
            rainItemView = itemView.findViewById(R.id.rainItemView);
            timeItemView = itemView.findViewById(R.id.timeItemView);
        }

        public void setItem(WeatherInfoData weatherInfo) {
            tempItemView.setText(Integer.toString(weatherInfo.getTempature()) );
            rainItemView.setText(Integer.toString(weatherInfo.getRain()));
            timeItemView.setText("임시");
        }
    }



    //생성자에서 데이터 리스트 객체를 전달받음.
    WeatherViewAdapter(ArrayList<WeatherInfoData> list) {
        itemViewArrayList = list;
    }

    //onCreateViewHolder() - 아이템 뷰를 위한 뷰홀더 객체 생성하여 리턴
    @Override
    public WeatherViewAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        Context mContext;
        mContext = parent.getContext();
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        View view = inflater.inflate(R.layout.weather_recyclerview, parent, false);
        WeatherViewAdapter.ViewHolder vh = new WeatherViewAdapter.ViewHolder(view);

        return vh;
    }

    //onBindViewHolder() - position에 해당하는 데이터를 뷰홀더의 아이템 뷰에 표시
    @Override
    public void onBindViewHolder(WeatherViewAdapter.ViewHolder holder, int position) {
        WeatherInfoData weatherInfo = itemViewArrayList.get(position);
        holder.setItem(weatherInfo);

    }

    //getItemCount() 전체 데이터 갯수 리턴
    @Override
    public int getItemCount() {
        return itemViewArrayList.size();
    }
}
