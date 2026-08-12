import { useState } from "react";
import {
  useNavigate,
} from "react-router-dom";
import { useAuth } from "../context/useAuth";
import {
  saveProviderProfile,
} from "../services/providerProfileService";

function ProviderProfilePage() {
  const navigate = useNavigate();

  const {
    user,
    updateRoles,
  } = useAuth();

  const [formData, setFormData] =
    useState({
      businessName: "",
      description: "",
      providerType: "BOTH",
      serviceAddress: "",
      village: "",
      district: "",
      state: "Telangana",
      postalCode: "",
    });

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  const [success, setSuccess] =
    useState("");

  function handleChange(event) {
    const {
      name,
      value,
    } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setSuccess("");
    setSaving(true);

    try {
      const response =
        await saveProviderProfile(
          formData
        );

      updateRoles(response.roles);

      setSuccess(
        "Provider profile saved successfully."
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setSaving(false);
    }
  }

  function goToManagementPage() {
    if (
      formData.providerType ===
      "EQUIPMENT_OWNER"
    ) {
      navigate("/provider/equipment");
      return;
    }

    if (
      formData.providerType ===
      "PRODUCT_SELLER"
    ) {
      navigate("/provider/products");
      return;
    }

    navigate("/");
  }

  return (
    <main className="provider-profile-page">
      <section className="provider-profile-header">
        <p className="products-label">
          Provider setup
        </p>

        <h1>Sell or Rent</h1>

        <p>
          Create your provider profile to
          rent equipment or sell farming
          products through AgriSeva.
        </p>
      </section>

      <section className="provider-profile-content">
        <form
          className="provider-profile-form"
          onSubmit={handleSubmit}
        >
          <div className="provider-user-info">
            <strong>
              {user?.fullName}
            </strong>

            <span>
              {user?.email}
            </span>
          </div>

          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          {success && (
            <div className="success-message">
              {success}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="providerType">
              What do you want to do?
            </label>

            <select
              id="providerType"
              name="providerType"
              value={
                formData.providerType
              }
              onChange={handleChange}
              required
            >
              <option value="EQUIPMENT_OWNER">
                Rent Equipment
              </option>

              <option value="PRODUCT_SELLER">
                Sell Products
              </option>

              <option value="BOTH">
                Rent Equipment and Sell
                Products
              </option>
            </select>
          </div>

          <div className="form-group">
            <label htmlFor="businessName">
              Business name
            </label>

            <input
              id="businessName"
              name="businessName"
              type="text"
              value={
                formData.businessName
              }
              onChange={handleChange}
              maxLength={150}
              placeholder="Example: Sharath Agro Store"
            />
          </div>

          <div className="form-group">
            <label htmlFor="description">
              Description
            </label>

            <textarea
              id="description"
              name="description"
              value={
                formData.description
              }
              onChange={handleChange}
              maxLength={500}
              rows={4}
              placeholder="Tell farmers about your services"
            />
          </div>

          <div className="form-group">
            <label htmlFor="serviceAddress">
              Service address
            </label>

            <input
              id="serviceAddress"
              name="serviceAddress"
              type="text"
              value={
                formData.serviceAddress
              }
              onChange={handleChange}
              maxLength={255}
              required
              placeholder="Main Road"
            />
          </div>

          <div className="provider-location-grid">
            <div className="form-group">
              <label htmlFor="village">
                Village
              </label>

              <input
                id="village"
                name="village"
                type="text"
                value={
                  formData.village
                }
                onChange={handleChange}
                maxLength={100}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="district">
                District
              </label>

              <input
                id="district"
                name="district"
                type="text"
                value={
                  formData.district
                }
                onChange={handleChange}
                maxLength={100}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="state">
                State
              </label>

              <input
                id="state"
                name="state"
                type="text"
                value={
                  formData.state
                }
                onChange={handleChange}
                maxLength={100}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="postalCode">
                Postal code
              </label>

              <input
                id="postalCode"
                name="postalCode"
                type="text"
                value={
                  formData.postalCode
                }
                onChange={handleChange}
                maxLength={10}
                required
              />
            </div>
          </div>

          <div className="provider-form-actions">
            <button
              type="submit"
              className="primary-button"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : "Save Provider Profile"}
            </button>

            {success && (
              <button
                type="button"
                className="secondary-button"
                onClick={
                  goToManagementPage
                }
              >
                Continue
              </button>
            )}
          </div>
        </form>
      </section>
    </main>
  );
}

export default ProviderProfilePage;