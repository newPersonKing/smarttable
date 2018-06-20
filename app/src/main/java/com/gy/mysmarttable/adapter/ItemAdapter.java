package com.gy.mysmarttable.adapter;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gy.mysmarttable.R;
import com.gy.mysmarttable.bean.MainItem;

import java.util.List;

public class ItemAdapter extends BaseQuickAdapter<MainItem,BaseViewHolder> {


    public ItemAdapter(@Nullable List data) {
        super(R.layout.item_main,data);
    }

    @Override
    protected void convert(BaseViewHolder helper, MainItem item) {
        helper.setText(R.id.tv_chart_name,item.chartName);
    }
}
