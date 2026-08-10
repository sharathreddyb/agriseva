import {
  useEffect,
  useState,
} from "react";
import {
  getReceivedOrders,
  updateOrderStatus,
} from "../services/orderService";

function ReceivedOrdersPage() {
  const [orders, setOrders] =
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

    async function fetchOrders() {
      try {
        const response =
          await getReceivedOrders();

        if (!ignore) {
          setOrders(response || []);
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

    fetchOrders();

    return () => {
      ignore = true;
    };
  }, []);

  function handleNoteChange(
    orderId,
    value
  ) {
    setNotes((current) => ({
      ...current,
      [orderId]: value,
    }));
  }

  async function handleStatusUpdate(
    orderId,
    status
  ) {
    setError("");
    setUpdatingId(orderId);

    try {
      const updatedOrder =
        await updateOrderStatus(
          orderId,
          {
            status,
            sellerResponseNote:
              notes[orderId] || "",
          }
        );

      setOrders((current) =>
        current.map((order) =>
          order.id === orderId
            ? updatedOrder
            : order
        )
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setUpdatingId(null);
    }
  }

  function getStatusClass(status) {
    return `order-status order-status-${status.toLowerCase()}`;
  }

  return (
    <main className="orders-page">
      <section className="orders-header">
        <p className="products-label">
          Product seller
        </p>

        <h1>Orders Received</h1>

        <p>
          Review product orders placed by
          buyers.
        </p>
      </section>

      <section className="orders-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading received orders...</p>
        ) : orders.length === 0 ? (
          <div className="product-empty">
            <h2>No received orders</h2>

            <p>
              Orders for your products will
              appear here.
            </p>
          </div>
        ) : (
          <div className="orders-list">
            {orders.map((order) => (
              <article
                className="order-card"
                key={order.id}
              >
                <div className="order-card-header">
                  <div>
                    <p className="order-id">
                      Order #{order.id}
                    </p>

                    <h2>
                      {order.productName}
                    </h2>
                  </div>

                  <span
                    className={getStatusClass(
                      order.status
                    )}
                  >
                    {order.status}
                  </span>
                </div>

                <div className="order-details-grid">
                  <div>
                    <span>Buyer</span>

                    <strong>
                      {order.buyerName}
                    </strong>
                  </div>

                  <div>
                    <span>Quantity</span>

                    <strong>
                      {order.quantity}
                    </strong>
                  </div>

                  <div>
                    <span>Unit price</span>

                    <strong>
                      ₹{order.unitPrice}
                    </strong>
                  </div>

                  <div>
                    <span>Total amount</span>

                    <strong>
                      ₹{order.totalAmount}
                    </strong>
                  </div>
                </div>

                {order.buyerNote && (
                  <div className="order-note">
                    <strong>
                      Buyer note
                    </strong>

                    <p>
                      {order.buyerNote}
                    </p>
                  </div>
                )}

                {order.status ===
                  "PENDING" && (
                  <div className="seller-response-section">
                    <label>
                      Response note

                      <textarea
                        value={
                          notes[order.id] ||
                          ""
                        }
                        onChange={(event) =>
                          handleNoteChange(
                            order.id,
                            event.target.value
                          )
                        }
                        maxLength={500}
                        rows={3}
                        placeholder="Add a response for the buyer"
                      />
                    </label>

                    <div className="seller-order-actions">
                      <button
                        type="button"
                        className="accept-order-button"
                        disabled={
                          updatingId ===
                          order.id
                        }
                        onClick={() =>
                          handleStatusUpdate(
                            order.id,
                            "ACCEPTED"
                          )
                        }
                      >
                        Accept
                      </button>

                      <button
                        type="button"
                        className="reject-order-button"
                        disabled={
                          updatingId ===
                          order.id
                        }
                        onClick={() =>
                          handleStatusUpdate(
                            order.id,
                            "REJECTED"
                          )
                        }
                      >
                        Reject
                      </button>
                    </div>
                  </div>
                )}

                {order.status ===
                  "ACCEPTED" && (
                  <div className="seller-response-section">
                    <label>
                      Completion note

                      <textarea
                        value={
                          notes[order.id] ||
                          ""
                        }
                        onChange={(event) =>
                          handleNoteChange(
                            order.id,
                            event.target.value
                          )
                        }
                        maxLength={500}
                        rows={3}
                        placeholder="Add a final note for the buyer"
                      />
                    </label>

                    <div className="seller-order-actions">
                      <button
                        type="button"
                        className="complete-order-button"
                        disabled={
                          updatingId ===
                          order.id
                        }
                        onClick={() =>
                          handleStatusUpdate(
                            order.id,
                            "COMPLETED"
                          )
                        }
                      >
                        Mark Completed
                      </button>
                    </div>
                  </div>
                )}

                {order.status !==
                  "PENDING" &&
                  order.status !==
                    "ACCEPTED" &&
                  order.sellerResponseNote && (
                    <div className="order-note seller-note">
                      <strong>
                        Your response
                      </strong>

                      <p>
                        {
                          order.sellerResponseNote
                        }
                      </p>
                    </div>
                  )}
              </article>
            ))}
          </div>
        )}
      </section>
    </main>
  );
}

export default ReceivedOrdersPage;