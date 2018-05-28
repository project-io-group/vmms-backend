# vmms-backend
Virtual Machine Management System Backend

## Installation
Simply import project as gradle project

### Lombok
You need to use a `Lombok` plugin to be able to start the project from your IDE (You'll get compilation errors otherwise). There are installation instructions on [project's site](https://projectlombok.org/), among them instructions for [IntelliJ](https://projectlombok.org/setup/intellij) and [Eclipse](https://projectlombok.org/setup/eclipse).

## Development

### Manually running postgresql:
#### To set up the docker container with database
`docker-compose up -d`
option -d is for running in the background
#### To remove the database:
`docker-compose down`
#### To stop the database:
`docker-compose stop`
(without removing the data schema)
#### To start the database:
`docker-compose start`
(run only when you have the database, but it has been stopped)

### Connecting to the database:
`docker exec -it vmmsbackend_db_1 psql -h localhost -p 5432 -U vmms_app vmms`

Parameters for connecting with external software can be read from `docker-compose.yml`

### Manually inserting to database after connection:
`insert into users(id, is_admin, user_name) values (5, false, 'ala');`

### Manually checking endpoints:
`curl -H "Content-Type: application/json" -X POST -d '{"name": "ala"}' http://localhost:9045/user/create`
`curl -H "Content-Type: application/json" -X POST -d '{"userId": 1}' http://localhost:9045/user/delete`

### You can check users at:
`http://localhost:9045/users`

### You can check vm pools at:  
`http://localhost:9045/vm/` (list all)   
`http://localhost:9045/vm/enabled` (list only enabled)   
`http://localhost:9045/vm/tag/some_tag` (list all with some_tag in description)   

### You can import pcoips (pcoip_pools.csv) with:
`curl -i -X POST -F "file=@pcoip_pools.csv" http://localhost:9045/vm/import`

### You can check reservations at:
`http://localhost:9045/reservations?userId=user_id` (list all reservations made by user with given id)  
`http://localhost:9045/reservations?userId=user_id&from=from_date&to=to_date` (the same as above but reservations are between from_date and to_date)  
`http://localhost:9045/vm/{vmShortName}/between?from=from_date&to=to_date`  (list all reservations for one vmpool between from_date and to_date)  
`http://localhost:9045/reservation?reservationId=reservation_id`  (get reservation with given id)    

(Date format is `yyyy-MM-dd HH:mm`)

### You can send e-mail by POSTing at:
`http://localhost:9045/email?subject=definedSubject&content=URL-encodedContent`

For this to work you have to set `SENDGRID_API_KEY` environment variable with *SendGrid* API key. If you're working in IntelliJ, you have to set it in `Run`/`Edit Configurations` window, as system-wide variables are getting overwritten. You also have to define subjects and recipients...

**WARNING: the app WON'T RUN at all without the above mentioned ENVIRONMENT VARIABLE!!!**

#### Defining e-mail subjects and recipients
You can define e-mail subjects and recipients by **POST**ing at `/email/configure` with `Content-Type: application/json` a JSON of which example is in the `email_configuration_example.json` as a request body. Each new configuration overwrites the previous one.