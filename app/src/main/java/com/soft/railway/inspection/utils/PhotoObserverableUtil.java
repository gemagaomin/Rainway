package com.soft.railway.inspection.utils;

import com.soft.railway.inspection.observers.Observer;
import com.soft.railway.inspection.observers.Observerable;
import java.util.ArrayList;
import java.util.List;

public class PhotoObserverableUtil implements Observerable {
    private List<Observer> list;
    private static PhotoObserverableUtil photoObserverableUtil;

    private PhotoObserverableUtil() {
        list=new ArrayList<>();
    }

    public static PhotoObserverableUtil getInstance(){
        if(photoObserverableUtil==null){
            synchronized(PhotoObserverableUtil.class){
                if(photoObserverableUtil==null){
                    photoObserverableUtil=new PhotoObserverableUtil();
                }
            }
        }
        return photoObserverableUtil;
    }

    @Override
    public void registerObserver(Observer o) {
        list.add(o);
    }

    @Override
    public void removeObserver(Observer o) {
        if(!list.isEmpty()){
            list.remove(o);
        }
    }

    @Override
    public void notifyObserver() {
        for (Observer o:list
             ) {
            o.refreshView();
        }
    }

    public void refresh(){
        notifyObserver();
    }
}
