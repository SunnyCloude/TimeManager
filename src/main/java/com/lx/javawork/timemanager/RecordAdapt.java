package com.lx.javawork.timemanager;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class RecordAdapt extends BaseAdapter {

    String[][] listItems;
    private Context mContext;
    private LayoutInflater mLayoutInflater;

    public RecordAdapt(Context context){
        this.mContext=context;
        this.mLayoutInflater=LayoutInflater.from(mContext);
        listItems = new FileOperation().TomatoFileRead("TomatoRecords.txt",context);
    }
    @Override
    public int getCount() {
        if(listItems!=null)
            return listItems.length;
        else
            return 0;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    static class ViewHolder{
        public TextView tvwork,tvrest,tvcircle,tvdate,tvindex;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder=null;
        if(convertView==null){
            convertView=mLayoutInflater.inflate(R.layout.layout_timemanager4_item,null);
            viewHolder=new ViewHolder();
            viewHolder.tvwork=convertView.findViewById(R.id.recordwork);
            viewHolder.tvrest=convertView.findViewById(R.id.recordrest);
            viewHolder.tvcircle=convertView.findViewById(R.id.recordcircle);
            viewHolder.tvdate=convertView.findViewById(R.id.recorddate);
            viewHolder.tvindex=convertView.findViewById(R.id.index);
            convertView.setTag(viewHolder);
        }else{
            viewHolder= (ViewHolder) convertView.getTag();
        }
        viewHolder.tvindex.setText((position+1)+"");
        viewHolder.tvwork.setText("工作:"+listItems[position][1]+"m");
        viewHolder.tvrest.setText("休息:"+listItems[position][2]+"m");
        viewHolder.tvcircle.setText("循环:"+listItems[position][3]+"次");
        String[] time=listItems[position][0].split("\\.");
        if(time.length==4){
            viewHolder.tvdate.setText(time[0] + "月" + time[1] + "日 " + time[2] + ":"+ time[3]);
        }else {
            viewHolder.tvdate.setText(time[0] + "月" + time[1] + "日");
        }
        return convertView;
    }
}
