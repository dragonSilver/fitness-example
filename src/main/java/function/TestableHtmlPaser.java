package function;

import fitnesse.responders.run.SuiteResponder;
import fitnesse.wiki.*;

public class TestableHtmlPaser {
    private String content;
    private PageData pageData;
    private boolean includeSuiteSetup;
    private WikiPage wikiPage;

    public TestableHtmlPaser() {
    }

    public TestableHtmlPaser(PageData pageData, boolean includeSuiteSetup) {
        this.pageData = pageData;
        this.includeSuiteSetup = includeSuiteSetup;
        wikiPage = pageData.getWikiPage();
        content = new String();
    }

    public String surrond() throws Exception {

        if (ifTestPage())
            surroundPageWithSetupsAndTeardowns();

        pageData.setContent(content);

        return pageData.getHtml();
    }

    private void surroundPageWithSetupsAndTeardowns() throws Exception {
        content = includeSetups();
        content += pageData.getContent();
        content += includeTeardowns();
    }

    private String includeTeardowns() throws Exception {
        String result = "";
        result = includeInherited("teardown", "TearDown");

        if (includeSuiteSetup)
            result += includeInherited("teardown", SuiteResponder.SUITE_TEARDOWN_NAME);

        return result;
    }

    private String includeSetups() throws Exception {
        String result = "";
        if (includeSuiteSetup)
            result = includeInherited("setup", SuiteResponder.SUITE_SETUP_NAME);

        result += includeInherited("setup", "SetUp");

        return result;
    }

    private boolean ifTestPage() throws Exception {
        return pageData.hasAttribute("Test");
    }

    private String includeInherited(String mode, String pageName) throws Exception {
        WikiPage suiteSetup = PageCrawlerImpl.getInheritedPage(pageName, wikiPage);
        if (suiteSetup != null)
            return includePage(mode, suiteSetup);
        return "";
    }

    private String includePage(String mode, WikiPage suiteSetup) throws Exception {
        WikiPagePath pagePath = wikiPage.getPageCrawler().getFullPath(suiteSetup);
        String pagePathName = PathParser.render(pagePath);
        return String.format("!include -%s .%s\n", mode, pagePathName);
    }
}