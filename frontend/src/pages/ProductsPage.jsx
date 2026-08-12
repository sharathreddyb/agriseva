import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
} from "react-router-dom";
import { useAuth } from "../context/useAuth";
import ListingImage from "../components/ListingImage";
import {
  searchProducts,
} from "../services/productService";

function ProductsPage() {
  const navigate = useNavigate();

  const { isAuthenticated } =
    useAuth();

  const [products, setProducts] =
    useState([]);

  const [filters, setFilters] =
    useState({
      keyword: "",
      category: "",
      district: "",
      village: "",
      minPrice: "",
      maxPrice: "",
      inStock: "",
    });

  const [loading, setLoading] =
    useState(true);

  const [error, setError] =
    useState("");

  useEffect(() => {
    let ignore = false;

    async function fetchProducts() {
      try {
        const response =
          await searchProducts();

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

  async function loadProducts(
    searchFilters
  ) {
    setLoading(true);
    setError("");

    try {
      const response =
        await searchProducts(
          searchFilters
        );

      setProducts(response || []);
    } catch (exception) {
      setError(exception.message);
    } finally {
      setLoading(false);
    }
  }

  function handleChange(event) {
    const {
      name,
      value,
    } = event.target;

    setFilters((current) => ({
      ...current,
      [name]: value,
    }));
  }

  function handleSearch(event) {
    event.preventDefault();

    loadProducts(filters);
  }

  function handleReset() {
    const emptyFilters = {
      keyword: "",
      category: "",
      district: "",
      village: "",
      minPrice: "",
      maxPrice: "",
      inStock: "",
    };

    setFilters(emptyFilters);
    loadProducts(emptyFilters);
  }

  function handleBuy(product) {
    if (!isAuthenticated) {
      navigate("/login");
      return;
    }

    navigate(
      `/products/${product.id}/order`
    );
  }

  return (
    <main className="products-page">
      <section className="products-header">
        <div>
          <p className="products-label">
            Farming products
          </p>

          <h1>
            Find Agricultural Products
          </h1>

          <p>
            Browse fertilizers, seeds,
            pesticides and farming tools
            offered by local sellers.
          </p>
        </div>
      </section>

      <section className="products-content">
        <form
          className="product-filters"
          onSubmit={handleSearch}
        >
          <div className="product-filter-grid">
            <label>
              Keyword

              <input
                type="text"
                name="keyword"
                value={filters.keyword}
                onChange={handleChange}
                placeholder="Fertilizer, seeds..."
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

                <option value="FERTILIZER">
                  Fertilizer
                </option>

                <option value="SEED">
                  Seed
                </option>

                <option value="PESTICIDE">
                  Pesticide
                </option>

                <option value="FARMING_TOOL">
                  Farming Tool
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
              Minimum price

              <input
                type="number"
                name="minPrice"
                value={filters.minPrice}
                onChange={handleChange}
                min="0"
                step="0.01"
                placeholder="0"
              />
            </label>

            <label>
              Maximum price

              <input
                type="number"
                name="maxPrice"
                value={filters.maxPrice}
                onChange={handleChange}
                min="0"
                step="0.01"
                placeholder="5000"
              />
            </label>

            <label>
              Stock

              <select
                name="inStock"
                value={filters.inStock}
                onChange={handleChange}
              >
                <option value="">
                  Any stock
                </option>

                <option value="true">
                  In stock
                </option>

                <option value="false">
                  Out of stock
                </option>
              </select>
            </label>
          </div>

          <div className="product-filter-actions">
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
          <p className="product-message">
            Loading products...
          </p>
        ) : products.length === 0 ? (
          <div className="product-empty">
            <h2>No products found</h2>

            <p>
              Try changing your search
              filters.
            </p>
          </div>
        ) : (
          <div className="product-grid">
            {products.map(
              (product) => (
                <article
                  className="product-card"
                  key={product.id}
                >
                  <div className="product-image">
                    <ListingImage
                      imageUrl={
                        product.imageUrl
                      }
                      alt={product.name}
                      placeholder="Product"
                      placeholderClassName="product-image-placeholder"
                    />
                  </div>

                  <div className="product-card-body">
                    <div className="product-card-top">
                      <span className="product-category">
                        {
                          product.category
                        }
                      </span>

                      <span
                        className={
                          product.stockQuantity >
                          0
                            ? "product-stock-available"
                            : "product-stock-empty"
                        }
                      >
                        {product.stockQuantity >
                        0
                          ? "IN STOCK"
                          : "OUT OF STOCK"}
                      </span>
                    </div>

                    <h2>
                      {product.name}
                    </h2>

                    <p className="product-description">
                      {product.description ||
                        "No description provided."}
                    </p>

                    <p className="product-price">
                      ₹{product.price}

                      <span>
                        {" "}
                        / {product.unit}
                      </span>
                    </p>

                    <p className="product-stock">
                      Stock:{" "}
                      <strong>
                        {
                          product.stockQuantity
                        }{" "}
                        {product.unit}
                      </strong>
                    </p>

                    <div className="product-location">
                      <strong>
                        Location:
                      </strong>{" "}
                      {[
                        product.village,
                        product.district,
                        product.state,
                      ]
                        .filter(Boolean)
                        .join(", ") ||
                        "Not provided"}
                    </div>

                    <div className="product-seller">
                      <strong>
                        Seller:
                      </strong>{" "}
                      {
                        product.sellerName
                      }
                    </div>

                    <div className="product-card-actions">
                      <button
                        type="button"
                        className="buy-product-button"
                        disabled={
                          product.stockQuantity <=
                          0
                        }
                        onClick={() =>
                          handleBuy(
                            product
                          )
                        }
                      >
                        {product.stockQuantity >
                        0
                          ? "Buy Product"
                          : "Out of Stock"}
                      </button>
                    </div>
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

export default ProductsPage;