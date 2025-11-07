package com.rocketFoodDelivery.rocketFood.repository;

import com.rocketFoodDelivery.rocketFood.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Integer> {
    Optional<Order> findById(int id);
    List<Order> findByCustomerId(int id);
    List<Order> findByRestaurantId(int id);
    List<Order> findByCourierId(int id);

    // TODO
    // The native SQL query for the GET api/orders?type=restaurants&id={id} route 
    @Query(nativeQuery = true, value =
     """
    SELECT * FROM orders WHERE restaurant_id = :restaurantId
    """)
    List<Order> findOrdersByRestaurantId(@Param("restaurantId") int restaurantId);

    // TODO
    // The native SQL query for the DELETE /api/order/{id} route
    @Modifying
    @Transactional
    @Query(nativeQuery = true, value =
    """
    DELETE FROM orders WHERE id = :orderId
    """)
    void deleteOrderById(@Param("orderId") int orderId);

    // Add query to get orders with full details including products
    @Query(nativeQuery = true, value = """
        SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id,
               c.name as customer_name, 
               CONCAT(ca.street_address, ', ', ca.city, ', ', ca.postal_code) as customer_address,
               r.name as restaurant_name,
               CONCAT(ra.street_address, ', ', ra.city, ', ', ra.postal_code) as restaurant_address,
               cr.name as courier_name,
               os.name as status
        FROM orders o
        JOIN customers c ON o.customer_id = c.id
        JOIN addresses ca ON c.address_id = ca.id
        JOIN restaurants r ON o.restaurant_id = r.id
        JOIN addresses ra ON r.address_id = ra.id
        LEFT JOIN couriers cr ON o.courier_id = cr.id
        JOIN order_statuses os ON o.status_id = os.id
        WHERE o.customer_id = :customerId
        """)
    List<Object[]> findOrdersWithDetailsByCustomerId(@Param("customerId") int customerId);

    @Query(nativeQuery = true, value = """
        SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id,
               c.name as customer_name, 
               CONCAT(ca.street_address, ', ', ca.city, ', ', ca.postal_code) as customer_address,
               r.name as restaurant_name,
               CONCAT(ra.street_address, ', ', ra.city, ', ', ra.postal_code) as restaurant_address,
               cr.name as courier_name,
               os.name as status
        FROM orders o
        JOIN customers c ON o.customer_id = c.id
        JOIN addresses ca ON c.address_id = ca.id
        JOIN restaurants r ON o.restaurant_id = r.id
        JOIN addresses ra ON r.address_id = ra.id
        LEFT JOIN couriers cr ON o.courier_id = cr.id
        JOIN order_statuses os ON o.status_id = os.id
        WHERE o.restaurant_id = :restaurantId
        """)
    List<Object[]> findOrdersWithDetailsByRestaurantId(@Param("restaurantId") int restaurantId);

    @Query(nativeQuery = true, value = """
        SELECT o.id, o.customer_id, o.restaurant_id, o.courier_id,
               c.name as customer_name, 
               CONCAT(ca.street_address, ', ', ca.city, ', ', ca.postal_code) as customer_address,
               r.name as restaurant_name,
               CONCAT(ra.street_address, ', ', ra.city, ', ', ra.postal_code) as restaurant_address,
               cr.name as courier_name,
               os.name as status
        FROM orders o
        JOIN customers c ON o.customer_id = c.id
        JOIN addresses ca ON c.address_id = ca.id
        JOIN restaurants r ON o.restaurant_id = r.id
        JOIN addresses ra ON r.address_id = ra.id
        LEFT JOIN couriers cr ON o.courier_id = cr.id
        JOIN order_statuses os ON o.status_id = os.id
        WHERE o.courier_id = :courierId
        """)
    List<Object[]> findOrdersWithDetailsByCourierId(@Param("courierId") int courierId);
}
