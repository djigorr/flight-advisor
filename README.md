# Flight Advisor

Rest API for finding the cheapest flights

## Instructions for use from command line:  
git clone https://github.com/djigorr/flight-advisor.git  
cd flight-advisor/flight-advisor  

build: mvn clean install  
run: mvn spring-boot:run

#### Login:  
url: http://localhost:8080/api/login  
username: Admin  
passwor: 123
Note: For every request set header 'X-Auth-Token' with value of token gotten after successful login

#### H2-console:  
url: http://localhost:8080/h2-console  
username: sa  
password:  

#### Swagger:  
url: http://localhost:8080/swagger-ui.html  
