import { apiRequest } from "./api";

export async function saveProviderProfile(
  request
) {
  return apiRequest(
    "/users/me/provider-profile",
    {
      method: "POST",
      body: JSON.stringify(request),
    }
  );
}