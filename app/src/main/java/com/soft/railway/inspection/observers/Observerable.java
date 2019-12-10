package com.soft.railway.inspection.observers;

public interface Observerable {
    void registerObserver(Observer o);
    void removeObserver(Observer o);
    void notifyObserver();
}
