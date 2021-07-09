package pl.adrianremiza.demo.Services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pl.adrianremiza.demo.API.model.Track;
import pl.adrianremiza.demo.API.model.TrackJson;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class TrackService {
    private int counterSkipVote;
    private List<TrackJson> tracksQueue;

    public TrackService() {
        this.tracksQueue = new ArrayList<>();
        this.counterSkipVote = 0;
    }

    public List<TrackJson> getTracksQueue() {
        return tracksQueue;
    }
    public void addToList(TrackJson track){
        if(this.tracksQueue.size()<10)
        this.tracksQueue.add(track);

    }

    public void setTracksQueue(List<TrackJson> tracksQueue) {
        this.tracksQueue = tracksQueue;
    }
    public void deleteTrack(String nameTrack){
        TrackJson track = tracksQueue.stream()
                .filter(trackQueue -> nameTrack.equals(trackQueue.getName()))
                .findAny()
                .orElse(null);
        this.tracksQueue.remove(track);

    }

    public int getCounterSkipVote() {
        return counterSkipVote;
    }

    public void addCounterSkipVote() {
        this.counterSkipVote = counterSkipVote+1;
    }
    public void setCounterSkipVote() {
        this.counterSkipVote = 0;
    }
}