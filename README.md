# Event Management Service

The event-management-service provides an interface to manage events. The service only provides an interface to get artist events at the moment but provides the base to expand into other event management interfaces. 

# Interface
## /

## Running
### Docker 
If you simply wish to run the application, we recommend starting with Docker

#### Dependencies
- docker

#### Running
Within the base directory of the project, run the following command:
```
docker-compose up
```
#### Troubleshooting 
##### Port clashes
The app starts on 8085. You can change this port within the docker-compose.yml file.
