package truonggg.service.impl;

import com.fasterxml.jackson.core.type.TypeReference; // Đã thêm import này để sửa lỗi Jackson
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ChatbotServiceImpl {

    // Inject file JSON từ resources/data
    @Value("classpath:product_knowledge_base.json")
    private Resource productKnowledgeBaseResource;

    @Value("classpath:chatbot_intents.json")
    private Resource intentsResource;

    private List<Map<String, Object>> productsKnowledge = Collections.emptyList();
    private List<Map<String, Object>> intents = Collections.emptyList();

    // Dùng ObjectMapper để đọc và parse JSON
    private final ObjectMapper objectMapper = new ObjectMapper();

    // Khởi tạo: Đọc file JSON khi ứng dụng khởi động
    @PostConstruct
    public void init() {
        try {
            // 1. Đọc Product Knowledge Base
            productsKnowledge = objectMapper.readValue(productKnowledgeBaseResource.getInputStream(),
                    objectMapper.getTypeFactory().constructCollectionType(List.class, Map.class));

            // 2. SỬA LỖI ĐỌC INTENTS bằng TypeReference (Cách chuẩn và an toàn)
            // Khai báo TypeReference cho cấu trúc: Map<String, List<Map<String, Object>>>
            TypeReference<Map<String, List<Map<String, Object>>>> typeRef = new TypeReference<>() {};

            Map<String, List<Map<String, Object>>> intentMap = objectMapper.readValue(
                    intentsResource.getInputStream(),
                    typeRef
            );

            // Lấy danh sách intents từ key "intents"
            this.intents = intentMap.getOrDefault("intents", Collections.emptyList());

            System.out.println("✅ Chatbot Knowledge Base Loaded: " + productsKnowledge.size() + " products, " + this.intents.size() + " intents.");

        } catch (IOException e) {
            System.err.println("LỖI KHỞI TẠO CHATBOT DATA: Không đọc được file JSON.");
            e.printStackTrace();
        }
    }

    /**
     * Hàm chính xử lý câu hỏi người dùng
     */
    public String getChatResponse(String message) {
        String lowerCaseMsg = message.toLowerCase().trim();

        // 1. Xử lý Intents (Rule-based) - Ưu tiên trả lời quy trình
        String intentResponse = handleIntent(lowerCaseMsg);
        if (intentResponse != null) {
            return intentResponse;
        }

        // 2. Xử lý Keyword Search (Tìm kiếm sản phẩm)
        List<Map<String, Object>> suggestions = findProductSuggestions(lowerCaseMsg);
        return generateProductResponse(message, suggestions);
    }

    // **********************************
    // HÀM XỬ LÝ RULE-BASED (INTENTS)
    // **********************************
    private String handleIntent(String lowerCaseMsg) {
        for (Map<String, Object> intent : intents) {
            List<String> keywords = (List<String>) intent.get("keywords");
            String response = (String) intent.get("response");

            if (keywords != null && response != null) {
                for (String keyword : keywords) {
                    if (lowerCaseMsg.contains(keyword.toLowerCase())) {
                        return response;
                    }
                }
            }
        }
        return null;
    }

    // **********************************
    // HÀM XỬ LÝ KEYWORD SEARCH
    // **********************************
    private List<Map<String, Object>> findProductSuggestions(String lowerCaseMsg) {
        return productsKnowledge.stream()
                .filter(product -> {
                    String name = (String) product.getOrDefault("name", "").toString().toLowerCase();
                    String description = (String) product.getOrDefault("description", "").toString().toLowerCase();
                    String tags = product.getOrDefault("tags", Collections.emptyList()).toString().toLowerCase();

                    // Logic tìm kiếm: Khớp từ khóa trong Tên, Mô tả hoặc Tags
                    return name.contains(lowerCaseMsg) ||
                            description.contains(lowerCaseMsg) ||
                            tags.contains(lowerCaseMsg);
                })
                .limit(3) // Giới hạn 3 sản phẩm gợi ý
                .collect(Collectors.toList());
    }

    // **********************************
    // HÀM TẠO CÂU TRẢ LỜI SẢN PHẨM
    // **********************************
    private String generateProductResponse(String originalMessage, List<Map<String, Object>> context) {
        if (context.isEmpty()) {
            return String.format("Xin lỗi, mình chưa tìm thấy sản phẩm nào phù hợp với từ khóa \"%s\". Bạn có thể hỏi lại chi tiết hơn không?", originalMessage);
        }

        StringBuilder reply = new StringBuilder("Dựa trên yêu cầu của bạn, mình xin gợi ý các sản phẩm sau:\n");

        for (int i = 0; i < context.size(); i++) {
            Map<String, Object> p = context.get(i);
            String name = (String) p.get("name");
            // Định dạng tiền tệ
            String price = String.format("%,.0f VNĐ", ((Number) p.getOrDefault("price", 0.0)).doubleValue());
            String description = (String) p.get("description");

            reply.append(String.format("\n%d. **%s** (%s) - Giá: %s. \nMô tả: %s",
                    i + 1, name, p.get("category"), price, description));
        }

        reply.append("\n\nBạn quan tâm đến sản phẩm nào nhất ạ?");

        return reply.toString();
    }
}