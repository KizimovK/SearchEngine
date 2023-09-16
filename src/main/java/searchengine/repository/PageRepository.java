package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.LemmaEntity;
import searchengine.model.PageEntity;
import searchengine.model.SiteEntity;

import java.util.List;

@Repository
public interface PageRepository extends JpaRepository<PageEntity, Integer> {
    @Transactional
    void deleteAllBySiteEntity(SiteEntity siteEntity);
    @Transactional
    void deleteBySiteEntity(SiteEntity site);
    @Transactional
    List<PageEntity> findAllBySiteEntity(SiteEntity site);
    @Transactional
    PageEntity findByPathAndSiteEntity(String path, SiteEntity siteEntity);
    @Transactional
    int countBySiteEntity(SiteEntity siteEntity);
    @Transactional
    @Query(value = "SELECT p FROM PageEntity p " +
            "JOIN IndexEntity i ON p = i.pageEntity WHERE i.lemmaEntity IN :lemmaEntityList")
    List<PageEntity> findPagesByLemmas(@Param("lemmaEntityList")List<LemmaEntity> lemmaEntityList);
}
