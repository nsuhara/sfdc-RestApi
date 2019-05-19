# はじめに

*(Mac環境の記事ですが、Windows環境も同じ手順になります。環境依存の部分は読み替えてお試しください。)*

この記事を最後まで読むと、次のことができるようになります。

- SalesforceのAPI(OAuth 設定の有効化)を設定する
- Python, Java, Shell ScriptでSalesforceのデータを取得する
- SalesforceのApexでREST Webサービスを実装する

`サンプルコード`

```python_sfdc_get_user.py
sf = Salesforce(username=USERNAME, password=PASSWORD,
                security_token=SECURITY_TOKEN, sandbox=False)
res = sf.query(
    'SELECT Id, Name, LastLoginDate FROM User WHERE Name =\'nsuhara\'')
print(json.dumps(res, indent=4))
```

`実行結果`

```python_result.json
{
    "totalSize": 1,
    "done": true,
    "records": [
        {
            "attributes": {
                "type": "User",
                "url": "/services/data/v38.0/sobjects/User/0056F000006StK9QAK"
            },
            "Id": "0056F000006StK9QAK",
            "Name": "nsuhara",
            "LastLoginDate": "2019-05-18T13:07:00.000+0000"
        }
    ]
}
```

# 関連する記事

- [SFDC REST API 開発者ガイド](https://developer.salesforce.com/docs/atlas.ja-jp.api_rest.meta/api_rest/intro_what_is_rest_api.htm)

# 実行環境

|        環境       |    Ver.   |  Note  |
|-------------------|-----------|--------|
| macOS Mojave      | 10.14.4   | OS     |
| Salesforce        | Spring'19 | SaaS   |
| Python            | 3.7.3     | Python |
| simple-salesforce | 0.74.2    | Python |
| requests          | 2.22.0    | Python |
| Java              | 1.8.0_192 | Java   |
| httpclient        | 4.5.6     | Java   |
| json              | 20180813  | Java   |
| jackson-databind  | 2.9.8     | Java   |

# ソースコード

実際に実装内容やソースコードを追いながら読むとより理解が深まるかと思います。是非ご活用ください。

[GitHub](https://github.com/nsuhara/sfdc-RestApi.git)

# 事前準備

## SalesforceのOAuth設定

設定 > 作成 > アプリケーション > 接続アプリケーション > `新規`をクリックする

|            項目名            |                 設定値                  |
|------------------------------|-----------------------------------------|
| 接続アプリケーション名       | Sample                                  |
| API 参照名                   | Sample                                  |
| 取引先責任者 メール          | sample@gmail.com                        |
| OAuth 設定の有効化           | TRUE                                    |
| コールバック URL             | https://sample.auth0.com/login/callback |
| 選択した OAuth 範囲          | 基本情報へのアクセス                    |
| Web サーバフローの秘密が必要 | TRUE                                    |

<img width="500" alt="スクリーンショット 2018-12-19 23.42.27.png" src="https://qiita-image-store.s3.amazonaws.com/0/326996/32b14593-0691-bd48-531e-ed7ac6281e80.png">

`接続アプリケーションを使用する前に、サーバ上で変更が有効になるまで 2 ～ 10 分お待ちください。`

## パラメータの概要

|           変数名            |                              説明                              |  備考 |
|-----------------------------|----------------------------------------------------------------|-------|
| HOST                        | Production: login.salesforce.com, Sandbox: test.salesforce.com |       |
| CLIENT_ID                   | コンシューマ鍵                                                 | 参照1 |
| CLIENT_SECRET               | コンシューマの秘密                                             | 参照1 |
| USERNAME                    | Salesforceのユーザ名                                           |       |
| PASSWORD                    | Salesforceのパスワード                                         |       |
| SECURITY_TOKEN              | Salesforceのセキュリティトークン                               | 参照2 |
| PASSWORD_AND_SECURITY_TOKEN | Salesforceのパスワード + セキュリティトークン                  |       |

**(参照1)**

<img width="500" alt="スクリーンショット 2018-12-20 0.29.33.png" src="https://qiita-image-store.s3.amazonaws.com/0/326996/997ae843-b774-b32e-a576-bbe8fad75c8e.png">

**(参照2)**

セキュリティトークンは、私の設定 > 個人用 > `私のセキュリティトークのリセット`から再発行ができます。

<img width="400" alt="スクリーンショット 2018-12-20 0.43.24.png" src="https://qiita-image-store.s3.amazonaws.com/0/326996/1d6a4c55-d9ba-535e-4777-ef43ce8eeebe.png">

# Shell Scriptサンプル

```shell_script_sfdc_get_user.sh
#!/bin/sh

export HOST='<パラメータ参照>'
export CLIENT_ID='<パラメータ参照>'
export CLIENT_SECRET='<パラメータ参照>'
export USERNAME='<パラメータ参照>'
export PASSWORD_AND_SECURITY_TOKEN='<パラメータ参照>'

export INSTANCE_URL=`curl -s https://$HOST/services/oauth2/token -d "grant_type=password" -d "client_id=$CLIENT_ID" -d "client_secret=$CLIENT_SECRET" -d "username=$USERNAME" -d "password=$PASSWORD_AND_SECURITY_TOKEN" | awk 'BEGIN{FS="instance_url\":"}{print $2}' | awk 'BEGIN{FS=","}{print $1}' | sed -e 's/\"//g'`
export ACCESS_TOKEN=`curl -s https://$HOST/services/oauth2/token -d "grant_type=password" -d "client_id=$CLIENT_ID" -d "client_secret=$CLIENT_SECRET" -d "username=$USERNAME" -d "password=$PASSWORD_AND_SECURITY_TOKEN" | awk 'BEGIN{FS="access_token\":"}{print $2}' | awk 'BEGIN{FS=","}{print $1}' | sed -e 's/\"//g'`

export SOQL="SELECT+Id,Name,LastLoginDate+FROM+User+WHERE+Name='nsuhara'"

curl $INSTANCE_URL/services/data/v45.0/query?q=$SOQL -H "Authorization: OAuth $ACCESS_TOKEN" -H "X-PrettyPrint:1"
```

```shell_script_result.json
{
    "totalSize": 1,
    "done": true,
    "records": [
        {
            "attributes": {
                "type": "User",
                "url": "/services/data/v45.0/sobjects/User/0056F000006StK9QAK"
            },
            "Id": "0056F000006StK9QAK",
            "Name": "nsuhara",
            "LastLoginDate": "2019-05-18T14:04:05.000+0000"
        }
    ]
}
```

# Pythonサンプル

```python_sfdc_get_user.py
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
```

```python_result.json
{
    "totalSize": 1,
    "done": true,
    "records": [
        {
            "attributes": {
                "type": "User",
                "url": "/services/data/v38.0/sobjects/User/0056F000006StK9QAK"
            },
            "Id": "0056F000006StK9QAK",
            "Name": "nsuhara",
            "LastLoginDate": "2019-05-18T14:06:19.000+0000"
        }
    ]
}
```

# Javaサンプル

```java_App.java
package rest_api;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class App {
    static final String HOST = "<パラメータ参照>";
    static final String CLIENT_ID = "<パラメータ参照>";
    static final String CLIENT_SECRET = "<パラメータ参照>";
    static final String USERNAME = "<パラメータ参照>";
    static final String PASSWORD_AND_SECURITY_TOKEN = "<パラメータ参照>";

    static final String GRANT_SERVICE = "/services/oauth2/token?grant_type=password";

    public static void main(String[] args) {
        String UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_14_4) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/74.0.3729.131 Safari/537.36";
        List<Header> headers = new ArrayList<Header>();
        headers.add(new BasicHeader("User-Agent", UA));

        HttpClient httpClient = HttpClientBuilder.create().setDefaultHeaders(headers).build();

        String loginURL = "https://" + HOST + GRANT_SERVICE + "&client_id=" + CLIENT_ID + "&client_secret="
                + CLIENT_SECRET + "&username=" + USERNAME + "&password=" + PASSWORD_AND_SECURITY_TOKEN;

        HttpPost httpPost = new HttpPost(loginURL);
        HttpResponse response = null;

        try {
            response = httpClient.execute(httpPost);
        } catch (ClientProtocolException cpException) {
            // Handle protocol exception
        } catch (IOException ioException) {
            // Handle system IO exception
        }

        final int statusCode = response.getStatusLine().getStatusCode();
        if (statusCode != HttpStatus.SC_OK) {
            System.out.println("Error authenticating to Force.com: " + statusCode);
            // Error is in EntityUtils.toString(response.getEntity())
            return;
        }

        String getResult = null;
        try {
            getResult = EntityUtils.toString(response.getEntity());
        } catch (IOException ioException) {
            // Handle system IO exception
        }

        JSONObject jsonObject = null;
        String loginAccessToken = null;
        String loginInstanceUrl = null;

        try {
            jsonObject = (JSONObject) new JSONTokener(getResult).nextValue();
            loginInstanceUrl = jsonObject.getString("instance_url");
            loginAccessToken = jsonObject.getString("access_token");
        } catch (JSONException jsonException) {
            // Handle JSON exception
        }

        System.out.println(response.getStatusLine());

        httpPost.releaseConnection();
        try {
            final URIBuilder builder = new URIBuilder(loginInstanceUrl);
            final String SOQL = "SELECT Id, Name, LastLoginDate FROM User WHERE Name = \'nsuhara\'";
            builder.setPath("/services/data/v45.0/query/").setParameter("q", SOQL);
            final HttpGet get = new HttpGet(builder.build());
            get.setHeader("Authorization", "Bearer " + loginAccessToken);

            final HttpResponse queryResponse = httpClient.execute(get);

            ObjectMapper mapper = new ObjectMapper();
            final JsonNode queryResults = mapper.readValue(queryResponse.getEntity().getContent(), JsonNode.class);

            System.out.println("queryResults:" + queryResults);
        } catch (Exception e) {
            // Handle exception
        }
    }
}
```

```java_result.json
{
    "totalSize": 1,
    "done": true,
    "records": [
        {
            "attributes": {
                "type": "User",
                "url": "/services/data/v45.0/sobjects/User/0056F000006StK9QAK"
            },
            "Id": "0056F000006StK9QAK",
            "Name": "nsuhara",
            "LastLoginDate": "2019-05-18T14:09:31.000+0000"
        }
    ]
}
```

# REST Webサービスの実装サンプル

## Apex実装

```apex_SampleRestApi.cls
@RestResource(urlMapping='/sample/restapi/*')
global without sharing class SampleRestApi {
	@HttpGet
	global static List<User> getUsers() {
		return [SELECT Id, Name, LastLoginDate FROM User WHERE Name = 'nsuhara' LIMIT 1];
	}
}
```

## Apex REST サービス有効化

設定 > ユーザの管理 > 権限セット (または プロファイル) > `システム権限`を編集する

<img width="500" alt="スクリーンショット 2019-05-19 0.17.52.png" src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/326996/ac21b10d-62a6-a43f-92f6-78c628e04e00.png">

## Apex クラスアクセス有効化

設定 > ユーザの管理 > 権限セット (または プロファイル) > `Apex クラスアクセス`を編集する

<img width="500" alt="スクリーンショット 2019-05-19 0.18.41.png" src="https://qiita-image-store.s3.ap-northeast-1.amazonaws.com/0/326996/ea70227b-84cb-8daf-96ef-2cfb29284f89.png">

## HTTPメソッドGET テスト

```python_requests_sfdc.py
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
```

```python_result.json
[
    {
        "attributes": {
            "type": "User",
            "url": "/services/data/v45.0/sobjects/User/0056F000006StK9QAK"
        },
        "Id": "0056F000006StK9QAK",
        "Name": "nsuhara",
        "LastLoginDate": "2019-05-18T14: 11: 22.000+0000"
    }
]
```

# まとめ - 実装モデルの検討

環境や条件をもとに実装モデルを検討する

| No. |                    検討条件                    |            実装モデル            |              サンプル              |
|-----|------------------------------------------------|----------------------------------|------------------------------------|
|   1 | Salesforce向けのパッケージが用意されている場合 | パッケージを使用して実装する     | Pythonサンプル                     |
|   2 | SOQLの実行結果で事足りる場合                   | HTTPリクエストを使用して実装する | Shell Scriptサンプル, Javaサンプル |
|   3 | 上記以外の場合                                 | Apex REST Webサービスを実装する  | REST Webサービスの実装サンプル     |
