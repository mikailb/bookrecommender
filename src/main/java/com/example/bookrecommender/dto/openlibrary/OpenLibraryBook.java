package com.example.bookrecommender.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibraryBook {
    
    @JsonProperty("title")
    private String title;
    
    @JsonProperty("author_name")
    private List<String> authorName;
    
    @JsonProperty("isbn")
    private List<String> isbn;
    
    @JsonProperty("subject")
    private List<String> subject;
    
    @JsonProperty("first_sentence")
    private List<String> firstSentence;
    
    @JsonProperty("cover_i")
    private Long coverId;
    
    @JsonProperty("first_publish_year")
    private Integer firstPublishYear;
    
    @JsonProperty("publisher")
    private List<String> publisher;
}
