## Web chat for VK Core internship
Usage: start the server, start the client and run your commands.
To start the server, you need to specify database data in _application.properties_
### Command list:
* _login \[username] \[password]_
* _register \[username] \[password]_
* _friends_
* _add \[friend]_
* _accept \[friend]_
* _reject \[friend]_
* _chat \[friend]_
* _send \[friend] \[message]_

#### Так как клиент и сервер - независимые приложения, подразумевается запуск разных проектов, код фронта добавлен в проект для удобства

### Сборка и запуск сервера:
* Конфигурируем resource bundle: src/main/resources/application.properties
* * _dbURL_ - ссылка на базу данных postgres
* * _dbUsername_ - логин для подключения к БД
* * _dbPassword_ - пароль для поключения к БД
* * _secret_ - любая строка, на основе которой генирируются токены
* _maven package_ - собрать проект в jar
* _java -jar VKTestTaskBackend-1.0-SNAPSHOT-shaded.jar_ - запуск jar-файла с необходимыми зависимостями

### Сборка и запуск клиента:
* Интересующий нас проект: https://github.com/Erwelus/VKTestTaskFrontend
* _maven package_ - собрать проект в jar
* _java -jar VKTestTaskFrontend-1.0-SNAPSHOT.jar_ - запуск jar-файла