package com.soft.railway.inspection.utils;

import com.soft.railway.inspection.observers.Observer;
import com.soft.railway.inspection.observers.Observerable;
import java.util.ArrayList;
import java.util.List;

public class WorkObserverableUtil implements Observerable {
    private List<Observer> list;
    private static WorkObserverableUtil workObserverableUtil;

    private WorkObserverableUtil() {
        list=new ArrayList<>();
    }

    public static WorkObserverableUtil getInstance(){
        if(workObserverableUtil==null){
            synchronized(WorkObserverableUtil.class){
                if(workObserverableUtil==null){
                    workObserverableUtil=new WorkObserverableUtil();
                }
            }
        }
        return workObserverableUtil;
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
