package com.adobe.aem.assignment.core.beans;

import java.util.List;

public class SiteNavigationBean {

    private String title;
    private String path;
    private int navDepth;
    private List<SiteNavigationBean> childList;
    private String template;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public List<SiteNavigationBean> getChildList() {
        return childList;
    }

    public void setChildList(List<SiteNavigationBean> childList) {
        this.childList = childList;
    }

    public int getNavDepth() {
        return navDepth;
    }

    public void setNavDepth(int navDepth) {
        this.navDepth = navDepth;
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    public String getTemplate() {
        return template;
    }


}
