package com.gy.mysmarttable;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.utils.DensityUtils;
import com.gy.mysmarttable.bean.ChildData;
import com.gy.mysmarttable.bean.MergeInfo;

import java.util.ArrayList;
import java.util.List;

public class MergeModeActivity extends AppCompatActivity {

    private SmartTable<MergeInfo> table;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_full_table);
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(this,15));
       
        List<MergeInfo> list = new ArrayList<>();
        for(int i = 0;i <50; i++) {
            list.add(new MergeInfo("huangyanbinhuangyanbinhuangyanbinhuangyanbin", 18, System.currentTimeMillis(),true,new ChildData("测试1")));
            list.add(new MergeInfo("huangyanbinhuangyanbinhuangyanbinhuangyanbin", 18, System.currentTimeMillis(),true,new ChildData("测试1")));
            list.add(new MergeInfo("huangyanbinhuangyanbinhuangyanbinhuangyanbin", 18, System.currentTimeMillis(),true,new ChildData("测试1")));
            list.add(new MergeInfo("huangyanbinhuangyanbinhuangyanbinhuangyanbin", 18, System.currentTimeMillis(),true,new ChildData("测试1")));
            list.add(new MergeInfo("lihuangyanbinhuangyanbinhuangyanbinhuangyanbin", 23, System.currentTimeMillis(),false,null));
            list.add(new MergeInfo("lihuangyanbinhuangyanbinhuangyanbinhuangyanbin", 23, System.currentTimeMillis(),false,null));
            list.add(new MergeInfo("lihuangyanbinhuangyanbinhuangyanbinhuangyanbin", 23, System.currentTimeMillis(),false,null));
            list.add(new MergeInfo("lihuangyanbinhuangyanbinhuangyanbinhuangyanbin", 23, System.currentTimeMillis(),false,null));
        }
        table = (SmartTable<MergeInfo>) findViewById(R.id.table);
        table.setData(list);
        table.getConfig().setShowTableTitle(false);
        table.setZoom(true,2,0.2f);

    }

}
