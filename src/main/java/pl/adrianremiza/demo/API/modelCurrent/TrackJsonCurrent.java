package pl.adrianremiza.demo.API.modelCurrent;

import java.io.Serializable;

public class TrackJsonCurrent implements Serializable {
    private String name;
    private String uri;
    private int durationMs;
    private int progressMs;
    private String url;


    public TrackJsonCurrent(String name, String uri, int progressMs, int durationMs, String url) {
        this.name = name;
        this.uri = uri;
        this.durationMs = durationMs;
        this.url = url;
        this.progressMs = progressMs;
    }

    public int getProgressMs() {
        return progressMs;
    }

    public void setProgressMs(int progressMs) {
        this.progressMs = progressMs;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public int getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(int durationMs) {
        this.durationMs = durationMs;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }
}
