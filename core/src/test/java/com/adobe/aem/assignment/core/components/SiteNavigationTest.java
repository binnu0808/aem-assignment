package com.adobe.aem.assignment.core.components;

import com.adobe.aem.assignment.core.beans.SiteNavigationBean;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ValueMap;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.Iterator;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.support.membermodification.MemberModifier.stub;


@RunWith(PowerMockRunner.class)
@PrepareForTest({SiteNavigation.class, SiteNavigationBean.class})
public class SiteNavigationTest {

    private SiteNavigation siteNavigation;
    private Page languagePage;
    private Page page1;
    private Page child11;
    private Page child12;
    private Page child11a;
    private ValueMap propMap1;
    private ValueMap propMap2;
    private ValueMap propMap3;
    private ValueMap propMap4;
    private ValueMap propMap5;
    private Iterator<Page> rootPageIterator;
    private Iterator<Page> languagePageIterator;
    private Iterator<Page> child11Iterator;
    private Iterator<Page> child12Iterator;

    protected Page currentPage;
    protected ValueMap properties;
    protected PageManager pageManager;
    protected ValueMap pageProperties;
    protected SlingHttpServletRequest request;
    protected Resource resource;
    protected Page resourcePage;
    protected ResourceResolver resourceResolver;

    @Before
    public void setUp() throws NoSuchFieldException {
        mockPages();
    }

    @SuppressWarnings("unchecked")
    private void mockPages() {
        siteNavigation = new SiteNavigation();
        initializeCommonObjects(siteNavigation);

        languagePage = mock(Page.class);
        page1 = mock(Page.class);
        child11 = mock(Page.class);
        child11a = mock(Page.class);
        child12 = mock(Page.class);
        propMap1 = mock(ValueMap.class);
        propMap2 = mock(ValueMap.class);
        propMap3 = mock(ValueMap.class);
        propMap4 = mock(ValueMap.class);
        propMap5 = mock(ValueMap.class);
        languagePageIterator = (Iterator<Page>) mock(Iterator.class);
        rootPageIterator = (Iterator<Page>) mock(Iterator.class);
        child11Iterator = (Iterator<Page>) mock(Iterator.class);
        child12Iterator = (Iterator<Page>) mock(Iterator.class);

        when(currentPage.getAbsoluteParent(2)).thenReturn(languagePage);

        when(languagePage.listChildren()).thenReturn(languagePageIterator);
        when(page1.listChildren()).thenReturn(rootPageIterator);
        when(child11.listChildren()).thenReturn(child11Iterator);
        when(child12.listChildren()).thenReturn(child12Iterator);

        when(languagePageIterator.hasNext()).thenReturn(true, false);
        when(rootPageIterator.hasNext()).thenReturn(true, true, false);
        when(child11Iterator.hasNext()).thenReturn(true, false);
        when(child12Iterator.hasNext()).thenReturn(false);

        when(languagePageIterator.next()).thenReturn(page1);
        when(rootPageIterator.next()).thenReturn(child11, child12);
        when(child11Iterator.next()).thenReturn(child11a);

        mockPage(page1, "content/page1", "page1");
        mockPage(child11, "content/page1/child11", "child11");
        mockPage(child12, "content/page1/child12", "child12");
        mockPage(child11a, "content/page1/child11/child11a", "child11a");

        when(page1.getProperties()).thenReturn(propMap1);
        when(propMap1.get("hideInNav", false)).thenReturn(false);
        when(currentPage.getProperties()).thenReturn(propMap2);
        when(propMap2.get("cq:template", String.class)).thenReturn("/apps/assignment/components/structure/home-page");

        when(child11.getProperties()).thenReturn(propMap3);
        when(propMap3.get("hideInNav", false)).thenReturn(false);
        when(child12.getProperties()).thenReturn(propMap4);
        when(propMap4.get("hideInNav", false)).thenReturn(false);
        when(child11a.getProperties()).thenReturn(propMap5);
        when(propMap5.get("hideInNav", false)).thenReturn(false);
    }

    @Test
    public void testGetSiteNavigationItems() throws Exception {
        siteNavigation.activate();
        assertEquals(1, siteNavigation.getPagelist().size());
        assertEquals("page1", siteNavigation.getPagelist().get(0).getTitle());
        assertEquals(2, siteNavigation.getPagelist().get(0).getNavDepth());
        assertEquals("content/page1", siteNavigation.getPagelist().get(0).getPath());
        assertEquals(2, siteNavigation.getPagelist().get(0).getChildList().size());
        assertEquals("child11", siteNavigation.getPagelist().get(0).getChildList().get(0).getTitle());
        assertEquals("content/page1/child11", siteNavigation.getPagelist().get(0).getChildList().get(0).getPath());
        assertEquals("child12", siteNavigation.getPagelist().get(0).getChildList().get(1).getTitle());
        assertEquals("content/page1/child12", siteNavigation.getPagelist().get(0).getChildList().get(1).getPath());
        assertEquals("child11a",
                siteNavigation.getPagelist().get(0).getChildList().get(0).getChildList().get(0).getTitle());
        assertEquals("content/page1/child11/child11a",
                siteNavigation.getPagelist().get(0).getChildList().get(0).getChildList().get(0).getPath());
    }

    @Test
    public void testGetSiteNavigationItems_InvalidIterator() throws Exception {
        // Empty Iterator
        when(languagePageIterator.hasNext()).thenReturn(false);
        siteNavigation.activate();
        assertEquals(0, siteNavigation.getPagelist().size());
        // Null Iterator
        when(languagePage.listChildren()).thenReturn(null);
        siteNavigation.activate();
        assertEquals(0, siteNavigation.getPagelist().size());
    }

    @Test
    public void testGetSiteNavigationItems_HideInNav() throws Exception {
        when(propMap1.get("hideInNav", false)).thenReturn(true);
        siteNavigation.activate();
        // verify logic
        assertEquals(0, siteNavigation.getPagelist().size());
    }

    @Test
    public void testSiteNavigationItems_Valid() {
        assertEquals(2, siteNavigation.getChildPagesList(rootPageIterator, 2).size());
    }

    @Test
    public void testSiteNavigationItems_Invalid() {
        // Null Iterator
        assertEquals(0, siteNavigation.getChildPagesList(null, 2).size());
        // No Level2 Children
        when(child11.getDepth()).thenReturn(4);
        assertEquals(null, siteNavigation.getChildPagesList(rootPageIterator, 2).get(0).getChildList());
    }

    @Test
    public void testSiteNavigationItems_HideInNav() {
        when(propMap3.get("hideInNav", false)).thenReturn(true);
        when(propMap4.get("hideInNav", false)).thenReturn(true);
        assertEquals(0, siteNavigation.getChildPagesList(rootPageIterator, 2).size());
    }

    private void mockPage(Page page, String path, String title) {
        when(page.getPath()).thenReturn(path);
        when(page.getTitle()).thenReturn(title);
    }

    private void initializeCommonObjects(Object componentTest) {

        currentPage = mock(Page.class);
        properties = mock(ValueMap.class);
        pageManager = mock(PageManager.class);
        pageProperties = mock(ValueMap.class);
        properties = mock(ValueMap.class);
        request = mock(SlingHttpServletRequest.class);
        resource = mock(Resource.class);
        resourcePage = mock(Page.class);
        resourceResolver = mock(ResourceResolver.class);


        stub(PowerMockito.method(componentTest.getClass(), "getCurrentPage")).toReturn(currentPage);
        stub(PowerMockito.method(componentTest.getClass(), "getInheritedPageProperties"))
                .toReturn(properties);
        stub(PowerMockito.method(componentTest.getClass(), "getPageManager")).toReturn(pageManager);
        stub(PowerMockito.method(componentTest.getClass(), "getPageProperties")).toReturn(pageProperties);
        stub(PowerMockito.method(componentTest.getClass(), "getProperties")).toReturn(properties);
        stub(PowerMockito.method(componentTest.getClass(), "getRequest")).toReturn(request);
        stub(PowerMockito.method(componentTest.getClass(), "getResource")).toReturn(resource);
        stub(PowerMockito.method(componentTest.getClass(), "getResourcePage")).toReturn(resourcePage);
        stub(PowerMockito.method(componentTest.getClass(), "getResourceResolver")).toReturn(resourceResolver);

    }
}
