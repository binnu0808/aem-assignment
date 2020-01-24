package com.adobe.aem.assignment.core.components;

import com.adobe.aem.assignment.core.beans.SiteNavigationBean;
import com.adobe.cq.sightly.WCMUsePojo;
import com.day.cq.wcm.api.Page;
import org.apache.sling.api.resource.ValueMap;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The SiteNavigation class provides list of pages and their children(upto childDepth level) for respective pages.
 *
 * @author Samba Shiva
 */
public class SiteNavigation extends WCMUsePojo {

    private static final int DEFAULT_CHILD_DEPTH = 2;
    private static final int ABS_PARENT = 2;
    private static final String HIDE_IN_NAV = "hideInNav";

    private List<SiteNavigationBean> pagelist = new ArrayList<>();

    @Override
    public void activate() throws Exception {
        // Calling method to get final list of pages for navigation
        getSiteNavigationItems();
    }

    /**
     * This method gets the landing page and iterates over its child list(parent pages). Calls getChildPagesList method
     * internally and returns child list of parent pages.
     *
     * @return void
     */
    public void getSiteNavigationItems() {
        Page languagePage = getCurrentPage().getAbsoluteParent(ABS_PARENT);
        Iterator<Page> iterator = languagePage.listChildren();

        while (null != iterator && iterator.hasNext()) {
            Page parentPage = iterator.next();
            ValueMap propMap = parentPage.getProperties();
            boolean hideInNav = propMap.get(HIDE_IN_NAV, false);
            if (!hideInNav) {
                Iterator<Page> childIterator = parentPage.listChildren();
                SiteNavigationBean siteNav = new SiteNavigationBean();
                siteNav.setTitle(parentPage.getTitle());
                siteNav.setPath(parentPage.getPath());
                siteNav.setNavDepth(DEFAULT_CHILD_DEPTH);
                siteNav.setChildList(getChildPagesList(childIterator, DEFAULT_CHILD_DEPTH + ABS_PARENT));
                siteNav.setTemplate(propMap.get("cq:template", String.class));
                pagelist.add(siteNav);
            }
        }
    }

    /**
     * This method iterates over child pages list and returns it
     *
     * @param iterator the iterator
     * @param childDepth the nav child
     * @return list of events
     */
    public List<SiteNavigationBean> getChildPagesList(Iterator<Page> iterator, int childDepth) {

        List<SiteNavigationBean> childPageList = new ArrayList<>();

        while (iterator != null && iterator.hasNext()) {
            Page childPage = iterator.next();
            ValueMap propMap = childPage.getProperties();
            boolean hideInNav = propMap.get(HIDE_IN_NAV, false);
            if (!hideInNav) {
                Iterator<Page> childIterator = childPage.listChildren();
                SiteNavigationBean siteNav = new SiteNavigationBean();
                siteNav.setTitle(childPage.getTitle());
                siteNav.setPath(childPage.getPath());
                if ((childPage.getDepth() - 1) <= childDepth) {
                    siteNav.setChildList(getChildPagesList(childIterator, childDepth + 1));
                }
                childPageList.add(siteNav);
            }
        }
        return childPageList;
    }

    /**
     * @return the pagelist
     */
    public List<SiteNavigationBean> getPagelist() {
        return pagelist;
    }
}
