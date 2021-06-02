package me.Mohamad82.RUoM.WorldEdit;

import java.util.ArrayList;
import java.util.List;

public class SchemProgress {

    private List<Float> layerTimeTaken;
    private boolean isDone;
    private boolean isFailed;
    private int maxLayers;
    private float progress;
    private float timeTaken;

    public SchemProgress() {
        isDone = false;
        isFailed = true;
        progress = 0;
        layerTimeTaken = new ArrayList<>();
    }

    public boolean isDone() {
        return isDone;
    }

    public boolean isFailed() {
        return isFailed;
    }

    public void done() {
        isDone = true;
        isFailed = false;
    }

    public float getTimeTaken() {
        return timeTaken;
    }

    public List<Float> getLayerTimeTaken() {
        return layerTimeTaken;
    }

    public void setLayerTimeTaken(List<Float> layerTimeTaken) {
        this.layerTimeTaken = layerTimeTaken;
    }

    public void setProgress(float progress) {
        this.progress = progress;
    }

    public void addProgress() {
        progress++;
    }

    public float getProgress() {
        return progress;
    }

    public void setTimeTaken(float timeTaken) {
        this.timeTaken = timeTaken;
    }

    public float getLayerTimeTaken(int layer) throws IndexOutOfBoundsException {
        return layerTimeTaken.get(layer);
    }

}
