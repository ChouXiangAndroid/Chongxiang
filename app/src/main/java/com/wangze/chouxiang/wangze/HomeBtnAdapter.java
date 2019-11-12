package com.wangze.chouxiang.wangze;

import android.graphics.Rect;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;


import com.wangze.chouxiang.R;

import java.util.List;

public class HomeBtnAdapter extends RecyclerView.Adapter<HomeBtnAdapter.ContentHolder>
{

    private final List<HomeBtnItemFactory.HomeBtnItem> funcList;

    class ContentHolder extends RecyclerView.ViewHolder
    {
        private ImageButton btnSelf;
        private TextView btnName;

        public ContentHolder(@NonNull View itemView)
        {
            super(itemView);
            btnSelf = itemView.findViewById(R.id.home_btn);
            btnName = itemView.findViewById(R.id.home_btn_name);
        }
    }

    public HomeBtnAdapter(List<HomeBtnItemFactory.HomeBtnItem> funcList)
    {
        this.funcList = funcList;
    }

    @NonNull
    @Override
    public ContentHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType)
    {
        ContentHolder holder =
                new ContentHolder(LayoutInflater.from(parent.getContext()).
                        inflate(R.layout.layout_homepage_btn, parent, false));

        return holder;
    }

    @Override
    public void onBindViewHolder(@NonNull final ContentHolder holder, int position)
    {
        final HomeBtnItemFactory.HomeBtnItem homeBtnItem = funcList.get(position);
        holder.btnName.setText(homeBtnItem.getBtnName());
        holder.btnSelf.setImageResource(homeBtnItem.getImageSrc());
        holder.btnSelf.setOnClickListener(homeBtnItem);
    }

    @Override
    public int getItemCount()
    {
        return funcList.size();
    }
}
