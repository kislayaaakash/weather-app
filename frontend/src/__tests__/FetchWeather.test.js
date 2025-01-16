import fetchWeatherDetails from "../components/FetchWeather"; // Import the function to be tested

// Mock the global fetch function
global.fetch = jest.fn();

describe("fetchWeatherDetails", () => {
  afterEach(() => {
    // Clear all mocks after each test
    jest.clearAllMocks();
  });

  it("should return weather data when API call is successful (status 200)", async () => {
    const mockData = { data: { temperature: "25°C", condition: "Sunny" } };

    // Mock the fetch function to simulate a successful response
    fetch.mockResolvedValueOnce({
      status: 200,
      json: jest.fn().mockResolvedValueOnce(mockData),
    });

    const city = "London";
    const result = await fetchWeatherDetails(city);

    expect(result.success).toBe(true);
    expect(result.data).toEqual(mockData.data);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:8080/api/v1/weather/advice?city=${city}`
    );
  });

  it("should return an error message when API returns status 404", async () => {
    const mockError = { message: "City not found" };

    // Mock the fetch function to simulate a 404 error response
    fetch.mockResolvedValueOnce({
      status: 404,
      json: jest.fn().mockResolvedValueOnce(mockError),
    });

    const city = "UnknownCity";
    const result = await fetchWeatherDetails(city);

    expect(result.success).toBe(false);
    expect(result.message).toBe(mockError.message);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:8080/api/v1/weather/advice?city=${city}`
    );
  });

  it("should return an error message when API returns status 503", async () => {
    const mockError = { message: "Service unavailable" };

    // Mock the fetch function to simulate a 503 error response
    fetch.mockResolvedValueOnce({
      status: 503,
      json: jest.fn().mockResolvedValueOnce(mockError),
    });

    const city = "NewYork";
    const result = await fetchWeatherDetails(city);

    expect(result.success).toBe(false);
    expect(result.message).toBe(mockError.message);
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:8080/api/v1/weather/advice?city=${city}`
    );
  });

  it("should handle network errors gracefully", async () => {
    // Mock the fetch function to simulate a network error
    fetch.mockRejectedValueOnce(new Error("Network error"));

    const city = "Tokyo";
    const result = await fetchWeatherDetails(city);

    expect(result.success).toBe(false);
    expect(result.message).toBe("Service temporarily unavailable.");
    expect(fetch).toHaveBeenCalledWith(
      `http://localhost:8080/api/v1/weather/advice?city=${city}`
    );
  });
});
