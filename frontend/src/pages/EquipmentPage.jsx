import { useEffect, useState } from "react";
import { searchEquipment } from "../services/equipmentService";

function EquipmentPage() {
  const [equipment, setEquipment] = useState([]);

  const [filters, setFilters] = useState({
    keyword: "",
    category: "",
    district: "",
    village: "",
    status: "",
  });

  const [loading, setLoading] = useState(true);
  const [error, setError] = useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchEquipment() {
      try {
        const response = await searchEquipment();

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

  async function loadEquipment(searchFilters) {
    setLoading(true);
    setError("");

    try {
      const response =
        await searchEquipment(searchFilters);

      setEquipment(response || []);
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  function handleChange(event) {
    const { name, value } = event.target;

    setFilters((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function handleSearch(event) {
    event.preventDefault();
    loadEquipment(filters);
  }

  function handleReset() {
    const emptyFilters = {
      keyword: "",
      category: "",
      district: "",
      village: "",
      status: "",
    };

    setFilters(emptyFilters);
    loadEquipment(emptyFilters);
  }

  return (
    <main className="equipment-page">
      <section className="equipment-header">
        <div>
          <p className="equipment-label">
            Farm equipment
          </p>

          <h1>Find Equipment for Rent</h1>

          <p>
            Search tractors, harvesters,
            sprayers and other agricultural
            equipment available from local
            owners.
          </p>
        </div>
      </section>

      <section className="equipment-content">
        <form
          className="equipment-filters"
          onSubmit={handleSearch}
        >
          <div className="equipment-filter-grid">
            <label>
              Keyword

              <input
                type="text"
                name="keyword"
                value={filters.keyword}
                onChange={handleChange}
                placeholder="Tractor, sprayer..."
              />
            </label>

            <label>
              Category

              <select
                name="category"
                value={filters.category}
                onChange={handleChange}
              >
                <option value="">
                  All categories
                </option>

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
            </label>

            <label>
              District

              <input
                type="text"
                name="district"
                value={filters.district}
                onChange={handleChange}
                placeholder="Siddipet"
              />
            </label>

            <label>
              Village

              <input
                type="text"
                name="village"
                value={filters.village}
                onChange={handleChange}
                placeholder="Village"
              />
            </label>

            <label>
              Status

              <select
                name="status"
                value={filters.status}
                onChange={handleChange}
              >
                <option value="">
                  Any status
                </option>

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
            </label>
          </div>

          <div className="equipment-filter-actions">
            <button
              type="submit"
              className="primary-button"
            >
              Search
            </button>

            <button
              type="button"
              className="secondary-button"
              onClick={handleReset}
            >
              Reset
            </button>
          </div>
        </form>

        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p className="equipment-message">
            Loading equipment...
          </p>
        ) : equipment.length === 0 ? (
          <div className="equipment-empty">
            <h2>No equipment found</h2>

            <p>
              Try changing your search filters.
            </p>
          </div>
        ) : (
          <div className="equipment-grid">
            {equipment.map((item) => (
              <article
                className="equipment-card"
                key={item.id}
              >
                <div className="equipment-image">
                  {item.imageUrl ? (
                    <img
                      src={item.imageUrl}
                      alt={item.name}
                    />
                  ) : (
                    <div className="equipment-image-placeholder">
                      Equipment
                    </div>
                  )}
                </div>

                <div className="equipment-card-body">
                  <div className="equipment-card-top">
                    <span className="equipment-category">
                      {item.category}
                    </span>

                    <span
                      className={
                        item.status === "AVAILABLE"
                          ? "status-available"
                          : "status-unavailable"
                      }
                    >
                      {item.status}
                    </span>
                  </div>

                  <h2>{item.name}</h2>

                  <p className="equipment-description">
                    {item.description ||
                      "No description provided."}
                  </p>

                  <p className="equipment-price">
                    ₹{item.rentalPricePerDay}
                    <span> / day</span>
                  </p>

                  {item.securityDeposit != null && (
                    <p className="equipment-deposit">
                      Security deposit: ₹
                      {item.securityDeposit}
                    </p>
                  )}

                  <div className="equipment-location">
                    <strong>Location:</strong>{" "}
                    {[
                      item.village,
                      item.district,
                      item.state,
                    ]
                      .filter(Boolean)
                      .join(", ") || "Not provided"}
                  </div>

                  <div className="equipment-owner">
                    <strong>Owner:</strong>{" "}
                    {item.ownerName}
                  </div>
                </div>
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default EquipmentPage;