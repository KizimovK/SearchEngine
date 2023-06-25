package searchengine.services;

public interface PageIndexService {
    void startIndexPage() throws Exception;
    boolean isActiveIndexing();
    void stopIndexPage();
}
