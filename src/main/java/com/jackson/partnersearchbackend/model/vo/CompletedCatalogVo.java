package com.jackson.partnersearchbackend.model.vo;

import com.jackson.partnersearchbackend.model.domain.Catalog;
import com.jackson.partnersearchbackend.model.domain.Tag;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 主要是为了配合前端的数据类型
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CompletedCatalogVo {
    private String text;
    private List<ChildrenItem> children;

    public static CompletedCatalogVo init(Catalog catalog, List<Tag> tagList) {
        CompletedCatalogVo completedCatalogVo = new CompletedCatalogVo();
        completedCatalogVo.setText(catalog.getCatalogItem());
        completedCatalogVo.setChildren(tagList.stream().map(tag -> ChildrenItem.init(tag.getTagName())).toList());
        return completedCatalogVo;
    }
}

@Data
@NoArgsConstructor
@AllArgsConstructor
class ChildrenItem {
    private String text;
    private String id;

    public static ChildrenItem init(String tagName) {
        ChildrenItem childrenItem = new ChildrenItem();
        childrenItem.setText(tagName);
        childrenItem.setId(tagName);
        return childrenItem;
    }

    @Override
    public String toString() {
        return "{ text: "+this.text+", "+"id: "+this.id+" }";
    }
}
