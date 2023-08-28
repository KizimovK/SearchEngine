package searchengine.dto.searchs;

import lombok.Data;

import java.util.List;
@Data
public class SearchResponse {
    boolean result;
    int count;
    List<SearchDto> data;
}
