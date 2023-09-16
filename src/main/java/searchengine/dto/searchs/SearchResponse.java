package searchengine.dto.searchs;

import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.util.List;
@Data
public class SearchResponse {
    boolean result;
    int count;
    List<SearchDto> data;
}
