package me.kyllian.xRay.tasks;

import java.util.ArrayList;
import java.util.List;

public interface Task {

    void send();
    void restore(List<?> toRestore);
    void update();

    TaskType getType();
    ArrayList<?> getRunning();

}
