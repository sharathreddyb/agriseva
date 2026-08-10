import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
  useParams,
} from "react-router-dom";
import {
  createEquipment,
  getEquipmentById,
  updateEquipment,
} from "../services/equipmentService";

function EquipmentFormPage() {
  const navigate = useNavigate();
  const { equipmentId } = useParams();

  const isEditMode =
    Boolean(equipmentId);

  const [formData, setFormData] =
    useState({
      name: "",
      category: "TRACTOR",
      description: "",
      rentalPricePerDay: "",
      securityDeposit: "",
      status: "AVAILABLE",
      serviceAddress: "",
      village: "",
      district: "",
      state: "Telangana",
      postalCode: "",
      imageUrl: "",
    });

  const [loading, setLoading] =
    useState(isEditMode);

  const [saving, setSaving] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    if (!isEditMode) {
      return;
    }

    let ignore = false;

    async function fetchEquipment() {
      try {
        const equipment =
          await getEquipmentById(
            equipmentId
          );

        if (!ignore) {
          setFormData({
            name:
              equipment.name || "",
            category:
              equipment.category ||
              "TRACTOR",
            description:
              equipment.description ||
              "",
            rentalPricePerDay:
              equipment.rentalPricePerDay ??
              "",
            securityDeposit:
              equipment.securityDeposit ??
              "",
            status:
              equipment.status ||
              "AVAILABLE",
            serviceAddress:
              equipment.serviceAddress ||
              "",
            village:
              equipment.village || "",
            district:
              equipment.district || "",
            state:
              equipment.state ||
              "Telangana",
            postalCode:
              equipment.postalCode ||
              "",
            imageUrl:
              equipment.imageUrl || "",
          });
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
  }, [equipmentId, isEditMode]);

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
    setSaving(true);

    const request = {
      ...formData,
      rentalPricePerDay:
        Number(
          formData.rentalPricePerDay
        ),
      securityDeposit:
        formData.securityDeposit === ""
          ? null
          : Number(
              formData.securityDeposit
            ),
    };

    try {
      if (isEditMode) {
        await updateEquipment(
          equipmentId,
          request
        );
      } else {
        await createEquipment(
          request
        );
      }

      navigate(
        "/provider/equipment"
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <main className="equipment-form-page">
        <p>Loading equipment...</p>
      </main>
    );
  }

  return (
    <main className="equipment-form-page">
      <section className="equipment-form-header">
        <p className="products-label">
          Equipment owner
        </p>

        <h1>
          {isEditMode
            ? "Edit Equipment"
            : "Add Equipment"}
        </h1>

        <p>
          Enter the equipment details
          farmers will see when browsing.
        </p>
      </section>

      <section className="equipment-form-content">
        <form
          className="equipment-management-form"
          onSubmit={handleSubmit}
        >
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="name">
              Equipment name
            </label>

            <input
              id="name"
              name="name"
              type="text"
              value={formData.name}
              onChange={handleChange}
              maxLength={150}
              required
            />
          </div>

          <div className="equipment-form-grid">
            <div className="form-group">
              <label htmlFor="category">
                Category
              </label>

              <select
                id="category"
                name="category"
                value={
                  formData.category
                }
                onChange={handleChange}
                required
              >
                <option value="TRACTOR">
                  Tractor
                </option>

                <option value="HARVESTER">
                  Harvester
                </option>

                <option value="CULTIVATOR">
                  Cultivator
                </option>

                <option value="PLOUGH">
                  Plough
                </option>

                <option value="ROTAVATOR">
                  Rotavator
                </option>

                <option value="SEEDER">
                  Seeder
                </option>

                <option value="SPRAYER">
                  Sprayer
                </option>

                <option value="IRRIGATION_EQUIPMENT">
                  Irrigation Equipment
                </option>

                <option value="JCB">
                  JCB
                </option>

                <option value="OTHER">
                  Other
                </option>
              </select>
            </div>

            <div className="form-group">
              <label htmlFor="status">
                Status
              </label>

              <select
                id="status"
                name="status"
                value={formData.status}
                onChange={handleChange}
                required
              >
                <option value="AVAILABLE">
                  Available
                </option>

                <option value="UNAVAILABLE">
                  Unavailable
                </option>

                <option value="MAINTENANCE">
                  Maintenance
                </option>
              </select>
            </div>
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
              maxLength={1000}
              rows={4}
            />
          </div>

          <div className="equipment-form-grid">
            <div className="form-group">
              <label htmlFor="rentalPricePerDay">
                Rental price per day
              </label>

              <input
                id="rentalPricePerDay"
                name="rentalPricePerDay"
                type="number"
                min="0.01"
                step="0.01"
                value={
                  formData.rentalPricePerDay
                }
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="securityDeposit">
                Security deposit
              </label>

              <input
                id="securityDeposit"
                name="securityDeposit"
                type="number"
                min="0"
                step="0.01"
                value={
                  formData.securityDeposit
                }
                onChange={handleChange}
              />
            </div>
          </div>

          <div className="form-group">
            <label htmlFor="imageUrl">
              Image URL
            </label>

            <input
              id="imageUrl"
              name="imageUrl"
              type="url"
              value={
                formData.imageUrl
              }
              onChange={handleChange}
              maxLength={500}
              placeholder="https://example.com/equipment.jpg"
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
            />
          </div>

          <div className="equipment-form-grid">
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
                value={formData.state}
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

          <div className="equipment-form-actions">
            <button
              type="submit"
              className="primary-button"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : isEditMode
                  ? "Update Equipment"
                  : "Create Equipment"}
            </button>

            <button
              type="button"
              className="secondary-button"
              onClick={() =>
                navigate(
                  "/provider/equipment"
                )
              }
            >
              Cancel
            </button>
          </div>
        </form>
      </section>
    </main>
  );
}

export default EquipmentFormPage;