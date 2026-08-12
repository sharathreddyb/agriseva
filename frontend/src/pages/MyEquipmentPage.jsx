import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
} from "react-router-dom";
import {
  deactivateEquipment,
  getMyEquipment,
} from "../services/equipmentService";

function MyEquipmentPage() {
  const navigate = useNavigate();

  const [equipment, setEquipment] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [
    deactivatingId,
    setDeactivatingId,
  ] = useState(null);

  useEffect(() => {
    let ignore = false;

    async function fetchEquipment() {
      try {
        const response =
          await getMyEquipment();

        if (!ignore) {
          setEquipment(response || []);
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
  }, []);

  async function handleDeactivate(
    equipmentId
  ) {
    const confirmed =
      window.confirm(
        "Deactivate this equipment?"
      );

    if (!confirmed) {
      return;
    }

    setError("");
    setDeactivatingId(equipmentId);

    try {
      await deactivateEquipment(
        equipmentId
      );

      setEquipment((current) =>
        current.map((item) =>
          item.id === equipmentId
            ? {
                ...item,
                active: false,
              }
            : item
        )
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setDeactivatingId(null);
    }
  }

  return (
    <main className="my-equipment-page">
      <section className="my-equipment-header">
        <div>
          <p className="products-label">
            Equipment owner
          </p>

          <h1>My Equipment</h1>

          <p>
            Manage the equipment you rent
            through AgriSeva.
          </p>
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={() =>
            navigate(
              "/provider/equipment/new"
            )
          }
        >
          Add Equipment
        </button>
      </section>

      <section className="my-equipment-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading equipment...</p>
        ) : equipment.length === 0 ? (
          <div className="product-empty">
            <h2>No equipment yet</h2>

            <p>
              Add your first equipment
              listing to start renting.
            </p>
          </div>
        ) : (
          <div className="my-equipment-grid">
            {equipment.map(
              (item) => (
                <article
                  key={item.id}
                  className="my-equipment-card"
                >
                  <div className="my-equipment-card-header">
                    <div>
                      <p className="product-category">
                        {item.category}
                      </p>

                      <h2>
                        {item.name}
                      </h2>
                    </div>

                    <div className="equipment-badges">
                      <span
                        className={`equipment-status-badge equipment-status-${item.status.toLowerCase()}`}
                      >
                        {item.status}
                      </span>

                      <span
                        className={
                          item.active
                            ? "product-active-badge"
                            : "product-inactive-badge"
                        }
                      >
                        {item.active
                          ? "ACTIVE"
                          : "INACTIVE"}
                      </span>
                    </div>
                  </div>

                  <p className="my-equipment-description">
                    {item.description ||
                      "No description provided."}
                  </p>

                  <div className="my-equipment-details">
                    <div>
                      <span>
                        Rental price
                      </span>

                      <strong>
                        ₹
                        {
                          item.rentalPricePerDay
                        }{" "}
                        / day
                      </strong>
                    </div>

                    <div>
                      <span>
                        Security deposit
                      </span>

                      <strong>
                        ₹
                        {item.securityDeposit ??
                          0}
                      </strong>
                    </div>

                    <div>
                      <span>Location</span>

                      <strong>
                        {item.village},{" "}
                        {item.district}
                      </strong>
                    </div>
                  </div>

                  <div className="my-equipment-actions">
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() =>
                        navigate(
                          `/provider/equipment/${item.id}/edit`
                        )
                      }
                    >
                      Edit
                    </button>

                    {item.active && (
                      <button
                        type="button"
                        className="deactivate-product-button"
                        disabled={
                          deactivatingId ===
                          item.id
                        }
                        onClick={() =>
                          handleDeactivate(
                            item.id
                          )
                        }
                      >
                        {deactivatingId ===
                        item.id
                          ? "Deactivating..."
                          : "Deactivate"}
                      </button>
                    )}
                  </div>
                </article>
              )
            )}
          </div>
        )}
      </section>
    </main>
  );
}

export default MyEquipmentPage;