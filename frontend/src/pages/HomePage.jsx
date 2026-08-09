import { Link } from "react-router-dom";

function HomePage() {
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
          </div>
        </div>
      </section>

      <section className="feature-section">
        <div className="feature-card">
          <h2>Rent Equipment</h2>
          <p>
            Find tractors, harvesters,
            sprayers and other farm equipment.
          </p>
        </div>

        <div className="feature-card">
          <h2>Buy Products</h2>
          <p>
            Search fertilizers, seeds,
            pesticides and farming tools.
          </p>
        </div>

        <div className="feature-card">
          <h2>Sell or Rent</h2>
          <p>
            Equipment owners and sellers can
            list their services for farmers.
          </p>
        </div>
      </section>
    </main>
  );
}

export default HomePage;