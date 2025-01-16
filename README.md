# City Weather Advisory App

### Overview
The application provides a real-time weather advisory service to users, with a robust fallback mechanism for offline or error scenarios. The service fetches data from an external API and maintains a local backup for uninterrupted service.

---

### Key Features:
1. **Real-Time Weather Advisory**: Fetches weather data for cities using an external API.
2. **Offline Fallback**: Uses backup data when the service is offline or the external API fails.
3. **Weather Advice Generation**: Provides actionable advice based on weather conditions.
4. **Data Backup and Maintenance**: Updates the local backup with the latest weather data to ensure availability.

---

### Service Architecture and work-flow:

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

#### Flow Diagram:
Weather Advisory Service:

![Flow Diagram](https://mermaid.ink/img/pako:eNp9k0tz2jAUhf_KHS26AsZgzMOLZHiEQhJIJkA7rWGh2hfQ1JaoLJFSzH-vLENCZkoX9tjSd885upIOJBQREp-sJd1uYNZfcIBOMFVUqiWUyzfQDUYpPPGYcYSxYW-XOdLN57JvmGbQCwaowg18Rao2KKFPFYWVFAl0nkcX8ERkcB-8oJIMd3iBdWn4U28NmbM9a9o_GFNTDi-YbgVPEb7QmEW3xxzpv3vfBZ-Ro6QK3-w70Y6FuHwHc99BMKQ8ihFGfJcLWW150j4531nnUZ5QS_4vvfwZWWoYzLfRpWuxBmArk_iXZhIjWzGwdGbaqXQKPtSdegbPZ4vLEJBgmtI1Qhl6TO1hIhQMhObRKV2h9P_-AdzbFU91GBq1DB6utedK4YCyWEvM4PEc0XNcmKK0NTNMtkJSyeI9zDndGZj-iM_tK94PNub4ahNzZmiZSb7FjzRVULQyghtwHRgzrhWmIOTHA9VnqxVK5Ko4AxObdya1CfsUTFF9lHplagM9LfMCmM96MGMJwqfT_Hm3LDXBV-uwPC-ClEiCMqEsMtfiYEeJyZHggvjmM8IV1bFakAU_GpRqJaZ7HhJfmTAlIoVeb4i_onFq_rT16zNqrlfyNrql_LsQybkEI6aEHBf30F5HixD_QH4Tv9ZoVmperdlquY7X9qpOs0T2xC97XqvScJv1lus69Xbdqx5L5I9VrVUadbdWbVe9ttOseW3XO_4FWhFDqw?type=png)

Retrieve data from backup:

![Flow Diagram](https://mermaid.ink/img/pako:eNpNUs93mkAQ_lfm7aEn9YkKEg7pa0zTeGgPTXtoJIeRHWUrsLxl1tao_3sH0KRcmB2-H_Mtc1SZ1aQStXVY5_DjPq1AnufVE6PjFxgOb_vOp9V3YmdoT6CRETbOlrDGbOfrDgV3x2UDmeED_CHknBxoYjRFA7iXF64LAlNdKB_PvepdSz39ouYEi9WDKVho1jM0jMXFaAiOSiu2NTYsdWadbgawI6oh885RxfABNp69o-tnePlf_ps9wUM7vncVYAVU1jKlXf-mjAXYQxddCHhcfaGKHDIJUoteR7omQr03GV3VH1vK8rjIKduB2byZZ9bLTDlKlWO1JQ21ldH7a-sySvqUl-_ZP69-1rr1vBr1t9SipWc4v_BEqlW5-KuBKsmVaLT8v2PbS5WwS0pVIqWmDfqCU5VWZ4GiZ_t0qDKVsPM0UM76ba6SDRaNnHznf29Q9qB869ZYPVtbXimkDVv3tV-Ybm86iEqO6q9Kgnk4moTBOIzGN7NoHIcDdVDJcDKJRlEUB9NoOr-ZRJPp7DxQr51qMIrH4SyOg_l0Ho7jIAzP_wBg2NVQ?type=png)

### Service Implementation:

#### API-First Approach

The development process followed an **API-first approach**, ensuring clear communication and alignment between stakeholders and developers.

##### Contract Finalization

1. The API contracts were finalized before hand keeping in my mind a             robust and consistent response model for all types of responses wether        valid or invalid.  
2. The success response structure was kept simple and self descriptive so that     it's integration could be quick and can meet the UI requirements easily. 
3. This approach ensured the API design met all functional requirements and       addressed edge cases before implementation began.

##### OpenAPI Specification

1. Once the contract finalized, **OpenAPI specifications** were created to       define the API structure, including:
      - Endpoint and their use case.
      - Endpoints
      - Sample request and response structures for all possible responses.
      - Swagger was used to implement the Open API specification.
2. These specifications acted as a **single source of truth**, enabling teams to develop and test their components independently.

#### Integrating Open Weather API

The City Weather Advisory Service is based on **reactive programming paradigm** coupled **circuit breaker design pattern**, for non-blocking and asynchronous execution. This approach ensures high performance, scalability, resilience and fault-tolerant making it an ideal solution for a weather service that can receive multiple concurrent requests.

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

