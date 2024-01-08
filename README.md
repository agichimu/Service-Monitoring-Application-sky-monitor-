# **Service-Monitoring-Application-sky-monitor**
### A Service is an application hosted on a particular server. 
## The purpose of this Service Monitoring Application is to check if:
1. A service’s application is up or down e.g. a website or web application or even an API.
2. The server where the service is hosted is up or down
## Functional Requirements
1. The system should have a configuration file to store the services that need to be monitored.
2. The system should dynamically support the following configuration file types:
   `a. XML (.xml)
   b. JSON (.json)
   c. YAML (.yaml)
   d. INI (.ini)`
3. Each service to be monitored should be store in the configuration file.
4. The system should log each service separately.
5. **`The system should log for each service whether:
   a. the service’s application is up or down
   b. the server hosting the service is reachable or not`**.
6. The **system should log as per the Monitoring Intervals** defined in the configuration file
7. The system should log the timestamp & status when the monitoring was done.
8. The **system should allow logging of the status of the services to a log file as per File Logging
   Interval**. The system should have configuration on the file, Enable File Logging, to enable
   or disable this feature.
9. If file logging is enabled, the system should log to files in the intervals outlined on the
   configuration file.
10. The **system should allow archiving of older log files as per the Log Archiving Intervals**. The
    system should have configuration on the file, Enable Logs Archiving, to enable or disable
    this feature.

## error connecting to a service
![Screenshot from 2024-01-08 15-50-20](https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-/assets/97959452/67b9a5e7-1249-40a4-bf01-ab9c9e7d1bf1)


### `folder for logging`  
![Screenshot from 2024-01-08 13-40-12](https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-/assets/97959452/acae62ad-8d12-42a2-988c-b8a3e581ce0d)


## `logs for each service` 
![Screenshot from 2024-01-08 13-40-27](https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-/assets/97959452/abcae61b-74a0-498c-bcd3-1da98779d93b)

![Screenshot from 2024-01-08 13-40-58](https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-/assets/97959452/2c6273b5-1cd6-4392-b77d-db64546b8d32)


![Screenshot from 2024-01-08 13-41-12](https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-/assets/97959452/89ca2935-21ec-4aa8-899f-0c021e3c8134)

````
Author :  https://github.com/agichimu/Service-Monitoring-Application-sky-monitor-
````
