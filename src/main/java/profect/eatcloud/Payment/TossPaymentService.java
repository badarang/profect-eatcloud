package profect.eatcloud.Payment;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import profect.eatcloud.Payment.Dto.TossPaymentResponse;
import profect.eatcloud.Payment.Dto.TossPaymentRequest;

import java.util.Base64;

@Service
public class TossPaymentService {

    @Autowired
    @Qualifier("tossWebClient")
    private WebClient tossWebClient;
    
    @Value("${toss.secret-key}")
    private String secretKey;

    /**
     * 토스페이먼츠 결제 승인 API 호출 (표준 결제용)
     */
    public TossPaymentResponse confirmPayment(String paymentKey, String orderId, Integer amount) {
        System.out.println("=== 토스페이먼츠 결제 승인 요청 ===");
        System.out.println("Payment Key: " + paymentKey);
        System.out.println("Order ID: " + orderId);
        System.out.println("Amount: " + amount);
        
        // 1. 시크릿 키 Base64 인코딩 (토스 API 인증 방식)
        String encodedAuth = Base64.getEncoder()
            .encodeToString((secretKey + ":").getBytes());
        
        // 2. 요청 데이터 생성
        TossPaymentRequest request = new TossPaymentRequest(paymentKey, orderId, amount);
        
        try {
            // 3. WebClient로 POST 요청
            TossPaymentResponse response = tossWebClient
                .post()
                .uri("/payments/confirm")  // 토스 결제 승인 API 엔드포인트
                .header("Authorization", "Basic " + encodedAuth)  // 인증 헤더
                .bodyValue(request)  // 요청 데이터 (JSON으로 자동 변환)
                .retrieve()  // 응답 받기
                .bodyToMono(TossPaymentResponse.class)  // 응답을 TossPaymentResponse 객체로 변환
                .block();  // 동기 방식으로 결과 기다리기
                
            System.out.println("=== 토스페이먼츠 승인 성공 ===");
            System.out.println("결제 상태: " + response.getStatus());
            System.out.println("결제 방법: " + response.getMethod());
            
            return response;
            
        } catch (Exception e) {
            System.out.println("=== 토스페이먼츠 승인 실패 ===");
            System.out.println("에러 메시지: " + e.getMessage());
            throw new RuntimeException("결제 승인 중 오류가 발생했습니다: " + e.getMessage(), e);
        }
    }
}