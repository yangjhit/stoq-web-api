#!/bin/bash
echo "======================================"
echo "  集群和仓库管理接口测试"
echo "======================================"
echo ""

# 设置Token
TOKEN="eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiJ0ZXN0QGV4YW1wbGUuY29tIiwiaWF0IjoxNzYzMzAxMzIyLCJleHAiOjE3NjMzODc3MjJ9.naTz14nlXa5_n_5L0qLC4es1vu4X0sbGcZxNUUAEMnuupqUrTlPv1fe4AjjRJLtM_BvvoHP0vG7vRVGhKSZM2w"
BASE_URL="http://localhost:8080/api"

echo "========== 1. 创建第一个集群 =========="
echo "POST /clusters"
CLUSTER1=$(curl -s -X POST $BASE_URL/clusters \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "示例科技集群",
    "logo": "https://example.com/logo.png",
    "address": "北京市朝阳区123号",
    "countryId": 1,
    "city": "北京",
    "field": "科技",
    "employeeCount": 50,
    "type": "PROFESSIONAL",
    "registrationNumber": "REG-2024-001"
  }')
echo "$CLUSTER1" | python3 -m json.tool
CLUSTER1_ID=$(echo "$CLUSTER1" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "集群ID: $CLUSTER1_ID"
echo ""
sleep 1

echo "========== 2. 创建第二个集群 =========="
echo "POST /clusters"
CLUSTER2=$(curl -s -X POST $BASE_URL/clusters \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "另一家科技集群",
    "logo": "https://example.com/logo2.png",
    "address": "上海市浦东新区456号",
    "countryId": 1,
    "city": "上海",
    "field": "金融科技",
    "employeeCount": 100,
    "type": "PROFESSIONAL",
    "registrationNumber": "REG-2024-002"
  }')
echo "$CLUSTER2" | python3 -m json.tool
CLUSTER2_ID=$(echo "$CLUSTER2" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "集群ID: $CLUSTER2_ID"
echo ""
sleep 1

echo "========== 3. 获取所有集群 =========="
echo "GET /clusters"
curl -s -X GET $BASE_URL/clusters \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 4. 根据ID获取第一个集群 =========="
echo "GET /clusters/$CLUSTER1_ID"
curl -s -X GET $BASE_URL/clusters/$CLUSTER1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 5. 获取我创建的所有集群 =========="
echo "GET /clusters/my"
curl -s -X GET $BASE_URL/clusters/my \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 6. 在第一个集群中创建仓库 =========="
echo "POST /stoqs"
STOQ1=$(curl -s -X POST $BASE_URL/stoqs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"北京仓库\",
    \"description\": \"集群主要仓库\",
    \"administrator\": \"张三\",
    \"clusterId\": $CLUSTER1_ID
  }")
echo "$STOQ1" | python3 -m json.tool
STOQ1_ID=$(echo "$STOQ1" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "仓库ID: $STOQ1_ID"
echo ""
sleep 1

echo "========== 7. 在第一个集群中创建第二个仓库 =========="
echo "POST /stoqs"
STOQ2=$(curl -s -X POST $BASE_URL/stoqs \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d "{
    \"name\": \"上海仓库\",
    \"description\": \"上海分仓\",
    \"administrator\": \"李四\",
    \"clusterId\": $CLUSTER1_ID
  }")
echo "$STOQ2" | python3 -m json.tool
STOQ2_ID=$(echo "$STOQ2" | python3 -c "import sys, json; print(json.load(sys.stdin).get('id', ''))")
echo "仓库ID: $STOQ2_ID"
echo ""
sleep 1

echo "========== 8. 获取第一个集群的所有仓库 =========="
echo "GET /stoqs/cluster/$CLUSTER1_ID"
curl -s -X GET $BASE_URL/stoqs/cluster/$CLUSTER1_ID \
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
    \"clusterId\": $CLUSTER1_ID
  }" | python3 -m json.tool
echo ""
sleep 1

echo "========== 12. 更新第一个集群信息 =========="
echo "PUT /clusters/$CLUSTER1_ID"
curl -s -X PUT $BASE_URL/clusters/$CLUSTER1_ID \
  -H "Authorization: Bearer $TOKEN" \
  -H "Content-Type: application/json" \
  -d '{
    "name": "示例科技集群-更新",
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
echo "GET /stoqs/cluster/$CLUSTER1_ID"
curl -s -X GET $BASE_URL/stoqs/cluster/$CLUSTER1_ID \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
echo ""
sleep 1

echo "========== 15. 删除第二个集群 =========="
echo "DELETE /clusters/$CLUSTER2_ID"
HTTP_CODE=$(curl -s -o /dev/null -w "%{http_code}" -X DELETE $BASE_URL/clusters/$CLUSTER2_ID \
  -H "Authorization: Bearer $TOKEN")
echo "HTTP状态码: $HTTP_CODE"
if [ "$HTTP_CODE" = "204" ]; then
  echo "✅ 集群删除成功"
else
  echo "❌ 集群删除失败"
fi
echo ""
sleep 1

echo "========== 16. 验证集群已删除 =========="
echo "GET /clusters/my"
curl -s -X GET $BASE_URL/clusters/my \
  -H "Authorization: Bearer $TOKEN" | python3 -m json.tool
