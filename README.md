# DzenlabTest
В тестовой работе использовались:
* Wildfly 10.0
* Hibernate 5.1
* Maven 3.0.4
* JUnit 4.12
* JMockit 1.16
* IntelliJ IDEA 2016.1.2

Для сборки необходимо:
* Clone|download проект на локальную машину
* Перейти в каталог проекта
* собрать: mvn clean test package
* На wildfly сервере создать Non-XA DataSource с jndi name - java:/DzenlabDS
* Задеплоить target/DzenlabTest-1.0-SNAPSHOT.war
* Перейти на страницу: http://localhost:8080/DzenlabTest/ 
