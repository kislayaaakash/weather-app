import React, { useState } from "react";
import {
  Form,
  FormControl,
  Button,
  Container,
  Row,
  Col,
} from "react-bootstrap";
import fetchWeatherDetails from "./FetchWeather";

const SearchBar = ({ setWeatherData }) => {
  const [city, setCity] = useState("");
  const [error, setError] = useState("");

  const handleSearch = async () => {
    if (city.trim()) {
      try {
        const result = await fetchWeatherDetails(city);
        if (result.success) {
          setWeatherData(result); // Update App.js state with the API response
          setError(""); // Clear any previous error
        } else if (result.message && result.message.trim()) {
          setWeatherData(result);
          setError("");
        }
        // else {
        //   setWeatherData(null);
        // }
      } catch (error) {
        setWeatherData(null);
      }
    } else {
      setError("City name cannot be empty."); // Handle empty input scenario
    }
  };

  return (
    <Container>
      <Row className="justify-content-center">
        <Col xs={12} md={8}>
          <Form className="d-flex">
            <FormControl
              type="text"
              placeholder="Enter a city to get weather details."
              value={city}
              onChange={(e) => setCity(e.target.value)}
            />
            <Button
              variant="primary"
              className="ms-2"
              onClick={handleSearch} // Trigger API call on button click
            >
              Search
            </Button>
          </Form>
          {error && <p className="text-danger mt-3">{error}</p>}
        </Col>
      </Row>
    </Container>
  );
};

export default SearchBar;
