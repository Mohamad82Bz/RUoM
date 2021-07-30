package me.Mohamad82.RUoM.worldedit;

import me.Mohamad82.RUoM.utils.ProgressBar;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

public class SchemProgress {

    DecimalFormat decimalFormat = new DecimalFormat("#.00");

    private List<Float> layerTimeTaken = new ArrayList<>();
    private boolean isDone = false;
    private boolean isFailed = true;
    private int maxLayers;
    private float progress = 0;
    private float timeTaken;

    public String getProgressBar(int total, String completeString, String notCompleteString) {
        return ProgressBar.getBar(Math.round(progress), 100, total, completeString, notCompleteString);
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void setFailed(boolean isFailed) {
        this.isFailed = isFailed;
    }

    protected void done() {
        isDone = true;
        isFailed = false;
    }

    public float getTimeTaken() {
        return timeTaken;
    }

    public List<Float> getLayerTimeTaken() {
        return layerTimeTaken;
    }

    protected void setLayerTimeTaken(List<Float> layerTimeTaken) {
        this.layerTimeTaken = layerTimeTaken;
    }

    protected void setProgress(float progress) {
        this.progress = progress;
    }

    public void addProgress() {
        progress++;
    }

    public float getProgress() {
        return progress;
    }

    protected void setTimeTaken(float timeTaken) {
        this.timeTaken = timeTaken;
    }

    public float getLayerTimeTaken(int layer) throws IndexOutOfBoundsException {
        return layerTimeTaken.get(layer);
    }

}
