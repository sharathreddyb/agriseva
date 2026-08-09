import {
  useEffect,
  useState,
} from "react";
import {
  cancelRental,
  getMyRentals,
} from "../services/rentalService";

function MyRentalsPage() {
  const [rentals, setRentals] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchRentals() {
      try {
        const response =
          await getMyRentals();

        if (!ignore) {
          setRentals(response || []);
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

    fetchRentals();

    return () => {
      ignore = true;
    };
  }, []);

  async function handleCancel(
    rentalId
  ) {
    setError("");

    try {
      const updatedRental =
        await cancelRental(rentalId);

      setRentals((current) =>
        current.map((rental) =>
          rental.id === rentalId
            ? updatedRental
            : rental
        )
      );
    } catch (exception) {
      setError(exception.message);
    }
  }

  function getStatusClass(status) {
    return `rental-status rental-status-${status.toLowerCase()}`;
  }

  return (
    <main className="rentals-page">
      <section className="rentals-header">
        <p className="equipment-label">
          My rentals
        </p>

        <h1>Rental Requests</h1>

        <p>
          View the equipment rental
          requests you have submitted.
        </p>
      </section>

      <section className="rentals-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading rentals...</p>
        ) : rentals.length === 0 ? (
          <div className="equipment-empty">
            <h2>No rental requests</h2>

            <p>
              Equipment you request for
              rental will appear here.
            </p>
          </div>
        ) : (
          <div className="rentals-list">
            {rentals.map((rental) => (
              <article
                className="rental-card"
                key={rental.id}
              >
                <div className="rental-card-header">
                  <div>
                    <p className="rental-id">
                      Rental #
                      {rental.id}
                    </p>

                    <h2>
                      {
                        rental.equipmentName
                      }
                    </h2>
                  </div>

                  <span
                    className={getStatusClass(
                      rental.status
                    )}
                  >
                    {rental.status}
                  </span>
                </div>

                <div className="rental-details-grid">
                  <div>
                    <span>Owner</span>
                    <strong>
                      {rental.ownerName}
                    </strong>
                  </div>

                  <div>
                    <span>Start date</span>
                    <strong>
                      {rental.startDate}
                    </strong>
                  </div>

                  <div>
                    <span>End date</span>
                    <strong>
                      {rental.endDate}
                    </strong>
                  </div>

                  <div>
                    <span>Total days</span>
                    <strong>
                      {rental.totalDays}
                    </strong>
                  </div>

                  <div>
                    <span>
                      Price per day
                    </span>
                    <strong>
                      ₹
                      {
                        rental.rentalPricePerDay
                      }
                    </strong>
                  </div>

                  <div>
                    <span>
                      Total amount
                    </span>
                    <strong>
                      ₹
                      {
                        rental.totalAmount
                      }
                    </strong>
                  </div>
                </div>

                {rental.farmerNote && (
                  <div className="rental-note">
                    <strong>
                      Your note
                    </strong>

                    <p>
                      {
                        rental.farmerNote
                      }
                    </p>
                  </div>
                )}

                {rental.ownerResponseNote && (
                  <div className="rental-note owner-note">
                    <strong>
                      Owner response
                    </strong>

                    <p>
                      {
                        rental.ownerResponseNote
                      }
                    </p>
                  </div>
                )}

                {rental.status ===
                  "PENDING" && (
                  <button
                    type="button"
                    className="cancel-rental-button"
                    onClick={() =>
                      handleCancel(
                        rental.id
                      )
                    }
                  >
                    Cancel Request
                  </button>
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default MyRentalsPage;