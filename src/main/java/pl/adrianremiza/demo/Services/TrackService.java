package pl.adrianremiza.demo.Services;

import org.springframework.stereotype.Service;
import pl.adrianremiza.demo.API.model.TrackJson;
import pl.adrianremiza.demo.API.modelCurrent.SongLast;

import java.util.*;

@Service
public class TrackService {
    private int counterSkipVote;
    private List<TracksJsons> tracksQueue;
    private SongLast lastSong;
    public TrackService() {
        this.tracksQueue = new ArrayList<>();
        this.counterSkipVote = 0;
    }



    public SongLast getLastSong() {
        return lastSong;
    }

    public void setLastSong(TrackJson lastSong) {
        this.lastSong = new SongLast(lastSong);
    }

    public void setCounterSkipVote(int counterSkipVote) {
        this.counterSkipVote = counterSkipVote;
    }



    public List<TracksJsons> getTracksQueue() {
        return this.tracksQueue;
    }
    public void addToList(TrackJson track){
        if(this.tracksQueue.size()<10)
        this.tracksQueue.add(new TracksJsons(track));
    }

    public void deleteTrack(String nameTrack){
        TracksJsons track = tracksQueue.stream()
                .filter(trackQueue -> nameTrack.equals(trackQueue.getTrackJson().getName()))
                .findAny()
                .orElse(null);
        System.out.println(track.toString());
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

    public void addVoteToSong(String name) {
        TracksJsons track = tracksQueue.stream()
                .filter(trackQueue -> name.equals(trackQueue.getTrackJson().getName()))
                .findAny()
                .orElse(null);
        int a = this.tracksQueue.indexOf(track);
        this.tracksQueue.get(a).add_glos();
        this.tracksQueue.stream().forEach(System.out::println);
    }
    public int getVoteFromSong(String name) {
        TracksJsons track = tracksQueue.stream()
                .filter(trackQueue -> name.equals(trackQueue.getTrackJson().getName()))
                .findAny()
                .orElse(null);
        return track.getIle_glosow();
    }
}
