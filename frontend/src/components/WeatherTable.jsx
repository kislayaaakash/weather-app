import React from "react";

const WeatherTable = ({ weatherData }) => {
  // Check if weatherData is a string
  if (!weatherData.success) {
    return <div style={styles.errorMessage}>{weatherData.message}</div>;
  }

  return (
    <div style={styles.tableContainer}>
      <h2 style={styles.title}>Weather Forecast</h2>
      {Object.keys(weatherData.data).map((date) => {
        const weatherByDate = weatherData.data[date];

        return (
          <div key={date} style={styles.dateContainer}>
            <h3 style={styles.dateTitle}>{date}</h3>
            <table style={styles.table}>
              <thead>
                <tr>
                  <th style={styles.th}>Time</th>
                  <th style={styles.th}>Temperature (°C)</th>
                  <th style={styles.th}>Weather</th>
                  <th style={styles.th}>Advice</th>
                </tr>
              </thead>
              <tbody>
                {weatherByDate.map((entry, index) => (
                  <tr
                    key={index}
                    style={index % 2 === 0 ? styles.evenRow : styles.oddRow}
                  >
                    <td style={styles.td}>{entry.time}</td>
                    <td style={styles.td}>{entry.temperature}</td>
                    <td style={styles.td}>{entry.weather[0].description}</td>
                    <td style={styles.td}>{entry.advice}</td>
                  </tr>
                ))}
              </tbody>
            </table>
          </div>
        );
      })}
    </div>
  );
};

const styles = {
  errorMessage: {
    color: "red",
    textAlign: "center",
    fontSize: "1.5em",
    marginTop: "50px",
  },
  tableContainer: {
    width: "80%",
    margin: "0 auto",
    overflowX: "auto",
    padding: "20px 0",
  },
  title: {
    textAlign: "center",
    fontSize: "2em",
    marginBottom: "20px",
  },
  dateContainer: {
    marginBottom: "30px",
  },
  dateTitle: {
    fontSize: "1.5em",
    textAlign: "center",
    marginBottom: "10px",
  },
  table: {
    width: "100%",
    borderCollapse: "collapse",
    margin: "0 auto",
    border: "1px solid #ddd",
  },
  th: {
    padding: "12px",
    backgroundColor: "#f4f4f4",
    borderBottom: "2px solid #ddd",
  },
  td: {
    padding: "10px",
    textAlign: "center",
    borderBottom: "1px solid #ddd",
  },
  evenRow: {
    backgroundColor: "#f9f9f9",
  },
  oddRow: {
    backgroundColor: "#fff",
  },
};

export default WeatherTable;
