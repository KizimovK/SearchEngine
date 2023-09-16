package searchengine.services;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import searchengine.companets.ExtractLemma;
import searchengine.dto.data.SiteDto;
import searchengine.mapping.SiteMapper;
import searchengine.model.LemmaEntity;
import searchengine.model.SiteEntity;
import searchengine.repository.IndexRepository;
import searchengine.repository.LemmaRepository;
import searchengine.repository.SiteRepository;

import javax.persistence.EntityManagerFactory;
import java.util.List;

@Service
@Slf4j
public class LemmaIndexServiceImpl implements LemmaIndexService {

   private final ExtractLemma extractLemma;
    private final LemmaRepository lemmaRepository;
    private final IndexRepository indexRepository;
    private final SiteMapper siteMapper;
    private final SiteRepository siteRepository;
    private  EntityManagerFactory entityManagerFactory;



    public LemmaIndexServiceImpl(ExtractLemma extractLemma, LemmaRepository lemmaRepository,
                                 IndexRepository indexRepository,
                                 SiteMapper siteMapper,
                                 SiteRepository siteRepository, EntityManagerFactory entityManagerFactory) {
        this.extractLemma = extractLemma;
        this.lemmaRepository = lemmaRepository;
        this.indexRepository = indexRepository;
        this.siteMapper = siteMapper;
        this.siteRepository = siteRepository;
        this.entityManagerFactory = entityManagerFactory;
    }

    @Override
    public synchronized int updateLemma(int idLemma) {
        lemmaRepository.updateLemma(idLemma);
        return idLemma;
    }
    @Override
    public synchronized int newLemmaSave(String lemma, SiteEntity siteEntity){
        LemmaEntity lemmaEntity = new LemmaEntity();
        lemmaEntity.setSiteEntity(siteEntity);
        lemmaEntity.setFrequency(1);
        lemmaEntity.setLemma(lemma);
        lemmaEntity = lemmaRepository.save(lemmaEntity);
        return lemmaEntity.getId();
    }

    @Override
    public synchronized void indexSave(int idLemma, int idPage, float rank) {
        indexRepository.insertIndex(idPage,idLemma,rank);
    }

    @Override
    public List<LemmaEntity> findAllLemmas(SiteDto siteDto) {
        lemmaRepository.flush();
        List<LemmaEntity> lemmaEntityList = lemmaRepository.findAllBySiteEntity(siteMapper.toSiteEntity(siteDto));
        return lemmaEntityList;
    }


}
