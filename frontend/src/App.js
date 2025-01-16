import React, { useState } from "react";
import "./App.css";
import Header from "./components/Header.jsx";
import Footer from "./components/Footer.jsx";
import SearchBar from "./components/SearchBar.jsx";
import WeatherTable from "./components/WeatherTable.jsx";
import "bootstrap/dist/css/bootstrap.min.css";

function App() {
  const [weatherData, setWeatherData] = useState(null);

  return (
    <div>
      {/* Header */}
      <Header />

      {/* Main Content */}
      <div
        id="top"
        style={{ marginTop: "56px", minHeight: "100vh", paddingTop: "20px" }}
      >
        {/* Search Bar */}
        <SearchBar setWeatherData={setWeatherData} />

        {/* Display WeatherTable only if weatherData is not null */}
        <div className="mt-5 text-center">
          {weatherData && <WeatherTable weatherData={weatherData} />}
        </div>
      </div>

      {/* Footer */}
      <Footer />
    </div>
  );
}

export default App;
