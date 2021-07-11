package pl.adrianremiza.demo.API.modelCurrent;

import pl.adrianremiza.demo.API.model.TrackJson;

import java.time.LocalTime;

public class SongLast {
    private TrackJson trackJson;
    private LocalTime localTime;

    public SongLast(TrackJson trackJson) {
        this.trackJson = trackJson;
        this.localTime = LocalTime.now();
    }

    public TrackJson getTrackJson() {
        return trackJson;
    }

    public void setTrackJson(TrackJson trackJson) {
        this.trackJson = trackJson;
    }

    public LocalTime getLocalTime() {
        return localTime;
    }

    public void setLocalTime(LocalTime localTime) {
        this.localTime = localTime;
    }
}
