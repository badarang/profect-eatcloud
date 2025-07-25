package profect.eatcloud.Payment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 토스페이먼츠 결제 승인 API 요청 DTO
 */
public class TossPaymentRequest {
    
    @JsonProperty("paymentKey")
    private String paymentKey;
    
    @JsonProperty("orderId") 
    private String orderId;
    
    @JsonProperty("amount")
    private Integer amount;
    
    // 기본 생성자
    public TossPaymentRequest() {}
    
    // 편의용 생성자
    public TossPaymentRequest(String paymentKey, String orderId, Integer amount) {
        this.paymentKey = paymentKey;
        this.orderId = orderId;
        this.amount = amount;
    }
    
    // Getter 메서드들
    public String getPaymentKey() {
        return paymentKey;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public Integer getAmount() {
        return amount;
    }
    
    // Setter 메서드들
    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setAmount(Integer amount) {
        this.amount = amount;
    }
} 