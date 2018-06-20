package com.gy.mysmarttable.adapter;

import android.support.annotation.Nullable;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;
import com.gy.mysmarttable.R;
import com.gy.mysmarttable.bean.TanBean;

import java.util.List;

/**
 * Created by huang on 2017/10/13.
 */

public class TanTanAdapter extends BaseQuickAdapter<TanBean,BaseViewHolder> {


    public TanTanAdapter(@Nullable List<TanBean> data) {
        super(R.layout.item_tantan, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, TanBean item) {
        helper.setText(R.id.tvName,item.getName());
        Glide
                .with(mContext)
                .load(item.getUrl())
                .into((ImageView) helper.getView(R.id.iv));
    }
}
