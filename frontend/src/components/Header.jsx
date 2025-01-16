import React from "react";
import { Navbar, Container } from "react-bootstrap";

const Header = () => {
  return (
    <Navbar bg="dark" variant="dark" expand="lg" fixed="top">
      <Container>
        <Navbar.Brand href="#" className="mx-auto">
          Kislaya Weather App
        </Navbar.Brand>
      </Container>
    </Navbar>
  );
};

export default Header;
