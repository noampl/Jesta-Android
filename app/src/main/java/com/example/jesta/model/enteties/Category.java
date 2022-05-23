package com.example.jesta.model.enteties;

import java.util.List;
import java.util.Objects;

public class Category {

    // region Members

    private String _id;
    private String name;
    private List<Category> _childrens;

    // endregion

    // region C'tor

    public Category(String id, String name, List<Category> childrens) {
        _id = id;
        this.name = name;
        _childrens = childrens;
    }

    public Category(String id, String name) {
        _id = id;
        this.name = name;
    }

    // endregion


    // region Properties

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Category> get_childrens() {
        return _childrens;
    }

    public void set_childrens(List<Category> _childrens) {
        this._childrens = _childrens;
    }

    // endregion


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Category category = (Category) o;
        return _id.equals(category._id) && name.equals(category.name) && Objects.equals(_childrens, category._childrens);
    }

    @Override
    public int hashCode() {
        return Objects.hash(_id, name);
    }
}
