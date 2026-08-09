import {
  useEffect,
  useState,
} from "react";
import {
  getOwnerRentalRequests,
  updateRentalStatus,
} from "../services/rentalService";

function OwnerRentalRequestsPage() {
  const [rentals, setRentals] =
    useState([]);

  const [notes, setNotes] =
    useState({});

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  const [updatingId, setUpdatingId] =
    useState(null);

  useEffect(() => {
    let ignore = false;

    async function fetchRequests() {
      try {
        const response =
          await getOwnerRentalRequests();

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

    fetchRequests();

    return () => {
      ignore = true;
    };
  }, []);

  function handleNoteChange(
    rentalId,
    value
  ) {
    setNotes((current) => ({
      ...current,
      [rentalId]: value,
    }));
  }

  async function handleStatusUpdate(
    rentalId,
    status
  ) {
    setError("");
    setUpdatingId(rentalId);

    try {
      const updatedRental =
        await updateRentalStatus(
          rentalId,
          {
            status,
            ownerResponseNote:
              notes[rentalId] || "",
          }
        );

      setRentals((current) =>
        current.map((rental) =>
          rental.id === rentalId
            ? updatedRental
            : rental
        )
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setUpdatingId(null);
    }
  }

  function getStatusClass(status) {
    return `rental-status rental-status-${status.toLowerCase()}`;
  }

  return (
    <main className="rentals-page">
      <section className="rentals-header">
        <p className="equipment-label">
          Equipment owner
        </p>

        <h1>Rental Requests Received</h1>

        <p>
          Review rental requests submitted
          for your equipment.
        </p>
      </section>

      <section className="rentals-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading rental requests...</p>
        ) : rentals.length === 0 ? (
          <div className="equipment-empty">
            <h2>No rental requests</h2>

            <p>
              Rental requests for your
              equipment will appear here.
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
                      Rental #{rental.id}
                    </p>

                    <h2>
                      {rental.equipmentName}
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
                    <span>Farmer</span>
                    <strong>
                      {rental.farmerName}
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
                      ₹{rental.totalAmount}
                    </strong>
                  </div>
                </div>

                {rental.farmerNote && (
                  <div className="rental-note">
                    <strong>
                      Farmer note
                    </strong>

                    <p>
                      {rental.farmerNote}
                    </p>
                  </div>
                )}

                {rental.status ===
                "PENDING" ? (
                  <div className="owner-response-section">
                    <label>
                      Response note

                      <textarea
                        value={
                          notes[rental.id] ||
                          ""
                        }
                        onChange={(event) =>
                          handleNoteChange(
                            rental.id,
                            event.target.value
                          )
                        }
                        maxLength={500}
                        rows={3}
                        placeholder="Add a response for the farmer"
                      />
                    </label>

                    <div className="owner-request-actions">
                      <button
                        type="button"
                        className="approve-button"
                        disabled={
                          updatingId ===
                          rental.id
                        }
                        onClick={() =>
                          handleStatusUpdate(
                            rental.id,
                            "APPROVED"
                          )
                        }
                      >
                        Approve
                      </button>

                      <button
                        type="button"
                        className="reject-button"
                        disabled={
                          updatingId ===
                          rental.id
                        }
                        onClick={() =>
                          handleStatusUpdate(
                            rental.id,
                            "REJECTED"
                          )
                        }
                      >
                        Reject
                      </button>
                    </div>
                  </div>
                ) : (
                  rental.ownerResponseNote && (
                    <div className="rental-note owner-note">
                      <strong>
                        Your response
                      </strong>

                      <p>
                        {
                          rental.ownerResponseNote
                        }
                      </p>
                    </div>
                  )
                )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default OwnerRentalRequestsPage;