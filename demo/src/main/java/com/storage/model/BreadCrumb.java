package com.storage.model;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Data
public class BreadCrumb {

    private String parentReference;
    private String name;

    public String getParentReference() {
        return parentReference;
    }

    public void setParentReference(String parentReference) {
        this.parentReference = parentReference;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BreadCrumb(String parentReference, String name) {
        this.parentReference = parentReference;
        this.name = name;
    }


    public BreadCrumb() {}
}
