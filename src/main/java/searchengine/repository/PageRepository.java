package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
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
}
