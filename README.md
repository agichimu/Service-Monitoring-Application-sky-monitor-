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

### `folder for logging`  
![Screenshot from 2024-01-08 13-40-12.png](..%2F..%2F..%2F..%2Fhome%2Fbrutal%2FPictures%2FScreenshots%2FScreenshot%20from%202024-01-08%2013-40-12.png)

## `logs for each service` 
![Screenshot from 2024-01-08 13-40-27.png](..%2F..%2F..%2F..%2Fhome%2Fbrutal%2FPictures%2FScreenshots%2FScreenshot%20from%202024-01-08%2013-40-27.png) 

![Screenshot from 2024-01-08 13-40-58.png](..%2F..%2F..%2F..%2Fhome%2Fbrutal%2FPictures%2FScreenshots%2FScreenshot%20from%202024-01-08%2013-40-58.png)

![Screenshot from 2024-01-08 13-41-12.png](..%2F..%2F..%2F..%2Fhome%2Fbrutal%2FPictures%2FScreenshots%2FScreenshot%20from%202024-01-08%2013-41-12.png)
````
Authors : Alexander Wambugu 
````