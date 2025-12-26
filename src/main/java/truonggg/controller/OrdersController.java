package truonggg.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import lombok.RequiredArgsConstructor;
import truonggg.DTO.request.OrderRequestDTO;
import truonggg.DTO.response.OrderResponseDTO;
import truonggg.exception.NotFoundException;
import truonggg.response.SuccessReponse;
import truonggg.service.OrdersService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class OrdersController {

	private final OrdersService ordersService;

	@PostMapping
	public SuccessReponse<?> createOrder(@RequestBody OrderRequestDTO request) {
		return SuccessReponse.of(ordersService.createOrder(request));
	}

	@GetMapping("/my-orders")
	public SuccessReponse<?> getMyOrders(@RequestParam Integer userId, 
			@RequestParam(defaultValue = "0") int page,
			@RequestParam(defaultValue = "10") int size) {
		Pageable pageable = PageRequest.of(page, size);
		return SuccessReponse.ofPage(ordersService.getMyOrders(userId, pageable));
	}

	@GetMapping("/{id}")
	public SuccessReponse<?> getOrderById(@PathVariable Integer id, @RequestParam Integer userId) {
		OrderResponseDTO order = ordersService.getOrderById(id);

		if (order == null) {
			throw new NotFoundException("Đơn hàng không tồn tại."); // Ghi rõ lỗi
		}

		if (!order.getUserId().equals(userId)) {
			throw new RuntimeException("Bạn không có quyền xem đơn hàng này."); // Thông báo rõ ràng
		}

		return SuccessReponse.of(order);
	}

    @DeleteMapping("/{id}")
    public SuccessReponse<Void> cancelOrder(@PathVariable Integer id, @RequestParam Integer userId) {
        ordersService.cancelOrderByUser(id, userId);
        return SuccessReponse.of(null);
    }

	// ✅ NHÂN VIÊN: XEM TẤT CẢ ĐƠN HÀNG
    @GetMapping("/admin")
    public SuccessReponse<List<OrderResponseDTO>> getAllOrdersForAdmin(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<OrderResponseDTO> result = ordersService.getAllOrders(pageable);
        return SuccessReponse.ofPage(result);
    }

	// ✅ NHÂN VIÊN: XEM CHI TIẾT ĐƠN HÀNG
	@GetMapping("/admin/{id}")
	public SuccessReponse<?> getOrderDetailByAdmin(@PathVariable Integer id) {
		OrderResponseDTO order = ordersService.getOrderById(id);
		if (order == null) {
			throw new NotFoundException("Đơn hàng không tồn tại.");
		}
		return SuccessReponse.of(order);
	}

	// ✅ NHÂN VIÊN: XÁC NHẬN ĐƠN HÀNG
	@PutMapping("/admin/{id}/confirm")
	public SuccessReponse<?> confirmOrder(@PathVariable Integer id) {
		OrderResponseDTO order = ordersService.confirmOrder(id);
		return SuccessReponse.of(order);
	}

	// ✅ NHÂN VIÊN: GIAO HÀNG (CHUYỂN TRẠNG THÁI)
	@PutMapping("/admin/{id}/ship")
	public SuccessReponse<?> shipOrder(@PathVariable Integer id) {
		OrderResponseDTO order = ordersService.shipOrder(id);
		return SuccessReponse.of(order);
	}

	// ✅ NHÂN VIÊN: HOÀN THÀNH GIAO HÀNG
	@PutMapping("/admin/{id}/delivered")
	public SuccessReponse<?> deliveredOrder(@PathVariable Integer id) {
		OrderResponseDTO order = ordersService.deliveredOrder(id);
		return SuccessReponse.of(order);
	}

	// ✅ NHÂN VIÊN: HUỶ ĐƠN
	@PutMapping("/admin/{id}/cancel")
	public SuccessReponse<?> cancelOrderByAdmin(@PathVariable Integer id) {
		OrderResponseDTO order = ordersService.cancelOrderByAdmin(id);
		return SuccessReponse.of(order);
	}
}