
package com.moneymate.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import okhttp3.*;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Service
public class HuggingFaceService {

    @Value("${huggingface.api.key}")
    private String apiKey;

    private final OkHttpClient client;
    private final ObjectMapper objectMapper;

    public HuggingFaceService() {
        this.client = new OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(90, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .build();
        this.objectMapper = new ObjectMapper();
    }

    public String generateSpendingStrategy(Map<String, Object> userData) {
        try {
            String prompt = buildPrompt(userData);
            
            String modelUrl = "https://api-inference.huggingface.co/models/beomi/llama-2-ko-7b";
            
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("inputs", prompt);
            requestBody.put("parameters", Map.of(
                "max_new_tokens", 500,
                "temperature", 0.8,
                "top_p", 0.95,
                "do_sample", true,
                "return_full_text", false
            ));

            RequestBody body = RequestBody.create(
                objectMapper.writeValueAsString(requestBody),
                MediaType.parse("application/json")
            );

            Request request = new Request.Builder()
                .url(modelUrl)
                .addHeader("Authorization", "Bearer " + apiKey)
                .addHeader("Content-Type", "application/json")
                .post(body)
                .build();

            System.out.println("=== Hugging Face API 호출 ===");
            System.out.println("Model: " + modelUrl);

            try (Response response = client.newCall(request).execute()) {
                String responseBody = response.body().string();
                System.out.println("=== API 응답 ===");
                System.out.println("Status: " + response.code());
                
                if (!response.isSuccessful()) {
                    System.err.println("Hugging Face API 오류: " + response.code());
                    
                    if (response.code() == 503) {
                        return "⏳ AI 모델이 준비 중입니다. 20-30초 후 다시 시도해주세요!\n\n" + 
                               getDefaultStrategy(userData);
                    }
                    
                    return getDefaultStrategy(userData);
                }
                
                JsonNode root = objectMapper.readTree(responseBody);
                
                if (root.isArray() && root.size() > 0) {
                    String generatedText = root.get(0).path("generated_text").asText();
                    
                    if (generatedText == null || generatedText.trim().isEmpty()) {
                        return getDefaultStrategy(userData);
                    }
                    
                    if (generatedText.contains("답변:")) {
                        generatedText = generatedText.split("답변:")[1].trim();
                    } else {
                        generatedText = generatedText.replace(prompt, "").trim();
                    }
                    
                    if (generatedText.length() < 50) {
                        return getDefaultStrategy(userData);
                    }
                    
                    return generatedText;
                }
                
                return getDefaultStrategy(userData);
            }

        } catch (Exception e) {
            System.err.println("Hugging Face API 호출 실패: " + e.getMessage());
            e.printStackTrace();
            return getDefaultStrategy(userData);
        }
    }

    private String buildPrompt(Map<String, Object> userData) {
        StringBuilder prompt = new StringBuilder();
        
        Integer totalSpent = (Integer) userData.get("totalSpent");
        Integer budget = (Integer) userData.get("budget");
        Map<String, Integer> categorySpending = (Map<String, Integer>) userData.get("categorySpending");

        if (totalSpent == null || totalSpent == 0) {
            prompt.append("당신은 전문 재무 상담사입니다.\n\n");
            prompt.append("상황: 사용자가 가계부 앱을 처음 시작했고, 아직 지출 내역이 없습니다.\n\n");
            prompt.append("지시사항:\n");
            prompt.append("1. 가계부 관리 시작 방법 3가지를 구체적으로 제안하세요\n");
            prompt.append("2. 각 조언은 실천 가능한 구체적 행동을 포함해야 합니다\n");
            prompt.append("3. 이모지(✅, 💡, ⚠️)로 시작하고 높임말로 작성하세요\n");
            prompt.append("4. 각 팁은 2-3문장으로 자세히 설명하세요\n");
            prompt.append("5. 구체적인 금액은 언급하지 마세요\n\n");
            prompt.append("답변:");
            return prompt.toString();
        }

        // 예산 대비 지출 비율 계산
        int ratio = 0;
        String budgetStatus = "설정 안 됨";
        if (budget != null && budget > 0) {
            ratio = (int) ((totalSpent * 100.0) / budget);
            if (ratio > 100) {
                budgetStatus = "예산 초과 (" + (ratio - 100) + "% 초과)";
            } else if (ratio > 80) {
                budgetStatus = "주의 필요 (" + ratio + "% 사용)";
            } else if (ratio > 50) {
                budgetStatus = "양호 (" + ratio + "% 사용)";
            } else {
                budgetStatus = "우수 (" + ratio + "% 사용)";
            }
        }

        // 카테고리별 지출 분석
        String topCategory = "없음";
        int topAmount = 0;
        String categoryAnalysis = "";
        
        if (categorySpending != null && !categorySpending.isEmpty()) {
            var sortedCategories = categorySpending.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .collect(Collectors.toList());
            
            if (!sortedCategories.isEmpty()) {
                var top = sortedCategories.get(0);
                topCategory = top.getKey();
                topAmount = top.getValue();
                
                int topPercentage = (int) ((topAmount * 100.0) / totalSpent);
                categoryAnalysis = String.format("%s 카테고리가 전체 지출의 %d%%를 차지합니다.", 
                    topCategory, topPercentage);
            }
        }

        // AI 프롬프트 구성
        prompt.append("당신은 개인 재무 관리 전문가입니다.\n\n");
        prompt.append("=== 사용자의 이번 달 소비 현황 ===\n");
        prompt.append("📊 총 지출 비율: 100%\n");
        
        if (budget != null && budget > 0) {
            prompt.append("💰 예산 대비 상태: ").append(budgetStatus).append("\n");
            prompt.append("📈 예산 사용률: ").append(ratio).append("%\n");
            int remainingRatio = 100 - ratio;
            if (remainingRatio > 0) {
                prompt.append("💵 남은 예산 비율: ").append(remainingRatio).append("%\n");
            }
        }
        
        prompt.append("\n=== 카테고리별 지출 상세 ===\n");
        if (categorySpending != null && !categorySpending.isEmpty()) {
            categorySpending.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .forEach(entry -> {
                    int percentage = (int) ((entry.getValue() * 100.0) / totalSpent);
                    prompt.append("• ").append(entry.getKey())
                          .append(": ").append(percentage).append("%\n");
                });
            
            prompt.append("\n🔍 핵심 발견: ").append(categoryAnalysis).append("\n");
        }

        prompt.append("\n=== 분석 요청 ===\n");
        prompt.append("위 소비 데이터를 바탕으로 다음을 제공하세요:\n\n");
        
        prompt.append("1️⃣ 소비 패턴 진단 (2-3문장)\n");
        prompt.append("   - 가장 큰 문제점이나 특이사항 지적\n");
        if (budget != null && budget > 0) {
            if (ratio > 100) {
                prompt.append("   - 예산 초과 원인 분석\n");
            } else if (ratio > 80) {
                prompt.append("   - 예산 관리 위험 요소 경고\n");
            }
        }
        prompt.append("\n");
        
        prompt.append("2️⃣ 맞춤형 절약 전략 3가지\n");
        prompt.append("   - 각 전략은 이모지(✅, 💡, ⚠️)로 시작\n");
        prompt.append("   - ").append(topCategory).append(" 카테고리 지출을 줄이는 구체적 방법 포함\n");
        prompt.append("   - 실제로 실천 가능한 액션 아이템 제시\n");
        prompt.append("   - 각 전략은 3-4문장으로 자세히 설명\n");
        prompt.append("   - 구체적인 금액은 절대 언급하지 마세요 (예: 10만원, $100 등)\n");
        prompt.append("\n");
        
        prompt.append("3️⃣ 다음 달 목표 제안 (1-2문장)\n");
        if (budget != null && budget > 0) {
            prompt.append("   - 현재 예산 사용률(").append(ratio)
                  .append("%)을 기준으로 개선 목표 제시\n");
        }
        prompt.append("\n");
        
        prompt.append("⚠️ 주의사항:\n");
        prompt.append("- 높임말 사용 (예: ~해요, ~하세요)\n");
        prompt.append("- 비율(%)은 언급 가능하지만 구체적인 금액은 절대 언급 금지\n");
        prompt.append("- 실천 가능한 조언만 제공\n");
        prompt.append("- 부정적 표현보다 긍정적 대안 제시\n\n");
        
        prompt.append("답변:");

        return prompt.toString();
    }

    private String getDefaultStrategy(Map<String, Object> userData) {
        Integer totalSpent = (Integer) userData.get("totalSpent");
        Integer budget = (Integer) userData.get("budget");
        Map<String, Integer> categorySpending = (Map<String, Integer>) userData.get("categorySpending");
        
        StringBuilder strategy = new StringBuilder();
        
        if (totalSpent == null || totalSpent == 0) {
            strategy.append("✅ 가계부 시작을 축하드려요! 첫 걸음이 가장 중요합니다.\n\n");
            strategy.append("💡 매일 지출을 기록하는 습관을 들여보세요. 아침에 전날 지출을 5분만 투자해서 입력하면 됩니다. ");
            strategy.append("작은 금액도 모두 기록하는 게 핵심이에요.\n\n");
            strategy.append("⚠️ 예산을 먼저 설정해보세요. 월급의 70%는 필수 지출, 20%는 저축, 10%는 여유 자금으로 ");
            strategy.append("나누는 게 일반적입니다. 본인의 상황에 맞게 조절해보세요!");
            return strategy.toString();
        }

        // 예산 상태 분석
        strategy.append("📊 이번 달 소비 분석\n\n");
        
        if (budget != null && budget > 0) {
            int ratio = (int) ((totalSpent * 100.0) / budget);
            int remainingRatio = 100 - ratio;
            
            if (ratio > 100) {
                int overRatio = ratio - 100;
                strategy.append("⚠️ 예산을 ").append(overRatio).append("% 초과했어요! ");
                strategy.append("이번 달 남은 기간 동안은 필수 지출만 하고 충동구매는 꼭 참아보세요. ");
                strategy.append("카드 대신 현금을 쓰면 지출을 더 잘 통제할 수 있습니다.\n\n");
            } else if (ratio > 80) {
                strategy.append("⚠️ 예산의 ").append(ratio).append("%를 사용 중이에요. ");
                strategy.append("아직 ").append(remainingRatio).append("%가 남았지만 ");
                strategy.append("신중하게 써야 합니다. 이번 주는 외식을 줄이고 집밥을 먹어보는 건 어떨까요? ");
                strategy.append("불필요한 구독 서비스가 있다면 지금 정리하는 게 좋습니다.\n\n");
            } else {
                strategy.append("✅ 예산 관리 잘하고 계세요! ").append(ratio).append("% 사용 중이고 ");
                strategy.append("아직 ").append(remainingRatio).append("%가 남았어요. ");
                strategy.append("이 페이스를 유지하면서 남은 예산은 비상금으로 모아두면 좋겠습니다. ");
                strategy.append("계속 이렇게 관리하면 금방 재테크 고수가 될 거예요!\n\n");
            }
        }

        // 카테고리별 분석
        if (categorySpending != null && !categorySpending.isEmpty()) {
            var sortedCategories = categorySpending.entrySet().stream()
                .sorted((e1, e2) -> e2.getValue().compareTo(e1.getValue()))
                .limit(3)
                .collect(Collectors.toList());
            
            if (!sortedCategories.isEmpty()) {
                var top = sortedCategories.get(0);
                int topPercentage = (int) ((top.getValue() * 100.0) / totalSpent);
                
                strategy.append("💡 ").append(top.getKey()).append(" 카테고리에서 ");
                strategy.append("전체의 ").append(topPercentage).append("%를 사용하셨어요. ");
                
                // 카테고리별 맞춤 조언
                switch (top.getKey()) {
                    case "식비":
                    case "외식":
                        strategy.append("배달 대신 직접 요리하거나, 외식 횟수를 주 1-2회로 제한해보세요. ");
                        strategy.append("점심은 도시락을 싸가면 상당한 금액을 절약할 수 있습니다.\n\n");
                        break;
                    case "교통":
                        strategy.append("택시 대신 대중교통을 이용하거나, 가까운 거리는 걸어다녀보세요. ");
                        strategy.append("자전거나 전동킥보드도 좋은 대안입니다.\n\n");
                        break;
                    case "쇼핑":
                    case "의류":
                        strategy.append("충동구매를 줄이기 위해 24시간 규칙을 써보세요. 사고 싶은 게 있으면 ");
                        strategy.append("하루 기다렸다가 정말 필요한지 다시 생각해보는 것입니다.\n\n");
                        break;
                    case "문화":
                    case "여가":
                        strategy.append("OTT 구독이 여러 개면 1-2개로 줄이고, 무료 문화 행사나 ");
                        strategy.append("도서관을 활용해보세요. 재미는 그대로인데 비용은 확 줄어들 거예요.\n\n");
                        break;
                    default:
                        strategy.append("이 부분 지출을 10-20% 줄이는 걸 목표로 해보세요. ");
                        strategy.append("작은 변화가 모여서 큰 절약이 됩니다.\n\n");
                }
            }
        }

        // 실천 가능한 맞춤형 팁
        strategy.append("✅ 맞춤 실천 팁\n\n");
        
        // 예산 상태에 따른 팁
        if (budget != null && budget > 0) {
            int ratio = (int) ((totalSpent * 100.0) / budget);
            
            if (ratio > 100) {
                strategy.append("1. 이번 달은 예산 초과 상태이니, 남은 기간 동안 ");
                strategy.append("'필수 지출만 하기 챌린지'를 해보세요. ");
                strategy.append("식료품은 리스트를 작성해서 계획적으로 구매하고, ");
                strategy.append("외출 시 지갑에 필요한 만큼만 넣고 다니면 충동구매를 막을 수 있어요.\n\n");
            } else if (ratio > 80) {
                strategy.append("1. 예산 마감이 가까워지고 있으니, 이번 주부터는 ");
                strategy.append("'일주일 no-spend 챌린지'를 시도해보세요. ");
                strategy.append("집에 있는 재료로 식사하고, 무료 활동을 즐기면서 ");
                strategy.append("예산 내에서 마무리할 수 있습니다.\n\n");
            } else {
                strategy.append("1. 예산 관리를 잘하고 계시니, 매주 일요일 저녁 ");
                strategy.append("5분만 투자해서 지난 주 지출을 리뷰해보세요. ");
                strategy.append("이 습관을 유지하면 장기적으로 더 효과적인 관리가 가능합니다.\n\n");
            }
        } else {
            strategy.append("1. 아직 예산이 설정되지 않았다면, 지난 3개월 평균 지출을 ");
            strategy.append("기준으로 현실적인 예산을 세워보세요. ");
            strategy.append("예산이 있어야 목표가 생기고 절약 동기도 명확해집니다.\n\n");
        }
        
        // 최고 지출 카테고리에 따른 팁
        if (categorySpending != null && !categorySpending.isEmpty()) {
            var topEntry = categorySpending.entrySet().stream()
                .max((e1, e2) -> e1.getValue().compareTo(e2.getValue()))
                .orElse(null);
            
            if (topEntry != null) {
                String category = topEntry.getKey();
                
                strategy.append("2. ");
                switch (category) {
                    case "식비":
                    case "외식":
                        strategy.append("식비 지출이 가장 높으시네요. ");
                        strategy.append("주말에 한 주 식단을 미리 계획하고 장을 보면 ");
                        strategy.append("불필요한 배달이나 충동적인 외식을 줄일 수 있어요. ");
                        strategy.append("'집밥 먹는 날' 목표를 정해보는 것도 좋습니다.\n\n");
                        break;
                    case "교통":
                        strategy.append("교통비 지출이 가장 높으시네요. ");
                        strategy.append("출퇴근 경로를 재점검해서 더 효율적인 방법이 있는지 확인해보세요. ");
                        strategy.append("카풀이나 자전거 이용, 한 번에 여러 용무 처리하기 등으로 ");
                        strategy.append("이동 횟수를 줄일 수 있습니다.\n\n");
                        break;
                    case "쇼핑":
                    case "의류":
                        strategy.append("쇼핑 지출이 가장 높으시네요. ");
                        strategy.append("구매 전 '24시간 대기 규칙'을 적용해보세요. ");
                        strategy.append("장바구니에 담고 하루 뒤에 정말 필요한지 다시 생각하면 ");
                        strategy.append("충동구매를 많이 줄일 수 있어요.\n\n");
                        break;
                    case "문화":
                    case "여가":
                        strategy.append("문화/여가 지출이 가장 높으시네요. ");
                        strategy.append("구독 서비스를 점검해서 실제로 사용하지 않는 것은 해지하고, ");
                        strategy.append("무료 문화 행사나 도서관 프로그램을 활용하면 ");
                        strategy.append("비용을 줄이면서도 여가를 즐길 수 있어요.\n\n");
                        break;
                    case "생활":
                    case "주거":
                        strategy.append("생활비 지출이 가장 높으시네요. ");
                        strategy.append("정기적으로 나가는 고정비(통신비, 보험료, 구독료)를 ");
                        strategy.append("점검해보세요. 더 저렴한 요금제로 변경하거나 ");
                        strategy.append("불필요한 서비스를 해지하면 매달 자동으로 절약됩니다.\n\n");
                        break;
                    default:
                        strategy.append("현재 ").append(category).append(" 지출이 가장 높으시네요. ");
                        strategy.append("이 카테고리에서 꼭 필요한 지출과 줄일 수 있는 지출을 ");
                        strategy.append("구분해서 정리해보세요. 작은 변화들이 모이면 큰 차이를 만듭니다.\n\n");
                }
            }
        } else {
            strategy.append("2. 고정 지출(월세, 통신비, 구독료)과 변동 지출(식비, 교통비)을 구분해서 관리하세요. ");
            strategy.append("고정 지출은 줄이기 어렵지만, 변동 지출은 스스로 컨트롤할 수 있습니다.\n\n");
        }
        
        // 공통 저축 팁 (예산 상태에 따라 조정)
        strategy.append("3. ");
        if (budget != null && budget > 0) {
            int ratio = (int) ((totalSpent * 100.0) / budget);
            if (ratio <= 80) {
                strategy.append("예산 관리를 잘하고 계시니, 남은 예산의 일부를 ");
                strategy.append("자동이체로 저축 계좌로 옮겨보세요. ");
                strategy.append("작은 금액이라도 매달 꾸준히 모으면 ");
                strategy.append("1년 후엔 목돈이 됩니다!");
            } else {
                strategy.append("다음 달부터는 월급 받자마자 ");
                strategy.append("소액이라도 자동이체로 저축을 시작해보세요. ");
                strategy.append("'지출하고 남은 돈을 저축'하는 게 아니라 ");
                strategy.append("'저축하고 남은 돈으로 지출'하는 습관이 중요합니다!");
            }
        } else {
            strategy.append("소액이라도 매달 자동이체로 저축을 시작해보세요. ");
            strategy.append("월급 받자마자 일정 비율을 따로 떼어놓으면, ");
            strategy.append("1년 후엔 꽤 큰 금액이 모일 거예요!");
        }

        return strategy.toString();
    }
}
