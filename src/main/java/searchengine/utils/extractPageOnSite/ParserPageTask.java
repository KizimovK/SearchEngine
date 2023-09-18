package searchengine.utils.extractPageOnSite;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.HttpStatusException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import searchengine.config.ConfigOptions;
import searchengine.dto.data.PageDto;
import searchengine.dto.data.SiteDto;
import searchengine.model.StatusIndexing;
import searchengine.services.PageService;
import searchengine.services.SiteService;
import searchengine.utils.MakerIndex;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.SocketTimeoutException;
import java.net.URL;
import java.net.UnknownHostException;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.CopyOnWriteArraySet;
import java.util.concurrent.RecursiveTask;

//@RequiredArgsConstructor
@Slf4j
public class ParserPageTask extends RecursiveTask<CopyOnWriteArraySet> {


    private static volatile boolean canceled = false;
    private final String urlSite;
    private final CopyOnWriteArraySet<String> allPagesSite;
    private final ConfigOptions configOptions;
    private final PageService pageService;
    private final SiteService siteService;
    private final MakerIndex makerIndex;
    private final ConcurrentHashMap<String, Integer> lemmasMapOnSite;
    private SiteDto siteDto;

    public ParserPageTask(String urlSite, SiteDto siteDto,
                          CopyOnWriteArraySet<String> allPagesSite, ConfigOptions configOptions,
                          PageService pageService, SiteService siteService, MakerIndex makerIndex,
                          ConcurrentHashMap<String, Integer> lemmasMapOnSite) {
        this.urlSite = urlSite;
        this.siteDto = siteDto;
        this.allPagesSite = allPagesSite;
        this.configOptions = configOptions;
        this.pageService = pageService;
        this.siteService = siteService;
        this.makerIndex = makerIndex;
        this.lemmasMapOnSite = lemmasMapOnSite;
    }

    public ParserPageTask(ConfigOptions configOptions, PageService pageService, SiteService siteService,
                          MakerIndex makerIndex) {
        this.urlSite = null;
        this.siteDto = null;
        this.allPagesSite = null;
        this.configOptions = configOptions;
        this.pageService = pageService;
        this.siteService = siteService;
        this.makerIndex = makerIndex;
        this.lemmasMapOnSite = new ConcurrentHashMap<>();
    }


    public static void stop() {
        canceled = true;
    }

    public static void begin() {
        canceled = false;
    }


    @Override
    protected CopyOnWriteArraySet<String> compute() {
        Document document;
        List<ParserPageTask> taskList = new CopyOnWriteArrayList<>();
        try {
            sleep(150);
            document = getDocumentUrlPage(urlSite);
            String urlPage;
            Elements elements = document.select("a[href]");
            siteService.updateStatusTime(siteDto);
            ParserPageTask task;
            for (Element element : elements) {
                if (canceled) {
                    throw new StopIndexingException();
                }
                urlPage = element.absUrl("href");
                if (checkUrlPage(urlPage) && !urlPage.isEmpty() &&
                        urlPage.startsWith(element.baseUri()) && !allPagesSite.contains(urlPage) &&
                        !urlPage.contains("#")) {
                    allPagesSite.add(urlPage);
                    log.info("PARSING PAGE " + urlPage);
                    createLemmasIndexesSavingPage(urlPage, siteDto, lemmasMapOnSite);
                    task = new ParserPageTask(urlPage, siteDto, allPagesSite,
                            configOptions, pageService, siteService, makerIndex, lemmasMapOnSite);
                    task.fork();
                    taskList.add(task);
                }
            }
        } catch (Exception e) {
            handleException(e);
        } finally {
            for (ParserPageTask parserPageTask : taskList) {
                parserPageTask.join();
            }
        }
        return allPagesSite;
    }

    private void createLemmasIndexesSavingPage(String urlPage, SiteDto siteDto,
                                               ConcurrentHashMap<String, Integer> lemmasMapOnSite) throws IOException {
        PageDto pageDto = pageService.savePage(getPageDtoUrl(urlPage),siteDto);
        makerIndex.taskCreateIndexLemmas(pageDto, siteDto, lemmasMapOnSite);
    }

    private void handleException(Exception e) {
        if (e instanceof HttpStatusException) {
            pageService.savePage(getPageDtoHttpStatusException((HttpStatusException) e), siteDto);
        } else if (e instanceof UnknownHostException) {
            setException("Unknown host exception or not connecting url " + e.getMessage());
        } else if (e instanceof SocketTimeoutException) {
            setException("Read timed out or not connecting.");
        } else if (e instanceof StopIndexingException) {
            setException(e.getMessage());
        } else {
            setException(e.getMessage());
        }
    }

    private void setSiteDto(SiteDto siteDto) {
        this.siteDto = siteDto;
    }

    public void parserPageOneTask(String urlPage, SiteDto siteDto) {
        setSiteDto(siteDto);
        try {
            createLemmasIndexesSavingPage(urlPage, siteDto, makerIndex.getMapLemmas(siteDto));
        } catch (Exception e) {
            handleException(e);
        }
    }

    private Document getDocumentUrlPage(String urlPage) throws IOException {
        return Jsoup.connect(urlPage).userAgent(configOptions.getUserAgent())
                .referrer(configOptions.getReferrer()).get();
    }

    private PageDto getPageDtoUrl(String urlPage) throws IOException {
        Document document = getDocumentUrlPage(urlPage);
        int code = document.connection().response().statusCode();
        String content = document.outerHtml();
        PageDto pageDto = new PageDto();
        pageDto.setPath(getPathPage(urlPage));
        pageDto.setCode(code);
        pageDto.setContent(content);
        return pageDto;
    }

    private PageDto getPageDtoHttpStatusException(HttpStatusException e) {
        String lastError = e.getMessage();
        int code = e.getStatusCode();
        String urlPage = e.getUrl();
        PageDto pageDto = new PageDto();
        getPathPage(urlPage);
        pageDto.setPath(getPathPage(urlPage));
        pageDto.setCode(code);
        pageDto.setContent(null);
        log.error(lastError);
        siteService.updateStatusTime(siteDto);
//        siteService.updateStatusSite(siteDto, lastError, StatusIndexing.INDEXED);
        return pageDto;
    }

    private void setException(String error) {
        log.error(error);
        siteService.updateStatusSite(siteDto, error, StatusIndexing.FAILED);
    }

    private boolean checkUrlPage(String urlPage) {
        return !urlPage.contains("#")
                && !urlPage.contains("?")
                && !urlPage.contains("%")
                && !urlPage.contains(".pdf")
                && !urlPage.contains(".jpg")
                && !urlPage.contains(".doc")
                && !urlPage.contains(".docx")
                && !urlPage.contains(".jpeg");
    }

    private String getPathPage(String urlPage) {
        if (urlPage.isEmpty()) {
            return "";
        }
        URL url;
        try {
            url = new URL(urlPage);
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
        return url.getPath();
    }

    private void sleep(int mills) throws InterruptedException {
        Thread.sleep(mills);
    }

    private class StopIndexingException extends Exception {
        public StopIndexingException() {
            super("Stop Indexing");
        }
    }

}
