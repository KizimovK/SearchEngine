package searchengine.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.config.ConfigOptions;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.SiteEntity;
import searchengine.model.StatusIndexing;
import searchengine.repository.LemmaRepository;
import searchengine.repository.PageRepository;
import searchengine.repository.SiteRepository;

import java.time.ZoneOffset;
import java.time.temporal.ChronoField;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final Random random = new Random();
    private final ConfigOptions sites;
    private final PageRepository pageRepository;
    private final SiteRepository siteRepository;
    private final LemmaRepository lemmaRepository;

    @Override
    public StatisticsResponse getStatistics() {
        TotalStatistics total = getTotalStatistics();
        List<DetailedStatisticsItem> detailed = getItemList();
        StatisticsResponse response = new StatisticsResponse();
        StatisticsData data = new StatisticsData();
        data.setTotal(total);
        data.setDetailed(detailed);
        response.setStatistics(data);
        response.setResult(true);
        return response;
    }

    private TotalStatistics getTotalStatistics() {
        TotalStatistics total = new TotalStatistics();
        total.setSites((int) siteRepository.count());
        total.setPages((int) pageRepository.count());
        total.setLemmas((int) lemmaRepository.count());
        total.setIndexing(isIndexingRun());
        return total;
    }

    private boolean isIndexingRun() {
        for (SiteEntity siteEntity : siteRepository.findAll()) {
            if (siteEntity.getStatus().equals(StatusIndexing.INDEXING)) {
                return true;
            }
        }
        return false;
    }

    private DetailedStatisticsItem getItem(SiteEntity siteEntity) {
        DetailedStatisticsItem item = new DetailedStatisticsItem();
        item.setName(siteEntity.getName());
        item.setUrl(siteEntity.getUrl());
        item.setStatus(siteEntity.getStatus().toString());
        item.setError(siteEntity.getLastError());
        //TODO:  Get time status
        long statusTime = siteEntity.getStatusTime().toEpochSecond(ZoneOffset.UTC);
        item.setStatusTime(statusTime);
        item.setPages(pageRepository.countBySiteEntity(siteEntity));
        item.setLemmas(lemmaRepository.countBySiteEntity(siteEntity));
        return item;
    }

    private List<DetailedStatisticsItem> getItemList() {
        return siteRepository.findAll().stream().map(this::getItem).collect(Collectors.toList());
    }
}
