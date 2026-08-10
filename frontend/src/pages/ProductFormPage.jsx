import {
  useEffect,
  useState,
} from "react";
import {
  useNavigate,
  useParams,
} from "react-router-dom";
import {
  createProduct,
  getProductById,
  updateProduct,
} from "../services/productService";

function ProductFormPage() {
  const navigate = useNavigate();
  const { productId } = useParams();

  const isEditMode =
    Boolean(productId);

  const [formData, setFormData] =
    useState({
      name: "",
      category: "FERTILIZER",
      description: "",
      price: "",
      stockQuantity: "",
      unit: "KG",
      imageUrl: "",
      serviceAddress: "",
      village: "",
      district: "",
      state: "Telangana",
      postalCode: "",
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

    async function fetchProduct() {
      try {
        const product =
          await getProductById(
            productId
          );

        if (!ignore) {
          setFormData({
            name: product.name || "",
            category:
              product.category ||
              "FERTILIZER",
            description:
              product.description || "",
            price:
              product.price ?? "",
            stockQuantity:
              product.stockQuantity ?? "",
            unit:
              product.unit || "KG",
            imageUrl:
              product.imageUrl || "",
            serviceAddress:
              product.serviceAddress || "",
            village:
              product.village || "",
            district:
              product.district || "",
            state:
              product.state || "Telangana",
            postalCode:
              product.postalCode || "",
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

    fetchProduct();

    return () => {
      ignore = true;
    };
  }, [isEditMode, productId]);

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
      price:
        Number(formData.price),
      stockQuantity:
        Number(
          formData.stockQuantity
        ),
    };

    try {
      if (isEditMode) {
        await updateProduct(
          productId,
          request
        );
      } else {
        await createProduct(
          request
        );
      }

      navigate(
        "/provider/products"
      );
    } catch (exception) {
      setError(exception.message);
    } finally {
      setSaving(false);
    }
  }

  if (loading) {
    return (
      <main className="product-form-page">
        <p>Loading product...</p>
      </main>
    );
  }

  return (
    <main className="product-form-page">
      <section className="product-form-header">
        <p className="products-label">
          Product seller
        </p>

        <h1>
          {isEditMode
            ? "Edit Product"
            : "Add Product"}
        </h1>

        <p>
          Enter the product details farmers
          will see in the marketplace.
        </p>
      </section>

      <section className="product-form-content">
        <form
          className="product-management-form"
          onSubmit={handleSubmit}
        >
          {error && (
            <div className="error-message">
              {error}
            </div>
          )}

          <div className="form-group">
            <label htmlFor="name">
              Product name
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

          <div className="product-form-grid">
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
            </div>

            <div className="form-group">
              <label htmlFor="unit">
                Unit
              </label>

              <select
                id="unit"
                name="unit"
                value={formData.unit}
                onChange={handleChange}
                required
              >
                <option value="KG">
                  Kg
                </option>

                <option value="LITRE">
                  Litre
                </option>

                <option value="PACK">
                  Pack
                </option>

                <option value="PIECE">
                  Piece
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

          <div className="product-form-grid">
            <div className="form-group">
              <label htmlFor="price">
                Price
              </label>

              <input
                id="price"
                name="price"
                type="number"
                min="0.01"
                step="0.01"
                value={formData.price}
                onChange={handleChange}
                required
              />
            </div>

            <div className="form-group">
              <label htmlFor="stockQuantity">
                Stock quantity
              </label>

              <input
                id="stockQuantity"
                name="stockQuantity"
                type="number"
                min="0"
                step="1"
                value={
                  formData.stockQuantity
                }
                onChange={handleChange}
                required
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
              placeholder="https://example.com/product.jpg"
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

          <div className="product-form-grid">
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
                pattern="[0-9]{6}"
                maxLength={6}
                required
              />
            </div>
          </div>

          <div className="product-form-actions">
            <button
              type="submit"
              className="primary-button"
              disabled={saving}
            >
              {saving
                ? "Saving..."
                : isEditMode
                  ? "Update Product"
                  : "Create Product"}
            </button>

            <button
              type="button"
              className="secondary-button"
              onClick={() =>
                navigate(
                  "/provider/products"
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

export default ProductFormPage;