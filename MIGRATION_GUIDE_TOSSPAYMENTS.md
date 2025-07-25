# 토스페이먼츠 결제위젯 연동 프로젝트 마이그레이션 가이드

이 문서는 토스페이먼츠 결제위젯을 연동한 Spring Boot 프로젝트를  
새로운 환경으로 마이그레이션할 때 필요한 모든 정보를 정리한 가이드입니다.

---

## 📋 1. 프로젝트 개요

### 구현된 기능
- **토스페이먼츠 결제위젯 연동** (v1/payment-widget)
- **결제 요청 및 승인 처리**
- **결제 성공/실패 페이지**
- **Spring Boot + Thymeleaf** 기반 웹 애플리케이션
- **WebClient**를 통한 토스페이먼츠 API 호출

### 주요 기술 스택
- Java 17+ (Spring Boot)
- Thymeleaf (템플릿 엔진)
- WebClient (HTTP 클라이언트)
- 토스페이먼츠 결제위젯 v1

---

## 📁 2. 파일 구조

```
src/
  main/
    java/profect/eatcloud/
      Config/
        WebClientConfig.java           # WebClient 설정
      Payment/
        Controller/
          PaymentController.java       # 결제 관련 컨트롤러
        Dto/
          TossPaymentRequest.java      # 결제 요청 DTO
          TossPaymentResponse.java     # 결제 응답 DTO
        TossPaymentService.java        # 토스페이먼츠 API 서비스
      Security/
        SecurityConfig.java            # Spring Security 설정
    resources/
      application.properties           # 환경 설정
      templates/payment/
        checkout.html                  # 결제 페이지
        success.html                   # 결제 성공 페이지
        fail.html                      # 결제 실패 페이지
```

---

## ⚙️ 3. 핵심 설정 파일

### 3.1. application.properties
```properties
# 데이터베이스 설정
spring.datasource.url=jdbc:postgresql://localhost:5432/postgres
spring.datasource.username=groom
spring.datasource.password=

# 토스페이먼츠 결제위젯 연동 키 설정
toss.client.key=test_ck_XXXXXXXXXXXXXXXXXXXX
toss.secret.key=test_gsk_docs_OaPz8L5KdmQXkzRz3y47BMw6

# 결제 성공/실패 콜백 URL
toss.success.url=http://localhost:8080/payment/success
toss.fail.url=http://localhost:8080/payment/fail
```

### 3.2. build.gradle 의존성
```gradle
dependencies {
    implementation 'org.springframework.boot:spring-boot-starter-web'
    implementation 'org.springframework.boot:spring-boot-starter-thymeleaf'
    implementation 'org.springframework.boot:spring-boot-starter-webflux' // WebClient용
    implementation 'org.springframework.boot:spring-boot-starter-security'
    // 기타 필요한 의존성들...
}
```

---

## 🔑 4. 주요 코드 구조

### 4.1. 결제 플로우
1. **결제 페이지 요청**: `GET /payments/charge?userId=123&amount=1000`
2. **결제위젯 렌더링**: checkout.html에서 PaymentWidget 인스턴스 생성
3. **결제 요청**: 사용자가 "결제하기" 버튼 클릭
4. **토스페이먼츠 처리**: 토스 서버에서 결제 처리
5. **콜백 처리**: 
   - 성공 시: `GET /payment/success?paymentKey=xxx&orderId=xxx&amount=xxx`
   - 실패 시: `GET /payment/fail?message=xxx&code=xxx`

### 4.2. 핵심 JavaScript (checkout.html)
```javascript
// 결제위젯 인스턴스 생성 (결제위젯 연동 키 사용)
const paymentWidget = PaymentWidget(myClientKey, userId);

// 결제 수단 UI 렌더링
paymentWidget.renderPaymentMethods("#payment-method", { value: amountValue });

// 결제 요청
await paymentWidget.requestPayment({
    orderId: orderId,
    orderName: "포인트 충전",
    successUrl: window.location.origin + "/payment/success",
    failUrl: window.location.origin + "/payment/fail"
});
```

### 4.3. 백엔드 결제 승인 (PaymentController.java)
```java
@GetMapping("/payment/success")
public String paymentSuccess(@RequestParam String paymentKey,
                            @RequestParam String orderId,
                            @RequestParam Integer amount,
                            Model model) {
    // 토스페이먼츠 승인 API 호출
    TossPaymentResponse response = tossPaymentService.confirmPayment(paymentKey, orderId, amount);
    
    // 성공 페이지로 이동
    return "payment/success";
}
```

---

## 🚨 5. 마이그레이션 시 주의사항

### 5.1. **토스페이먼츠 키 종류**
- ✅ **결제위젯 연동**: `test_ck_...` (클라이언트 키) + `test_gsk_...` (시크릿 키)
- ❌ **API 개별 연동**: `test_client_...` (클라이언트 키) + `test_sk_...` (시크릿 키)
- **반드시 결제위젯 연동 키를 사용해야 함!**

### 5.2. **SDK 버전**
- ✅ 사용: `https://js.tosspayments.com/v1/payment-widget`
- ❌ 사용 금지: `https://js.tosspayments.com/v2/standard`

### 5.3. **패키지명 변경**
- 현재: `profect.eatcloud`
- 새 프로젝트에 맞게 패키지명 변경 필요

### 5.4. **환경 변수**
- `application.properties`의 모든 `toss.*` 설정값을 새 환경에 맞게 변경
- 특히 `toss.client.key`와 `toss.secret.key`는 실제 발급받은 키로 교체

---

## 🔧 6. 테스트 방법

### 6.1. 로컬 테스트
```bash
# 애플리케이션 실행
./gradlew bootRun

# 브라우저에서 접속
http://localhost:8080/payments/charge?userId=123&amount=1000
```

### 6.2. 테스트 카드 정보
- **카드번호**: 4242-4242-4242-4242
- **유효기간**: 12/25
- **CVC**: 123
- **비밀번호**: 1234

---

## 📝 7. 주요 API 엔드포인트

| 메서드 | 경로 | 설명 |
|--------|------|------|
| GET | `/payments/charge` | 결제 페이지 |
| GET | `/payment/success` | 결제 성공 콜백 |
| GET | `/payment/fail` | 결제 실패 콜백 |
| POST | `/api/payments/confirm` | 결제 승인 API (legacy) |

---

## 🐛 8. 자주 발생하는 에러 및 해결법

### 8.1. "API 개별 연동 키의 클라이언트 키로 SDK를 연동해주세요"
- **원인**: v2/standard SDK와 결제위젯 연동 키 불일치
- **해결**: v1/payment-widget SDK 사용 + 결제위젯 연동 키 사용

### 8.2. "Cannot access 'payment' before initialization"
- **원인**: PaymentWidget 인스턴스 생성 전에 결제 요청
- **해결**: renderPaymentMethods 실행 후 requestPayment 호출

### 8.3. 결제 승인 실패
- **원인**: 클라이언트 키-시크릿 키 쌍 불일치
- **해결**: 토스페이먼츠 콘솔에서 키 쌍 확인

---

## 📞 9. 추가 참고 자료

- [토스페이먼츠 결제위젯 공식 문서](https://docs.tosspayments.com/guides/v2/payment-widget/integration)
- [토스페이먼츠 API 레퍼런스](https://docs.tosspayments.com/reference)
- [토스페이먼츠 개발자센터](https://dashboard.tosspayments.com/)

---

## 🔄 10. 마이그레이션 체크리스트

- [ ] 모든 소스 파일 복사
- [ ] 패키지명 변경
- [ ] application.properties 설정 변경
- [ ] 토스페이먼츠 키 발급 및 설정
- [ ] build.gradle 의존성 확인
- [ ] 로컬 테스트 실행
- [ ] 결제 테스트 (테스트 카드 사용)
- [ ] 성공/실패 페이지 동작 확인
- [ ] 프로덕션 환경 배포 전 실결제 키로 변경

---

**⚠️ 중요**: 프로덕션 배포 시에는 반드시 실제 결제 키(live_ck_..., live_gsk_...)로 변경하고,  
HTTPS 환경에서만 운영해야 합니다! 