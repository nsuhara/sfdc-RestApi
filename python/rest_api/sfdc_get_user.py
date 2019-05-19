import json

from simple_salesforce import Salesforce

USERNAME = '<パラメータ参照>'
PASSWORD = '<パラメータ参照>'
SECURITY_TOKEN = '<パラメータ参照>'


def main():
    sf = Salesforce(username=USERNAME, password=PASSWORD,
                    security_token=SECURITY_TOKEN, sandbox=False)
    res = sf.query(
        'SELECT Id, Name, LastLoginDate FROM User WHERE Name =\'nsuhara\'')
    print(json.dumps(res, indent=4))


if __name__ == '__main__':
    main()
