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

### Manually inserting to database after connection:
`insert into users(id, user_name) values (1, 'ala');`

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
`curl -i -X POST -F "file=@path_to_file" http://localhost:9045/vm/import`
