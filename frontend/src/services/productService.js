import { apiRequest } from "./api";

export async function searchProducts(
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

  if (filters.keyword) {
    params.append(
      "keyword",
      filters.keyword
    );
  }

  if (filters.minPrice) {
    params.append(
      "minPrice",
      filters.minPrice
    );
  }

  if (filters.maxPrice) {
    params.append(
      "maxPrice",
      filters.maxPrice
    );
  }

  if (filters.inStock !== "") {
    params.append(
      "inStock",
      filters.inStock
    );
  }

  const queryString =
    params.toString();

  const endpoint = queryString
    ? `/products?${queryString}`
    : "/products";

  return apiRequest(endpoint);
}

export async function getProductById(
  productId
) {
  return apiRequest(
    `/products/${productId}`
  );
}