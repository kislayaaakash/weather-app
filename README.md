# City Weather Advisory App

## Overview
The application provides a real-time weather advisory service to users, with a robust fallback mechanism for offline or error scenarios. The service fetches data from an external API and maintains a local backup for uninterrupted service.

### Key Features:
1. **Real-Time Weather Advisory**: Fetches weather data for cities using an external API.
2. **Offline Fallback**: Uses backup data when the service is offline or the external API fails.
3. **Weather Advice Generation**: Provides actionable advice based on weather conditions.
4. **Data Backup and Maintenance**: Updates the local backup with the latest weather data to ensure availability.

### Service Design and work-flow:

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
   - Other issues: Fall back to the backup, generate weather advice, and if not found, return 503 error with the message: "Service temporarily unavailable."

#### Offline Mode:
1. Directly retrieve weather details from the backup, generate weather advice, and return.
2. If unavailable return 503 error with the message: "Service temporarily unavailable."

### Flow Diagram:
Weather Advisory Service:

![Flow Diagram](https://mermaid.ink/img/pako:eNp9k0tz2jAUhf_KHS26AsZgzMOLZHiEQhJIJkA7rWGh2hfQ1JaoLJFSzH-vLENCZkoX9tjSd885upIOJBQREp-sJd1uYNZfcIBOMFVUqiWUyzfQDUYpPPGYcYSxYW-XOdLN57JvmGbQCwaowg18Rao2KKFPFYWVFAl0nkcX8ERkcB-8oJIMd3iBdWn4U28NmbM9a9o_GFNTDi-YbgVPEb7QmEW3xxzpv3vfBZ-Ro6QK3-w70Y6FuHwHc99BMKQ8ihFGfJcLWW150j4531nnUZ5QS_4vvfwZWWoYzLfRpWuxBmArk_iXZhIjWzGwdGbaqXQKPtSdegbPZ4vLEJBgmtI1Qhl6TO1hIhQMhObRKV2h9P_-AdzbFU91GBq1DB6utedK4YCyWEvM4PEc0XNcmKK0NTNMtkJSyeI9zDndGZj-iM_tK94PNub4ahNzZmiZSb7FjzRVULQyghtwHRgzrhWmIOTHA9VnqxVK5Ko4AxObdya1CfsUTFF9lHplagM9LfMCmM96MGMJwqfT_Hm3LDXBV-uwPC-ClEiCMqEsMtfiYEeJyZHggvjmM8IV1bFakAU_GpRqJaZ7HhJfmTAlIoVeb4i_onFq_rT16zNqrlfyNrql_LsQybkEI6aEHBf30F5HixD_QH4Tv9ZoVmperdlquY7X9qpOs0T2xC97XqvScJv1lus69Xbdqx5L5I9VrVUadbdWbVe9ttOseW3XO_4FWhFDqw?type=png)

Retrieve data from backup:

![Flow Diagram](https://mermaid.ink/img/pako:eNpNUk1z0zAQ_Ss7OnByMrHdxI0PMLQhNAcuFIahcQ8baR2L2JZHXqUEJ_8d2W4Kumi1-_a9_VAnpFEkUrG32BTwbZXV4M_T9pHR8jNMJu9Hz8ftV2Kr6UigkBFyayrYoTy4ZkDBXbdpQWo-_SDkguyKGHXZAh79hbuSPlxGprsefv5J7Rnut2tdMlkwjqFlLF_JJ2CpMl6qwZa9LY1VbQAHogaks5ZqhneQO3aWrmF4_p9-7UV98Azrvm5na8AaqGr4BGb3iyR79Ii_H6qHh-1nqskik0cqTzokvYy9AKqjlnSVeOhTNp0sSB5A528VSON8YQV6q8B6T8p3nPHmX7-ftt8b1UtceccBQq595y-ai97y8yA1zOFVTgSiIluhVn5PXe_LhM-uKBOpNxXl6ErORFZfPBQdm8dTLUXK1lEgrHH7QqQ5lq1_uUF_pdHvu3rzNlg_GVNdU0hpNvbL-DGG_zFARNqJ3yINF-E0nkfxIpol8zBJkttAnEQaJdPlMpnNwygJwzhK4vgSiD8DazSNZ7fhTbi8WSTLaDGL5pe_NqrNJA?type=png)


---

## How to Use:

1. **Start the Service**: Initialize the service to start fetching weather data.
2. **Handle Errors**: Ensure that fallback mechanisms are in place for API failures or offline scenarios.
3. **Backup Updates**: The service will periodically update the backup to maintain the latest data.

---

### Notes:
- This project follows **SOLID** principles for better maintainability and scalability.
- It is designed to be highly **scalable** by implementing asynchronous communication and efficient data fetching.
- **12-factor app** methodology is considered by ensuring:
  - Externalized configuration (API endpoints, backup paths).
  - Stateless operations where applicable.
  - Backups are kept outside of the app container (storage-based).
  - Continuous integration and monitoring through logging and error handling.
