package com.gy.mysmarttable.excel;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.support.v4.content.ContextCompat;

import com.bin.david.form.core.SmartTable;
import com.bin.david.form.core.TableConfig;
import com.bin.david.form.data.CellInfo;
import com.bin.david.form.data.CellRange;
import com.bin.david.form.data.column.Column;
import com.bin.david.form.data.format.IFormat;
import com.bin.david.form.data.format.bg.BaseBackgroundFormat;
import com.bin.david.form.data.format.bg.BaseCellBackgroundFormat;
import com.bin.david.form.data.format.draw.LeftTopDrawFormat;
import com.bin.david.form.data.format.draw.TextDrawFormat;
import com.bin.david.form.data.format.selected.BaseSelectFormat;
import com.bin.david.form.data.format.tip.MultiLineBubbleTip;
import com.bin.david.form.data.style.FontStyle;
import com.bin.david.form.data.style.LineStyle;
import com.bin.david.form.data.table.ArrayTableData;
import com.bin.david.form.utils.DensityUtils;
import com.bin.david.form.utils.DrawUtils;
import com.gy.mysmarttable.R;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;


/**
 * Created by huang on 2018/1/23.
 * 通用Excel导入表格
 */

public abstract class BaseExcel2Table<T> implements IExcel2Table<T> {

    private ExcelCallback callback;
    private SheetAsyncTask sheetAsyncTask;
    private ExcelAsyncTask<T> excelAsyncTask;
    private String fileName;
    private List<CellRange> ranges;
    private float fontScale =  1.7f;
    protected SmartTable<T> smartTable;
    private boolean isAssetsFile = true; //默认从Assets文件读取
    /**
     * 初始化默认配置
     * @param context
     * @param table
     */
    @Override
    public void initTableConfig(final Context context,SmartTable<T> table) {
        this.smartTable = table;
        table.getConfig().setFixedYSequence(true);
        table.getConfig().setFixedXSequence(true);
        table.getConfig().setShowTableTitle(false);
        int backgroundColor = ContextCompat.getColor(context, R.color.arc_bg);
        int xyGridColor = ContextCompat.getColor(context,R.color.excel_bg); //x,y序列网格颜色
        //配置
        table.getConfig().setHorizontalPadding(DensityUtils.dp2px(context,10))
                .setColumnTitleHorizontalPadding(DensityUtils.dp2px(context,5))
                .setXSequenceBackground(new BaseBackgroundFormat(backgroundColor))
                .setYSequenceBackground(new BaseBackgroundFormat(backgroundColor))
                .setLeftAndTopBackgroundColor(backgroundColor)
                .setSequenceGridStyle(new LineStyle().setColor(xyGridColor))
                .setLeftTopDrawFormat(new LeftTopDrawFormat() { //设置左上角三角形
                    @Override
                    protected int getResourceID() {
                        return R.mipmap.excel_triangle;
                    }

                    @Override
                    protected Context getContext() {
                        return context;
                    }
                });
        //设置表格背景颜色
        table.getConfig().setContentCellBackgroundFormat(new BaseCellBackgroundFormat<CellInfo>() {
            @Override
            public int getBackGroundColor(CellInfo cellInfo) {
               if(cellInfo.data !=null) {
                   return getBackgroundColor(context, (T) cellInfo.data);
               }
               return TableConfig.INVALID_COLOR;
            }

        });
        table.setZoom(true,3,0.5f);
        //绘制选中区域
        table.setSelectFormat(new BaseSelectFormat());
        //增加批注
        FontStyle fontStyle = new FontStyle();
        fontStyle.setTextColor(context.getResources().getColor(android.R.color.white));
        MultiLineBubbleTip<Column> tip = new MultiLineBubbleTip<Column>(context,R.mipmap.round_rect,R.mipmap.triangle,fontStyle) {
            @Override
            public boolean isShowTip(Column column, int position) {
                T cell = (T) column.getDatas().get(position);
                if(cell !=null ){
                   return hasComment(cell);
                }
                return false;
            }


            @Override
            public String[] format(Column column, int position) {
                T cell = (T) column.getDatas().get(position);
                String comment = getComment(cell);
                return comment.split("\n");
            }
        };
        tip.setColorFilter(ContextCompat.getColor(context,R.color.column_bg));
        tip.setAlpha(0.9f);
        table.getProvider().setTip(tip);
    }

    public BaseExcel2Table() {
        this.ranges = new ArrayList<>();
    }


    protected String getFileName(){
        return fileName;
    }

    @Override
    public void setCallback(ExcelCallback excelCallback) {
        this.callback = excelCallback;
    }

    @Override
    public void loadSheetList(Context context,String fileName) {
        sheetAsyncTask = new SheetAsyncTask(context,callback);
        sheetAsyncTask.execute(fileName);
        this.fileName = fileName;
    }

    @Override
    public void loadSheetContent(Context context,int position) {

        excelAsyncTask = new ExcelAsyncTask<>(context);
        excelAsyncTask.execute(position);
    }

    @Override
    public void clear() {
        if(sheetAsyncTask !=null){
            sheetAsyncTask.cancel(true);
            sheetAsyncTask.callback = null;
        }
        if(excelAsyncTask !=null){
            excelAsyncTask.cancel(true);
            excelAsyncTask.softReference.clear();
            excelAsyncTask.softReference = null;
        }
        sheetAsyncTask = null;
        excelAsyncTask = null;
        callback = null;
    }

    //读取sheet
    public  class SheetAsyncTask extends AsyncTask<String,Void,List<String>>{
        private ExcelCallback callback;
        WeakReference<Context> softReference;
        public SheetAsyncTask(Context context,ExcelCallback callback){
            softReference=new WeakReference<>(context);
            this.callback = callback;
        }

            @Override
            protected List<String> doInBackground(String... fileName) {

                try {
                    if(softReference.get() !=null) {
                        return getSheetName(softReference.get(), fileName[0]);
                    }
                } catch(Exception e){
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<String> sheets) {
                if(callback !=null){
                    callback.getSheetListSuc(sheets);
                }
            }
        }
    public  abstract List<String> getSheetName(Context context,String fileName) throws Exception;
    protected abstract T[][] readExcelCell(Context context,String fileName,int position) throws Exception;
    protected  abstract int getFontSize(Context context,T t);
    protected abstract int getTextColor(Context context,T t);
    protected abstract int getBackgroundColor(Context context,T t);
    protected abstract String getFormat(T t);
    protected abstract Paint.Align getAlign(T t);
    protected abstract boolean hasComment(T t);
    protected abstract String getComment(T t);
    public abstract T[][]  getEmptyTableData();
    public void loadDataSuc(Context context){}

    /**
     * 获取输出流
     * @param context
     * @return
     * @throws IOException
     */
    public InputStream getInputStream(Context context,String fileName) throws IOException {
        InputStream is;
        if(isAssetsFile)
            is = context.getAssets().open(fileName);
        else
            is = new FileInputStream(fileName);
        return is;
    }


    @Override
    public void setIsAssetsFile(boolean isAssetsFile) {
        this.isAssetsFile = isAssetsFile;
    }

    public class ExcelAsyncTask<K> extends AsyncTask<Integer,Void, K[][]>{

        WeakReference<Context> softReference;


        public ExcelAsyncTask(Context context) {
            softReference=new WeakReference<>(context);
        }

        @Override
        protected K[][] doInBackground(Integer... position) {

            try {
                ranges.clear();
                return (K[][])readExcelCell(softReference.get(),fileName,position[0]);
            } catch (Exception e) {
            }

            return null;
        }

        @Override
        protected void onPostExecute(K[][] data) {
            if(callback !=null && softReference.get()!=null) {
                if(data ==null || data.length==0) {
                    data = (K[][])getEmptyTableData(); //美观
                }
                //设置字体
                ArrayTableData<K> tableData = ArrayTableData.create((SmartTable<K>) smartTable, "", data, new TextDrawFormat<K>() {

                    //Excel 因为每格的大小都不一样，所以需要重新计算高度和宽度
                    @Override
                    public int measureWidth(Column<K> column, int position, TableConfig config) {
                        if(softReference.get() == null){
                            return 0;
                        }
                        int width = 0;
                        K cell = column.getDatas().get(position);
                        if (cell != null) {
                            int fontSize = (int) (getFontSize(softReference.get(),(T) cell) * fontScale); //增加字体，效果更好看
                            config.getPaint().setTextSize(DensityUtils.sp2px(softReference.get(), fontSize));
                            width = DrawUtils.getMultiTextWidth(config.getPaint(),getSplitString(column.format(position)));
                        }
                        return width;
                    }

                    @Override
                    public int measureHeight(Column<K> column, int position, TableConfig config) {
                        if(softReference.get() == null){
                            return 0;
                        }
                        K cell = column.getDatas().get(position);
                        if (cell != null) {

                            int fontSize = (int) (getFontSize(softReference.get(),(T) cell) * fontScale); //增加字体，效果更好看
                            config.getPaint().setTextSize(DensityUtils.sp2px(softReference.get(), fontSize));
                            return DrawUtils.getMultiTextHeight(config.getPaint(), getSplitString(column.format(position)));
                        }
                        return super.measureHeight(column, position, config);
                    }

                    @Override
                    protected void drawText(Canvas c, String value, Rect rect, Paint paint) {
                        DrawUtils.drawMultiText(c, paint, rect, getSplitString(value));
                    }

                    @Override
                    public void setTextPaint(TableConfig config, CellInfo<K> cellInfo, Paint paint) {
                        if(softReference.get() == null){
                            return;
                        }
                        super.setTextPaint(config, cellInfo, paint);
                        if (cellInfo.data != null) {
                            config.getPaint().setTextAlign(getAlign((T) cellInfo.data));
                            int fontSize = (int) (getFontSize(softReference.get(),(T) cellInfo.data) * fontScale); //增加字体，效果更好看
                            config.getPaint().setTextSize(DensityUtils.sp2px(softReference.get(), fontSize)*config.getZoom());
                            paint.setColor(getTextColor(softReference.get(),(T) cellInfo.data));

                        }
                    }


                });
                if (ranges != null) {
                    tableData.setUserCellRange(ranges); //设置自定义规则
                }
                tableData.setFormat(new IFormat<K>() {
                    @Override
                    public String format(K cell) {
                        if (cell != null) {
                            return getFormat((T) cell);
                        }
                        return "";
                    }
                });
                int defaultCellSize = DensityUtils.dp2px(softReference.get(),30);
                tableData.setMinWidth(defaultCellSize);
                tableData.setMinWidth(defaultCellSize);
                loadDataSuc(softReference.get());
                smartTable.getMatrixHelper().reset();
                ((SmartTable<K>)smartTable).setTableData(tableData);


            }
        }
    }

    public List<CellRange> getRanges() {
        return ranges;
    }

    public float getFontScale() {
        return fontScale;
    }

    public void setFontScale(float fontScale) {
        this.fontScale = fontScale;
    }
}
