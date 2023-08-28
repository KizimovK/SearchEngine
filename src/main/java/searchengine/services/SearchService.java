package searchengine.services;

import searchengine.dto.searchs.SearchDto;
import searchengine.dto.searchs.SearchResponse;

import java.util.List;

public interface SearchService {
    SearchResponse getResponseSearchQuery(String query, String site, int offset, int limit);

}
