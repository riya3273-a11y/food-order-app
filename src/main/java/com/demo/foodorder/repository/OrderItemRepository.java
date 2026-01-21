package com.demo.foodorder.repository;

import com.demo.foodorder.entity.OrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface OrderItemRepository extends JpaRepository<OrderItem, Long> {

    @Query("""
                select oi.menuItem.id, count(oi.id)
                from OrderItem oi
                where oi.order.consumer.id = :userId
                group by oi.menuItem.id
            """)
    List<Object[]> countMenuItemsByUser(Long userId);

    @Query("""
                select oi.menuItem.id, count(oi.id)
                from OrderItem oi
                group by oi.menuItem.id
            """)
    List<Object[]> countMenuItemPopularity();
}
