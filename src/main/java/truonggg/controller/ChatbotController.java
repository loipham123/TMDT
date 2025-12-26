package truonggg.controller;

import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import lombok.RequiredArgsConstructor;
import truonggg.response.SuccessReponse;
import truonggg.service.impl.ChatbotServiceImpl;

@CrossOrigin(origins = "http://127.0.0.1:5500")
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatbotController {

    private final ChatbotServiceImpl chatbotService;

    /**
     * Endpoint chính để chat
     * Request body: {"message": "giày chạy bộ nam"}
     * Response body: SuccessReponse chứa câu trả lời
     */
    @PostMapping
    public ResponseEntity<SuccessReponse<Map<String, String>>> chat(@RequestBody Map<String, String> body) {
        String message = body.get("message");
        if (message == null || message.trim().isEmpty()) {
            return ResponseEntity.badRequest().body(SuccessReponse.of(Map.of("reply", "Vui lòng nhập câu hỏi.")));
        }

        String answer = chatbotService.getChatResponse(message);

        // Trả về response chứa câu trả lời
        return ResponseEntity.ok(SuccessReponse.of(Map.of("reply", answer)));
    }
}