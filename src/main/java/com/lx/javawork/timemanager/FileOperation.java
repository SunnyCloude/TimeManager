package com.lx.javawork.timemanager;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileOperation {
    public boolean FileSave(String fileName, Context context, String content){

        FileOutputStream fileOutputStream=null;
        try {
            File dir =new File(Environment.getExternalStorageDirectory(),"TimeManager");
            if(!dir.exists()){
                dir.mkdirs();
            }
            File file=new File(dir,fileName);
            if(!file.exists()){
                try {
                    file.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            fileOutputStream=new FileOutputStream(file,true);
//            fileOutputStream=context.openFileOutput(fileName,Context.MODE_PRIVATE);
            fileOutputStream.write(content.getBytes());
            return true;
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileOutputStream!=null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (NullPointerException e) {
                    e.printStackTrace();
                }
            }
        }
        return false;
    }

    public String FileRead(String fileName,Context context,String key){
        FileInputStream fileInputStream=null;
        try {
            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"TimeManager",fileName);
            fileInputStream=new FileInputStream(file);
            byte[] buffer=new byte[1024];
            StringBuffer sb=new StringBuffer("");
            int lenth=0;
            while((lenth=fileInputStream.read(buffer))>0){
                sb.append(new String(buffer,0,lenth));
            }
            String[] records=sb.toString().split(",");
            for(int i=records.length-1;i>=0;i--){
                String[] record=records[i].split(":");
                if(record[0].equals(key)){
                    return record[1];
                }
            }
            return "0";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "0";
    }

    public String[][] TomatoFileRead(String fileName,Context context){
        FileInputStream fileInputStream=null;
        String[][] listItems=new String[][]{};
        try {
            //读取原数据
            File file=new File(Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"TimeManager",fileName);
            fileInputStream=new FileInputStream(file);
            byte[] buffer=new byte[1024];
            StringBuffer sb=new StringBuffer("");
            int lenth=0;
            while((lenth=fileInputStream.read(buffer))>0){
                sb.append(new String(buffer,0,lenth));
            }
            //处理数据
            String[] records=sb.toString().split(",");
            listItems=new String[records.length][];
            for(int i=0;i<records.length;i++){
                listItems[i]=records[records.length-1-i].split(":");
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            if(fileInputStream!=null) {
                try {
                    fileInputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        if(listItems!=null){
            return listItems;
        }else{
            return null;
        }
    }
}
