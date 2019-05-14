//This CRUD operation is working fine, with just little issue with update operation
//to run this file run fueski server in the background and create a database with name "test"

//CREATE 
//use POST request

http://localhost:8080/insert

{

"name" : "Deepak",

"username": "dk@gmail.com",

"password":"123456"

}

//Read
//use GET request to fetch-all records
http://localhost:8080/selecta

output: you will receive full json data

//use POST request to fetch-current user record by username
http://localhost:8080/selectb
{

"username": "dk@gmail.com"
}

//Delete
//use POST request

http://localhost:8080/delete
{

"name" : "Deepak",

"username": "dk@gmail.com",

"password":"123456"

}

//update

http://localhost:8080/update

{

"name" : "Deepak Garg",

"username": "dk@gmail.com",

"password":"987654"

}

it will update the records, just little issue here of data appending


