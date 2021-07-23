package pl.adrianremiza.demo.Controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.adrianremiza.demo.API.model.*;
import pl.adrianremiza.demo.API.modelCurrent.Example;
import pl.adrianremiza.demo.API.modelCurrent.TrackJsonCurrent;
import pl.adrianremiza.demo.Services.TrackService;
import pl.adrianremiza.demo.Services.TracksJsons;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalTime;
import java.util.*;

@Controller
@RestController

//@CrossOrigin(origins = "https://remizanowawies.herokuapp.com")
@CrossOrigin(origins = "http://localhost:4200")

public class SpotifyController {
    private String jwt;

    @Autowired
    TrackService trackService;

    @GetMapping("/authorize")
    public Principal authorize(Principal principal,OAuth2Authentication details){
        this.jwt = ((OAuth2AuthenticationDetails)details.getDetails()).getTokenValue();
        return principal;
    }

    @GetMapping("/album/{authorName}")
    public Tracks getAlbumsByAuthor(@PathVariable String authorName) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Tracks> exchange = restTemplate.exchange("https://api.spotify.com/v1/search?q="+ authorName + "&type=track&market=US&limit=10&offset=5",
                HttpMethod.GET,
                httpEntity,
                Tracks.class);
        return exchange.getBody();
    }

    @GetMapping("/player/devices")
    public String getCurrentDevices(){

        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<String> exchange = restTemplate.exchange("https://api.spotify.com/v1/me/player/devices",
                HttpMethod.GET,
                httpEntity,
                String.class);
        return exchange.getBody();
    }

    @GetMapping("/song/skip")
    public void skipCurrent(){
        this.trackService.setCounterSkipVote();
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity exchangePost =
                restTemplate.exchange("https://api.spotify.com/v1/me/player/next?device_id=8bf9ebb09ae56bea6ac31393315d97ef49581af5",
                        HttpMethod.POST,
                        httpEntity,
                        void.class);
    }

    @GetMapping("/song/getSongs")
    public List<TrackJson> getSongsFromPlaylist() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        List<TrackJson> trackJson = new ArrayList<>();
        for (int i = 0; i < 600; i=i+100) {
            ResponseEntity<Tracks> exchangePost =
                    restTemplate.exchange("https://api.spotify.com/v1/playlists/1Sq8XHC2XX5hKIlHmGbeAg/tracks?market=eS&fields=items(track(name%2Curi%2Calbum(images(url))))&limit=100&offset=" + i ,
                            HttpMethod.GET,
                            httpEntity,
                            Tracks.class);
            for (Item item : exchangePost.getBody().getItems()) {
                trackJson.add(new TrackJson(
                        item.getTrack().getName(),
                        item.getTrack().getUri(),
                        item.getTrack().getDurationMs(),
                        item.getTrack().getAlbum().getImages().get(0).getUrl()));
            }
        }
            return trackJson;
    }

    @PostMapping("/song")
    public void addSongToQueue(@RequestBody TrackJson trackJson) throws InterruptedException {
            if(trackService.getLastSong() == null) {
                this.trackService.setLastSong(trackJson);
                this.trackService.deleteTrack(trackJson.getName());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Bearer " + jwt);
                HttpEntity httpEntity = new HttpEntity(httpHeaders);
                ResponseEntity exchangePost =
                        restTemplate.exchange("https://api.spotify.com/v1/me/player/queue?uri=" + trackJson.getUri() + "&device_id=8bf9ebb09ae56bea6ac31393315d97ef49581af5",
                                HttpMethod.POST,
                                httpEntity,
                                void.class);
                Thread.sleep(2000);
                this.skipCurrent();
                this.trackService.setCounterSkipVote();
            }else if (LocalTime.now().toSecondOfDay()-8 >= this.trackService.getLastSong().getLocalTime().toSecondOfDay()) {
                this.trackService.setLastSong(trackJson);
                this.trackService.deleteTrack(trackJson.getName());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Bearer " + jwt);
                HttpEntity httpEntity = new HttpEntity(httpHeaders);
                ResponseEntity exchangePost =
                        restTemplate.exchange("https://api.spotify.com/v1/me/player/queue?uri=" + trackJson.getUri() + "&device_id=8bf9ebb09ae56bea6ac31393315d97ef49581af5",
                                HttpMethod.POST,
                                httpEntity,
                                void.class);
                Thread.sleep(2000);
                this.skipCurrent();
                this.trackService.setCounterSkipVote();
            }else{
                System.out.println("BŁĄD");
                this.trackService.setCounterSkipVote();
            }
    }

    @GetMapping("/song/current")
    public Serializable getSurrentSong(){

        try {
            RestTemplate restTemplate = new RestTemplate();
            HttpHeaders httpHeaders = new HttpHeaders();
            httpHeaders.add("Authorization", "Bearer " + jwt);
            HttpEntity<Track> httpEntity = new HttpEntity(httpHeaders);

            ResponseEntity<Example> exchangePost =
                    restTemplate.exchange("https://api.spotify.com/v1/me/player/currently-playing?market=ES",
                            HttpMethod.GET,
                            httpEntity,
                            Example.class);

            TrackJsonCurrent trackJson = new TrackJsonCurrent(
                    exchangePost.getBody().getItem().getName(),
                    exchangePost.getBody().getItem().getUri(),
                    exchangePost.getBody().getItem().getDurationMs(),
                    exchangePost.getBody().getProgressMs(),
                    exchangePost.getBody().getItem().getAlbum().getImages().get(0).getUrl()
            );
            return trackJson;
        } catch (Exception ex) {
            return null;
        }
    }

    @PostMapping("/queue")
    public void addSongToQueueTab(@RequestBody TrackJson trackJson){
        this.trackService.addToList(trackJson);
    }


    @GetMapping("/queue")
    public List<TracksJsons> getQueue(){
        return this.trackService.getTracksQueue();
    }
    @Transactional
    @DeleteMapping("/queue/{name}")
    public void deleteSongFromQueueTab(@PathVariable String name) {
        System.out.println(name);
        this.trackService.deleteTrack(name);
    }

    @PostMapping("/queue/vote")
    public Object addToCounterToSkipVote(@RequestBody int vote) throws InterruptedException {
            if(trackService.getCounterSkipVote()>1){
                if(this.trackService.getTracksQueue().size()==0){
                    this.skipCurrent();
                    this.trackService.setCounterSkipVote();
                }else{
                    for(int i = 0 ; i < 5 ; i++) {
                        this.addSongToQueue(this.trackService.getTracksQueue().get(0).getTrackJson());
                    }
                    this.trackService.setCounterSkipVote();
                }
            }else{
                this.trackService.addCounterSkipVote();
            }
        return getVotes();
    }
    @GetMapping("/queue/clearVote")
    public void clearSkip(){
        this.trackService.setCounterSkipVote();
    }

    @GetMapping("/queue/vote")
    public int getVotes(){
        return this.trackService.getCounterSkipVote();
    }


}
