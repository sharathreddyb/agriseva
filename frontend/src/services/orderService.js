import { apiRequest } from "./api";

export async function createOrder(request) {
  return apiRequest("/orders", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function getMyOrders() {
  return apiRequest("/orders/mine");
}

export async function cancelOrder(orderId) {
  return apiRequest(
    `/orders/${orderId}/cancel`,
    {
      method: "PUT",
    }
  );
}

export async function getReceivedOrders() {
  return apiRequest(
    "/orders/received"
  );
}

export async function updateOrderStatus(
  orderId,
  request
) {
  return apiRequest(
    `/orders/${orderId}/status`,
    {
      method: "PUT",
      body: JSON.stringify(request),
    }
  );
}