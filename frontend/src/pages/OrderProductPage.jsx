import {
  useEffect,
  useState,
} from "react";
import {
  Link,
  useNavigate,
  useParams,
} from "react-router-dom";
import { getProductById } from "../services/productService";
import { createOrder } from "../services/orderService";

function OrderProductPage() {
  const { productId } = useParams();
  const navigate = useNavigate();

  const [product, setProduct] =
    useState(null);

  const [formData, setFormData] =
    useState({
      quantity: 1,
      buyerNote: "",
    });

  const [loading, setLoading] =
    useState(true);

  const [submitting, setSubmitting] =
    useState(false);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchProduct() {
      try {
        const response =
          await getProductById(productId);

        if (!ignore) {
          setProduct(response);
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

    fetchProduct();

    return () => {
      ignore = true;
    };
  }, [productId]);

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

    const quantity =
      Number(formData.quantity);

    if (quantity < 1) {
      setError(
        "Quantity must be at least 1."
      );
      return;
    }

    if (
      product &&
      quantity >
        product.stockQuantity
    ) {
      setError(
        "Requested quantity exceeds available stock."
      );
      return;
    }

    setSubmitting(true);

    try {
      await createOrder({
        productId:
          Number(productId),
        quantity,
        buyerNote:
          formData.buyerNote,
      });

      navigate("/orders");
    } catch (exception) {
      setError(exception.message);
    } finally {
      setSubmitting(false);
    }
  }

  if (loading) {
    return (
      <main className="page-container">
        <p>Loading product...</p>
      </main>
    );
  }

  if (!product) {
    return (
      <main className="page-container">
        <div className="error-message">
          {error ||
            "Product could not be loaded."}
        </div>
      </main>
    );
  }

  const quantity =
    Number(formData.quantity) || 0;

  const estimatedTotal =
    Number(product.price) * quantity;

  const inStock =
    product.stockQuantity > 0;

  return (
    <main className="order-page">
      <section className="order-form-card">
        <div className="order-heading">
          <p className="products-label">
            Product order
          </p>

          <h1>
            Buy {product.name}
          </h1>

          <p>
            Place an order with{" "}
            {product.sellerName}.
          </p>
        </div>

        <div className="order-product-summary">
          <div>
            <span>Category</span>

            <strong>
              {product.category}
            </strong>
          </div>

          <div>
            <span>Price</span>

            <strong>
              ₹{product.price} /{" "}
              {product.unit}
            </strong>
          </div>

          <div>
            <span>Available stock</span>

            <strong>
              {product.stockQuantity}{" "}
              {product.unit}
            </strong>
          </div>

          <div>
            <span>Seller</span>

            <strong>
              {product.sellerName}
            </strong>
          </div>

          <div>
            <span>Location</span>

            <strong>
              {[
                product.village,
                product.district,
              ]
                .filter(Boolean)
                .join(", ") ||
                "Not provided"}
            </strong>
          </div>

          <div>
            <span>
              Estimated total
            </span>

            <strong>
              ₹
              {estimatedTotal.toFixed(
                2
              )}
            </strong>
          </div>
        </div>

        {!inStock && (
          <div className="error-message">
            This product is currently
            out of stock.
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
          <label>
            Quantity

            <input
              type="number"
              name="quantity"
              value={
                formData.quantity
              }
              onChange={handleChange}
              min="1"
              max={
                product.stockQuantity
              }
              required
            />
          </label>

          <label>
            Note to seller

            <textarea
              name="buyerNote"
              value={
                formData.buyerNote
              }
              onChange={handleChange}
              maxLength={500}
              rows={4}
              placeholder="Add any details about your order"
            />
          </label>

          <div className="order-actions">
            <button
              type="submit"
              className="auth-submit-button"
              disabled={
                submitting ||
                !inStock
              }
            >
              {submitting
                ? "Placing order..."
                : "Place Order"}
            </button>

            <Link
              to="/products"
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

export default OrderProductPage;