
package pl.adrianremiza.demo.API.modelCurrent;

import java.util.HashMap;
import java.util.Map;
import javax.annotation.Generated;
import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder({
    "item",
    "currently_playing_type",
    "context",
    "is_playing",
    "progress_ms",
    "actions",
    "timestamp"
})
@Generated("jsonschema2pojo")
public class Example {

    @JsonProperty("item")
    private Item item;
    @JsonProperty("currently_playing_type")
    private String currentlyPlayingType;
    @JsonProperty("context")
    private Context context;
    @JsonProperty("is_playing")
    private Boolean isPlaying;
    @JsonProperty("progress_ms")
    private Integer progressMs;
    @JsonProperty("actions")
    private Actions actions;
    @JsonProperty("timestamp")
    private Long timestamp;
    @JsonIgnore
    private Map<String, Object> additionalProperties = new HashMap<String, Object>();

    @JsonProperty("item")
    public Item getItem() {
        return item;
    }

    @JsonProperty("item")
    public void setItem(Item item) {
        this.item = item;
    }

    @JsonProperty("currently_playing_type")
    public String getCurrentlyPlayingType() {
        return currentlyPlayingType;
    }

    @JsonProperty("currently_playing_type")
    public void setCurrentlyPlayingType(String currentlyPlayingType) {
        this.currentlyPlayingType = currentlyPlayingType;
    }

    @JsonProperty("context")
    public Context getContext() {
        return context;
    }

    @JsonProperty("context")
    public void setContext(Context context) {
        this.context = context;
    }

    @JsonProperty("is_playing")
    public Boolean getIsPlaying() {
        return isPlaying;
    }

    @JsonProperty("is_playing")
    public void setIsPlaying(Boolean isPlaying) {
        this.isPlaying = isPlaying;
    }

    @JsonProperty("progress_ms")
    public Integer getProgressMs() {
        return progressMs;
    }

    @JsonProperty("progress_ms")
    public void setProgressMs(Integer progressMs) {
        this.progressMs = progressMs;
    }

    @JsonProperty("actions")
    public Actions getActions() {
        return actions;
    }

    @JsonProperty("actions")
    public void setActions(Actions actions) {
        this.actions = actions;
    }

    @JsonProperty("timestamp")
    public Long getTimestamp() {
        return timestamp;
    }

    @JsonProperty("timestamp")
    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    @JsonAnyGetter
    public Map<String, Object> getAdditionalProperties() {
        return this.additionalProperties;
    }

    @JsonAnySetter
    public void setAdditionalProperty(String name, Object value) {
        this.additionalProperties.put(name, value);
    }

}
