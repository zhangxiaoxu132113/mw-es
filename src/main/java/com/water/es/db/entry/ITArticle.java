package com.water.es.db.entry;

import com.water.es.utils.Constants;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.io.Serializable;

@Document(indexName = Constants.ES_CONFIG.INDEX_BLOG, type = Constants.ES_CONFIG.TYPE_ITARTICLE)
public class ITArticle implements Serializable {
    @Id
    private String id;

    @Field(type = FieldType.String)
    private String title;

    @Field(type = FieldType.String)
    private String description;

    @Field(type = FieldType.String)
    private String author;

    @Field(type = FieldType.String)
    private String category;

    @Field(type = FieldType.String)
    private String reference;

    @Field(type = FieldType.String)
    private String descryptUrl;

    @Field(type = FieldType.String)
    private String releaseTime;

    @Field(type = FieldType.Long)
    private Long createOn;

    @Field(type = FieldType.String)
    private String content;

    private static final long serialVersionUID = 1L;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id == null ? null : id.trim();
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