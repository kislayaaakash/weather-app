import { render, screen, fireEvent, waitFor } from "@testing-library/react";
import SearchBar from "../components/SearchBar"; // Adjust the import path to your component
import "@testing-library/jest-dom"; // For the "toBeInTheDocument" matcher

// Mock global fetch to avoid CORS issues
beforeAll(() => {
  global.fetch = jest.fn();
});

describe("SearchBar Component", () => {
  it("should render input field and button", () => {
    render(<SearchBar setWeatherData={() => {}} />);

    const inputElement = screen.getByRole("textbox");
    const buttonElement = screen.getByRole("button");

    expect(inputElement).toBeInTheDocument();
    expect(buttonElement).toBeInTheDocument();
  });

  it("should update input value when typed", () => {
    render(<SearchBar setWeatherData={() => {}} />);

    const inputElement = screen.getByRole("textbox");

    fireEvent.change(inputElement, { target: { value: "testCity" } });

    expect(inputElement.value).toBe("testCity");
  });

  it("should call the setWeatherData function when button is clicked", async () => {
    const setWeatherDataMock = jest.fn();
    render(<SearchBar setWeatherData={setWeatherDataMock} />);

    const inputElement = screen.getByRole("textbox");
    const buttonElement = screen.getByRole("button");

    fireEvent.change(inputElement, { target: { value: "testCity" } });
    fireEvent.click(buttonElement);

    // Wait for the mock function to be called
    await waitFor(() => expect(setWeatherDataMock).toHaveBeenCalledTimes(1));
  });

  it("should show an error message when the city name is empty", () => {
    render(<SearchBar setWeatherData={() => {}} />);

    const buttonElement = screen.getByRole("button");

    fireEvent.click(buttonElement);

    // Check if the error message is shown
    const errorElement = screen.getByText(/City name cannot be empty/i); // Fix to match actual error message
    expect(errorElement).toBeInTheDocument();
  });
});
