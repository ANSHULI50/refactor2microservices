#!/bin/bash

if [ -z ${SERVICES_HOST} ]; then
    SERVICES_HOST=localhost
fi
if [ -z ${SERVICES_PORT} ]; then
    SERVICES_PORT=8080
fi
if [ -z ${USER_NAME} ]; then
    USER_NAME=admin
fi
if [ -z ${USER_PASS} ]; then
    USER_PASS=password
fi

AUTH_CLIENT_CRED=$(printf "web-client:secret" | base64)

echo -e '\n--------Get Token----------\n'

curl -X POST -H "Content-Type: application/json" -H "Authorization: Basic ${AUTH_CLIENT_CRED}" -D - --data '{"id":"'${USER_NAME}'","password":"'${USER_PASS}'"}' ${SERVICES_HOST}:${SERVICES_PORT}/auth/token
echo

ACCESS_TOKEN=$(curl -s -X POST -H "Authorization: Basic ${AUTH_CLIENT_CRED}" -H "Content-Type: application/json" --data '{"id":"'${USER_NAME}'","password":"'${USER_PASS}'"}' ${SERVICES_HOST}:${SERVICES_PORT}/auth/token | jq .access_token | tr -d '"')

echo -e '\n---------Get User Auth---------\n'

curl -X GET -D - ${SERVICES_HOST}:${SERVICES_PORT}/auth/token/user?access_token=$ACCESS_TOKEN
echo

echo -e '\n-------Get Products-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - ${SERVICES_HOST}:${SERVICES_PORT}/products
echo

echo -e '\n-------Add Products-----------\n'

curl -X PUT -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" --data '{"id":"product-1","name":"Product One","price":10.0}' -D - http://${SERVICES_HOST}:${SERVICES_PORT}/products/product-1
echo
curl -X PUT -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" --data '{"id":"product-2","name":"Product Two","price":100.0}' -D - http://${SERVICES_HOST}:${SERVICES_PORT}/products/product-2
echo

echo -e '\n-------Get Products-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/products
echo

echo -e '\n-------Get Product-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/products/product-1
echo

echo -e '\n-------Add Inventory-----------\n'

PROD1QTY=$(curl -s -X GET -H "Authorization: Bearer $ACCESS_TOKEN" http://${SERVICES_HOST}:${SERVICES_PORT}/inventory/product-1 | jq .quantity)
if [ "$PROD1QTY" -gt 100 ]; then
    echo -e "Inventory for product-1 already = $PROD1QTY. Not adding."
else
    echo "Adding inventory for product-1"
    curl -X PUT -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" --data '{"productId":"product-1","quantity":1000}' -D - http://${SERVICES_HOST}:${SERVICES_PORT}/inventory/product-1
fi
echo

PROD2QTY=$(curl -s -X GET -H "Authorization: Bearer $ACCESS_TOKEN" http://${SERVICES_HOST}:${SERVICES_PORT}/inventory/product-2 | jq .quantity)
if [ "$PROD2QTY" -gt 100 ]; then
    echo -e "Inventory for product-2 already = $PROD2QTY. Not adding."
else
    echo "Adding inventory for product-2"
    curl -X PUT -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/json" --data '{"productId":"product-2","quantity":1000}' -D - http://${SERVICES_HOST}:${SERVICES_PORT}/inventory/product-2
fi
echo

echo -e '\n-------Get All Inventory-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/inventory
echo


echo -e '\n-------Post Add to Cart-----------\n'

curl -X POST -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/x-www-form-urlencoded" --data '' -D - "http://${SERVICES_HOST}:${SERVICES_PORT}/carts?id=admin&productId=product-1&quantity=1"
echo
curl -X POST -H "Authorization: Bearer $ACCESS_TOKEN" -H "Content-Type: application/x-www-form-urlencoded" --data '' -D - "http://${SERVICES_HOST}:${SERVICES_PORT}/carts?id=admin&productId=product-2&quantity=2"
echo

echo -e '\n-------Get Cart-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/carts/admin
echo

echo -e '\n-------Create Order-----------\n'

curl -X POST -H "Authorization: Bearer $ACCESS_TOKEN" --data '' -D - http://${SERVICES_HOST}:${SERVICES_PORT}/orders/order/carts/admin
echo

echo -e '\n-------Get Orders-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/orders
echo

echo -e '\n-------Get All Inventory-----------\n'

curl -X GET -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/inventory
echo


#echo -e '\n-------Create Test Data-----------\n'
#curl -X DELETE -H "Authorization: Bearer $ACCESS_TOKEN" -D - http://${SERVICES_HOST}:${SERVICES_PORT}/admin/dataset
#curl -X POST -H "Content-Type: application/x-www-form-urlencoded" -H "Authorization: Bearer $ACCESS_TOKEN" -D - "http://${SERVICES_HOST}:${SERVICES_PORT}/admin/dataset?userCount=10&productCount=10"

echo -e '\n---------Done---------\n'
