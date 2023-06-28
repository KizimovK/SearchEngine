package searchengine.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.data.IndexDto;
import searchengine.model.IndexEntity;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

@Component
public class IndexMapperImpl implements IndexMapper {
    private final PageMapper pageMapper;
    private final LemmaMapper lemmaMapper;

    @Autowired
    public IndexMapperImpl(PageMapper pageMapper, LemmaMapper lemmaMapper) {
        this.pageMapper = pageMapper;
        this.lemmaMapper = lemmaMapper;
    }

    @Override
    public IndexEntity toIndexEntity(IndexDto indexDto) {
        if (indexDto == null) {
            return null;
        } else {
            IndexEntity indexEntity = new IndexEntity();
            indexEntity.setId(indexDto.getId());
            indexEntity.setPageEntity(pageMapper.toPageEntity(indexDto.getPageDto()));
            indexEntity.setLemmaEntity(lemmaMapper.toLemmaEntity(indexDto.getLemmaDto()));
            indexEntity.setRank(indexDto.getRank());
            return indexEntity;
        }
    }

    @Override
    public IndexDto toIndexDto(IndexEntity indexEntity) {
        if (indexEntity == null) {
            return null;
        } else {
            IndexDto indexDto = new IndexDto();
            indexDto.setId(indexEntity.getId());
            indexDto.setPageDto(pageMapper.toPageDto(indexEntity.getPageEntity()));
            indexDto.setLemmaDto(lemmaMapper.toLemmaDto(indexEntity.getLemmaEntity()));
            indexDto.setRank(indexEntity.getRank());
            return indexDto;
        }
    }

    @Override
    public List<IndexEntity> toListIndexEntity(List<IndexDto> indexDtoList) {
        if (indexDtoList == null) {
            return null;
        } else {
            List<IndexEntity> indexEntityList = new ArrayList<>(indexDtoList.size());
            Iterator var3 = indexDtoList.iterator();
            while (var3.hasNext()) {
                IndexDto indexDto = (IndexDto) var3.next();
                indexEntityList.add(this.toIndexEntity(indexDto));
            }
            return indexEntityList;
        }
    }

    @Override
    public List<IndexDto> toListIndexDto(List<IndexEntity> indexEntityList) {
        if (indexEntityList == null) {
            return null;
        } else {
            List<IndexDto> indexDtoList = new ArrayList<>(indexEntityList.size());
            Iterator var3 = indexEntityList.iterator();
            while (var3.hasNext()) {
                IndexEntity indexEntity = (IndexEntity)  var3.next();
                indexDtoList.add(this.toIndexDto(indexEntity));
            }
            return indexDtoList;
        }
    }
}
