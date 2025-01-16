import { render, screen } from "@testing-library/react";
import WeatherTable from "../components/WeatherTable";

describe("WeatherTable", () => {
  const weatherData = {
    success: true,
    data: {
      "2025-01-16": [
        {
          time: "10:00 AM",
          temperature: "25°C",
          weather: [{ description: "Sunny" }],
          advice: "Wear sunglasses",
        },
      ],
      "2025-01-17": [
        {
          time: "10:00 AM",
          temperature: "22°C",
          weather: [{ description: "Cloudy" }],
          advice: "Carry an umbrella",
        },
      ],
    },
  };

  it("should render an error message when weatherData.success is false", () => {
    const errorData = { success: false, message: "Error fetching data" };

    render(<WeatherTable weatherData={errorData} />);

    expect(screen.getByText(/Error fetching data/i)).toBeInTheDocument();
  });

  it("should render the weather forecast table when weatherData.success is true", () => {
    render(<WeatherTable weatherData={weatherData} />);

    expect(screen.getByText("Weather Forecast")).toBeInTheDocument();
    expect(screen.getByText("2025-01-16")).toBeInTheDocument();
    expect(screen.getByText("25°C")).toBeInTheDocument();
    expect(screen.getByText("Wear sunglasses")).toBeInTheDocument();
  });

  it("should render multiple dates when weather data includes multiple dates", () => {
    render(<WeatherTable weatherData={weatherData} />);

    expect(screen.getByText("2025-01-16")).toBeInTheDocument();
    expect(screen.getByText("2025-01-17")).toBeInTheDocument();
  });

  it("should render the correct row styles for the weather table", () => {
    render(<WeatherTable weatherData={weatherData} />);
    const rows = screen.getAllByRole("row");

    // Check that even row has the correct background color (index 1)
    expect(rows[1]).toHaveStyle("background-color: rgb(249, 249, 249)"); // Even row
  });
});
