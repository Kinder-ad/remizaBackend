package pl.adrianremiza.demo.API.model;

import java.io.Serializable;

public class TrackJson implements Serializable {
    private String name;
    private String uri;
    private int durationMs;
    private String url;

    public TrackJson(String name, String uri, int durationMs, String url) {
        this.name = name;
        this.uri = uri;
        this.durationMs = durationMs;
        this.url = url;
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

    @Override
    public String toString() {
        return "TrackJson{" +
                "name='" + name + '\'' +
                ", uri='" + uri + '\'' +
                ", durationMs=" + durationMs +
                ", url='" + url + '\'' +
                '}';
    }
}
