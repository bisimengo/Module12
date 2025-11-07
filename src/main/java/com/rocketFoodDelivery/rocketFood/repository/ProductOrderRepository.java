package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.ProductOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface ProductOrderRepository extends JpaRepository<ProductOrder, Integer> {

    // TODO
    //The native SQL query for the DELETE /api/product_orders?order={id} route
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value =
    """
    DELETE FROM product_orders WHERE order_id = :orderId
    """)
    void deleteProductOrdersByOrderId(@Param("orderId") int orderId);

    Optional<ProductOrder> findById(int id);
    List<ProductOrder> findByOrderId(int id);
    List<ProductOrder> findByProductId(int id);
    @Override
    void deleteById(Integer productOrderId);
    @Query(nativeQuery = true, value = """
        SELECT po.product_id, p.name as product_name, po.quantity, p.cost as unit_cost,
               (po.quantity * p.cost) as total_cost
        FROM product_orders po
        JOIN products p ON po.product_id = p.id
        WHERE po.order_id = :orderId
        """)
    List<Object[]> findProductsByOrderId(@Param("orderId") int orderId);

    @Modifying
    @Transactional
    @Query(nativeQuery = true, value = """
        INSERT INTO product_orders (order_id, product_id, quantity) 
        VALUES (:orderId, :productId, :quantity)
        """)
    void createProductOrder(@Param("orderId") int orderId, 
                           @Param("productId") int productId, 
                           @Param("quantity") int quantity);
}
