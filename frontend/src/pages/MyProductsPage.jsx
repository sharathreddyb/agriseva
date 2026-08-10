import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
} from "react-router-dom";
import {
  deactivateProduct,
  getMyProducts,
} from "../services/productService";

function MyProductsPage() {
  const navigate = useNavigate();

  const [products, setProducts] =
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

    async function fetchProducts() {
      try {
        const response =
          await getMyProducts();

        if (!ignore) {
          setProducts(response || []);
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

    fetchProducts();

    return () => {
      ignore = true;
    };
  }, []);

  async function handleDeactivate(
    productId
  ) {
    const confirmed =
      window.confirm(
        "Deactivate this product?"
      );

    if (!confirmed) {
      return;
    }

    setError("");
    setDeactivatingId(productId);

    try {
      await deactivateProduct(
        productId
      );

      setProducts((current) =>
        current.map((product) =>
          product.id === productId
            ? {
                ...product,
                active: false,
              }
            : product
        )
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setDeactivatingId(null);
    }
  }

  return (
    <main className="my-products-page">
      <section className="my-products-header">
        <div>
          <p className="products-label">
            Product seller
          </p>

          <h1>My Products</h1>

          <p>
            Manage the products you sell
            through AgriSeva.
          </p>
        </div>

        <button
          type="button"
          className="primary-button"
          onClick={() =>
            navigate(
              "/provider/products/new"
            )
          }
        >
          Add Product
        </button>
      </section>

      <section className="my-products-content">
        {error && (
          <div className="error-message">
            {error}
          </div>
        )}

        {loading ? (
          <p>Loading products...</p>
        ) : products.length === 0 ? (
          <div className="product-empty">
            <h2>No products yet</h2>

            <p>
              Add your first farming
              product to start selling.
            </p>
          </div>
        ) : (
          <div className="my-products-grid">
            {products.map(
              (product) => (
                <article
                  key={product.id}
                  className="my-product-card"
                >
                  <div className="my-product-card-header">
                    <div>
                      <p className="product-category">
                        {product.category}
                      </p>

                      <h2>
                        {product.name}
                      </h2>
                    </div>

                    <span
                      className={
                        product.active
                          ? "product-active-badge"
                          : "product-inactive-badge"
                      }
                    >
                      {product.active
                        ? "ACTIVE"
                        : "INACTIVE"}
                    </span>
                  </div>

                  <p className="my-product-description">
                    {product.description ||
                      "No description provided."}
                  </p>

                  <div className="my-product-details">
                    <div>
                      <span>Price</span>

                      <strong>
                        ₹{product.price} /{" "}
                        {product.unit}
                      </strong>
                    </div>

                    <div>
                      <span>Stock</span>

                      <strong>
                        {
                          product.stockQuantity
                        }
                      </strong>
                    </div>

                    <div>
                      <span>Location</span>

                      <strong>
                        {product.village},{" "}
                        {product.district}
                      </strong>
                    </div>
                  </div>

                  <div className="my-product-actions">
                    <button
                      type="button"
                      className="secondary-button"
                      onClick={() =>
                        navigate(
                          `/provider/products/${product.id}/edit`
                        )
                      }
                    >
                      Edit
                    </button>

                    {product.active && (
                      <button
                        type="button"
                        className="deactivate-product-button"
                        disabled={
                          deactivatingId ===
                          product.id
                        }
                        onClick={() =>
                          handleDeactivate(
                            product.id
                          )
                        }
                      >
                        {deactivatingId ===
                        product.id
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

export default MyProductsPage;