import { useState } from "react";
import { Link, useNavigate } from "react-router-dom";
import { apiRequest } from "../services/api";

function RegisterPage() {
  const navigate = useNavigate();

  const [formData, setFormData] = useState({
    fullName: "",
    email: "",
    phoneNumber: "",
    password: "",
    addressLine: "",
    village: "",
    district: "",
    state: "",
    postalCode: "",
  });

  const [error, setError] = useState("");
  const [loading, setLoading] = useState(false);

  function handleChange(event) {
    const { name, value } = event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");
    setLoading(true);

    try {
      await apiRequest(
        "/users/register",
        {
          method: "POST",
          body: JSON.stringify(formData),
        }
      );

      navigate("/login");
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  return (
    <main className="auth-page">
      <section className="auth-card register-card">
        <div className="auth-heading">
          <p className="auth-label">
            Join AgriSeva
          </p>

          <h1>Create Account</h1>

          <p>
            Register as a farmer first.
            You can activate seller or equipment
            owner features later.
          </p>
        </div>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >
          <label>
            Full name

            <input
              type="text"
              name="fullName"
              value={formData.fullName}
              onChange={handleChange}
              maxLength={100}
              required
            />
          </label>

          <label>
            Email

            <input
              type="email"
              name="email"
              value={formData.email}
              onChange={handleChange}
              maxLength={150}
              required
            />
          </label>

          <label>
            Phone number

            <input
              type="tel"
              name="phoneNumber"
              value={formData.phoneNumber}
              onChange={handleChange}
              pattern="[6-9][0-9]{9}"
              maxLength={10}
              placeholder="10-digit mobile number"
              required
            />
          </label>

          <label>
            Password

            <input
              type="password"
              name="password"
              value={formData.password}
              onChange={handleChange}
              minLength={8}
              maxLength={72}
              required
            />
          </label>

          <label>
            Address

            <input
              type="text"
              name="addressLine"
              value={formData.addressLine}
              onChange={handleChange}
              maxLength={255}
            />
          </label>

          <div className="form-row">
            <label>
              Village

              <input
                type="text"
                name="village"
                value={formData.village}
                onChange={handleChange}
                maxLength={100}
              />
            </label>

            <label>
              District

              <input
                type="text"
                name="district"
                value={formData.district}
                onChange={handleChange}
                maxLength={100}
              />
            </label>
          </div>

          <div className="form-row">
            <label>
              State

              <input
                type="text"
                name="state"
                value={formData.state}
                onChange={handleChange}
                maxLength={100}
              />
            </label>

            <label>
              Postal code

              <input
                type="text"
                name="postalCode"
                value={formData.postalCode}
                onChange={handleChange}
                pattern="[1-9][0-9]{5}"
                maxLength={6}
              />
            </label>
          </div>

          <button
            type="submit"
            className="auth-submit-button"
            disabled={loading}
          >
            {loading
              ? "Creating account..."
              : "Create Account"}
          </button>
        </form>

        <p className="auth-footer">
          Already have an account?{" "}
          <Link to="/login">
            Login
          </Link>
        </p>
      </section>
    </main>
  );
}

export default RegisterPage;