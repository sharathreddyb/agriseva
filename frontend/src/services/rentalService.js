import { apiRequest } from "./api";

export async function createRental(request) {
  return apiRequest("/rentals", {
    method: "POST",
    body: JSON.stringify(request),
  });
}

export async function getMyRentals() {
  return apiRequest("/rentals/mine");
}

export async function cancelRental(rentalId) {
  return apiRequest(
    `/rentals/${rentalId}/cancel`,
    {
      method: "PATCH",
    }
  );
}

export async function getOwnerRentalRequests() {
  return apiRequest("/rentals/owner");
}

export async function updateRentalStatus(
  rentalId,
  request
) {
  return apiRequest(
    `/rentals/${rentalId}/status`,
    {
      method: "PATCH",
      body: JSON.stringify(request),
    }
  );
}