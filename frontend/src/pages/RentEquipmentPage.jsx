import {
  useEffect,
  useState,
} from "react";
import {
  Link,
  useNavigate,
  useParams,
} from "react-router-dom";
import { getEquipmentById } from "../services/equipmentService";
import { createRental } from "../services/rentalService";

function RentEquipmentPage() {
  const { equipmentId } = useParams();
  const navigate = useNavigate();

  const [equipment, setEquipment] =
    useState(null);

  const [formData, setFormData] =
    useState({
      startDate: "",
      endDate: "",
      farmerNote: "",
    });

  const [loading, setLoading] =
    useState(true);

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchEquipment() {
      try {
        const response =
          await getEquipmentById(
            equipmentId
          );

        if (!ignore) {
          setEquipment(response);
        }
      } catch (exception) {
        if (!ignore) {
          setError(exception.message);
        }
      } finally {
        if (!ignore) {
          setLoading(false);
        }
      }
    }

    fetchEquipment();

    return () => {
      ignore = true;
    };
  }, [equipmentId]);

  function handleChange(event) {
    const { name, value } =
      event.target;

    setFormData((current) => ({
      ...current,
      [name]: value,
    }));
  }

  async function handleSubmit(event) {
    event.preventDefault();

    setError("");

    if (
      formData.endDate <
      formData.startDate
    ) {
      setError(
        "End date must be on or after the start date."
      );
      return;
    }

    setSubmitting(true);

    try {
      await createRental({
        equipmentId:
          Number(equipmentId),
        startDate:
          formData.startDate,
        endDate:
          formData.endDate,
        farmerNote:
          formData.farmerNote,
      });

      navigate("/rentals");
    } catch (exception) {
      setError(exception.message);
    } finally {
      setSubmitting(false);
    }
  }

  const today =
    new Date()
      .toISOString()
      .split("T")[0];

  if (loading) {
    return (
      <main className="page-container">
        <p>Loading equipment...</p>
      </main>
    );
  }

  if (!equipment) {
    return (
      <main className="page-container">
        <div className="error-message">
          {error ||
            "Equipment could not be loaded."}
        </div>
      </main>
    );
  }

  return (
    <main className="rental-page">
      <section className="rental-form-card">
        <div className="rental-heading">
          <p className="equipment-label">
            Rental request
          </p>

          <h1>
            Rent {equipment.name}
          </h1>

          <p>
            Send a rental request to{" "}
            {equipment.ownerName}.
          </p>
        </div>

        <div className="rental-equipment-summary">
          <div>
            <span>Category</span>
            <strong>
              {equipment.category}
            </strong>
          </div>

          <div>
            <span>Price per day</span>
            <strong>
              ₹
              {
                equipment.rentalPricePerDay
              }
            </strong>
          </div>

          <div>
            <span>Security deposit</span>
            <strong>
              ₹
              {equipment.securityDeposit ??
                0}
            </strong>
          </div>

          <div>
            <span>Location</span>
            <strong>
              {[
                equipment.village,
                equipment.district,
              ]
                .filter(Boolean)
                .join(", ") ||
                "Not provided"}
            </strong>
          </div>
        </div>

        {equipment.status !==
          "AVAILABLE" && (
          <div className="error-message">
            This equipment is currently
            not available for rental.
          </div>
        )}

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        <form
          className="auth-form"
          onSubmit={handleSubmit}
        >
          <div className="form-row">
            <label>
              Start date

              <input
                type="date"
                name="startDate"
                value={
                  formData.startDate
                }
                onChange={handleChange}
                min={today}
                required
              />
            </label>

            <label>
              End date

              <input
                type="date"
                name="endDate"
                value={
                  formData.endDate
                }
                onChange={handleChange}
                min={
                  formData.startDate ||
                  today
                }
                required
              />
            </label>
          </div>

          <label>
            Note to equipment owner

            <textarea
              name="farmerNote"
              value={
                formData.farmerNote
              }
              onChange={handleChange}
              maxLength={500}
              rows={4}
              placeholder="Add any details about your rental requirement"
            />
          </label>

          <div className="rental-actions">
            <button
              type="submit"
              className="auth-submit-button"
              disabled={
                submitting ||
                equipment.status !==
                  "AVAILABLE"
              }
            >
              {submitting
                ? "Sending request..."
                : "Send Rental Request"}
            </button>

            <Link
              to="/equipment"
              className="secondary-button"
            >
              Back
            </Link>
          </div>
        </form>
      </section>
    </main>
  );
}

export default RentEquipmentPage;