# vmms-backend
Virtual Machine Management System Backend

## Installation
Simply import project as gradle project

## Development

### Manually running postgresql:
`sudo docker-compose up`

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