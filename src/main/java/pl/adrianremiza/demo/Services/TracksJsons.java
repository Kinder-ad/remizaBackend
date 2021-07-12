package pl.adrianremiza.demo.Services;

import pl.adrianremiza.demo.API.model.TrackJson;

public class TracksJsons {
    private TrackJson trackJson;
    private int ile_glosow;

    public TracksJsons(TrackJson trackJson) {
        this.trackJson = trackJson;
        this.ile_glosow = 0;
    }

    public TrackJson getTrackJson() {
        return trackJson;
    }

    public void setTrackJson(TrackJson trackJson) {
        this.trackJson = trackJson;
    }

    public int getIle_glosow() {
        return ile_glosow;
    }

    public void setIle_glosow(int ile_glosow) {
        this.ile_glosow = ile_glosow;
    }
    public void add_glos(){
        this.ile_glosow += 1;
    }

    @Override
    public String toString() {
        return "TracksJsons{" +
                "trackJson=" + trackJson.getName() +
                ", ile_glosow=" + ile_glosow +
                '}';
    }
}
