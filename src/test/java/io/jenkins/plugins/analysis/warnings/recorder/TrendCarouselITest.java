package io.jenkins.plugins.analysis.warnings.recorder;

import java.io.IOException;
import java.util.stream.IntStream;

import org.junit.Test;

import com.gargoylesoftware.htmlunit.html.HtmlPage;

import hudson.model.FreeStyleProject;
import hudson.model.Result;

import io.jenkins.plugins.analysis.core.model.AnalysisResult;
import io.jenkins.plugins.analysis.core.steps.IssuesRecorder;
import io.jenkins.plugins.analysis.core.testutil.IntegrationTestWithJenkinsPerSuite;
import io.jenkins.plugins.analysis.warnings.Java;
import io.jenkins.plugins.analysis.warnings.recorder.pageobj.TrendCarousel;
import io.jenkins.plugins.analysis.warnings.recorder.pageobj.TrendCarousel.TrendChartType;

import static org.assertj.core.api.Assertions.*;

/**
 * Provides tests for the trend carousel shown on the details page.
 *
 * @author Tobias Redl
 * @author Andreas Neumeier
 */

public class TrendCarouselITest extends IntegrationTestWithJenkinsPerSuite {

    private HtmlPage webPage;

    /**
     * Test that tools trend chart is default.
     */
    @Test
    public void shouldShowToolsTrendChartAsDefault() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
    }

    /**
     * Test that new versus fixed trend chart is next.
     */
    @Test
    public void shouldShowNewVersusFixedTrendChartAsNext() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.next().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));

    }

    /**
     * Test that severities trend chart is previous.
     */
    @Test
    public void shouldShowSeveritiesTrendChartAsPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.previous().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
    }

    /**
     * Test that severities trend chart is shown after two clicks on next.
     */
    @Test
    public void shouldShowSeveritiesTrendChartAfterTwoTimesNext() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        IntStream.generate(() -> 1).limit(2).forEach(x -> carousel.next());
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
    }

    /**
     * Test that new versus fixed trend chart is shown after two clicks on previous.
     */
    @Test
    public void shouldShowNewVersusFixedTrendChartAfterTwoTimesPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        IntStream.generate(() -> 1).limit(2).forEach(x -> carousel.previous());
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
    }

    /**
     * Test that tools trend chart is shown after three clicks on next.
     */
    @Test
    public void shouldShowToolsTrendChartAfterThreeTimesNext() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        IntStream.generate(() -> 1).limit(3).forEach(x -> carousel.next());
        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
    }

    /**
     * Test that tools trend chart is shown after three clicks on previous.
     */
    @Test
    public void shouldShowToolsTrendChartAfterThreeTimesPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        IntStream.generate(() -> 1).limit(3).forEach(x -> carousel.previous());
        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
    }

    /**
     * Test that tools, new versus fixed and severities trend charts are shown in this order by clicking next.
     */
    @Test
    public void shouldShowTrendChartsInRightOrderByNext() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
        assertThat(carousel.next().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
        assertThat(carousel.next().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
    }

    /**
     * Test that tools, severities and new versus fixed trend charts are shown in this order by clicking previous.
     */
    @Test
    public void shouldShowTrendChartsInRightOrderByPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
        assertThat(carousel.previous().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
        assertThat(carousel.previous().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
    }

    /**
     * Test that tools trend chart is shown after clicking next and previous and clicking previous and next.
     */
    @Test
    public void shouldShowToolsTrendChartAfterNextAndPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
        assertThat(carousel.next().equals(carousel.getActive()));
        assertThat(carousel.previous().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
        assertThat(carousel.previous().equals(carousel.getActive()));
        assertThat(carousel.next().equals(carousel.getActive()));
        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
    }

    /**
     * Test that severities trend chart is shown after clicking previous and also after refreshing the page (loaded from
     * local storage).
     */
    @Test
    public void shouldShowSeveritiesTrendChartAfterPreviousAndReload() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        carousel.previous();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
        try {
            webPage.refresh();
        }
        catch (IOException e) {
            throw new RuntimeException("WebPage refresh failed.");
        }
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
    }

    /**
     * Test that severities trend chart is shown after clicking next and also after refreshing the page (loaded from
     * local storage).
     */
    @Test
    public void shouldShowNewVersusNextTrendChartAfterPreviousAndReload() {
        TrendCarousel carousel = setUpTrendChartTest(false);

        carousel.previous();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
        try {
            webPage.refresh();
        }
        catch (IOException e) {
            throw new RuntimeException("WebPage refresh failed.");
        }
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
    }

    /**
     * Test that health trend chart is shown after clicking four times next.
     */
    @Test
    public void shouldShowHealthTrendChartAfterFourTimesNext() {
        TrendCarousel carousel = setUpTrendChartTest(true);
        IntStream.generate(() -> 1).limit(4).forEach(x -> carousel.next());

        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.SEVERITIES));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.NEW_VERSUS_FIXED));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.TOOLS));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
        carousel.next();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
    }

    /**
     * Test that health trend chart is shown after clicking previous.
     */
    @Test
    public void shouldShowHealthTrendChartAfterPrevious() {
        TrendCarousel carousel = setUpTrendChartTest(true);
        carousel.previous();
        assertThat(carousel.getActiveChartType().equals(TrendChartType.HEALTH));
    }

    private TrendCarousel setUpTrendChartTest(final boolean hasHealthReport) {
        FreeStyleProject project = createFreeStyleProject();

        Java java = new Java();
        //TODO should we use this?
        java.setPattern("**/*.txt");
        IssuesRecorder recorder = enableWarnings(project, java);

        if (hasHealthReport) {
            recorder.setHealthy(1);
            recorder.setUnhealthy(9);
        }

        createWorkspaceFileWithWarnings(project, 1, 2);
        AnalysisResult analysisResult = scheduleBuildAndAssertStatus(project, Result.SUCCESS);

        int buildNumber = analysisResult.getBuild().getNumber();
        String pluginId = analysisResult.getId();
        webPage = getWebPage(JavaScriptSupport.JS_ENABLED, project, buildNumber + "/" + pluginId);

        return new TrendCarousel(webPage);
    }

    private void createWorkspaceFileWithWarnings(final FreeStyleProject project,
            final int... linesWithWarning) {
        StringBuilder warningText = new StringBuilder();
        for (int lineNumber : linesWithWarning) {
            warningText.append(createDeprecationWarning(lineNumber)).append("\n");
        }

        createFileInWorkspace(project, "javac.txt", warningText.toString());
    }

    private String createDeprecationWarning(final int lineNumber) {
        return String.format(
                "[WARNING] C:\\Path\\SourceFile.java:[%d,42] [deprecation] path.AClass in path has been deprecated\n",
                lineNumber);
    }
}
