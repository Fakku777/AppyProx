/*
 * Decompiled with CFR 0.152.
 */
package xaero.map;

import java.util.ArrayList;
import xaero.map.MapProcessor;
import xaero.map.WorldMapSession;
import xaero.map.task.MapRunnerTask;

public class MapRunner
implements Runnable {
    private boolean stopped;
    private ArrayList<MapRunnerTask> tasks = new ArrayList();

    @Override
    public void run() {
        while (!this.stopped) {
            WorldMapSession worldmapSession = WorldMapSession.getCurrentSession();
            if (worldmapSession != null && worldmapSession.isUsable()) {
                MapProcessor mapProcessor = worldmapSession.getMapProcessor();
                mapProcessor.run(this);
            } else {
                this.doTasks(null);
            }
            try {
                Thread.sleep(100L);
            }
            catch (InterruptedException interruptedException) {}
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void doTasks(MapProcessor mapProcessor) {
        while (!this.tasks.isEmpty()) {
            MapRunnerTask task;
            ArrayList<MapRunnerTask> arrayList = this.tasks;
            synchronized (arrayList) {
                if (this.tasks.isEmpty()) {
                    break;
                }
                task = this.tasks.remove(0);
            }
            task.run(mapProcessor);
        }
    }

    /*
     * WARNING - Removed try catching itself - possible behaviour change.
     */
    public void addTask(MapRunnerTask task) {
        ArrayList<MapRunnerTask> arrayList = this.tasks;
        synchronized (arrayList) {
            this.tasks.add(task);
        }
    }

    public void stop() {
        this.stopped = true;
    }
}

