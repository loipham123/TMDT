package truonggg.service;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import truonggg.DTO.request.OrderRequestDTO;
import truonggg.DTO.response.OrderResponseDTO;

public interface OrdersService {
	OrderResponseDTO createOrder(OrderRequestDTO request);

	List<OrderResponseDTO> getMyOrders(Integer userId);

	Page<OrderResponseDTO> getMyOrders(Integer userId, Pageable pageable);

	void cancelOrderByUser(Integer orderId, Integer userId);

	OrderResponseDTO getOrderById(Integer id);

    List<OrderResponseDTO> getAllOrders();

    Page<OrderResponseDTO> getAllOrders(Pageable pageable);

	OrderResponseDTO confirmOrder(Integer id);

	OrderResponseDTO shipOrder(Integer id);

	OrderResponseDTO deliveredOrder(Integer id);

	OrderResponseDTO cancelOrder(Integer id);

	OrderResponseDTO cancelOrderByAdmin(Integer id);

}
