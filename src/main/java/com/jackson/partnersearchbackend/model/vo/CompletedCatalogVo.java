package com.jackson.partnersearchbackend.model.vo;

import com.jackson.partnersearchbackend.model.domain.Catalog;
import com.jackson.partnersearchbackend.model.domain.Tag;
import lombok.Data;

import java.util.List;

/**
 * 主要是为了配合前端的数据类型
 */
@Data
public class CompletedCatalogVo {
    private String text;
    private List<childrenItem> children;

    public CompletedCatalogVo(Catalog catalog, List<Tag> tagList) {
        this.text = catalog.getCatalogItem();
        this.children = tagList.stream().map(tag -> new childrenItem(tag.getTagName())).toList();
    }
}

@Data
class childrenItem {
    private String text;
    private String id;

    public childrenItem(String tagName) {
        this.text = tagName;
        this.id = tagName;
    }

    @Override
    public String toString() {
        return "{ text: "+this.text+", "+"id: "+this.id+" }";
    }
}
