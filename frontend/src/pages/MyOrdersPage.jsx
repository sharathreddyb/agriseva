import {
  useEffect,
  useState,
} from "react";
import {
  cancelOrder,
  getMyOrders,
} from "../services/orderService";

function MyOrdersPage() {
  const [orders, setOrders] =
    useState([]);

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchOrders() {
      try {
        const response =
          await getMyOrders();

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

  async function handleCancel(
    orderId
  ) {
    setError("");

    try {
      const updatedOrder =
        await cancelOrder(orderId);

      setOrders((current) =>
        current.map((order) =>
          order.id === orderId
            ? updatedOrder
            : order
        )
      );
    } catch (exception) {
      setError(exception.message);
    }
  }

  function getStatusClass(status) {
    return `order-status order-status-${status.toLowerCase()}`;
  }

  return (
    <main className="orders-page">
      <section className="orders-header">
        <p className="products-label">
          My orders
        </p>

        <h1>Product Orders</h1>

        <p>
          View agricultural product
          orders you have placed.
        </p>
      </section>

      <section className="orders-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading orders...</p>
        ) : orders.length === 0 ? (
          <div className="product-empty">
            <h2>No orders found</h2>

            <p>
              Products you order will
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
                      {
                        order.productName
                      }
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
                    <span>Seller</span>

                    <strong>
                      {order.sellerName}
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
                    <span>
                      Total amount
                    </span>

                    <strong>
                      ₹{order.totalAmount}
                    </strong>
                  </div>
                </div>

                {order.buyerNote && (
                  <div className="order-note">
                    <strong>
                      Your note
                    </strong>

                    <p>
                      {order.buyerNote}
                    </p>
                  </div>
                )}

                {order.sellerResponseNote && (
                  <div className="order-note seller-note">
                    <strong>
                      Seller response
                    </strong>

                    <p>
                      {
                        order.sellerResponseNote
                      }
                    </p>
                  </div>
                )}

                {order.status ===
                  "PENDING" && (
                  <button
                    type="button"
                    className="cancel-order-button"
                    onClick={() =>
                      handleCancel(
                        order.id
                      )
                    }
                  >
                    Cancel Order
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

export default MyOrdersPage;