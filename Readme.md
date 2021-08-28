https://docs.gradle.org/current/userguide/userguide.html

https://docs.spring.io/spring-boot/docs/current/gradle-plugin/reference/htmlsingle/#managing-dependencies

# 프로젝트 설정 파일 자동 생성  
https://start.spring.io/  

open jdk 16  

Gradle Version 7.0.2  
https://gradle.org/install/  
(setting에서 gradle 외부폴더 참조로 변경함)  


error: variable name not initialized in the default constructor  
[https://github.com/jojoldu/freelec-springboot2-webservice/issues/2](https://github.com/jojoldu/freelec-springboot2-webservice/issues/2)  


데이터 확인  
[http://localhost:8080/h2-console](http://localhost:8080/h2-console)  
JDBC URL값을 아래와 같이 변경  
jdbc:h2:mem:testdb  


로그인 연동을 위한 key 발급 경로  
[https://console.cloud.google.com/](https://console.cloud.google.com/)  
[https://developers.naver.com/apps/#/register?api=nvlogin](https://developers.naver.com/apps/#/register?api=nvlogin)  


클라우드 형태  
1.Infrastructure as a Service (IaaS,아이아스, 이에스)  
- 기존 물리 장비를 미들웨어와 함께 묶어둔 추상화 서비스
- 가상 머신, 스토리지, 네트워크, 운영체제 등의 it 인프라를 대여
- AWS의 EC2, S3 등
2.Platform as a Service (PaaS, 파스)
- IaaS에서 한 번 더 추상화, 많은 기능 자동화
- AWS의 Veanstalk(빈스톡), Heroku(헤로쿠) 등
3.Software as a Service (SaaS, 사스)
- 소프트웨어 서비스, 구글드라이브, 와탭, 드랍박스 등  


아마존 리눅스 서버 생성시 꼭 해야할 설정들  
- java8 설치
> sudo yum install -y java-1.8.0-openjdk-devel.x86_64  
> sudo /usr/sbin/alternatives --config java #인스턴트 버전 변경  

- 타임존 변경  
> sudo rm /etc/localtime  
> sudo ln -s /usr/share/zoneinfo/Asiz/Seoul /etc/localtime  
> date #변경된 시간 확인  

- hostname 변경  
> sudo vim /etc/sysconfig/network  
> HOSTNAME=원하는서비스명  
> 저장 후 빠져나옴  
> sudo reboot #서버 재부팅  
> sudo vim /etc/hosts  
> 127.0.0.1 등록한호스트명  
> 저장 후 빠져나옴
> curl 등록한호스트명 #변경 내용 확인  


배포 스크립트 만들기  
vim ~/app/step1/deploy.sh  
```
#! /bin/bash

REPOSITORY=/home/ec2-user/app/step1
PROJECT_NAME=freelec-springboot2-webservice

cd $REPOSITORY/$PROJECT_NAME/

echo "> Git Pull"
git pull

echo "> 프로젝트 Build 시작"
./gradlew build

echo "> step1 디렉토리로 이동"
cd $REPOSITORY

echo "> Build 파일 복사"
cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -f ${PROJECT_NAME}.*.jar)

echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID" 

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다" 
else
    echo "> kill -15 $CURRENT_PID" 
    kill -15 $CURRENT_PID
    sleep 5
fi


echo "> 어플리케이션 배포" 
JAR_NAME=$(ls -tr $REPOSITORY/ | grep jar | tail -n 1)

echo "> JAR Name: $JAR_NAME" 
nohup java -jar $REPOSITORY/$JAR_NAME 2>&1
```

배포 스크립트 실행 시 설정값 포함  
```
# 파일생성 및 내용 입력 후 저장
vim /home/ec2-user/app/application-oauth.properties

# 실행시 설정파일 추가  
nohup java -jar \ 
-Dspring.config.location=classpath:/application.properties,/home/ec2-user/app/application-oauth.properties \
$REPOSITORY/$$JAR_NAME 2>&1 &

vim ~/app/application-real-db.properties

# 아래 내용 입력 후 저장
spring.jpa.hibernate.ddl-auto=none # 테이블 자동 생성 옵션 off
spring.datasource.url=jdbc:mariadb://rds주소:포트명/database이름
spring.datasource.username=db계정
spring.datasource.password=비밀번호
spring.datasource.driver-class-name=org.mariadb.jdbc.Driver

nohup java -jar \ 
-Dspring.config.location=classpath:/application.properties,\
/home/ec2-user/app/application-oauth.properties,\
/home/ec2-user/app/application-real-db.properties,\
classpat:/application-real.properties \
-Dspring.profiles.active=real \
$REPOSITORY/$$JAR_NAME 2>&1 &

```


CI & CD (Continuous Integration Continuous Deployment)  
- Travis CI: 깃헙에서 제공하는 무료 CI 서비스 [https://travis-ci.org/](https://travis-ci.org/)  
[계정명-settings] 저장소 상태바 활성화  
프로젝트의 build.gradle 위치에 .travis.yml 파일 생성 후 아래 내용추가
```
language: java
jdk:
  - openjdk8

# 어느 브랜치가 푸시될때 수행할지
branches:
  only:
    - master

# Travis CI 서버의 Home
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

# master 브랜치에 푸시되었을때 수행
script: "./gradlew clean build"

# S3로 배포 파일 전달
before_deploy:
  - zip -r freelec-springboot2-webservice * #현재 위치의 모든 파일 압축
  - mkdir -p deploy #Travis CI가 실행중인 위치에서 생성
  - mv freelec-springboot2-webservice.zip deploy/freelec-springboot2-webservice.zip

# S3로 파일 업로드 혹은 CodeDeploy로 배포 등 외부 서비스와 연동될 행위들을 선언
deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY #Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY #Travis repo settings에 설정된 값
    bucket: freelec-springboot-build #S3 버킷
    region: ap-northest-2
    skip_cleanup: true
    acl: private #zip 파일 접근을 private로
    local_dir: deploy #before_deploy에서 생성한 디렉토리, 해당 위치의 파일들만 S3로 전송
    wait-until-deployed: true
  - provider: codedeploy
    access_key_id: $AWS_ACCESS_KEY #Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECREY_KEY #Travis repo settings에 설정된 값
    bucket: freelec-springboot-build #S3 버킷
    key: freelec-springboot2-webservice.zip #빌드 파일을 압축해서 전달
    bundle_type: zip
    application: freelec-springboot2-webservice #웹 콘솔에서 등록한 codeDeploy 어플리케이션
    deployment_group: freelec-springboot2-webservice-group  #웹 콘솔에서 등록한 codeDeploy 베포 그룹
    region: ap-northeast-2
    wait-until-deployed: true

# CI 실행 완료 시 메일로 알람
norifications:
  email:
    recipients:
      - 내 이메일 주소

```



Travis CI 배포 자동화  
[https://velog.io/@swchoi0329/Travis-CI-%EB%B0%B0%ED%8F%AC-%EC%9E%90%EB%8F%99%ED%99%94](https://velog.io/@swchoi0329/Travis-CI-%EB%B0%B0%ED%8F%AC-%EC%9E%90%EB%8F%99%ED%99%94)  


AWS 배포  
- Code Commit: 깃허브와 같은 코드 저장소
- Code Build: 빌드 서비스
- Code Deploy: 배포 서비스  


AWS CodeDeploy 설정  
appspec.yml 파일애 아래 내용 입력  
```
version: 0.0
os: linux
files:
  - source: /
    destination: /home/ec2-user/app/step2/zip/ #파일을 받을 위치
    overwrite: yes
```


-------

Travis, S3를 활용한 배포 실행 파일  
1. deploy.sh 수정  
```
#! /bin/bash

REPOSITORY=/home/ec2-user/app/step2
PROJECT_NAME=freelec-springboot2-webservice

echo "> Build 파일 복사"
cp $REPOSITORY/$PROJECT_NAME/build/libs/*.jar $REPOSITORY/

echo "> 현재 구동중인 애플리케이션 pid 확인"
CURRENT_PID=$(pgrep -fl freelec-springboot2-webservice | grep jar | awk '{print $1}')

echo "> 현재 구동중인 애플리케이션 pid: $CURRENT_PID" 

if [ -z "$CURRENT_PID" ]; then
    echo "> 현재 구동 중인 애플리케이션이 없으므로 종료하지 않습니다" 
else
    echo "> kill -15 $CURRENT_PID" 
    kill -15 $CURRENT_PID
    sleep 5
fi


echo "> 어플리케이션 배포" 
JAR_NAME=$(ls -tr $REPOSITORY/ | tail -n 1)

echo "> JAR Name: $JAR_NAME" 

echo "> $JAR_NAME에 실행권한 추가" 
chmod +x $JAR_NAME

echo "> $JAR_NAME에 실행" 
nohup java -jar \
-Dspring.config.location=classpath:/application.properties,\
classpath:/application-real.properties,/home/ec2-user/app/application-oaut.proterties,\
/home/ec2-user/app/application-real-db.properties -Dspring.profiles.active=real\
$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &

#nohup실행시 codedeploy 무한대기현상 해결을 위해 입출력 처리
```

2. .travis.yml 수정  
```
language: java
jdk:
  - openjdk8

# 어느 브랜치가 푸시될때 수행할지
branches:
  only:
    - master

# Travis CI 서버의 Home
cache:
  directories:
    - '$HOME/.m2/repository'
    - '$HOME/.gradle'

# master 브랜치에 푸시되었을때 수행
script: "./gradlew clean build"

# S3로 배포 파일 전달
before_deploy:
  - mkdir -p before-deploy #zip에 포함시킬 파일들을 담을 디렉토리 생성
  - cp scripts/*.sh before-deploy/
  - cp appspec.yml before-deploy/
  - cp build/libs/*.jar before-deploy/
  - cd before-deploy && zip -r before-deploy * #before-deploy 전체 압축
  - cd ../ && mkdir -p deploy
  - mv before-deploy/before-deploy.zip deploy/freelec-springboot2-webservice.zip #deploy로 zip파일 이동

# S3로 파일 업로드 혹은 CodeDeploy로 배포 등 외부 서비스와 연동될 행위들을 선언
deploy:
  - provider: s3
    access_key_id: $AWS_ACCESS_KEY #Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECRET_KEY #Travis repo settings에 설정된 값
    bucket: freelec-springboot-build #S3 버킷
    region: ap-northest-2
    skip_cleanup: true
    acl: private #zip 파일 접근을 private로
    local_dir: deploy #before_deploy에서 생성한 디렉토리, 해당 위치의 파일들만 S3로 전송
    wait-until-deployed: true
  - provider: codedeploy
    access_key_id: $AWS_ACCESS_KEY #Travis repo settings에 설정된 값
    secret_access_key: $AWS_SECREY_KEY #Travis repo settings에 설정된 값
    bucket: freelec-springboot-build #S3 버킷
    key: freelec-springboot2-webservice.zip #빌드 파일을 압축해서 전달
    bundle_type: zip
    application: freelec-springboot2-webservice #웹 콘솔에서 등록한 codeDeploy 어플리케이션
    deployment_group: freelec-springboot2-webservice-group  #웹 콘솔에서 등록한 codeDeploy 베포 그룹
    region: ap-northeast-2
    wait-until-deployed: true

# CI 실행 완료 시 메일로 알람
norifications:
  email:
    recipients:
      - 내 이메일 주소

```

3. appspec.yml 수정  
```
version: 0.0
os: linux
files:
  - source: /
    destination: /home/ec2-user/app/step2/zip/ #파일을 받을 위치
    overwrite: yes

permissions:
  - object: /
    pattern: "**"
    owner: ec2-user
    group: ec2-user

hooks:
  ApplicationStart:
    - location: deploy.sh
      timeout: 60
      runas: ec2-user
```

CodeDeploy에 대한 내용 위치  
/opt/codedeploy-agent/deploymenet-root  

CodeDeploy 로그 위치  
/opt/codedeploy-agent/deploymenet-root/deployment-logs/codedeploy-agent-deployments.log  



무중단 배포  
* 엔진엑스 설치  
> sudo yum install nginx  
> sudo service nginx start  

* 보안그룹 추가
EC2 - 보안그룹 - EC2 보안 그룹 선택 - 인바운드 편집  
8080이 아닌 80포트로 주소 변경  

* 설정 파일 변경
```
sudo vim /etc/nginx/nginx.conf

#아래 내용 추가
location / {
    proxy_pass http://localhost:8080
    proxy_set_header X-Real-IP $remote_addr;
    proxy_set_header X-Forwarded-For $proxy_add_forarded_for;
    proxy_set_header Host $Http_host;
}

#엔진엑스 재시작
sudo service nginx restart
```

* 엔진엑스 설정 수정  
```
sudo vim /etc/nginx/conf.d/service-url.inc

#아래 코드 입력
set $service_url http://127.0.0.1:8080;

#저장 종료

sudo vim /etc/nginx/nginx.conf

#location / 부분을 찾아 아래와 같이 변경
include /etc/nginx/conf.d/service-url.inc;

location / {
    proxy_pass $service_url;

#저장 종료

sudo service nginx restart
```

* 배포 스크립트 작성  
```
version: 0.0
os: linux
files:
  - source: /
    destination: /home/ec2-user/app/step3/zip/ #파일을 받을 위치
    overwrite: yes

permissions:
  - object: /
    pattern: "**"
    owner: ec2-user
    group: ec2-user

hooks:
  AfterInstall:
    - location: stop.sh #엔진엑스와 연결되어 있지 않은 스프링부트 종료
      timeout: 60
      runas: ec2-user
  ApplicationStart:
    - location: start.sh #엔진엑스와 연결되어있지 않은 port로 새 버전의 스프링부트 시작
      timeout: 60
      runas: ec2-user
  ApplicationStart:
    - location: health.sh #새 스프링부트가 정상적으로 실행되었는지 확인
      timeout: 60
      runas: ec2-user
```

* profile.sh 생성  
```
#!/usr/bin/env bash

# 쉬고있는 profile 찾기

funciton find_idle_profile()
{
    #현재 엔진엑스가 바라보는 스프링부트가 정상수행중인지 확인
    RESPONSE_CODE=$(curl -s -o /dev/null -w "%{http_code}" http://localhost/profile)

    if [ ${RESPONSE_CODE} -ge 400 ] #400보다 크면 
    then
        CURRENT_PROFILE=resl2
    else
        CURRENT_PROFILE=$(curl -s http://localhost/profile)
    fi

    if [ ${CURRENT_PROFILE} == real1 ]
    then
        IDLE_PROFILE=resl2
    else
        IDLE_PROFILE=resl1
    fi

    echo "${IDLE_PROFILE}"
}

# 쉬고 있는 프로파일 포트 찾기
function find_idle_port()
{
    IDLE_PROFILE=$(find_idle_profile)

    if [ ${IDLE_PROFILE} == real1 ]
    then
        echo "8081"
    else
        echo "8082"
    fi
}
```

* stop.sh 생성  
```
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

IDLE_PORT=$(find_idle_port)

echo "> $IDLE_PORT 에서 구동중인 애플리케이션 pid 확인"
IDLE_PID=$(lsof -ti tcp:${IDLE_PORT})

if [ -z ${IDLE_PID} ]
then
    echo "> 현재 구동중인 애플리케이션 없음"
else
    echo "kill $IDLE_PID"
    kill -15 ${IDLE_PID}
    sleep 5
fi
```

* start.sh 생성  
```
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

REPOSITORY=/home/ec2-user/app/step3
PROJECT_NAME=freelec-springboot2-webservice

echo "> Build 파일 복사"
echo "> cp $REPOSITORY/zip/*.jar $REPOSITORY/"
cp $REPOSITORY/zip/*.jar $REPOSITORY/

echo "> 새 애플리케이션 배포"
JAR_NAME=$(ls -tr $REPOSITORY/*.jar | tail -n 1)
echo "> JAR Name: $JAR_NAME"

echo "> $JAR_NAME 에 실행권한 추가"
chmod +x $JAR_NAME

echo "> $JAR_NAME 실행"
IDLE_PROFILE=$(find_idle_profile)

echo "> $JAR_NAME를 profile=$IDLE_PROFILE로 실행.. "
nohup java -jar \
-Dspring.config.location=classpath:/application.properties,\
classpath:/application-$IDLE_PROFILE.properties,\
/home/ec2-user/app/application-oauth.properties,/home/ec2-user/app/application-real-db.properties \
-Dspring.profiles.active=$IDLE_PROFILE \
$JAR_NAME > $REPOSITORY/nohup.out 2>&1 &
```

* health.sh 생성  
```
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh
source ${ABSDIR}/swich.sh

IDLE_PROT=$(find_idle_port)

echo "> Health Check Start!"
echo "> IDLE_PORT: $IDLE_PROT"
echo "> curl -s http://localost:$IDLE_PROT/profile"
sleep 10

for RETRY_COUNT in {1..10}
do
    RESPONSE=$(curl -s http://localhost:${IDLE_PROT}/profile)
    UP_COUNT=$(echo $RESPONSE | grep 'real' | wc -l)

    # "real" 문자열이 있는지 검증
    if [ ${UP_COUNT} -ge 1 ]
    then
        echo "> Health check 성공"
        switch_proxy
        break
    else
        echo "> Health check 응답을 알 수 없어나, 실행상태가 아닙니다."
        echo "> Health check: $RESPONSE"
    fi

    if [ ${RETRY_COUNT} -eq 10 ]
    then
        echo "> Health check 실패."
        echo "> 엔진엑스에 연결하지 않고 배포 종료"
    fi

    echo "> Health check 연결 실패, 재시도..."
    sleep 10
done

```

* switch.sh 생성  
```
#!/usr/bin/env bash

ABSPATH=$(readlink -f $0)
ABSDIR=$(dirname $ABSPATH)
source ${ABSDIR}/profile.sh

function switch_proxy() {
    IDLE_PORT=$(find_idle_port)

    echo "> 전환할 port: $IDLE_PORT"
    echo "> port 전환"
    echo "set \$service_url http://127.0.0.1:${IDLE_PORT};" | sudo tee /etc/nginx/conf.d/service-url.inc

    echo "> nginx Reload"
    sudo service nginx reload
}
```

깃헙으로 소스 푸시하면 자동 배포 실행  
로그확인  
tail -f /opt/codedeploy-agent/deployment-root/deployment-logs/codedeploy-agent-deployments.log  


** 기타 도구
- 댓글 서비스
Disqus [https://disqus.com/](https://disqus.com/)  
LiveRe [http://livere.com/](http://livere.com/)  
Utterances [https://utteranc.es/](https://utteranc.es/)  

- SNS 연동  
Zapier [https://zapier.com/](https://zapier.com/)  
IFTTT [https://ifttt.com/](https://ifttt.com/)  

- 방문자 분석  
구글 애널리틱스 [https://analytics.google.com/analytics/web/provision/?hl=ko&pli=1#/provision](https://analytics.google.com/analytics/web/provision/?hl=ko&pli=1#/provision)  

- CDN(Content Delivery Network) 분산된 서버 네트워크   
클라우드플레어 [https://www.cloudflare.com/ko-kr/](https://www.cloudflare.com/ko-kr/)  

- 이메일 마케팅  
Mailchimp [https://mailchimp.com/](https://mailchimp.com/)  
