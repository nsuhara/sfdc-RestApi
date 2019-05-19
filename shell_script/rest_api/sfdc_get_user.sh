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
