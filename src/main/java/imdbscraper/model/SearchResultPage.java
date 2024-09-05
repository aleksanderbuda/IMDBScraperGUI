package imdbscraper.model;

import lombok.Getter;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;
import java.util.stream.Collectors;

@Getter
public class SearchResultPage {

    @FindBy(xpath = "//h3[@class=\"ipc-title__text\"]")
    private WebElement title;

    @FindBy(className = "ipc-rating-star--rating")
    private WebElement userScore;

    @FindBy(className = "ipc-rating-star--voteCount")
    private WebElement voteCount;

    @FindBy(className = "ipc-html-content-inner-div")
    private WebElement movieDescription;

    @FindBy(xpath = "//span[contains(@class,'sc-b189961a-8 hCbzGp')][1]")
    private WebElement releaseYear;

    @FindBy(xpath = "//span[contains(@class,'sc-b189961a-8 hCbzGp')][2]")
    private WebElement movieLength;

    @FindBy(xpath = "//span[contains(@class,'sc-b189961a-8 hCbzGp')][3]")
    private WebElement contentRating;

//    @FindBy(xpath = "//ul[@class=\"ipc-metadata-list ipc-metadata-list--dividers-between sc-748571c8-0 jApQAb detailed-list-view ipc-metadata-list--base\"]")
    @FindBy(xpath = "//li[@class=\"ipc-metadata-list-summary-item\"]")
    private List<WebElement> singleMovieContainer;

//    @FindBy(className = "ipc-lockup-overlay ipc-focusable")
//    private WebElement image;

    private static WebDriver driver;
    private WebDriverWait wait;

    public SearchResultPage(WebDriver driver) {
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(5));
        PageFactory.initElements(driver, this);
    }

    public List<Movie> getMovies() {
        System.out.println("Attempting to get movies...");
        return singleMovieContainer.stream()
                .map(this::extractMovieData)
                .distinct()
                .collect(Collectors.toList());
    }

    private Movie extractMovieData(WebElement resultElement) {
        String title = resultElement.getText();
        String userScore = resultElement.getText();
        String voteCount = resultElement.getText();
        String movieDescription = resultElement.getText();
        String releaseYear = resultElement.getText();
        String movieLength = resultElement.getText();
        String contentRating = resultElement.getText();

        return new Movie(title, userScore, voteCount, movieDescription, releaseYear, movieLength, contentRating);
    }

    public void waitForSearchResults() {
        try {
            System.out.println("Waiting for search results to load...");
            wait.until(ExpectedConditions.visibilityOfAllElements(singleMovieContainer));
            System.out.println("Movies container found. Found " + singleMovieContainer.size() + " container.");
        } catch (TimeoutException e) {
            System.out.println("Timeout waiting for search results to load.");
            throw e;
        }
    }

}
//iteruje teraz 7 razy każde scrapowane dane przez title, userscore itp. czyli np na title pokazuje wszystkie dane i  traktuje to jako 1. film znaleziony później 2 film to dla userscore ten sam a pozniej
//dla votecount 3 raz i tak dalej, ma traktować każdy single container jako oddzielną rzecz i te 7 danych jest podawane dla jednego containera a nie inaczej.