package com.gy.mysmarttable;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.utils.DensityUtils;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.gy.mysmarttable.adapter.SheetAdapter;
import com.gy.mysmarttable.excel.ExcelCallback;
import com.gy.mysmarttable.excel.IExcel2Table;
import com.gy.mysmarttable.excel.JXLExcel2Table;

import java.util.List;

import jxl.Cell;


public class JXLExcelModeActivity extends AppCompatActivity implements ExcelCallback {

    private SmartTable<Cell> table;
    private RecyclerView recyclerView;
    private String fileName = "ic_class.xls";
    private IExcel2Table<Cell> iExcel2Table;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_excel_table);
        FontStyle.setDefaultTextSize(DensityUtils.sp2px(this,15));
        recyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false));
        table = (SmartTable<Cell>) findViewById(R.id.table);
        iExcel2Table = new JXLExcel2Table();
        iExcel2Table.initTableConfig(this,table);
        iExcel2Table.setCallback(this);
        iExcel2Table.loadSheetList(this,fileName);

    }



    @Override
    protected void onDestroy() {
        if(iExcel2Table !=null){
            iExcel2Table.clear();
        }
        iExcel2Table = null;
        super.onDestroy();
    }

    @Override
    public void getSheetListSuc(List<String> sheetNames) {
        recyclerView.setHasFixedSize(true);
        if(sheetNames!=null && sheetNames.size() >0) {
            final SheetAdapter sheetAdapter = new SheetAdapter(sheetNames);
            sheetAdapter.setOnItemClickListener(new BaseQuickAdapter.OnItemClickListener() {
                @Override
                public void onItemClick(BaseQuickAdapter adapter, View view, int position) {
                    sheetAdapter.setSelectPosition(position);
                    iExcel2Table.loadSheetContent(JXLExcelModeActivity.this,position);
                }
            });
            recyclerView.setAdapter(sheetAdapter);
            iExcel2Table.loadSheetContent(JXLExcelModeActivity.this,0);
        }
    }
}
