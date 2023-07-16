package searchengine.mapping;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import searchengine.dto.data.LemmaDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.LemmaEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

@Component
public class LemmaMapperImpl implements LemmaMapper {
    private final SiteMapper siteMapper;

    @Autowired
    public LemmaMapperImpl(SiteMapper siteMapper) {
        this.siteMapper = siteMapper;
    }

    @Override
    public LemmaDto toLemmaDto(LemmaEntity lemmaEntity) {
        if (lemmaEntity == null) {
            return null;
        } else {
            LemmaDto lemmaDto = new LemmaDto();
            lemmaDto.setId(lemmaEntity.getId());
            lemmaDto.setLemma(lemmaEntity.getLemma());
            lemmaDto.setSiteDto(siteMapper.toSiteDto(lemmaEntity.getSiteEntity()));
            lemmaDto.setFrequency(lemmaEntity.getFrequency());
            return lemmaDto;
        }
    }

    @Override
    public LemmaEntity toLemmaEntity(LemmaDto lemmaDto) {
        if (lemmaDto == null) {
            return null;
        } else {
            LemmaEntity lemmaEntity = new LemmaEntity();
            lemmaEntity.setId(lemmaDto.getId());
            lemmaEntity.setLemma(lemmaDto.getLemma());
            lemmaEntity.setSiteEntity(siteMapper.toSiteEntity(lemmaDto.getSiteDto()));
            lemmaEntity.setFrequency(lemmaDto.getFrequency());
            return lemmaEntity;
        }
    }

    @Override
    public List<LemmaDto> toListLemmaDto(List<LemmaEntity> lemmaEntityList) {
        if (lemmaEntityList == null) {
            return null;
        } else {
            List<LemmaDto> lemmaDtoList = new ArrayList<>(lemmaEntityList.size());
            Iterator var3 = lemmaEntityList.iterator();
            while (var3.hasNext()) {
                LemmaEntity lemmaEntity = (LemmaEntity) var3.next();
                lemmaDtoList.add(this.toLemmaDto(lemmaEntity));
            }
            return lemmaDtoList;
        }
    }

    @Override
    public List<LemmaEntity> toListLemmaEntity(List<LemmaDto> lemmaDtoList) {
        if (lemmaDtoList == null) {
            return null;
        } else {
            List<LemmaEntity> lemmaEntityList = new ArrayList<>(lemmaDtoList.size());
            Iterator var3 = lemmaDtoList.iterator();
            while (var3.hasNext()) {
                LemmaDto lemmaDto = (LemmaDto) var3.next();
                lemmaEntityList.add(this.toLemmaEntity(lemmaDto));
            }
            return lemmaEntityList;
        }
    }

    @Override
    public List<LemmaDto> toListLemmaDto(HashMap<String, Integer> mapLemmas, SiteDto siteDto) {
        if (mapLemmas == null) {
            return null;
        } else {
            List<LemmaDto> lemmaDtoList = new ArrayList<>(mapLemmas.size());
            mapLemmas.keySet().forEach(lemma -> {
                LemmaDto lemmaDto = new LemmaDto();
                lemmaDto.setLemma(lemma);
                lemmaDto.setSiteDto(siteDto);
                lemmaDto.setFrequency(mapLemmas.get(lemma));
                lemmaDtoList.add(lemmaDto);
            });
            return lemmaDtoList;
        }
    }

    @Override
    public List<LemmaEntity> toListLemmaEntity(HashMap<String, Integer> mapLemmas, SiteDto siteDto) {
        if (mapLemmas == null) {
            return null;
        } else {
            List<LemmaEntity> lemmaEntityList = new ArrayList<>(mapLemmas.size());
            mapLemmas.keySet().forEach(lemma -> {
                LemmaEntity lemmaEntity = new LemmaEntity();
                lemmaEntity.setLemma(lemma);
                lemmaEntity.setSiteEntity(siteMapper.toSiteEntity(siteDto));
                lemmaEntity.setFrequency(mapLemmas.get(lemma));
                lemmaEntityList.add(lemmaEntity);
            });
            return lemmaEntityList;
        }
    }


}
