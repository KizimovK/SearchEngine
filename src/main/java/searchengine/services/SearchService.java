package searchengine.services;

import searchengine.dto.searchs.SearchResponse;

public interface SearchService {

    SearchResponse getResponseSearch(String query, String site, int offset, int limit);

}
