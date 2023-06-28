package searchengine.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import searchengine.model.SiteEntity;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SiteRepository extends JpaRepository<SiteEntity, Integer> {
    @Transactional
    List<SiteEntity> findAllByUrl(String urlSite);
    @Transactional
    SiteEntity findByUrl(String urlSite);


}
