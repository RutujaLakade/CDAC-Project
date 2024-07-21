import { Link } from "react-router-dom";
const Footer = () => {
  return (
    <div>
      <div className="container my-5 justify-content-center">
        <footer className="text-center text-lg-start text-color">
          <div className="container-fluid p-4 pb-0">
            <section className="">
              <p className="d-flex justify-content-center align-items-center">
                <span className="me-3 text-color">Login from here</span>
                <Link to="/user/login" className="active">
                  <button
                    type="button"
                    className="btn btn-outline-light btn-rounded bg-color custom-bg-text"
                  >
                    Log in
                  </button>
                </Link>
              </p>
            </section>

            <hr className="mb-4" />
          </div>

          <div className="text-center">
            © 2023 Copyright: &nbsp;
            <a className="text-color-3" href="https://codewithmurad.com/">
              SPARS
            </a>
          </div>
        </footer>
      </div>
    </div>
  );
};

export default Footer;
