@RestResource(urlMapping='/sample/restapi/*')
global without sharing class SampleRestApi {
	@HttpGet
	global static List<User> getUsers() {
		return [SELECT Id, Name, LastLoginDate FROM User WHERE Name = 'nsuhara' LIMIT 1];
	}
}