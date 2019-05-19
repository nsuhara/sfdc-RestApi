import requests

HOST = '<パラメータ参照>'
CLIENT_ID = '<パラメータ参照>'
CLIENT_SECRET = '<パラメータ参照>'
USERNAME = '<パラメータ参照>'
PASSWORD_AND_SECURITY_TOKEN = '<パラメータ参照>'


def main():
    params = {
        'grant_type': 'password',
        'client_id': CLIENT_ID,
        'client_secret': CLIENT_SECRET,
        'username': USERNAME,
        'password': PASSWORD_AND_SECURITY_TOKEN
    }

    res_post = requests.post(
        'https://{}/services/oauth2/token'.format(HOST), params=params)

    access_token = res_post.json().get('access_token')
    instance_url = res_post.json().get('instance_url')
    services_url = '/services/apexrest/sample/restapi'
    headers = {
        'Content-type': 'application/json',
        'Accept-Encoding': 'gzip',
        'Authorization': 'Bearer {}'.format(access_token)
    }

    res_get = requests.request(method='get', url=instance_url+services_url,
                               headers=headers, params={'xxx': 'yyy'}, timeout=10)

    print(res_get.status_code)
    print(res_get.json())


if __name__ == '__main__':
    main()
