import { Link } from "react-router-dom";
import { useAuth } from "../context/useAuth";

function HomePage() {
  const { isAuthenticated } = useAuth();

  return (
    <main>
      <section className="hero-section">
        <div className="hero-content">
          <p className="hero-label">
            Farming made easier
          </p>

          <h1>
            Rent equipment and buy
            farming products in one place
          </h1>

          <p className="hero-description">
            AgriSeva connects farmers with
            equipment owners and agricultural
            product sellers in nearby areas.
          </p>

          <div className="hero-actions">
            {isAuthenticated ? (
              <>
                <Link
                  to="/equipment"
                  className="primary-button"
                >
                  Browse Equipment
                </Link>

                <Link
                  to="/products"
                  className="secondary-button"
                >
                  Browse Products
                </Link>
              </>
            ) : (
              <>
                <Link
                  to="/register"
                  className="primary-button"
                >
                  Get Started
                </Link>

                <Link
                  to="/login"
                  className="secondary-button"
                >
                  Login
                </Link>
              </>
            )}
          </div>
        </div>
      </section>

      <section className="feature-section">
        <Link
          to="/equipment"
          className="feature-card feature-card-link"
        >
          <h2>Rent Equipment</h2>

          <p>
            Find tractors, harvesters,
            sprayers and other farm equipment.
          </p>

          <span className="feature-card-action">
            Browse Equipment →
          </span>
        </Link>

        <Link
          to="/products"
          className="feature-card feature-card-link"
        >
          <h2>Buy Products</h2>

          <p>
            Search fertilizers, seeds,
            pesticides and farming tools.
          </p>

          <span className="feature-card-action">
            Browse Products →
          </span>
        </Link>

        <Link
          to="/provider"
          className="feature-card feature-card-link"
        >
          <h2>Sell or Rent</h2>

          <p>
            Equipment owners and sellers can
            list their services for farmers.
          </p>

          <span className="feature-card-action">
            Become a Provider →
          </span>
        </Link>
      </section>
    </main>
  );
}

export default HomePage;