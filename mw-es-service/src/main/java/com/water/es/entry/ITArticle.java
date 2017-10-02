package com.water.es.entry;

import com.water.es.annotation.EsMapping;
import com.water.es.utils.Constants;

import java.io.Serializable;

public class ITArticle implements Serializable {
    @EsMapping(isAnalyzer = false)
    private Integer id;

    @EsMapping(isAnalyzer = true, indexAnalyzer = Constants.Analyzer.IK_SMART, searchAnalyzer = Constants.Analyzer.IK_SMART)
    private String title;

    @EsMapping(isAnalyzer = true, indexAnalyzer = Constants.Analyzer.IK_SMART, searchAnalyzer = Constants.Analyzer.IK_SMART)
    private String description;

    @EsMapping(isAnalyzer = true, indexAnalyzer = Constants.Analyzer.IK_SMART, searchAnalyzer = Constants.Analyzer.IK_SMART)
    private String author;

    @EsMapping(isStore = false, indexAnalyzer = Constants.Analyzer.IK_SMART, searchAnalyzer = Constants.Analyzer.IK_SMART)
    private String content;

    @EsMapping(isAnalyzer = false)
    private String category;

    @EsMapping(isAnalyzer = false)
    private String reference;

    @EsMapping(isAnalyzer = false)
    private String descryptUrl;

    @EsMapping(isAnalyzer = false)
    private String releaseTime;

    @EsMapping(isAnalyzer = false)
    private Long createOn;

    @EsMapping(isMapping = false)
    private static final long serialVersionUID = 1L;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title == null ? null : title.trim();
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description == null ? null : description.trim();
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author == null ? null : author.trim();
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category == null ? null : category.trim();
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference == null ? null : reference.trim();
    }

    public String getDescryptUrl() {
        return descryptUrl;
    }

    public void setDescryptUrl(String descryptUrl) {
        this.descryptUrl = descryptUrl == null ? null : descryptUrl.trim();
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime == null ? null : releaseTime.trim();
    }

    public Long getCreateOn() {
        return createOn;
    }

    public void setCreateOn(Long createOn) {
        this.createOn = createOn;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content == null ? null : content.trim();
    }
}