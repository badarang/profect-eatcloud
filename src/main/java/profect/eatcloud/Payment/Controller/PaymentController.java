package profect.eatcloud.Payment.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import profect.eatcloud.Payment.TossPaymentService;
import profect.eatcloud.Payment.Dto.TossPaymentResponse;

import java.util.Map;
import java.util.UUID;

/**
 * 토스페이먼츠 표준 결제 컨트롤러
 */
@Controller
public class PaymentController {
    
    @Autowired
    private TossPaymentService tossPaymentService;
    
    // API 개별 연동 클라이언트 키
    @Value("${toss.client-key}")
    private String clientKey;
    
    /**
     * 결제 페이지 (직접 구현한 UI)
     * GET /payments/charge?userId=123&amount=1
     */
    @GetMapping("/payments/charge")
    public String getPaymentPage(@RequestParam(required = false) String userId,
                                 @RequestParam(required = false) Integer amount,
                                 Model model) {
        
        System.out.println("=== 결제 페이지 요청 ===");
        System.out.println("User ID: " + userId);
        System.out.println("Amount: " + amount);
        
        // 기본값 설정
        if (userId == null) userId = "user_" + System.currentTimeMillis();
        if (amount == null) amount = 1;
        
        // 주문번호 생성
        String orderId = "ORDER_" + UUID.randomUUID().toString().substring(0, 12);
        
        // 템플릿에 전달할 데이터 설정
        model.addAttribute("userId", userId);
        model.addAttribute("clientKey", clientKey);
        model.addAttribute("amount", amount);
        model.addAttribute("orderId", orderId);
        
        System.out.println("Order ID 생성: " + orderId);
        System.out.println("Client Key: " + clientKey);
        
        return "payment/checkout";
    }
    
    /**
     * 결제 성공 콜백 (토스페이먼츠에서 리다이렉트)
     * GET /payment/success?paymentKey=xxx&orderId=xxx&amount=xxx
     */
    @GetMapping("/payment/success")
    public String paymentSuccess(@RequestParam String paymentKey,
                                @RequestParam String orderId,
                                @RequestParam Integer amount,
                                Model model) {
        try {
            System.out.println("=== 결제 성공 콜백 수신 ===");
            System.out.println("Payment Key: " + paymentKey);
            System.out.println("Order ID: " + orderId);
            System.out.println("Amount: " + amount);
            
            // 토스페이먼츠 승인 API 호출
            TossPaymentResponse response = tossPaymentService.confirmPayment(paymentKey, orderId, amount);
            
            // 성공 페이지에 전달할 데이터
            model.addAttribute("paymentKey", paymentKey);
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", amount);
            model.addAttribute("status", response.getStatus());
            model.addAttribute("method", response.getMethod());
            model.addAttribute("approvedAt", response.getApprovedAt());
            
            System.out.println("=== 결제 처리 완료 ===");
            
            // TODO: 여기서 비즈니스 로직 처리
            // - 포인트 충전
            // - 주문 상태 업데이트
            // - 알림 발송 등
            
            return "payment/success";
            
        } catch (Exception e) {
            System.out.println("=== 결제 승인 실패 ===");
            System.out.println("오류 메시지: " + e.getMessage());
            
            model.addAttribute("error", e.getMessage());
            model.addAttribute("paymentKey", paymentKey);
            model.addAttribute("orderId", orderId);
            model.addAttribute("amount", amount);
            
            return "payment/fail";
        }
    }
    
    /**
     * 결제 실패 콜백 (토스페이먼츠에서 리다이렉트)
     * GET /payment/fail?message=xxx&code=xxx&orderId=xxx
     */
    @GetMapping("/payment/fail")
    public String paymentFail(@RequestParam(required = false) String message,
                             @RequestParam(required = false) String code,
                             @RequestParam(required = false) String orderId,
                             Model model) {
        System.out.println("=== 결제 실패 콜백 수신 ===");
        System.out.println("Error Message: " + message);
        System.out.println("Error Code: " + code);
        System.out.println("Order ID: " + orderId);
        
        model.addAttribute("message", message != null ? message : "알 수 없는 오류가 발생했습니다.");
        model.addAttribute("code", code);
        model.addAttribute("orderId", orderId);
        
        return "payment/fail";
    }
    
    /**
     * 결제 상태 확인 API (선택사항)
     * GET /api/payments/status/{orderId}
     */
    @GetMapping("/api/payments/status/{orderId}")
    @ResponseBody
    public ResponseEntity<?> getPaymentStatus(@PathVariable String orderId) {
        try {
            // TODO: DB에서 주문 상태 조회 로직 구현
            return ResponseEntity.ok(Map.of(
                "orderId", orderId,
                "status", "pending",
                "message", "결제 대기 중입니다."
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of(
                "error", e.getMessage(),
                "orderId", orderId
            ));
        }
    }
}