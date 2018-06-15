package com.cqebd.student.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.cqebd.student.R;
import com.cqebd.student.constant.Constant;
import com.google.gson.Gson;

import java.util.List;

import gorden.util.PreferencesUtil;

/**
 * Created by diswy on 2018/4/9.
 */

public class AccountListAdapter extends RecyclerView.Adapter<AccountListAdapter.ViewHolder> {

    private Context context;
    private List<String> list;
    private View.OnClickListener mItemListener;

    public void setItemListener(View.OnClickListener mItemListener) {
        this.mItemListener = mItemListener;
    }

    public AccountListAdapter(Context context, List<String> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new ViewHolder(LayoutInflater.from(context).inflate(R.layout.accout_list_item, parent, false));
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.tv.setText(list.get(position));
        holder.tv.setOnClickListener(v -> {
            if (mItemListener != null) {
                mItemListener.onClick(v);
            }

        });

        holder.btnDel.setOnClickListener(v -> {
            list.remove(position);
            Gson gson = new Gson();
            String jsonList = gson.toJson(list);
            PreferencesUtil.getInstance(context).putString(Constant.USER_NAME_LIST, jsonList);
            notifyDataSetChanged();
        });
    }

    @Override
    public int getItemCount() {
        return list == null ? 0 : list.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {
        TextView tv;
        ImageButton btnDel;

        public ViewHolder(View itemView) {
            super(itemView);
            tv = itemView.findViewById(R.id.tv);
            btnDel = itemView.findViewById(R.id.btn_del);
        }
    }
}
