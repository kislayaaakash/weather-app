# City Weather Advisory App

### Overview

The application provides a real-time weather advisory service to users, with a robust fallback mechanism for offline or error scenarios. The service fetches data from an external API and maintains a local backup for uninterrupted service.


### Key Features:
1. **Real-Time Weather Advisory**: Fetches weather data for cities using an external API.
2. **Offline Fallback**: Uses backup data when the service is offline or the external API fails.
3. **Weather Advice Generation**: Provides actionable advice based on weather conditions.
4. **Data Backup and Maintenance**: Updates the local backup with the latest weather data to ensure availability.
5. **Interactive UI**: The weather data is displayed in a tabular format, organized in ascending order by date, with corresponding advice provided for each entry.
---

### Service Architecture and work-flow:

#### Flow Diagram:
###### Weather Advisory Service:

![Flow Diagram](https://mermaid.ink/img/pako:eNp9k0tz2jAUhf_KHS26AsZgzMOLZHiEQhJIJkA7rWGh2hfQ1JaoLJFSzH-vLENCZkoX9tjSd885upIOJBQREp-sJd1uYNZfcIBOMFVUqiWUyzfQDUYpPPGYcYSxYW-XOdLN57JvmGbQCwaowg18Rao2KKFPFYWVFAl0nkcX8ERkcB-8oJIMd3iBdWn4U28NmbM9a9o_GFNTDi-YbgVPEb7QmEW3xxzpv3vfBZ-Ro6QK3-w70Y6FuHwHc99BMKQ8ihFGfJcLWW150j4531nnUZ5QS_4vvfwZWWoYzLfRpWuxBmArk_iXZhIjWzGwdGbaqXQKPtSdegbPZ4vLEJBgmtI1Qhl6TO1hIhQMhObRKV2h9P_-AdzbFU91GBq1DB6utedK4YCyWEvM4PEc0XNcmKK0NTNMtkJSyeI9zDndGZj-iM_tK94PNub4ahNzZmiZSb7FjzRVULQyghtwHRgzrhWmIOTHA9VnqxVK5Ko4AxObdya1CfsUTFF9lHplagM9LfMCmM96MGMJwqfT_Hm3LDXBV-uwPC-ClEiCMqEsMtfiYEeJyZHggvjmM8IV1bFakAU_GpRqJaZ7HhJfmTAlIoVeb4i_onFq_rT16zNqrlfyNrql_LsQybkEI6aEHBf30F5HixD_QH4Tv9ZoVmperdlquY7X9qpOs0T2xC97XqvScJv1lus69Xbdqx5L5I9VrVUadbdWbVe9ttOseW3XO_4FWhFDqw?type=png)

###### Retrieve data from backup:

![Flow Diagram](https://mermaid.ink/img/pako:eNpNUs93mkAQ_lfm7aEn9YkKEg7pa0zTeGgPTXtoJIeRHWUrsLxl1tao_3sH0KRcmB2-H_Mtc1SZ1aQStXVY5_DjPq1AnufVE6PjFxgOb_vOp9V3YmdoT6CRETbOlrDGbOfrDgV3x2UDmeED_CHknBxoYjRFA7iXF64LAlNdKB_PvepdSz39ouYEi9WDKVho1jM0jMXFaAiOSiu2NTYsdWadbgawI6oh885RxfABNp69o-tnePlf_ps9wUM7vncVYAVU1jKlXf-mjAXYQxddCHhcfaGKHDIJUoteR7omQr03GV3VH1vK8rjIKduB2byZZ9bLTDlKlWO1JQ21ldH7a-sySvqUl-_ZP69-1rr1vBr1t9SipWc4v_BEqlW5-KuBKsmVaLT8v2PbS5WwS0pVIqWmDfqCU5VWZ4GiZ_t0qDKVsPM0UM76ba6SDRaNnHznf29Q9qB869ZYPVtbXimkDVv3tV-Ybm86iEqO6q9Kgnk4moTBOIzGN7NoHIcDdVDJcDKJRlEUB9NoOr-ZRJPp7DxQr51qMIrH4SyOg_l0Ho7jIAzP_wBg2NVQ?type=png)

#### Online Mode:
1. Fetches weather data from the external API.
2. If the API returns valid data:
   - Generate and return weather advice. 
   - If city weather details do not exist in the backup, set the `lastUpdated` field with the current UTC time and update the backup with the data.
   - If the city exists in the backup, generate and return weather advice. Then update the backup if:
     - The difference between the current UTC time and the `lastUpdated` value is greater than 30 minutes.
     - The the first weather entry in the backup is different from the fetched details from the API.
     - Before update, set the `lastUpdated` field with the current UTC time.
3. API failure scenarios:
   - Wrong city input: Return empty data with a corresponding message and status code.
   - Other issues: Fall back to the backup, retrieve data from backup and filter out the past records, generate weather advice, then update the backup file if data has been filtered. If       not found, return 503 error with the message: "Service temporarily unavailable."

#### Offline Mode:
1. Directly retrieve weather details from the backup, retrieve data from backup and filter out the past records, generate weather advice, then update the backup file if data has been 
   filtered. 
2. If unavailable return 503 error with the message: "Service temporarily unavailable."

##

### Custom Backup Design

This section outlines the design and workflow of the weather backup system, focusing on efficient read and initialization of backup, asynchronous updates, and concurrency handling. The solution ensures quick API responses, thread-safe operations, and the ability to handle concurrent read and write operations seamlessly.

#### Read Workflow

1. **File Read during startup**: 
   - At the time of program initialization, the backup file is read only          once.
   - The file contents are deserialized and mapped into an in-memory `Map`        for quick access.
   - This ensures no read and deserializing operations during runtime,            allowing the service to fetch city weather details directly from             in-memory `Map`.

2. **Concurrent Operations Handling**:
   - The in-memory `Map` allows fast, thread-safe operations.
   - Concurrent operations are fully supported, providing the latest weather      data without contention.

#### Update and write workflow

To ensure thread safety and efficient handling of concurrent updates, updates to the backup file are managed through a **queue-based mechanism**. All update requests are queued and processed asynchronously. This ensures:

- **Non-blocking API Responses**: 3rd party API responses are returned         immediately to next component for generating weather advice ,                without waiting for the backup file to be updated. This ensures that         service is asynchronous and quickly provide response to the end user.
- **Asynchronous Updates**: The update and write process runs as a side        effect, ensuring in-memory data updates and writes to backup files happens   in the background.
- **Thread Safety**: Only one update thread interacts with the file at a time, avoiding file corruption.

#### Workflow : Update of in-memory map and backup file

1. **Real-Time Updates for In-Memory data**:
   - The in-memory `Map` is synchronized and updated immediately and made available with latest data.
   - This ensures subsequent calls to the service fetches the latest data from memory, even if the backup file is still being updated (if 3rd party services are unavailable or service is in offline mode).

2. **Queue-Based Update Requests and Executions**:
   - Each update request (e.g., a new city weather detail) is added to a thread-safe queue.
   - Requests are hence, processed sequentially to prevent concurrent writes to the backup file.
   - When the backup file is free from any current thread operation, the next update in the queue is processed.
---

### Service Implementation:

#### API-First Approach

The development process followed an **API-first approach**, ensuring clear communication and alignment between stakeholders and developers.

##### Contract Finalization

1. The API contracts were finalized before hand keeping in my mind a             robust and consistent response model for all types of responses whether        valid or invalid.  
2. The success response structure was kept simple and self descriptive so that     it's integration could be quick and can meet the UI requirements easily. 
3. This approach ensured the API design met all functional requirements and       addressed edge cases before implementation began.

##### OpenAPI Specification

1. Once the contract finalized, **OpenAPI specifications** were created to       define the API structure, including:
      - Endpoint and their use case.
      - Endpoints
      - Sample request and response structures for all possible responses.
      - Swagger was used to implement the Open API specification.
2. These specifications acted as a **single source of truth**, enabling teams to develop and test their components independently.

##

#### Integrating Open Weather API

The City Weather Advisory Service is based on **reactive programming paradigm** along with **circuit breaker design pattern**, for non-blocking and asynchronous execution. This approach ensures high performance, scalability, resilience and fault-tolerant making it an ideal solution for a weather service that can receive multiple concurrent requests.

##### Why a Reactive Approach and Circuit Breaker Pattern?

1. Given the high volume of requests the service needs to process, the           reactive paradigm ensures efficient resource usage and concurrent request     handling. 
2. The circuit breaker pattern is integrated to enhance fault tolerance by       gracefully managing failures from third-party APIs, ensuring uninterrupted    service availability.

##### Key Workflows:
1. **Data Fetching**:
   - Weather data is fetched reactively from the third-party service.
   - The non-blocking nature of the system allows parallel handling of multiple requests.
2. **Backup Update**:
   - Once data is fetched, the system updates the local backup asynchronously as a **side effect**, ensuring that the response time to the user remains unaffected.
3. **Fault Tolerance**:
   - In case of third-party failures, the circuit breaker activates and retrieves weather data from the local backup, maintaining service continuity.

##
### Logging and Monitoring in the Weather Application

#### Logging Setup

The weather application uses **SLF4J (Simple Logging Facade for Java)** for logging. SLF4J provides a simple abstraction that enables developers to integrate their code with various logging frameworks. In this application, **Logback** is used as the underlying logging implementation.

#### Why Logback 

- **Performance and Reliability:**
     Logback is designed for high-performance logging, ensuring that log operations do not impede application performance. It handles large volumes of logs efficiently, making it suitable for a production-grade weather service.

- **Flexible Configuration:** Logback’s configuration allows for fine-grained control over logging output. This includes adjusting log levels (INFO, WARN, ERROR, etc.), setting output formats, and choosing log destinations.

- **Advanced Logging Options:** The application’s logging configuration supports rolling file appenders to manage log files over time, ensuring that logs do not grow indefinitely. This helps maintain system stability and makes it easier to archive or delete older logs.

- **Structured Logging for Insights:** By including structured data (like city names, timestamps, and error codes) in the logs, developers and operators can quickly pinpoint issues, analyze trends, and understand the application’s state at any given time.

#### Monitoring Integration

To monitor the application’s logs, a centralized monitoring stack is configured. The **ELK Stack (Elasticsearch, Logstash, and Kibana)** provides a robust environment for log aggregation, searching, visualization, and alerting.

#### Use case in the application:

- **Centralized Log Management:**  
  By consolidating logs into a single platform, developers can easily query logs from multiple instances of the application.

- **Real-Time Visibility:**  
  Kibana dashboards offer live insights into the application’s behavior, helping to detect anomalies and address potential problems before they escalate.

- **Proactive Alerts:**  
  Monitoring rules and alerts ensure that critical events—such as API failures or unusual response times—are brought to attention immediately.

- **Enhanced Troubleshooting:**  
  With a structured log environment, identifying root causes of errors becomes faster and more efficient, reducing downtime and improving overall reliability.

#### Summary

The combination of SLF4J with Logback along with ELK Stack provides a comprehensive logging and monitoring solution. This approach helps maintain system stability, ensures quick troubleshooting, and delivers valuable insights into the health and performance of the weather application.

##

### Code developemnt and practices

#### Adherence to SOLID Principles

1. **Single Responsibility Principle:**
    - `CityWeatherService` focuses solely on fetching and preparing weather data, delegating external calls and file operations to dedicated utilities like `ExternalAPIManager` and `FileManager`.

2. **Open/Closed Principle:**
    - The code uses interfaces (`ICityWeatherService`) and abstractions, allowing future implementations of `ICityWeatherService` without modifying existing code.

3. **Liskov Substitution Principle:**
    - Code written against the `ICityWeatherService` interface can easily swap implementations without altering dependent logic.

4. **Dependency Inversion Principle:**
    - The constructor of `CityWeatherService` takes `ExternalAPIManager` and `FileManager` as dependencies, which are provided at runtime, decoupling the core logic from specific implementations.

#### Adherence to the DRY Principle

**Reusable External API Calls:**  The `fetchData` method in `ExternalAPIManager` follows the DRY principle by providing a generic mechanism to perform GET calls against any API endpoint. Instead of writing duplicate logic for each API interaction, this single method handles the URL building, query parameter handling, response mapping, and error handling. As a result, the same reusable code can be applied across multiple services or API integrations, reducing redundancy and improving maintainability.

#### 12 Factor App Guidelines in Practice

**Config Through Environment Variables:** API and service realted configurations are externalized using `@Value` annotations to inject environment-specific variables, adhering to the configuration as code principle.

**Single Codebase for All Environments:** All code is maintained in a single Git repository and is used consistently across development, staging, and production environments, ensuring uniformity and simplicity in deployments.



#### Application of Design Patterns

**Factory Pattern:**
    - `FileManager` and `ExternalAPIManager` act as providers for different types of resources and utilities, abstracting the instantiation logic.

**Template Method:**
 The `getWeatherAdvice` method outlines the high-level process of retrieving weather data. It relies on the lower-level methods (`getCityWeatherDetails`, `generateWeatherAdvice`) to handle specific steps, following a defined structure while allowing flexibility in detail handling.

**Decorator/Adapter Patterns:**
 The `WeatherAdviceManager` and `DateTimeManager` utilities act as adapters, transforming raw API data and timestamps into usable formats without altering the underlying source.

**Strategy Pattern:** Different advice generation strategies could be added by modifying the `generateWeatherAdvice` method. The current implementation shows how specific logic is encapsulated, making it easier to introduce new advice strategies in the future.

---
### UI Design and workflow

#### Overview
This section outlines the proposed development of a mobile-first weather application using React and Bootstrap. The app is intended to fetch weather data for a city entered by the user and provide a detailed weather forecast and a coressponding advice. The design approach prioritizes responsiveness, modularity, and effective state management to ensure scalability and maintainability.

#### Features
 1. **Mobile-First Design**
    - The application is designed with a mobile-first approach to ensure usability on smaller screens.
    - Components and layouts will adapt seamlessly to larger screens to enhance user experience.

 2. **Modular Components**
    - The app is built using independent, reusable components.
    - Functional components like `SearchBar`, `WeatherTable`, `Header`, and `Footer` will simplify maintenance and allow for future enhancements.

3. **State Management**
    - React Hooks were used to manage states effectively.
    - State updates triggered re-renders, ensuring data consistency across       the app.

4. **Error Handling**
      - The app implements robust error handling mechanisms:
          - Display appropriate error messages for API failures.
          - Handle invalid or empty user inputs gracefully.

5. **API Integration**
    - The app is integrated with a backend API to fetch weather data.
    - The UI is designed to dynamically render weather data fetched from the API.
    - Fallback messages will be displayed to handle scenarios where the API is unreachable.

#### User Journey
1. **Search for Weather**
   - Users will input a city name in the search bar.
   - Clicking the "Search" button will trigger an API call.

2. **Data Display**
   - On successful response, weather data will be displayed in a structured table.
   - On failure, appropriate error messages will be shown to the user.

3. **Navigation**
   - A "Back to Top" link will allow users to quickly navigate to the search bar.

---

### CI/CD Design

#### Overview
The CI/CD pipeline for the Weather App ensures secure, automated deployment of both the frontend (FE) and backend (BE) applications. Docker containers are utilized for consistent environments, and Jenkins is leveraged for pipeline automation.

#### Design Goals
1. **Secure API Key Management:**
   - Backend applications receive API keys from Jenkins as environment variables during the pipeline execution.
   - This ensures that sensitive information is not hardcoded or exposed in the source code.

2. **Dockerized Applications:**
   - Both frontend and backend are containerized for consistency across development, testing, and production environments.
   - Docker Compose is used to deploy and manage the containers together, ensuring seamless communication.

3. **Automated Pipeline Workflow:**
   - Jenkins pipeline automates the process of building, testing, and deploying the applications.

#### CI/CD Pipeline Steps
1. **Code Checkout:**
   - Source code is pulled from the Git repository.

2. **Build:**
   - Docker images are built for both the frontend and backend applications.

3. **Environment Variable Injection:**
   - Jenkins injects API keys as environment variables to the backend container.

4. **Unit Test Execution:**
   - Unit tests for both the frontend and backend are executed within their respective Docker containers.

5. **Deployment:**
   - Use Docker Compose to deploy both the frontend and backend containers:
---
