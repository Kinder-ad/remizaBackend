package pl.adrianremiza.demo.Controllers;

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
import pl.adrianremiza.demo.Services.TrackService;

import javax.annotation.PostConstruct;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

@Controller
@RestController

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
                restTemplate.exchange("https://api.spotify.com/v1/me/player/next?device_id=8bf9ebb09ae56bea6ac31393315d97ef49581af5",
                        HttpMethod.POST,
                        httpEntity,
                        void.class);
    }

    @GetMapping("/getSongs")
    public List<TrackJson> getSongsFromPlaylist(){
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);

        ResponseEntity<Tracks> exchangePost =
                restTemplate.exchange("https://api.spotify.com/v1/playlists/37i9dQZF1DX3HUaZJRcDLd/tracks?market=eS&fields=items(track(name%2Curi%2Calbum(images(url))))",
                        HttpMethod.GET,
                        httpEntity,
                        Tracks.class);
        List<TrackJson> trackJson = new ArrayList<>();
        for (Item item : exchangePost.getBody().getItems()) {
            trackJson.add(new TrackJson(
                    item.getTrack().getName(),
                    item.getTrack().getUri(),
                    item.getTrack().getDurationMs(),
                    item.getTrack().getAlbum().getImages().get(0).getUrl()));
        }
        return trackJson;
    }
    @GetMapping("/song/add/{songURI}")
    public void addSongToQueue(@PathVariable String songURI){
        System.out.println(songURI);
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add("Authorization", "Bearer " + jwt);
        HttpEntity httpEntity = new HttpEntity(httpHeaders);
        ResponseEntity exchangePost =
                restTemplate.exchange("https://api.spotify.com/v1/me/player/queue?uri="+songURI+"&device_id=8bf9ebb09ae56bea6ac31393315d97ef49581af5",
                        HttpMethod.POST,
                        httpEntity,
                        void.class);
    }

    @GetMapping("/song/current")
    public TrackJsonCurrent getSurrentSong(){
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
    }

    @PostMapping("/song")
    public void addSongToQueueTab(@RequestBody TrackJson trackJson){
        this.trackService.addToList(trackJson);
    }


    @GetMapping("/getQueue")
    public List<TrackJson> getQueue(){
        return this.trackService.getTracksQueue();
    }
    @Transactional
    @DeleteMapping("/song/queue/{name}")
    public void deleteSongFromQueueTab(@PathVariable String name) {
        System.out.println(name);
        this.trackService.deleteTrack(name);
    }

    @GetMapping("/song/queue/skipvote")
    public Object addToCounterToSkipVote(){
        if(this.trackService.getCounterSkipVote()<9){
            this.trackService.addCounterSkipVote();
            return getVotes();
        }else{
            this.skipCurrent();
            this.trackService.setCounterSkipVote();
            return getVotes();
        }

    }
    @GetMapping("/song/queue/getvote")
    public int getVotes(){
        return this.trackService.getCounterSkipVote();
    }

}
