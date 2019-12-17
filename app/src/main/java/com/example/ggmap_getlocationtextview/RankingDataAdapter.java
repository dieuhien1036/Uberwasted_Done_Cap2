package com.example.ggmap_getlocationtextview;

import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.RequiresApi;

import java.util.List;

public class RankingDataAdapter extends BaseAdapter {
    private Context context;
    private int layout;
    private List<User> userList;

    public RankingDataAdapter(Context context, int layout, List<User> userList) {
        this.context = context;
        this.layout = layout;
        this.userList = userList;
    }

    @Override
    public int getCount() {
        return userList.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    private class ViewHolder{
        TextView tvHoTen , tvJob, tvScore , tvNumber;
        ImageView avatar;
        RelativeLayout relativeLayout;
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder;
        if(convertView == null){
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(layout,null);
            holder.tvNumber = convertView.findViewById(R.id.txtNumber);
            holder.tvHoTen = (TextView) convertView.findViewById(R.id.txtName);
            holder.tvJob   = (TextView) convertView.findViewById(R.id.txtJob);
            holder.tvScore = (TextView) convertView.findViewById(R.id.txtScore);
            holder.avatar  = (ImageView) convertView.findViewById(R.id.avatar);
            holder.relativeLayout = (RelativeLayout) convertView.findViewById(R.id.rankingLayout);
            convertView.setTag(holder);
        }else {
            holder = (ViewHolder) convertView.getTag();
        }
        User user = userList.get(position);
        holder.tvHoTen.setText(user.getName());
        holder.tvJob.setText(user.getJob());
        holder.tvScore.setText(String.valueOf(user.getScore()));
        holder.tvNumber.setText(String.valueOf(position+1));
        if(position%2 == 0){
            holder.relativeLayout.setBackgroundColor(Color.parseColor("#008000"));
        }
        return convertView;
    }
    public void setMargins (View view, int left, int top, int right, int bottom) {
        if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
            ViewGroup.MarginLayoutParams p = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
            p.setMargins(left, top, right, bottom);
            view.requestLayout();
        }
    }
}
