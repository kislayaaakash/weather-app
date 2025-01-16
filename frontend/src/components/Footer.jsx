import React from "react";
import { Container, Row, Col } from "react-bootstrap";

const Footer = () => {
  return (
    <footer className="bg-dark text-white">
      {/* Top section with "Back to Top" link */}
      <div style={{ backgroundColor: "#d3d3d3", padding: "1rem" }}>
        {/* Adjusted color */}
        <Container>
          <Row>
            <Col className="text-center">
              <a href="#top" className="text-decoration-none">
                Back to Top
              </a>
            </Col>
          </Row>
        </Container>
      </div>

      {/* Bottom section with footer text */}
      <div className="py-3">
        <Container>
          <Row>
            <Col className="text-center">
              <p className="mb-0">
                &copy; 2025 Kislaya Weather App. All Rights Reserved.
              </p>
            </Col>
          </Row>
        </Container>
      </div>
    </footer>
  );
};

export default Footer;
