package com.example.myShop.repository;

import com.example.myShop.entity.Item;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.querydsl.QuerydslPredicateExecutor;
import org.springframework.data.repository.Repository;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.query.Param;

public interface ItemRepositoryTest extends JpaRepository<Item, Long>, QuerydslPredicateExecutor<Item>, ItemRepositoryCustom {


    List<Item> findByItemName(String itemName);
   @Query("select i from Item i where i.itemDetail like "+
    "%:itemDetail% order by i.price desc ")
    List<Item> findByItemNameDetail(@Param("itemDetail")String itemDetail);

    @Query(value = "select * from t_item i where i.item_detail like :itemDetail order by i.price desc",
            nativeQuery = true)
    List<Item> findByItemDetailByNative(@Param("itemDetail")String itemDetail);
}


