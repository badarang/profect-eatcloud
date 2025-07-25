package profect.eatcloud.Payment.Dto;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 토스페이먼츠 결제 승인 API 응답 DTO
 * 유니티의 [Serializable] 클래스와 비슷한 역할
 */
public class TossPaymentResponse {
    
    @JsonProperty("paymentKey")
    private String paymentKey;
    
    @JsonProperty("orderId")
    private String orderId;
    
    @JsonProperty("status")
    private String status;
    
    @JsonProperty("totalAmount")
    private Integer totalAmount;
    
    @JsonProperty("method")
    private String method;
    
    @JsonProperty("requestedAt")
    private String requestedAt;
    
    @JsonProperty("approvedAt")
    private String approvedAt;
    
    // 기본 생성자 (Spring이 JSON을 객체로 변환할 때 필요)
    public TossPaymentResponse() {}
    
    // Getter 메서드들 (유니티의 public 필드와 비슷한 역할)
    public String getPaymentKey() {
        return paymentKey;
    }
    
    public String getOrderId() {
        return orderId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public Integer getTotalAmount() {
        return totalAmount;
    }
    
    public String getMethod() {
        return method;
    }
    
    public String getRequestedAt() {
        return requestedAt;
    }
    
    public String getApprovedAt() {
        return approvedAt;
    }
    
    // Setter 메서드들
    public void setPaymentKey(String paymentKey) {
        this.paymentKey = paymentKey;
    }
    
    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    public void setTotalAmount(Integer totalAmount) {
        this.totalAmount = totalAmount;
    }
    
    public void setMethod(String method) {
        this.method = method;
    }
    
    public void setRequestedAt(String requestedAt) {
        this.requestedAt = requestedAt;
    }
    
    public void setApprovedAt(String approvedAt) {
        this.approvedAt = approvedAt;
    }
} 