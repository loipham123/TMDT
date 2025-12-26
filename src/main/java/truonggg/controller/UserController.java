package truonggg.controller;

import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestParam;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.CrossOrigin;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import truonggg.DTO.request.UserRequestDTO;
import truonggg.DTO.request.UserUpdateDTO;
import truonggg.DTO.response.UserResponseDTO;
import truonggg.mapper.user.UserMapper;
import truonggg.response.SuccessReponse;
import truonggg.service.UserService;

@RestController
@RequestMapping(path = "/users")
@RequiredArgsConstructor
@CrossOrigin(origins = "http://127.0.0.1:5500")
public class UserController {
	private final UserService userService;
	private final UserMapper userMapper;

    @GetMapping("")
    public SuccessReponse<List<UserResponseDTO>> displayAll(@RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "20") int size) {
        Pageable pageable = PageRequest.of(page, size);
        Page<UserResponseDTO> result = userService.getAllUsers(pageable);
        return SuccessReponse.ofPage(result);
    }

	@PostMapping("")
	public SuccessReponse<UserResponseDTO> save(@Valid @RequestBody UserRequestDTO dto) {
		UserResponseDTO newUser = this.userService.saveUser(dto);
		return SuccessReponse.of(newUser);
	}

	@PutMapping("/{id}")
	public SuccessReponse<UserResponseDTO> updateUser(@PathVariable("id") Integer id, @RequestBody UserUpdateDTO dto) {
		UserResponseDTO updated = userService.updateUser(id, dto);
		return SuccessReponse.of(updated);
	}

	@DeleteMapping("/{id}")
	public SuccessReponse<String> softDeleteUser(@PathVariable Integer id) {
		boolean result = userService.deleteUser(id);
		if (!result) {
			return SuccessReponse.of("Tài khoản đã bị vô hiệu hóa trước đó.");
		}
		return SuccessReponse.of("Đã xóa mềm người dùng thành công.");
	}

    @DeleteMapping("/permanent/{id}")
    public SuccessReponse<String> deleteUser(@PathVariable Integer id) {
        userService.deleteUserPermanently(id);
        return SuccessReponse.of("Xóa người dùng thành công!");
    }

	@GetMapping("/me")
	public SuccessReponse<UserResponseDTO> getCurrentUserInfo() {
		// ⚠️ Ví dụ: lấy userId từ token hoặc hardcode (nếu chưa có Spring Security)
		Integer userId = 1; // Tạm thời hardcode
		UserResponseDTO user = userService.getUserById(userId);
		return SuccessReponse.of(user);
	}

}
