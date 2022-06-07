package com.example.jesta.model.repositories;

import android.util.Log;

import com.example.jesta.model.enteties.Category;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class CategoriesRepository {

    // region Members

    private final ConcurrentHashMap<Category, List<Category>> mapCategoryToSubCategory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Category> mapIdToCategory = new ConcurrentHashMap<>();
    private final ConcurrentHashMap<String, Category> mapNameToCategory = new ConcurrentHashMap<>();

    // endregion

    // region Singleton

    private static CategoriesRepository instance = null;

    private CategoriesRepository() {
    }

    public static CategoriesRepository getInstance() {
        if (instance == null)
            instance = new CategoriesRepository();
        return instance;
    }

    // endregion

    // region properties

    public ConcurrentHashMap<Category, List<Category>> getMapCategoryToSubCategory() {
        return mapCategoryToSubCategory;
    }

    public void setMapCategoryToSubCategory(HashMap<Category, List<Category>> mapCategoryToSubCategory) {
        this.mapCategoryToSubCategory.putAll(mapCategoryToSubCategory);
        mapCategoryToSubCategory.forEach((k, v) -> {
            GraphqlRepository.getInstance().getChildCategoryByParent(k);
            mapIdToCategory.put(k.get_id(), k);
            mapNameToCategory.put(k.getName(), k);
        });

        Log.d("category", "finish adding subcategory");
    }

    // endregion

    // region public Methods

    public void addSubCategories(List<Category> subCategories, Category parent) {
        mapCategoryToSubCategory.put(parent, subCategories);
        subCategories.forEach(c -> {
            mapIdToCategory.put(c.get_id(), c);
            mapNameToCategory.put(c.getName(), c);
        });
    }

    public String getCategoryNameById(String id) {
        return mapIdToCategory.get(id).getName();
    }

    public String getCategoryNameById(String id1, String id2) {
        return getCategoryNameById(id1) + " " + getCategoryNameById(id2);
    }

    public Category getCategoryByName(String name){
        if (mapNameToCategory.containsKey(name)){
            return mapNameToCategory.get(name);
        }
        return null;
    }

    // endregion
}
