#!/bin/bash
echo "======================================"
echo "  公司和仓库管理接口测试"
echo "======================================"
echo ""

# 设置Token
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzYzMzAxMzIyLCJleHAiOjE3NjMzODc3MjJ9.naTz14nlXa5_n_5L0qLC4es1vu4X0sbGcZxNUUAEMnuupqUrTlPv1fe4AjjRJLtM_BvvoHP0vG7vRVGhKSZM2w"
BASE_URL="http://localhost:8080/api"

echo "========== 1. 创建第一个公司 =========="
echo "POST /companies"
COMPANY1=$(curl -s -X POST $BASE_URL/companies \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "示例科技公司",
    "logo": "https://example.com/logo.png",
    "address": "北京市朝阳区123号",
    "countryId": 1,
    "city": "北京",
    "field": "科技",
    "employeeCount": 50,
    "type": "PROFESSIONAL",
    "registrationNumber": "REG-2024-001"
  }')
echo "$COMPANY1" | python3 -m json.tool
COMPANY1_ID=$(echo "$COMPANY1" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "公司ID: $COMPANY1_ID"
echo ""
sleep 1

echo "========== 2. 创建第二个公司 =========="
echo "POST /companies"
COMPANY2=$(curl -s -X POST $BASE_URL/companies \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "另一家科技公司",
    "logo": "https://example.com/logo2.png",
    "address": "上海市浦东新区456号",
    "countryId": 1,
    "city": "上海",
    "field": "金融科技",
    "employeeCount": 100,
    "type": "PROFESSIONAL",
    "registrationNumber": "REG-2024-002"
  }')
echo "$COMPANY2" | python3 -m json.tool
COMPANY2_ID=$(echo "$COMPANY2" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "公司ID: $COMPANY2_ID"
echo ""
sleep 1

echo "========== 3. 获取所有公司 =========="
echo "GET /companies"
curl -s -X GET $BASE_URL/companies \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 4. 根据ID获取第一个公司 =========="
echo "GET /companies/$COMPANY1_ID"
curl -s -X GET $BASE_URL/companies/$COMPANY1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 5. 获取我创建的所有公司 =========="
echo "GET /companies/my"
curl -s -X GET $BASE_URL/companies/my \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 6. 在第一个公司中创建仓库 =========="
echo "POST /stoqs"
STOQ1=$(curl -s -X POST $BASE_URL/stoqs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"北京仓库\",
    \"description\": \"公司主要仓库\",
    \"administrator\": \"张三\",
    \"companyId\": $COMPANY1_ID
  }")
echo "$STOQ1" | python3 -m json.tool
STOQ1_ID=$(echo "$STOQ1" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "仓库ID: $STOQ1_ID"
echo ""
sleep 1

echo "========== 7. 在第一个公司中创建第二个仓库 =========="
echo "POST /stoqs"
STOQ2=$(curl -s -X POST $BASE_URL/stoqs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"上海仓库\",
    \"description\": \"上海分公司仓库\",
    \"administrator\": \"李四\",
    \"companyId\": $COMPANY1_ID
  }")
echo "$STOQ2" | python3 -m json.tool
STOQ2_ID=$(echo "$STOQ2" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "仓库ID: $STOQ2_ID"
echo ""
sleep 1

echo "========== 8. 获取第一个公司的所有仓库 =========="
echo "GET /stoqs/company/$COMPANY1_ID"
curl -s -X GET $BASE_URL/stoqs/company/$COMPANY1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 9. 根据ID获取第一个仓库 =========="
echo "GET /stoqs/$STOQ1_ID"
curl -s -X GET $BASE_URL/stoqs/$STOQ1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 10. 获取我创建的所有仓库 =========="
echo "GET /stoqs/my"
curl -s -X GET $BASE_URL/stoqs/my \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 11. 更新第一个仓库信息 =========="
echo "PUT /stoqs/$STOQ1_ID"
curl -s -X PUT $BASE_URL/stoqs/$STOQ1_ID \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"北京仓库-更新\",
    \"description\": \"更新后的仓库描述\",
    \"administrator\": \"王五\",
    \"companyId\": $COMPANY1_ID
  }" | python3 -m json.tool
echo ""
sleep 1

echo "========== 12. 更新第一个公司信息 =========="
echo "PUT /companies/$COMPANY1_ID"
curl -s -X PUT $BASE_URL/companies/$COMPANY1_ID \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "示例科技公司-更新",
    "logo": "https://example.com/logo-updated.png",
    "address": "北京市朝阳区789号",
    "countryId": 1,
    "city": "北京",
    "field": "科技和创新",
    "employeeCount": 100,
    "type": "PROFESSIONAL",
    "registrationNumber": "REG-2024-001-UPDATED"
  }' | python3 -m json.tool
echo ""
sleep 1

echo "========== 13. 删除第二个仓库 =========="
echo "DELETE /stoqs/$STOQ2_ID"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE $BASE_URL/stoqs/$STOQ2_ID \
  -H "Authorization: Bearer $TOKEN")
echo "HTTP状态码: $HTTP_CODE"
if [ "$HTTP_CODE" = "204" ]; then
  echo "✅ 仓库删除成功"
else
  echo "❌ 仓库删除失败"
fi
echo ""
sleep 1

echo "========== 14. 验证仓库已删除 =========="
echo "GET /stoqs/company/$COMPANY1_ID"
curl -s -X GET $BASE_URL/stoqs/company/$COMPANY1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 15. 删除第二个公司 =========="
echo "DELETE /companies/$COMPANY2_ID"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE $BASE_URL/companies/$COMPANY2_ID \
  -H "Authorization: Bearer $TOKEN")
echo "HTTP状态码: $HTTP_CODE"
if [ "$HTTP_CODE" = "204" ]; then
  echo "✅ 公司删除成功"
else
  echo "❌ 公司删除失败"
fi
echo ""
sleep 1

echo "========== 16. 验证公司已删除 =========="
echo "GET /companies/my"
curl -s -X GET $BASE_URL/companies/my \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""

echo "======================================"
echo "  测试完成!"
echo "======================================"
