package pl.adrianremiza.demo.Controllers;

import com.wrapper.spotify.SpotifyApi;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.authentication.OAuth2AuthenticationDetails;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import pl.adrianremiza.demo.API.model.*;
import pl.adrianremiza.demo.API.modelCurrent.Example;
import pl.adrianremiza.demo.API.modelCurrent.TrackJsonCurrent;
import pl.adrianremiza.demo.Exception.ForbiddenException;
import pl.adrianremiza.demo.Services.TrackService;
import pl.adrianremiza.demo.Services.TracksJsons;

import java.io.Serializable;
import java.security.Principal;
import java.time.LocalTime;
import java.util.*;

@Controller
@RestController

//@CrossOrigin(origins = "https://remiza-front-app.herokuapp.com")
@CrossOrigin(origins = "http://localhost:4200")

public class SpotifyController {
    private String jwt;

    @Autowired
    TrackService trackService;

    @GetMapping("/aut")
    public Principal autoryzuj(Principal principal,OAuth2Authentication details){
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

    @GetMapping("/skip")
    public void skipCurrent(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity exchangePost =
                restTemplate.exchange("https://api.spotify.com/v1/me/player/next?device_id=fb1c077efb4cdba435f1426ba0af0ec4c0c94387",
                        HttpMethod.POST,
                        httpEntity,
                        void.class);
    }

    @GetMapping("/getSongs")
    public List<TrackJson> getSongsFromPlaylist() {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        List<TrackJson> trackJson = new ArrayList<>();
        for (int i = 0; i < 600; i=i+100) {
            ResponseEntity<Tracks> exchangePost =
                    restTemplate.exchange("https://api.spotify.com/v1/playlists/4cb63SLdvSFWAmjz1uzHdd/tracks?market=eS&fields=items(track(name%2Curi%2Calbum(images(url))))&limit=100&offset=" + i ,
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

    @PostMapping("/song/add")
    public void addSongToQueue(@RequestBody TrackJson trackJson){
            if(trackService.getLastSong() == null) {
                this.trackService.setLastSong(trackJson);
                this.trackService.deleteTrack(trackJson.getName());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Bearer " + jwt);
                HttpEntity httpEntity = new HttpEntity(httpHeaders);
                ResponseEntity exchangePost =
                        restTemplate.exchange("https://api.spotify.com/v1/me/player/queue?uri=" + trackJson.getUri() + "&device_id=fb1c077efb4cdba435f1426ba0af0ec4c0c94387",
                                HttpMethod.POST,
                                httpEntity,
                                void.class);
            }else if (LocalTime.now().toSecondOfDay()-10 >= this.trackService.getLastSong().getLocalTime().toSecondOfDay()) {
                this.trackService.setLastSong(trackJson);
                this.trackService.deleteTrack(trackJson.getName());
                RestTemplate restTemplate = new RestTemplate();
                HttpHeaders httpHeaders = new HttpHeaders();
                httpHeaders.add("Authorization", "Bearer " + jwt);
                HttpEntity httpEntity = new HttpEntity(httpHeaders);
                ResponseEntity exchangePost =
                        restTemplate.exchange("https://api.spotify.com/v1/me/player/queue?uri=" + trackJson.getUri() + "&device_id=fb1c077efb4cdba435f1426ba0af0ec4c0c94387",
                                HttpMethod.POST,
                                httpEntity,
                                void.class);
            }else{
                System.out.println("BŁĄD");
            }
        System.out.println(this.trackService.getLastSong().getTrackJson().getName());
        System.out.println(LocalTime.now().toSecondOfDay()-60);
        System.out.println(this.trackService.getLastSong().getLocalTime().toSecondOfDay());
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

    @PostMapping("/song")
    public void addSongToQueueTab(@RequestBody TrackJson trackJson){
        this.trackService.addToList(trackJson);
    }


    @GetMapping("/getQueue")
    public List<TracksJsons> getQueue(){
        return this.trackService.getTracksQueue();
    }
    @Transactional
    @DeleteMapping("/song/queue/{name}")
    public void deleteSongFromQueueTab(@PathVariable String name) {
        System.out.println(name);
        this.trackService.deleteTrack(name);
    }

    @GetMapping("/song/queue/skipvote")
    public Object addToCounterToSkipVote() throws InterruptedException {
        System.out.println("counterVote: "+trackService.getCounterSkipVote());
            if(trackService.getCounterSkipVote()>=2){
                System.out.println(trackService.getCounterSkipVote()+"2");
                if(this.trackService.getTracksQueue().size()==0){
                    System.out.println(trackService.getCounterSkipVote()+"3");
                    this.skipCurrent();
                    this.trackService.setCounterSkipVote();
                }else{
                    System.out.println(trackService.getCounterSkipVote()+"4");
                    this.addSongToQueue(this.trackService.getTracksQueue().get(0).getTrackJson());
                    Thread.sleep(1);
                    this.skipCurrent();
                    this.trackService.setCounterSkipVote();
                }
            }else{
                System.out.println(trackService.getCounterSkipVote()+"5");
                this.trackService.addCounterSkipVote();
            }
//        if(this.trackService.getCounterSkipVote()<2){
//            if(this.trackService.getCounterSkipVote()<2) {
//                this.trackService.addCounterSkipVote();
//                return getVotes();
//            }else {
//                return getVotes();
//            }
//        }else {
//            if(this.trackService.getTracksQueue().size()==0){
//                this.skipCurrent();
//                this.trackService.setCounterSkipVote();
//                return getVotes();
//            }else {
//                this.addSongToQueue(this.trackService.getTracksQueue().get(0).getTrackJson());
//                this.trackService.deleteTrack(this.trackService.getTracksQueue().get(0).getTrackJson().getName());
//                Thread.sleep(2);
//                this.skipCurrent();
//                this.trackService.setCounterSkipVote();
//                return getVotes();
//            }
//        }
        return getVotes();
    }
    @GetMapping("/song/queue/clearVote")
    public void clearSkip(){
        this.trackService.setCounterSkipVote();
    }

    @GetMapping("/song/queue/getvote")
    public int getVotes(){
        return this.trackService.getCounterSkipVote();
    }
    @GetMapping("/song/queue/{name}")
    public void addVoteToSong(@PathVariable String name){
        this.trackService.addVoteToSong(name);
    }


}
