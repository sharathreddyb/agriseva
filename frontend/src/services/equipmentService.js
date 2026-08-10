import { apiRequest } from "./api";

export async function searchEquipment(
  filters = {}
) {
  const params = new URLSearchParams();

  if (filters.category) {
    params.append(
      "category",
      filters.category
    );
  }

  if (filters.district) {
    params.append(
      "district",
      filters.district
    );
  }

  if (filters.village) {
    params.append(
      "village",
      filters.village
    );
  }

  if (filters.status) {
    params.append(
      "status",
      filters.status
    );
  }

  if (filters.keyword) {
    params.append(
      "keyword",
      filters.keyword
    );
  }

  const queryString =
    params.toString();

  const endpoint = queryString
    ? `/equipment?${queryString}`
    : "/equipment";

  return apiRequest(endpoint);
}

export async function getEquipmentById(
  equipmentId
) {
  return apiRequest(
    `/equipment/${equipmentId}`
  );
}

export async function getMyEquipment() {
  return apiRequest(
    "/equipment/mine"
  );
}

export async function createEquipment(
  request
) {
  return apiRequest(
    "/equipment",
    {
      method: "POST",
      body: JSON.stringify(request),
    }
  );
}

export async function updateEquipment(
  equipmentId,
  request
) {
  return apiRequest(
    `/equipment/${equipmentId}`,
    {
      method: "PUT",
      body: JSON.stringify(request),
    }
  );
}

export async function deactivateEquipment(
  equipmentId
) {
  return apiRequest(
    `/equipment/${equipmentId}`,
    {
      method: "DELETE",
    }
  );
}