package com.example.bookrecommender.dto.openlibrary;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenLibrarySearchResponse {
    
    @JsonProperty("numFound")
    private Integer numFound;
    
    @JsonProperty("start")
    private Integer start;
    
    @JsonProperty("docs")
    private List<OpenLibraryBook> docs;
}
