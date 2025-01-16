// src/components/FetchWeather.js
const fetchWeatherDetails = async (city) => {
  try {
    const response = await fetch(
      `http://localhost:8080/api/v1/weather/advice?city=${city}`
    );
    const result = await response.json();

    if (response.status === 200) {
      return { success: true, data: result.data }; // Propagate the data field
    } else if (response.status === 404 || response.status === 503) {
      return { success: false, data: null, message: result.message }; // Propagate the message field
    } else {
      throw new Error(`Unexpected status code: ${response.status}`);
    }
  } catch (error) {
    return {
      success: false,
      message: "Service temporarily unavailable.",
    };
  }
};

export default fetchWeatherDetails;
