
## Test Vault user password

Use postgrees database

	CREATE USER postgres1 WITH PASSWORD 'postgres1';
	GRANT ALL PRIVILEGES ON ALL TABLES IN SCHEMA public TO postgres1;
	
	
Update csw application.properties

	vault.databaseUser=postgres,postgres1
	vault.password.url={}
		
Start the CSW

change the postgres user password

	sudo su
	su - postgres
	psql
	\password
	
You shoud see a message like this	
	
	######### Try to get a new password from Vault for
	
	